<template>
  <div class="task-item" :class="{ completed: task.completed }">
    <div class="drag-handle">
      <span class="handle-icon">&#8942;&#8942;</span>
    </div>
    <div class="task-checkbox" @click="$emit('toggle')">
      <span class="check-icon">{{ task.completed ? '✓' : '' }}</span>
    </div>
    <div class="task-content">
      <div class="task-title">{{ task.title }}</div>
      <div class="task-description" v-if="task.description">{{ task.description }}</div>
      <div class="task-meta">
        <span class="due-date" :class="{ overdue: isOverdue, today: isToday }">
          {{ formatDate(task.dueDate) }}
        </span>
        <span v-if="isFromYesterday" class="from-yesterday">From yesterday</span>
        <span v-if="task.rescheduleCount > 0" class="reschedule-badge">
          {{ task.rescheduleCount }}× rescheduled
        </span>
        <span class="priority-badge" :class="task.taskPriority">
          {{ task.taskPriority }}
        </span>
      </div>
    </div>
    <div class="task-actions">
      <button class="action-btn" @click="$emit('copy')" title="Copy to today">Copy</button>
      <button class="action-btn" @click="$emit('reschedule')" title="Reschedule">Date</button>
      <button class="action-btn" @click="$emit('edit')" title="Edit">Edit</button>
      <button class="action-btn danger" @click="$emit('delete')" title="Delete">Del</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { Task } from '../api'

const props = defineProps<{
  task: Task
}>()

defineEmits<{
  toggle: []
  copy: []
  reschedule: []
  edit: []
  delete: []
}>()

const today = new Date()
today.setHours(0, 0, 0, 0)

const yesterday = new Date(today)
yesterday.setDate(yesterday.getDate() - 1)

const taskDate = computed(() => {
  const [year, month, day] = props.task.dueDate.split('-').map(Number)
  return new Date(year, month - 1, day)
})

const isOverdue = computed(() => taskDate.value < today && !props.task.completed)
const isToday = computed(() => taskDate.value.getTime() === today.getTime())
const isFromYesterday = computed(() => {
  return taskDate.value.getTime() === yesterday.getTime() && !props.task.completed
})

function formatDate(dateStr: string): string {
  const [year, month, day] = dateStr.split('-').map(Number)
  const date = new Date(year, month - 1, day)

  if (date.getTime() === today.getTime()) return 'Today'
  if (date.getTime() === yesterday.getTime()) return 'Yesterday'

  return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' })
}
</script>

<style scoped>
.task-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: var(--card-bg);
  border-radius: 8px;
  margin-bottom: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
  transition: all 0.2s;
}

.task-item:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.12);
}

.task-item.completed {
  opacity: 0.6;
}

.task-item.completed .task-title {
  text-decoration: line-through;
}

.drag-handle {
  cursor: grab;
  color: var(--text-secondary);
  padding: 4px;
}

.drag-handle:active {
  cursor: grabbing;
}

.handle-icon {
  font-size: 18px;
  letter-spacing: -2px;
}

.task-checkbox {
  width: 24px;
  height: 24px;
  border: 2px solid var(--border);
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  flex-shrink: 0;
}

.task-checkbox:hover {
  border-color: var(--primary);
}

.task-item.completed .task-checkbox {
  background: var(--success);
  border-color: var(--success);
  color: white;
}

.check-icon {
  font-weight: bold;
  font-size: 14px;
}

.task-content {
  flex: 1;
  min-width: 0;
}

.task-title {
  font-weight: 500;
  margin-bottom: 4px;
}

.task-description {
  font-size: 13px;
  color: var(--text-secondary);
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.task-meta {
  display: flex;
  gap: 8px;
  align-items: center;
}

.due-date {
  font-size: 12px;
  color: var(--text-secondary);
  padding: 2px 8px;
  background: var(--bg);
  border-radius: 4px;
}

.due-date.overdue {
  color: var(--danger);
  background: #fef2f2;
}

.due-date.today {
  color: var(--primary);
  background: #eef2ff;
}

.from-yesterday {
  font-size: 12px;
  color: var(--danger);
  padding: 2px 8px;
  background: #fef2f2;
  border-radius: 4px;
}

.reschedule-badge {
  font-size: 11px;
  color: var(--text-secondary);
  padding: 2px 6px;
  background: var(--bg);
  border-radius: 4px;
}

.priority-badge {
  font-size: 11px;
  font-weight: 600;
  padding: 2px 8px;
  border-radius: 4px;
  text-transform: uppercase;
}

.priority-badge.high {
  color: #dc2626;
  background: #fef2f2;
}

.priority-badge.medium {
  color: #d97706;
  background: #fffbeb;
}

.priority-badge.low {
  color: #16a34a;
  background: #f0fdf4;
}

.task-actions {
  display: flex;
  gap: 4px;
  opacity: 0;
  transition: opacity 0.2s;
}

.task-item:hover .task-actions {
  opacity: 1;
}

.action-btn {
  padding: 4px 8px;
  font-size: 12px;
  background: var(--bg);
  border: 1px solid var(--border);
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.action-btn:hover {
  background: var(--border);
}

.action-btn.danger {
  color: var(--danger);
}

.action-btn.danger:hover {
  background: #fef2f2;
  border-color: var(--danger);
}
</style>
