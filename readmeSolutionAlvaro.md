# Backend dev technical test
**Note:** Deleted simulado and created mock server that uses a secure Node.js for security reasons.

# To execute the test run:

docker-compose up -d mock-server influxdb grafana

curl http://localhost:3001/product/1/similarids

curl http://localhost:5000/product/1/similar

docker-compose run --rm k6 run /scripts/test.js

0% failed, 100% success