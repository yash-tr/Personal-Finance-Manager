@echo off
echo Testing registration and login...
echo.

echo 1. Testing registration...
curl -X POST "http://localhost:8080/api/auth/register" -H "Content-Type: application/json" -d "{\"username\":\"test@example.com\",\"password\":\"password123\",\"fullName\":\"Test User\",\"phoneNumber\":\"+1234567890\"}"
echo.
echo.

echo 2. Testing login...
curl -X POST "http://localhost:8080/api/auth/login" -H "Content-Type: application/json" -d "{\"username\":\"test@example.com\",\"password\":\"password123\"}"
echo.
echo.

echo Tests completed.
pause 