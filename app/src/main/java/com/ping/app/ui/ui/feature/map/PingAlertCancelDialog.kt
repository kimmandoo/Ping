package com.ping.app.ui.ui.feature.map

import android.content.Context
import android.view.LayoutInflater
import com.ping.app.databinding.DialogPingCancleBinding
import com.ping.app.ui.base.BaseDialog

class PingAlertCancelDialog(context: Context, private val message: String): BaseDialog<DialogPingCancleBinding>(context) {
    override fun inflateBinding(inflater: LayoutInflater): DialogPingCancleBinding {
        return DialogPingCancleBinding.inflate(inflater)
    }

    override fun showDialog() {
        super.showDialog()
        binding.apply {
            binding.tvPostAlert.text = message
            btnCancel.setOnClickListener {
                alertDialog.dismiss()
            }
            btnConfirm.setOnClickListener {
                alertDialog.cancel()
            }
        }
    }
}