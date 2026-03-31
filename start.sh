#!/bin/bash

# Start backend (Spring Boot)
cd "$(dirname "$0")/backend"
mvn spring-boot:run -q &
BACKEND_PID=$!

# Start frontend (Vite)
cd "$(dirname "$0")/frontend"
npm run dev &
FRONTEND_PID=$!

echo "Backend started (PID: $BACKEND_PID)"
echo "Frontend started (PID: $FRONTEND_PID)"
echo ""
echo "Backend: http://localhost:8080"
echo "Frontend: http://localhost:5173"
echo ""
echo "Press Ctrl+C to stop all services"

# Wait for both processes
wait
