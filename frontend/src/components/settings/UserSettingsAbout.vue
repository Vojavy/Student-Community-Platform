<template>
  <div class="space-y-6">
    <h2 class="text-xl font-semibold">{{ t('profile.settings.tabs.about') }}</h2>

    <!-- BIO и STATUS -->
    <div
        v-for="f in basicFields"
        :key="f.key"
        class="space-y-2"
    >
      <label class="block font-medium">{{ t(f.label) }}</label>

      <!-- view mode -->
      <div class="flex flex-wrap items-center gap-2">
        <span class="text-text/60 flex-1 min-w-[100px]">
          {{ displayBasic(f.key) || t('profile.settings.empty') }}
        </span>
        <button
            class="px-3 py-1 text-sm rounded border border-accent-primary text-accent-primary hover:bg-accent-primary/10 transition"
            @click="startBasic(f.key)"
        >
          {{ displayBasic(f.key) ? t('common.change') : t('common.add') }}
        </button>
        <button
            v-if="displayBasic(f.key)"
            class="px-3 py-1 text-sm rounded border border-red-600 text-red-600 hover:bg-red-600/10 transition"
            @click="removeBasic(f.key)"
        >
          {{ t('common.delete') }}
        </button>
      </div>

      <!-- edit mode -->
      <div v-if="editing[f.key]" class="flex flex-col gap-2">
        <textarea
            v-if="f.key === 'bio'"
            v-model="local[f.key]"
            ref="refs[f.key]"
            class="w-full p-2 border rounded bg-primary text-text"
            rows="4"
        />
        <input
            v-else
            type="text"
            v-model="local[f.key]"
            ref="refs[f.key]"
            class="w-full p-2 border rounded bg-primary text-text"
        />
        <div class="flex flex-wrap gap-2">
          <button
              class="px-4 py-1 bg-accent-primary text-white rounded hover:bg-accent-primary/90 transition"
              @click="saveBasic(f.key)"
          >
            {{ t('common.save') }}
          </button>
          <button
              class="px-4 py-1 bg-gray-300 text-gray-700 rounded hover:bg-gray-400 transition"
              @click="cancelBasic(f.key)"
          >
            {{ t('common.cancel') }}
          </button>
        </div>
      </div>
    </div>

    <!-- SKILLS LIST -->
    <div class="space-y-2">
      <label class="block font-medium">{{ t('profile.settings.fields.skills') }}</label>

      <!-- список навыков -->
      <ul class="list-disc list-inside space-y-1">
        <li
            v-for="(skill, idx) in local.skills"
            :key="idx"
            class="flex flex-wrap items-center gap-2"
        >
          <span class="flex-1 min-w-[100px]">{{ skill }}</span>
          <button
              class="px-2 py-1 text-sm rounded border border-accent-primary text-accent-primary hover:bg-accent-primary/10 transition"
              @click="startSkill(idx)"
          >
            {{ t('common.change') }}
          </button>
          <button
              class="px-2 py-1 text-sm rounded border border-red-600 text-red-600 hover:bg-red-600/10 transition"
              @click="removeSkill(idx)"
          >
            {{ t('common.delete') }}
          </button>
        </li>
      </ul>

      <!-- add/edit skill -->
      <div class="flex flex-wrap gap-2 mt-2">
        <input
            v-model="skillValue"
            type="text"
            class="flex-1 min-w-[100px] p-2 border rounded bg-primary text-text"
            :placeholder="editingSkill !== null ? t('common.change') : t('common.add')"
        />
        <button
            class="px-4 py-1 bg-accent-primary text-white rounded hover:bg-accent-primary/90 transition"
            @click="confirmSkill"
        >
          {{ editingSkill !== null ? t('common.save') : t('common.add') }}
        </button>
        <button
            v-if="editingSkill !== null"
            class="px-4 py-1 bg-gray-300 text-gray-700 rounded hover:bg-gray-400 transition"
            @click="cancelSkill"
        >
          {{ t('common.cancel') }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, watch, nextTick } from 'vue'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
const props = defineProps({ profile: Object })
const emit  = defineEmits(['update-details'])

// базовые поля bio + status
const basicFields = [
  { key: 'bio',    label: 'profile.settings.fields.bio' },
  { key: 'status', label: 'profile.settings.fields.status' }
]

// локальное хранилище
const local = reactive({
  bio: '',
  status: '',
  skills: []
})
// edit-флаги
const editing = reactive({
  bio:    false,
  status: false
})
// refs для фокуса
const refs = reactive({})

// при изменении входных данных синхронизируем local
watch(() => props.profile.details, d => {
  local.bio    = d.bio    || ''
  local.status = d.status || ''
  local.skills = Array.isArray(d.skills) ? [...d.skills] : []
  editing.bio    = false
  editing.status = false
}, { immediate: true })

// ——— BIO / STATUS ——————————————————————

const displayBasic = key => props.profile.details?.[key] || ''

function startBasic(key) {
  editing[key] = true
  nextTick(() => refs[key]?.focus())
}

function cancelBasic(key) {
  local[key] = props.profile.details?.[key] || ''
  editing[key] = false
}

function removeBasic(key) {
  emit('update-details', { [key]: '' })
}

function saveBasic(key) {
  emit('update-details', { [key]: local[key] })
  editing[key] = false
}

// ——— SKILLS ——————————————————————————

const editingSkill = ref(null)
const skillValue   = ref('')

function startSkill(idx) {
  editingSkill.value = idx
  skillValue.value = local.skills[idx]
}

function removeSkill(idx) {
  const arr = local.skills.filter((_, i) => i !== idx)
  emit('update-details', { skills: arr })
}

function confirmSkill() {
  const val = skillValue.value.trim()
  if (!val) return
  const arr = [...local.skills]
  if (editingSkill.value !== null) {
    arr[editingSkill.value] = val
  } else {
    arr.push(val)
  }
  emit('update-details', { skills: arr })
  editingSkill.value = null
  skillValue.value = ''
}

function cancelSkill() {
  editingSkill.value = null
  skillValue.value = ''
}
</script>
