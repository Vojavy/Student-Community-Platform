import { defineStore } from 'pinia'
import createDomainModel from '@/iam/models/domainModel.js'

const model = createDomainModel()

export const useDomainStore = defineStore('domainStore', {
    state: () => ({
        domains: [],
        domain: null,
        loading: false,
        error: null
    }),
    actions: {
        async fetchDomains() {
            this.loading = true
            this.error = null
            try {
                this.domains = await model.fetchDomains()
                return this.domains
            } catch (e) {
                this.error = e.response?.data?.message || e.message || 'Fetch domains failed'
                throw e
            } finally {
                this.loading = false
            }
        },
        async fetchById(id) {
            this.loading = true
            this.error = null
            try {
                this.domain = await model.fetchDomainById(id)
                return this.domain
            } catch (e) {
                this.error = e.response?.data?.message || e.message || 'Fetch domain by ID failed'
                throw e
            } finally {
                this.loading = false
            }
        },
        async fetchByCode(domainCode) {
            this.loading = true
            this.error = null
            try {
                this.domain = await model.fetchDomainByCode(domainCode)
                return this.domain
            } catch (e) {
                this.error = e.response?.data?.message || e.message || 'Fetch domain by code failed'
                throw e
            } finally {
                this.loading = false
            }
        }
    }
})
