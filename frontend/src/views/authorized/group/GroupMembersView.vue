<template>
  <div class="space-y-6">
    <h1 class="text-2xl font-semibold">{{ t('groups.members') }}</h1>

    <!-- Search & Filters -->
    <div class="flex flex-col md:flex-row md:items-end md:space-x-4 space-y-4 md:space-y-0">
      <!-- Поиск по имени -->
      <div class="flex-1">
        <label class="block text-sm font-medium mb-1">{{ t('common.search') }}</label>
        <input
            v-model="searchTerm"
            @input="applyFilters"
            type="text"
            placeholder="Введите имя..."
            class="w-full border rounded px-3 py-2"
        />
      </div>

      <!-- Фильтр по статусам -->
      <div>
        <label class="block text-sm font-medium mb-1">{{ t('groups.filterStatus') }}</label>
        <select
            v-model="selectedStatuses"
            @change="applyFilters"
            multiple
            class="w-full border rounded px-3 py-2"
        >
          <option
              v-for="statusOption in statusOptions"
              :key="statusOption"
              :value="statusOption"
          >
            {{ t(`groups.status.${statusOption}`) }}
          </option>
        </select>
      </div>

      <!-- Фильтр по ролям -->
      <div>
        <label class="block text-sm font-medium mb-1">{{ t('groups.filterRole') }}</label>
        <select
            v-model="selectedRoles"
            @change="applyFilters"
            multiple
            class="w-full border rounded px-3 py-2"
        >
          <option
              v-for="roleOption in roleOptions"
              :key="roleOption"
              :value="roleOption"
          >
            {{ t(`roles.${roleOption}`) }}
          </option>
        </select>
      </div>
    </div>

    <!-- Список участников -->
    <ul class="space-y-4">
      <li
          v-for="member in filteredMembers"
          :key="member.id"
          class="p-4 bg-secondary rounded flex flex-col md:flex-row md:items-center justify-between"
      >
        <div>
          <!-- Кликабельное имя -->
          <button
              @click="navigateToUserProfile(member.user.id)"
              class="text-lg font-medium text-accent-primary hover:underline"
          >
            {{ member.user.name }}
          </button>
          <div class="text-sm text-text/60">
            {{ t('groups.status') }}:
            {{ t(`groups.status.${member.status}`) }},
            {{ t('groups.role') }}:
            {{ t(`roles.${member.role}`) }}
          </div>
        </div>

        <!-- Кнопки управления (не показываем для owner) -->
        <div
            v-if="canManage && member.role !== 'owner'"
            class="mt-4 md:mt-0 flex flex-wrap gap-2"
        >
          <!-- Pending: принять / отклонить -->
          <template v-if="member.status === 'pending'">
            <button
                @click="approveMember(member.user.id)"
                class="px-3 py-1 bg-green-600 text-white rounded hover:bg-green-700"
            >
              {{ t('groups.approve') }}
            </button>
            <button
                @click="declineMember(member.user.id)"
                class="px-3 py-1 bg-red-600 text-white rounded hover:bg-red-700"
            >
              {{ t('groups.decline') }}
            </button>
          </template>

          <!-- Invited: отменить приглашение -->
          <template v-else-if="member.status === 'invited'">
            <button
                @click="cancelInvitation(member.user.id)"
                class="px-3 py-1 bg-orange-600 text-white rounded hover:bg-orange-700"
            >
              {{ t('groups.cancelInvitation') }}
            </button>
          </template>

          <!-- Approved: кик / бан / смена роли -->
          <template v-else-if="member.status === 'approved'">
            <button
                @click="kickMember(member.user.id)"
                class="px-3 py-1 bg-red-600 text-white rounded hover:bg-red-700"
            >
              {{ t('groups.kick') }}
            </button>
            <button
                @click="banMember(member.user.id)"
                class="px-3 py-1 bg-gray-600 text-white rounded hover:bg-gray-700"
            >
              {{ t('groups.ban') }}
            </button>
            <select
                v-model="member.role"
                @change="changeRole(member.user.id, member.role)"
                class="px-3 py-1 border rounded"
            >
              <option
                  v-for="roleOption in roleOptions"
                  :key="roleOption"
                  :value="roleOption"
              >
                {{ t(`roles.${roleOption}`) }}
              </option>
            </select>
          </template>
        </div>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { inject } from 'vue'
import { useI18n } from 'vue-i18n'
import checkRights from '@/utils/groups/checkRights'
// import {
//   processJoinRequestIntent,
//   removeMemberIntent,
//   changeMemberRoleIntent,
//   fetchMembersIntent
// } from '@/intents/groupIntents'
// import { handleGroupIntent } from '@/actions/groupActions'

const { t } = useI18n()

// Данные из GroupWrapper
const group = inject('group')
const members = inject('members')
const userRole = inject('role')
const coordinator = inject('coordinator')

// Опции фильтрации
const statusOptions = ['approved', 'pending', 'banned']
const roleOptions = ['member', 'helper', 'admin', 'owner', 'invited']

// Поиск и фильтры
const searchTerm = ref('')
const selectedStatuses = ref([])
const selectedRoles = ref([])

// Отфильтрованный список
const filteredMembers = ref([])

// Проверка прав
const canManage = computed(() =>
    checkRights(userRole.value, group.value.minRoleForEvents)
)

// Фильтрация
function applyFilters() {
  filteredMembers.value = members.value.filter(member => {
    const matchesName = member.user.name
        .toLowerCase()
        .includes(searchTerm.value.trim().toLowerCase())
    const matchesStatus =
        selectedStatuses.value.length === 0 ||
        selectedStatuses.value.includes(member.status)
    const matchesRole =
        selectedRoles.value.length === 0 ||
        selectedRoles.value.includes(member.role)
    return matchesName && matchesStatus && matchesRole
  })
}

// Инициализация и реактивное обновление
applyFilters()
watch(
    [members, searchTerm, selectedStatuses, selectedRoles],
    applyFilters
)

// Навигация по пользователю
function navigateToUserProfile(userId) {
  coordinator.navigateToUser(userId)
}

// Заглушки для обработчиков действий
async function approveMember(userId) { /* ... */ }
async function declineMember(userId) { /* ... */ }
async function cancelInvitation(userId) { /* ... */ }
async function kickMember(userId) { /* ... */ }
async function unbanMember(userId) { /* ... */ }
async function banMember(userId) { /* ... */ }
async function changeRole(userId, newRole) { /* ... */ }
</script>
