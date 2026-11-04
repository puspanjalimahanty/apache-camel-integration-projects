
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




# Project 03: Kafka-to-Database Integration Using Apache Camel

## Overview

This project demonstrates a Kafka-based order integration using **Apache Camel and Spring Boot**. Camel consumes order messages from a Kafka topic, converts JSON messages into Java objects, validates order details, prevents duplicate orders, and persists valid orders into an H2 database.

The application includes retry handling, Dead Letter Queue (DLQ) processing, REST APIs for retrieving orders, and application health monitoring.

## Scenario

An e-commerce application receives order messages from upstream systems through a Kafka topic. Apache Camel consumes the messages, unmarshals the JSON payload into a Java object, and validates the order details.

Valid orders are checked for duplicates and saved in the H2 database. If message processing fails, Camel retries the operation and routes messages that continue to fail to a Dead Letter Queue. REST APIs allow users to retrieve all saved orders or search for a specific order by its order ID.

## Integration Flow

```text
Kafka Topic (orders)
   |
   v
Message Consumption
   |
   v
JSON Unmarshalling
   |
   v
Order Validation
   |
   v
Duplicate Order Check
   |
   v
Order Mapping
   |
   v
H2 Database Persistence
   |
   v
Processing Logs
```

**Failure Flow:**

```text
Message Processing Failure
   |
   v
Retry (3 Attempts)
   |
   v
Dead Letter Queue (orders.DLQ)
```

## Technologies Used

- Java 17
- Spring Boot
- Apache Camel 4.22.0
- Apache Kafka
- Spring Data JPA
- H2 Database
- Maven
- REST APIs
- Spring Boot Actuator

## Features

- Kafka message consumption
- JSON-to-Java object conversion
- Order data validation
- Duplicate order prevention
- H2 database persistence
- Retry and exception handling
- Dead Letter Queue (DLQ) processing
- REST APIs to retrieve order records
- Application health monitoring using Actuator

## Sample Input

**Kafka Topic:** `orders`

**Message Body:**

```json
{
  "orderId": "ORD-2001",
  "customerName": "John Smith",
  "productName": "Laptop",
  "quantity": 2,
  "unitPrice": 75000.00,
  "correlationId": "CORR-1001"
}
```

## Expected Outcomes

- Camel consumes valid order messages from the Kafka `orders` topic.
- JSON messages are converted into Java objects and validated.
- Valid orders are saved in the H2 database.
- Duplicate orders are skipped to prevent duplicate database records.
- Invalid or failed messages are retried up to 3 times.
- Messages that continue to fail are routed to the `orders.DLQ` topic.
- `GET /api/orders` retrieves all saved orders.
- `GET /api/orders/{orderId}` retrieves a specific order.
- The Actuator health endpoint reports application status.

## Database Details

- **Database:** H2
- **Table:** `order_record`
- **Database Type:** In-memory

### H2 Console

```text
http://localhost:8081/h2-console
```

Use the JDBC URL configured in `application.properties`.

## Kafka Details

- **Broker:** `localhost:9092`
- **Input Topic:** `orders`
- **Consumer Group:** `order-database-consumer`
- **Dead Letter Topic:** `orders.DLQ`
- **Retry Attempts:** `3`
- **Retry Delay:** `2 seconds`

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

## How to Run

1. Clone the repository.
2. Open the `KafkaDatabaseIntegration` project in your IDE.
3. Ensure Java 17, Maven, and Kafka are configured.
4. Start the Kafka broker.
5. Ensure the required Kafka topics are available.
6. Run the Spring Boot application.
7. Publish an order message to the `orders` topic.
8. Check the application logs and H2 database for processed orders.
9. Test the REST APIs using Postman.

## Application Health

```http
GET /actuator/health
```

Returns the health status of the application.

## Learning Outcomes

