@echo off
start /min cmd.exe @cmd /c docker compose -f .\Docker-DBs\docker-compose.yml up
echo Starting DBs...
timeout 5 > nul
start /min cmd.exe @cmd /c "cd .\Backend-SSO && .\mvnw clean install -DskipTests -q && java -jar .\target\Backend-SSO.jar --spring.profiles.active=demo"
start /min cmd.exe @cmd /c "cd .\Backend-App1 && .\mvnw clean install -DskipTests -q && java -jar .\target\Backend-App1.jar --spring.profiles.active=demo"
start /min cmd.exe @cmd /c "cd .\Backend-App2 && .\mvnw clean install -DskipTests -q && java -jar .\target\Backend-App2.jar"
start /min cmd.exe @cmd /c npm start --prefix .\Frontend-SSO\
start /min cmd.exe @cmd /c npm start --prefix .\Frontend-App1\
echo Starting system...
