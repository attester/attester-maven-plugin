@echo off

echo Please make sure phantomjs.exe version 1.9.0 is in the current directory.
echo Then press any key to continue. This will execute the following command line:
echo call mvn install:install-file -Dfile="phantomjs.exe" -DgroupId=com.google.code.phantomjs -DartifactId=phantomjs -Dversion=1.9.0 -Dpackaging=exe -Dclassifier=win32

pause
call mvn install:install-file -Dfile="phantomjs.exe" -DgroupId=com.google.code.phantomjs -DartifactId=phantomjs -Dversion=1.9.0 -Dpackaging=exe -Dclassifier=win32
pause
