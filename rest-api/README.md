### Spring Boot Rest API

This version contains some *beans*, *exception handlers* and *tests*, to view a simple version [click here](https://github.com/victorgomez09/backendDevTest/tree/basic_rest_api).

Application runs on port **5000**

Mocks url is located in `application.properties`
```
mocks.url=http://localhost:3001
```

To run you have 2 options:
- Run with `Spring Boot Dashboard`, a plugin that comes in most IDEs.
- Package and run .jar file:
    1. Run `mvn packge` inside **rest-api** project folder
    2. Go to target folder and run `java -jar rest-api-0.0.1-SNAPSHOT.jar`
