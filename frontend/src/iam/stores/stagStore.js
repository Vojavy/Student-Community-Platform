import { defineStore } from 'pinia'
import createStagModel from '@/iam/models/stagModel.js'

const model = createStagModel()

export const useStagStore = defineStore('stagStore', {
    state: () => ({
        status: null,
        loading: false,
        error: null,
        studentInfo: null
    }),
    actions: {
        async checkTicket() {
            this.loading = true
            this.error = null
            try {
                this.status = await model.checkStagTicket()
                return this.status
            } catch (e) {
                this.error = e.response?.data?.message || e.message || 'Check STAG ticket failed'
                throw e
            } finally {
                this.loading = false
            }
        },
        async startLogin(domain) {
            this.error = null
            try {
                const originalUrl = window.location.origin + window.location.pathname
                await model.startStagLogin(domain, originalUrl)
            } catch (e) {
                this.error = e.message || 'STAG login failed'
                console.error('STAG login failed', e)
            }
        },
        async saveTicket(ticket, longTicket, domain) {
            this.error = null
            try {
                await model.saveStagTicket(ticket, longTicket, domain)
                window.history.replaceState({}, document.title, window.location.pathname)
            } catch (e) {
                this.error = e.message || 'Save STAG token failed'
                console.error('STAG token saving error', e)
            }
        },
        async deleteTicket() {
            this.loading = true
            this.error = null
            try {
                await model.deleteStagTicket()
                this.status = null
                window.history.replaceState({}, document.title, window.location.pathname)
            } catch (e) {
                this.error = e.message || 'Delete STAG token failed'
                console.error('STAG token deletion error', e)
            } finally {
                this.loading = false
            }
        },
        async fetchStudentInfo(domain) {
            this.loading = true
            this.error = null
            try {
                this.studentInfo = await model.fetchStudentInfo(domain)
                return this.studentInfo
            } catch (e) {
                this.error = e.response?.data?.message || e.message || 'Fetch student info failed'
                throw e
            } finally {
                this.loading = false
            }
        }
    }
})
