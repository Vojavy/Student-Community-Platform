<template>
  <div class="max-w-4xl mx-auto p-6 space-y-6">

    <!-- Ждём загрузки форума -->
    <div v-if="forum" class="space-y-6">

      <!-- 1. Информация о форуме -->
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

      <!-- 4. Редактор нового сообщения -->
      <ReplyEditor
          v-if="editorState.open && editorState.parentPostId === null"
          :parent-post-id="null"
          @submit="onReply"
          @cancel="closeEditor"
      />

      <!-- 5. Список сообщений -->
      <div class="space-y-4">
        <div
            v-for="post in posts"
            :key="post.id"
            :id="`post-${post.id}`"
            class="p-4 bg-secondary rounded-lg"
            :style="{ marginLeft: `${postDepths[post.id] * indentRem}rem` }"
        >
          <!-- цитата родителя -->
          <div
              v-if="post.parentPostId"
              class="quote-blurb mb-2 p-2 bg-white/70 rounded cursor-pointer"
              @click="scrollTo(post.parentPostId)"
          >
            <div class="text-xs font-semibold">{{ t('forum.detail.inReplyTo') }}:</div>
            <div class="quote-content text-xs" v-html="postMap[post.parentPostId]?.content"></div>
          </div>

          <!-- содержимое -->
          <div class="flex justify-between items-start">
            <div>
              <p class="whitespace-pre-wrap" v-html="post.content" />
              <div class="text-xs text-gray-400 mt-2">
                {{ post.author.name }}, {{ formatDateTime(post.createdAt) }}
              </div>
            </div>
            <button
                v-if="!settings.closed && settings.status !== 'banned'"
                @click="openEditor(post.id)"
                class="text-sm underline text-accent-secondary"
            >{{ t('forum.detail.reply') }}</button>
          </div>

          <!-- inline-редактор -->
          <ReplyEditor
              v-if="editorState.open && editorState.parentPostId === post.id"
              :parent-post-id="post.id"
              @submit="onReply"
              @cancel="closeEditor"
          />
        </div>
      </div>

      <!-- 6. Кнопка добавить сообщение внизу -->
      <div class="flex justify-end">
        <button
            v-if="!settings.closed && settings.status !== 'banned'"
            @click="openEditor(null)"
            class="px-3 py-1 bg-accent-primary text-white rounded hover:bg-accent-primary/90 transition"
        >
          {{ t('forum.detail.addMessage') }}
        </button>
      </div>

      <!-- 7. Настройки форума (админ/создатель) -->
      <div v-if="canEdit" class="border-t pt-6 space-y-6">

        <h2 class="text-xl font-semibold">{{ t('forum.detail.settingsTitle') }}</h2>

        <!-- Name -->
        <div>
          <label class="block mb-1 font-medium">{{ t('forum.create.name') }}</label>
          <input
              v-model="settings.name"
              type="text"
              class="w-full border p-2 rounded"
              :placeholder="t('forum.create.namePlaceholder')"
          />
        </div>

        <!-- Description -->
        <div>
          <label class="block mb-1 font-medium">{{ t('forum.create.description') }}</label>
          <textarea
              v-model="settings.description"
              class="w-full border p-2 rounded h-32"
              :placeholder="t('forum.create.descriptionPlaceholder')"
          ></textarea>
        </div>

        <!-- Topics -->
        <div>
          <label class="block mb-1 font-medium">{{ t('forum.create.topics') }}</label>
          <input
              v-model="settings.topicsInput"
              type="text"
              class="w-full border p-2 rounded"
              :placeholder="t('forum.create.topicsPlaceholder')"
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

        <!-- Informational (только админ) -->
        <div v-if="isAdmin" class="flex items-center space-x-2">
          <input
              type="checkbox"
              id="informational"
              v-model="settings.informational"
          />
          <label for="informational">{{ t('forum.create.informational') }}</label>
        </div>

        <!-- Public -->
        <div v-if="!isOnlyUnverified" class="flex items-center space-x-2">
          <input type="checkbox" id="public" v-model="settings.public" />
          <label for="public">{{ t('forum.create.public') }}</label>
        </div>

        <!-- Pinned (только админ) -->
        <div v-if="isAdmin" class="flex items-center space-x-2">
          <input type="checkbox" id="pinned" v-model="settings.pinned" />
          <label for="pinned">{{ t('forum.create.pinned') }}</label>
        </div>

        <!-- Минимальная роль (только админ) -->
        <div v-if="isAdmin">
          <label class="block mb-1 font-medium">{{ t('forum.create.minAllowedRole') }}</label>
          <select v-model="minRoleName" class="w-full border p-2 rounded">
            <option disabled value="">{{ t('forum.create.roleNone') }}</option>
            <option
                v-for="r in sortedRoles"
                :key="r.name"
                :value="r.name"
            >
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

        <!-- Actions: Close/Archive/Resolve/Ban/Delete/Save -->
