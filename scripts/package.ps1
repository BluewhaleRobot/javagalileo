# build JNIGalileoWrapper
Set-Location JNIGalileoWrapper
MSBuild.exe JNIGalileoWrapper.sln /p:configuration=Release /p:platform=x64
Set-Location ..
mkdir javagalileo\src\main\resources
Copy-Item JNIGalileoWrapper\x64\Release\GalileoSDK.dll javagalileo\src\main\resources\
Copy-Item JNIGalileoWrapper\x64\Release\JNIGalileoWrapper.dll javagalileo\src\main\resources\
Set-Location javagalileo
mvn package
Set-Location ..