This project demonstrates how Apache Camel can build Kafka-to-database integrations with message consumption, JSON unmarshalling, validation, duplicate prevention, database persistence, retry handling, Dead Letter Queue processing, and REST-based data retrieval.




# Project 04: File-to-Database Integration Using Apache Camel

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



# Project 05: SFTP-to-REST Integration Using Apache Camel

## Overview

This project demonstrates an SFTP-based order integration using **Apache Camel and Spring Boot**. Camel periodically polls an SFTP server for order files, validates the records, converts valid order data into JSON, and submits it to a REST API.

The application includes retry handling, duplicate file prevention, correlation ID tracking, and file movement to processed and error directories.

## Scenario

An external partner system uploads order files to an SFTP server. Apache Camel polls the incoming directory, reads and validates each order record, converts the order data into JSON, and submits it to an internal REST API.

Correlation IDs are used to trace each order across SFTP and REST processing. Failed REST requests are retried, duplicate file processing is prevented, and files are moved to the appropriate directories based on the processing result.

## Integration Flow

```text
SFTP Server
   |
   v
Scheduled Polling
   |
   v
File Reading
   |
   v
Correlation ID Generation
   |
   v
Duplicate File Check
   |
   v
Order Validation
   |
   v
JSON Conversion
   |
   v
REST API Submission
   |
   v
REST Response Logging
   |
   v
File Movement
```

**Failure Flow:**

```text
REST Processing Failure
   |
   v
Retry (3 Attempts)
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
- Apache MINA SSHD
- Camel SFTP
- Camel HTTP
- Maven
- REST APIs

## Features

- Embedded SFTP server
- Scheduled polling of SFTP files
- Order file validation
- Order record-to-JSON conversion
- REST API order submission
- Retry and exception handling
- Duplicate file prevention
- Correlation ID generation and propagation
- Processed and error file handling

## Sample Input

**File:** `order-001.txt`

```text
ORD-3001,John Smith,Laptop,1,75000.00
```

## Expected Outcomes

- Camel polls the SFTP incoming directory for order files.
- Order records are read and validated.
- A correlation ID is generated and included in logs and the REST request header.
- Valid order records are converted into JSON.
- The JSON order is submitted to the REST API.
- The REST API returns an order response with status `RECEIVED`.
- Successfully processed files are moved to the processed directory.
- Invalid or failed files are moved to the error directory.
- Failed REST requests are retried up to 3 times.
- Duplicate file names are skipped by the idempotent repository during the application's current runtime.

## SFTP Details

- **Server:** Embedded Apache MINA SSHD
- **Host:** `localhost`
- **Port:** `2222`
- **Username:** `cameluser`
- **Incoming Directory:** `C:\CamelProject\sftp-server\incoming`
- **Success Directory:** `.processed`
- **Failure Directory:** `.error`

## REST API

### Submit Order

```http
POST /api/orders
```

Receives an order submitted by the SFTP integration. 

**Request Header:**

```text
X-Correlation-ID: <generated-correlation-id>
```

**Response Example:**

```json
{
  "message": "Order received successfully",
  "orderId": "ORD-3001",
  "status": "RECEIVED"
}
```

## Reliability Details

- **Retry Attempts:** `3`
- **Retry Delay:** `2 seconds`
- **Duplicate Prevention:** In-memory idempotent repository
- **Correlation Header:** `X-Correlation-ID`

## File Directory Structure

```text
sftp-server/
├── incoming/
├── .processed/
└── .error/
```

- `incoming` — Order files waiting to be processed
- `.processed` — Successfully processed files
- `.error` — Files that failed processing

## How to Run

1. Clone the repository.
2. Open the `SftpRestIntegration` project in your IDE.
3. Ensure Java 17 and Maven are configured.
4. Run the Spring Boot application.
5. Upload an order file to the SFTP `incoming` directory.
6. Check the application logs for validation, correlation ID, and REST response details.
7. Verify the file is moved to the appropriate directory.

## Learning Outcomes

This project demonstrates how
Apache Camel can build SFTP-to-REST integrations with scheduled polling, validation, JSON conversion, retry handling, duplicate prevention, correlation ID tracking, and REST API communication.



# Project 06: Customer Legacy SOAP-to-REST Integration Using Apache Camel

## Overview

This project demonstrates how to integrate a legacy SOAP-based customer management system with modern REST applications using **Spring Boot and Apache Camel**.

The application exposes a REST API that accepts a customer ID, invokes a SOAP service through Apache Camel CXF, processes the SOAP response, and returns customer information as JSON.

The integration includes request validation, customer-not-found handling, retry and error handling, correlation ID logging, and application health monitoring.

## Scenario

A company uses a legacy customer management system that provides customer information through a SOAP web service. Modern applications, such as web portals and mobile applications, consume customer information through REST APIs that return JSON.

Apache Camel acts as the integration layer between these systems.

The application receives REST requests, validates the customer ID, invokes the SOAP service, transforms the SOAP response into a Java object, and returns the customer details as JSON.

## Integration Flow

### Successful Flow

```text
REST Client
    |
    | GET /api/customers/{customerId}
    v
