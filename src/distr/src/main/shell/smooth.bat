@echo off
set "SMOOTH_HOME=%~dp0"
java --enable-preview ^
     -cp "%SMOOTH_HOME%\smooth.jar" org.smoothbuild.cli.Main %*
