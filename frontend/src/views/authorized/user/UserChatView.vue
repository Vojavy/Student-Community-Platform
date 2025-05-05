<!-- src/views/authorized/chat/ChatWrapper.vue -->
<template>
  <div class="p-6 max-w-2xl mx-auto flex flex-col h-full">
    <div v-if="isLoading" class="text-center text-gray-500">
      ⏳ {{ t('common.loading') }}
    </div>
    <div v-else-if="error" class="text-center text-red-500">
      {{ error }}
    </div>
    <div v-else class="flex flex-col h-full">
      <h2 class="text-xl font-semibold mb-4">
        {{ t('chat.title', { name: `${user.jmeno} ${user.prijmeni}` }) }}
      </h2>

      <div
          ref="messagesContainer"
          class="flex-1 overflow-auto space-y-2 mb-4"
      >
        <div
            v-for="msg in filteredMessages"
            :key="msg.id"
            :class="msg.senderId === selfId ? 'text-right' : 'text-left'"
        >
          <div
              :class="[
              'inline-block px-4 py-2 rounded-lg',
              msg.senderId === selfId
                ? 'bg-indigo-600 text-white'
                : 'bg-gray-200 text-gray-800'
            ]"
          >
            {{ msg.contentText }}
          </div>
          <div class="text-xs text-gray-400 mt-1">
            {{ formatDate(msg.createdAt) }}
          </div>
        </div>
      </div>

      <div class="flex gap-2">
        <input
            v-model="newMessage"
            type="text"
            :placeholder="t('chat.placeholder')"
            class="flex-1 p-2 border rounded-lg"
            @keyup.enter="onSend"
        />
        <button
            @click="onSend"
            class="px-4 py-2 bg-indigo-600 text-white rounded-lg"
        >
          {{ t('chat.send') }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useUserStore } from '@/iam/stores/userStore.js'
import { useChatStore } from '@/iam/stores/chatStore.js'
import { getUserIdFromToken } from '@/utils/jwt/getUserIdFromToken.js'

const { t } = useI18n()
const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const chatStore = useChatStore()

const viewedId = Number(route.params.id)
const selfId = getUserIdFromToken()

const isLoading = ref(true)
const error = ref(null)
const user = ref({})
const newMessage = ref('')
const messagesContainer = ref(null)

const filteredMessages = computed(() =>
    chatStore.messages.filter(
        m =>
            (m.senderId === selfId && m.recipientId === viewedId) ||
            (m.senderId === viewedId && m.recipientId === selfId)
    )
)

function formatDate(iso) {
  const d = new Date(iso)
  return d.toLocaleTimeString()
}

async function onSend() {
  const text = newMessage.value.trim()
  if (!text) return
  await chatStore.sendMessage({
    recipientId: viewedId,
    contentText: text,
    contentBase64: null,
    parentMessageId: null
  })
  newMessage.value = ''
}

onMounted(async () => {
  try {
    user.value = await userStore.fetchById(viewedId)
  } catch (e) {
    if (e.response?.status === 404) {
      return router.replace({ name: 'access-denied' })
    }
    error.value = e.message || t('common.error')
    return
  }

  try {
    await chatStore.connect()
    chatStore.subscribe()
    chatStore.requestHistory(viewedId)
  } catch (e) {
    error.value = e.message
  } finally {
    isLoading.value = false
  }
})

onBeforeUnmount(() => {
  chatStore.disconnect()
})
</script>


<style scoped>
.flex-1 { flex: 1; }
</style>