Camel REST Endpoint
    |
    v
Customer Request Validation
    |
    v
SOAP Request Preparation
    |
    v
Camel CXF SOAP Invocation
    |
    v
Legacy Customer SOAP Service
    |
    v
SOAP Response Processing
    |
    v
Customer Java Object Mapping
    |
    v
JSON Marshalling
    |
    v
REST JSON Response
```

### Failure Flow

```text
SOAP Service Failure
        |
        v
Retry (3 Attempts)
        |
        v
Retry Delay (2 Seconds)
        |
        v
Retries Exhausted
        |
        v
HTTP 502 Bad Gateway
        |
        v
JSON Error Response
```

## Technology Stack

| Component | Technology |
|---|---|
| Programming Language | Java 17 |
| Application Framework | Spring Boot 4.1.2 |
| Integration Framework | Apache Camel 4.22.0 |
| REST Integration | Camel Platform HTTP |
| SOAP Integration | Apache Camel CXF |
| SOAP Contract | WSDL |
| JSON Transformation | Jackson |
| Monitoring | Spring Boot Actuator |
| Build Tool | Maven |
| API Testing | Postman |

## Project Structure

```text
SoapRestIntegration
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com.example.soaprest
│   │   │       ├── config
│   │   │       │   └── SoapConfig.java
│   │   │       ├── model
│   │   │       │   └── Customer.java
│   │   │       ├── route
│   │   │       │   ├── CustomerRestRoute.java
│   │   │       │   ├── CustomerSoapRoute.java
│   │   │       │   └── CustomerValidationRoute.java
│   │   │       ├── soap
│   │   │       │   ├── CustomerSoapService.java
│   │   │       │   ├── CustomerSoapServiceImpl.java
│   │   │       │   └── CustomerSoapResponse.java
│   │   │       └── SoapRestIntegrationApplication.java
│   │   └── resources
│   │       └── application.properties
│   └── test
│       └── java
├── pom.xml
└── README.md
```

## SOAP Service Details

| Property | Value |
|---|---|
| SOAP Service | `CustomerSoapService` |
| SOAP Operation | `getCustomer` |
| SOAP Endpoint | `http://localhost:8086/services/customer` |
| WSDL URL | `http://localhost:8086/services/customer?wsdl` |
| SOAP Component | Apache CXF |
| Data Format | POJO |

The SOAP service provides sample customer information for the following customer IDs:

| Customer ID | Customer Name | Email |
|---|---|---|
| `CUST-101` | John Smith | john@example.com |
| `CUST-102` | Alice Johnson | alice@example.com |

## REST API Details

### Get Customer by ID

**Endpoint**

```http
GET /api/customers/{customerId}
```

**Example**

```http
GET http://localhost:8086/api/customers/CUST-101
```