<!--        TODO доделать кнопки-->
        <div class="flex flex-col sm:flex-row sm:flex-wrap gap-2 justify-end">
          <button
              v-if="isCreatorOrAdmin && !settings.closed"
              @click="closeForum"
              class="w-full sm:w-auto px-3 py-1 bg-yellow-600 text-white rounded hover:opacity-90 transition"
          >{{ t('forum.detail.closeForum') }}</button>

          <button
              v-if="isCreatorOrAdmin && settings.status !== 'archived'"
              @click="archiveForum"
              class="w-full sm:w-auto px-3 py-1 bg-gray-600 text-white rounded hover:opacity-90 transition"
          >{{ t('forum.detail.archiveForum') }}</button>

          <button
              v-if="isCreatorOrAdmin && settings.status !== 'resolved'"
              @click="resolveForum"
              class="w-full sm:w-auto px-3 py-1 bg-green-600 text-white rounded hover:opacity-90 transition"
          >{{ t('forum.detail.resolveForum') }}</button>

          <button
              v-if="isAdmin && settings.status !== 'banned'"
              @click="banForum"
              class="w-full sm:w-auto px-3 py-1 bg-red-600 text-white rounded hover:opacity-90 transition"
          >{{ t('forum.detail.banForum') }}</button>

          <button
              v-if="isCreatorOrAdmin"
              @click="deleteForum"
              class="w-full sm:w-auto px-3 py-1 bg-error text-white rounded hover:opacity-90 transition"
          >{{ t('forum.detail.deleteForum') }}</button>

          <button
              @click="saveSettings"
              class="w-full sm:w-auto px-3 py-1 bg-accent-primary text-white rounded hover:bg-accent-primary/90 transition"
          >{{ t('common.save') }}</button>
        </div>
      </div>
    </div>

    <!-- Загрузка -->
    <div v-else class="p-6 text-center text-gray-500">
      {{ t('common.loading') }}…
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, inject } from 'vue'
import { useRoute } from 'vue-router'
import { useI18n }  from 'vue-i18n'
import { getUserIdFromToken } from '@/utils/jwt/getUserIdFromToken.js'

import createForumModel    from '@/iam/models/forumModel.js'
import { handleForumIntent } from '@/iam/actions/forumActions.js'
import {
  fetchForumIntent,
  fetchForumPostsIntent,
  createForumPostIntent,
  updateForumIntent,
  deleteForumIntent
} from '@/iam/intents/forumIntents.js'

import createDomainModel   from '@/iam/models/domainModel.js'
import { fetchDomainsIntent } from '@/iam/intents/domainIntents.js'
import { handleDomainIntent } from '@/iam/actions/domainActions.js'

import createRoleModel     from '@/iam/models/roleModel.js'
import { fetchRolesIntent } from '@/iam/intents/roleIntents.js'
import { handleRoleIntent } from '@/iam/actions/roleActions.js'

