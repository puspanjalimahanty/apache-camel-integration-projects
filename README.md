
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
