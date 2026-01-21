@echo off
echo =============================================
echo        Starting Store Client...
echo =============================================
echo.

cd /d "%~dp0"

set CLASSPATH=target\classes;lib\gson-2.10.1.jar

java -cp "%CLASSPATH%" com.storemgmt.client.ClientApp

pause
