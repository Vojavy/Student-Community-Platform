// src/iam/stores/forumStore.js
import { defineStore }   from 'pinia'
import createForumModel  from '@/iam/models/forumModel.js'

const forumModel = createForumModel()

export const useForumStore = defineStore('forumStore', {
    state: () => ({
        forums: {
            content: [],
            page: 0,
            size: 25,
            totalPages: 1,
            totalElements: 0
        },
        currentForum: null,

        posts: {
            content: [],
            page: 0,
            size: 20,
            totalPages: 1,
            totalElements: 0
        },
        post: null,

        loading: false,
        error:   null
    }),

    actions: {
        _parseTopics(raw) {
            if (Array.isArray(raw)) return raw
            try {
                const parsed = JSON.parse(raw)
                return Array.isArray(parsed) ? parsed : []
            } catch {
                return []
            }
        },

        // === forums ===
        async fetchForums({ page = 0, size = 25, isPublic = null, domain = null,
                              status = null, name = '', topics = [], sort = 'newest' } = {}) {
            this.loading = true
            this.error   = null
            try {
                const resp = await forumModel.fetchForums({
                    page, size, isPublic, domain, status, name, topics, sort
                })
                const content = resp.content.map(f => ({
                    ...f,
                    topics: this._parseTopics(f.topics)
                }))
                this.forums = {
                    content,
                    page: resp.page ?? page,
                    size: resp.size ?? size,
                    totalPages: resp.totalPages,
                    totalElements: resp.totalElements ?? content.length
                }
                return this.forums
            } catch (e) {
                this.error = e.response?.data?.message || e.message
                console.error('[forumStore] fetchForums error →', this.error)
                throw e
            } finally {
                this.loading = false
            }
        },

        async fetchForum(forumId) {
            this.loading = true
            this.error   = null
            try {
                const raw = await forumModel.fetchForum(forumId)
                this.currentForum = {
                    ...raw,
                    topics: this._parseTopics(raw.topics)
                }
                return this.currentForum
            } catch (e) {
                this.error = e.response?.data?.message || e.message
                console.error('[forumStore] fetchForum error →', this.error)
                throw e
            } finally {
                this.loading = false
            }
        },

        async createForum(forumData) {
            this.loading = true
            this.error   = null
            try {
                const raw = await forumModel.createForum(forumData)
                const created = {
                    ...raw,
                    topics: this._parseTopics(raw.topics)
                }
                this.forums.content.unshift(created)
                this.forums.totalElements++
                return created
            } catch (e) {
                this.error = e.response?.data?.message || e.message
                console.error('[forumStore] createForum error →', this.error)
                throw e
            } finally {
                this.loading = false
            }
        },

        async updateForum(forumId, forumData) {
            this.loading = true
            this.error   = null
            try {
                const raw = await forumModel.updateForum(forumId, forumData)
                const updated = {
                    ...raw,
                    topics: this._parseTopics(raw.topics)
                }
                if (this.currentForum?.id === forumId) {
                    this.currentForum = { ...this.currentForum, ...updated }
                }
                return updated
            } catch (e) {
                this.error = e.response?.data?.message || e.message
                console.error('[forumStore] updateForum error →', this.error)
                throw e
            } finally {
                this.loading = false
            }
        },

        async deleteForum(forumId) {
            this.loading = true
            this.error   = null
            try {
                await forumModel.deleteForum(forumId)
                this.forums.content = this.forums.content.filter(f => f.id !== forumId)
                this.forums.totalElements--
                if (this.currentForum?.id === forumId) {
                    this.currentForum = null
                }
            } catch (e) {
                this.error = e.response?.data?.message || e.message
                console.error('[forumStore] deleteForum error →', this.error)
                throw e
            } finally {
                this.loading = false
            }
        },


        // === forum posts ===
        async fetchForumPosts(forumId, page = 0, size = 20, parentPostId = null) {
            this.error   = null
            try {
                const resp = await forumModel.fetchForumPosts(forumId, page, size, parentPostId)
                const content = resp.content.map(p => ({
                    ...p,
                    topics: this._parseTopics(p.topics)
                }))
                this.posts = {
                    content,
                    page: resp.page ?? page,
                    size: resp.size ?? size,
                    totalPages: resp.totalPages,
                    totalElements: resp.totalElements ?? content.length
                }
                return this.posts
            } catch (e) {
                this.error = e.response?.data?.message || e.message
                console.error('[forumStore] fetchForumPosts error →', this.error)
                throw e
            }
        },

        async createForumPost(forumId, postData) {
            this.error   = null
            try {
                const raw = await forumModel.createForumPost(forumId, postData)
                const created = {
                    ...raw,
                    topics: this._parseTopics(raw.topics)
                }
                this.posts.content.unshift(created)
                this.posts.totalElements++
                return created
            } catch (e) {
                this.error = e.response?.data?.message || e.message
                console.error('[forumStore] createForumPost error →', this.error)
                throw e
            }
        },

        async updateForumPost(forumId, postId, postData) {
            this.error   = null
            try {
                const raw = await forumModel.updateForumPost(forumId, postId, postData)
                const updated = {
                    ...raw,
                    topics: this._parseTopics(raw.topics)
                }
                this.posts.content = this.posts.content.map(p =>
                    p.id === postId ? updated : p
                )
                return updated
            } catch (e) {
                this.error = e.response?.data?.message || e.message
                console.error('[forumStore] updateForumPost error →', this.error)
                throw e
            }
        },

        async deleteForumPost(forumId, postId) {
            this.error   = null
            try {
                await forumModel.deleteForumPost(forumId, postId)
                this.posts.content = this.posts.content.filter(p => p.id !== postId)
                this.posts.totalElements--
            } catch (e) {
                this.error = e.response?.data?.message || e.message
                console.error('[forumStore] deleteForumPost error →', this.error)
                throw e
            }
        }
    }
})
