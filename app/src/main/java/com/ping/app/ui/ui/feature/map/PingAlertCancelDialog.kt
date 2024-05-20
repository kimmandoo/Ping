package com.ping.app.ui.ui.feature.map

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.ping.app.databinding.DialogPingCancleBinding
import com.ping.app.ui.base.BaseDialog

class PingAlertCancelDialog(private val context: Context): BaseDialog<DialogPingCancleBinding>(context) {
    override fun inflateBinding(inflater: LayoutInflater): DialogPingCancleBinding {
        return DialogPingCancleBinding.inflate(inflater)
    }

    override fun showDialog() {
        super.showDialog()
        binding.apply {
            btnCancel.setOnClickListener {
                Toast.makeText(context, "취소했습니다", Toast.LENGTH_SHORT)
                    .show()
                alertDialog.dismiss()
            }
            btnConfirm.setOnClickListener {
                alertDialog.cancel()
            }
        }
    }
}