package com.kuaishou.auto

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityManager
import androidx.appcompat.app.AppCompatActivity
import com.kuaishou.auto.databinding.ActivityMainBinding
import com.kuaishou.auto.service.KuaishouAccessibilityService
import com.kuaishou.auto.util.ConfigManager

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var accessibilityManager: AccessibilityManager
    
    companion object {
        private const val TAG = "MainActivity"
        private const val REQUEST_ACCESSIBILITY = 1
        private const val REQUEST_OVERLAY_PERMISSION = 2
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // 初始化配置管理器
        ConfigManager.initialize(this)
        
        // 初始化无障碍管理器
        accessibilityManager = getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        
        setupUI()
        checkAccessibilityPermission()
        setupAccessibilityCallbacks()
    }
    
    override fun onResume() {
        super.onResume()
        checkAccessibilityPermission()
        updateButtonStates()
    }
    
    private fun setupUI() {
        // 开始按钮点击事件
        binding.btnStart.setOnClickListener {
            if (isAccessibilityServiceEnabled()) {
                KuaishouAccessibilityService.startScript()
                updateButtonStates()
            } else {
                requestAccessibilityPermission()
            }
        }
        
        // 暂停按钮点击事件
        binding.btnPause.setOnClickListener {
            KuaishouAccessibilityService.pauseScript()
            updateButtonStates()
        }
        
        // 停止按钮点击事件
        binding.btnStop.setOnClickListener {
            KuaishouAccessibilityService.stopScript()
            updateButtonStates()
        }
        
        // 开启无障碍权限按钮
        binding.btnEnableAccessibility.setOnClickListener {
            requestAccessibilityPermission()
        }
    }
    
    private fun setupAccessibilityCallbacks() {
        // 设置状态回调
        KuaishouAccessibilityService.statusCallback = { status ->
            runOnUiThread {
                binding.tvStatus.text = status
            }
        }
        
        // 设置时间回调
        KuaishouAccessibilityService.timeCallback = { timeText ->
            runOnUiThread {
                binding.tvTime.text = timeText
            }
        }
    }
    
    private fun checkAccessibilityPermission() {
        val isEnabled = isAccessibilityServiceEnabled()
        
        binding.tvPermissionHint.visibility = if (isEnabled) {
            android.view.View.GONE
        } else {
            android.view.View.VISIBLE
        }
        
        binding.btnEnableAccessibility.visibility = if (isEnabled) {
            android.view.View.GONE
        } else {
            android.view.View.VISIBLE
        }
        
        updateButtonStates()
    }
    
    private fun isAccessibilityServiceEnabled(): Boolean {
        val enabledServices = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        
        return enabledServices?.contains(packageName) == true
    }
    
    private fun requestAccessibilityPermission() {
        try {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivityForResult(intent, REQUEST_ACCESSIBILITY)
        } catch (e: Exception) {
            Log.e(TAG, "打开无障碍设置失败", e)
            // 备用方案：直接打开设置页面
            val intent = Intent(Settings.ACTION_SETTINGS)
            startActivity(intent)
        }
    }
    
    private fun requestOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivityForResult(intent, REQUEST_OVERLAY_PERMISSION)
            }
        }
    }
    
    private fun updateButtonStates() {
        val isEnabled = isAccessibilityServiceEnabled()
        val isRunning = KuaishouAccessibilityService.isScriptRunning
        val isPaused = KuaishouAccessibilityService.isScriptPaused
        
        // 开始按钮
        binding.btnStart.isEnabled = isEnabled && !isRunning
        
        // 暂停按钮
        binding.btnPause.isEnabled = isEnabled && isRunning
        binding.btnPause.text = if (isPaused) "继续" else "暂停"
        
        // 停止按钮
        binding.btnStop.isEnabled = isEnabled && isRunning
        
        // 更新状态显示
        if (!isEnabled) {
            binding.tvStatus.text = "请开启无障碍权限"
        } else if (!isRunning) {
            binding.tvStatus.text = "准备就绪"
        }
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        when (requestCode) {
            REQUEST_ACCESSIBILITY -> {
                // 从无障碍设置返回，重新检查权限
                checkAccessibilityPermission()
            }
            REQUEST_OVERLAY_PERMISSION -> {
                // 悬浮窗权限处理
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Settings.canDrawOverlays(this)) {
                        // 权限已授予，可以显示悬浮窗
                    }
                }
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // 清理回调
        KuaishouAccessibilityService.statusCallback = null
        KuaishouAccessibilityService.timeCallback = null
    }
}