<template>
  <div class="max-w-3xl mx-auto p-6 space-y-6">
    <h1 class="text-2xl font-semibold">{{ t('forum.create.title') }}</h1>

    <div>
      <label class="block mb-1 font-medium">{{ t('forum.create.name') }}</label>
      <input
          v-model="form.name"
          type="text"
          class="w-full p-2 border rounded"
      />
    </div>

    <div>
      <label class="block mb-1 font-medium">{{ t('forum.create.description') }}</label>
      <textarea
          v-model="form.description"
          class="w-full p-2 border rounded h-32"
      ></textarea>
    </div>

    <div>
      <label class="block mb-1 font-medium">{{ t('forum.create.topics') }}</label>
      <input
          v-model="topicsInput"
          type="text"
          class="w-full p-2 border rounded"
      />
      <p class="mt-1 text-xs text-text/60">{{ t('forum.create.topicsHelper') }}</p>
    </div>

    <div>
      <label class="block mb-1 font-medium">{{ t('forum.create.domain') }}</label>
      <select v-model="form.universityDomain" class="w-full p-2 border rounded">
        <option value="">{{ t('forum.create.domainNone') }}</option>
        <option v-for="d in domains" :key="d.domain" :value="d.domain">
          {{ d.domainName }} ({{ d.domain }})
        </option>
      </select>
    </div>

    <div v-if="isAdmin" class="flex items-center space-x-2">
      <input type="checkbox" id="informational" v-model="form.informational" />
      <label for="informational">{{ t('forum.create.informational') }}</label>
    </div>

    <div class="flex items-center space-x-2">
      <input type="checkbox" id="public" v-model="form.public" />
      <label for="public">{{ t('forum.create.public') }}</label>
    </div>

    <div v-if="isAdmin" class="flex items-center space-x-2">
      <input type="checkbox" id="pinned" v-model="form.pinned" />
      <label for="pinned">{{ t('forum.create.pinned') }}</label>
    </div>

    <div v-if="isAdmin">
      <label class="block mb-1 font-medium">{{ t('forum.create.minAllowedRole') }}</label>
      <select v-model="minRoleId" class="w-full p-2 border rounded">
        <option disabled value="">{{ t('forum.create.roleNone') }}</option>
        <option v-for="r in sortedRoles" :key="r.id" :value="r.id">
          {{ r.name }}
        </option>
      </select>
      <p class="mt-1 text-xs text-text/60">{{ t('forum.create.minAllowedRoleHelper') }}</p>
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
import { ref, computed, onMounted }    from 'vue'
import { useI18n }                     from 'vue-i18n'
import { inject }                      from 'vue'
import { getUserIdFromToken }          from '@/utils/jwt/getUserIdFromToken.js'
import { useForumStore }               from '@/iam/stores/forumStore.js'
import { useDomainStore }              from '@/iam/stores/domainStore.js'
import { useRoleStore }                from '@/iam/stores/roleStore.js'

const { t }          = useI18n()
const coordinator    = inject('coordinator')
const forumStore     = useForumStore()
const domainStore    = useDomainStore()
const roleStore      = useRoleStore()

const isAuth         = computed(() => !!getUserIdFromToken())
const isAdmin        = computed(() => roleStore.roles.some(r=>r.name==='ROLE_ADMIN'))

// form
const form           = ref({
  name:             '',
  description:      '',
  universityDomain: '',
  informational:    false,
  public:           true,
  pinned:           false
})
const topicsInput    = ref('')
const domains        = ref([])
const roles          = ref([])
const minRoleId      = ref('')

onMounted(async () => {
  await domainStore.fetchDomains()
  domains.value = domainStore.domains
  if (isAdmin.value) {
    await roleStore.fetchRoles()
    roles.value = roleStore.roles
  }
})

const sortedRoles    = computed(() => [...roles.value].sort((a,b)=>a.id-b.id))
const allowedRoleNames = computed(() => {
  const idx = sortedRoles.value.findIndex(r=>r.id===+minRoleId.value)
  return idx<0 ? [] : sortedRoles.value.slice(idx).map(r=>r.name)
})

function cancel() {
  coordinator.navigateToForum('')
}

async function submit() {
  const topics = topicsInput.value
      .split(',').map(s=>s.trim()).filter(Boolean)

  const allowedRoles = isAdmin.value && minRoleId.value
      ? sortedRoles.value.slice(
          sortedRoles.value.findIndex(r=>r.id===+minRoleId.value)
      ).map(r=>r.name)
      : undefined

  const payload = {
    name:             form.value.name.trim(),
    description:      form.value.description.trim(),
    topics,
    universityDomain: form.value.universityDomain || null,
    public:           form.value.public,
    pinned:           isAdmin.value ? form.value.pinned : false,
    closed:           false,
    status:           form.value.informational ? 'informational' : 'active',
    allowedRoles
  }

  const created = await forumStore.createForum(payload)
  coordinator.navigateToForum(created.id)
}
</script>

<style scoped>
.prose img { max-width:100%; height:auto; }
</style>
