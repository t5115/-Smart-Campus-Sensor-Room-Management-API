
# Sensor Management API (JAX-RS)

## Overview

This project is a RESTful API built using JAX-RS (Jersey) for managing rooms and sensors within a campus environment.

The API supports:

- Room/Sensor creation, retrieval, and deletion  
- Nested sensor readings  
- Advanced error handling with custom exception mappers  
- Request/response logging for observability  

## Tech Stack

- Java (JDK 8+)  
- JAX-RS (Jersey)  
- Apache Tomcat  
- Maven  
- NetBeans IDE  

## How to Run the Project (Step-by-Step)

### 1. Clone the Repository

```bash
git clone https://github.com/YOUR_USERNAME/YOUR_REPO_NAME.git
cd YOUR_REPO_NAME
```

### 2. Open Project in NetBeans

- Open NetBeans  
- Click File → Open Project  
- Select the cloned project folder  
- Wait for dependencies to load (Maven will auto-build)  

### 3. Set Up Apache Tomcat

If you don’t already have Tomcat:

- Download Apache Tomcat (version 9 or later)  
- Extract it to a known location  

Add Tomcat to NetBeans:

- Go to Services tab  
- Right-click Servers → Add Server  
- Select Apache Tomcat  
- Browse to your Tomcat installation folder  
- Finish setup  

### 4. Configure Project to Use Tomcat

- Right-click your project → Properties  
- Go to Run  
- Set:
  - Server: Apache Tomcat  
  - Context Path: /sensor-api (or leave default)  

### 5. Build the Project

- Right-click project → Clean and Build  

### 6. Run the Project

- Right-click project → Run  

This will:

- Deploy the application to Tomcat  
- Start the server automatically  

## API Base URL

```
http://localhost:8080/api/v1/rooms
```

Example:

```
http://localhost:8080/sensor-api/api/v1
```

## Available Endpoints

### Discovery

```
GET /api/v1
```

### Rooms

```
GET    /api/v1/rooms
POST   /api/v1/rooms
GET    /api/v1/rooms/{roomId}
DELETE /api/v1/rooms/{roomId}
```

### Sensors

```
GET    /api/v1/sensors
GET    /api/v1/sensors?type={type}
POST   /api/v1/sensors
PUT    /api/v1/sensors/{sensorId}/status
```

### Sensor Readings (Sub-Resource)

```
GET    /api/v1/sensors/{sensorId}/reading
POST   /api/v1/sensors/{sensorId}/reading
```

### Testing / Error Simulation

```
GET /api/v1/sensors/test-500
```

## API Testing (cURL Examples)

Below are 5 example requests to test the core functionality of the API.

### 1. Get All Rooms

```bash
curl -X GET http://localhost:8080/api/v1/rooms
```

### 2. Create a New Room

```bash
curl -X POST http://localhost:8080/api/v1/rooms \
-H "Content-Type: application/json" \
-d '{"name":"Room A"}'
```

### 3. Create a New Sensor

```bash
curl -X POST http://localhost:8080/api/v1/sensors \
-H "Content-Type: application/json" \
-d '{"name":"Temperature Sensor","type":"temperature","roomId":"71e1e8ae-f075-446d-a587-0998ae0e3427"}'
```

### 4. Get Sensors by Type (Query Parameter)

```bash
curl -X GET "http://localhost:8080/api/v1/sensors?type=temperature"
```

### 5. Update Sensor Status to MAINTENANCE

```bash
curl -X PUT http://localhost:8080/api/v1/sensors/92c01d38-3c2a-4a04-b7b2-38441b9a8512/status \
-H "Content-Type: application/json" \
-d '{"id":"92c01d38-3c2a-4a04-b7b2-38441b9a8512","status":"MAINTENANCE"}'
```

## Important Notes

- No database is used — all data is stored in memory 
- Data will reset when the server restarts  
- Cannot delete a room if it contains sensors  
- API returns proper HTTP error codes (400, 403, 409, 422, 500)  

## How to start the Application

Instead of having a main() method.

- Tomcat acts as the server  
- JAX-RS automatically detects your resource classes  
- The application starts when deployed to Tomcat  

When you click Run in NetBeans:

- The project is built  
- It is deployed to Tomcat  
- Tomcat handles all incoming HTTP requests  


# 📘 Coursework Report Answers

---

## Part 1: Service Architecture & Setup

### 1.1 Resource Lifecycle in JAX-RS

By default, JAX-RS resource classes are instantiated using a **per-request lifecycle**. This means that a new instance of the resource class is created for every incoming HTTP request, rather than being treated as a singleton.

