:restartAll
copy /y ..\HyperiorCloud\build\libs\HyperiorCloud-1.1.0-SNAPSHOT.jar .
copy /y ..\HyperiorCore\build\libs\HyperiorCore-5.0.0-SNAPSHOT.jar templates\lobby\plugins
copy /y ..\HyperiorCore\build\libs\HyperiorCore-5.0.0-SNAPSHOT.jar templates\proxy\plugins
copy /y ..\HyperiorLobby\build\libs\HyperiorLobby-4.0.0-SNAPSHOT.jar templates\lobby\plugins
copy /y ..\HyperiorProxy\build\libs\HyperiorProxy-5.0.0-SNAPSHOT.jar templates\proxy\plugins
java -jar HyperiorCloud-1.1.0-SNAPSHOT.jar
goto restartAll
