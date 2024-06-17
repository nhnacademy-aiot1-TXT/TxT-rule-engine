#!/bin/bash

echo "Stopping existing process..."
existing_pid=$(ps aux | grep 'rule-engine-0.0.1-SNAPSHOT.jar' | grep -v grep | awk '{print $2}')
if [ -n "$existing_pid" ]; then
    kill -9 $existing_pid
    echo "Existing process stopped."
else
    echo "No existing process found."
fi

echo "Starting new process..."
java -jar -Dspring.profiles.active=prod /app/rule-engine-0.0.1-SNAPSHOT.jar > /app/log 2>&1 &
echo "New process started."
