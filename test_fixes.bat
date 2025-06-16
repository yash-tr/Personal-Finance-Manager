@echo off
echo ========================================
echo Testing Fixed Application
echo ========================================
echo.
echo Waiting for server to start...
timeout /t 20 /nobreak > nul
echo.
echo Running comprehensive tests against localhost...
echo Target URL: http://localhost:8080/api
echo.

"C:\Program Files\Git\bin\bash.exe" financial_manager_tests.sh "http://localhost:8080/api"

echo.
echo ========================================
echo Test execution completed.
echo ========================================
pause 