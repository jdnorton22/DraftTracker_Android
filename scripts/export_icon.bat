@echo off
REM Export Fantasy Draft Picker app icon to PNG

echo Fantasy Draft Picker - Icon Export Tool
echo ========================================
echo.

if "%~1"=="" (
    echo Usage: export_icon.bat ^<output_path^> [size]
    echo.
    echo Examples:
    echo   export_icon.bat icon.png
    echo   export_icon.bat C:\Icons\fantasy_draft_icon.png 1024
    echo   export_icon.bat icon.png 512
    echo.
    echo Default size is 512x512 pixels if not specified.
    exit /b 1
)

python scripts\export_app_icon.py %*
