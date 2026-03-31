# Todo Application

A full-stack todo application built with Vue 3 + Spring Boot + MySQL.

## Features

- User authentication (register/login)
- View yesterday's incomplete tasks and upcoming tasks on home
- Copy yesterday's incomplete tasks to today
- Reschedule tasks to future dates
- Drag-and-drop task prioritization
- Task CRUD operations

## Tech Stack

- **Frontend**: Vue 3, Vite, TypeScript, Pinia, Vue Router, vuedraggable
- **Backend**: Spring Boot 3, Java 17, Spring Security, JWT
- **Database**: MySQL 8

## Prerequisites

- JDK 17+
- Node.js 18+
- MySQL 8+

## Setup

### 1. Database

Create the database (or update `application.yml` with your connection details):

```sql
CREATE DATABASE todo_db;
```

Update `backend/src/main/resources/application.yml` with your MySQL credentials if needed.

### 2. Backend

```bash
cd backend
./mvnw spring-boot:run
```

The backend will run on `http://localhost:8080`.

### 3. Frontend

```bash
cd frontend
npm install
npm run dev
```

The frontend will run on `http://localhost:5173`.

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login

### Tasks (requires JWT token)
- `GET /api/tasks` - Get home tasks (yesterday's incomplete + today/future)
- `GET /api/tasks/all` - Get all tasks
- `POST /api/tasks` - Create task
- `PUT /api/tasks/{id}` - Update task
- `DELETE /api/tasks/{id}` - Delete task
- `PATCH /api/tasks/{id}/complete` - Toggle completion
- `POST /api/tasks/{id}/copy-to-today` - Copy task to today
- `PUT /api/tasks/{id}/reschedule` - Reschedule task
- `PUT /api/tasks/reorder` - Reorder tasks (bulk update priorities)
