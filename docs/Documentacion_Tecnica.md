# Documentación Técnica - API Gestión de Fondos BTG Pactual

## 1. Stack Tecnológico General
* **Lenguaje:** Java 17
* **Framework Backend:** Spring Boot 3.2.x
* **Persistencia de Datos:** MongoDB (Spring Data MongoDB)
* **Seguridad:** Spring Security con JSON Web Tokens (JWT)
* **Testing:** JUnit 5, Mockito
* **Infraestructura como Código:** AWS CloudFormation
* **Control de Dependencias:** Maven

## 2. Arquitectura de Software
Se ha utilizado el patrón arquitectónico de **Capas (Layered Architecture)**, fomentando los principios de Clean Code y Separación de Preocupaciones.
* **Domain:** Define las entidades exactas de base de datos modeladas para NoSQL (`User`, `Fund`, `Transaction`).
* **DTO (Data Transfer Objects):** Objetos de entrada/salida que viajan en las peticiones HTTP HTTP para aislar la base de datos de las exposiciones REST (Ej: `SubscribeRequest`).
* **Repository:** Capa de abstracción usando `MongoRepository` permitiendo consultas asíncronas sin utilizar queries quemados.
* **Service:** Capa de lógica de negocio transaccional. Ej: `FundService` controla las transacciones financieras y validaciones del cliente, validando la integridad referencial lógicamente.
* **Controller:** Controladores REST limpios. Su única responsabilidad es recibir peticiones Json, mapearlas y reenviarlas al Service.

## 3. Modelo NoSQL (Colecciones Documentales)
Al elegir MongoDB, la información se almacena en 3 documentos principales:
1. **Colección `users`**: Contiene credenciales encriptadas y el estado monetario en tiempo real (`balance`).
2. **Colección `funds`**: Catálogo base.
3. **Colección `transactions`**: Auditoría central de inmutabilidad (Inmutable Log). Cada vez que hay una operación, nunca se borra, se agrega un nuevo documento `Type: SUBSCRIBE` o `Type: UNSUBSCRIBE`, lo que permite recalcular estados y crear historial auditable.

## 4. Modelo de Seguridad
* **Autenticación (Stateless):** Endpoint `/api/auth/login` que genera un JWT firmado con un Secret Key (`HMAC-SHA256`). El token vence a las 24 horas.
* **Contraseñas:** Encriptadas en la colección NoSQL mediante el algoritmo **BCrypt** (`PasswordEncoder`).
* **Autorización:** Todo acceso que inicia por `/api/funds/**` es filtrado por la clase custom `JwtAuthenticationFilter`, la cual verifica las firmas sin la necesidad de tener sesiones HTTP vivas.

## 5. Manejo Global de Excepciones (Error Handling)
Implementado bajo la anotación `@ControllerAdvice` (`GlobalExceptionHandler.java`). 
Este mecanismo evita fugas de System stacktraces y excepciones fallidas de tomcat hacia el exterior. Por el contrario, intercepta las excepciones creadas expresamente como `InsufficientBalanceException` (para la regla de negocio del saldo insuficiente) y retorna respuestas `HTTP 400 Bad Request` formateadas estandarizadamente a JSON para su simple consumo por clientes Front-End.

## 6. Despliegue e Infraestructura Cloud (AWS)
La solución está proyectada para desplegarse Serverless sobre AWS.
En la ruta `/aws/cloudformation-template.yaml` se incluye un archivo de *Infrastructure As Code (IaC)*.
* Despliega la aplicación sobre **AWS ECS (Elastic Container Service) con tipo de lanzamiento FARGATE**, aislando la aplicación sobre contenedores administrados evitando mantenimiento a nivel de SO (Mínimo esfuerzo de Ops).
* Utiliza variables de entorno (Envs) dinámicas inyectando de forma segura los endpoints de la base de datos que podría ser instanciada en **AWS DocumentDB** (la versión soportada por AWS que mantiene compatibilidad técnica completa e ininterrumpida con MongoDB).
