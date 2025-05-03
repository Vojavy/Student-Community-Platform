<template>
  <div class="min-h-screen bg-secondary py-4">
    <div class="mx-2 sm:mx-auto max-w-full sm:max-w-4xl bg-white rounded-xl shadow-lg overflow-hidden">
      <div class="px-4 py-3 border-b border-gray-200">
        <h1 class="text-lg sm:text-2xl font-semibold text-text">
          {{ t('profile.settings.title') }}
        </h1>
      </div>

      <div class="overflow-x-auto whitespace-nowrap bg-primary/50 sm:bg-transparent px-2 sm:px-4 py-2">
        <button
            v-for="tab in tabs"
            :key="tab.key"
            @click="currentTab = tab.key"
            :class="[
            'inline-block text-sm sm:text-base font-medium px-4 py-2 rounded-lg mr-2',
            currentTab === tab.key
              ? 'bg-accent-primary text-white shadow-md'
              : 'bg-primary text-text hover:bg-accent-primary/20'
          ]"
        >
          {{ t(tab.label) }}
        </button>
      </div>

      <div class="p-4 sm:p-6">
        <div v-if="loading" class="text-center py-12 text-gray-400">
          ⏳ {{ t('common.loading') }}
        </div>
        <div v-else>
          <component
              :is="tabComponents[currentTab]"
              :profile="profile"
              @update-user="onUpdateUser"
              @update-details="onUpdateDetails"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, inject } from 'vue'
import { useI18n } from 'vue-i18n'
import { getUserIdFromToken } from '@/utils/jwt/getUserIdFromToken'
import { useUserStore } from '@/iam/stores/userStore'

import UserSettingsSecurity from '@/components/settings/UserSettingsSecurity.vue'
import UserSettingsContacts from '@/components/settings/UserSettingsContacts.vue'
import UserSettingsPersonal from '@/components/settings/UserSettingsPersonal.vue'
import UserSettingsAbout    from '@/components/settings/UserSettingsAbout.vue'

const { t } = useI18n()
const coordinator = inject('coordinator')
const userStore   = useUserStore()

const userId = getUserIdFromToken()
const loading = computed(() => userStore.loading)
const profile = computed(() => userStore.profile)

const tabs = [
  { key: 'security', label: 'profile.settings.tabs.security' },
  { key: 'contacts', label: 'profile.settings.tabs.contacts' },
  { key: 'personal', label: 'profile.settings.tabs.personal' },
  { key: 'about',    label: 'profile.settings.tabs.about' }
]
const currentTab   = ref('security')
const tabComponents = {
  security: UserSettingsSecurity,
  contacts: UserSettingsContacts,
  personal: UserSettingsPersonal,
  about:    UserSettingsAbout
}

onMounted(() => {
  userStore.fetchProfile(userId)
})

async function onUpdateUser(updated) {
  await userStore.updateUser(userId, updated)
}
async function onUpdateDetails(partial) {
  const merged = {
    ...userStore.profile.details,
    ...partial
  }
  await userStore.updateUserDetails(userId, merged)
}
</script>
