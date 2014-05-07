@ECHO OFF

SET SMOOTH_HOME=%~dp
java -cp %SMOOTH_HOME%\smooth.jar;%SMOOTH_HOME%\funcs.jar org.smoothbuild.Main %*

