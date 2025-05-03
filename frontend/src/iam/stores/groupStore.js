// src/iam/stores/groupStore.js
import { defineStore }        from 'pinia'
import createGroupModel       from '@/iam/models/group/groupModel.js'
import createGroupPostModel   from '@/iam/models/group/groupPostModel.js'

const groupModel = createGroupModel()
const postModel  = createGroupPostModel()

export const useGroupStore = defineStore('groupStore', {
    state: () => ({
        // your own groups
        userGroups: {
            content: [], page: 0, size: 25, totalPages: 1, totalElements: 0
        },
        // browse/search groups
        browseGroups: {
            content: [], page: 0, size: 25, totalPages: 1, totalElements: 0
        },
        // single‐group view
        currentGroup: null,

        // membership
        members: [],                   // Array<Member>
        memberStatus: { status: 'none', role: 'member' },

        // posts in the current group
        posts: {
            content: [], page: 0, size: 20, totalPages: 1, totalElements: 0
        },
        post: null,                    // single post view

        // global loading / error
        loading: false,
        error:   null
    }),

    actions: {
        // Utility to normalize topics field, flattening JSON-encoded strings or arrays
        _parseTopics(raw) {
            if (typeof raw === 'string') {
                try {
                    const parsed = JSON.parse(raw)
                    return Array.isArray(parsed) ? parsed : []
                } catch {
                    return []
                }
            }
            if (Array.isArray(raw)) {
                const result = []
                for (const el of raw) {
                    if (typeof el === 'string' && el.trim().startsWith('[')) {
                        try {
                            const inner = JSON.parse(el)
                            if (Array.isArray(inner)) {
                                result.push(...inner)
                                continue
                            }
                        } catch {}
                    }
                    result.push(el)
                }
                return result
            }
            return []
        },

        // === groups: listing & browsing ===
        async fetchUserGroups(page = 0, size = 25) {
            this.loading = true; this.error = null
            try {
                const resp = await groupModel.fetchUserGroups(page, size)
                console.log('[groupStore] fetchUserGroups →', resp)
                const content = resp.content.map(g => ({
                    ...g,
                    topics: this._parseTopics(g.topics)
                }))
                this.userGroups = {
                    content,
                    page: resp.page ?? page,
                    size: resp.size ?? size,
                    totalPages: resp.totalPages,
                    totalElements: resp.totalElements ?? content.length
                }
                return this.userGroups
            } catch (e) {
                this.error = e.response?.data?.message || e.message
                console.error('[groupStore] fetchUserGroups error →', this.error)
                throw e
            } finally {
                this.loading = false
            }
        },

        async fetchBrowseGroups(filters = {}, page = 0, size = 25) {
            this.loading = true; this.error = null
            try {
                const resp = await groupModel.fetchBrowseGroups({ ...filters, page, size })
                console.log('[groupStore] fetchBrowseGroups →', resp)
                const content = resp.content.map(g => ({
                    ...g,
                    topics: this._parseTopics(g.topics)
                }))
                this.browseGroups = {
                    content,
                    page: resp.page ?? page,
                    size: resp.size ?? size,
                    totalPages: resp.totalPages,
                    totalElements: resp.totalElements ?? content.length
                }
                return this.browseGroups
            } catch (e) {
                this.error = e.response?.data?.message || e.message
                console.error('[groupStore] fetchBrowseGroups error →', this.error)
                throw e
            } finally {
                this.loading = false
            }
        },

        // === single‐group operations ===
        async createGroup(groupData) {
            this.loading = true; this.error = null
            try {
                const raw = await groupModel.createGroup(groupData)
                console.log('[groupStore] createGroup →', raw)
                const created = {
                    ...raw,
                    topics: this._parseTopics(raw.topics)
                }
                this.userGroups.content.unshift(created)
                this.userGroups.totalElements++
                return created
            } catch (e) {
                this.error = e.response?.data?.message || e.message
                console.error('[groupStore] createGroup error →', this.error)
                throw e
            } finally {
                this.loading = false
            }
        },

        async fetchGroup(groupId) {
            this.loading = true; this.error = null
            try {
                const raw = await groupModel.fetchGroup(groupId)
                console.log('[groupStore] fetchGroup →', raw)
                this.currentGroup = {
                    ...raw,
                    topics: this._parseTopics(raw.topics)
                }
                return this.currentGroup
            } catch (e) {
                this.error = e.response?.data?.message || e.message
                console.error('[groupStore] fetchGroup error →', this.error)
                throw e
            } finally {
                this.loading = false
            }
        },

        async updateGroupSettings(groupId, settingsData) {
            this.loading = true; this.error = null
            try {
                const updated = await groupModel.updateGroupSettings(groupId, settingsData)
                console.log('[groupStore] updateGroupSettings →', updated)
                this.currentGroup = {
                    ...this.currentGroup,
                    ...updated,
                    topics: this._parseTopics(updated.topics)
                }
                return this.currentGroup
            } catch (e) {
                this.error = e.response?.data?.message || e.message
                console.error('[groupStore] updateGroupSettings error →', this.error)
                throw e
            } finally {
                this.loading = false
            }
        },

        async deleteGroup(groupId) {
            this.loading = true; this.error = null
            try {
                await groupModel.deleteGroup(groupId)
                console.log('[groupStore] deleteGroup →', groupId)
                this.userGroups.content = this.userGroups.content.filter(g => g.id !== groupId)
                this.userGroups.totalElements--
                if (this.currentGroup?.id === groupId) this.currentGroup = null
            } catch (e) {
                this.error = e.response?.data?.message || e.message
                console.error('[groupStore] deleteGroup error →', this.error)
                throw e
            } finally {
                this.loading = false
            }
        },

        // === membership ===
        async fetchMemberStatus(groupId, targetUserId = null) {
            this.loading = true; this.error = null
            try {
                const stat = await groupModel.fetchMemberStatus(groupId, targetUserId)
                console.log('[groupStore] fetchMemberStatus →', stat)
                this.memberStatus = stat
                return stat
            } catch (e) {
                this.error = e.response?.data?.message || e.message
                console.error('[groupStore] fetchMemberStatus error →', this.error)
                throw e
            } finally {
                this.loading = false
            }
        },

        async fetchMembers(groupId, status = null) {
            this.loading = true; this.error = null
            try {
                const mems = await groupModel.fetchMembers(groupId, status)
                console.log('[groupStore] fetchMembers →', mems)
                this.members = mems
                return mems
            } catch (e) {
                this.error = e.response?.data?.message || e.message
                console.error('[groupStore] fetchMembers error →', this.error)
                throw e
            } finally {
                this.loading = false
            }
        },

        async joinGroup(groupId) {
            this.error = null
            try {
                const res = await groupModel.joinGroup(groupId)
                console.log('[groupStore] joinGroup →', res)
                return res
            } catch (e) {
                this.error = e.response?.data?.message || e.message
                console.error('[groupStore] joinGroup error →', this.error)
                throw e
            }
        },

        async leaveGroup(groupId) {
            this.error = null
            try {
                const res = await groupModel.leaveGroup(groupId)
                console.log('[groupStore] leaveGroup →', res)
                return res
            } catch (e) {
                this.error = e.response?.data?.message || e.message
                console.error('[groupStore] leaveGroup error →', this.error)
                throw e
            }
        },

        async inviteUser(groupId, targetUserId, role) {
            this.error = null
            try {
                const res = await groupModel.inviteUser(groupId, targetUserId, role)
                console.log('[groupStore] inviteUser →', res)
                return res
            } catch (e) {
                this.error = e.response?.data?.message || e.message
                console.error('[groupStore] inviteUser error →', this.error)
                throw e
            }
        },

        async processJoinRequest(groupId, targetUserId, approve) {
            this.error = null
            try {
                const res = await groupModel.processJoinRequest(groupId, targetUserId, approve)
                console.log('[groupStore] processJoinRequest →', res)
                this.members = this.members.map(m =>
                    m.user.id === targetUserId
                        ? { ...m, status: approve ? 'approved' : 'banned' }
                        : m
                )
                return res
            } catch (e) {
                this.error = e.response?.data?.message || e.message
                console.error('[groupStore] processJoinRequest error →', this.error)
                throw e
            }
        },

        async changeMemberRole(groupId, targetUserId, newRole) {
            this.error = null
            try {
                const res = await groupModel.changeMemberRole(groupId, targetUserId, newRole)
                console.log('[groupStore] changeMemberRole →', res)
                this.members = this.members.map(m =>
                    m.user.id === targetUserId ? { ...m, role: newRole } : m
                )
                return res
            } catch (e) {
                this.error = e.response?.data?.message || e.message
                console.error('[groupStore] changeMemberRole error →', this.error)
                throw e
            }
        },

        async removeMember(groupId, targetUserId) {
            this.error = null
            try {
                await groupModel.removeMember(groupId, targetUserId)
                console.log('[groupStore] removeMember →', targetUserId)
                this.members = this.members.filter(m => m.user.id !== targetUserId)
            } catch (e) {
                this.error = e.response?.data?.message || e.message
                console.error('[groupStore] removeMember error →', this.error)
                throw e
            }
        },

        async banMember(groupId, targetUserId) {
            this.error = null
            try {
                const res = await groupModel.banMember(groupId, targetUserId)
                console.log('[groupStore] banMember →', res)
                this.members = this.members.map(m =>
                    m.user.id === targetUserId ? { ...m, status: 'banned' } : m
                )
                return res
            } catch (e) {
                this.error = e.response?.data?.message || e.message
                console.error('[groupStore] banMember error →', this.error)
                throw e
            }
        },

        async unbanMember(groupId, targetUserId) {
            this.error = null
            try {
                const res = await groupModel.unbanMember(groupId, targetUserId)
                console.log('[groupStore] unbanMember →', res)
                this.members = this.members.map(m =>
                    m.user.id === targetUserId ? { ...m, status: 'approved' } : m
                )
                return res
            } catch (e) {
                this.error = e.response?.data?.message || e.message
                console.error('[groupStore] unbanMember error →', this.error)
                throw e
            }
        },

        // === posts CRUD ===
        async fetchGroupPosts(groupId, page = 0, size = 20, search = '') {
            this.loading = true; this.error = null
            try {
                const resp = await postModel.fetchPosts(groupId, page, size, search)
                console.log('[groupStore] fetchGroupPosts →', resp)
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
                console.error('[groupStore] fetchGroupPosts error →', this.error)
                throw e
            } finally {
                this.loading = false
            }
        },

        async createGroupPost(groupId, postData) {
            this.loading = true; this.error = null
            try {
                const raw = await postModel.createPost(groupId, postData)
                console.log('[groupStore] createGroupPost →', raw)
                const created = {
                    ...raw,
                    topics: this._parseTopics(raw.topics)
                }
                this.posts.content.unshift(created)
                this.posts.totalElements++
                return created
            } catch (e) {
                this.error = e.response?.data?.message || e.message
                console.error('[groupStore] createGroupPost error →', this.error)
                throw e
            } finally {
                this.loading = false
            }
        },

        async updateGroupPost(groupId, postId, postData) {
            this.loading = true; this.error = null
            try {
                const raw = await postModel.updatePost(groupId, postId, postData)
                console.log('[groupStore] updateGroupPost →', raw)
                const updated = {
                    ...raw,
                    topics: this._parseTopics(raw.topics)
                }
                this.posts.content = this.posts.content.map(p => p.id === postId ? updated : p)
                return updated
            } catch (e) {
                this.error = e.response?.data?.message || e.message
                console.error('[groupStore] updateGroupPost error →', this.error)
                throw e
            } finally {
                this.loading = false
            }
        },

        async deleteGroupPost(groupId, postId) {
            this.loading = true; this.error = null
            try {
                await postModel.deletePost(groupId, postId)
                console.log('[groupStore] deleteGroupPost →', postId)
                this.posts.content = this.posts.content.filter(p => p.id !== postId)
                this.posts.totalElements--
            } catch (e) {
                this.error = e.response?.data?.message || e.message
                console.error('[groupStore] deleteGroupPost error →', this.error)
                throw e
            } finally {
                this.loading = false
            }
        }
    }
})
