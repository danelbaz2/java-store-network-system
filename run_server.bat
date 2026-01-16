@echo off
echo =============================================
echo        Starting Store Server...
echo =============================================
echo.

cd /d "%~dp0"

set CLASSPATH=target\classes;%USERPROFILE%\.m2\repository\com\google\code\gson\gson\2.10.1\gson-2.10.1.jar

java -cp "%CLASSPATH%" com.storemgmt.server.ServerApp

pause
