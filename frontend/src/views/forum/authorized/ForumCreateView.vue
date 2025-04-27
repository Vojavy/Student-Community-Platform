<!-- src/views/forum/authorized/ForumCreateView.vue -->
<template>
  <div class="max-w-3xl mx-auto p-6 space-y-6">
    <h1 class="text-2xl font-semibold">{{ t('forum.create.title') }}</h1>

    <!-- Name -->
    <div>
      <label class="block mb-1 font-medium">{{ t('forum.create.name') }}</label>
      <input
          v-model="form.name"
          type="text"
          class="w-full p-2 border rounded"
          :placeholder="t('forum.create.namePlaceholder')"
      />
    </div>

    <!-- Description -->
    <div>
      <label class="block mb-1 font-medium">{{ t('forum.create.description') }}</label>
      <textarea
          v-model="form.description"
          class="w-full p-2 border rounded h-32"
          :placeholder="t('forum.create.descriptionPlaceholder')"
      ></textarea>
    </div>

    <!-- Topics -->
    <div>
      <label class="block mb-1 font-medium">{{ t('forum.create.topics') }}</label>
      <input
          v-model="topicsInput"
          type="text"
          class="w-full p-2 border rounded"
          :placeholder="t('forum.create.topicsPlaceholder')"
      />
      <p class="mt-1 text-xs text-text/60">{{ t('forum.create.topicsHelper') }}</p>
    </div>

    <!-- Domain (optional) -->
    <div>
      <label class="block mb-1 font-medium">{{ t('forum.create.domain') }}</label>
      <select v-model="form.universityDomain" class="w-full p-2 border rounded">
        <option value="">{{ t('forum.create.domainNone') }}</option>
        <option v-for="d in domains" :key="d.id" :value="d.domain">
          {{ d.domainName }} ({{ d.domain }})
        </option>
      </select>
    </div>

    <!-- Informational (admin only) -->
    <div v-if="isAdmin" class="flex items-center space-x-2">
      <input type="checkbox" id="informational" v-model="form.informational" />
      <label for="informational">{{ t('forum.create.informational') }}</label>
    </div>

    <!-- Public / Private -->
    <div class="flex items-center space-x-2">
      <input type="checkbox" id="public" v-model="form.public" />
      <label for="public">{{ t('forum.create.public') }}</label>
    </div>

    <!-- Pinned (admin only) -->
    <div v-if="isAdmin" class="flex items-center space-x-2">
      <input type="checkbox" id="pinned" v-model="form.pinned" />
      <label for="pinned">{{ t('forum.create.pinned') }}</label>
    </div>

    <!-- Minimum allowed role (admin only) -->
    <div v-if="isAdmin">
      <label class="block mb-1 font-medium">{{ t('forum.create.minAllowedRole') }}</label>
      <select v-model="minRoleId" class="w-full p-2 border rounded">
        <option disabled value="">{{ t('forum.create.roleNone') }}</option>
        <option v-for="r in sortedRoles" :key="r.id" :value="r.id">
          {{ r.name }}
        </option>
      </select>
      <p class="mt-1 text-xs text-text/60">{{ t('forum.create.minAllowedRoleHelper') }}</p>

      <!-- Display allowed-up-to role names -->
      <div v-if="allowedRoleNames.length" class="mt-2 text-sm">
        <strong>{{ t('forum.create.allowedUpTo') }}:</strong>
        <span
            v-for="name in allowedRoleNames"
            :key="name"
            class="ml-2 px-1 py-0.5 bg-gray-200 rounded"
        >
          {{ name }}
        </span>
      </div>
    </div>

    <!-- Actions -->
    <div class="flex justify-end gap-2">
      <button @click="cancel" class="px-4 py-2 border rounded">
        {{ t('common.cancel') }}
      </button>
      <button
          @click="submit"
          class="px-4 py-2 bg-accent-primary text-white rounded hover:bg-accent-primary/90 transition"
      >
        {{ t('forum.create.submit') }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useI18n }                from 'vue-i18n'
