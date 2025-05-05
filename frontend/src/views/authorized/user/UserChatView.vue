<template>
  <div class="p-6 mx-auto max-w-2xl flex flex-col h-screen">
    <!-- Header -->
    <header class="flex-none mb-4">
      <h2 class="text-xl font-semibold">
        {{ t('chat.title', { name: `${user.jmeno} ${user.prijmeni}` }) }}
      </h2>
    </header>

    <!-- Messages area (80% из flex-1) -->
    <div
        ref="messagesContainer"
        class="flex-1 overflow-auto space-y-4 px-2"
    >
      <div
          v-for="msg in filteredMessages"
          :key="msg.id"
          class="flex flex-col"
          @click="markAsRead(msg)"
      >
        <!-- Bubble + optional reply-preview -->
        <div
            class="flex items-start"
            :class="msg.senderId === selfId ? 'justify-end' : 'justify-start'"
        >
          <!-- Own message: preview слева -->
          <template v-if="msg.senderId === selfId && msg.parentMessageId">
            <div
                class="inline-block max-w-xs bg-gray-100 rounded text-sm text-gray-600 px-3 py-2 mr-2 break-words"
                :class="'border-l-4 border-gray-400'"
            >
              {{ parentMap[msg.parentMessageId]?.contentText
            || t('chat.replyPreviewDeleted') }}
            </div>
          </template>

          <!-- Message bubble -->
          <div
              :class="[
              'inline-block px-4 py-2 rounded-lg max-w-xs break-words',
              msg.senderId === selfId
                ? 'bg-indigo-600 text-white'
                : 'bg-gray-200 text-gray-800'
            ]"
          >
            <template v-if="msg.contentBase64">
              <img
                  :src="`data:image/*;base64,${msg.contentBase64}`"
                  class="max-h-48 rounded-lg mb-2"
                  alt="attachment"
              />
            </template>
            <div>{{ msg.contentText }}</div>
            <button
                class="text-xs text-indigo-400 hover:underline mt-1"
                @click.stop="replyTo(msg.id)"
            >
              {{ t('chat.reply') }}
            </button>
          </div>

          <!-- Other's message: preview справа -->
          <template v-if="msg.senderId !== selfId && msg.parentMessageId">
            <div
                class="inline-block max-w-xs bg-gray-100 rounded text-sm text-gray-600 px-3 py-2 ml-2 break-words"
                :class="'border-r-4 border-gray-400'"
            >
              {{ parentMap[msg.parentMessageId]?.contentText
            || t('chat.replyPreviewDeleted') }}
            </div>
          </template>
        </div>

        <!-- Timestamp & read status -->
        <div class="text-xs text-gray-400 mt-1"
             :class="msg.senderId === selfId ? 'text-right' : 'text-left'">
          {{ formatDate(msg.createdAt) }}
          <span v-if="msg.read && msg.senderId === selfId">
            • {{ t('chat.readAt', { time: formatDate(msg.readAt) }) }}
          </span>
        </div>
      </div>
    </div>

    <!-- Input area (20%) -->
    <div class="flex-none bg-white p-4 border-t border-gray-200">
      <!-- Reply preview indicator -->
      <div v-if="replyToId" class="mb-2 text-sm text-gray-600 flex items-center">
        {{ t('chat.replyingTo') }} #{{ replyToId }}
        <button class="ml-2 text-red-500" @click="cancelReply">✖</button>
      </div>

      <div class="flex items-center gap-2">
        <input
            v-model="newMessage"
            type="text"
            :placeholder="t('chat.placeholder')"
            class="flex-1 p-2 border rounded-lg"
            @keyup.enter="onSend"
        />
        <label class="px-3 py-2 bg-gray-200 rounded-lg cursor-pointer">
          📎
          <input
              type="file"
              class="hidden"
              accept="image/*"
              @change="onFileChange"
          />
        </label>
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
import { ref, computed, watch, nextTick, onMounted, onBeforeUnmount } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useUserStore } from '@/iam/stores/userStore.js'
import { useChatStore } from '@/iam/stores/chatStore.js'
import { getUserIdFromToken } from '@/utils/jwt/getUserIdFromToken.js'

const { t } = useI18n()
const route  = useRoute()
const router = useRouter()
const userStore = useUserStore()
const chatStore = useChatStore()

const viewedId = Number(route.params.id)
const selfId   = getUserIdFromToken()

const isLoading = ref(true)
const error     = ref(null)
const user      = ref({})

const newMessage        = ref('')
const pendingImage      = ref(null)
const replyToId         = ref(null)
const messagesContainer = ref(null)

// Для цитат-ответов
const parentMap = ref({})

const filteredMessages = computed(() => {
  // собираем все сообщения в parentMap по id
  chatStore.messages.forEach(m => (parentMap.value[m.id] = m))
  // фильтруем по текущему диалогу
  return chatStore.messages.filter(
      m =>
          (m.senderId === selfId && m.recipientId === viewedId) ||
          (m.senderId === viewedId && m.recipientId === selfId)
  )
})

// автоскролл вниз при обновлении списка
watch(filteredMessages, async () => {
  await nextTick()
  const el = messagesContainer.value
  if (el) el.scrollTop = el.scrollHeight
})

function formatDate(iso) {
  return new Date(iso).toLocaleTimeString()
}

async function onSend() {
  const text = newMessage.value.trim()
  if (!text && !pendingImage.value) return

  await chatStore.sendMessage(
      viewedId,
      { text, base64: pendingImage.value, parentMessageId: replyToId.value }
  )

  newMessage.value   = ''
  pendingImage.value = null
  replyToId.value    = null
}

function onFileChange(e) {
  const file = e.target.files[0]
  if (!file) return
  const reader = new FileReader()
  reader.onload = () => {
    // берём только base64 без префикса data:image/*
    pendingImage.value = reader.result.split(',')[1]
  }
  reader.readAsDataURL(file)
}

function markAsRead(msg) {
  if (!msg.read && msg.recipientId === selfId) {
    chatStore.markAsRead(msg.id)
  }
}

function replyTo(id) {
  replyToId.value = id
}
function cancelReply() {
  replyToId.value = null
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
    chatStore.clearMessages()
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
/* Всё управление размерами и скроллом делаем через Tailwind-классы выше */
</style>
