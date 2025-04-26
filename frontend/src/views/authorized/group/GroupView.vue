<template>
  <div class="p-6 bg-secondary rounded-lg shadow space-y-6">
    <!-- Header with actions -->
    <div class="flex justify-between items-center">
      <h2 class="text-2xl font-semibold">{{ group.name }}</h2>
      <div class="space-x-2">
        <!-- Owner can delete -->
        <button
            v-if="isOwner"
            @click="onDelete"
            class="px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700 transition"
        >
          {{ t('groups.deleteGroup') }}
        </button>
        <!-- Member can leave -->
        <button
            v-else-if="isMember"
            @click="onLeave"
            class="px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700 transition"
        >
          {{ t('groups.leave') }}
        </button>
        <!-- Non-member in public group can join -->
        <button
            v-else-if="!isMember && group.isPublic"
            @click="onJoin"
            class="px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700 transition"
        >
          {{ t('groups.join') }}
        </button>
      </div>
    </div>

    <!-- Group metadata -->
    <div class="flex flex-wrap gap-4 text-sm text-text/70">
      <span>{{ t('groups.createdAt') }}: {{ formattedDate }}</span>
      <span>
        {{ t('groups.domain') }}:
        <span v-if="group.domain">{{ group.domain.domainName }}</span>
        <span v-else>—</span>
      </span>
      <span :class="group.isPublic ? 'text-green-600' : 'text-red-600'">
        {{ group.isPublic ? t('groups.public') : t('groups.private') }}
      </span>
    </div>

    <!-- Description -->
    <p class="text-text">{{ group.description }}</p>

    <!-- Tags -->
    <div v-if="Array.isArray(group.topics) && group.topics.length" class="flex flex-wrap gap-2">
      <span
          v-for="tag in group.topics"
          :key="tag"
          class="px-2 py-1 bg-primary rounded-full text-sm"
      >
        #{{ tag }}
      </span>
    </div>

    <!-- Owner, Admins, Helpers -->
    <div class="space-y-4 text-text">
      <div>
        <strong>{{ t('groups.owner') }}:</strong>
        {{ group.owner.name }}
      </div>
      <div v-if="group.admins?.length">
        <strong>{{ t('groups.admins') }}:</strong>
        <span v-for="(u, i) in group.admins" :key="u.id">
          {{ u.name }}<span v-if="i+1 < group.admins.length">, </span>
        </span>
      </div>
      <div v-if="group.helpers?.length">
        <strong>{{ t('groups.helpers') }}:</strong>
        <span v-for="(u, i) in group.helpers" :key="u.id">
          {{ u.name }}<span v-if="i+1 < group.helpers.length">, </span>
        </span>
      </div>
    </div>

    <!-- Members list -->
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
import { inject, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { getUserIdFromToken } from '@/utils/jwt/getUserIdFromToken'
import createGroupModel from '@/iam/models/group/groupModel.js'
import {
  joinGroupIntent,
  leaveGroupIntent,
  deleteGroupIntent
} from '@/iam/intents/groupIntents'
import { handleGroupIntent } from '@/iam/actions/groupActions'

const { t } = useI18n()
const coordinator = inject('coordinator')
const group = inject('group')
const members = inject('members')
const status = inject('status')

const model = createGroupModel()

const isMember = computed(() => status.value === 'approved')
const currentUserId = getUserIdFromToken()
const isOwner = computed(() => group.value.owner.id === currentUserId)

const formattedDate = computed(() =>
    group.value.createdAt
        ? new Date(group.value.createdAt).toLocaleDateString()
        : ''
)

async function onJoin() {
  await handleGroupIntent(
      joinGroupIntent(group.value.id),
      { model, coordinator }
  )
  coordinator.refreshPage()
}

async function onLeave() {
  await handleGroupIntent(
      leaveGroupIntent(group.value.id),
      { model, coordinator }
  )
  coordinator.refreshPage()
}

async function onDelete() {
  await handleGroupIntent(
      deleteGroupIntent(group.value.id),
      { model, coordinator }
  )
  coordinator.navigateToGroups()
}

function goToUser(userId) {
  coordinator.navigateToUser(userId)
}
</script>
