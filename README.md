
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

### 1. Clone the Repository (NetBeans Instructions)

You can clone this repository directly using Apache NetBeans.

#### Option 1: Clone inside NetBeans (Recommended)
1. Open **Apache NetBeans**
2. Go to **Team → Git → Clone...**
3. In the **Repository URL** field, enter:

```bash
https://github.com/t5115/-Smart-Campus-Sensor-Room-Management-API.git
```
4. Click **Next**
5. Choose a local directory where the project will be stored
6. Click **Finish**
7. When prompted, click **Yes** to open the project

#### Option 2: Open the Project Manually
If the project does not open automatically:
1. Go to **File → Open Project**
2. Navigate to the cloned folder: -Smart-Campus-Sensor-Room-Management-API
3. Select the folder and click **Open Project**

#### Option 3: Clone Using Terminal (Optional)
If you prefer using Git commands:

```bash
git clone https://github.com/t5115/-Smart-Campus-Sensor-Room-Management-API.git
cd -Smart-Campus-Sensor-Room-Management-API
```

### 4. Set Up Apache Tomcat

If you don’t already have Tomcat:

- Download Apache Tomcat (version 9 or later)  
- Extract it to a known location  

Add Tomcat to NetBeans:

- Go to Services tab  
- Right-click Servers → Add Server  
- Select Apache Tomcat  
- Browse to your Tomcat installation folder  
- Finish setup  

### 5. Configure Project to Use Tomcat

- Right-click your project → Properties  
- Go to Run  
- Set:
  - Server: Apache Tomcat  
  - Context Path: /sensor-api (or leave default)  

### 6. Build the Project

- Right-click project → Clean and Build  

### 7. Run the Project

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

---
# 📘 Coursework Report Answers

---

## Part 1: Service Architecture & Setup

### 1.1 Resource Lifecycle in JAX-RS

By default, JAX-RS resource classes are instantiated using a **per-request lifecycle**. This means that a new instance of the resource class is created for every incoming HTTP request, rather than being treated as a singleton.

This design has important implications for managing in-memory data structures such as `HashMap` or `ConcurrentHashMap`.

Since each request gets a new resource instance:
- Instance variables are **not shared across requests**
- They **cannot be relied upon for persistent storage**

To ensure data consistency across requests, shared data structures must be:
- Declared as `static` (so they persist across instances), or  
- Managed externally (e.g., database or application-scoped components)

However, sharing mutable data across threads introduces concurrency issues such as race conditions. To prevent this:
- Use thread-safe collections like `ConcurrentHashMap`
- Apply synchronisation for compound operations where needed

This ensures the API remains thread-safe while avoiding data loss in a multi-threaded server environment.

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
- IDs only → efficient but requires more client work  
- Full objects → easier for clients but higher bandwidth usage  

In most REST APIs, full objects are preferred for usability unless performance constraints dictate otherwise.

---

### 2.2 Idempotency of DELETE Operation

The DELETE operation is considered idempotent, meaning that making the same request multiple times produces the same result.

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

### 3.1 Effect of @Consumes(MediaType.APPLICATION_JSON)

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

In this implementation, the `/sensors` endpoint supports optional filtering via query parameters, allowing the same endpoint to serve both filtered and unfiltered results dynamically.

#### Query Parameters
- Represent optional filtering criteria  
- Allow multiple filters (e.g., `/sensors?type=CO2&status=ACTIVE`)  
- Keep the resource path clean  
- Enable composability without increasing endpoint complexity  

#### Path Parameters
- Imply hierarchical structure rather than filtering  
- Reduce flexibility  
- Can lead to unnecessary endpoint proliferation  

By using query parameters, the API avoids rigid endpoint structures and supports scalable, extensible filtering aligned with REST best practices.

---

## Part 4: Deep Nesting with Sub-Resources

### 4.1 Benefits of the Sub-Resource Locator Pattern

The Sub-Resource Locator pattern improves API design by delegating responsibility to specialised classes rather than handling all logic in a single large resource class.

In this implementation, requests to `/sensors/{sensorId}/readings` are delegated to a dedicated `SensorReadingResource` class.

#### Key Benefits
- Separation of concerns  
- Improved readability  
- Better maintainability  
- Scalability  

This prevents “god classes” and improves testability.

---

### 4.2 Maintaining Consistency Between Readings and Sensors

The `SensorReadingResource` supports:
- `GET` – retrieving historical readings  
- `POST` – creating new readings  

When a new reading is created, the parent sensor’s `currentValue` is updated immediately.

This ensures:
- Consistency between historical data and current state  
- Easy access to latest values  
- No stale data  

---

### 5.2 Why HTTP 422 is More Appropriate than 404

HTTP 422 Unprocessable Entity is more accurate because:
- The request is syntactically valid  
- The endpoint exists  
- The issue is semantic  

#### Comparison
- 404 Not Found → endpoint/resource does not exist  
- 422 Unprocessable Entity → request understood but invalid  

#### Benefits
- Clearer client feedback  
- Better distinction of error types  
- Improved API usability  

  
---

### 5.4 Risks of Exposing Stack Traces (Cybersecurity)

Exposing stack traces can reveal:
- Internal class names  
- File paths  
- Frameworks used  
- Exact error locations  

This can enable targeted attacks.

#### Best Practice
- Return generic error messages  
- Hide internal implementation details  

---

### 5.5 Advantages of Using JAX-RS Filters for Logging

Using filters is better than manual logging.

#### Benefits
- Separation of concerns  
- Consistent logging  
- Reduced duplication  
- Easier maintenance  

Filters operate at the framework level, making them ideal for cross-cutting concerns like logging, authentication, and auditing.

## Author

Tahmid Ahmed  
w2115837@westminster.ac.uk
