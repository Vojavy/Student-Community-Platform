<template>
  <div>
    <UserProfilePersonalView v-if="isOwner" />
    <UserProfilePublicView v-else />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { getUserIdFromToken } from '@/utils/jwt/getUserIdFromToken'

import UserProfilePersonalView from '@/views/authorized/User/UserProfilePersonalView.vue'
import UserProfilePublicView from '@/views/authorized/User/UserProfilePublicView.vue'

const route = useRoute()
const tokenUserId = getUserIdFromToken()
const routeUserId = computed(() => route.params.id)

const isOwner = computed(() => String(tokenUserId) === String(routeUserId.value))
</script>