This design has important implications for managing in-memory data structures such as `HashMap` or `ConcurrentHashMap`. Since each request gets a new resource instance, instance variables are **not shared across requests**, and therefore cannot be relied upon for persistent storage.

To ensure data consistency across requests, shared data structures must be:

- Declared as `static` (so they persist across instances), or  
- Managed externally (e.g., database or application-scoped components)

However, sharing mutable data across threads introduces concurrency issues such as race conditions. To prevent this:

- Thread-safe collections like `ConcurrentHashMap` should be used  
- Synchronisation mechanisms may be required for compound operations  

This approach ensures that the API remains thread-safe while avoiding data loss in a multi-threaded server environment.

---

### 1.2 Hypermedia (HATEOAS) in REST APIs

Hypermedia, also known as **HATEOAS (Hypermedia as the Engine of Application State)**, is a key principle of advanced RESTful API design. It involves including links and navigational information directly within API responses, allowing clients to dynamically discover available actions.

Instead of relying on static documentation, clients can:

- Follow links provided in responses  
- Navigate between related resources  
- Adapt to API changes without breaking  

#### Example Response

```json
{
  "rooms": "/api/v1/rooms",
  "sensors": "/api/v1/sensors"
}
```

#### Benefits for Client Developers

- Reduces dependency on external documentation  
- Improves API discoverability  
- Enables more flexible and maintainable client applications  
- Supports evolvability of the API without breaking existing clients  

Overall, HATEOAS makes APIs more self-descriptive and resilient to change.

---

## Part 2: Room Management

### 2.1 Returning IDs vs Full Objects

Returning only room IDs instead of full room objects reduces network bandwidth usage, especially when dealing with large datasets. Smaller responses lead to faster transmission and improved performance.

However, this approach shifts additional responsibility to the client, which must:

- Make additional requests to retrieve full details  
- Handle more complex logic  

Returning full room objects:

- Increases payload size  
- Simplifies client-side processing  
- Reduces the number of API calls required  

#### Trade-off

- **IDs only →** efficient but requires more client work  
- **Full objects →** easier for clients but higher bandwidth usage  

In most REST APIs, full objects are preferred for usability unless performance constraints dictate otherwise.

---

### 2.2 Idempotency of DELETE Operation

The DELETE operation is considered **idempotent**, meaning that making the same request multiple times produces the same result.

In this implementation:

- The first DELETE request removes the room successfully  
- Subsequent DELETE requests return **404 Not Found**, since the resource no longer exists  

Despite returning different status codes, the overall system state remains unchanged after the first request, which satisfies idempotency.

Additionally:

- If the room contains sensors, the request is blocked with **409 Conflict**  
- Repeated requests will consistently return the same error  

This predictable behaviour confirms that the DELETE operation is idempotent.

---

## Part 3: Sensor Operations & Linking

### 3.1 Effect of `@Consumes(MediaType.APPLICATION_JSON)`

The `@Consumes(MediaType.APPLICATION_JSON)` annotation specifies that the endpoint only accepts requests with a `Content-Type` of `application/json`.

If a client sends data in a different format, such as:

- `text/plain`  
- `application/xml`  

JAX-RS will automatically:

- Reject the request  
- Return **HTTP 415 Unsupported Media Type**  

This happens because no suitable message body reader is available to convert the incoming data into a Java object.

#### Benefits

- Enforces strict input validation  
- Ensures data consistency  
- Prevents malformed or incompatible requests from being processed  

---

### 3.2 Query Parameters vs Path Parameters for Filtering

Using query parameters (e.g., `/sensors?type=CO2`) is generally preferred over path parameters (e.g., `/sensors/type/CO2`) for filtering collections.

#### Query Parameters

- Represent optional filtering criteria  
- Allow multiple filters (e.g., `/sensors?type=CO2&status=ACTIVE`)  
- Keep the resource path clean  

#### Path Parameters

- Imply hierarchical structure rather than filtering  
- Reduce flexibility  
- Can lead to unnecessary endpoint proliferation  

Therefore, query parameters provide a more scalable and semantically correct approach for filtering.

---

## Part 4: Deep Nesting with Sub-Resources

### 4.1 Benefits of the Sub-Resource Locator Pattern

The **Sub-Resource Locator pattern** improves API design by delegating responsibility to specialised classes rather than handling all logic in a single large resource class.

#### Key Benefits

