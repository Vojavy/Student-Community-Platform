<template>
  <div class="space-y-6">
    <h2 class="text-xl font-semibold">{{ t('profile.settings.tabs.about') }}</h2>

    <div v-for="f in basicFields" :key="f.key" class="space-y-2">
      <label class="block font-medium">{{ t(f.label) }}</label>
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
      <div v-if="editing[f.key]" class="flex flex-col gap-2">
        <textarea
            v-if="f.key==='bio'"
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

    <!-- SKILLS -->
    <div class="space-y-2">
      <label class="block font-medium">{{ t('profile.settings.fields.skills') }}</label>
      <ul class="list-disc list-inside space-y-1">
        <li v-for="(skill, i) in local.skills" :key="i" class="flex gap-2">
          <span class="flex-1">{{ skill }}</span>
          <button @click="startSkill(i)" class="px-2 py-1 text-sm border rounded text-accent-primary hover:bg-accent-primary/10">
            {{ t('common.change') }}
          </button>
          <button @click="removeSkill(i)" class="px-2 py-1 text-sm border rounded text-red-600 hover:bg-red-600/10">
            {{ t('common.delete') }}
          </button>
        </li>
      </ul>
      <div class="flex gap-2 mt-2">
        <input v-model="skillValue" type="text" class="flex-1 p-2 border rounded bg-primary text-text"
               :placeholder="editingSkill!==null ? t('common.change') : t('common.add')" />
        <button @click="confirmSkill" class="px-4 py-1 bg-accent-primary text-white rounded hover:bg-accent-primary/90">
          {{ editingSkill!==null ? t('common.save') : t('common.add') }}
        </button>
        <button v-if="editingSkill!==null" @click="cancelSkill"
                class="px-4 py-1 bg-gray-300 text-gray-700 rounded hover:bg-gray-400">
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

const basicFields = [
  { key:'bio',    label:'profile.settings.fields.bio' },
  { key:'status', label:'profile.settings.fields.status' }
]

const local   = reactive({ bio:'', status:'', skills:[] })
const editing = reactive({ bio:false, status:false })
const refs    = reactive({})

watch(
    () => props.profile.details,
    details => {
      if (!details) return
      local.bio    = details.bio    || ''
      local.status = details.status || ''
      local.skills = Array.isArray(details.skills) ? [...details.skills] : []
      editing.bio    = false
      editing.status = false
    },
    { immediate: true }
)

const displayBasic = key => props.profile.details?.[key] || ''

function startBasic(key) {
  editing[key] = true
  nextTick(() => refs[key]?.focus?.())
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

const editingSkill = ref(null)
const skillValue   = ref('')

function startSkill(i) {
  editingSkill.value = i
  skillValue.value   = local.skills[i]
}
function removeSkill(i) {
  const arr = local.skills.filter((_, idx) => idx !== i)
  emit('update-details', { skills: arr })
}
function confirmSkill() {
  const v = skillValue.value.trim()
  if (!v) return
  const arr = [...local.skills]
  if (editingSkill.value !== null) arr[editingSkill.value] = v
  else arr.push(v)
  emit('update-details', { skills: arr })
  editingSkill.value = null
  skillValue.value   = ''
}
function cancelSkill() {
  editingSkill.value = null
  skillValue.value   = ''
}
</script>
