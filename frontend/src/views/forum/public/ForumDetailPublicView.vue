<template>
  <div class="space-y-6">
    <h1 class="text-2xl font-bold">{{ forum?.name }}</h1>
    <div class="flex flex-wrap gap-2">
      <span
          v-for="topic in forum.topics"
          :key="topic"
          class="text-xs bg-gray-200 px-2 py-0.5 rounded-full"
      >
        {{ topic }}
      </span>
    </div>
    <div class="prose" v-html="forum.description"></div>

    <div class="text-sm text-gray-500 space-x-4">
      <span>
        <strong>{{ t('forum.detail.createdBy') }}:</strong>
        {{ forum.createdBy.displayName }}
      </span>
      <span>
        <strong>{{ t('forum.detail.createdAt') }}:</strong>
        {{ formatDate(forum.createdAt) }}
      </span>
      <span>
        <strong>{{ t('forum.detail.status') }}:</strong>
        {{ t(`forum.status.${forum.status}`) }}
      </span>
    </div>

    <div v-if="isAuth && !forum.closed" class="border-t pt-4">
      <h2 class="font-semibold mb-2">{{ t('forum.detail.newPostTitle') }}</h2>
      <textarea
          v-model="newPost.content"
          rows="4"
          class="w-full p-2 border rounded"
          :placeholder="t('forum.detail.newPostPlaceholder')"
      />
      <div v-if="newPost.parentPostId" class="text-xs text-gray-600 mt-1">
        {{ t('forum.detail.replyingTo') }}
        <a
            :href="'#post-' + newPost.parentPostId"
            class="text-accent-secondary underline"
        >
          {{ newPost.parentPostId }}
        </a>
        <button @click="cancelReply" class="ml-2 text-red-500 text-xs">
          {{ t('common.cancel') }}
        </button>
      </div>
      <button
          @click="submitPost"
          class="mt-2 px-4 py-2 bg-accent-primary text-white rounded hover:bg-accent-primary/90 transition"
      >
        {{ t('common.submit') }}
      </button>
    </div>

    <div class="space-y-4 mt-6">
      <div
          v-for="post in forumPosts"
          :key="post.id"
          :id="'post-' + post.id"
          class="p-4 bg-secondary rounded-lg"
          :class="{ 'ml-8': post.parentPostId }"
      >
        <div class="flex justify-between items-start">
          <div>
            <p class="whitespace-pre-wrap" v-html="post.content" />
            <div class="text-xs text-gray-400 mt-2">
              {{ post.author.displayName }}, {{ formatDateTime(post.createdAt) }}
            </div>
          </div>
          <button
              v-if="isAuth"
              @click="startReply(post.id)"
              class="text-sm text-accent-primary underline"
          >
            {{ t('forum.detail.reply') }}
          </button>
        </div>
        <div v-if="post.parentPostId" class="text-xs mt-2">
          <RouterLink
              :to="{ name:'forum-info-public', params:{ forumId }, hash:'#post-'+post.parentPostId }"
              class="underline text-accent-secondary"
          >
            {{ t('forum.detail.replyToPost') }} {{ post.parentPostId }}
          </RouterLink>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, RouterLink }    from 'vue-router'
import { useI18n }                 from 'vue-i18n'
import { useForumStore }           from '@/iam/stores/forumStore.js'
import { getUserIdFromToken }      from '@/utils/jwt/getUserIdFromToken.js'

const { t }          = useI18n()
const route         = useRoute()
const forumId       = Number(route.params.forumId || route.params.id)
const forumStore    = useForumStore()
const forum         = computed(() => forumStore.currentForum)
const forumPosts    = ref([])
const isAuth        = computed(() => !!getUserIdFromToken())

const newPost       = ref({ content: '', parentPostId: null })

async function loadForum() {
  await forumStore.fetchForum(forumId)
}

async function loadPosts() {
  const resp = await forumStore.fetchForumPosts(forumId)
  forumPosts.value = resp.content
}

function formatDate(s) {
  return new Date(s).toLocaleDateString()
}
function formatDateTime(s) {
  return new Date(s).toLocaleString()
}

function startReply(id) {
  newPost.value.parentPostId = id
}
function cancelReply() {
  newPost.value.parentPostId = null
}

async function submitPost() {
  if (!newPost.value.content.trim()) return
  await forumStore.createForumPost(forumId, newPost.value)
  newPost.value.content      = ''
  newPost.value.parentPostId = null
  await loadPosts()
}

onMounted(async () => {
  await loadForum()
  await loadPosts()
})
</script>

<style scoped>
.ml-8 { margin-left: 2rem; }
</style>
