
rem @ECHO OFF
SETLOCAL

set OPENREFINE_ROOT=C:\src\OpenRefine-2.6-beta.1

rmdir \s \q ..\local-repository
mkdir ..\local-repository

:: Do not put the double quote between the variable. mvn do not like it.
call :import main\webapp\WEB-INF\lib\ant-tools-1.8.0.jar ant-tools ant-tools 1.8.0
call :import main\webapp\WEB-INF\lib\butterfly-1.0.1.jar edu.mit.simile butterfly 1.0.1
call :import main\webapp\WEB-INF\lib\opencsv-2.4-SNAPSHOT.jar net.sf.opencsv opencsv 2.4-SNAPSHOT

call :import build\openrefine-trunk.jar org.openrefine openrefine-core 2.6.1
call :import build\openrefine-trunk-server.jar org.openrefine openrefine-server 2.6.1

:: force execution to quit at the end of the main logic
EXIT /B %ERRORLEVEL%

:import
  set source=%1
  set group=%2
  set id=%3
  set version=%4
  :: have to call external program: http://stackoverflow.com/questions/232651/why-the-system-cannot-find-the-batch-label-specified-is-thrown-even-if-label-e
  call mvn org.apache.maven.plugins:maven-install-plugin:2.3.1:install-file^
 -Dfile=%OPENREFINE_ROOT%\%source%^
 -DgroupId=%group%^
 -DartifactId=%id%^
 -Dversion=%version%^
 -Dpackaging=jar^
 -DlocalRepositoryPath=..\local-repository
EXIT /B 0
