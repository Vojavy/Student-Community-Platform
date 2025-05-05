<template>
  <UserProfilePersonalView v-if="isOwner" />
  <UserProfilePublicView  v-else />
</template>

<script setup>
import { computed }        from 'vue'
import { useRoute }       from 'vue-router'
import { getUserIdFromToken } from '@/utils/jwt/getUserIdFromToken'

import UserProfilePersonalView from './UserProfilePersonalView.vue'
import UserProfilePublicView   from './UserProfilePublicView.vue'

const route        = useRoute()
const currentUser = getUserIdFromToken()
const viewedUser  = computed(() => route.params.id)

const isOwner = computed(() =>
    String(currentUser) === String(viewedUser.value)
)
</script>
