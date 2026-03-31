import { defineStore } from 'pinia'
import { ref } from 'vue'
import { projectApi, type Project, type ProjectRequest } from '../api'

export const useProjectStore = defineStore('project', () => {
  const projects = ref<Project[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  async function fetchProjects() {
    loading.value = true
    error.value = null
    try {
      const response = await projectApi.getAllProjects()
      projects.value = response.data
    } catch (e: any) {
      error.value = e.response?.data?.error || 'Failed to fetch projects'
    } finally {
      loading.value = false
    }
  }

  async function createProject(data: ProjectRequest) {
    await projectApi.createProject(data)
    await fetchProjects()
  }

  async function updateProject(id: number, data: ProjectRequest) {
    await projectApi.updateProject(id, data)
    await fetchProjects()
  }

  async function deleteProject(id: number) {
    await projectApi.deleteProject(id)
    await fetchProjects()
  }

  return {
    projects,
    loading,
    error,
    fetchProjects,
    createProject,
    updateProject,
    deleteProject
  }
})