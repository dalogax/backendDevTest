# Enunciado

El enunciado de la prueba se encuentra en: https://github.com/dalogax/backendDevTest#readme

# Despliegue

Para realizar el despliegue de la prueba, se ha modificado el docker para que el compose también lance la propia prueba, para ello sólo habrá que ejecutar el comando:

```
docker-compose up -d
```

Posteriormente, para realizar los tests de rendimiento:

```
docker-compose run --rm k6 run scripts/test.js
```

Finalmente se pueden visualizar los datos en: http://localhost:3000/d/Le2Ku9NMk/k6-performance-test

## Despliegue sin docker

Si se desea desplegar sin dockerizar la aplicación, será **importante modificar el fichero application.properties** en la ruta /src/main/resources. Habrá que editar la variable **application.simulado.host** indicando el host donde se encuentren los mocks (por defecto, http://localhost:3001)


# Consideraciones de diseño

1. Se ha utilizado WebFlux para realizar las peticiones de manera asíncrona, ya que se puede paralelizar las peticiones de los productos relacionados, mejorando considerablemente el rendimiento.
2. Se ha utilizado arquitectura hexagonal, aunque la realidad es que es un error utilizar este tipo de arquitecturas para proyectos tan pequeños, pero al ser una prueba técnica se entiende que el proyecto real será mucho mayor.
3. Se han utilizado algunos principios de DDD a la hora de definir el dominio como value objects. Tampoco es necesario en este proyecto, porque se entiende que los datos vienen validados de la API externa. Igualmente se utiliza por la misma razón que la arquitectura hexagonal.
4. Al tener un objeto de dominio modelado diferente a como se pretenden transmitir los datos, se ha tenido que añadir DTOs y Mappers.
5. Se han implementado tests unitarios, de integración y E2E, aunque no son muy exhaustivos, pues no eran un requisito para la prueba.
