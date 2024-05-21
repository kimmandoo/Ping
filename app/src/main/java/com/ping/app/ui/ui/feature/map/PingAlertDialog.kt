package com.ping.app.ui.ui.feature.map

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.ping.app.databinding.DialogPingBinding
import com.ping.app.ui.base.BaseDialog

class PingAlertDialog(private val context: Context): BaseDialog<DialogPingBinding>(context) {
    override fun inflateBinding(inflater: LayoutInflater): DialogPingBinding {
        return DialogPingBinding.inflate(inflater)
    }
    
    override fun showDialog() {
        super.showDialog()
        binding.apply {
            btnCancel.setOnClickListener {
                Toast.makeText(context, "취소했습니다", Toast.LENGTH_SHORT)
                    .show()
                dismissDialog()
            }
            btnConfirm.setOnClickListener {
                cancelDialog()
            }
        }
    }
}