- **Separation of concerns** – Each class handles a specific responsibility  
- **Improved readability** – Smaller, focused classes are easier to understand  
- **Better maintainability** – Changes are isolated  
- **Scalability** – Easier to extend with nested resources  

Without this pattern, a single controller becomes:

- Large and difficult to manage  
- Prone to bugs  
- Hard to test and maintain  

By splitting functionality (e.g., sensors vs readings), the API remains clean and modular.

---

## Part 5: Error Handling, Exception Mapping & Logging

### 5.1 Why HTTP 422 is More Appropriate than 404

**HTTP 422 Unprocessable Entity** is more accurate than 404 in this scenario because:

- The request is syntactically valid (correct JSON)  
- The endpoint exists  
- The issue is with the **semantic content**  

Specifically:

- The client provides a `roomId` that does not exist  
- This is a validation failure, not a missing endpoint  

#### Comparison

- **404 Not Found →** resource/endpoint does not exist  
- **422 Unprocessable Entity →** request understood but invalid  

#### Benefits of 422

- Clearer client feedback  
- Better distinction of error types  
- Improved API usability  

---

### 5.2 Risks of Exposing Stack Traces (Cybersecurity)

Exposing raw Java stack traces in API responses poses serious security risks.

Attackers can extract:

- Internal class names and package structure  
- File paths and server configuration  
- Frameworks and libraries in use  
- Exact error locations in code  

This can be used to:

- Identify vulnerabilities  
- Perform targeted attacks  
- Reverse-engineer the system  

#### Best Practice

- Return generic error messages  
- Hide internal implementation details  

This significantly reduces the attack surface.

---

### 5.3 Advantages of Using JAX-RS Filters for Logging

Using JAX-RS filters for logging is superior to manual logging inside each resource method.

#### Benefits

- **Separation of concerns** – Logging is independent of business logic  
- **Consistency** – All requests/responses are logged uniformly  
- **Reduced duplication** – No repeated logging code  
- **Ease of maintenance** – Centralised control  

#### Problems with Manual Logging

- Repetitive code  
- Risk of missing logs  
- Harder maintenance  

Filters provide a clean, scalable solution for cross-cutting concerns like logging and monitoring.

---
# 📘 Coursework Report Answers

---

## Part 1: Service Architecture & Setup

### 1.1 Resource Lifecycle in JAX-RS

By default, JAX-RS resource classes are instantiated using a **per-request lifecycle**. This means that a new instance of the resource class is created for every incoming HTTP request, rather than being treated as a singleton.

This design has important implications for managing in-memory data structures such as `HashMap` or `ConcurrentHashMap`. Since each request gets a new resource instance, instance variables are **not shared across requests**, and therefore cannot be relied upon for persistent storage.

To ensure data consistency across requests, shared data structures must be:

- Declared as `static` (so they persist across instances), or  
- Managed externally (e.g., database or application-scoped components)

However, sharing mutable data across threads introduces concurrency issues such as race conditions. To prevent this:

- Thread-safe collections like `ConcurrentHashMap` should be used  
- Synchronisation mechanisms may be required for compound operations  

This approach ensures that the API remains thread-safe while avoiding data loss in a multi-threaded server environment.

---

### 1.2 Hypermedia (HATEOAS) in REST APIs

Hypermedia is a key principle of advanced RESTful API design. It involves including links and navigational information directly within API responses, allowing clients to dynamically discover available actions.

Instead of relying on static documentation, clients can:

- Follow links provided in responses  
- Navigate between related resources  
- Adapt to API changes without breaking  

#### Example Response

```json
{
  "rooms": "/api/v1/rooms",
  "sensors": "/api/v1/sensors"
}
```

#### Benefits for Client Developers

- Reduces dependency on external documentation  
- Improves API discoverability  
- Enables more flexible and maintainable client applications  
- Supports evolvability of the API without breaking existing clients  

Overall, HATEOAS makes APIs more self-descriptive and resilient to change.

---

## Part 2: Room Management

### 2.1 Returning IDs vs Full Objects

Returning only room IDs instead of full room objects reduces network bandwidth usage, especially when dealing with large datasets. Smaller responses lead to faster transmission and improved performance.

However, this approach shifts additional responsibility to the client, which must:

- Make additional requests to retrieve full details  
- Handle more complex logic  

Returning full room objects:

- Increases payload size  
- Simplifies client-side processing  
- Reduces the number of API calls required  

#### Trade-off

- **IDs only →** efficient but requires more client work  
- **Full objects →** easier for clients but higher bandwidth usage  

