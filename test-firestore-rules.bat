@echo off
REM ============================================
REM Test Firestore Security Rules in Emulator
REM ============================================

echo.
echo ========================================
echo   Nestera - Test Firestore Rules
echo ========================================
echo.

REM Check if Firebase CLI is installed
where firebase >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Firebase CLI not found!
    echo.
    echo Please install Firebase CLI:
    echo   npm install -g firebase-tools
    echo.
    pause
    exit /b 1
)

echo [1/3] Initializing Firebase emulators...
if not exist "firebase.json" (
    echo [INFO] Initializing Firebase project...
    firebase init emulators
)

echo [2/3] Starting Firestore emulator...
echo.
echo Emulator will run on:
echo - Firestore: http://localhost:8080
echo - Emulator UI: http://localhost:4000
echo.
echo Press Ctrl+C to stop emulator
echo.

start http://localhost:4000

firebase emulators:start --only firestore

echo.
echo Emulator stopped
pause
