<template>
  <div class="p-6 bg-secondary rounded-lg shadow space-y-6">
    <!-- Заголовок и кнопка действия -->
    <div class="flex justify-between items-center">
      <h2 class="text-2xl font-semibold">{{ group.name }}</h2>
      <div>
        <!-- Если текущий пользователь — владелец -->
        <button
            v-if="isOwner"
            @click="$emit('delete')"
            class="px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700 transition"
        >
          {{ t('groups.deleteGroup') }}
        </button>
        <!-- Иначе, если участник -->
        <button
            v-else-if="isMember"
            @click="$emit('leave')"
            class="px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700 transition"
        >
          {{ t('groups.leave') }}
        </button>
      </div>
    </div>

    <!-- Метаданные группы -->
    <div class="flex flex-wrap gap-4 text-sm text-text/70">
      <span>{{ t('groups.createdAt') }}: {{ formattedDate }}</span>
      <span>
        {{ t('groups.domain') }}:
        <span v-if="group.domain">{{ group.domain.domainName }}</span>
        <span v-else>—</span>
      </span>
      <span :class="group.public ? 'text-green-600' : 'text-red-600'">
        {{ group.public ? t('groups.public') : t('groups.private') }}
      </span>
    </div>

    <!-- Описание -->
    <p class="text-text">{{ group.description }}</p>

    <!-- Теги -->
    <div v-if="Array.isArray(group.topics) && group.topics.length" class="flex flex-wrap gap-2">
      <span
          v-for="tag in group.topics"
          :key="tag"
          class="px-2 py-1 bg-primary rounded-full text-sm"
      >
        #{{ tag }}
      </span>
    </div>

    <!-- Владелец, админы, помощники -->
    <div class="space-y-4 text-text">
      <div>
        <strong>{{ t('groups.owner') }}:</strong>
        {{ group.owner.firstName }} {{ group.owner.lastName }}
      </div>

      <div v-if="group.admins?.length">
        <strong>{{ t('groups.admins') }}:</strong>
        <span v-for="(u, i) in group.admins" :key="u.userId">
          {{ u.firstName }} {{ u.lastName }}<span v-if="i+1<group.admins.length">, </span>
        </span>
      </div>

      <div v-if="group.helpers?.length">
        <strong>{{ t('groups.helpers') }}:</strong>
        <span v-for="(u, i) in group.helpers" :key="u.userId">
          {{ u.firstName }} {{ u.lastName }}<span v-if="i+1<group.helpers.length">, </span>
        </span>
      </div>
    </div>

    <!-- Список участников -->
    <div v-if="members.length" class="space-y-1">
      <strong>{{ t('groups.members') }}:</strong>
      <ul class="list-disc list-inside ml-4">
        <li
            v-for="m in members"
            :key="m.id"
            class="flex items-center justify-between"
        >
          <!-- Кликабельное имя -->
          <button
              @click="goToUser(m.user.id)"
              class="text-accent-primary hover:underline"
          >
            {{ m.user.name }}
          </button>
          <!-- Опционально: показать роль участника -->
          <span class="text-text/60 text-xs">{{ t(`groups.role.${m.role}`) }}</span>
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { defineProps, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { inject } from 'vue'
import { getUserIdFromToken } from '@/utils/jwt/getUserIdFromToken'

const { t } = useI18n()
const coordinator = inject('coordinator')

const props = defineProps({
  group:    Object,
  members:  Array,
  isMember: Boolean
})

// Определяем, владелец ли текущий пользователь
const currentUserId = getUserIdFromToken()
const isOwner = computed(() => props.group.owner?.userId === currentUserId)

// Форматирование даты создания
const formattedDate = computed(() =>
    props.group.createdAt
        ? new Date(props.group.createdAt).toLocaleDateString()
        : ''
)

// Переход на страницу пользователя
function goToUser(userId) {
  coordinator.navigateToUser(userId)
}
</script>
