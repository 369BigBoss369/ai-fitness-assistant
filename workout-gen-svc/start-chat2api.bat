@echo off
echo Starting Chat2API server...
cd /d "%~dp0chat2api"
if not exist "app.py" (
    echo ERROR: app.py not found in chat2api directory
    pause
    exit /b 1
)

REM Set the authorization token from environment variable or use default
if "%CHAT2API_ACCESS_TOKEN%"=="" (
    echo WARNING: CHAT2API_ACCESS_TOKEN environment variable not set
    echo Chat2API may not work without authentication
) else (
    echo Setting AUTHORIZATION environment variable...
    set AUTHORIZATION=%CHAT2API_ACCESS_TOKEN%
)

echo Running: python app.py
python app.py
echo Chat2API server stopped.
