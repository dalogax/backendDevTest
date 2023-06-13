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

:electron::electron::electron::electron:

## Development:

I used hexagonal architecture for the solution, to show the three layers of structure Application, Domain and Infrastructure.

You just need to download the project and update the dependencies in the build.gradle file.

Once downloaded, you can run clean, build and bootRun then the application will be up and running.

you can test the url with postman, for productId:

GET:

 http://localhost:8080/product/3
 
 
Response:

```
{
    "method": "GET",
    "status": 200,
    "path": "/product/3",
    "body": {
        "id": 3,
        "name": "Camisa",
        "price": 70,
        "availability": true
    },
    "headers": null,
    "isRegexPah": false
}
```


for similarids

GET:

 http://localhost:8080/product/14/similarids


 Response:
 ```
 {
    "method": "GET",
    "status": 200,
    "path": "/product/14/similarids",
    "body": [
        {
            "id": 1401,
            "name": "Camisa",
            "price": 120,
            "availability": true
        },
        {
            "id": 1402,
            "name": "Sudadera",
            "price": 85,
            "availability": true
        },
        {
            "id": 1403,
            "name": "Botas",
            "price": 50,
            "availability": false
        }
    ],
    "headers": null,
    "isRegexPah": false
}


:electron::electron::electron::electron::electron:
