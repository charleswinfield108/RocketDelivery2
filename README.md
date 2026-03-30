# Rocket Food Delivery

A robust backend application for managing food delivery operations, including restaurants, products, orders, customers, couriers, and their respective statuses. This system provides a complete API for handling restaurant management, order processing, and delivery tracking.

## � Table of Contents

- [Tech Stack](#️-tech-stack)
- [Project Structure](#-project-structure)
- [Installation & Setup Instructions](#-installation--setup-instructions)
- [Environment Variables](#️-environment-variables)
- [API Documentation](#-api-documentation)
- [Author](#-author)
- [License](#-license)
- [Additional Notes](#-additional-notes)

## �🛠️ Tech Stack

| Component | Technology |
|-----------|-----------|
[⬆ Back to Table of Contents](#-table-of-contents)

| **Language** | Java 21 |
| **Framework** | Spring Boot 3.5.13 |
| **Build Tool** | Maven |
| **Database** | MySQL |
| **ORM** | JPA/Hibernate |
| **Security** | Spring Security with JWT (JSON Web Tokens) |
| **Template Engine** | Thymeleaf |
| **Code Generation** | Lombok |
| **Validation** | Spring Validation |
| **Testing** | JUnit & Spring Boot Test |

## 📁 Project Structure

```
rdelivery-template-m12/
├── src/
│   ├── main/
│   │   ├── java/com/rocketFoodDelivery/rocketFood/
│   │   │   ├── controller/              # REST API controllers
│   │   │   │   ├── api/
│   │   │   │   │   ├── AuthController.java
│   │   │   │   │   └── RestaurantApiController.java
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   ├── dtos/                    # Data Transfer Objects for API communication
│   │   │   │   ├── Auth-related DTOs
│   │   │   │   ├── Restaurant DTOs
│   │   │   │   ├── Order DTOs
│   │   │   │   ├── Product DTOs
│   │   │   │   └── Account DTOs
│   │   │   ├── models/                  # JPA Entity models
│   │   │   │   ├── UserEntity.java
│   │   │   │   ├── Restaurant.java
│   │   │   │   ├── Order.java
│   │   │   │   ├── Product.java
│   │   │   │   ├── Customer.java
│   │   │   │   ├── Courier.java
│   │   │   │   ├── Employee.java
│   │   │   │   ├── Address.java
│   │   │   │   ├── OrderStatus.java
│   │   │   │   ├── CourierStatus.java
│   │   │   │   └── ProductOrder.java
│   │   │   ├── repository/              # JPA Repository interfaces for DB operations
│   │   │   ├── service/                 # Business logic layer
│   │   │   │   ├── UserService.java
│   │   │   │   ├── RestaurantService.java
│   │   │   │   ├── OrderService.java
│   │   │   │   ├── ProductService.java
│   │   │   │   ├── CustomerService.java
│   │   │   │   ├── CourierService.java
│   │   │   │   ├── EmployeeService.java
│   │   │   │   ├── AddressService.java
│   │   │   │   ├── OrderStatusService.java
│   │   │   │   ├── CourierStatusService.java
│   │   │   │   └── ProductOrderService.java
│   │   │   ├── security/                # JWT and security configuration
│   │   │   ├── util/                    # Utility classes
│   │   │   ├── exception/               # Custom exception classes
│   │   │   └── RocketFoodApplication.java
│   │   ├── resources/
│   │   │   ├── application.properties  # Configuration file
│   │   │   └── templates/               # Thymeleaf HTML templates
│   │   │       ├── login.html
│   │   │       ├── newRestaurant.html
│   │   │       ├── editRestaurant.html
│   │   │       ├── restaurant.html
│   │   │       └── navbar.html
│   └── test/
│       └── java/com/rocketFoodDelivery/rocketFood/  # Test classes
├── pom.xml                              # Maven configuration
└── README.md                            # This file
[⬆ Back to Table of Contents](#-table-of-contents)

```

## 📋 Installation & Setup Instructions

### ✅ Prerequisites

Before you begin, ensure you have the following installed:
- **Java 21** (or higher)
- **Maven 3.8+**
- **MySQL Server 8.0+**
- **Git**

### 1️⃣ Step 1: Clone the Repository

```bash
git clone https://github.com/charleswinfield108/RocketDelivery2.git
cd rdelivery-template-m12
```

### 2️⃣ Step 2: Create MySQL Database

```bash
mysql -u root -p
```

In the MySQL shell:
```sql
CREATE DATABASE rocket_food_delivery;
```

### 3️⃣ Step 3: Configure Database Connection

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url = jdbc:mysql://localhost:3306/rocket_food_delivery
spring.datasource.username = <your_mysql_username>
spring.datasource.password = <your_mysql_password>
```

### 4️⃣ Step 4: Build the Project

**On Linux/Mac:**
```bash
./mvnw clean install
```

**On Windows:**
```bash
mvnw.cmd clean install
```

### 5️⃣ Step 5: Run the Application

**On Linux/Mac:**
```bash
./mvnw spring-boot:run
```

**On Windows:**
```bash
mvnw.cmd spring-boot:run
```

The application will start on `http://localhost:8080`

### 6️⃣ Step 6: Automatic Data Seeding

Upon startup, the application automatically seeds the database with sample data (restaurants, products, users, orders, etc.) via the `DataSeeder` component. This allows immediate testing of the API without manual data entry.

[⬆ Back to Table of Contents](#-table-of-contents)

## ⚙️ Environment Variables

Configure the following in `src/main/resources/application.properties`:

### Database Configuration
| Variable | Description | Default | Example |
|----------|-----------|---------|---------|
| `spring.datasource.url` | MySQL database connection URL | `jdbc:mysql://localhost:3306/<database_name>` | `jdbc:mysql://localhost:3306/rocket_food_delivery` |
| `spring.datasource.username` | MySQL database username | `<username>` | `root` |
| `spring.datasource.password` | MySQL database password | `<password>` | `your_password` |
| `spring.datasource.driver-class-name` | JDBC driver class | `com.mysql.cj.jdbc.Driver` | *(Default)* |

### JPA/Hibernate Configuration
| Variable | Description | Default |
|----------|-----------|---------|
| `spring.jpa.hibernate.ddl-auto` | Schema generation strategy (`update`, `create`, `create-drop`, `validate`, `none`) | `update` |
| `spring.jpa.show-sql` | Display SQL queries in console | `true` |
| `spring.jpa.properties.hibernate.format_sql` | Format SQL output | `true` |
| `spring.jpa.database-platform` | Hibernate SQL dialect | `org.hibernate.dialect.MySQLDialect` |

### Development Tools Configuration
| Variable | Description | Default |
|----------|-----------|---------|
| `spring.devtools.restart.enabled` | Enable automatic restart on file changes | `true` |
| `spring.devtools.livereload.enabled` | Enable browser LiveReload | `true` |

[⬆ Back to Table of Contents](#-table-of-contents)

## 📚 API Documentation

The application provides RESTful API endpoints for managing food delivery operations. Below is an overview of the main endpoints:

### 🔐 Authentication Endpoints

#### 1. User Login / Authentication
**Endpoint:** `POST /api/auth`

**Description:** Authenticates a user and returns a JWT token for subsequent API requests.

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response (Success):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "userRole": "CUSTOMER"
}
```

**Response (Error):**
```json
{
  "error": "Invalid email or password"
}
```

---

### 🍽️ Restaurant Management Endpoints

**Base Path:** `/api/restaurants`

The `RestaurantApiController` provides endpoints for:
- Retrieving all restaurants
- Fetching restaurant details by ID
- Creating new restaurants (Employee only)
- Updating restaurant information (Employee only)
- Deleting restaurants (Employee only)
- Rating restaurants (Customer)
- Managing products within restaurants

**Common Operations:**
- `GET /api/restaurants` - Get all restaurants
- `GET /api/restaurants/{id}` - Get restaurant by ID
- `POST /api/restaurants` - Create a new restaurant
- `PUT /api/restaurants/{id}` - Update restaurant details
- `DELETE /api/restaurants/{id}` - Delete a restaurant
- `POST /api/restaurants/{id}/rate` - Rate a restaurant

---

### 📦 Additional API Resources

The following services and endpoints are available but not detailed here (refer to controller implementations for specific endpoints):

- **Orders** - Create, retrieve, update, and track order status
- **Products** - Manage restaurant products and inventory
- **Customers** - Customer account management and profile
- **Couriers** - Delivery personnel management and status tracking
- **Employees** - Employee account management
- **Addresses** - Customer address book management

### 🔑 Authentication

All API endpoints (except `/api/auth`) require JWT authentication. Include the token in the request header:

```
Authorization: Bearer <your_jwt_token>
```

### ⚠️ Error Handling

The application implements global exception handling via `GlobalExceptionHandler`:
- **BadRequestException** - Invalid input data
- **ResourceNotFoundException** - Requested resource not found
- **ValidationException** - Validation errors

All errors are returned in a standardized format:
```json
{
  "error": "Error message description",
  "timestamp": "2024-03-30T10:30:00Z",
  "status": 400
}
```

[⬆ Back to Table of Contents](#-table-of-contents)

## 👤 Author

**Charles Winfield**

- GitHub: [@charleswinfield108](https://github.com/charleswinfield108)

[⬆ Back to Table of Contents](#-table-of-contents)

## 📜 License

This project is not currently licensed. Please check the repository for license information.

[⬆ Back to Table of Contents](#-table-of-contents)

## 📝 Additional Notes

- **Development Mode:** The application runs with Spring Boot DevTools enabled for automatic restart and LiveReload support during development.
- **Data Validation:** All API endpoints include validation using Spring Validation annotations (e.g., `@Valid`).
- **Security:** The application uses Spring Security with JWT tokens for stateless authentication. Passwords are securely stored and validated.
- **Database Schema:** The database schema is automatically managed by Hibernate during application startup based on the `spring.jpa.hibernate.ddl-auto` setting.

For more information or issues, visit the [GitHub repository](https://github.com/charleswinfield108/RocketDelivery2).

[⬆ Back to Table of Contents](#-table-of-contents)
