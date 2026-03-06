# OWASP Broken Access Control

Broken access control occurs when applications do not properly restrict users from accessing resources that belong to other users. This can allow attackers to access sensitive information by manipulating request parameters.

This lab allows learners to:

1. Discover access control weaknesses
2. Exploit insecure resource access
3. Implement proper authorization checks

---

# Vulnerability Selected and Why It Matters

Access control ensures that users can only access resources they are authorized to view or modify.

When access control is implemented incorrectly, attackers may:

- Access other users’ private data
- Modify resources they do not own
- Perform unauthorized operations

One common example is **Insecure Direct Object Reference (IDOR)**, where attackers manipulate object identifiers such as user IDs to access other users' data.

This lab demonstrates how improper authorization checks lead to **Broken Access Control vulnerabilities**.

---

# Target Audience

This lab is designed for:

- Developers learning **secure backend development**
- Students studying **web application security**
- Security learners exploring **OWASP Top 10 vulnerabilities**

---

# Learning Objectives

After completing this lab learners will be able to:

- Understand how Broken Access Control vulnerabilities occur
- Identify insecure object access implementations
- Exploit APIs that do not enforce proper authorization checks
- Implement secure access control validation
- Understand how proper authorization prevents IDOR attacks

---

# Lab Scenario (Discover → Exploit → Fix)

The application exposes **two API endpoints**:

- **Unsecured endpoint** (vulnerable)
- **Secured endpoint** (fixed implementation)

---

## Step 1 — Discover the Vulnerability

Users can register and obtain a user ID.

```
POST /api/users/register
```

Example request:

```json
{
  "username": "kishor",
  "email": "kishor@email.com",
  "role": "ROLE_USER"
}
```

Each user receives a unique ID stored in the database.

The application uses this ID when requesting profile information.

---

## Step 2 — Exploit the Vulnerability

The vulnerable endpoint allows users to access any user profile by changing the ID.

```
GET /api/users/unsecured/{id}
```

Example request:

```
GET /api/users/unsecured/2
Header: userId: 1
```

This request allows **User 1 to access User 2’s profile**, exposing sensitive information.

This vulnerability occurs because the application **does not enforce proper authorization checks**. :contentReference[oaicite:0]{index=0}

---

## Step 3 — Fix the Vulnerability

The secured endpoint ensures users can only access their own profile.

```
GET /api/users/secured/{id}
```

Example request:

```
GET /api/users/secured/2
Header: userId: 1
```

The application now checks whether the requesting user ID matches the requested profile ID.

If they do not match, access is denied.

This prevents unauthorized access to other users' data.

---

# Technical Design

## Architecture

```
Client (Postman / Browser)
        │
        ▼
Spring Boot Controller
        │
        ▼
Service Layer
        │
        ▼
Repository Layer
        │
        ▼
H2 Database
```

---

## Technology Stack

| Component | Technology |
|---|---|
Language | Java 17 |
Framework | Spring Boot |
Security | Spring Security |
Database | H2 In-Memory Database |
ORM | Spring Data JPA |

Dependencies such as **Spring Web, Spring Security, Spring Data JPA, and H2 database** are configured in the Maven project. :contentReference[oaicite:1]{index=1}

---

# Running the Lab

### 1 Clone the repository

```
git clone <repository-url>
```

### 2 Build the project

```
mvn clean install
```

### 3 Run the application

```
mvn spring-boot:run
```

The application starts using the Spring Boot entry point. :contentReference[oaicite:2]{index=2}

---

# API Demonstration

## Register User

```
POST /api/users/register
```

Example request:

```json
{
  "username": "user1",
  "email": "user1@email.com",
  "role": "ROLE_USER"
}
```

---

## Access Profile (Vulnerable)

```
GET /api/users/unsecured/{id}
```

Example:

```
GET /api/users/unsecured/2
Header: userId: 1
```

Result:

User 1 can access User 2's data.

This demonstrates **Broken Access Control**.

---

## Access Profile (Secured)

```
GET /api/users/secured/{id}
```

Example:

```
GET /api/users/secured/2
Header: userId: 1
```

Result:

Access is denied because users are only allowed to access their own profiles.

---

# Learning Validation

Learners validate their understanding by completing the following tasks:

1. Register multiple users
2. Access their own profile successfully
3. Attempt to access another user’s profile using the vulnerable endpoint
4. Observe that the system exposes the data
5. Use the secured endpoint and verify that unauthorized access is denied

Completion of these tasks demonstrates how Broken Access Control vulnerabilities occur and how they can be prevented.

---

# Repository Structure

| File | Description |
|---|---|
BrokenAccessControlApplication.java | Application entry point |
UserController.java | API endpoints |
UserService.java | Service interface |
UserServiceImpl.java | Business logic |
UserRepository.java | Database operations |
User.java | Entity model |
SecurityConfig.java | Security configuration |