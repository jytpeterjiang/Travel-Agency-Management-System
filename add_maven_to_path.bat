@echo off

set SCRIPT_DIR=%~dp0

set MAVEN_BIN=%SCRIPT_DIR%maven\bin

if not exist "%MAVEN_BIN%" (
    echo Maven bin directory not found: %MAVEN_BIN%
    pause
    exit /b 1
)

setx PATH "%MAVEN_BIN%;%PATH%"

echo Maven bin directory has been permanently added to PATH.
echo Please restart your terminal or IDE to take effect.

pause