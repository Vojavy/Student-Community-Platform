<template>
  <div class="space-y-6">
    <h1 class="text-2xl font-semibold">{{ t('groups.members') }}</h1>


    <div class="flex-1">
      <label class="block text-sm font-medium mb-1">
        {{ t('common.search') }}
      </label>
      <input
          v-model="searchTerm"
          @input="applyFilters"
          type="text"
          placeholder="Name..."
          class="w-full border rounded px-3 py-2"
      />
    </div>
    <!-- Search & Filters -->
    <div class="flex flex-col md:flex-row md:items-start md:space-x-4 space-y-4 md:space-y-0">
      <!-- Поиск по имени -->


      <!-- Фильтр по статусам -->
      <div>
        <span class="block text-sm font-medium mb-1">
          {{ t('groups.filterStatus') }}
        </span>
        <div class="flex flex-wrap gap-2">
          <label
              v-for="statusOption in statusOptions"
              :key="statusOption"
              class="inline-flex items-center space-x-1"
          >
            <input
                type="checkbox"
                :value="statusOption"
                v-model="selectedStatuses"
                @change="applyFilters"
                class="form-checkbox h-4 w-4"
            />
            <span class="text-sm">
              {{ t(`groups.status.${statusOption}`) }}
            </span>
          </label>
        </div>
      </div>

      <!-- Фильтр по ролям -->
      <div>
        <span class="block text-sm font-medium mb-1">
          {{ t('groups.filterRole') }}
        </span>
        <div class="flex flex-wrap gap-2">
          <label
              v-for="roleOption in roleOptions"
              :key="roleOption"
              class="inline-flex items-center space-x-1"
          >
            <input
                type="checkbox"
                :value="roleOption"
                v-model="selectedRoles"
                @change="applyFilters"
                class="form-checkbox h-4 w-4"
            />
            <span class="text-sm">
              {{ t(`groups.role.${roleOption}`) }}
            </span>
          </label>
        </div>
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
          <button
              @click="navigateToUserProfile(member.user.id)"
              class="text-lg font-medium text-accent-primary hover:underline"
          >
            {{ member.user.name }}
          </button>
          <div class="text-sm text-text/60">
            {{ t('groups.status.status') }}:
            {{ t(`groups.status.${member.status}`) }},
            {{ t('groups.role.role') }}:
            {{ t(`groups.role.${member.role}`) }}
          </div>
        </div>

        <div
            v-if="canManage && member.role !== 'owner'"
            class="mt-4 md:mt-0 flex flex-wrap gap-2"
        >
          <!-- Pending -->
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

          <!-- Invited -->
          <template v-else-if="member.status === 'invited'">
            <button
                @click="cancelInvitation(member.user.id)"
                class="px-3 py-1 bg-orange-600 text-white rounded hover:bg-orange-700"
            >
              {{ t('groups.cancelInvitation') }}
            </button>
          </template>

          <!-- Approved -->
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
                {{ t(`groups.role.${roleOption}`) }}
              </option>
            </select>
          </template>

          <!-- Banned -->
          <template v-else-if="member.status === 'banned'">
            <button
                @click="unbanMember(member.user.id)"
                class="px-3 py-1 bg-blue-600 text-white rounded hover:bg-blue-700"
            >
              {{ t('groups.unban') }}
            </button>
          </template>
        </div>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, inject } from 'vue'
import { useI18n } from 'vue-i18n'
import checkRights from '@/utils/groups/checkRights'

// Intents & Actions
import {
  fetchMembersIntent,
  changeMemberRoleIntent,
  banMemberIntent,
  unbanMemberIntent,
  kickMemberIntent,
  apply_pendingJoinRequestsIntent,
  decline_pendingJoinRequestsIntent
} from '@/iam/intents/groupIntents.js'
import { handleGroupIntent } from '@/iam/actions/groupActions.js'
import createGroupModel from "@/iam/models/group/groupModel.js";

const { t } = useI18n()

// Из wrapper-а
const group = inject('group')
const coordinator = inject('coordinator')
const userRole = inject('role')

// Модель и общий диспетчер действий
const model = createGroupModel()
const actions = handleGroupIntent

// локальные реактивы
const members = ref([])
const filteredMembers = ref([])

const searchTerm = ref('')
const selectedStatuses = ref([])
const selectedRoles = ref([])

const statusOptions = ['approved', 'pending', 'banned']
const roleOptions = ['member', 'helper', 'admin', 'owner', 'invited']

// проверка прав менеджмента
const canManage = computed(() =>
    checkRights(userRole.value, group.value.minRoleForEvents)
)

// загрузка списка
async function loadMembers() {
  const data = await actions(
      fetchMembersIntent(group.value.id, null),
      { model, coordinator }
  )
  members.value = data
  applyFilters()
}

// фильтрация
function applyFilters() {
  const term = searchTerm.value.trim().toLowerCase()
  filteredMembers.value = members.value.filter(m => {
    const name = m.user.name.toLowerCase()
    const okName   = !term || name.includes(term)
    const okStatus = selectedStatuses.value.length === 0
        || selectedStatuses.value.includes(m.status)
    const okRole   = selectedRoles.value.length === 0
        || selectedRoles.value.includes(m.role)
    return okName && okStatus && okRole
  })
}

// при монтировании подгружаем
onMounted(loadMembers)

// реагируем на изменения фильтров
watch(
    [members, searchTerm, selectedStatuses, selectedRoles],
    applyFilters
)

// навигация
function navigateToUserProfile(userId) {
  coordinator.navigateToUser(userId)
}

// === обработчики действий ===
async function approveMember(userId) {
  await actions(
      apply_pendingJoinRequestsIntent(group.value.id, userId),
      { model, coordinator }
  )
  await loadMembers()
}

async function declineMember(userId) {
  await actions(
      decline_pendingJoinRequestsIntent(group.value.id, userId),
      { model, coordinator }
  )
  await loadMembers()
}

async function cancelInvitation(userId) {
  await actions(
      cancelInvitationIntent(group.value.id, userId),
      { model, coordinator }
  )
  await loadMembers()
}

async function kickMember(userId) {
  await actions(
      kickMemberIntent(group.value.id, userId),
      { model, coordinator }
  )
  await loadMembers()
}

async function banMember(userId) {
  await actions(
      banMemberIntent(group.value.id, userId),
      { model, coordinator }
  )
  await loadMembers()
}

async function unbanMember(userId) {
  await actions(
      unbanMemberIntent(group.value.id, userId),
      { model, coordinator }
  )
  await loadMembers()
}

async function changeRole(userId, newRole) {
  await actions(
      changeMemberRoleIntent(group.value.id, userId, newRole),
      { model, coordinator }
  )
  await loadMembers()
}
</script>
