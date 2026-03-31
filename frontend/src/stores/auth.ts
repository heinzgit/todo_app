import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(localStorage.getItem('token'))
  const username = ref<string | null>(localStorage.getItem('username'))
  const userId = ref<number | null>(Number(localStorage.getItem('userId')) || null)

  const isAuthenticated = computed(() => !!token.value)

  function login(newToken: string, newUsername: string, newUserId: number) {
    token.value = newToken
    username.value = newUsername
    userId.value = newUserId
    localStorage.setItem('token', newToken)
    localStorage.setItem('username', newUsername)
    localStorage.setItem('userId', String(newUserId))
  }

  function logout() {
    token.value = null
    username.value = null
    userId.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('username')
    localStorage.removeItem('userId')
  }

  return {
    token,
    username,
    userId,
    isAuthenticated,
    login,
    logout
  }
})
