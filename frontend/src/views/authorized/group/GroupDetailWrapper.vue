<template>
  <div>
    <!-- загрузка -->
    <div v-if="isLoading" class="text-center py-12 text-text/70">
      ⏳ {{ t('common.loading') }}
    </div>

    <!-- группа не найдена -->
    <div v-else-if="notFound" class="text-center py-12 text-text/70">
      {{ t('groups.notFound') }}
    </div>

    <!-- приватный вид -->
    <GroupPrivateView
        v-else-if="!isAllowed"
        :group="group"
        :status="membershipStatus"
        @join="onJoin"
        @leave="onLeave"
    />

    <!-- публичный / участник -->
    <GroupView
        v-else
        :group="group"
        :members="members"
        :isMember="isMember"
        @leave="onLeave"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute }       from 'vue-router'
import { useI18n }        from 'vue-i18n'

import createGroupModel       from '@/models/groupModel'
import {
  fetchGroupIntent,
  fetchMemberStatusIntent,
  fetchMembersIntent,
  joinGroupIntent,
  removeMemberIntent
} from '@/intents/groupIntents'
import { handleGroupIntent }  from '@/actions/groupActions'

import GroupView        from './GroupView.vue'
import GroupPrivateView from './GroupPrivateView.vue'

const { t }  = useI18n()
const route  = useRoute()
const model  = createGroupModel()

const groupId          = Number(route.params.groupId)
const isLoading        = ref(true)
const notFound         = ref(false)
const group            = ref(null)
const membershipStatus = ref('none')
const members          = ref([])

onMounted(async () => {
  try {
    // 1. fetch group
    const resp = await handleGroupIntent(
        fetchGroupIntent(groupId),
        { model }
    )
    // распарсим topics
    if (typeof resp.topics === 'string') {
      resp.topics = JSON.parse(resp.topics)
    }
    group.value = resp

    // 2. fetch status
    membershipStatus.value = await handleGroupIntent(
        fetchMemberStatusIntent(groupId, null),
        { model }
    )

    // 3. если можно — загрузить участников
    if (group.value.public || membershipStatus.value === 'approved') {
      const list = await handleGroupIntent(
          fetchMembersIntent(groupId, 'approved'),
          { model }
      )
      members.value = list
    }
  }
  catch (err) {
    if (err.response?.status === 404) {
      notFound.value = true
    } else {
      console.error(err)
    }
  }
  finally {
    isLoading.value = false
  }
})

const isMember  = computed(() => membershipStatus.value === 'approved')
const isAllowed = computed(() => group.value?.public || isMember.value)

async function onJoin() {
  await handleGroupIntent(joinGroupIntent(groupId), { model })
  // обновить статус
  membershipStatus.value = await handleGroupIntent(
      fetchMemberStatusIntent(groupId),
      { model }
  )
  // если сразу approved — подгрузить участников
  if (membershipStatus.value === 'approved') {
    members.value = await handleGroupIntent(
        fetchMembersIntent(groupId, 'approved'),
        { model }
    )
  }
}

async function onLeave() {
  await handleGroupIntent(removeMemberIntent(groupId), { model })
  membershipStatus.value = 'none'
  members.value = []
}
</script>
