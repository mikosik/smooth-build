@ECHO OFF

SET SMOOTH_HOME=%~dp
java -cp %SMOOTH_HOME%\smooth.jar org.smoothbuild.Main %*

