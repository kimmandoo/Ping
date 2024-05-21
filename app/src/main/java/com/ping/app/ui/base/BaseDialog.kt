package com.ping.app.ui.base

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.viewbinding.ViewBinding

abstract class BaseDialog<VB : ViewBinding>(private val context: Context) {
    
    protected val binding: VB by lazy { inflateBinding(LayoutInflater.from(context)) }
    lateinit var alertDialog: AlertDialog
    
    init {
        initDialog()
    }
    
    abstract fun inflateBinding(inflater: LayoutInflater): VB
    
    private fun initDialog() {
        val dialogBuilder = AlertDialog.Builder(context)
            .setView(binding.root)
            .setCancelable(false)
        alertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
    
    open fun showDialog() {
        alertDialog.show()
    }
    
    fun dismissDialog() {
        alertDialog.dismiss()
    }
    
    fun cancelDialog() {
        alertDialog.cancel()
    }
}