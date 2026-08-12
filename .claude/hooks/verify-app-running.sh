#!/bin/sh
# PreToolUse hook (matcher: Bash, if: docker-compose run ... k6 ...).
# Refuses to launch the k6 load test if the app isn't answering on port 5000,
# so the test doesn't report a wall of connection errors.

status=$(curl -s -o /dev/null -w "%{http_code}" --max-time 3 http://localhost:5000/product/1/similar 2>/dev/null)

if [ "$status" = "200" ]; then
  exit 0
fi

echo "App is not responding at http://localhost:5000 (got '${status:-no response}'). Start it before load testing: cd app && mvn spring-boot:run — and make sure mocks are up: docker-compose up -d simulado." >&2
exit 2
