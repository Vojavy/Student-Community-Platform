<template>
  <div>
    <!-- Loading -->
    <div v-if="loading" class="text-center py-12 text-text/70">
      ⏳ {{ t('common.loading') }}
    </div>

    <!-- Not found -->
    <div v-else-if="isNotFound || (!loading && !group)" class="text-center py-12 text-text/70">
      {{ t('groups.notFound') }}
    </div>

    <!-- Private (pending / banned) -->
    <GroupPrivateView
        v-else-if="group && !isAllowed"
        :group="group"
        :status="status"
        @join="handleJoin"
        @leave="handleLeave"
    />

    <!-- Main content -->
    <div v-else class="flex flex-col md:flex-row min-h-[80vh]">
      <aside class="w-full md:w-64 bg-secondary p-4">
        <SidebarMenu
            :groupId="group.id"
            :role="role"
            :minRoleForPosts="group.minRoleForPosts"
            :minRoleForEvents="group.minRoleForEvents"
        />
      </aside>
      <section class="flex-1 p-6">
        <router-view />
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useGroupStore } from '@/iam/stores/groupStore.js'
import GroupPrivateView from './GroupPrivateView.vue'
import SidebarMenu from '@/components/group/SidebarMenu.vue'

const { t }      = useI18n()
const route      = useRoute()
const store      = useGroupStore()
const isNotFound = ref(false)

const groupId = computed(() => Number(route.params.groupId))
const loading = computed(() => store.loading)
const group   = computed(() => store.currentGroup)
const status  = computed(() => store.memberStatus.status)
const role    = computed(() => store.memberStatus.role)

const isMember  = computed(() => status.value === 'approved')
const isAllowed = computed(() => group.value?.isPublic || isMember.value)

import { provide } from 'vue'
provide('group',   group)
provide('members', computed(() => store.members))
provide('role',    role)
provide('status',  status)

async function loadAll() {
  isNotFound.value = false
  try {
    await store.fetchGroup(groupId.value)
    await store.fetchMemberStatus(groupId.value, null)
    if (store.currentGroup.isPublic || isMember.value) {
      await store.fetchMembers(groupId.value, 'approved')
    }
  } catch (err) {
    if (err.response?.status === 404) {
      isNotFound.value = true
    } else {
      console.error(err)
    }
  }
}

onMounted(loadAll)
watch(groupId, loadAll)

async function handleJoin() {
  await store.joinGroup(groupId.value)
  await store.fetchMemberStatus(groupId.value, null)
  if (store.memberStatus.status === 'approved') {
    await store.fetchMembers(groupId.value, 'approved')
  }
}

async function handleLeave() {
  await store.leaveGroup(groupId.value)
  store.memberStatus = { status: 'none', role: 'member' }
  store.members = []
}
</script>
