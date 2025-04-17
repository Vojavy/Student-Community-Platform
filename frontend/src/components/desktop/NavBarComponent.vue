<template>
  <nav class="h-full w-1/3 p-4 bg-secondary text-text">
    <ul class="space-y-4">
      <li><a href="#" class="hover:text-accent-primary">{{ t('navBar.index') }}</a></li>
      <li><a href="#" class="hover:text-accent-primary">{{ t('navBar.forum') }}</a></li>
      <li><a href="#" class="hover:text-accent-primary">{{ t('navBar.actions') }}</a></li>
      <li><button @click="goProfile" class="hover:text-accent-primary">{{ t('navBar.profile') }}</button></li>
      <li><a href="#" class="hover:text-accent-primary">{{ t('navBar.market') }}</a></li>
      <li><a href="#" class="hover:text-accent-primary">{{ t('navBar.calendar') }}</a></li>
      <li><a href="#" class="hover:text-accent-primary">{{ t('navBar.messages') }}</a></li>
      <li><a href="#" class="hover:text-accent-primary">{{ t('navBar.groups') }}</a></li>
    </ul>
  </nav>
</template>

<script setup>
import { useI18n } from "vue-i18n";
import {inject} from "vue";
const coordinator = inject("coordinator")

const { t } = useI18n()

const goProfile = async () => {
  coordinator.navigateToUser()
}
</script>
