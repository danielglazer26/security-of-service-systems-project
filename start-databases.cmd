@echo off
start cmd.exe @cmd /c docker compose -f .\Docker-DBs\docker-compose.yml up
echo Databases started successfully!