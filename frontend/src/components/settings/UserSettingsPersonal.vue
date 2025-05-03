<template>
  <div class="space-y-6">
    <h2 class="text-xl font-semibold">{{ t('profile.settings.tabs.personal') }}</h2>

    <div v-for="f in fields" :key="f.key" class="space-y-2">
      <label class="block font-medium">{{ t(f.label) }}</label>

      <div v-if="!editing[f.key]" class="flex items-center gap-4">
        <span class="flex-1 text-text/60">{{ display(f.key) || t('profile.settings.empty') }}</span>
        <button
            class="px-3 py-1 border rounded border-accent-primary text-accent-primary hover:bg-accent-primary/10"
            @click="start(f.key)"
        >
          {{ display(f.key) ? t('common.change') : t('common.add') }}
        </button>
        <button
            v-if="display(f.key)"
            class="px-3 py-1 border rounded border-red-600 text-red-600 hover:bg-red-600/10"
            @click="remove(f.key)"
        >
          {{ t('common.delete') }}
        </button>
      </div>

      <div v-else class="flex flex-col gap-2">
        <input
            v-if="f.type==='date'"
            type="date"
            v-model="local[f.key]"
            ref="refs[f.key]"
            class="w-full p-2 border rounded bg-primary text-text"
        />
        <select
            v-else-if="f.type==='multiselect'"
            v-model="local[f.key]"
            ref="refs[f.key]"
            multiple
            class="w-full p-2 border rounded bg-primary text-text h-32"
        >
          <option v-for="o in f.options" :key="o.value" :value="o.value">{{ o.label }}</option>
        </select>
        <input
            v-else
            type="text"
            v-model="local[f.key]"
            ref="refs[f.key]"
            class="w-full p-2 border rounded bg-primary text-text"
        />

        <div class="flex gap-2">
          <button
              class="px-4 py-1 bg-accent-primary text-white rounded hover:bg-accent-primary/90"
              @click="save(f.key)"
          >
            {{ t('common.save') }}
          </button>
          <button
              class="px-4 py-1 bg-gray-300 text-gray-700 rounded hover:bg-gray-400"
              @click="cancel(f.key)"
          >
            {{ t('common.cancel') }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, watch, nextTick } from 'vue'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
const props = defineProps({ profile: Object })
const emit  = defineEmits(['update-details'])

const fields = [
  { key:'birthDate', label:'profile.personal.birthDate', type:'date' },
  { key:'languages', label:'profile.personal.languages', type:'multiselect', options:[
      {value:'en', label:'English'}, {value:'ru', label:'Русский'}, {value:'cs', label:'Čeština'}
    ]
  },
  { key:'location', label:'profile.personal.location', type:'text' },
  { key:'website',  label:'profile.personal.website',  type:'text' }
]

const local   = reactive({})
const editing = reactive({})
const refs    = reactive({})

watch(
    () => props.profile.details,
    details => {
      if (!details) return
      fields.forEach(f => {
        if (f.key === 'languages') {
          local.languages = Array.isArray(details.languages) ? [...details.languages] : []
        } else {
          local[f.key] = details[f.key] || ''
        }
        editing[f.key] = false
        refs[f.key]    = null
      })
    },
    { immediate: true }
)

const display = key => {
  const v = props.profile.details?.[key]
  if (key === 'languages') return Array.isArray(v) ? v.join(', ') : ''
  return v || ''
}

function start(key) {
  editing[key] = true
  nextTick(() => refs[key]?.focus?.())
}
function cancel(key) {
  const v = props.profile.details?.[key]
  local[key] = key==='languages'
      ? (Array.isArray(v) ? [...v] : [])
      : (v || '')
  editing[key] = false
}
function remove(key) {
  emit('update-details', { [key]: key==='languages' ? [] : '' })
}
function save(key) {
  const val = key === 'languages' ? local.languages : local[key]
  emit('update-details', { [key]: val })
  editing[key] = false
}
</script>
