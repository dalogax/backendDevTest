#!/bin/sh
# PreToolUse hook (matcher: Bash).
# Refuses to launch the k6 load test if the app isn't answering on port 5000,
# so the test doesn't report a wall of connection errors.
#
# PreToolUse only supports `matcher` (a tool-NAME regex) — there is no config field
# that filters on command content — so the command filter lives here. Anything that
# isn't a k6 run passes straight through.

payload=$(cat)

echo "$payload" | tr '\n' ' ' | grep -Eq '"command"[^"]*"[^"]*k6' || exit 0

status=$(curl -s -o /dev/null -w "%{http_code}" --max-time 3 http://localhost:5000/product/1/similar 2>/dev/null)

if [ "$status" = "200" ]; then
  exit 0
fi

echo "App is not responding at http://localhost:5000 (got '${status:-no response}'). Start it before load testing: cd app && mvn spring-boot:run — and make sure mocks are up: docker-compose up -d simulado." >&2
exit 2
