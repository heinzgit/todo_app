# Todo Application Specification

## 1. Project Overview

- **Project Name**: Todo App
- **Type**: Full-stack Web Application
- **Core Functionality**: A personalized todo management system where users can view incomplete tasks from yesterday and upcoming tasks, easily reschedule or copy tasks to today, and prioritize tasks via drag-and-drop.
- **Target Users**: Individual users who want to manage daily tasks effectively

## 2. Technology Stack

- **Frontend**: Vue 3 + Vite + TypeScript
- **Backend**: Spring Boot 3 + Java 17
- **Database**: MySQL 8
- **API**: RESTful JSON API

## 3. Functionality Specification

### 3.1 Authentication
- [x] User registration with username/password
- [x] User login with JWT token
- [x] Password hashing with BCrypt
- [x] Token-based session management

### 3.2 Task Management
- [x] Create new task with title, description, due date
- [x] Edit task details
- [x] Delete task
- [x] Mark task as complete/incomplete
- [x] Tasks belong to a specific user

### 3.3 Task Filtering (Home View)
On login/home, display tasks that match:
- [x] Tasks from yesterday (any status - complete or incomplete) that are NOT completed
- [x] Tasks with due date today or in the future (any status)

### 3.4 Task Rescheduling
- [x] Copy yesterday's incomplete task to today (creates new task with due date = today)
- [x] Reschedule task to a specific future date
- [x] Both actions accessible via task action menu

### 3.5 Priority & Drag-and-Drop
- [x] Each task has a priority order (integer, lower = higher priority)
- [x] Drag-and-drop to reorder tasks
- [x] Priority automatically updates based on position
- [x] Visual feedback during drag

### 3.6 Data Model

**User**
```
id: Long (PK, auto-increment)
username: String (unique, not null)
password: String (hashed, not null)
createdAt: Timestamp
```

**Task**
```
id: Long (PK, auto-increment)
userId: Long (FK to User)
title: String (not null)
description: String (nullable)
dueDate: Date (not null)
completed: Boolean (default false)
priority: Integer (default 0, lower = higher priority)
createdAt: Timestamp
updatedAt: Timestamp
```

## 4. API Endpoints

### Auth
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login, returns JWT

### Tasks (requires JWT auth)
- `GET /api/tasks` - Get filtered tasks for home view
- `GET /api/tasks/all` - Get all tasks for user
- `POST /api/tasks` - Create task
- `PUT /api/tasks/{id}` - Update task
- `DELETE /api/tasks/{id}` - Delete task
- `PATCH /api/tasks/{id}/complete` - Toggle complete status
- `POST /api/tasks/{id}/copy-to-today` - Copy task to today
- `PUT /api/tasks/{id}/reschedule` - Reschedule to new date
- `PUT /api/tasks/reorder` - Bulk update priorities

## 5. Project Structure

```
todo_app/
├── backend/                 # Spring Boot application
│   ├── src/main/java/com/todo/
│   │   ├── TodoApplication.java
│   │   ├── config/
│   │   │   └── SecurityConfig.java
│   │   ├── controller/
│   │   │   ├── AuthController.java
│   │   │   └── TaskController.java
│   │   ├── dto/
│   │   ├── entity/
│   │   │   ├── User.java
│   │   │   └── Task.java
│   │   ├── repository/
│   │   ├── service/
│   │   └── exception/
│   └── src/main/resources/
│       └── application.yml
└── frontend/                # Vue application
    ├── src/
    │   ├── components/
    │   ├── views/
    │   ├── stores/
    │   ├── api/
    │   └── router/
    └── package.json
```

## 6. Acceptance Criteria

1. User can register and login successfully
2. After login, user sees:
   - Yesterday's incomplete tasks
   - Tasks with due date >= today
3. User can copy a yesterday's incomplete task to today
4. User can reschedule a task to a future date
5. User can drag and drop tasks to change priority
6. Priority order persists after refresh
7. Task CRUD operations work correctly
8. All API endpoints return appropriate status codes
