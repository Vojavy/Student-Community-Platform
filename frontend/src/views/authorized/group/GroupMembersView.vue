<!-- src/views/authorized/Group/GroupMembersView.vue -->
<template>
  <div class="space-y-6">
    <h1 class="text-2xl font-semibold">{{ t('groups.members') }}</h1>

    <!-- поиск по имени -->
    <div>
      <label class="block text-sm font-medium mb-1">{{ t('common.search') }}</label>
      <input
          v-model.trim="searchTerm"
          type="text"
          :placeholder="t('groups.searchPlaceholder')"
          class="w-full border rounded px-3 py-2"
      />
    </div>

    <!-- фильтры -->
    <div class="flex flex-col md:flex-row gap-6">
      <!-- статус -->
      <div>
        <span class="block text-sm font-medium mb-1">{{ t('groups.filterStatus') }}</span>
        <div class="flex flex-wrap gap-2">
          <label v-for="opt in statusOptions" :key="opt" class="inline-flex items-center gap-1">
            <input
                type="checkbox"
                :value="opt"
                v-model="selectedStatuses"
                class="form-checkbox h-4 w-4"
            />
            <span class="text-sm">{{ t(`groups.status.${opt}`) }}</span>
          </label>
        </div>
      </div>

      <!-- роль -->
      <div>
        <span class="block text-sm font-medium mb-1">{{ t('groups.filterRole') }}</span>
        <div class="flex flex-wrap gap-2">
          <label v-for="opt in roleOptions" :key="opt" class="inline-flex items-center gap-1">
            <input
                type="checkbox"
                :value="opt"
                v-model="selectedRoles"
                class="form-checkbox h-4 w-4"
            />
            <span class="text-sm">{{ t(`groups.role.${opt}`) }}</span>
          </label>
        </div>
      </div>
    </div>

    <!-- список участников -->
    <ul class="space-y-4">
      <li
          v-for="m in filteredMembers"
          :key="m.id"
          class="p-4 bg-secondary rounded flex flex-col md:flex-row md:items-center justify-between"
      >
        <!-- имя + статус/роль -->
        <div>
          <button
              @click="goToUser(m.user.id)"
              class="text-lg font-medium text-accent-primary hover:underline"
          >
            {{ m.user.name }}
          </button>
          <div class="text-sm text-text/60">
            {{ t('groups.status.status') }}:
            {{ t(`groups.status.${m.status}`) }},
            {{ t('groups.role.role') }}:
            {{ t(`groups.role.${m.role}`) }}
          </div>
        </div>

        <!-- действия (только если canManage) -->
        <div
            v-if="canManage && m.role !== 'owner'"
            class="mt-4 md:mt-0 flex flex-wrap gap-2"
        >
          <!-- PENDING ---------------------------------------------------------------->
          <template v-if="m.status === 'pending'">
            <button
                @click="approve(m.user.id)"
                class="px-3 py-1 bg-green-600 text-white rounded hover:bg-green-700"
            >
              {{ t('groups.approve') }}
            </button>
            <button
                @click="decline(m.user.id)"
                class="px-3 py-1 bg-red-600 text-white rounded hover:bg-red-700"
            >
              {{ t('groups.decline') }}
            </button>
          </template>

          <!-- INVITED --------------------------------------------------------------->
          <template v-else-if="m.status === 'invited'">
            <button
                @click="cancelInvite(m.user.id)"
                class="px-3 py-1 bg-orange-600 text-white rounded hover:bg-orange-700"
            >
              {{ t('groups.cancelInvitation') }}
            </button>
          </template>

          <!-- APPROVED -------------------------------------------------------------->
          <template v-else-if="m.status === 'approved'">
            <button
                @click="kick(m.user.id)"
                class="px-3 py-1 bg-red-600 text-white rounded hover:bg-red-700"
            >
              {{ t('groups.kick') }}
            </button>
            <button
                @click="ban(m.user.id)"
                class="px-3 py-1 bg-gray-600 text-white rounded hover:bg-gray-700"
            >
              {{ t('groups.ban') }}
            </button>
            <select
                v-model="m.role"
                @change="changeRole(m.user.id, m.role)"
                class="px-3 py-1 border rounded"
            >
              <option v-for="opt in roleOptions" :key="opt" :value="opt">
                {{ t(`groups.role.${opt}`) }}
              </option>
            </select>
          </template>

          <!-- BANNED ---------------------------------------------------------------->
          <template v-else-if="m.status === 'banned'">
            <button
                @click="unban(m.user.id)"
                class="px-3 py-1 bg-blue-600 text-white rounded hover:bg-blue-700"
            >
              {{ t('groups.unban') }}
            </button>
          </template>
        </div>
      </li>

      <li
          v-if="!filteredMembers.length"
          class="italic text-center text-text/60 py-6"
      >
        {{ t('groups.emptyMembers') }}
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, inject } from 'vue'
import { useI18n } from 'vue-i18n'
import checkRights from '@/utils/groups/checkRights'
import { useGroupStore } from '@/iam/stores/groupStore'

const { t }        = useI18n()
const coordinator  = inject('coordinator')
const store        = useGroupStore()

const searchTerm       = ref('')
const selectedStatuses = ref([])
const selectedRoles    = ref([])

const statusOptions = ['approved', 'pending', 'banned']
const roleOptions   = ['member', 'helper', 'admin', 'owner', 'invited']

const members       = computed(() => store.members)
const currentGroup  = computed(() => store.currentGroup)
const memberStatus  = computed(() => store.memberStatus)

const canManage = computed(() =>
    checkRights(memberStatus.value?.role, currentGroup.value?.minRoleForEvents)
)

const filteredMembers = computed(() => {
  if (!members.value) return []
  const term = searchTerm.value.trim().toLowerCase()

  return members.value.filter(m => {
    const byName   = !term || (m.user.name || '').toLowerCase().includes(term)
    const byStatus = !selectedStatuses.value.length || selectedStatuses.value.includes(m.status)
    const byRole   = !selectedRoles.value.length   || selectedRoles.value.includes(m.role)
    return byName && byStatus && byRole
  })
})

onMounted(async () => {
  if (!members.value.length && currentGroup.value?.id) {
    await store.fetchMembers(currentGroup.value.id)
  }
})

const goToUser = id => coordinator.navigateToUser(id)

const reload = () => store.fetchMembers(currentGroup.value.id)

const approve      = async id => { await store.processJoinRequest(currentGroup.value.id, id, true);  await reload() }
const decline      = async id => { await store.processJoinRequest(currentGroup.value.id, id, false); await reload() }
const cancelInvite = async id => { await store.removeMember       (currentGroup.value.id, id);       await reload() }
const kick         = async id => { await store.removeMember       (currentGroup.value.id, id);       await reload() }
const ban          = async id => { await store.banMember          (currentGroup.value.id, id);       await reload() }
const unban        = async id => { await store.unbanMember        (currentGroup.value.id, id);       await reload() }
const changeRole   = async (id, r) => { await store.changeMemberRole(currentGroup.value.id, id, r);  await reload() }
</script>
