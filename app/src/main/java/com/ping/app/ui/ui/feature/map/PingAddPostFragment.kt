package com.ping.app.ui.ui.feature.map

import android.app.AlertDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.activityViewModels
import com.naver.maps.geometry.LatLng
import com.ping.app.PingApplication
import com.ping.app.R
import com.ping.app.data.model.Gathering
import com.ping.app.databinding.DialogPingAddBinding
import com.ping.app.databinding.FragmentPingAddPostBinding
import com.ping.app.ui.base.BaseBottomSheetDialogFragment
import com.ping.app.ui.presentation.MainActivityViewModel
import com.ping.app.ui.presentation.map.PingMapViewModel
import com.ping.app.ui.ui.util.Map.USER_POSITION_LAT
import com.ping.app.ui.ui.util.Map.USER_POSITION_LNG
import com.ping.app.ui.ui.util.easyToast
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID
import java.util.concurrent.TimeUnit

private const val TAG = "PingAddPostFragment_싸피"

class PingAddPostFragment :
    BaseBottomSheetDialogFragment<FragmentPingAddPostBinding, PingMapViewModel>(
        R.layout.fragment_ping_add_post
    ) {
    override val viewModel: PingMapViewModel by activityViewModels()

    /** mainActivityViewModel 분리 해야함
     */
    private val mainActivityViewModel : MainActivityViewModel by activityViewModels()
    private val pingMapInstance = PingApplication.pingMapRepo
    private lateinit var gatheringTime: String
    override fun initView(savedInstanceState: Bundle?) {
        val pingPosition = LatLng(
            requireArguments().getDouble(USER_POSITION_LAT),
            requireArguments().getDouble(USER_POSITION_LNG)
        )
        binding.apply {
            addPostTvAddress.text =
                pingMapInstance.requestAddress(pingPosition.latitude, pingPosition.longitude)
            addPostIvDialog.setOnClickListener {
                addDateDialog()
            }
            addPostBtnSend.setOnClickListener {

                Log.d(TAG, "initViUUUUUUUUUew: ${mainActivityViewModel.userUid.value.toString()}")
                val title = addPostEtWhere.text.toString()
                val content = addPostEtWhat.text.toString()
                if (::gatheringTime.isInitialized && title.isNotEmpty() && content.isNotEmpty()) {
                    pingMapInstance.sendPingInfo(
                        Gathering(
                            uid = mainActivityViewModel.userUid.value.toString(),
                            uuid = UUID.randomUUID().toString(),
                            gatheringTime = gatheringTime,
                            title = title,
                            content = content,
                            longitude = pingPosition.longitude,
                            latitude = pingPosition.latitude
                        )
                    )
                }
            }
        }
    }
    
    /**
     *  calendar, timepicker에서 값을 받아와 확인버튼을 누르면 parent view에 값을 넣어준다.
     */
    private fun addDateDialog() {
        // dialog 띄운다.
        val calendar = Calendar.getInstance()
        val dialogBinding = DialogPingAddBinding.inflate(layoutInflater)
        dialogBinding.apply {
            addPostDp.minDate = calendar.timeInMillis
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            calendar.set(year, month, calendar.get(Calendar.DAY_OF_MONTH) + 7)
            addPostDp.maxDate = calendar.timeInMillis
            var targetDay = 0L
            addPostDp.setOnDateChangeListener { view, yy, mm, dd ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(yy, mm, dd)
                val currentCalendar = Calendar.getInstance()
                val differenceInMillis =
                    selectedCalendar.timeInMillis - currentCalendar.timeInMillis
                
                targetDay = TimeUnit.MILLISECONDS.toDays(differenceInMillis)
            }
            addPostTp.apply {
                setOnTimeChangedListener { view, hourOfDay, minute ->
                    if ((targetDay * 24 + hourOfDay) * 60 + minute < calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(
                            Calendar.MINUTE
                        )
                    ) {
                        context.easyToast("선택할 수 없는 시간입니다.")
                        this.hour = calendar.get(Calendar.HOUR_OF_DAY)
                        this.minute = calendar.get(Calendar.MINUTE)
                    }
                }
            }
        }
        
        val dialog =
            AlertDialog.Builder(binding.root.context).setView(dialogBinding.root)
                .setNegativeButton("취소") { dialog, which ->
                    dialog.dismiss()
                }.setPositiveButton("확인") { dialog, which ->
                    val format = "M월 d일"
                    val h = dialogBinding.addPostTp.hour
                    val hour = if (h > 12) {
                        "오후 ${h - 12}"
                    } else {
                        "오전 $h"
                    }
                    val formattedDateString = "${
                        SimpleDateFormat(format, Locale.KOREA).format(calendar.time)
                    } ${hour}시 ${dialogBinding.addPostTp.minute}분"
                    gatheringTime =
                        (calendar.time.time + dialogBinding.addPostTp.hour * 60 * 60 + dialogBinding.addPostTp.minute * 60).toString()
                    binding.addPostTv.text = formattedDateString
                }.create()
        dialog.show()
    }
}