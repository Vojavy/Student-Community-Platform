<template>
  <div class="max-w-4xl mx-auto p-6 space-y-6">
    <!-- Loading -->
    <div v-if="loading" class="text-center text-text/60">
      ⏳ {{ t('common.loading') }}…
    </div>

    <div v-else-if="forum">
      <!-- 1. Forum info -->
      <div class="space-y-3">
        <h1 class="text-3xl font-bold">{{ forum.name }}</h1>
        <div class="flex flex-wrap gap-2">
          <span
              v-for="topic in forum.topics"
              :key="topic"
              class="text-xs bg-gray-200 p-1 rounded-full"
          >{{ topic }}</span>
        </div>
        <div class="prose" v-html="forum.description" />
        <div class="text-sm text-gray-500 flex flex-wrap gap-4">
          <div>
            <strong>{{ t('forum.detail.createdBy') }}:</strong>
            {{ forum.createdBy.displayName }}
          </div>
          <div>
            <strong>{{ t('forum.detail.createdAt') }}:</strong>
            {{ formatDate(forum.createdAt) }}
          </div>
          <div>
            <strong>{{ t('forum.detail.status') }}:</strong>
            {{ t(`forum.status.${forum.status}`) }}
          </div>
        </div>
      </div>

      <!-- Top-level reply editor -->
      <ReplyEditor
          v-if="canReply && editorState.open && editorState.parentPostId === null"
          :parent-post-id="null"
          @submit="onReply"
          @cancel="closeEditor"
      />

      <!-- Posts tree -->
      <div class="space-y-4">
        <div
            v-for="post in posts"
            :key="post.id"
            :id="`post-${post.id}`"
            class="p-4 bg-secondary rounded-lg"
            :style="{ marginLeft: `${depths[post.id] * indentRem}rem` }"
        >
          <!-- quote parent -->
          <div
              v-if="post.parentPostId"
              class="quote-blurb mb-2 p-2 bg-white/70 rounded cursor-pointer"
              @click="scrollTo(post.parentPostId)"
          >
            <div class="text-xs font-semibold">{{ t('forum.detail.inReplyTo') }}:</div>
            <div class="quote-content text-xs" v-html="postMap[post.parentPostId]?.content"></div>
          </div>

          <div class="flex justify-between items-start">
            <div>
              <p class="whitespace-pre-wrap" v-html="post.content" />
              <div class="text-xs text-gray-400 mt-2">
                {{ post.author.name }}, {{ formatDateTime(post.createdAt) }}
              </div>
            </div>
            <button
                v-if="canReply"
                @click="openEditor(post.id)"
                class="text-sm underline text-accent-secondary"
            >
              {{ t('forum.detail.reply') }}
            </button>
          </div>

          <!-- inline reply editor -->
          <ReplyEditor
              v-if="canReply && editorState.open && editorState.parentPostId === post.id"
              :parent-post-id="post.id"
              @submit="onReply"
              @cancel="closeEditor"
          />
        </div>
      </div>

      <!-- “Add message” button -->
      <div class="flex justify-end">
        <button
            v-if="canReply"
            @click="openEditor(null)"
            class="px-3 py-1 bg-accent-primary text-white rounded hover:bg-accent-primary/90 transition"
        >
          {{ t('forum.detail.addMessage') }}
        </button>
      </div>

      <!-- Admin/creator settings -->
      <div v-if="canEdit" class="border-t pt-6 space-y-6">
        <h2 class="text-xl font-semibold">{{ t('forum.detail.settingsTitle') }}</h2>

        <!-- Name -->
        <div>
          <label class="block mb-1 font-medium">{{ t('forum.create.name') }}</label>
          <input v-model="settings.name" type="text" class="w-full border p-2 rounded" />
        </div>

        <!-- Description -->
        <div>
          <label class="block mb-1 font-medium">{{ t('forum.create.description') }}</label>
          <textarea
              v-model="settings.description"
              class="w-full border p-2 rounded h-32"
          ></textarea>
        </div>

        <!-- Topics -->
        <div>
          <label class="block mb-1 font-medium">{{ t('forum.create.topics') }}</label>
          <input
              v-model="settings.topicsInput"
              type="text"
              class="w-full border p-2 rounded"
          />
          <p class="mt-1 text-xs text-text/60">{{ t('forum.create.topicsHelper') }}</p>
        </div>

        <!-- Domain -->
        <div>
          <label class="block mb-1 font-medium">{{ t('forum.create.domain') }}</label>
          <select v-model="settings.universityDomain" class="w-full border p-2 rounded">
            <option value="">{{ t('forum.create.domainNone') }}</option>
            <option
                v-for="d in domains"
                :key="d.domain"
                :value="d.domain"
            >
              {{ d.domainName }} ({{ d.domain }})
            </option>
          </select>
        </div>

        <!-- Informational -->
        <div v-if="isAdmin" class="flex items-center space-x-2">
          <input type="checkbox" id="informational" v-model="settings.informational" />
          <label for="informational">{{ t('forum.create.informational') }}</label>
        </div>

        <!-- Public -->
        <div v-if="!onlyUnverified" class="flex items-center space-x-2">
          <input type="checkbox" id="public" v-model="settings.public" />
          <label for="public">{{ t('forum.create.public') }}</label>
        </div>

        <!-- Pinned -->
        <div v-if="isAdmin" class="flex items-center space-x-2">
          <input type="checkbox" id="pinned" v-model="settings.pinned" />
          <label for="pinned">{{ t('forum.create.pinned') }}</label>
        </div>

        <!-- Min allowed role -->
        <div v-if="isAdmin">
          <label class="block mb-1 font-medium">{{ t('forum.create.minAllowedRole') }}</label>
          <select v-model="minRoleName" class="w-full border p-2 rounded">
            <option disabled value="">{{ t('forum.create.roleNone') }}</option>
            <option
                v-for="r in sortedRoles"
                :key="r.name"
                :value="r.name"
            >{{ r.name }}</option>
          </select>
          <p class="mt-1 text-xs text-text/60">{{ t('forum.create.minAllowedRoleHelper') }}</p>
          <div v-if="allowedRoleNames.length" class="mt-2 text-sm">
            <strong>{{ t('forum.create.allowedUpTo') }}:</strong>
            <span
                v-for="name in allowedRoleNames"
                :key="name"
                class="ml-2 px-1 py-0.5 bg-gray-200 rounded"
            >{{ name }}</span>
          </div>
        </div>

        <!-- Actions -->
        <div class="flex flex-col sm:flex-row sm:flex-wrap gap-2 justify-end">
          <button
              v-if="isCreatorOrAdmin && !settings.closed"
              @click="closeForum"
              class="px-3 py-1 bg-yellow-600 text-white rounded"
          >{{ t('forum.detail.closeForum') }}</button>
          <button
              v-if="isCreatorOrAdmin && settings.status !== 'archived'"
              @click="archiveForum"
              class="px-3 py-1 bg-gray-600 text-white rounded"
          >{{ t('forum.detail.archiveForum') }}</button>
          <button
              v-if="isCreatorOrAdmin && settings.status !== 'resolved'"
              @click="resolveForum"
              class="px-3 py-1 bg-green-600 text-white rounded"
          >{{ t('forum.detail.resolveForum') }}</button>
          <button
              v-if="isAdmin && settings.status !== 'banned'"
              @click="banForum"
              class="px-3 py-1 bg-red-600 text-white rounded"
          >{{ t('forum.detail.banForum') }}</button>
          <button
              v-if="isCreatorOrAdmin"
              @click="deleteForum"
              class="px-3 py-1 bg-error text-white rounded"
          >{{ t('forum.detail.deleteForum') }}</button>
          <button
              @click="saveSettings"
              class="px-3 py-1 bg-accent-primary text-white rounded"
          >{{ t('common.save') }}</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, inject } from 'vue'
import { useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { getUserIdFromToken } from '@/utils/jwt/getUserIdFromToken.js'
import { useForumStore } from '@/iam/stores/forumStore.js'
import { useDomainStore } from '@/iam/stores/domainStore.js'
import { useRoleStore } from '@/iam/stores/roleStore.js'
import ReplyEditor from '@/components/forum/ReplyEditor.vue'

const { t }       = useI18n()
const route       = useRoute()
const coordinator = inject('coordinator')

const forumStore  = useForumStore()
const domainStore = useDomainStore()
const roleStore   = useRoleStore()

const forumId     = Number(route.params.id)
const loading     = computed(() => forumStore.loading)
const forum       = computed(() => forumStore.currentForum)
const posts       = ref([])

const indentRem = 0.75
const postMap   = computed(() =>
    posts.value.reduce((m, p) => ((m[p.id] = p), m), {})
)
const depths    = computed(() => {
  const d = {}
  function calc(p) {
    if (d[p.id] != null) return d[p.id]
    d[p.id] = p.parentPostId
        ? calc(postMap.value[p.parentPostId] || { parentPostId: null }) + 1
        : 0
    return d[p.id]
  }
  posts.value.forEach(calc)
  return d
})

const settings    = reactive({
  name:            '',
  description:     '',
  topicsInput:     '',
  universityDomain:'',
  informational:   false,
  public:          true,
  pinned:          false,
  closed:          false,
  status:          ''
})
const minRoleName = ref('')

const editorState = ref({ open: false, parentPostId: null })
function openEditor(pid) { editorState.value = { open: true, parentPostId: pid } }
function closeEditor()    { editorState.value = { open: false, parentPostId: null } }

async function loadAux() {
  await domainStore.fetchDomains()
  await roleStore.fetchRoles()
}

const userRoles        = computed(() => roleStore.roles)
const isAdmin          = computed(() => userRoles.value.some(r => r.name === 'ROLE_ADMIN'))
const isCreator        = computed(() => forum.value?.createdBy.id === getUserIdFromToken())
const isCreatorOrAdmin = computed(() => isAdmin.value || isCreator.value)
const canEdit          = computed(() => !!forum.value && isCreatorOrAdmin.value)

const isAuth   = computed(() => !!getUserIdFromToken())
const canReply = computed(() =>
    isAuth.value && forum.value && !forum.value.closed
)

const onlyUnverified   = computed(() =>
    userRoles.value.length === 1 && userRoles.value[0].name === 'ROLE_UNVERIFIED'
)

const sortedRoles      = computed(() => [...userRoles.value].sort((a,b) => a.id - b.id))
const allowedRoleNames = computed(() => {
  const idx = sortedRoles.value.findIndex(r => r.name === minRoleName.value)
  return idx < 0 ? [] : sortedRoles.value.slice(idx).map(r => r.name)
})

const formatDate     = s => new Date(s).toLocaleDateString()
const formatDateTime = s => new Date(s).toLocaleString()

async function loadForum() {
  await forumStore.fetchForum(forumId)
  const f = forumStore.currentForum
  Object.assign(settings, {
    name:            f.name,
    description:     f.description,
    topicsInput:     f.topics.join(','),
    universityDomain:f.universityDomain.domain,
    informational:   f.status === 'informational',
    public:          f.public,
    pinned:          f.pinned,
    closed:          f.closed,
    status:          f.status
  })
  minRoleName.value = f.allowedRoles?.[0] || ''
}
async function loadPosts() {
  const resp = await forumStore.fetchForumPosts(forumId)
  posts.value = resp.content
}

onMounted(async () => {
  await Promise.all([loadAux(), loadForum(), loadPosts()])
})

async function onReply({ content, parentPostId }) {
  await forumStore.createForumPost(forumId, { content, parentPostId })
  closeEditor()
  await loadPosts()
}

async function updateForumStatus(payload) {
  await forumStore.updateForum(forumId, payload)
  await loadForum()
}

async function closeForum() {
  await updateForumStatus({
    ...settings,
    topics:        settings.topicsInput.split(',').map(s => s.trim()).filter(Boolean),
    status:        'archived',
    closed:        true,
    allowedRoles:  allowedRoleNames.value
  })
}
async function archiveForum() {
  await updateForumStatus({
    ...settings,
    topics:       settings.topicsInput.split(',').map(s => s.trim()).filter(Boolean),
    status:       'archived',
    allowedRoles: allowedRoleNames.value
  })
}
async function resolveForum() {
  await updateForumStatus({
    ...settings,
    topics:       settings.topicsInput.split(',').map(s => s.trim()).filter(Boolean),
    status:       'resolved',
    allowedRoles: allowedRoleNames.value
  })
}
async function banForum() {
  await updateForumStatus({
    ...settings,
    topics:       settings.topicsInput.split(',').map(s => s.trim()).filter(Boolean),
    status:       'banned',
    closed:       true,
    allowedRoles: allowedRoleNames.value
  })
}
async function saveSettings() {
  await updateForumStatus({
    name:            settings.name.trim(),
    description:     settings.description.trim(),
    topics:          settings.topicsInput.split(',').map(s => s.trim()).filter(Boolean),
    universityDomain:settings.universityDomain,
    status:          settings.informational ? 'informational' : settings.status,
    public:          settings.public,
    pinned:          settings.pinned,
    closed:          settings.closed,
    allowedRoles:    allowedRoleNames.value
  })
}
async function deleteForum() {
  if (!confirm(t('forum.detail.confirmDelete'))) return
  await forumStore.deleteForum(forumId)
  coordinator.navigateToForumSearch()
}

// scroll to quote
function scrollTo(id) {
  const el = document.getElementById(`post-${id}`)
  if (el) {
    el.scrollIntoView({ behavior: 'smooth', block: 'center' })
    el.classList.add('ring-2','ring-accent-primary')
    setTimeout(() => el.classList.remove('ring-2','ring-accent-primary'), 2000)
  }
}
</script>

<style scoped>
.prose img { max-width: 100%; height: auto; }
.quote-blurb {
  position: relative;
  max-height: 100px;
  overflow: hidden;
}
.quote-blurb::after {
  content: '';
  position: absolute;
  bottom: 0; left: 0;
  width: 100%; height: 25px;
  background: linear-gradient(rgba(255,255,255,0), rgba(255,255,255,0.7));
}
</style>