**Successful Response — HTTP 200 OK**

```json
{
  "customerId": "CUST-101",
  "customerName": "John Smith",
  "email": "john@example.com"
}
```

### Customer Not Found

**Example**

```http
GET http://localhost:8086/api/customers/CUST-999
```

**Response — HTTP 404 Not Found**

```json
{
  "error": "Customer not found",
  "customerId": "CUST-999"
}
```

### Invalid Customer ID

**Example**

```http
GET http://localhost:8086/api/customers/@@@
```

**Response — HTTP 400 Bad Request**

```json
{
  "error": "Invalid customer ID",
  "message": "Customer ID must contain 3–20 letters, numbers, or hyphens"
}
```

### SOAP Service Unavailable

If the SOAP service fails after the configured retry attempts, the integration returns:

**Response — HTTP 502 Bad Gateway**

```json
{
  "error": "Customer service is temporarily unavailable"
}
```

## Request and Response Transformation

The integration performs the following transformations:

1. Receives the customer ID from the REST URL.
2. Validates the customer ID.
3. Prepares the SOAP operation parameters.
4. Invokes the SOAP service through Camel CXF.
5. Extracts the SOAP operation response from the CXF response.
6. Maps the SOAP response into a Java `Customer` object.
7. Uses Jackson to serialize the Java object into JSON.
8. Returns the JSON response to the REST client.

## Reliability and Error Handling

The application includes the following reliability features:

- **Request Validation:** Rejects malformed customer IDs before invoking the SOAP service.
- **Customer Not Found Handling:** Returns HTTP `404 Not Found` for unknown customers.
- **Retry Mechanism:** Retries failed SOAP calls up to 3 times.
- **Retry Delay:** Waits 2 seconds between retry attempts.
- **Controlled Error Response:** Returns HTTP `502 Bad Gateway` if the SOAP service remains unavailable.
- **Correlation ID Logging:** Uses the `X-Correlation-ID` header to trace requests through the integration flow.

## Health Monitoring

Spring Boot Actuator is configured to expose application health information.

**Health Endpoint**

```http
GET http://localhost:8086/actuator/health
```

**Expected Response**

```json
{
  "status": "UP"
}
```

## Configuration

The application uses the following configuration in `application.properties`:

```properties
server.port=8086

customer.soap.endpoint=http://localhost:8086/services/customer

cxf.path=/services

management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=always
```

## How to Run

### Prerequisites

- Java 17 or later
- Maven
- Eclipse, IntelliJ IDEA, or another Java IDE
- Postman

### Steps

1. Clone or download the repository.
2. Import the project as a Maven project.
3. Ensure the configured Java version is available.
4. Update Maven dependencies.
5. Run `SoapRestIntegrationApplication`.
6. Confirm that the application starts on port `8086`.
7. Open the WSDL URL to verify the SOAP service.
8. Use Postman to test the REST API.

### Verify the SOAP WSDL

Open:

```text
http://localhost:8086/services/customer?wsdl
```

### Test the REST API

Send:

```http
GET http://localhost:8086/api/customers/CUST-101
```

The application should return the customer details as JSON.

## Expected Outcomes

- REST clients can retrieve customer information from a SOAP-based legacy system.
- SOAP requests are invoked through Apache Camel CXF.
- SOAP responses are transformed into JSON.
- Invalid requests are rejected before reaching the SOAP service.
- Unknown customers receive an appropriate `404` response.
- Failed SOAP calls are retried before returning a controlled error.
- Correlation IDs support request tracing.
- Actuator provides application health information.

## Project Outcome

This project demonstrates how Apache Camel can bridge legacy SOAP services and modern REST applications.

It covers REST-to-SOAP communication, request and response transformation, validation, error handling, retry mechanisms, correlation ID logging, and health monitoring.

The integration enables modern applications to consume customer information through REST APIs without directly interacting with the legacy SOAP system.
