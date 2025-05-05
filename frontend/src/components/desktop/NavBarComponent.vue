<!-- src/components/layouts/SidebarNav.vue -->
<template>
  <nav class="h-full w-1/3 p-4 bg-secondary text-text">
    <ul class="space-y-4">
      <li>
        <button @click="goHome" class="hover:text-accent-primary">
          {{ t('navBar.index') }}
        </button>
      </li>
      <li>
        <button @click="goProfile" class="hover:text-accent-primary">
          {{ t('navBar.profile') }}
        </button>
      </li>
      <li>
        <button @click="goUserSearch" class="hover:text-accent-primary">
          {{ t('navBar.searchUsers') }}
        </button>
      </li>
      <li>
        <button @click="goForum" class="hover:text-accent-primary">
          {{ t('navBar.forum') }}
        </button>
      </li>
<!--      <li>-->
<!--        <button @click="goActions" class="hover:text-accent-primary">-->
<!--          {{ t('navBar.actions') }}-->
<!--        </button>-->
<!--      </li>-->
<!--      <li>-->
<!--        <button @click="goMarket" class="hover:text-accent-primary">-->
<!--          {{ t('navBar.market') }}-->
<!--        </button>-->
<!--      </li>-->
<!--      <li>-->
<!--        <button @click="goCalendar" class="hover:text-accent-primary">-->
<!--          {{ t('navBar.calendar') }}-->
<!--        </button>-->
<!--      </li>-->
      <li>
        <button @click="goGroups" class="hover:text-accent-primary">
          {{ t('navBar.groups') }}
        </button>
      </li>
    </ul>
  </nav>
</template>

<script setup>
import { inject } from 'vue'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
const coordinator = inject('coordinator')

const goHome       = () => coordinator.navigateToHome()
const goForum      = () => coordinator.navigateToForum()
const goActions    = () => coordinator.navigateToActions()
const goProfile    = () => coordinator.navigateToUser()
const goMarket     = () => coordinator.navigateToMarket?.()
const goCalendar   = () => coordinator.navigateToCalendar()
const goGroups     = () => coordinator.navigateToGroups()
const goUserSearch = () => coordinator.navigateToUserSearch()
</script>
