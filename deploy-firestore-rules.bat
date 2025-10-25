@echo off
REM ============================================
REM Deploy Firestore Security Rules
REM ============================================

echo.
echo ========================================
echo   Nestera - Deploy Firestore Rules
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

echo [1/4] Checking Firebase login status...
firebase login:ci >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [INFO] Not logged in. Opening browser for authentication...
    firebase login
)

echo [2/4] Verifying firestore.rules file...
if not exist "firestore.rules" (
    echo [ERROR] firestore.rules not found!
    pause
    exit /b 1
)
echo [OK] Rules file found

echo [3/4] Validating rules syntax...
firebase deploy --only firestore:rules --dry-run
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Rules validation failed!
    echo Please check firestore.rules for syntax errors
    pause
    exit /b 1
)

echo [4/4] Deploying rules to Firebase...
firebase deploy --only firestore:rules,firestore:indexes,storage

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo   Deployment Successful!
    echo ========================================
    echo.
    echo Next steps:
    echo 1. Verify rules in Firebase Console
    echo 2. Test rules with emulator or Rules Playground
    echo 3. Update app authentication to use Custom Claims
    echo.
) else (
    echo.
    echo ========================================
    echo   Deployment Failed!
    echo ========================================
    echo.
    echo Check error messages above
    echo.
)

pause
