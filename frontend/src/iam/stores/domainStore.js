// src/iam/stores/domainStore.js
import { defineStore } from 'pinia'
import createDomainModel from '@/iam/models/domainModel.js'

const domainModel = createDomainModel()

export const useDomainStore = defineStore('domainStore', {
    state: () => ({
        domains: [],           // Array<{ domain, domainName, … }>
        currentDomain: null,   // single domain
        loading: false,
        error:   null
    }),
    actions: {
        async fetchDomains() {
            this.loading = true
            this.error   = null
            try {
                const data = await domainModel.fetchDomains()
                this.domains = data
                return data
            } catch (e) {
                this.error = e.response?.data?.message || e.message
                console.error('[domainStore] fetchDomains error →', this.error)
                throw e
            } finally {
                this.loading = false
            }
        },

        async fetchDomainById(id) {
            this.loading = true
            this.error   = null
            try {
                const data = await domainModel.fetchDomainById(id)
                this.currentDomain = data
                return data
            } catch (e) {
                this.error = e.response?.data?.message || e.message
                console.error('[domainStore] fetchDomainById error →', this.error)
                throw e
            } finally {
                this.loading = false
            }
        },

        async fetchDomainByCode(domainCode) {
            this.loading = true
            this.error   = null
            try {
                const data = await domainModel.fetchDomainByCode(domainCode)
                this.currentDomain = data
                return data
            } catch (e) {
                this.error = e.response?.data?.message || e.message
                console.error('[domainStore] fetchDomainByCode error →', this.error)
                throw e
            } finally {
                this.loading = false
            }
        }
    }
})
