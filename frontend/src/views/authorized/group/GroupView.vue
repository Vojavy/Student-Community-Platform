<!-- src/views/authorized/Group/GroupView.vue -->
<template>
  <div class="p-6 bg-secondary rounded-lg shadow space-y-6">
    <!-- Header + Actions -->
    <div class="flex justify-between items-center">
      <h2 class="text-2xl font-semibold">{{ group.name }}</h2>
      <div class="space-x-2">
        <button
            v-if="isOwner"
            @click="onDelete"
            class="px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700 transition"
        >
          {{ t('groups.deleteGroup') }}
        </button>
        <button
            v-else-if="isMember"
            @click="onLeave"
            class="px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700 transition"
        >
          {{ t('groups.leave') }}
        </button>
        <button
            v-else-if="!isMember && group.isPublic"
            @click="onJoin"
            class="px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700 transition"
        >
          {{ t('groups.join') }}
        </button>
      </div>
    </div>

    <!-- Metadata -->
    <div class="flex flex-wrap gap-4 text-sm text-text/70">
      <span>{{ t('groups.createdAt') }}: {{ formattedDate }}</span>
      <span>
        {{ t('groups.domain') }}:
        <span v-if="group.domain">{{ group.domain.domainName }}</span><span v-else>—</span>
      </span>
      <span :class="group.isPublic ? 'text-green-600' : 'text-red-600'">
        {{ group.isPublic ? t('groups.public') : t('groups.private') }}
      </span>
    </div>

    <!-- Description & Tags -->
    <p class="text-text">{{ group.description }}</p>
    <div v-if="normalizedTopics.length" class="flex flex-wrap gap-2">
      <span
          v-for="tag in normalizedTopics"
          :key="tag"
          class="px-2 py-1 bg-primary rounded-full text-sm"
      >
        {{ tag }}
      </span>
    </div>

    <!-- Owner/Admins/Helpers -->
    <div class="space-y-4 text-text">
      <div>
        <strong>{{ t('groups.owner') }}:</strong> {{ group.owner.name }}
      </div>
      <div v-if="group.admins?.length">
        <strong>{{ t('groups.admins') }}:</strong>
        <span v-for="(u,i) in group.admins" :key="u.id">
          {{ u.name }}<span v-if="i+1<group.admins.length">, </span>
        </span>
      </div>
      <div v-if="group.helpers?.length">
        <strong>{{ t('groups.helpers') }}:</strong>
        <span v-for="(u,i) in group.helpers" :key="u.id">
          {{ u.name }}<span v-if="i+1<group.helpers.length">, </span>
        </span>
      </div>
    </div>

    <!-- Members List -->
    <div v-if="members.length" class="space-y-1">
      <strong>{{ t('groups.members') }}:</strong>
      <ul class="list-disc list-inside ml-4">
        <li
            v-for="member in members"
            :key="member.id"
            class="flex items-center justify-between"
        >
          <button
              @click="goToUser(member.user.id)"
              class="text-accent-primary hover:underline"
          >
            {{ member.user.name }}
          </button>
          <span class="text-text/60 text-xs">
            {{ t(`groups.role.${member.role}`) }}
          </span>
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { computed }         from 'vue'
import { useI18n }          from 'vue-i18n'
import { getUserIdFromToken } from '@/utils/jwt/getUserIdFromToken'
import { inject }           from 'vue'
import { useGroupStore }    from '@/iam/stores/groupStore.js'

const { t }         = useI18n()
const coordinator    = inject('coordinator')
const store          = useGroupStore()

const group          = computed(() => store.currentGroup || {})
const members        = computed(() => store.members)
const status         = computed(() => store.memberStatus.status)

const isMember       = computed(() => status.value === 'approved')
const currentUserId  = getUserIdFromToken()
const isOwner        = computed(() => group.value.owner?.id === currentUserId)

const formattedDate  = computed(() =>
    group.value.createdAt
        ? new Date(group.value.createdAt).toLocaleDateString()
        : ''
)

const normalizedTopics = computed(() => {
  const raw = group.value.topics
  if (!raw) return []
  let out = []
  if (Array.isArray(raw)) {
    raw.forEach(item => {
      if (typeof item === 'string' && item.trim().startsWith('[')) {
        try {
          const parsed = JSON.parse(item)
          if (Array.isArray(parsed)) {
            out.push(...parsed)
            return
          }
        } catch { }
      }
      out.push(item)
    })
  } else if (typeof raw === 'string') {
    try {
      const parsed = JSON.parse(raw)
      if (Array.isArray(parsed)) return parsed
    } catch { }
    out.push(raw)
  }
  return out
})

async function onJoin() {
  await store.joinGroup(group.value.id)
  await store.fetchMemberStatus(group.value.id)
}
async function onLeave() {
  await store.leaveGroup(group.value.id)
  await store.fetchMemberStatus(group.value.id)
}
async function onDelete() {
  await store.deleteGroup(group.value.id)
  coordinator.navigateToGroups()
}
function goToUser(id) {
  coordinator.navigateToUser(id)
}
</script>
