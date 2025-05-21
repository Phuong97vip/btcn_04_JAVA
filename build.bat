@echo off
echo Building Chat Application...

echo Building Client...
cd ChatClient
if not exist bin mkdir bin
javac -d bin -sourcepath src src/client/*.java
jar cfm ChatClient.jar MANIFEST.MF -C bin .
cd ..

echo Building Server...
cd ChatServer
if not exist bin mkdir bin
javac -d bin -sourcepath src src/server/*.java
jar cfm ChatServer.jar MANIFEST.MF -C bin .
cd ..

echo Build complete!
echo JAR files created:
echo - ChatClient/ChatClient.jar
echo - ChatServer/ChatServer.jar
echo.
echo To run the application:
echo 1. Start the server: java -jar ChatServer/ChatServer.jar
echo 2. Start the client: java -jar ChatClient/ChatClient.jar
pause 