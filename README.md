# User Management System

This project is a User Management System built with Spring Boot. It provides various endpoints 
for managing user accounts, including registration, updating, soft deletion, and retrieval.
It also integrates with an email service to send welcome emails upon user registration.

## Features

- **User Registration**: Register a new user with validation checks.
- **User Update**: Update existing user details.
- **User Soft Deletion**: Soft delete users by setting their status to inactive.
- **User Retrieval**: Fetch a single user or list all users.
- **Bulk Operations**: Perform bulk user registrations and updates.

## Technology Stack

- **Java 21**
- **Spring Boot**
- **PostgreSQL** for data storage
- **MailHog** for email testing
- **Docker** for containerization

## Prerequisites

- Docker and Docker Compose installed on your machine.

## Getting Started

1. **Clone the repository**

    ```bash
    git clone https://github.com/your-username/user-management-system.git
    cd user-management-system
    ```

2. **Building and Running the Application with Docker**

   The application is containerized using Docker. To build and run the application, follow these steps:

    ```bash
    docker-compose up --build
    ```

## Access the Application

- **User Management API**: [http://localhost:8080/api/users](http://localhost:8080/api/users)
- **Swagger UI**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **MailHog Web UI**: [http://localhost:8025](http://localhost:8025)

## API Endpoints

- **POST /api/users/register**: Register a new user.
- **PUT /api/users**: Update an existing user.
- **GET /api/users/{id}**: Get user details by ID.
- **GET /api/users/all**: Retrieve all users.
- **PATCH /api/users/{id}/soft-delete**: Soft delete a user.
- **POST /api/users/register/bulk**: Register multiple users in a single request.
- **PUT /api/users/bulk-update**: Update multiple users in a single request.
- **PATCH /api/users/bulk-soft-delete**: Soft delete multiple users in a single request.

## MailHog Details

- **SMTP Server**: Port 1025
- **Web UI**: Port 8025

## Testing Guidelines

### Basic Authentication

The application uses Basic Authentication for securing the endpoints. To access any secured endpoint, you must provide the username and password of a registered user.

### Steps to Test

1. **Register a User**

   Before testing other endpoints, you need to register a user. You can do this by sending a POST request to `/api/users/register`. For example, using Postman:

   - URL: `http://localhost:8080/api/users/register`
   - Method: `POST`
   - Body:
     ```json
     {
       "username": "testuser@example.com",
       "password": "password123",
       "role": "admin",
       "enabled": true
     }
     ```

2. **Authenticate and Access Other Endpoints**

   Once you have registered a user, you can use the credentials (username and password) to access other endpoints. For example, to get user details:

   - URL: `http://localhost:8080/api/users/{id}`
   - Method: `GET`
   - Authorization: Basic Auth
      - Username: `testuser@example.com`
      - Password: `password123`

3. **Using Postman**

   - Add the Basic Auth credentials to the `Authorization` tab in Postman.
   - Make sure you have registered a user first, as outlined above, before attempting to access secured endpoints.

4. **Error Handling**

   If you try to access a secured endpoint without providing the correct credentials, or if the user does not exist, you will receive a `401 Unauthorized` or `404 Not Found` response.

### Testing Tips

- Use the **Swagger UI** to explore the API endpoints and see the expected inputs and outputs for each operation.
- Use **MailHog** to view any emails sent by the application, such as the welcome email sent upon user registration.

By following these guidelines, you can effectively test the application and ensure that all features work as expected.
