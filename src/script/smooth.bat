@ECHO OFF

SET SMOOTH_DIR=%~dp
java -cp %SMOOTH_DIR%/smooth-all.jar org.smoothbuild.Main %*

