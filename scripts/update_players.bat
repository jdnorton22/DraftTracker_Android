@echo off
REM Fantasy Football Player Data Updater - Windows Batch Script
REM This script updates the players.json file with current data

echo ============================================================
echo Fantasy Football Player Data Updater
echo ============================================================
echo.

REM Check if Python is installed
python --version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Python is not installed or not in PATH
    echo Please install Python from https://www.python.org/
    pause
    exit /b 1
)

echo Python found. Checking for required packages...
echo.

REM Check if requests module is installed
python -c "import requests" >nul 2>&1
if errorlevel 1 (
    echo Installing required package: requests
    pip install requests
    echo.
)

echo Running player data update script...
echo.

REM Run the Python script
python scripts\update_players.py

echo.
echo ============================================================
echo Update Complete!
echo ============================================================
echo.
echo Next steps:
echo 1. Review app\src\main\res\raw\players.json
echo 2. Build: gradlew assembleDebug
echo 3. Deploy: adb install -r app\build\outputs\apk\debug\app-debug.apk
echo.

pause
