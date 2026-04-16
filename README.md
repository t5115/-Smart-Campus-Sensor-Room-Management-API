
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

## Author

Tahmid Ahmed  
w2115837@westminster.ac.uk
