package com.ping.app.presentation.ui.feature.map

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.activityViewModels
import com.naver.maps.geometry.LatLng
import com.ping.app.R
import com.ping.app.databinding.FragmentPingAddPostBinding
import com.ping.app.presentation.base.BaseBottomSheetDialogFragment
import com.ping.app.presentation.util.Map.USER_POSITION_LAT
import com.ping.app.presentation.util.Map.USER_POSITION_LNG
import com.ping.app.presentation.util.getAddress

private const val TAG = "PingAddPostFragment_싸피"

class PingAddPostFragment :
    BaseBottomSheetDialogFragment<FragmentPingAddPostBinding, PingMapViewModel>(
        R.layout.fragment_ping_add_post
    ) {
    override val viewModel: PingMapViewModel by activityViewModels()
    
    override fun initView(savedInstanceState: Bundle?) {
        val pingPosition = LatLng(
            requireArguments().getDouble(USER_POSITION_LAT),
            requireArguments().getDouble(USER_POSITION_LNG)
        )
        binding.apply {
            addPostTvAddress.text = getAddress(pingPosition.latitude, pingPosition.longitude)
        }
        
        Log.d(TAG, "initView: $pingPosition")
    }
}