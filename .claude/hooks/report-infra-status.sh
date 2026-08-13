#!/bin/sh
# SessionStart hook.
# Prints the state of the test infrastructure so Claude knows, at the start of a
# session, whether the mock server and the app are reachable. On SessionStart the
# stdout of an exit-0 hook is injected into Claude's context.

mock=$(curl -s -o /dev/null -w "%{http_code}" --max-time 2 http://localhost:3001/product/1/similarids 2>/dev/null)
app=$(curl -s -o /dev/null -w "%{http_code}" --max-time 2 http://localhost:5000/product/1/similar 2>/dev/null)

[ "$mock" = "200" ] && mock_state="up" || mock_state="down"
[ "$app" = "200" ] && app_state="up" || app_state="down"

echo "Similar Products infra status: Simulado mock (:3001) is $mock_state; app (:5000) is $app_state."
[ "$mock_state" = "down" ] && echo "Start mocks with: docker-compose up -d simulado influxdb grafana"
[ "$app_state" = "down" ] && echo "Start the app with: cd app && mvn spring-boot:run (or the 'yourapp' compose service)."

exit 0
