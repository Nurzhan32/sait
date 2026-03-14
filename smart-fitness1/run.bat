@echo off
cd /d %~dp0
echo Compiling...
javac --module-path "C:\javafx-sdk-25\lib" --add-modules javafx.controls -d out main.java
if errorlevel 1 (
  echo Compilation failed.
  pause
  exit /b 1
)
echo Copying CSS to out...
copy /Y styles.css out >nul
echo Running...
java --module-path "C:\javafx-sdk-25\lib" --add-modules javafx.controls -cp out main
pause
