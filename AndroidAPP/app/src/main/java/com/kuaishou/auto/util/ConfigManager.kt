package com.kuaishou.auto.util

import android.content.Context
import android.content.SharedPreferences
import kotlin.random.Random

object ConfigManager {
    
    private const val PREFS_NAME = "kuaishou_auto_config"
    private const val KEY_SWIPE_INTERVAL_MIN = "swipe_interval_min"
    private const val KEY_SWIPE_INTERVAL_MAX = "swipe_interval_max"
    private const val KEY_RUN_DURATION = "run_duration"
    private const val KEY_INITIAL_WAIT_TIME = "initial_wait_time"
    private const val KEY_SWIPE_DURATION_MIN = "swipe_duration_min"
    private const val KEY_SWIPE_DURATION_MAX = "swipe_duration_max"
    
    // 默认配置
    private const val DEFAULT_SWIPE_INTERVAL_MIN = 8
    private const val DEFAULT_SWIPE_INTERVAL_MAX = 22
    private const val DEFAULT_RUN_DURATION = 30  // 分钟
    private const val DEFAULT_INITIAL_WAIT_TIME = 10  // 秒
    private const val DEFAULT_SWIPE_DURATION_MIN = 500  // 毫秒
    private const val DEFAULT_SWIPE_DURATION_MAX = 1000  // 毫秒
    
    // 屏幕配置（可根据手机分辨率调整）
    private const val SWIPE_START_X_MIN = 300
    private const val SWIPE_START_X_MAX = 600
    private const val SWIPE_START_Y_MIN = 800
    private const val SWIPE_START_Y_MAX = 1000
    private const val SWIPE_END_Y_MIN = 100
    private const val SWIPE_END_Y_MAX = 300
    
    private lateinit var sharedPreferences: SharedPreferences
    
    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    // 获取滑动间隔（秒）
    fun getSwipeInterval(): Int {
        val min = sharedPreferences.getInt(KEY_SWIPE_INTERVAL_MIN, DEFAULT_SWIPE_INTERVAL_MIN)
        val max = sharedPreferences.getInt(KEY_SWIPE_INTERVAL_MAX, DEFAULT_SWIPE_INTERVAL_MAX)
        return Random.nextInt(min, max + 1)
    }
    
    // 获取运行时长（分钟）
    fun getRunDuration(): Int {
        return sharedPreferences.getInt(KEY_RUN_DURATION, DEFAULT_RUN_DURATION)
    }
    
    // 获取初始等待时间（秒）
    fun getInitialWaitTime(): Int {
        return sharedPreferences.getInt(KEY_INITIAL_WAIT_TIME, DEFAULT_INITIAL_WAIT_TIME)
    }
    
    // 获取滑动持续时间（毫秒）
    fun getSwipeDuration(): Int {
        val min = sharedPreferences.getInt(KEY_SWIPE_DURATION_MIN, DEFAULT_SWIPE_DURATION_MIN)
        val max = sharedPreferences.getInt(KEY_SWIPE_DURATION_MAX, DEFAULT_SWIPE_DURATION_MAX)
        return Random.nextInt(min, max + 1)
    }
    
    // 获取滑动起始点X坐标
    fun getSwipeStartX(): Float {
        return Random.nextInt(SWIPE_START_X_MIN, SWIPE_START_X_MAX + 1).toFloat()
    }
    
    // 获取滑动起始点Y坐标
    fun getSwipeStartY(): Float {
        return Random.nextInt(SWIPE_START_Y_MIN, SWIPE_START_Y_MAX + 1).toFloat()
    }
    
    // 获取滑动结束点X坐标（增加随机偏移）
    fun getSwipeEndX(startX: Float): Float {
        return startX + Random.nextInt(-50, 51)
    }
    
    // 获取滑动结束点Y坐标
    fun getSwipeEndY(): Float {
        return Random.nextInt(SWIPE_END_Y_MIN, SWIPE_END_Y_MAX + 1).toFloat()
    }
    
    // 保存配置
    fun saveConfig(
        swipeIntervalMin: Int,
        swipeIntervalMax: Int,
        runDuration: Int,
        initialWaitTime: Int,
        swipeDurationMin: Int,
        swipeDurationMax: Int
    ) {
        sharedPreferences.edit().apply {
            putInt(KEY_SWIPE_INTERVAL_MIN, swipeIntervalMin)
            putInt(KEY_SWIPE_INTERVAL_MAX, swipeIntervalMax)
            putInt(KEY_RUN_DURATION, runDuration)
            putInt(KEY_INITIAL_WAIT_TIME, initialWaitTime)
            putInt(KEY_SWIPE_DURATION_MIN, swipeDurationMin)
            putInt(KEY_SWIPE_DURATION_MAX, swipeDurationMax)
            apply()
        }
    }
    
    // 获取当前配置
    fun getCurrentConfig(): Map<String, Int> {
        return mapOf(
            KEY_SWIPE_INTERVAL_MIN to sharedPreferences.getInt(KEY_SWIPE_INTERVAL_MIN, DEFAULT_SWIPE_INTERVAL_MIN),
            KEY_SWIPE_INTERVAL_MAX to sharedPreferences.getInt(KEY_SWIPE_INTERVAL_MAX, DEFAULT_SWIPE_INTERVAL_MAX),
            KEY_RUN_DURATION to sharedPreferences.getInt(KEY_RUN_DURATION, DEFAULT_RUN_DURATION),
            KEY_INITIAL_WAIT_TIME to sharedPreferences.getInt(KEY_INITIAL_WAIT_TIME, DEFAULT_INITIAL_WAIT_TIME),
            KEY_SWIPE_DURATION_MIN to sharedPreferences.getInt(KEY_SWIPE_DURATION_MIN, DEFAULT_SWIPE_DURATION_MIN),
            KEY_SWIPE_DURATION_MAX to sharedPreferences.getInt(KEY_SWIPE_DURATION_MAX, DEFAULT_SWIPE_DURATION_MAX)
        )
    }
}