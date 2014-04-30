@ECHO OFF

SET SMOOTH_DIR=%~dp
java -cp %SMOOTH_DIR%/smooth.jar;%SMOOTH_DIR%/funcs.jar org.smoothbuild.Main %*

