# Backend dev technical test
We want to offer a new feature to our customers showing similar products to the one they are currently seeing. To do this we agreed with our front-end applications to create a new REST API operation that will provide them the product detail of the similar products for a given one. [Here](./similarProducts.yaml) is the contract we agreed.

We already have an endpoint that provides the product Ids similar for a given one. We also have another endpoint that returns the product detail by product Id. [Here](./existingApis.yaml) is the documentation of the existing APIs.

**Create a Spring boot application that exposes the agreed REST API on port 5000.**

![Diagram](./assets/diagram.jpg "Diagram")

Note that _Test_ and _Mocks_ components are given, you must only implement _yourApp_.

## Testing and Self-evaluation
You can run the same test we will put through your application. You just need to have docker installed.

First of all, you may need to enable file sharing for the `shared` folder on your docker dashboard -> settings -> resources -> file sharing.

Then you can start the mocks and other needed infrastructure with the following command.
```
docker-compose up -d simulado influxdb grafana
```
Check that mocks are working with a sample request to [http://localhost:3001/product/1/similarids](http://localhost:3001/product/1/similarids).

To execute the test run:
```
docker-compose run --rm k6 run scripts/test.js
```
Browse [http://localhost:3000/d/Le2Ku9NMk/k6-performance-test](http://localhost:3000/d/Le2Ku9NMk/k6-performance-test) to view the results.

## Evaluation
The following topics will be considered:
- Code clarity and maintainability
- Performance
- Resilience

## How to test the resolution
The only thing needed is to add the new application container to the docker-compose command.
```
docker-compose up -d simulado influxdb grafana shop_app
```

With this command, the application image will be downloaded from Docker hub to save time.

If you want to execute it isolated you can build first execute the previous docker-compose command without shop_app and then build the image of the new application using the following command (you can even download it from Docker hub):
```
docker build -t shop_app . -f ./apps/shop_app/Dockerfile
```

After that you can run the application using the following instruction:
```
docker run -it --add-host=host.docker.internal:host-gateway -p 5000:5000 --env PRODUCTS_URL=http://host.docker.internal:3001 shop_app
```
We pass the bridge domain of the host as environment variable, and we ensure the exposed port is the 5000.

After the container is up, we can launch an execution to the url GET http://localhost:5000/product/1/similar using Postman or whatever and the execution should return a filled list. 