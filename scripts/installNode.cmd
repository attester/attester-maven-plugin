@echo off

echo Please make sure node.exe (version 4.2.1) is in the current directory.
echo Then press any key to continue. This will execute the following command line:
echo call mvn install:install-file -Dfile="node.exe" -DgroupId=org.nodejs -DartifactId=node -Dversion=4.2.1 -Dpackaging=exe

pause
call mvn install:install-file -Dfile="node.exe" -DgroupId=org.nodejs -DartifactId=node -Dversion=4.2.1 -Dpackaging=exe
pause
