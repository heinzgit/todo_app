import axios from 'axios'
import { useAuthStore } from '../stores/auth'

const api = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json'
  }
})

api.interceptors.request.use((config) => {
  const authStore = useAuthStore()
  if (authStore.token) {
    config.headers.Authorization = `Bearer ${authStore.token}`
  }
  return config
})

export interface Task {
  id: number
  title: string
  description: string | null
  dueDate: string
  completed: boolean
  priority: number
  createdAt: string
  updatedAt: string
  completedAt: string | null
  rescheduleCount: number
  taskPriority: 'high' | 'medium' | 'low'
  projectId: number | null
  projectName: string | null
}

export interface TaskRequest {
  title: string
  description?: string
  dueDate: string
  taskPriority?: 'high' | 'medium' | 'low'
  projectId?: number | null
}

export interface Project {
  id: number
  name: string
  description: string | null
  createdAt: string
}

export interface ProjectRequest {
  name: string
  description?: string
}

export const authApi = {
  register(username: string, password: string) {
    return api.post('/auth/register', { username, password })
  },
  login(username: string, password: string) {
    return api.post('/auth/login', { username, password })
  }
}

export const taskApi = {
  getHomeTasks() {
    return api.get<Task[]>('/tasks')
  },
  getAllTasks() {
    return api.get<Task[]>('/tasks/all')
  },
  getPastCompletedTasks() {
    return api.get<Task[]>('/tasks/past-completed')
  },
  getTodayTasks() {
    return api.get<Task[]>('/tasks/today')
  },
  createTask(data: TaskRequest) {
    return api.post<Task>('/tasks', data)
  },
  updateTask(id: number, data: TaskRequest) {
    return api.put<Task>(`/tasks/${id}`, data)
  },
  deleteTask(id: number) {
    return api.delete(`/tasks/${id}`)
  },
  toggleComplete(id: number) {
    return api.patch<Task>(`/tasks/${id}/complete`)
  },
  copyToToday(id: number) {
    return api.post<Task>(`/tasks/${id}/copy-to-today`)
  },
  reschedule(id: number, newDueDate: string) {
    return api.put<Task>(`/tasks/${id}/reschedule`, { newDueDate })
  },
  reorder(taskIds: number[]) {
    return api.put<Task[]>('/tasks/reorder', { taskIds })
  }
}

export const projectApi = {
  getAllProjects() {
    return api.get<Project[]>('/projects')
  },
  createProject(data: ProjectRequest) {
    return api.post<Project>('/projects', data)
  },
  updateProject(id: number, data: ProjectRequest) {
    return api.put<Project>(`/projects/${id}`, data)
  },
  deleteProject(id: number) {
    return api.delete(`/projects/${id}`)
  }
}

export default api
