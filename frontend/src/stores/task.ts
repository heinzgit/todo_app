import { defineStore } from 'pinia'
import { ref } from 'vue'
import { taskApi, type Task, type TaskRequest } from '../api'

export const useTaskStore = defineStore('task', () => {
  const pastCompletedTasks = ref<Task[]>([])
  const todayTasks = ref<Task[]>([])
  const allTasks = ref<Task[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  async function fetchAllTasks() {
    try {
      const response = await taskApi.getAllTasks()
      allTasks.value = response.data
    } catch (e: any) {
      error.value = e.response?.data?.error || 'Failed to fetch tasks'
    }
  }

  async function fetchPastCompletedTasks() {
    loading.value = true
    error.value = null
    try {
      const response = await taskApi.getPastCompletedTasks()
      pastCompletedTasks.value = response.data
    } catch (e: any) {
      error.value = e.response?.data?.error || 'Failed to fetch tasks'
    } finally {
      loading.value = false
    }
  }

  async function fetchTodayTasks() {
    loading.value = true
    error.value = null
    try {
      const response = await taskApi.getTodayTasks()
      todayTasks.value = response.data
    } catch (e: any) {
      error.value = e.response?.data?.error || 'Failed to fetch tasks'
    } finally {
      loading.value = false
    }
  }

  async function fetchAll() {
    await Promise.all([fetchPastCompletedTasks(), fetchTodayTasks(), fetchAllTasks()])
  }

  async function createTask(data: TaskRequest) {
    await taskApi.createTask({ ...data, projectId: data.projectId ?? null })
    await fetchAll()
  }

  async function updateTask(id: number, data: TaskRequest) {
    await taskApi.updateTask(id, { ...data, projectId: data.projectId ?? null })
    await fetchAll()
  }

  async function deleteTask(id: number) {
    await taskApi.deleteTask(id)
    await fetchAll()
  }

  async function toggleComplete(id: number) {
    await taskApi.toggleComplete(id)
    await fetchAll()
  }

  async function copyToToday(id: number) {
    await taskApi.copyToToday(id)
    await fetchAll()
  }

  async function reschedule(id: number, newDueDate: string) {
    await taskApi.reschedule(id, newDueDate)
    await fetchAll()
  }

  async function reorder(taskIds: number[]) {
    await taskApi.reorder(taskIds)
    await fetchAll()
  }

  return {
    pastCompletedTasks,
    todayTasks,
    allTasks,
    loading,
    error,
    fetchAllTasks,
    fetchPastCompletedTasks,
    fetchTodayTasks,
    fetchAll,
    createTask,
    updateTask,
    deleteTask,
    toggleComplete,
    copyToToday,
    reschedule,
    reorder
  }
})
