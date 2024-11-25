# Banking System API

This is a RESTful API for a basic banking system that supports account management and transactions such as deposits, withdrawals, and transfers.

## Technologies Used

### Spring Framework
- **Spring Boot Starter Web**
- **Spring Boot Starter Data JPA**
- **Spring Boot Starter Validation**

### Database
- **PostgreSQL**
- **Flyway**

### Documentation
- **Springdoc OpenAPI**

### Tools
- **Lombok**
- **MapStruct**
- **Jacoco**

### Testing
- **Spring Boot Starter Test**

### Why These Technologies?
1. **Spring Boot**: Enables rapid development and offers production-ready defaults.
2. **PostgreSQL**: Provides strong SQL compliance and scalability for data persistence.
3. **Flyway**: Ensures database schema consistency across environments.
4. **Springdoc OpenAPI**: Simplifies documentation for developers and external consumers.
5. **Lombok and MapStruct**: Streamline codebase maintenance and improve readability.
6. **Jacoco**: Encourages writing quality tests by measuring code coverage.

---

## How to Run Locally

### Prerequisites
1. **Java 23**.
2. **Docker** and **Docker Compose** for setting up the database.

---

### Steps to Run

#### 1. Clone the repository:
```bash
git clone https://github.com/iltfuande/banking.git
```

#### 2. Build the project
```bash
Windows ".\gradlew.bat clean build"

Linux ".\gradlew clean build"
```

#### 3. Start the PostgreSQL database using Docker:
```bash
docker-compose up -d database
```

#### 4. Start the Banking project using Docker:
```bash
docker-compose up -d app
```

#### 5. Start the Banking project and the PostgreSQL Database a using Docker:
```bash
docker-compose up -d
```

#### 6. Swagger link:
```bash
http://localhost:8080/swagger-ui/index.html
```

### Jacoco code coverage

#### After build task:
```bash
build/reports/jacoco/test/html/index.html
```