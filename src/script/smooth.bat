@ECHO OFF

SET SMOOTH_DIR=%~dp
java -cp %SMOOTH_DIR%/smooth.jar org.smoothbuild.Main %*

