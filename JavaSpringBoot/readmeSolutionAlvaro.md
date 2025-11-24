Total time 3h aprox

# To execute the test run in cmd the following:

Start Java Spring boot application main

docker-compose up -d simulado influxdb grafana

curl http://localhost:3001/product/1/similarids

curl http://localhost:5000/product/1/similar

docker-compose run --rm k6 run /scripts/test.js

0% failed, 100% success

