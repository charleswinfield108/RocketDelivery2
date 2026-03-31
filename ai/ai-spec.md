# 🤖 AI_SPEC — Rocket Food Delivery REST API

## 🎯 Project Identity

- **📱 Project Name:** Rocket Food Delivery REST API
- **📝 Short Description:** A Java Spring Boot REST API for a food delivery marketplace that connects restaurants, customers, and couriers. Built using Test-Driven Development with complete coverage by automated tests.
- **🏗️ Project Type:** Java Spring Boot REST API (Backend Service)

---

## 🎪 Goal and Scope

### 🎯 Goal

Build a robust, well-tested REST API that serves as the critical bridge between a mobile application and the Rocket Food Delivery database. The API must follow Test-Driven Development practices, with comprehensive automated test coverage ensuring reliability and preventing regressions during future changes.

### ✅ In Scope (Build Now)

- Complete authentication endpoint (already provided)
- Complete 12 native SQL queries across 6 repository classes
- Complete RestaurantApiController with POST, PUT, DELETE methods
- Implement 3 new controllers: OrderApiController, ProductApiController, OrderStatusApiController
- Implement business logic in service classes for order creation, cascade deletion, and status transitions
- Write 4 Order API tests from scratch following TDD pattern
- Write 2 additional restaurant controller tests
- All endpoints must be accessible via REST API (GET, POST, PUT, DELETE)
- All SQL queries must use parameterized bindings (NO string concatenation)
- Postman collection covering all endpoints
- AI Specification documents and feature specifications
- Video demonstrations: Concepts, LeetCode solutions, and technical overview
- Professional README explaining the entire project setup

### ❌ Out of Scope (Do NOT Build)

- Frontend or UI components
- Mobile app implementation
- Modifying provided entity models, DTOs, AuthApiController, GlobalExceptionHandler, ResponseBuilder, DataSeeder
- Modifying the 2 pre-written restaurant tests
- Any authentication implementation changes (Auth is pre-provided)
- String concatenation in SQL queries (use parameterized statements only)
- Business logic in controllers (must be in service layer)

---

## 👥 Users and Use Cases

- **👨‍💼 Mobile App Developers** — Consume API endpoints to build customer-facing mobile application
- **👤 Mobile App Users (Customers)** — Use the API indirectly through the mobile app to browse restaurants, place orders, track delivery
- **🚴 Mobile App Users (Couriers)** — Use the API indirectly through the mobile app to manage deliveries and update order status
- **🔌 API Consumers (Third-party integrations)** — Future integrations may consume endpoints for reporting, analytics, or partner platforms

---

## 🗂️ Feature Index (Links Only)

Feature specifications will be created as separate documents:

- 🔐 `ai_feature_authentication.md` — Login and JWT token generation (pre-provided)
- 🍽️ `ai_feature_restaurant_management.md` — Restaurant CRUD operations
- 📦 `ai_feature_order_management.md` — Order creation, retrieval, updates
- 🛒 `ai_feature_product_management.md` — Product listing and management
- 📊 `ai_feature_order_status.md` — Order status tracking and transitions
- 🚚 `ai_feature_courier_operations.md` — Courier assignments and status tracking

---

## 🗺️ Pages / Screens / Routes (API Endpoints)

### 🔐 Authentication
- `POST /api/auth/login` — Authenticate user and return JWT token (pre-provided)

### 🍽️ Restaurants
- `GET /api/restaurants` — Retrieve all restaurants
- `GET /api/restaurants/{id}` — Retrieve restaurant by ID (pre-provided)
- `POST /api/restaurants` — Create new restaurant
- `PUT /api/restaurants/{id}` — Update existing restaurant
- `DELETE /api/restaurants/{id}` — Delete restaurant (cascade delete related orders/products)

### 📦 Orders
- `GET /api/orders` — Retrieve all orders
- `GET /api/orders/{id}` — Retrieve order by ID
- `POST /api/orders` — Create new order
- `PUT /api/orders/{id}` — Update order
- `DELETE /api/orders/{id}` — Delete order

### 🛒 Products
- `GET /api/products` — Retrieve all products
- `GET /api/products/{id}` — Retrieve product by ID
- `GET /api/restaurants/{restaurantId}/products` — Retrieve products for a specific restaurant
- `POST /api/products` — Create new product
- `PUT /api/products/{id}` — Update product
- `DELETE /api/products/{id}` — Delete product

