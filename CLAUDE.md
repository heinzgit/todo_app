# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Structure

```
todo_app/
├── backend/                    # Spring Boot 3 REST API
│   ├── src/main/java/com/todo/
│   │   ├── config/            # Security configuration (JWT filter, BCrypt)
│   │   ├── controller/        # REST controllers (Auth, Task)
│   │   ├── dto/               # Request/Response DTOs
│   │   ├── entity/            # JPA entities (User, Task)
│   │   ├── exception/         # Global exception handling
│   │   ├── repository/        # JPA repositories
│   │   └── service/           # Business logic (AuthService, TaskService, JwtService)
│   └── src/main/resources/application.yml
└── frontend/                  # Vue 3 + Vite + TypeScript SPA
    ├── src/
    │   ├── api/               # Axios API client
    │   ├── components/       # Vue components (TaskItem)
    │   ├── router/            # Vue Router config
    │   ├── stores/            # Pinia stores (auth, task)
    │   └── views/             # Page views (Login, Register, Home)
    └── package.json
```

## Commands

### Prerequisites
- JDK 17+ (or 21 for full compatibility)
- Node.js 18+
- Maven 3.9+
- MySQL 8+ (ensure `todo_db` database exists)

### Backend
```bash
cd backend
mvn spring-boot:run   # Start Spring Boot on port 8080
```

### Frontend
```bash
cd frontend
npm install           # First time only
npm run dev           # Start Vite dev server on port 5173 (proxy to 8080)
npm run build         # Production build
```

### Database Configuration
Edit `backend/src/main/resources/application.yml` to update MySQL credentials:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/todo_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
    username: root
    password: YOUR_PASSWORD
```

## Architecture

### Backend
- JWT-based stateless authentication with BCrypt password hashing
- Spring Security with filter chain (public endpoints: /api/auth/**)
- TaskService provides three views:
  - `getTodayTasks()`: tasks with due date = today (completed and incomplete)
  - `getPastCompletedTasks()`: completed tasks with due date < today
  - `getHomeTasks()`: yesterday's incomplete + today/future tasks (original view)
- Priority ordering: lower number = higher priority, updated via drag-and-drop reorder endpoint
- `completedAt` timestamp recorded when task is marked complete

### Frontend
- Two-tab layout: "Today" (today's tasks) and "History" (past completed tasks)
- Pinia for state management (authStore, taskStore)
- Vue Router with navigation guards for auth protection
- vuedraggable for drag-and-drop task reordering
- Modal-based task creation/editing/rescheduling
- Copy-to-today and reschedule actions available on task items

## Database

### Tables
- **users**: id, username, password, created_at
- **tasks**: id, user_id, title, description, due_date, completed, priority, created_at, updated_at, completed_at

### Key API Endpoints
- `GET /api/tasks/today` - Get today's tasks
- `GET /api/tasks/past-completed` - Get past completed tasks
- `POST /api/tasks/{id}/copy-to-today` - Copy task to today
- `PUT /api/tasks/{id}/reschedule` - Reschedule task to new date
- `PATCH /api/tasks/{id}/complete` - Toggle task completion, sets completedAt timestamp
