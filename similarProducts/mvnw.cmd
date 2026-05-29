@REM ----------------------------------------------------------------------------
@REM Maven Wrapper startup script for Windows
@REM Requires: JAVA_HOME pointing to a JDK 21+
@REM ----------------------------------------------------------------------------
@echo off

if "%JAVA_HOME%" == "" (
    echo.
    echo Error: JAVA_HOME is not set.
    echo Please set JAVA_HOME to a JDK 21+ installation directory.
    echo Example: set JAVA_HOME=C:\Program Files\Java\jdk-21
    echo.
    exit /B 1
)

if not exist "%JAVA_HOME%\bin\java.exe" (
    echo.
    echo Error: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
    echo.
    exit /B 1
)

set "MAVEN_PROJECTBASEDIR=%~dp0"
set WRAPPER_JAR="%MAVEN_PROJECTBASEDIR%.mvn\wrapper\maven-wrapper.jar"

"%JAVA_HOME%\bin\java.exe" ^
  -classpath %WRAPPER_JAR% ^
  "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" ^
  org.apache.maven.wrapper.MavenWrapperMain %*
