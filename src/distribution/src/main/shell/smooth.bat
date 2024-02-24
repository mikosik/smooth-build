@echo off
set "BIN_DIRECTORY=%~dp0"
java --enable-preview ^
     -cp "%BIN_DIRECTORY%\smooth.jar" org.smoothbuild.cli.Main %*