In most REST APIs, full objects are preferred for usability unless performance constraints dictate otherwise.

---

### 2.2 Idempotency of DELETE Operation

The DELETE operation is considered **idempotent**, meaning that making the same request multiple times produces the same result.

In this implementation:

- The first DELETE request removes the room successfully  
- Subsequent DELETE requests return **404 Not Found**, since the resource no longer exists  

Despite returning different status codes, the overall system state remains unchanged after the first request, which satisfies idempotency.

Additionally:

- If the room contains sensors, the request is blocked with **409 Conflict**  
- Repeated requests will consistently return the same error  

This predictable behaviour confirms that the DELETE operation is idempotent.

---

## Part 3: Sensor Operations & Linking

### 3.1 Effect of `@Consumes(MediaType.APPLICATION_JSON)`

The `@Consumes(MediaType.APPLICATION_JSON)` annotation specifies that the endpoint only accepts requests with a `Content-Type` of `application/json`.

If a client sends data in a different format, such as:

- `text/plain`  
- `application/xml`  

JAX-RS will automatically:

- Reject the request  
- Return **HTTP 415 Unsupported Media Type**  

This happens because no suitable message body reader is available to convert the incoming data into a Java object.

#### Benefits

- Enforces strict input validation  
- Ensures data consistency  
- Prevents malformed or incompatible requests from being processed  

---

### 3.2 Query Parameters vs Path Parameters for Filtering

Using query parameters (e.g., `/sensors?type=CO2`) is generally preferred over path parameters (e.g., `/sensors/type/CO2`) for filtering collections.

#### Query Parameters

- Represent optional filtering criteria  
- Allow multiple filters (e.g., `/sensors?type=CO2&status=ACTIVE`)  
- Keep the resource path clean  

#### Path Parameters

- Imply hierarchical structure rather than filtering  
- Reduce flexibility  
- Can lead to unnecessary endpoint proliferation  

Therefore, query parameters provide a more scalable and semantically correct approach for filtering.

---

## Part 4: Deep Nesting with Sub-Resources

### 4.1 Benefits of the Sub-Resource Locator Pattern

The **Sub-Resource Locator pattern** improves API design by delegating responsibility to specialised classes rather than handling all logic in a single large resource class.

#### Key Benefits

- **Separation of concerns** – Each class handles a specific responsibility  
- **Improved readability** – Smaller, focused classes are easier to understand  
- **Better maintainability** – Changes are isolated  
- **Scalability** – Easier to extend with nested resources  

Without this pattern, a single controller becomes:

- Large and difficult to manage  
- Prone to bugs  
- Hard to test and maintain  

By splitting functionality (e.g., sensors vs readings), the API remains clean and modular.

---

## Part 5: Error Handling, Exception Mapping & Logging

### 5.2 Why HTTP 422 is More Appropriate than 404

**HTTP 422 Unprocessable Entity** is more accurate than 404 in this scenario because:

- The request is syntactically valid (correct JSON)  
- The endpoint exists  
- The issue is with the **semantic content**  

Specifically:

- The client provides a `roomId` that does not exist  
- This is a validation failure, not a missing endpoint  

#### Comparison

- **404 Not Found →** resource/endpoint does not exist  
- **422 Unprocessable Entity →** request understood but invalid  

#### Benefits of 422

- Clearer client feedback  
- Better distinction of error types  
- Improved API usability  

---

### 5.4 Risks of Exposing Stack Traces (Cybersecurity)

Exposing raw Java stack traces in API responses poses serious security risks.

Attackers can extract:

- Internal class names and package structure  
- File paths and server configuration  
- Frameworks and libraries in use  
- Exact error locations in code  

This can be used to:

- Identify vulnerabilities  
- Perform targeted attacks  
- Reverse-engineer the system  

#### Best Practice

- Return generic error messages  
- Hide internal implementation details  

This significantly reduces the attack surface.

---

### 5.5 Advantages of Using JAX-RS Filters for Logging

Using JAX-RS filters for logging is superior to manual logging inside each resource method.

#### Benefits

- **Separation of concerns** – Logging is independent of business logic  
- **Consistency** – All requests/responses are logged uniformly  
- **Reduced duplication** – No repeated logging code  
- **Ease of maintenance** – Centralised control  

#### Problems with Manual Logging

- Repetitive code  
- Risk of missing logs  
- Harder maintenance  

Filters provide a clean, scalable solution for cross-cutting concerns like logging and monitoring.

---

## Author

Tahmid Ahmed  
w2115837@westminster.ac.uk
