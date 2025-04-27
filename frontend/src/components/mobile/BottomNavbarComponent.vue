<!-- src/components/layouts/MobileNav.vue -->
<template>
  <div class="fixed bottom-0 w-full bg-secondary border-t border-gray-300 flex justify-around p-2 text-sm text-text">
    <button @click="goHome" class="flex flex-col items-center">
      <span>🏠</span>
      <span>{{ t('navBar.mobile.index') }}</span>
    </button>
    <button @click="goMarket" class="flex flex-col items-center">
      <span>🛍️</span>
      <span>{{ t('navBar.mobile.market') }}</span>
    </button>
    <button @click="goForum" class="flex flex-col items-center">
      <span>💬</span>
      <span>{{ t('navBar.mobile.forum') }}</span>
    </button>
    <button @click="goGroups" class="flex flex-col items-center">
      <span>👥</span>
      <span>{{ t('navBar.mobile.groups') }}</span>
    </button>
    <button @click="goMessages" class="flex flex-col items-center">
      <span>📩</span>
      <span>{{ t('navBar.mobile.messages') }}</span>
    </button>
  </div>
</template>

<script setup>
import { inject } from 'vue'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
const coordinator = inject('coordinator')

const goHome     = () => coordinator.navigateToHome()
const goMarket   = () => coordinator.navigateToMarket?.()
const goForum    = () => coordinator.navigateToForum()
const goGroups   = () => coordinator.navigateToGroups()
const goMessages = () => coordinator.navigateToMessages()
</script>
