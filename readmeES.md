# Prueba técnica de desarrollo backend

Queremos ofrecer a nuestros clientes una nueva función que les muestre productos similares al que están viendo actualmente. Para ello, hemos acordado con nuestras aplicaciones frontend crear una nueva operación API REST que les proporcione los detalles de los productos similares a uno determinado. [Aquí](./similarProducts.yaml) está el contrato que hemos acordado.

Ya tenemos un endpoint que proporciona los ID de productos similares a uno determinado. También tenemos otro endpoint que devuelve los detalles del producto por ID de producto. [Aquí](./existingApis.yaml) está la documentación de las API existentes.

**Crea una aplicación Spring Boot que exponga la API REST acordada en el puerto 5000.**

![Diagrama](./assets/diagram.jpg «Diagrama»)

Ten en cuenta que se proporcionan los componentes _Test_ y _Mocks_, solo debes implementar _yourApp_.

## Pruebas y autoevaluación

Puedes ejecutar la misma prueba que realizaremos en tu aplicación. Solo necesitas tener instalado Docker.

En primer lugar, es posible que tengas que habilitar el uso compartido de archivos para la carpeta `shared` en tu panel de control de Docker -> configuración -> recursos -> uso compartido de archivos.

A continuación, puede iniciar las simulaciones y otra infraestructura necesaria con el siguiente comando:

```
docker-compose up -d mock-server influxdb grafana
```

Compruebe que las simulaciones funcionan con una solicitud de muestra a [http://localhost:3001/product/1/similarids](http://localhost:3001/product/1/similarids).

Para ejecutar la prueba:

```
docker-compose run --rm k6 run scripts/test.js
```

Navega a [http://localhost:3000/d/Le2Ku9NMk/k6-performance-test](http://localhost:3000/d/Le2Ku9NMk/k6-performance-test) para ver los resultados.

## Evaluación

Se tendrán en cuenta los siguientes aspectos:

-   Claridad y facilidad de mantenimiento del código.
-   Rendimiento.
-   Resiliencia.
