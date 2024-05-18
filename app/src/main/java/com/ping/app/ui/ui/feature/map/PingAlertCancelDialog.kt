package com.ping.app.ui.ui.feature.map

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.ping.app.databinding.DialogPingCancleBinding

class PingAlertCancelDialog(private val context: Context) {
    private val binding = DialogPingCancleBinding.inflate(LayoutInflater.from(context), null, false)
    lateinit var alertDialog: AlertDialog

    init {
        initDialog()
    }

    fun showDialog() {
        alertDialog.show()
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

    private fun initDialog() {
        val dialogBuilder = AlertDialog.Builder(context)
            .setView(binding.root)
            .setCancelable(false)
        alertDialog = dialogBuilder.create()
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}