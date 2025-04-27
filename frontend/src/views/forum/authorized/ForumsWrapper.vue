<template>
  <div>
    <nav class="tabs">
      <RouterLink
          to="/app/forum"
          class="tab"
          exact-active-class="router-link-active"
      >
        {{ t('forum.tabs.search') }}
      </RouterLink>

      <RouterLink
          v-if="isAuth"
          to="/app/forum/following"
          class="tab"
          exact-active-class="router-link-active"
      >
        {{ t('forum.tabs.following') }}
      </RouterLink>

      <RouterLink
          to="/app/forum/info"
          class="tab"
          exact-active-class="router-link-active"
      >
        {{ t('forum.tabs.info') }}
      </RouterLink>

      <RouterLink
          v-if="isAuth"
          to="/app/forum/archived"
          class="tab"
          exact-active-class="router-link-active"
      >
        {{ t('forum.tabs.archived') }}
      </RouterLink>

      <RouterLink
          v-if="isAdmin"
          to="/app/forum/banned"
          class="tab"
          exact-active-class="router-link-active"
      >
        {{ t('forum.tabs.banned') }}
      </RouterLink>
    </nav>
    
    <RouterView />

    <!-- Floating Add -->
    <button
        @click="coordinator.navigateToForumCreate()"
        class="fixed right-6 bg-accent-primary text-white w-14 h-14 rounded-full shadow-lg flex items-center justify-center hover:bg-accent-primary/90 transition"
        :class="isMobile ? 'bottom-20' : 'bottom-6'"
        aria-label="Create Forum"
    >
      <svg xmlns="http://www.w3.org/2000/svg" class="w-6 h-6" fill="none"
           viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
        <path stroke-linecap="round" stroke-linejoin="round"
              d="M12 4v16m8-8H4" />
      </svg>
    </button>
  </div>
</template>

<script setup>
import { RouterLink, RouterView } from 'vue-router'
import { inject, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import checkRights from '@/utils/groups/checkRights'
import { getUserIdFromToken } from '@/utils/jwt/getUserIdFromToken.js'
import { useIsMobile } from "@/utils/device/useIsMobile.js";

const coordinator = inject('coordinator')
const role = inject('user_roles')
const isAuth  = computed(() => !!getUserIdFromToken())
const isAdmin = computed(() => role.value.some(r => r.name === 'ROLE_ADMIN'))
const { t } = useI18n()
const isMobile = useIsMobile()
</script>

<style scoped>
.tabs { display: flex; flex-wrap: wrap; gap: 1rem; margin-bottom: 1rem; }
.tab  { text-decoration: none; padding: .5rem 1rem; border-bottom: 2px solid transparent; }
.tab.router-link-active { border-color: var(--color-accent-secondary); }
</style>
