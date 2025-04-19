<template>
  <div class="p-6 max-w-4xl mx-auto bg-secondary rounded-lg shadow text-text">
    <h1 class="text-2xl font-bold mb-6">{{ t('profile.settings.title') }}</h1>

    <div class="flex border-b border-accent-primary mb-4">
      <button
          v-for="tab in tabs"
          :key="tab.key"
          @click="currentTab = tab.key"
          :class="[
          'px-4 py-2 mr-2 rounded-t',
          currentTab === tab.key
            ? 'bg-accent-primary text-white shadow'
            : 'bg-primary text-text hover:bg-accent-primary/10'
        ]"
      >
        {{ t(tab.label) }}
      </button>
    </div>

    <div>
      <component :is="tabComponents[currentTab]" />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'


import UserSettingsSecurity from "@/components/settings/UserSettingsSecurity.vue";
import UserSettingsContacts from "@/components/settings/UserSettingsContacts.vue";
import UserSettingsPersonal from "@/components/settings/UserSettingsPersonal.vue";
import UserSettingsAbout from "@/components/settings/UserSettingsAbout.vue";

const { t } = useI18n()

const currentTab = ref('security')

const tabs = [
  { key: 'security', label: 'profile.settings.tabs.security' },
  { key: 'contacts', label: 'profile.settings.tabs.contacts' },
  { key: 'personal', label: 'profile.settings.tabs.personal' },
  { key: 'about', label: 'profile.settings.tabs.about' }
]

const tabComponents = {
  security: UserSettingsSecurity,
  contacts: UserSettingsContacts,
  personal: UserSettingsPersonal,
  about: UserSettingsAbout
}
</script>
