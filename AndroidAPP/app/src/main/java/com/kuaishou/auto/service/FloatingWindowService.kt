package com.kuaishou.auto.service

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.kuaishou.auto.R

class FloatingWindowService : Service() {
    
    private var windowManager: WindowManager? = null
    private var floatingView: View? = null
    
    companion object {
        private const val TAG = "FloatingWindowService"
    }
    
    override fun onCreate() {
        super.onCreate()
        createFloatingWindow()
        setupFloatingWindowCallbacks()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }
    
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    
    override fun onDestroy() {
        super.onDestroy()
        removeFloatingWindow()
    }
    
    private fun createFloatingWindow() {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        floatingView = inflater.inflate(R.layout.layout_floating_window, null)
        
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.END
            x = 0
            y = 100
        }
        
        windowManager?.addView(floatingView, params)
        
        // 设置悬浮窗可拖动（简化版）
        setupFloatingWindowDrag()
    }
    
    private fun setupFloatingWindowDrag() {
        floatingView?.setOnTouchListener { view, event ->
            // 简化的拖动逻辑
            // 实际项目中需要实现完整的拖动逻辑
            false
        }
    }
    
    private fun setupFloatingWindowCallbacks() {
        // 设置状态回调
        KuaishouAccessibilityService.statusCallback = { status ->
            updateFloatingWindowStatus(status)
        }
        
        // 设置时间回调
        KuaishouAccessibilityService.timeCallback = { timeText ->
            updateFloatingWindowTime(timeText)
        }
    }
    
    private fun updateFloatingWindowStatus(status: String) {
        floatingView?.findViewById<TextView>(R.id.tv_floating_status)?.text = status
    }
    
    private fun updateFloatingWindowTime(timeText: String) {
        floatingView?.findViewById<TextView>(R.id.tv_floating_time)?.text = timeText
    }
    
    private fun removeFloatingWindow() {
        if (floatingView != null) {
            windowManager?.removeView(floatingView)
            floatingView = null
        }
    }
}