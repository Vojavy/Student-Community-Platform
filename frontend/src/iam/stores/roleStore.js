import { defineStore } from 'pinia'
import createRoleModel from '@/iam/models/roleModel.js'

const roleModel = createRoleModel()

export const useRoleStore = defineStore('roleStore', {
    state: () => ({
        roles: [],      // Array<{ name, … }>
        loading: false,
        error:   null
    }),
    actions: {
        async fetchRoles() {
            this.loading = true
            this.error   = null
            try {
                const data = await roleModel.fetchRoles()
                this.roles = data
                return data
            } catch (e) {
                this.error = e.response?.data?.message || e.message
                console.error('[roleStore] fetchRoles error →', this.error)
                throw e
            } finally {
                this.loading = false
            }
        }
    }
})
