package com.ping.app.ui.ui.feature.main

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.ping.app.data.model.Gathering
import com.ping.app.databinding.DialogMainBinding
import com.ping.app.ui.base.BaseDialog

private const val TAG = "MainAlertDialog_싸피"
class MainAlertDialog(private val context: Context, private val gathering: Gathering): BaseDialog<DialogMainBinding>(context) {
    override fun inflateBinding(inflater: LayoutInflater): DialogMainBinding {
        return DialogMainBinding.inflate(inflater)
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
                if(addPostCode.text.toString() == gathering.enterCode){
                    Log.d(TAG, "showDialog: ")
                    alertDialog.cancel()
                }else{
                    Toast.makeText(context, "입력 코드가 틀렸습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}