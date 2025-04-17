<template>
  <div class="fixed bottom-0 w-full bg-secondary border-t border-gray-300 flex justify-around p-2 text-sm text-text">
    <button class="flex flex-col items-center">
      <span>🏠</span>
      <span>{{ t('navBar.mobile.index') }}</span>
    </button>
    <button class="flex flex-col items-center">
      <span>🛍️</span>
      <span>{{ t('navBar.mobile.market') }}</span>
    </button>
    <button class="flex flex-col items-center">
      <span>💬</span>
      <span>{{ t('navBar.mobile.forum') }}</span>
    </button>
    <button class="flex flex-col items-center">
      <span>👥</span>
      <span>{{ t('navBar.mobile.groups') }}</span>
    </button>
    <button class="flex flex-col items-center">
      <span>📩</span>
      <span>{{ t('navBar.mobile.messages') }}</span>
    </button>
  </div>
</template>

<script setup>
import { useI18n } from "vue-i18n"
const { t } = useI18n()
</script>
