# 🚀 Rocket Food Delivery REST API
## Module 12: Test-Driven Development (TDD)

A production-ready Java Spring Boot REST API for a food delivery marketplace that connects restaurants, customers, and couriers. Built with **Test-Driven Development (TDD)** principles, featuring 298 comprehensive automated tests with 100% pass rate and complete API endpoint coverage.

---

## 📑 Table of Contents

- [📋 Project Overview](#-project-overview)
- [🛠️ Technology Stack](#️-technology-stack)
- [📁 Project Structure](#-project-structure)
- [⚙️ Installation & Setup](#️-installation--setup)
- [🧪 Testing & TDD](#-testing--tdd)
- [🌐 API Documentation](#-api-documentation)
- [🗄️ Database Schema](#️-database-schema)
- [🔐 Authentication](#-authentication)
- [📦 Deliverables](#-deliverables)
- [👨‍💻 Development Notes](#-development-notes)

## � Project Overview

**Rocket Food Delivery** is a comprehensive backend solution for managing food delivery operations. The system enables:

- 🍽️ **Restaurant Management** — Create, read, update, and delete restaurants with filtering by rating and price range
- 📦 **Order Management** — Full order lifecycle from creation to delivery status updates
- 🛒 **Product Management** — Restaurant product catalog with inventory management
- 🚚 **Courier Operations** — Delivery assignment and status tracking
- 🔐 **Authentication** — Secure JWT-based API access with role-based authorization
- 📊 **Order Tracking** — Real-time order status updates and history

### ✅ Module 12 Completion Status

This implementation successfully completes **Module 12: Test-Driven Development** with:
- ✅ **298 passing tests** (100% pass rate)
- ✅ **8 verified API endpoints** (Module 12 specification compliance)
- ✅ **12+ native SQL queries** (parameterized for security)
- ✅ **5+ service classes** (business logic layer)
- ✅ **5 REST controllers** (HTTP request handling)
- ✅ **20+ DTOs** (request/response models)
- ✅ **Complete TDD workflow** (tests written before implementation)

---

## 🛠️ Technology Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| **Language** | Java | 17 |
| **Framework** | Spring Boot | 3.5.13 |
| **Build Tool** | Maven | 3.9+ |
| **Database** | MySQL | 8.0+ |
| **ORM** | JPA/Hibernate | Spring Boot 3.5.13 |
| **Security** | Spring Security + JWT | 6.1+ |
| **Testing Framework** | JUnit 5 | 5.9+ |
| **Mocking** | Mockito | 5.0+ |
| **Testing Utilities** | MockMvc, @SpringBootTest | Spring Boot 3.5.13 |
| **Code Generation** | Lombok | 1.18+ |
| **Validation** | Jakarta Validation | 3.0+ |



## 📁 Project Structure

```
rdelivery-template-m12/
├── src/
│   ├── main/java/com/rocketFoodDelivery/rocketFood/
│   │   ├── config/
│   │   │   └── SecurityConfig.java           # Spring Security & JWT configuration
│   │   ├── controller/
│   │   │   ├── GlobalExceptionHandler.java   # Centralized exception handling
│   │   │   └── api/
│   │   │       ├── AuthApiController.java    # Authentication (POST /api/auth)
│   │   │       ├── RestaurantApiController.java # Restaurants (GET, POST, PUT, DELETE)
│   │   │       ├── OrdersApiController.java  # Orders (GET, POST, DELETE, status)
│   │   │       ├── ProductsApiController.java # Products (GET, DELETE)
│   │   │       └── AddressApiController.java # Addresses (POST)
│   │   ├── dtos/                             # Data Transfer Objects
│   │   │   ├── Auth*.java                    # Authentication DTOs
│   │   │   ├── ApiRestaurant*.java           # Restaurant DTOs
│   │   │   ├── ApiOrder*.java                # Order DTOs
│   │   │   ├── ApiProduct*.java              # Product DTOs
│   │   │   └── *ResponseDTO.java             # Response wrappers
│   │   ├── models/                           # JPA Entity models
│   │   │   ├── UserEntity.java               # Users (customer, courier, employee)
│   │   │   ├── Restaurant.java               # Restaurant entity
│   │   │   ├── Order.java                    # Order entity
│   │   │   ├── Product.java                  # Product entity
│   │   │   ├── Address.java                  # Address entity
│   │   │   └── ProductOrder.java             # Order-Product junction table
│   │   ├── repository/                       # Data Access Layer (JPA)
│   │   │   ├── OrderRepository.java          # Order CRUD + native queries
│   │   │   ├── RestaurantRepository.java     # Restaurant CRUD + native queries
│   │   │   ├── ProductRepository.java        # Product CRUD + native queries
│   │   │   └── ...                           # Other repository classes
│   │   ├── service/                          # Business Logic Layer
│   │   │   ├── OrderService.java             # Order operations & mapping
│   │   │   ├── RestaurantService.java        # Restaurant operations & filtering
│   │   │   ├── ProductService.java           # Product operations
│   │   │   ├── AuthService.java              # Authentication & JWT
│   │   │   └── ...                           # Other service classes
│   │   ├── security/                         # Security & JWT
│   │   │   ├── JwtTokenFilter.java           # JWT validation filter
│   │   │   ├── JwtUtil.java                  # JWT token operations
│   │   │   └── SecurityConfig.java           # Spring Security configuration
│   │   ├── util/                             # Utility classes
│   │   │   ├── ResponseBuilder.java          # API response envelope
│   │   │   └── ValidationUtil.java           # Input validation helpers
│   │   ├── exception/                        # Custom exceptions
│   │   │   ├── ResourceNotFoundException.java
│   │   │   ├── BadRequestException.java
│   │   │   └── UnauthorizedException.java
│   │   ├── DataSeeder.java                   # Test data population
│   │   └── RocketFoodApplication.java        # Spring Boot entry point
│   ├── resources/
│   │   ├── application.properties            # Database & server config
│   │   └── application-test.properties       # Test-specific config
│   └── test/java/com/rocketFoodDelivery/rocketFood/
│       ├── controller/api/                   # Controller integration tests (5 classes, 175 tests)
│       └── api/                              # API & mock service tests (5 classes, 123 tests)
├── ai/                                       # AI Specification documents
│   ├── ai-spec.md                            # Overall project specification
│   └── features/                             # Feature specifications
├── pom.xml                                   # Maven project configuration
├── mvnw / mvnw.cmd                          # Maven Wrapper (Linux/Windows)
├── Rocket_Food_Delivery_Module12.postman_collection.json # API testing
└── README.md                                 # This file
```

---

## ⚙️ Installation & Setup

### Prerequisites

Ensure you have the following installed:

```bash
# Check Java version (must be 17+)
java -version

# Check Maven version (must be 3.8+)
mvn -version

# Check MySQL version (must be 8.0+)
mysql --version
```

**Required Software:**
- Java 17 or higher
- Maven 3.8.1 or higher
- MySQL Server 8.0 or higher
- Git

### Step 1: Clone the Repository

```bash
git clone https://github.com/charleswinfield108/RocketDelivery2.git
cd rdelivery-template-m12
git checkout main  # or dev branch
```

### Step 2: Create MySQL Database

```bash
# Connect to MySQL
mysql -u root -p

# Create the database
CREATE DATABASE rocket_food_delivery;

# Exit MySQL
EXIT;
```

### Step 3: Configure Database Connection

Edit `src/main/resources/application.properties`:

```properties
# MySQL Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/rocket_food_delivery
spring.datasource.username=root
spring.datasource.password=your_password_here
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.show-sql=false

# Server Configuration
server.port=8080
server.servlet.context-path=/

# JWT Configuration
jwt.secret=your_jwt_secret_key_here_minimum_256_bits
jwt.expiration=3600000  # 1 hour in milliseconds
```

### Step 4: Build the Project

**On Linux/Mac:**
```bash
./mvnw clean install
```

**On Windows:**
```bash
mvnw.cmd clean install
```

Output should show: `BUILD SUCCESS`

### Step 5: Run the Application

**On Linux/Mac:**
```bash
./mvnw spring-boot:run
```

**On Windows:**
```bash
mvnw.cmd spring-boot:run
```

The application will start on `http://localhost:8080`

Verify with:
```bash
curl http://localhost:8080/api/restaurants
```

---

## 🧪 Testing & TDD

### Running Tests

**Run All Tests:**
```bash
./mvnw clean test
```

**Run Specific Test Class:**
```bash
./mvnw clean test -Dtest=AuthApiControllerTest
```

**Run Specific Test Method:**
```bash
./mvnw clean test -Dtest=AuthApiControllerTest#testAuthenticateWithValidCredentials_ShouldReturn200
```

### Test Statistics

| Component | Test Count | Pass Rate | Status |
|-----------|-----------|-----------|--------|
| AuthApiControllerTest | 29 | 100% | ✅ |
| RestaurantApiControllerTest (api/) | 57 | 100% | ✅ |
| RestaurantApiControllerTest (controller/api/) | 20 | 100% | ✅ |
| RestaurantGetDeleteTest | 32 | 100% | ✅ |
| OrdersApiControllerTest | 45 | 100% | ✅ |
| OrderApiControllerTest | 27 | 100% | ✅ |
| OrderStatusUpdateTest | 23 | 100% | ✅ |
| ProductsApiControllerTest | 24 | 100% | ✅ |
| ProductsGetTest | 24 | 100% | ✅ |
| AddressControllerTest | 17 | 100% | ✅ |
| **TOTAL** | **298** | **100%** | **✅** |

### TDD Workflow

Tests were written **before** implementation following TDD principles:

1. **Red Phase** — Write failing test
2. **Green Phase** — Write minimal code to pass test
3. **Refactor Phase** — Improve code while keeping tests green

---

## 🌐 API Documentation

### Base URL

```
http://localhost:8080
```

### Authentication

All endpoints (except POST /api/auth) require JWT authentication:

```
Authorization: Bearer <your_jwt_token>
```

### Response Format (Success)

```json
{
  "message": "Success",
  "data": { }
}
```

### Response Format (Error)

```json
{
  "error": "Error message",
  "details": "Additional details or null"
}
```

### 🔐 Authentication Endpoints

#### POST /api/auth — Login

**Request Body:**
```json
{
  "email": "admin@example.com",
  "password": "admin123"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### 🍽️ Restaurant Endpoints

- `GET /api/restaurants` — List all restaurants (with optional rating and price_range filters)
- `GET /api/restaurants/{id}` — Get restaurant by ID
- `POST /api/restaurants` — Create new restaurant
- `PUT /api/restaurants/{id}` — Update restaurant
- `DELETE /api/restaurants/{id}` — Delete restaurant with cascade

### 📦 Order Endpoints

- `GET /api/orders?type={type}&id={id}` — List orders (filter by customer/restaurant/courier)
- `POST /api/orders` — Create new order
- `DELETE /api/order/{id}` — Delete order
- `POST /api/order/{id}/status` — Update order status

### 🛒 Product Endpoints

- `GET /api/products?restaurant={id}` — List products for restaurant
- `DELETE /api/products?restaurant={id}` — Delete all products for restaurant

### 📍 Address Endpoints

- `POST /api/address` — Create new address

---

## 🗄️ Database Schema

The database includes tables for:
- **users** — User accounts (customers, couriers, employees)
- **restaurants** — Restaurant information
- **products** — Menu items
- **orders** — Customer orders
- **product_orders** — Order line items (junction table)
- **addresses** — Delivery and restaurant addresses
- **order_statuses** — Order status definitions

---

## 🔐 Authentication & Security

- ✅ **JWT Tokens** — Stateless authentication with 1-hour expiration
- ✅ **Role-Based Access** — ROLE_USER and ROLE_EMPLOYEE roles
- ✅ **Password Hashing** — bcrypt password encoding
- ✅ **SQL Injection Prevention** — Fully parameterized queries
- ✅ **Spring Security Integration** — Complete security filter chain

---

## 📦 Deliverables

### ✅ Completed

- [x] **REST API** — 5 controllers with 8+ endpoints
- [x] **Test Suite** — 298 tests with 100% pass rate
- [x] **Native SQL Queries** — 12+ parameterized queries
- [x] **Service Layer** — Complete business logic
- [x] **DTOs** — 20+ request/response models
- [x] **Module 12 API Compliance** — Verified against specification
- [x] **Comprehensive README** — This file
- [x] **Postman Collection** — All endpoints documented

---

## 👨‍💻 Development Notes

### Key Implementation Decisions

1. **Layered Architecture** — Controller → Service → Repository → Database
2. **Native SQL Queries** — Used for complex operations with parameterized bindings
3. **DTO Pattern** — Separate request/response models prevent data exposure
4. **TDD Methodology** — Tests written first ensuring 100% coverage
5. **Global Exception Handling** — Consistent error responses across API

### Naming Conventions

- **Database** (snake_case) — `restaurant_name`, `price_range`
- **Java** (camelCase) — `restaurantId`, `priceRange`
- **JSON** (snake_case) — `"restaurant_id": 1`
- **DTOs** — `ApiRestaurantDTO` (response), `ApiCreateRestaurantDTO` (request)

---

## ✨ Module 12 Completion

**Status:** ✅ **COMPLETE**

- Test Coverage: 298/298 tests passing (100%)
- API Endpoints: 8/8 endpoints verified
- SQL Queries: 12+ parameterized queries
- Documentation: Complete with README, AI spec, and features
- Postman Collection: All endpoints documented

**Ready for submission.**


[⬆ Back to Table of Contents](#-table-of-contents)

## 📝 Additional Notes

- **Development Mode:** The application runs with Spring Boot DevTools enabled for automatic restart and LiveReload support during development.
- **Data Validation:** All API endpoints include validation using Spring Validation annotations (e.g., `@Valid`).
- **Security:** The application uses Spring Security with JWT tokens for stateless authentication. Passwords are securely stored and validated.
- **Database Schema:** The database schema is automatically managed by Hibernate during application startup based on the `spring.jpa.hibernate.ddl-auto` setting.

For more information or issues, visit the [GitHub repository](https://github.com/charleswinfield108/RocketDelivery2).

[⬆ Back to Table of Contents](#-table-of-contents)
