
# Apache Camel Integration Projects

## Project 01: REST to Database Integration

### Scenario

An e-commerce application receives customer orders through REST APIs. Apache Camel processes incoming JSON requests, validates order details, prevents duplicate orders, and stores valid orders in an H2 database using Spring Data JPA.

The application also provides APIs to retrieve all orders and fetch individual orders by ID.

### Technology Stack

- Java 17
- Spring Boot
- Apache Camel 4.22.0
- Spring Data JPA
- H2 Database
- Maven
- Postman

### Integration Flow

REST API → JSON Request → Validation → Order Processing → Duplicate Check → H2 Database → JSON Response

### API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/orders` | Create a new order |
| GET | `/api/orders` | Retrieve all orders |
| GET | `/api/orders/{id}` | Retrieve an order by ID |

**Base URL:** `http://localhost:8081`

### Sample Input

**POST** `/api/orders`

```json
{
  "orderNumber": "ORD-2001",
  "customerName": "Vandita",
  "productName": "Keyboard",
  "quantity": 2,
  "unitPrice": 750.00
}
```

### Expected Output

**1. Create Order**

HTTP Status: `201 Created`

```json
{
  "id": 1,
  "orderNumber": "ORD-2001",
  "customerName": "Vandita",
  "productName": "Keyboard",
  "quantity": 2,
  "unitPrice": 750.00,
  "createdAt": "2026-10-29T07:50:56"
}
```

**2. Retrieve All Orders**

GET `/api/orders`

HTTP Status: `200 OK`

Returns all stored orders as a JSON array.

**3. Retrieve Single Order**

GET `/api/orders/1`

HTTP Status: `200 OK`

Returns the matching order details as a JSON object.

### Exception Handling

- Duplicate order: `409 Conflict`
- Invalid request: `400 Bad Request`
- Order not found: `404 Not Found`
- Unexpected error: `500 Internal Server Error`

### Database Details

- Database: H2 In-Memory Database
- Table: `orders`
- Repository: `OrderRepository`
- Service: `OrderService`

**H2 Console:** `http://localhost:8081/h2-console`

**JDBC URL:** `jdbc:h2:mem:orderdb;DB_CLOSE_DELAY=-1`

### Project Output

Orders are successfully created, validated, stored in H2, and retrieved through REST APIs.



# Project 02: REST to Kafka Order Integration Using Apache Camel

### Scenario

An e-commerce application receives customer orders through a REST API. Apache Camel validates the incoming order, checks for duplicates, stores the order in an H2 database, transforms it into JSON, and publishes it to an Apache Kafka topic. The application tracks the publishing status of each order.

### Technology Stack

- Java 17
- Spring Boot 4.1.1
- Apache Camel 4.22.0
- Apache Kafka 4.3.1
- H2 Database
- Spring Data JPA
- Maven
- Postman

### Integration Flow

REST API → Request Validation → Correlation ID Generation → Duplicate Check → H2 Database (PENDING) → JSON Transformation → Kafka Publishing → Status Update (PUBLISHED / FAILED)

### API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/orders` | Submit a new order |
| GET | `/api/orders/{orderId}/status` | Retrieve order processing status |

**Base URL:** `http://localhost:8082`

### Sample Input

**POST:** `http://localhost:8082/api/orders`

```json
{
  "orderId": "ORD-1001",
  "customerName": "Rahul",
  "productName": "Keyboard",
  "quantity": 2,
  "unitPrice": 750.00
}
```

### Expected Output

**Successful Response – HTTP 202 Accepted**

```json
{
  "message": "Order accepted for processing",
  "orderId": "ORD-1001",
  "correlationId": "<generated-correlation-id>",
  "status": "PUBLISHED"
}
```

**Order Status – GET**

`GET http://localhost:8082/api/orders/ORD-1001/status`

Returns the order ID, correlation ID, current status, timestamps, and error details if available.


### Database Details

- Database: H2
- Table: `order_records`
- Storage: File-based H2 database
- Initial status: `PENDING`
- Successful publishing: `PUBLISHED`
- Failed publishing: `FAILED`

**H2 Console:** `http://localhost:8082/h2-console`

### Kafka Details

- Broker: `localhost:9092`
- Topic: `orders`
- Partitions: 3
- Replication Factor: 1
- Message Format: JSON


# 3.File-to-Database Integration Using Apache Camel

## Overview

This project demonstrates a file-based order integration using **Apache Camel and Spring Boot**. Camel periodically polls an incoming directory for CSV order files, validates the records, prevents duplicate orders, and persists valid orders into an H2 database.

The application also provides REST APIs to retrieve stored orders and includes error handling, file movement, and application health monitoring.

## Scenario

An e-commerce system receives order data from external systems in CSV format. Apache Camel reads the incoming files, converts each record into a Java object, validates the order details, and saves valid orders into the database.

Invalid records are handled appropriately, while processed files are moved to the corresponding directories.

## Integration Flow

```text
CSV File
   |
   v
Scheduled File Polling
   |
   v
CSV Parsing
   |
   v
Order Validation
   |
   v
Duplicate Order Check
   |
   v
H2 Database Persistence
   |
   v
Processing Logs
```

**Failure Flow:**

```text
Invalid or Failed Record
   |
   v
Error Handling
   |
   v
Error Directory
```

## Technologies Used

- Java 17
- Spring Boot
- Apache Camel 4.22.0
- Spring Data JPA
- H2 Database
- Maven
- REST APIs
- Spring Boot Actuator

## Features

- Scheduled polling of CSV files
- CSV-to-Java object conversion
- Order data validation
- Duplicate order prevention
- H2 database persistence
- Processed and error file handling
- REST APIs to retrieve order records
- Application health monitoring using Actuator

## Sample Input

**File:** `orders.csv`

```csv
orderId,customerName,productCode,quantity,unitPrice
ORD-1001,John Smith,LAPTOP,2,75000.00
ORD-1002,Emma Watson,MOBILE,1,45000.00
```

## Expected Outcomes

- Camel polls the incoming directory for CSV files.
- CSV records are converted into Java objects.
- Order details are validated before persistence.
- Valid orders are saved in the H2 database.
- Duplicate orders are skipped.
- Successfully processed files are moved to the processed directory.
- Invalid or failed files are handled through the error directory.
- REST APIs allow users to retrieve stored order records.
- The Actuator health endpoint reports application status.

## Database Details

- **Database:** H2
- **Table:** `order_records`
- **Database Type:** In-memory

### H2 Console

```text
http://localhost:8084/h2-console
```

Use the JDBC URL configured in `application.properties`.

## REST API

### Retrieve All Orders

```http
GET /api/orders
```

Returns all orders stored in the database.

### Retrieve Order by Order ID

```http
GET /api/orders/{orderId}
```

Returns the order matching the specified order ID.

## File Directory Structure

```text
data/
└── orders/
    ├── incoming/
    ├── processed/
    └── error/
```

- `incoming` — CSV files waiting to be processed
- `processed` — Successfully processed files
- `error` — Files that failed processing

## How to Run

1. Clone the repository.
2. Open the `FileDatabaseIntegration` project in your IDE.
3. Ensure Java 17 and Maven are configured.
4. Run the Spring Boot application.
5. Place a CSV order file in the configured `incoming` directory.
6. Check the application logs and H2 database for processed orders.
7. Test the REST APIs using Postman.

## Application Health

```http
GET /actuator/health
```

Returns the health status of the application.

