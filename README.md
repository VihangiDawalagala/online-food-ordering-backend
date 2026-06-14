# Online Food Ordering Backend

Backend API for an Online Food Ordering System built with Spring Boot, Spring Security JWT, Spring Data JPA, and MySQL.

## Features

- User registration and login with JWT authentication
- Role-based authorization for `ADMIN` and `CUSTOMER`
- Food item management with availability status
- Category management
- Customer cart operations
- Order placement and order status updates
- Payment processing
- Global exception handling
- Service-layer logging for important business actions

## Tech Stack

- Java 21
- Spring Boot 3.5.3
- Spring Web
- Spring Data JPA
- Spring Security
- MySQL
- Lombok
- JWT using `jjwt`
- Maven

## Project Structure

```text
src/main/java/com/example/foodorder
├── config
├── controller
├── dto
├── entity
├── exception
├── repository
├── security
└── service
```

## Database Setup

Create the MySQL database before running the application:

```sql
CREATE DATABASE food_ordering_db;
```

Update database credentials in:

```text
src/main/resources/application.properties
```

Current default configuration:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/food_ordering_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=Admin1234
spring.jpa.hibernate.ddl-auto=update
```

## Run The Backend

```powershell
.\mvnw.cmd spring-boot:run
```

Backend URL:

```text
http://localhost:8080
```

## Run Tests

```powershell
.\mvnw.cmd clean test
```

## Authentication

### Sign Up

```http
POST /api/auth/signup
```

```json
{
  "name": "Customer One",
  "email": "customer@example.com",
  "password": "password123"
}
```

Sign up always creates a `CUSTOMER` account. Admin users should be created manually in the database or through a protected admin process.

### Sign In

```http
POST /api/auth/signin
```

```json
{
  "email": "customer@example.com",
  "password": "password123"
}
```

Use the returned token in protected requests:

```http
Authorization: Bearer <token>
```

## Main API Endpoints

### Public

```http
POST /api/auth/signup
POST /api/auth/signin
GET  /api/foods
GET  /api/foods/{id}
GET  /api/categories
GET  /images/{fileName}
```

### Admin

```http
POST   /api/foods
PUT    /api/foods/{id}
DELETE /api/foods/{id}

POST   /api/categories
PUT    /api/categories/{id}
DELETE /api/categories/{id}

GET    /api/users
GET    /api/users/{id}
GET    /api/orders
PUT    /api/orders/{id}/status
```

### Customer Or Admin

```http
GET    /api/cart/{userId}
POST   /api/cart/add
DELETE /api/cart/remove/{cartItemId}

POST   /api/orders/place
GET    /api/orders/{userId}

POST   /api/payments/process
```

## Example Food Request

```json
{
  "name": "Margherita Pizza",
  "description": "Classic pizza with tomato, mozzarella, and fresh basil.",
  "price": 1850,
  "imageUrl": "pizza.jpg",
  "status": "AVAILABLE",
  "category": {
    "id": 1
  }
}
```

## Example Cart Request

```json
{
  "userId": 1,
  "foodId": 1,
  "quantity": 2
}
```

## Example Order Request

```json
{
  "userId": 1
}
```

## CORS

The backend allows frontend development servers:

```text
http://localhost:5173
http://localhost:5174
```

## Submission

GitHub repository link:

```text
Add your GitHub repository URL here after pushing the project.
```
