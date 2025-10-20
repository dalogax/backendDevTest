# API de Productos Similares con Spring Boot y docker

## Objetivo

Crear un endpoint que, dado un producto, devuelva los detalles completos de sus productos similares mediante:
- Llamada al servicio de IDs similares: `GET /product/{id}/similarids`
- Llamadas paralelas al servicio de detalles: `GET /product/{id}`
- Manejo resiliente de errores (productos no disponibles)

## Stack Tecnológico

- **Java 21**
- **Spring Boot 3.3.x**
- **Spring WebFlux** - Para llamadas HTTP reactivas y paralelas
- **Lombok** - Reducción de código boilerplate
- **Maven** - Gestión de dependencias

## Estructura del Proyecto

```
src/
├── main/
│   ├── java/
│   │   └── com/example/demo/
│   │       ├── DemoApplication.java          # Clase principal
│   │       ├── controller/
│   │       │   └── ProductController.java    # REST Controller
│   │       ├── service/
│   │       │   └── ProductService.java       # Lógica de negocio
│   │       ├── client/
│   │       │   └── ProductClient.java        # Cliente HTTP
│   │       └── model/
│   │           └── Product.java              # Modelo de datos
│   └── resources/
│       └── application.properties            # Configuración
└── test/
```

## Requisitos Previos

### 2. Tener Java, Maven y Docker Desktop

### 3. Configuración de Spring Boot

En `src/main/resources/application.properties`:

```properties
# Puerto de la aplicación
server.port=5000

# URLs de los servicios mock
product.service.baseurl=http://localhost:3001
```

## Implementación

### 1. Modelo de Datos (`Product.java`)

```java
package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private String id;
    private String name;
    private double price;
    private boolean availability;
}
```

**Características:**
- `@Data`: Genera getters, setters, toString, equals y hashCode
- `@AllArgsConstructor`: Constructor con todos los parámetros
- `@NoArgsConstructor`: Constructor vacío (necesario para deserialización)

### 2. Controller (`ProductController.java`)

```java
package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.service.ProductService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {
    
    private final ProductService productService;
    
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    @GetMapping("/{productId}/similar")
    public List<Product> getSimilarProducts(@PathVariable String productId) {
        return productService.getSimilarProductsWithDetails(productId);
    }
}
```

**Puntos clave:**
- `@RestController`: Combina `@Controller` + `@ResponseBody`
- `@GetMapping`: Mapea peticiones HTTP GET
- `@PathVariable`: Extrae variables de la URL
- Inyección de dependencias vía constructor (recomendado sobre `@Autowired`)

### 3. Cliente HTTP (`ProductClient.java`)

```java
package com.example.demo.client;

import com.example.demo.model.Product;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class ProductClient {
    
    private final WebClient webClient;
    
    public ProductClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
            .baseUrl("http://localhost:3001")
            .build();
    }
    
    public List<String> getSimilarProductIds(String productId) {
        return webClient.get()
            .uri("/product/{id}/similarids", productId)
            .retrieve()
            .bodyToMono(List.class)
            .block();
    }
    
    public Mono<Product> getProductDetail(String productId) {
        return webClient.get()
            .uri("/product/{id}", productId)
            .retrieve()
            .bodyToMono(Product.class)
            .onErrorResume(error -> Mono.empty());
    }
}
```

**Características importantes:**
- **WebClient**: Cliente HTTP reactivo (reemplazo de RestTemplate)
- **Mono**: Representa 0 o 1 elemento asíncrono
- **onErrorResume(Mono.empty())**: Manejo resiliente - continúa si un producto falla
- **block()**: Convierte operación asíncrona a síncrona (solo para IDs)

### 4. Servicio (`ProductService.java`)

```java
package com.example.demo.service;

import com.example.demo.client.ProductClient;
import com.example.demo.model.Product;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
public class ProductService {
    
    private final ProductClient productClient;
    
    public ProductService(ProductClient productClient) {
        this.productClient = productClient;
    }
    
    public List<Product> getSimilarProductsWithDetails(String productId) {
        // 1. Obtener IDs de productos similares
        List<String> similarIds = productClient.getSimilarProductIds(productId);
        
        // 2. Obtener detalles en PARALELO y filtrar nulls
        return Flux.fromIterable(similarIds)
            .flatMap(id -> productClient.getProductDetail(id))
            .collectList()
            .block();
    }
}
```

**Flujo de ejecución:**
1. Obtiene lista de IDs similares de forma síncrona
2. `Flux.fromIterable()`: Convierte lista en stream reactivo
3. `flatMap()`: Ejecuta llamadas HTTP **en paralelo**
4. Productos con error se filtran automáticamente (Mono.empty())
5. `collectList()`: Agrupa resultados
6. `block()`: Espera a que terminen todas las llamadas

## Rendimiento y Optimización

### Llamadas Paralelas vs Secuenciales

**Escenario:** Producto tiene 3 similares [2, 3, 1000]
- Producto 2: 10ms
- Producto 3: 100ms (delay configurado en mock)
- Producto 1000: 5000ms (delay configurado en mock)

**Secuencial:**
```
Total = 10ms + 100ms + 5000ms = 5110ms
```

**Paralelo (el usado):**
```
Total = max(10ms, 100ms, 5000ms) = 5000ms
```

**Mejora:** ~10ms de diferencia no parece mucho, pero con más productos la diferencia es exponencial.

### Manejo de Errores

Productos que fallan (5, 6 en mocks) **no rompen** la respuesta completa:
- **Correcto:** Devolver solo productos exitosos
- **Incorrecto:** Fallar toda la petición por un producto