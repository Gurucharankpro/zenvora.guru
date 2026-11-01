@echo off
java %JAVA_OPTS% %GRADLE_OPTS% -classpath "%~dp0\gradle\wrapper\gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain %*
