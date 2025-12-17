@echo off
echo 正在验证Android项目结构...
echo.

if exist "app\src\main\AndroidManifest.xml" (
    echo ✅ AndroidManifest.xml 存在
) else (
    echo ❌ AndroidManifest.xml 不存在
)

if exist "app\build.gradle" (
    echo ✅ app/build.gradle 存在
) else (
    echo ❌ app/build.gradle 不存在
)

if exist "build.gradle" (
    echo ✅ build.gradle 存在
) else (
    echo ❌ build.gradle 不存在
)

if exist "settings.gradle" (
    echo ✅ settings.gradle 存在
) else (
    echo ❌ settings.gradle 不存在
)

if exist "gradlew" (
    echo ✅ gradlew 存在
) else (
    echo ❌ gradlew 不存在
)

if exist ".github\workflows\android-build.yml" (
    echo ✅ GitHub Actions配置 存在
) else (
    echo ❌ GitHub Actions配置 不存在
)

echo.
echo 项目结构验证完成！
echo 如果所有文件都存在，GitHub Actions应该可以正常构建。
pause