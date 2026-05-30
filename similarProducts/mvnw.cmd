@REM ----------------------------------------------------------------------------
@REM Maven Wrapper startup script for Windows
@REM Requires: JAVA_HOME pointing to a JDK 21+
@REM ----------------------------------------------------------------------------
@echo off
setlocal

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
set "WRAPPER_JAR=%MAVEN_PROJECTBASEDIR%.mvn\wrapper\maven-wrapper.jar"

@REM Build the full java command into a temporary script to avoid %* space issues
set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"

@REM Use short 8.3 paths to avoid space-in-path issues with argument passing
for %%I in ("%WRAPPER_JAR%") do set "WRAPPER_JAR_SHORT=%%~sI"
for %%I in ("%MAVEN_PROJECTBASEDIR%") do set "BASEDIR_SHORT=%%~sI"

"%JAVA_EXE%" ^
  -classpath "%WRAPPER_JAR_SHORT%" ^
  "-Dmaven.multiModuleProjectDirectory=%BASEDIR_SHORT%" ^
  org.apache.maven.wrapper.MavenWrapperMain %*
