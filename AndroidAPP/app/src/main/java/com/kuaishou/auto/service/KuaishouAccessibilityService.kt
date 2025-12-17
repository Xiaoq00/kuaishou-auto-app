package com.kuaishou.auto.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Intent
import android.graphics.Path
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.kuaishou.auto.util.ConfigManager

class KuaishouAccessibilityService : AccessibilityService() {
    
    companion object {
        private const val TAG = "KuaishouAccessibility"
        const val KUAI_SHOU_PACKAGE = "com.kuaishou.nebula"
        
        // 脚本状态
        var isScriptRunning = false
        var isScriptPaused = false
        var scriptStartTime = 0L
        var remainingTime = 0L
        
        // 回调接口
        var statusCallback: ((String) -> Unit)? = null
        var timeCallback: ((String) -> Unit)? = null
    }
    
    private val handler = Handler(Looper.getMainLooper())
    private var swipeRunnable: Runnable? = null
    
    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "无障碍服务已连接")
        updateStatus("服务已就绪")
    }
    
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event?.let {
            when (it.eventType) {
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                    handleWindowStateChanged(it)
                }
                AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                    handleWindowContentChanged(it)
                }
            }
        }
    }
    
    override fun onInterrupt() {
        Log.d(TAG, "无障碍服务被中断")
        stopScript()
        updateStatus("服务被中断")
    }
    
    private fun handleWindowStateChanged(event: AccessibilityEvent) {
        val packageName = event.packageName?.toString()
        val className = event.className?.toString()
        
        Log.d(TAG, "窗口状态变化: $packageName - $className")
        
        if (packageName == KUAI_SHOU_PACKAGE && isScriptRunning && !isScriptPaused) {
            // 检测到快手界面，开始自动刷视频
            startAutoSwipe()
        }
    }
    
    private fun handleWindowContentChanged(event: AccessibilityEvent) {
        // 可以在这里检测界面内容变化，进行更精确的控制
        if (isScriptRunning && !isScriptPaused) {
            // 检查是否需要执行滑动操作
        }
    }
    
    fun startScript() {
        if (isScriptRunning) return
        
        isScriptRunning = true
        isScriptPaused = false
        scriptStartTime = System.currentTimeMillis()
        remainingTime = ConfigManager.getRunDuration() * 60 * 1000L
        
        updateStatus("正在启动脚本...")
        updateTimeDisplay()
        
        // 打开快手极速版
        openKuaishouApp()
        
        // 启动时间更新
        handler.postDelayed(timeUpdateRunnable, 1000)
    }
    
    fun pauseScript() {
        if (!isScriptRunning) return
        
        isScriptPaused = !isScriptPaused
        
        if (isScriptPaused) {
            updateStatus("脚本已暂停")
            swipeRunnable?.let { handler.removeCallbacks(it) }
        } else {
            updateStatus("脚本运行中")
            startAutoSwipe()
        }
    }
    
    fun stopScript() {
        isScriptRunning = false
        isScriptPaused = false
        
        swipeRunnable?.let { handler.removeCallbacks(it) }
        handler.removeCallbacks(timeUpdateRunnable)
        
        updateStatus("脚本已停止")
        updateTimeDisplay("剩余时间: 30:00")
    }
    
    private fun openKuaishouApp() {
        try {
            val intent = packageManager.getLaunchIntentForPackage(KUAI_SHOU_PACKAGE)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                updateStatus("正在打开快手极速版...")
            } else {
                updateStatus("未找到快手极速版")
                stopScript()
            }
        } catch (e: Exception) {
            Log.e(TAG, "打开快手失败", e)
            updateStatus("打开快手失败")
            stopScript()
        }
    }
    
    private fun startAutoSwipe() {
        if (!isScriptRunning || isScriptPaused) return
        
        // 等待一段时间后开始滑动
        handler.postDelayed({
            performSwipe()
        }, ConfigManager.getInitialWaitTime() * 1000L)
    }
    
    private fun performSwipe() {
        if (!isScriptRunning || isScriptPaused) return
        
        try {
            // 创建滑动路径
            val path = Path()
            val startX = ConfigManager.getSwipeStartX()
            val startY = ConfigManager.getSwipeStartY()
            val endX = ConfigManager.getSwipeEndX()
            val endY = ConfigManager.getSwipeEndY()
            
            path.moveTo(startX, startY)
            path.lineTo(endX, endY)
            
            // 创建手势描述
            val gestureDescription = GestureDescription.Builder()
                .addStroke(GestureDescription.StrokeDescription(
                    path, 
                    0, 
                    ConfigManager.getSwipeDuration().toLong(),
                    true
                ))
                .build()
            
            // 执行滑动
            dispatchGesture(gestureDescription, object : GestureResultCallback() {
                override fun onCompleted(gestureDescription: GestureDescription?) {
                    super.onCompleted(gestureDescription)
                    Log.d(TAG, "滑动完成")
                    scheduleNextSwipe()
                }
                
                override fun onCancelled(gestureDescription: GestureDescription?) {
                    super.onCancelled(gestureDescription)
                    Log.d(TAG, "滑动取消")
                    scheduleNextSwipe()
                }
            }, null)
            
        } catch (e: Exception) {
            Log.e(TAG, "滑动失败", e)
            scheduleNextSwipe()
        }
    }
    
    private fun scheduleNextSwipe() {
        if (!isScriptRunning || isScriptPaused) return
        
        val interval = ConfigManager.getSwipeInterval() * 1000L
        
        swipeRunnable = Runnable {
            if (isScriptRunning && !isScriptPaused) {
                performSwipe()
            }
        }
        
        swipeRunnable?.let {
            handler.postDelayed(it, interval)
        }
    }
    
    private val timeUpdateRunnable = object : Runnable {
        override fun run() {
            if (isScriptRunning && !isScriptPaused) {
                val elapsed = System.currentTimeMillis() - scriptStartTime
                remainingTime = maxOf(0, ConfigManager.getRunDuration() * 60 * 1000L - elapsed)
                
                if (remainingTime <= 0) {
                    stopScript()
                    return
                }
                
                updateTimeDisplay()
                handler.postDelayed(this, 1000)
            }
        }
    }
    
    private fun updateStatus(status: String) {
        Log.d(TAG, "状态更新: $status")
        statusCallback?.invoke(status)
    }
    
    private fun updateTimeDisplay(displayText: String? = null) {
        val text = displayText ?: {
            val minutes = (remainingTime / 60000).toInt()
            val seconds = ((remainingTime % 60000) / 1000).toInt()
            "剩余时间: ${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
        }()
        
        timeCallback?.invoke(text)
    }
}