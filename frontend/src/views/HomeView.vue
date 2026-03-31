<template>
  <div class="home">
    <header class="header">
      <div class="container header-content">
        <h1>Todo App</h1>
        <div class="user-menu">
          <span>{{ authStore.username }}</span>
          <button @click="handleLogout" class="btn btn-secondary">Logout</button>
        </div>
      </div>
    </header>

    <main class="container">
      <div class="actions-bar">
        <button @click="openAddModal" class="btn btn-primary">+ Add Task</button>
      </div>

      <div class="tabs">
        <button
          class="tab"
          :class="{ active: activeTab === 'today' }"
          @click="activeTab = 'today'"
        >
          <span class="tab-icon">📋</span>
          Task List
          <span v-if="taskStore.todayTasks.length > 0" class="tab-badge">
            {{ taskStore.todayTasks.length }}
          </span>
        </button>
        <button
          class="tab"
          :class="{ active: activeTab === 'history' }"
          @click="activeTab = 'history'"
        >
          <span class="tab-icon">📝</span>
          Completed Tasks
          <span v-if="taskStore.pastCompletedTasks.length > 0" class="tab-badge">
            {{ taskStore.pastCompletedTasks.length }}
          </span>
        </button>
      </div>

      <div v-if="taskStore.loading" class="loading">Loading tasks...</div>
      <div v-else-if="taskStore.error" class="error">{{ taskStore.error }}</div>
      <div v-else>
        <!-- Today Tab -->
        <div v-if="activeTab === 'today'">
          <!-- Overdue Section -->
          <div v-if="groupedTasks.overdue.length > 0" class="task-group">
            <div class="group-header overdue">
              <span class="group-title">Overdue</span>
              <span class="group-count">{{ groupedTasks.overdue.length }}</span>
            </div>
            <div class="task-list">
              <TaskItem
                v-for="task in groupedTasks.overdue"
                :key="task.id"
                :task="task"
                @toggle="handleToggle(task.id)"
                @copy="handleCopy(task.id)"
                @reschedule="openRescheduleModal(task)"
                @edit="openEditModal(task)"
                @delete="handleDelete(task.id)"
              />
            </div>
          </div>

          <!-- Today Section -->
          <div v-if="groupedTasks.today.length > 0" class="task-group">
            <div class="group-header today">
              <span class="group-title">Today</span>
              <span class="group-count">{{ groupedTasks.today.length }}</span>
            </div>
            <div class="task-list">
              <TaskItem
                v-for="task in groupedTasks.today"
                :key="task.id"
                :task="task"
                @toggle="handleToggle(task.id)"
                @copy="handleCopy(task.id)"
                @reschedule="openRescheduleModal(task)"
                @edit="openEditModal(task)"
                @delete="handleDelete(task.id)"
              />
            </div>
          </div>

          <!-- Future Section -->
          <div v-if="groupedTasks.future.length > 0" class="task-group">
            <div class="group-header future">
              <span class="group-title">Upcoming</span>
              <span class="group-count">{{ groupedTasks.future.length }}</span>
            </div>
            <div class="task-list">
              <TaskItem
                v-for="task in groupedTasks.future"
                :key="task.id"
                :task="task"
                @toggle="handleToggle(task.id)"
                @copy="handleCopy(task.id)"
                @reschedule="openRescheduleModal(task)"
                @edit="openEditModal(task)"
                @delete="handleDelete(task.id)"
              />
            </div>
          </div>

          <div v-if="taskStore.todayTasks.length === 0" class="empty-state">
            <p>No tasks. All caught up!</p>
          </div>
        </div>

        <!-- History Tab -->
        <div v-if="activeTab === 'history'">
          <div v-if="taskStore.pastCompletedTasks.length === 0" class="empty-state">
            <p>No completed tasks yet.</p>
          </div>
          <div v-else class="history-list">
            <div
              v-for="task in taskStore.pastCompletedTasks"
              :key="task.id"
              class="history-item"
            >
              <div class="history-date">
                Due: {{ formatDate(task.dueDate) }}
                <span v-if="task.completedAt" class="completed-time">
                  · Completed: {{ formatDateTime(task.completedAt) }}
                </span>
              </div>
              <div class="history-content">
                <span class="completed-check">✓</span>
                <span class="history-title">{{ task.title }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>

    <!-- Add/Edit Task Modal -->
    <div v-if="showAddModal || showEditModal" class="modal-overlay" @click.self="closeModals">
      <div class="modal">
        <h2>{{ showEditModal ? 'Edit Task' : 'Add Task' }}</h2>
        <form @submit.prevent="handleSubmit">
          <div class="form-group">
            <label>Title</label>
            <input v-model="form.title" type="text" class="input" required />
          </div>
          <div class="form-group">
            <label>Description</label>
            <textarea v-model="form.description" class="input" rows="3"></textarea>
          </div>
          <div class="form-group">
            <label>Due Date</label>
            <input v-model="form.dueDate" type="date" class="input" required />
          </div>
          <div class="form-group">
            <label>Priority</label>
            <select v-model="form.taskPriority" class="input">
              <option value="high">High</option>
              <option value="medium">Medium</option>
              <option value="low">Low</option>
            </select>
          </div>
          <div class="modal-actions">
            <button type="button" @click="closeModals" class="btn btn-secondary">Cancel</button>
            <button type="submit" class="btn btn-primary">
              {{ showEditModal ? 'Save' : 'Add' }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- Reschedule Modal -->
    <div v-if="showRescheduleModal" class="modal-overlay" @click.self="closeModals">
      <div class="modal">
        <h2>Reschedule Task</h2>
        <form @submit.prevent="handleReschedule">
          <div class="form-group">
            <label>New Due Date</label>
            <input v-model="newDueDate" type="date" class="input" required />
          </div>
          <div class="modal-actions">
            <button type="button" @click="closeModals" class="btn btn-secondary">Cancel</button>
            <button type="submit" class="btn btn-primary">Reschedule</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import draggable from 'vuedraggable'
import { useAuthStore } from '../stores/auth'
import { useTaskStore } from '../stores/task'
import TaskItem from '../components/TaskItem.vue'
import type { Task } from '../api'

const router = useRouter()
const authStore = useAuthStore()
const taskStore = useTaskStore()

const activeTab = ref<'today' | 'history'>('today')

const today = new Date()
today.setHours(0, 0, 0, 0)

const groupedTasks = computed(() => {
  const tasks = taskStore.todayTasks
  const overdue: Task[] = []
  const todayTasks: Task[] = []
  const future: Task[] = []

  tasks.forEach(task => {
    const dueDate = new Date(task.dueDate)
    dueDate.setHours(0, 0, 0, 0)

    if (dueDate < today) {
      overdue.push(task)
    } else if (dueDate.getTime() === today.getTime()) {
      todayTasks.push(task)
    } else {
      future.push(task)
    }
  })

  const priorityOrder: Record<string, number> = { high: 0, medium: 1, low: 2 }

  const sortByPriority = (taskList: Task[]) => {
    return [...taskList].sort((a, b) => {
      const aOrder = priorityOrder[a.taskPriority] ?? 1
      const bOrder = priorityOrder[b.taskPriority] ?? 1
      return aOrder - bOrder
    })
  }

  return {
    overdue: sortByPriority(overdue),
    today: sortByPriority(todayTasks),
    future: sortByPriority(future)
  }
})

const showAddModal = ref(false)
const showEditModal = ref(false)
const showRescheduleModal = ref(false)

const form = reactive({
  title: '',
  description: '',
  dueDate: '',
  taskPriority: 'medium' as 'high' | 'medium' | 'low'
})

const editingTask = ref<Task | null>(null)
const newDueDate = ref('')

function handleLogout() {
  authStore.logout()
  router.push('/login')
}

function openAddModal() {
  form.title = ''
  form.description = ''
  form.dueDate = getTodayDateString()
  form.taskPriority = 'medium'
  showAddModal.value = true
}

function getTodayDateString(): string {
  const d = new Date()
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

async function handleToggle(id: number) {
  await taskStore.toggleComplete(id)
}

async function handleCopy(id: number) {
  await taskStore.copyToToday(id)
}

function openEditModal(task: Task) {
  editingTask.value = task
  form.title = task.title
  form.description = task.description || ''
  form.dueDate = task.dueDate
  form.taskPriority = task.taskPriority || 'medium'
  showEditModal.value = true
}

function openRescheduleModal(task: Task) {
  editingTask.value = task
  newDueDate.value = ''
  showRescheduleModal.value = true
}

async function handleSubmit() {
  if (showEditModal.value && editingTask.value) {
    await taskStore.updateTask(editingTask.value.id, {
      title: form.title,
      description: form.description,
      dueDate: form.dueDate,
      taskPriority: form.taskPriority
    })
  } else {
    await taskStore.createTask({
      title: form.title,
      description: form.description,
      dueDate: form.dueDate,
      taskPriority: form.taskPriority
    })
  }
  closeModals()
}

async function handleReschedule() {
  if (editingTask.value && newDueDate.value) {
    await taskStore.reschedule(editingTask.value.id, newDueDate.value)
    closeModals()
  }
}

async function handleDelete(id: number) {
  await taskStore.deleteTask(id)
}

async function handleDragEnd() {
  const taskIds = taskStore.todayTasks.map(t => t.id)
  await taskStore.reorder(taskIds)
}

function closeModals() {
  showAddModal.value = false
  showEditModal.value = false
  showRescheduleModal.value = false
  editingTask.value = null
  form.title = ''
  form.description = ''
  form.dueDate = ''
  form.taskPriority = 'medium'
  newDueDate.value = ''
}

function formatDate(dateStr: string): string {
  const date = new Date(dateStr)
  const today = new Date()
  const yesterday = new Date(today)
  yesterday.setDate(yesterday.getDate() - 1)

  if (date.toDateString() === today.toDateString()) {
    return 'Today'
  }
  if (date.toDateString() === yesterday.toDateString()) {
    return 'Yesterday'
  }

  return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })
}

function formatDateTime(dateTimeStr: string): string {
  const date = new Date(dateTimeStr)
  const time = date.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' })
  return `${formatDate(dateTimeStr)} ${time}`
}

onMounted(() => {
  taskStore.fetchAll()
})
</script>

<style scoped>
.home {
  min-height: 100vh;
}

.header {
  background: var(--card-bg);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  padding: 16px 0;
  margin-bottom: 24px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header h1 {
  color: var(--primary);
  font-size: 24px;
}

.user-menu {
  display: flex;
  align-items: center;
  gap: 16px;
}

.actions-bar {
  margin-bottom: 20px;
}

.tabs {
  display: flex;
  gap: 4px;
  margin-bottom: 20px;
  background: var(--bg);
  padding: 6px;
  border-radius: 12px;
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.05);
}

.tab {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px 20px;
  background: transparent;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  color: var(--text-secondary);
  transition: all 0.2s;
}

.tab:hover {
  color: var(--text);
  background: rgba(255, 255, 255, 0.5);
}

.tab.active {
  background: var(--card-bg);
  color: var(--primary);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.tab-icon {
  font-size: 16px;
}

.tab-badge {
  background: var(--primary);
  color: white;
  font-size: 11px;
  font-weight: 600;
  padding: 2px 8px;
  border-radius: 12px;
  min-width: 20px;
  text-align: center;
}

.tab.active .tab-badge {
  background: var(--primary);
}

.tab.active {
  background: var(--primary);
  color: white;
}

.task-list {
  min-height: 60px;
}

.task-group {
  margin-bottom: 24px;
}

.group-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 2px solid var(--border);
}

.group-header.overdue {
  border-bottom-color: var(--danger);
}

.group-header.today {
  border-bottom-color: var(--primary);
}

.group-header.future {
  border-bottom-color: var(--success);
}

.group-title {
  font-size: 14px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.group-header.overdue .group-title {
  color: var(--danger);
}

.group-header.today .group-title {
  color: var(--primary);
}

.group-header.future .group-title {
  color: var(--success);
}

.group-count {
  font-size: 12px;
  font-weight: 600;
  padding: 2px 8px;
  border-radius: 10px;
  background: var(--bg);
}

.group-header.overdue .group-count {
  color: var(--danger);
  background: #fef2f2;
}

.group-header.today .group-count {
  color: var(--primary);
  background: #eef2ff;
}

.group-header.future .group-count {
  color: var(--success);
  background: #f0fdf4;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.history-item {
  padding: 12px 16px;
  background: var(--card-bg);
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
}

.history-date {
  font-size: 12px;
  color: var(--text-secondary);
  margin-bottom: 4px;
}

.completed-time {
  color: var(--success);
}

.history-content {
  display: flex;
  align-items: center;
  gap: 8px;
}

.completed-check {
  color: var(--success);
  font-weight: bold;
}

.history-title {
  color: var(--text-secondary);
  text-decoration: line-through;
}

.loading, .error, .empty-state {
  text-align: center;
  padding: 40px;
  color: var(--text-secondary);
}

.error {
  color: var(--danger);
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  z-index: 100;
}

.modal {
  background: var(--card-bg);
  padding: 32px;
  border-radius: 12px;
  width: 100%;
  max-width: 450px;
}

.modal h2 {
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

textarea.input {
  resize: vertical;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
}
</style>
