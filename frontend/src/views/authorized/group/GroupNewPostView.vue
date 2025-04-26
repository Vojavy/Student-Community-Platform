<template>
  <div class="max-w-3xl mx-auto p-6 space-y-6">
    <h1 class="text-2xl font-semibold">{{ t('posts.newPost') }}</h1>

    <!-- Title -->
    <div>
      <label class="block mb-1 font-medium">{{ t('posts.title') }}</label>
      <input
          v-model="postTitle"
          type="text"
          class="w-full p-2 border rounded"
          :placeholder="t('posts.titlePlaceholder')"
      />
    </div>

    <!-- Topics -->
    <div>
      <label class="block mb-1 font-medium">{{ t('posts.topics') }}</label>
      <input
          v-model="topicsInput"
          type="text"
          class="w-full p-2 border rounded"
          :placeholder="t('posts.topicsPlaceholder')"
      />
    </div>

    <!-- Toolbar -->
    <div class="flex flex-wrap gap-2 bg-gray-100 p-2 rounded">
      <button @click="format('bold')" class="px-2 py-1 border rounded hover:bg-gray-200">
        {{ t('posts.bold') }}
      </button>
      <button @click="format('italic')" class="px-2 py-1 border rounded hover:bg-gray-200">
        {{ t('posts.italic') }}
      </button>
      <button @click="format('underline')" class="px-2 py-1 border rounded hover:bg-gray-200">
        {{ t('posts.underline') }}
      </button>
      <button @click="format('strikeThrough')" class="px-2 py-1 border rounded hover:bg-gray-200">
        {{ t('posts.strikethrough') }}
      </button>
      <button @click="formatBlock('H1')" class="px-2 py-1 border rounded hover:bg-gray-200">
        {{ t('posts.h1') }}
      </button>
      <button @click="formatBlock('H2')" class="px-2 py-1 border rounded hover:bg-gray-200">
        {{ t('posts.h2') }}
      </button>
      <button @click="insertLink" class="px-2 py-1 border rounded hover:bg-gray-200">
        {{ t('posts.insertLink') }}
      </button>
      <button @click="triggerImageInput" class="px-2 py-1 border rounded hover:bg-gray-200">
        {{ t('posts.insertImage') }}
      </button>
    </div>

    <!-- Editor -->
    <div
        ref="editor"
        contenteditable="true"
        class="border rounded p-4 min-h-[300px] overflow-auto prose max-w-none"
    ></div>
    <input
        ref="fileInput"
        type="file"
        accept="image/*"
        class="hidden"
        @change="onImageChange"
    />

    <!-- Actions -->
    <div class="flex justify-end gap-2">
      <button @click="cancel" class="px-4 py-2 border rounded">
        {{ t('common.cancel') }}
      </button>
      <button
          @click="submit"
          class="px-4 py-2 bg-accent-primary text-white rounded hover:bg-accent-primary/90 transition"
      >
        {{ t('posts.submit') }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useI18n }  from 'vue-i18n'
import { inject }   from 'vue'
import DOMPurify    from 'dompurify'
import checkRights  from '@/utils/groups/checkRights'

import {
  createGroupPostIntent
} from '@/iam/intents/groupIntents.js'
import { handleGroupIntent } from '@/iam/actions/groupActions.js'
import createGroupPostModel  from '@/iam/models/group/groupPostModel.js'

const { t }       = useI18n()
const coordinator = inject('coordinator')
const group       = inject('group')
const role        = inject('role')

const canPost = computed(() =>
    checkRights(role.value, group.value.minRoleForPosts)
)

onMounted(() => {
  if (!canPost.value) {
    coordinator.navigateToGroup(group.value.id)
  }
})

const postTitle   = ref('')
const topicsInput = ref('')
const editor      = ref(null)
const fileInput   = ref(null)

function format(cmd) {
  document.execCommand(cmd, false)
}
function formatBlock(tag) {
  document.execCommand('formatBlock', false, `<${tag}>`)
}
function insertLink() {
  const url = prompt(t('posts.linkPrompt'), 'https://')
  if (url) document.execCommand('createLink', false, url)
}
function triggerImageInput() {
  fileInput.value.click()
}
function onImageChange(e) {
  const file = e.target.files[0]; if (!file) return
  const reader = new FileReader()
  reader.onload = () => document.execCommand('insertImage', false, reader.result)
  reader.readAsDataURL(file)
}

function cancel() {
  coordinator.navigateToGroupPosts(group.value.id)
}

async function submit() {
  if (!canPost.value) return

  const clean   = DOMPurify.sanitize(editor.value.innerHTML, { USE_PROFILES: { html: true } })
  const topics  = topicsInput.value.split(',').map(s => s.trim()).filter(Boolean)

  try {
    await handleGroupIntent(
        createGroupPostIntent(group.value.id, {
          title:   postTitle.value,
          content: clean,
          topics
        }),
        { model: createGroupPostModel(), coordinator }
    )
    coordinator.navigateToGroupPosts(group.value.id)
  } catch (e) {
    console.error('Create post failed', e)
    alert(t('errors.generic') || 'Error')
  }
}
</script>

<style scoped>
.prose img {
  max-width: 100%;
  height: auto;
}
</style>
