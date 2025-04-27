<template>
  <div class="border bg-secondary rounded-lg overflow-hidden">

    <!-- Toolbar -->
    <div class="flex flex-wrap gap-2 bg-gray-100 p-2 rounded-t-lg">
      <button @click.prevent="exec('bold')" class="px-2 py-1 border rounded hover:bg-gray-200">
        {{ t('posts.bold') }}
      </button>
      <button @click.prevent="exec('italic')" class="px-2 py-1 border rounded hover:bg-gray-200">
        {{ t('posts.italic') }}
      </button>
      <button @click.prevent="exec('underline')" class="px-2 py-1 border rounded hover:bg-gray-200">
        {{ t('posts.underline') }}
      </button>
      <button @click.prevent="exec('strikeThrough')" class="px-2 py-1 border rounded hover:bg-gray-200">
        {{ t('posts.strikethrough') }}
      </button>
      <button @click.prevent="execBlock('H1')" class="px-2 py-1 border rounded hover:bg-gray-200">
        {{ t('posts.h1') }}
      </button>
      <button @click.prevent="execBlock('H2')" class="px-2 py-1 border rounded hover:bg-gray-200">
        {{ t('posts.h2') }}
      </button>
      <button @click.prevent="insertLink" class="px-2 py-1 border rounded hover:bg-gray-200">
        {{ t('posts.insertLink') }}
      </button>
      <button @click.prevent="triggerImage" class="px-2 py-1 border rounded hover:bg-gray-200">
        {{ t('posts.insertImage') }}
      </button>
    </div>

    <!-- Editor -->
    <div
        ref="editor"
        contenteditable="true"
        :placeholder="placeholderText"
        class="prose max-w-none border-t p-4 min-h-[120px] overflow-auto rounded-b-lg bg-white"
    ></div>
    <input
        ref="fileInput"
        type="file"
        accept="image/*"
        class="hidden"
        @change="onImageChange"
    />

    <!-- Actions -->
    <div class="flex justify-end gap-2 p-2 bg-secondary">
      <button @click="$emit('cancel')" class="px-4 py-2 border rounded hover:bg-gray-100 transition">
        {{ t('common.cancel') }}
      </button>
      <button @click="submit" class="px-4 py-2 bg-accent-primary text-white rounded hover:bg-accent-primary/90 transition">
        {{ t('common.submit') }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import DOMPurify from 'dompurify'

// Props (не вызываем t здесь!)
const props = defineProps({
  parentPostId: { type: Number, default: null },
  placeholder:  { type: String, default: '' }
})
const emit = defineEmits(['submit', 'cancel'])

const { t } = useI18n()

// вычисляем placeholder
const placeholderText = computed(() =>
    props.placeholder || t('forum.detail.newPostPlaceholder')
)

// Refs
const editor    = ref(null)
const fileInput = ref(null)

// Exec commands
function exec(cmd) {
  document.execCommand(cmd, false)
}
function execBlock(tag) {
  document.execCommand('formatBlock', false, `<${tag}>`)
}
function insertLink() {
  const url = prompt(t('posts.linkPrompt'), 'https://')
  if (url) document.execCommand('createLink', false, url)
}
function triggerImage() {
  fileInput.value.click()
}
function onImageChange(e) {
  const f = e.target.files[0]
  if (!f) return
  const reader = new FileReader()
  reader.onload = () => document.execCommand('insertImage', false, reader.result)
  reader.readAsDataURL(f)
}

// Submit
function submit() {
  const raw   = editor.value.innerHTML
  const clean = DOMPurify.sanitize(raw, { USE_PROFILES: { html: true } })
  emit('submit', { content: clean, parentPostId: props.parentPostId })
}
</script>

<style scoped>
.prose img { max-width: 100%; height: auto; }
/* Для плацхолдера contenteditable можно добавить, например: */
[contenteditable]:empty:before {
  content: attr(placeholder);
  color: #9ca3af; /* tailwind-gray-400 */
}
</style>
