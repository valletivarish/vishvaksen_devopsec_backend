# Inventory Management System - Backend

Student: Vishvaksen Machana (25173421)
Module: Cloud DevOpsSec (H9CDOS)

## Overview

This is the backend REST API for the Inventory Management System, built with Spring Boot 3 and Java 17. It provides CRUD operations for managing products, categories, suppliers, warehouses, and stock movements. The application includes JWT-based authentication, comprehensive input validation, and static code analysis integration.

## Tech Stack

- Java 17
- Spring Boot 3.2.5
- Spring Security 6 with JWT Authentication
- Spring Data JPA with MySQL
- Jakarta Bean Validation
- Lombok
- SpringDoc OpenAPI (Swagger)
- Maven

## Prerequisites

- Java 17 or higher
- Maven 3.8+
- MySQL 8.0+

## Local Development Setup

1. Create MySQL database:
   CREATE DATABASE inventory_db;

2. Update database credentials in src/main/resources/application.properties if needed.

3. Build and run:
   mvn clean install
   mvn spring-boot:run

4. Access the API at http://localhost:8080
5. Swagger UI at http://localhost:8080/swagger-ui.html

## API Endpoints

Authentication:
- POST /api/auth/register - Register a new user
- POST /api/auth/login - Login and receive JWT token

Products:
- GET /api/products - List all products
- GET /api/products/{id} - Get product by ID
- POST /api/products - Create a new product
- PUT /api/products/{id} - Update a product
- DELETE /api/products/{id} - Delete a product
- GET /api/products/low-stock - Get products below reorder level
- GET /api/products/search?name={name} - Search products by name
- GET /api/products/category/{categoryId} - Get products by category
- GET /api/products/supplier/{supplierId} - Get products by supplier

Categories:
- GET /api/categories - List all categories
- GET /api/categories/{id} - Get category by ID
- POST /api/categories - Create a category
- PUT /api/categories/{id} - Update a category
- DELETE /api/categories/{id} - Delete a category

Suppliers:
- GET /api/suppliers - List all suppliers
- GET /api/suppliers/{id} - Get supplier by ID
- POST /api/suppliers - Create a supplier
- PUT /api/suppliers/{id} - Update a supplier
- DELETE /api/suppliers/{id} - Delete a supplier
- GET /api/suppliers/search?name={name} - Search suppliers

Warehouses:
- GET /api/warehouses - List all warehouses
- GET /api/warehouses/{id} - Get warehouse by ID
- POST /api/warehouses - Create a warehouse
- PUT /api/warehouses/{id} - Update a warehouse
- DELETE /api/warehouses/{id} - Delete a warehouse

Stock Movements:
- GET /api/stock-movements - List all movements
- GET /api/stock-movements/{id} - Get movement by ID
- POST /api/stock-movements - Record a stock movement
- GET /api/stock-movements/product/{productId} - Movements by product
- GET /api/stock-movements/warehouse/{warehouseId} - Movements by warehouse
- GET /api/stock-movements/type/{type} - Movements by type
- GET /api/stock-movements/recent - Recent movements

Dashboard:
- GET /api/dashboard - Get dashboard summary

## Static Analysis

Run all static analysis tools:
   mvn verify

This runs SpotBugs, PMD, and JaCoCo code coverage analysis.

## Testing

Run tests:
   mvn test

Tests use H2 in-memory database.

## CI/CD

The GitHub Actions pipeline (.github/workflows/ci-cd.yml) runs on push and pull requests to main. It includes static analysis, security scanning with Semgrep, and deployment stages.