import { inject }                 from 'vue'

import { createForumIntent }      from '@/iam/intents/forumIntents.js'
import { handleForumIntent }      from '@/iam/actions/forumActions.js'
import createForumModel           from '@/iam/models/forumModel.js'

import { fetchDomainsIntent }     from '@/iam/intents/domainIntents.js'
import { handleDomainIntent }     from '@/iam/actions/domainActions.js'
import createDomainModel          from '@/iam/models/domainModel.js'

import { fetchRolesIntent }       from '@/iam/intents/roleIntents.js'
import { handleRoleIntent }       from '@/iam/actions/roleActions.js'
import createRoleModel            from '@/iam/models/roleModel.js'

import { getUserIdFromToken }     from '@/utils/jwt/getUserIdFromToken.js'

const { t }       = useI18n()
const coord       = inject('coordinator')
const role        = inject('user_roles')

// Form state
const form = ref({
  name:             '',
  description:      '',
  topics:           [],
  universityDomain: '',
  informational:    false,
  public:           true,
  pinned:           false,
  allowedRoles:   []
})

// Helpers
const topicsInput = ref('')
const domains     = ref([])
const roles       = ref([])
const minRoleId   = ref('')  // none selected by default

// Permissions
const isAuth  = computed(() => !!getUserIdFromToken())
const isAdmin = computed(() => role.value.some(r => r.name === 'ROLE_ADMIN'))

// Models
const forumModel  = createForumModel()
const domainModel = createDomainModel()
const roleModel   = createRoleModel()

onMounted(async () => {
  // Load domains
  domains.value = await handleDomainIntent(
      fetchDomainsIntent(),
      { model: domainModel, coordinator: coord }
  )
  // Load roles if admin
  if (isAdmin.value) {
    roles.value = await handleRoleIntent(
        fetchRolesIntent(),
        { model: roleModel }
    )
  }
})

// Sort roles by id Ascending
const sortedRoles = computed(() =>
    [...roles.value].sort((a, b) => a.id - b.id)
)

// Compute allowedUpTo role names
const allowedRoleNames = computed(() => {
  if (!minRoleId.value) return []
  const idx = sortedRoles.value.findIndex(r => r.id === +minRoleId.value)
  return idx < 0
      ? []
      : sortedRoles.value.slice(idx).map(r => r.name)
})

function cancel() {
  coord.navigateToForum('')  // adjust to your search route name
}

async function submit() {
  try {
    // Собираем темы
    const topics = topicsInput.value
        .split(',')
        .map(s => s.trim())
        .filter(Boolean)

    // Находим индекс минимальной роли
    let allowedRoleNames = []
    if (isAdmin.value && minRoleId.value) {
      const idx = sortedRoles.value.findIndex(r => r.id === +minRoleId.value)
      if (idx !== -1) {
        allowedRoleNames = sortedRoles.value
            .slice(idx)
            .map(r => r.name) // <-- тут теперь берем r.name вместо r.id
      }
    }

    // Формируем чистый объект
    const payload = {
      name: form.value.name.trim(),
      description: form.value.description.trim(),
      topics,
      universityDomain: form.value.universityDomain || null,
      public: form.value.public,
      pinned: isAdmin.value ? form.value.pinned : false,
      closed: false,
      status: form.value.informational ? 'informational' : 'active',
      allowedRoles: allowedRoleNames.length ? allowedRoleNames : undefined
    }

    const created = await handleForumIntent(
        createForumIntent(payload),
        { model: forumModel, coordinator: coord }
    )
    coord.navigateToForum(created.id)
  } catch (e) {
    console.error('Create forum failed', e)
    alert(t('errors.generic'))
  }
}
</script>

<style scoped>
.prose img {
  max-width: 100%;
  height: auto;
}
</style>
