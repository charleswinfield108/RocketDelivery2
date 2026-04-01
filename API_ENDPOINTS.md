# 🚀 Rocket Food Delivery - Complete API Endpoints Reference

## Overview
Complete list of all implemented API endpoints for the Rocket Food Delivery Module 12 application.

**Total Endpoints: 16 Implemented**
- GET: 9
- POST: 4
- PUT: 1
- DELETE: 2

---

## 🔐 Authentication (1 endpoint)

### 1. Login - Get Access Token
- **Method:** POST
- **Path:** `/api/auth`
- **Description:** Authenticate user with email and password. Returns access token for subsequent requests.
- **Request Body:**
  ```json
  {
    "email": "user@example.com",
    "password": "password"
  }
  ```
- **Response:** 200 OK - Access Token
- **Status Codes:** 200, 401

---

## 🏪 Restaurants (6 endpoints)

### 2. List All Restaurants
- **Method:** GET
- **Path:** `/api/restaurants`
- **Description:** Get all restaurants without filters
- **Query Parameters:** None
- **Response:** 200 OK - Array of restaurants
- **Status Codes:** 200

### 3. List Restaurants - Filter by Rating
- **Method:** GET
- **Path:** `/api/restaurants?rating=5`
- **Description:** Get restaurants filtered by rating (1-5)
- **Query Parameters:** 
  - `rating` (1-5)
- **Response:** 200 OK - Filtered restaurants
- **Status Codes:** 200

### 4. List Restaurants - Filter by Price Range
- **Method:** GET
- **Path:** `/api/restaurants?price_range=1`
- **Description:** Get restaurants filtered by price_range (1-3)
- **Query Parameters:** 
  - `price_range` (1-3)
- **Response:** 200 OK - Filtered restaurants
- **Status Codes:** 200

### 5. List Restaurants - Filter by Rating & Price Range
- **Method:** GET
- **Path:** `/api/restaurants?rating=4&price_range=2`
- **Description:** Get restaurants filtered by both rating and price_range
- **Query Parameters:** 
  - `rating` (1-5)
  - `price_range` (1-3)
- **Response:** 200 OK - Filtered restaurants
- **Status Codes:** 200

### 6. Get Restaurant by ID
- **Method:** GET
- **Path:** `/api/restaurants/{id}`
- **Description:** Get details for a specific restaurant by ID
- **Path Parameters:** 
  - `id` (integer) - Restaurant ID
- **Response:** 200 OK - Single restaurant object
- **Status Codes:** 200, 404

### 7. Create Restaurant
- **Method:** POST
- **Path:** `/api/restaurants`
- **Description:** Create a new restaurant with address information
- **Request Body:**
  ```json
  {
    "user_id": 2,
    "name": "Villa Wellington",
    "phone": "15141234567",
    "email": "villa@wellington.com",
    "price_range": 2,
    "address": {
      "street_address": "123 Wellington St.",
      "city": "Montreal",
      "postal_code": "H3G264"
    }
  }
  ```
- **Response:** 201 Created - Created restaurant object
- **Status Codes:** 201, 400

### 8. Update Restaurant
- **Method:** PUT
- **Path:** `/api/restaurants/{id}`
- **Description:** Update restaurant details (name, price_range, phone only)
- **Path Parameters:** 
  - `id` (integer) - Restaurant ID
- **Request Body:**
  ```json
  {
    "name": "B12 Nation",
    "price_range": 3,
    "phone": "2223334444"
  }
  ```
- **Response:** 200 OK - Updated restaurant object
- **Status Codes:** 200, 400, 404

### 9. Delete Restaurant
- **Method:** DELETE
- **Path:** `/api/restaurants/{id}`
- **Description:** Delete a restaurant by ID (cascade deletes products, orders, etc.)
- **Path Parameters:** 
  - `id` (integer) - Restaurant ID
- **Response:** 200 OK - Deleted restaurant details
- **Status Codes:** 200, 404

---

## 🍔 Products (1 endpoint)

### 10. Get Products by Restaurant
- **Method:** GET
- **Path:** `/api/products?restaurant=1`
- **Description:** Get all products for a specific restaurant (restaurant_id required)
- **Query Parameters:** 
  - `restaurant` (integer) - Restaurant ID (required)
- **Response:** 200 OK - Array of products
- **Status Codes:** 200, 400, 404

---

## 📦 Orders (6 endpoints - 45 test cases)

### 11. Get Orders by Customer - ✅ FULLY TESTED (20 tests)
- **Method:** GET
- **Path:** `/api/orders?type=customer&id=7`
- **Description:** Get all orders placed by a specific customer
- **Query Parameters:** 
  - `type=customer` (required)
  - `id` (integer) - Customer ID (required)
- **Response:** 200 OK - Array of customer orders with products
- **Status Codes:** 200, 400, 404

### 12. Get Orders by Restaurant - ✅ FULLY TESTED (20 tests)
- **Method:** GET
- **Path:** `/api/orders?type=restaurant&id=1`
- **Description:** Get all orders placed at a specific restaurant
- **Query Parameters:** 
  - `type=restaurant` (required)
  - `id` (integer) - Restaurant ID (required)
- **Response:** 200 OK - Array of restaurant orders with products
- **Status Codes:** 200, 400, 404

### 13. Get Orders by Courier - ✅ FULLY TESTED (20 tests)
- **Method:** GET
- **Path:** `/api/orders?type=courier&id=3`
- **Description:** Get all orders assigned to a specific courier
- **Query Parameters:** 
  - `type=courier` (required)
  - `id` (integer) - Courier ID (required)
