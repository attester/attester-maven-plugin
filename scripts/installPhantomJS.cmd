@echo off

echo Please make sure phantomjs-1.6.0-win32-static.zip is in the current directory.
echo Then press any key to continue. This will execute the following command line:
echo call mvn install:install-file -Dfile="phantomjs-1.6.0-win32-static.zip" -DgroupId=com.google.code.phantomjs -DartifactId=phantomjs -Dversion=1.6.0 -Dpackaging=zip -Dclassifier=win32-static

pause
call mvn install:install-file -Dfile="phantomjs-1.6.0-win32-static.zip" -DgroupId=com.google.code.phantomjs -DartifactId=phantomjs -Dversion=1.6.0 -Dpackaging=zip -Dclassifier=win32-static
pause