import ReplyEditor from '@/components/forum/ReplyEditor.vue'

const { t } = useI18n()
const route = useRoute()
const forum = ref(null)
const posts = ref([])
const coord = inject('coordinator')
const forumId = Number(route.params.id)

// отступы
const indentRem  = 0.75
const postMap    = computed(() => posts.value.reduce((m, p) => (m[p.id] = p, m), {}))
const postDepths = computed(() => {
  const depths = {}
  function depthOf(p) {
    if (depths[p.id] != null) return depths[p.id]
    depths[p.id] = p.parentPostId
        ? depthOf(postMap.value[p.parentPostId]||{parentPostId:null}) + 1
        : 0
    return depths[p.id]
  }
  posts.value.forEach(depthOf)
  return depths
})

// справочники
const domains  = ref([])
const allRoles = ref([])
async function loadAux() {
  domains.value = await handleDomainIntent(fetchDomainsIntent(), {
    model: createDomainModel()
  })
  allRoles.value = await handleRoleIntent(fetchRolesIntent(), {
    model: createRoleModel()
  })
}

// права
const role       = inject('user_roles')
const isAdmin    = computed(() => role.value.some(r => r.name === 'ROLE_ADMIN'))
const isCreator  = computed(() =>
    forum.value?.createdBy.id === getUserIdFromToken()
)
const isCreatorOrAdmin = computed(() =>
    isAdmin.value || isCreator.value
)
const canEdit    = computed(() =>
    forum.value && (isAdmin.value || isCreator.value)
)

const isOnlyUnverified = computed(() => {
  return role.value.length === 1 && role.value[0].name === 'ROLE_UNVERIFIED';
})

// редактор
const editorState = ref({ open:false, parentPostId:null })
function openEditor(pid) { editorState.value = { open:true, parentPostId:pid } }
function closeEditor()    { editorState.value = { open:false, parentPostId:null } }

// форма настроек
const settings = reactive({
  name:             '',
  description:      '',
  topicsInput:      '',
  universityDomain: '',
  informational:    false,
  public:           true,
  pinned:           false
})
const minRoleName = ref('')

// иерархия ролей по id
//TODO не работает(
const sortedRoles = computed(() =>
    [...allRoles.value].sort((a, b) => a.id - b.id)
)
const allowedRoleNames = computed(() => {
  const idx = sortedRoles.value.findIndex(r => r.name === minRoleName.value)
  return idx>=0
      ? sortedRoles.value.slice(idx).map(r=>r.name)
      : []
})

// загрузка форума и постов
async function loadForum() {
  const f = await handleForumIntent(fetchForumIntent(forumId), {
    model: createForumModel()
  })
  forum.value = f
  Object.assign(settings, {
    name:             f.name,
    description:      f.description,
    topicsInput:      f.topics.join(','),
    universityDomain: f.universityDomain.domain,
    informational:    f.status==='informational',
    public:           f.public,
    pinned:           f.pinned,
    closed:           f.closed,
  })
  minRoleName.value = f.allowedRoles.length ? f.allowedRoles[0] : ''
}
async function loadPosts() {
  const resp = await handleForumIntent(fetchForumPostsIntent(forumId), {
    model: createForumModel()
  })
  posts.value = resp.content
}

onMounted(async () => {
  await Promise.all([ loadAux(), loadForum(), loadPosts() ])
})

// ответы
async function onReply({content,parentPostId}) {
  await handleForumIntent(
      createForumPostIntent(forumId,{content,parentPostId}),
      {model:createForumModel()}
  )
  closeEditor()
  await loadPosts()
}

