Set-Location GalileoSDK
MSBuild.exe GalileoSDK.sln /p:configuration=release /p:platform=x64
Copy-Item x64\Release\GalileoSDK.dll ..\javagalileo\libs\win-x64\
Copy-Item x64\Release\GalileoSDK.lib ..\javagalileo\libs\win-x64\
Set-Location ..
Set-Location javagalileo
mvn clean
mvn compile
Set-Location target/classes
javah -classpath . javagalileo.GalileoSDK
Remove-Item ..\..\include\*.h
Move-Item *.h ..\..\include