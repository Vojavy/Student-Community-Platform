<template>
  <div class="space-y-6 max-w-xl mx-auto">
    <h1 class="text-2xl font-semibold">{{ t('groups.settings') }}</h1>

    <form @submit.prevent="onSave" class="space-y-4">
      <!-- Название -->
      <div>
        <label class="block text-sm font-medium mb-1">{{ t('groups.name') }}</label>
        <input v-model="name" type="text" class="w-full border rounded px-3 py-2" />
      </div>

      <!-- Описание -->
      <div>
        <label class="block text-sm font-medium mb-1">{{ t('groups.description') }}</label>
        <textarea v-model="description" class="w-full border rounded px-3 py-2"></textarea>
      </div>

      <!-- Публичность -->
      <div class="flex items-center space-x-2">
        <input id="public" type="checkbox" v-model="isPublic" />
        <label for="public">{{ t('groups.public') }}</label>
      </div>

      <!-- Домен -->
      <div>
        <label class="block text-sm font-medium mb-1">{{ t('groups.domain') }}</label>
        <select v-model="selectedDomainCode" class="w-full border rounded px-3 py-2">
          <option :value="null">{{ t('groups.noDomain') }}</option>
          <option
              v-for="domainItem in domainList"
              :key="domainItem.domain"
              :value="domainItem.domain"
          >
            {{ domainItem.domainName }}
          </option>
        </select>
      </div>

      <!-- Минимальная роль для постов -->
      <div>
        <label class="block text-sm font-medium mb-1">{{ t('groups.minRoleForPosts') }}</label>
        <select v-model="minRoleForPosts" class="w-full border rounded px-3 py-2">
          <option value="member">{{ t('roles.member') }}</option>
          <option value="helper">{{ t('roles.helper') }}</option>
          <option value="admin">{{ t('roles.admin') }}</option>
          <option value="owner">{{ t('roles.owner') }}</option>
        </select>
      </div>

      <!-- Минимальная роль для событий -->
      <div>
        <label class="block text-sm font-medium mb-1">{{ t('groups.minRoleForEvents') }}</label>
        <select v-model="minRoleForEvents" class="w-full border rounded px-3 py-2">
          <option value="member">{{ t('roles.member') }}</option>
          <option value="helper">{{ t('roles.helper') }}</option>
          <option value="admin">{{ t('roles.admin') }}</option>
          <option value="owner">{{ t('roles.owner') }}</option>
        </select>
      </div>

      <!-- Тэги (простой ввод через запятую) -->
      <div>
        <label class="block text-sm font-medium mb-1">{{ t('groups.topics') }}</label>
        <input
            v-model="topicsInput"
            type="text"
            placeholder="spring, lil, wayne"
            class="w-full border rounded px-3 py-2"
        />
      </div>

      <button
          type="submit"
          class="px-4 py-2 bg-accent-primary text-white rounded hover:bg-accent-secondary transition"
      >
        {{ t('common.save') }}
      </button>
    </form>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { inject } from 'vue'
import { useI18n } from 'vue-i18n'
import checkRights from '@/utils/groups/checkRights'
import { updateGroupSettingsIntent } from '@/intents/groupIntents'
import { handleGroupIntent } from '@/actions/groupActions'
import createGroupModel from '@/models/groupModel'
import createDomainModel from '@/models/domainModel'
import { fetchDomainsIntent } from '@/intents/domainIntents'
import { handleDomainIntent } from '@/actions/domainActions'

const { t } = useI18n()
const coordinator = inject('coordinator')

// inject provided group and role
const group = inject('group')
const role = inject('role')

// form fields
const name = ref(group.value.name)
const description = ref(group.value.description)
const isPublic = ref(group.value.isPublic)

// use domain.code as select value
const selectedDomainCode = ref(group.value.domain?.domain ?? null)

const minRoleForPosts = ref(group.value.minRoleForPosts)
const minRoleForEvents = ref(group.value.minRoleForEvents)
const topicsInput = ref(
    Array.isArray(group.value.topics) ? group.value.topics.join(', ') : ''
)

// domain list for dropdown
const domainList = ref([])
const domainModel = createDomainModel()

onMounted(async () => {
  // redirect if insufficient rights
  if (!checkRights(role.value, group.value.minRoleForEvents)) {
    coordinator.navigateToGroup(group.value.id)
    return
  }
  // fetch domains
  domainList.value = await handleDomainIntent(
      fetchDomainsIntent(),
      { model: domainModel }
  )
})

// compute full domain object from selected code
const selectedDomainObject = computed(() => {
  return domainList.value.find(d => d.domain === selectedDomainCode.value) ?? null
})

async function onSave() {
  const topics = topicsInput.value
      .split(',')
      .map(s => s.trim())
      .filter(Boolean)

  const settingsData = {
    name: name.value,
    description: description.value,
    isPublic: isPublic.value,
    domain: selectedDomainObject.value?.domain ?? null,
    minRoleForPosts: minRoleForPosts.value,
    minRoleForEvents: minRoleForEvents.value,
    topics
  }

  // perform intent
  await handleGroupIntent(
      updateGroupSettingsIntent(group.value.id, settingsData),
      { model: createGroupModel() }
  )

  // update local group
  group.value = { ...group.value, ...settingsData }
}
</script>