### 📊 Order Status
- `GET /api/order-status` — Retrieve all order statuses
- `GET /api/order-status/{id}` — Retrieve order status by ID
- `POST /api/order-status` — Create new order status
- `PUT /api/order-status/{id}` — Update order status

---

## 🗄️ Data and Models (Simple)

### 🏛️ Database Type
MySQL relational database (pre-configured in Module 11)

### 📋 Main Entities

- **👤 UserEntity** — Base user information (ID, username, password, email, phone, role)
- **👥 Customer** — Extends UserEntity with customer-specific data (address, rating)
- **🚴 Courier** — Extends UserEntity with courier-specific data (vehicle, availability)
- **👨‍💼 Employee** — Extends UserEntity with employee-specific data (restaurant assignment)
- **🍽️ Restaurant** — Name, address, phone, rating, owner
- **🍔 Product** — Name, description, price, restaurant_id, category
- **📦 Order** — Customer order containing multiple products, total price, status, timestamps
- **🛒 ProductOrder** — Junction table linking products to orders (many-to-many)
- **📊 OrderStatus** — Status definitions (PENDING, ACCEPTED, IN_DELIVERY, DELIVERED, CANCELED)
- **🚴‍♂️ CourierStatus** — Courier availability states (AVAILABLE, BUSY, OFFLINE)
- **📍 Address** — Delivery addresses linked to customers

---

## 🛠️ Tech Stack and Tools

### ☕ Backend
- **Language:** Java 17
- **Framework:** Spring Boot 3
- **Persistence:** Spring Data JPA, Hibernate ORM
- **Database:** MySQL 8.x
- **Build Tool:** Maven
- **Web:** Spring Web (REST Controllers)

### 🧪 Testing
- **Unit/Integration Testing:** JUnit 5 (Jupiter)
- **Mocking:** Mockito
- **API Testing:** MockMvc (Spring Test)
- **Test Reports:** Maven Surefire

### 🔐 Security
- **Authentication:** JWT Tokens (pre-implemented)
- **Authorization:** Role-based access control (RBAC)

### 📚 Tools & Libraries
- **Lombok** — Reduce boilerplate code
- **Jackson** — JSON serialization
- **Hibernate Validator** — Input validation
- **Postman** — API testing and documentation

---

## 📂 Repository Structure

```
rdelivery-template-m12/
├── src/
│   ├── main/
│   │   ├── java/com/rocketFoodDelivery/rocketFood/
│   │   │   ├── controller/
│   │   │   │   ├── api/
│   │   │   │   │   ├── AuthApiController.java (provided)
│   │   │   │   │   ├── RestaurantApiController.java (complete)
│   │   │   │   │   ├── OrderApiController.java (new)
│   │   │   │   │   ├── ProductApiController.java (new)
│   │   │   │   │   └── OrderStatusApiController.java (new)
│   │   │   │   └── GlobalExceptionHandler.java (provided)
│   │   │   ├── service/
│   │   │   │   ├── RestaurantService.java (complete with SQL)
│   │   │   │   ├── OrderService.java (new with business logic)
│   │   │   │   ├── ProductService.java (new with business logic)
│   │   │   │   └── OrderStatusService.java (new with business logic)
│   │   │   ├── repository/
│   │   │   │   ├── RestaurantRepository.java (complete with SQL queries)
│   │   │   │   ├── OrderRepository.java (new with parameterized SQL)
│   │   │   │   ├── ProductRepository.java (new with parameterized SQL)
│   │   │   │   ├── OrderStatusRepository.java (new with parameterized SQL)
│   │   │   │   ├── CourierRepository.java (complete with SQL)
│   │   │   │   └── ... (other repositories)
│   │   │   ├── models/ (provided entity classes)
│   │   │   ├── dtos/ (provided DTOs)
│   │   │   ├── exception/ (provided exception classes)
│   │   │   ├── security/ (authentication provided)
│   │   │   ├── util/
│   │   │   │   └── ResponseBuilder.java (provided)
│   │   │   ├── RocketFoodApplication.java (provided)
│   │   │   └── DataSeeder.java (provided)
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/rocketFoodDelivery/rocketFood/
│           ├── api/
│           │   ├── RestaurantApiControllerTest.java (2 tests pre-written)
│           │   ├── OrderApiControllerTest.java (4 tests to write)
│           │   ├── ProductApiControllerTest.java
│           │   └── OrderStatusApiControllerTest.java
│           └── service/
│               ├── RestaurantServiceTest.java
│               ├── OrderServiceTest.java
│               ├── ProductServiceTest.java
│               └── OrderStatusServiceTest.java
├── ai/
│   ├── ai-spec.md (this file)
│   ├── ai_feature_authentication.md
│   ├── ai_feature_restaurant_management.md
│   ├── ai_feature_order_management.md
│   ├── ai_feature_product_management.md
│   ├── ai_feature_order_status.md
│   └── ai_feature_courier_operations.md
├── pom.xml
├── mvnw / mvnw.cmd
└── README.md (comprehensive setup guide)
```

