<template>
  <div>
    <!-- Загрузка / ошибка / приватный -->
    <div v-if="isLoading" class="text-center py-12 text-text/70">
      ⏳ {{ translate('common.loading') }}
    </div>
    <div v-else-if="isNotFound" class="text-center py-12 text-text/70">
      {{ translate('groups.notFound') }}
    </div>
    <GroupPrivateView
        v-else-if="!isAllowed"
        :group="group"
        :status="membershipStatus.status"
        @join="handleJoin"
        @leave="handleLeave"
    />

    <!-- Основной контент -->
    <div v-else class="flex flex-col md:flex-row min-h-[80vh]">
      <!-- Боковое меню -->
      <aside class="w-full md:w-64 bg-secondary p-4">
        <SidebarMenu
            :groupId="group.id"
            :role="membershipStatus.role"
            :minRoleForPosts="group.minRoleForPosts"
            :minRoleForEvents="group.minRoleForEvents"
        />
      </aside>

      <!-- Дочерние маршруты -->
      <section class="flex-1 p-6">
        <router-view />
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, provide } from 'vue'
import { useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import createGroupModel from '@/iam/models/group/groupModel.js'
import {
  fetchGroupIntent,
  fetchMemberStatusIntent,
  fetchMembersIntent,
  joinGroupIntent,
  leaveGroupIntent
} from '@/iam/intents/groupIntents'
import { handleGroupIntent } from '@/iam/actions/groupActions'
import GroupPrivateView from './GroupPrivateView.vue'
import SidebarMenu from '@/components/group/SidebarMenu.vue'

const { t: translate } = useI18n()

const currentRoute = useRoute()
const groupModel = createGroupModel()

const groupId = Number(currentRoute.params.groupId)

const isLoading = ref(true)
const isNotFound = ref(false)
const group = ref(null)
const membershipStatus = ref({ status: 'none', role: 'member' })
const members = ref([])

onMounted(async () => {
  try {
    const fetchedGroup = await handleGroupIntent(
        fetchGroupIntent(groupId),
        { model: groupModel }
    )

    if (Array.isArray(fetchedGroup.topics)) {
      const decodedTopics = []
      for (const item of fetchedGroup.topics) {
        try {
          const parsed = JSON.parse(item)
          if (Array.isArray(parsed)) {
            decodedTopics.push(...parsed)
          }
        } catch {
        }
      }
      fetchedGroup.topics = decodedTopics
    }

    group.value = fetchedGroup

    const fetchedStatus = await handleGroupIntent(
        fetchMemberStatusIntent(groupId, null),
        { model: groupModel }
    )
    membershipStatus.value = fetchedStatus

    if (fetchedGroup.isPublic || fetchedStatus.status === 'approved') {
      members.value = await handleGroupIntent(
          fetchMembersIntent(groupId, 'approved'),
          { model: groupModel }
      )
    }
  } catch (error) {
    if (error.response?.status === 404) {
      isNotFound.value = true
    } else if (error.response?.status === 403)
    {
      group.value = error.response.data
    } else
    {
      console.error(error)
    }
    } finally {
      isLoading.value = false
    }
})

provide('group', group)
provide('members', members)
provide('role', computed(() => membershipStatus.value.role))
provide('status', computed(() => membershipStatus.value.status))

const isMember = computed(() => membershipStatus.value.status === 'approved')
const isAllowed = computed(() => group.value?.isPublic || isMember.value)

async function handleJoin() {
  await handleGroupIntent(joinGroupIntent(groupId), { model: groupModel })
  membershipStatus.value = await handleGroupIntent(
      fetchMemberStatusIntent(groupId, null),
      { model: groupModel }
  )
  if (membershipStatus.value.status === 'approved') {
    members.value = await handleGroupIntent(
        fetchMembersIntent(groupId, 'approved'),
        { model: groupModel }
    )
  }
}

async function handleLeave() {
  await handleGroupIntent(leaveGroupIntent(groupId), { model: groupModel })
  membershipStatus.value = { status: 'none', role: 'member' }
  members.value = []
}
</script>