// действия по кнопкам
async function closeForum() {
  await handleForumIntent(
      updateForumIntent(forumId,{
        name:             settings.name.trim(),
        description:      settings.description.trim(),
        topics:           settings.topicsInput.split(',').map(s=>s.trim()).filter(Boolean),
        universityDomain: settings.universityDomain,
        status:           'archived',
        public:           settings.public,
        pinned:           isAdmin.value ? settings.pinned : undefined,
        closed:           true,
        allowedRoles:     allowedRoleNames.value
      }),
      {model:createForumModel()}
  )
  settings.closed = true
}
async function archiveForum() {
  await handleForumIntent(
      updateForumIntent(forumId,{
        name:             settings.name.trim(),
        description:      settings.description.trim(),
        topics:           settings.topicsInput.split(',').map(s=>s.trim()).filter(Boolean),
        universityDomain: settings.universityDomain,
        status:           'archived',
        public:           settings.public,
        pinned:           isAdmin.value ? settings.pinned : undefined,
        closed:           settings.closed,
        allowedRoles:     allowedRoleNames.value
      }),
      {model:createForumModel()}
  )
  settings.informational = false
  forum.value.status = 'archived'
}
async function resolveForum() {
  await handleForumIntent(
      updateForumIntent(forumId,{
        name:             settings.name.trim(),
        description:      settings.description.trim(),
        topics:           settings.topicsInput.split(',').map(s=>s.trim()).filter(Boolean),
        universityDomain: settings.universityDomain,
        status:           'resolved',
        public:           settings.public,
        pinned:           isAdmin.value ? settings.pinned : undefined,
        closed:           settings.closed,
        allowedRoles:     allowedRoleNames.value
      }),
      {model:createForumModel()}
  )
  forum.value.status = 'resolved'
}
async function banForum() {
  await handleForumIntent(
      updateForumIntent(forumId,{
        name:             settings.name.trim(),
        description:      settings.description.trim(),
        topics:           settings.topicsInput.split(',').map(s=>s.trim()).filter(Boolean),
        universityDomain: settings.universityDomain,
        status:           'banned',
        public:           settings.public,
        pinned:           isAdmin.value ? settings.pinned : undefined,
        closed:           true,
        allowedRoles:     allowedRoleNames.value
      }),
      {model:createForumModel()}
  )
  forum.value.status = 'banned'
  settings.closed = true
}

// сохранение настроек
async function saveSettings() {
  await handleForumIntent(
      updateForumIntent(forumId,{
        name:             settings.name.trim(),
        description:      settings.description.trim(),
        topics:           settings.topicsInput.split(',').map(s=>s.trim()).filter(Boolean),
        universityDomain: settings.universityDomain,
        status:           settings.informational ? 'informational' : forum.value.status,
        public:           settings.public,
        pinned:           isAdmin.value ? settings.pinned : undefined,
        closed:           settings.closed,
        allowedRoles:     allowedRoleNames.value
      }),
      {model:createForumModel()}
  )
  await loadForum()
}

// удаление
async function deleteForum() {
  if (!confirm(t('forum.detail.confirmDelete'))) return
  await handleForumIntent(deleteForumIntent(forumId), {
    model: createForumModel()
  })
  coord.navigateToForumSearch()
}

// плавный скролл
function scrollTo(id) {
  const el = document.getElementById(`post-${id}`)
  if (!el) return
  el.scrollIntoView({ behavior:'smooth', block:'center' })
  el.classList.add('ring-2','ring-accent-primary')
  setTimeout(()=>el.classList.remove('ring-2','ring-accent-primary'),2000)
}

// формат дат
function formatDate(s)     { return new Date(s).toLocaleDateString() }
function formatDateTime(s) { return new Date(s).toLocaleString() }
</script>

<style scoped>
.prose img { max-width:100%; height:auto; }
.quote-blurb {
  position:relative;
  max-height:100px;
  overflow:hidden;
}
.quote-blurb::after {
  content:'';
  position:absolute;
  bottom:0; left:0;
  width:100%; height:25px;
  background:linear-gradient(rgba(255,255,255,0),rgba(255,255,255,0.7));
}
</style>