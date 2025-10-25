@echo off
echo Checking Android logs...
adb logcat -d | findstr /i "NguoiThueDebug AndroidRuntime FATAL Exception"
pause

