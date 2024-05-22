package com.ping.app.ui.ui.util

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.math.absoluteValue


class MovableFloatingActionButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FloatingActionButton(context, attrs, defStyleAttr), View.OnTouchListener {
    
    companion object {
        private const val CLICK_DRAG_TOLERANCE =
            10f // 흔히 사용자들이 FAB를 탭할 때 약간의 의도하지 않은 드래그가 발생하므로 이를 고려해야 합니다.
    }
    
    private var downRawX = 0f
    private var downRawY = 0f
    private var dX = 0f
    private var dY = 0f
    
    init {
        setOnTouchListener(this)
    }
    
    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
        
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                downRawX = motionEvent.rawX
                downRawY = motionEvent.rawY
                dX = view.x - downRawX
                dY = view.y - downRawY
                
                return true // Consumed
            }
            
            MotionEvent.ACTION_MOVE -> {
                val viewWidth = view.width
                val viewHeight = view.height
                
                val viewParent = view.parent as View
                val parentWidth = viewParent.width
                val parentHeight = viewParent.height
                
                var newX = motionEvent.rawX + dX
                newX = newX.coerceIn(
                    layoutParams.leftMargin.toFloat(),
                    (parentWidth - viewWidth - layoutParams.rightMargin).toFloat()
                )
                
                var newY = motionEvent.rawY + dY
                newY = newY.coerceIn(
                    layoutParams.topMargin.toFloat(),
                    (parentHeight - viewHeight - layoutParams.bottomMargin).toFloat()
                )
                
                view.animate()
                    .x(newX)
                    .y(newY)
                    .setDuration(0)
                    .start()
                
                return true // Consumed
            }
            
            MotionEvent.ACTION_UP -> {
                val upRawX = motionEvent.rawX
                val upRawY = motionEvent.rawY
                
                val upDX = upRawX - downRawX
                val upDY = upRawY - downRawY
                
                return if (upDX.absoluteValue < CLICK_DRAG_TOLERANCE && upDY.absoluteValue < CLICK_DRAG_TOLERANCE) {
                    // A click
                    performClick()
                } else {
                    // A drag
                    true // Consumed
                }
            }
            
            else -> return super.onTouchEvent(motionEvent)
        }
    }
}