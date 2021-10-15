@echo off
set rtm_java_root=%RTM_JAVA_ROOT%
set JAR_BASE=%rtm_java_root%\jar\
for /F %%A in ('dir "%JAR_BASE%OpenRTM*" /B') do (set FILE1=%%A)
for /F %%A in ('dir "%JAR_BASE%commons-cli*" /B') do (set FILE2=%%A)
for /F %%A in ('dir "%JAR_BASE%jna-?.?.?.*" /B') do (set FILE3=%%A)
for /F %%A in ('dir "%JAR_BASE%jna-platform-?.?.?.*" /B') do (set FILE4=%%A)
set CLASSPATH=.;%JAR_BASE%%FILE1%;%JAR_BASE%%FILE2%;%JAR_BASE%%FILE3%;%JAR_BASE%%FILE4%;%~dp0\bin
@echo on 
java template_javaComp -f rtc.conf %*
pause;
