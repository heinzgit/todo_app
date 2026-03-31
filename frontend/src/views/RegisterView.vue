<template>
  <div class="auth-container">
    <div class="auth-card">
      <h1>Todo App</h1>
      <h2>Register</h2>
      <form @submit.prevent="handleRegister">
        <div class="form-group">
          <label>Username</label>
          <input v-model="username" type="text" class="input" required />
        </div>
        <div class="form-group">
          <label>Password</label>
          <input v-model="password" type="password" class="input" required />
        </div>
        <p v-if="error" class="error">{{ error }}</p>
        <button type="submit" class="btn btn-primary" :disabled="loading">
          {{ loading ? 'Registering...' : 'Register' }}
        </button>
        <p class="switch-auth">
          Already have an account? <router-link to="/login">Login</router-link>
        </p>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { authApi } from '../api'

const router = useRouter()
const authStore = useAuthStore()

const username = ref('')
const password = ref('')
const loading = ref(false)
const error = ref('')

async function handleRegister() {
  loading.value = true
  error.value = ''
  try {
    const response = await authApi.register(username.value, password.value)
    authStore.login(response.data.token, response.data.username, response.data.userId)
    router.push('/')
  } catch (e: any) {
    error.value = e.response?.data?.error || e.response?.data?.username?.[0] || 'Registration failed'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.auth-card {
  background: var(--card-bg);
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
}

.auth-card h1 {
  text-align: center;
  color: var(--primary);
  margin-bottom: 8px;
}

.auth-card h2 {
  text-align: center;
  color: var(--text);
  margin-bottom: 24px;
  font-size: 20px;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 6px;
  font-weight: 500;
  font-size: 14px;
}

.error {
  color: var(--danger);
  font-size: 14px;
  margin-bottom: 16px;
  text-align: center;
}

button {
  width: 100%;
  padding: 12px;
}

.switch-auth {
  text-align: center;
  margin-top: 16px;
  font-size: 14px;
  color: var(--text-secondary);
}

.switch-auth a {
  color: var(--primary);
  text-decoration: none;
}

.switch-auth a:hover {
  text-decoration: underline;
}
</style>
