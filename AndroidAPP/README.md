# 快手极速版自动刷视频APP

基于Android无障碍服务的自动刷视频应用，支持Android 8.0+设备。

## 功能特性

- ✅ **自动刷视频** - 模拟真实用户滑动操作
- ✅ **智能控制** - 开始/暂停/停止功能
- ✅ **随机间隔** - 8-22秒随机滑动间隔
- ✅ **时间限制** - 30分钟自动停止
- ✅ **悬浮窗显示** - 实时显示运行状态
- ✅ **反检测措施** - 自然滑动轨迹，降低被检测风险

## 使用说明

### 1. 安装应用
将APK文件安装到Android手机

### 2. 开启权限
- **无障碍权限**：设置 → 无障碍 → 快手自动刷视频 → 开启
- **悬浮窗权限**：设置 → 应用 → 快手自动刷视频 → 显示在其他应用上层 → 允许

### 3. 启动脚本
1. 打开"快手自动刷视频"应用
2. 点击"开始脚本"按钮
3. 应用会自动打开快手极速版并开始刷视频

### 4. 控制脚本
- **暂停**：临时停止脚本
- **继续**：恢复脚本运行
- **停止**：完全停止脚本

## 技术架构

- **开发语言**：Kotlin
- **目标版本**：Android 8.0+ (API 26+)
- **核心组件**：无障碍服务(AccessibilityService)
- **界面框架**：Material Design

## 项目结构

```
app/
├── src/main/
│   ├── java/com/kuaishou/auto/
│   │   ├── MainActivity.kt          # 主界面
│   │   ├── service/
│   │   │   ├── KuaishouAccessibilityService.kt  # 无障碍服务
│   │   │   └── FloatingWindowService.kt         # 悬浮窗服务
│   │   └── util/
│   │       └── ConfigManager.kt     # 配置管理
│   ├── res/
│   │   ├── layout/                  # 界面布局
│   │   ├── values/                  # 资源文件
│   │   └── xml/                     # 配置文件
│   └── AndroidManifest.xml          # 应用清单
└── build.gradle                     # 构建配置
```

## 构建说明

### 使用Android Studio
1. 打开Android Studio
2. 导入项目
3. 连接Android设备或启动模拟器
4. 点击运行按钮

### 使用命令行
```bash
# 清理项目
./gradlew clean

# 构建Debug版本
./gradlew assembleDebug

# 构建Release版本
./gradlew assembleRelease
```

## 注意事项

1. **权限要求**：需要无障碍服务权限才能正常工作
2. **兼容性**：支持Android 8.0及以上版本
3. **稳定性**：建议在稳定的网络环境下使用
4. **合法性**：请遵守快手平台的使用规则

## 更新日志

### v1.0.0
- 初始版本发布
- 实现基础自动刷视频功能
- 支持开始/暂停/停止控制
- 添加悬浮窗状态显示