---

## 📏 Rules for the AI

1. **🧪 Test-Driven Development (TDD):** Write tests first, then implementation. Commits must show tests before implementation code.
2. **🔒 SQL Queries:** Use **parameterized bindings ONLY**. No string concatenation in SQL queries. Example: `@Query("SELECT * FROM restaurant WHERE id = ?1")` or use named parameters.
3. **⚙️ Service Layer:** All business logic must be in service classes. Controllers only:
   - Parse HTTP requests
   - Delegate to services
   - Return HTTP responses
4. **🚫 Do Not Modify Provided Code:**
   - Entity models in `/models`
   - DTOs in `/dtos`
   - AuthApiController
   - GlobalExceptionHandler
   - ResponseBuilder utility
   - DataSeeder
   - The 2 pre-written restaurant tests
5. **📝 Naming Conventions:**
   - Controllers: `*ApiController.java`
   - Services: `*Service.java`
   - Repositories: `*Repository.java`
   - Tests: `*Test.java` or `*Tests.java`
6. **⚠️ Exception Handling:** Use provided exceptions (BadRequestException, ResourceNotFoundException, ValidationException) and GlobalExceptionHandler.
7. **📤 Response Format:** Use ResponseBuilder to construct all API responses (consistency across endpoints).
8. **🌳 Branching:** Create feature branches from `dev`, merge back to `dev`, never commit directly to `main`.
9. **📊 Git History:** Must reflect TDD workflow — commits show tests first, then implementation.
10. **💎 Code Quality:** Keep code junior-friendly and readable. Avoid over-engineering.

---

## 🚀 How to Run / Test the Project

### 📋 Prerequisites
- Java 17 installed
- Maven installed
- MySQL 8.x running locally
- Git configured

### ⚙️ Setup

1. **Clone and navigate:**
   ```bash
   git clone <repository-url>
   cd rdelivery-template-m12
   ```

2. **Configure database** in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/rocket_food_delivery
   spring.datasource.username=root
   spring.datasource.password=<your-password>
   spring.jpa.hibernate.ddl-auto=create-drop
   ```

3. **Install dependencies:**
   ```bash
   ./mvnw clean install
   ```

### ▶️ Run the Application

```bash
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`

### 🧪 Run All Tests

```bash
./mvnw test
```

### 🧪 Run Specific Test Class

```bash
./mvnw test -Dtest=OrderApiControllerTest
```

### 🧪 Run Tests with Coverage

```bash
./mvnw clean test jacoco:report
```

### 🔐 Test Authentication

```bash
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "password"
}
```

---

## ✅ Definition of Done

- [ ] All 12 native SQL queries implemented with parameterized bindings across 6 repository classes
- [ ] RestaurantApiController completed (POST, PUT, DELETE methods)
- [ ] 3 new controllers implemented (OrderApiController, ProductApiController, OrderStatusApiController)
- [ ] Business logic implemented in all service classes
- [ ] 4 Order API tests written from scratch (TDD pattern)
- [ ] 2 additional restaurant controller tests written
- [ ] All tests pass (`./mvnw test`)
- [ ] No test failures or compilation errors
- [ ] Postman collection exported as `PostmanCollection.json` with all endpoints
- [ ] AI Specification document completed (this file)
- [ ] 6 feature specification documents created (one per feature area)
- [ ] Comprehensive README.md explaining project setup, API usage, and development workflow
- [ ] Git history reflects TDD workflow (tests committed before implementation)
- [ ] Feature branches merged properly (feature/* → dev → main)
- [ ] Code follows all constraints: parameterized SQL, service-layer logic, no prohibited modifications
- [ ] Concepts video recorded (3 challenging concepts with explanations)
- [ ] LeetCode challenge solutions completed with explanations and screenshots
- [ ] Technical demonstration and code overview video recorded
- [ ] Professional presentation and code quality standards met
- [ ] Project submitted by deadline (Friday 11:59 PM)
- [ ] All extra miles completed (if attempted)