- **Response:** 200 OK - Array of courier orders with products
- **Status Codes:** 200, 400, 404

### 14. Create Order - ✅ FULLY TESTED (21 tests)
- **Method:** POST
- **Path:** `/api/orders`
- **Description:** Create a new order with products
- **Request Body:**
  ```json
  {
    "restaurant_id": 1,
    "customer_id": 3,
    "products": [
      {
        "id": 2,
        "quantity": 1
      },
      {
        "id": 3,
        "quantity": 3
      }
    ],
    "total_cost": 1900
  }
  ```
- **Response:** 201 Created - Created order with products
- **Status Codes:** 201, 400, 404
- **Test Coverage:** Comprehensive validation of customer/restaurant/product existence, quantity validation, price calculation, null safety, and error scenarios

### 15. Update Order Status - ✅ TESTED
- **Method:** POST
- **Path:** `/api/order/{id}/status`
- **Description:** Update the status of an order (pending, in progress, delivered, cancelled)
- **Path Parameters:** 
  - `id` (integer) - Order ID
- **Request Body:**
  ```json
  {
    "status": "delivered"
  }
  ```
- **Response:** 200 OK - Updated status
- **Status Codes:** 200, 400, 404

### 16. Delete Order - ✅ FULLY TESTED (4 tests)
- **Method:** DELETE
- **Path:** `/api/order/{id}`
- **Description:** Delete an order by ID (cascade deletes associated ProductOrder entries)
- **Path Parameters:** 
  - `id` (integer) - Order ID
- **Response:** 200 OK - Deletion confirmation
- **Status Codes:** 200, 404

---

## 🏠 Addresses (1 endpoint)

### 17. Create Address
- **Method:** POST
- **Path:** `/api/address`
- **Description:** Create a new address record
- **Request Body:**
  ```json
  {
    "street_address": "123 Main Street",
    "city": "Montreal",
    "postal_code": "H1A1A1"
  }
  ```
- **Response:** 201 Created - Created address object with ID
- **Status Codes:** 201, 400

---

## 🔍 HTTP Status Codes Reference

| Code | Status | Description |
|------|--------|-------------|
| 200 | OK | Request successful |
| 201 | Created | Resource created successfully |
| 400 | Bad Request | Invalid input or missing parameters |
| 404 | Not Found | Resource not found |
| 401 | Unauthorized | Authentication failed |
| 500 | Internal Server Error | Server error |

---

## 📋 Request/Response Format

### Success Response Format
```json
{
  "message": "Success",
  "data": { /* actual response object */ }
}
```

### Error Response Format
```json
{
  "error": "Error message",
  "details": "Additional error details"
}
```

### Simple Response Format (Some Endpoints)
```json
{
  "status": "delivered"
}
```

---

## 🔑 Authentication

All endpoints (except `/api/auth`) require:
- **Header:** `Authorization: Bearer {accessToken}`
- **Token:** Obtained from `/api/auth` endpoint

---

## 📝 Test Coverage Summary

### Implemented Test Cases
- ✅ **Order GET Tests** (`/api/orders`): 20 test cases
- ✅ **Order POST Tests** (`/api/orders` - CREATE): 21 comprehensive test cases
- ✅ **Order DELETE Tests** (`/api/order/{id}`): 4 test cases
- ✅ **Order Status Tests** (`/api/order/{id}/status`): Verified in separate test class
- ✅ Restaurant GET/DELETE Tests: 32 test cases
- ✅ Products GET Tests: 24 test cases

**Total: 101+ comprehensive test cases** across all implemented endpoints with success and error scenarios.

### 🎯 Recent Additions (April 2026)
- ✅ **21 comprehensive POST /api/orders test cases** covering:
  - Happy path scenarios (basic creation, multiple products)
  - Data integrity (customer/restaurant references, product ordering)
  - Calculation verification (order total accuracy)
  - Entity validation (customer, restaurant, product existence)
  - Field validation (quantities, IDs must be positive)
  - Null safety and required fields
  - Error handling (400 Bad Request responses)
  - Consistency (duplicate prevention, multiple order creation)

---

## 🚀 Getting Started

1. Start the application on `http://localhost:8080`
2. Use Postman collection: `Rocket_Food_Delivery_Module12.postman_collection.json`
3. Set environment variables:
   - `base_url`: http://localhost:8080
   - `accessToken`: Will be auto-populated after login
4. Begin making API requests

---

## 📚 API Documentation

For detailed API documentation, see:
- Feature specifications in `/ai/features/` directory
- Test files in `/src/test/java/com/rocketFoodDelivery/rocketFood/api/`
- Controller implementations in `/src/main/java/com/rocketFoodDelivery/rocketFood/controller/api/`

---

## ✅ Complete Endpoint Checklist

- [x] 🔐 Authentication - 1 endpoint
- [x] 🏪 Restaurants - 8 endpoints
- [x] 🍔 Products - 1 endpoint
- [x] 📦 Orders - 6 endpoints (45 comprehensive test cases)
- [x] 🏠 Addresses - 1 endpoint

**Total: 16 implemented endpoints across 5 resource categories**

### ✅ Test Status Summary
- ✅ **GET /api/orders** - Fully tested (20 tests)
- ✅ **POST /api/orders** - Fully tested (21 tests)
- ✅ **DELETE /api/order/{id}** - Fully tested (4 tests)
- ✅ **POST /api/order/{id}/status** - Verified
- ❌ **GET /api/orders/{id}** - Not yet implemented
- ❌ **PUT /api/orders/{id}** - Not yet implemented
