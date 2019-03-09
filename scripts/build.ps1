Set-Location GalileoSDK
MSBuild.exe GalileoSDK.sln /p:configuration=Debug /p:platform=x64
Set-Location ..
Remove-Item javagalileo\libs\win-x64\*
Copy-Item JNIGalileoWrapper\x64\Debug\GalileoSDK.lib javagalileo\libs\win-x64\
Copy-Item JNIGalileoWrapper\x64\Debug\GalileoSDK.dll javagalileo\libs\win-x64\
Set-Location JNIGalileoWrapper
MSBuild.exe JNIGalileoWrapper.sln /p:configuration=Debug /p:platform=x64
Set-Location ..\javagalileo
mvn clean
mvn compile
Set-Location target/classes
javah -classpath . javagalileo.GalileoSDK
Remove-Item ..\..\include\*.h
Move-Item *.h ..\..\include
Set-Location ..