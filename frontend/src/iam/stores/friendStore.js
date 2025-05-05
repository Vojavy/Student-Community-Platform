import { defineStore } from 'pinia'
import createFriendModel from '@/iam/models/friendModel.js'

const friendModel = createFriendModel()

export const useFriendStore = defineStore('friendStore', {
    state: () => ({

        friends: [],
        myFriends: [],
        incomingRequests: [],

        loadingFriends: false,
        loadingMyFriends: false,
        loadingIncoming: false,

        error: null
    }),

    actions: {
        async fetchFriends(userId) {
            this.loadingFriends = true
            this.error = null
            try {
                this.friends = await friendModel.fetchFriends(userId)
                return this.friends
            } catch (e) {
                this.error = e.response?.data?.message || e.message || 'Fetch friends failed'
                console.error('[friendStore] fetchFriends →', this.error)
                throw e
            } finally {
                this.loadingFriends = false
            }
        },

        async fetchMyFriends() {
            this.loadingMyFriends = true
            this.error = null
            try {
                this.myFriends = await friendModel.fetchMyFriends()
                return this.myFriends
            } catch (e) {
                this.error = e.response?.data?.message || e.message || 'Fetch my friends failed'
                console.error('[friendStore] fetchMyFriends →', this.error)
                throw e
            } finally {
                this.loadingMyFriends = false
            }
        },

        async fetchIncomingRequests() {
            this.loadingIncoming = true
            this.error = null
            try {
                this.incomingRequests = await friendModel.fetchIncomingRequests()
                return this.incomingRequests
            } catch (e) {
                this.error = e.response?.data?.message || e.message || 'Fetch incoming requests failed'
                console.error('[friendStore] fetchIncomingRequests →', this.error)
                throw e
            } finally {
                this.loadingIncoming = false
            }
        },

        async sendRequest(targetId) {
            this.error = null
            try {
                await friendModel.sendFriendRequest(targetId)
                await this.fetchMyFriends()
            } catch (e) {
                this.error = e.response?.data?.message || e.message || 'Send friend request failed'
                console.error('[friendStore] sendRequest →', this.error)
                throw e
            }
        },

        async approveRequest(fromId) {
            this.error = null
            try {
                await friendModel.approveFriendRequest(fromId)
                await Promise.all([
                    this.fetchMyFriends(),
                    this.fetchIncomingRequests()
                ])
            } catch (e) {
                this.error = e.response?.data?.message || e.message || 'Approve request failed'
                console.error('[friendStore] approveRequest →', this.error)
                throw e
            }
        },

        async declineRequest(fromId) {
            this.error = null
            try {
                await friendModel.declineFriendRequest(fromId)
                await this.fetchIncomingRequests()
            } catch (e) {
                this.error = e.response?.data?.message || e.message || 'Decline request failed'
                console.error('[friendStore] declineRequest →', this.error)
                throw e
            }
        },

        async removeFriend(friendId) {
            this.error = null
            try {
                await friendModel.removeFriend(friendId)
                await Promise.all([
                    this.fetchMyFriends(),
                    this.fetchFriends(friendId)
                ])
            } catch (e) {
                this.error = e.response?.data?.message || e.message || 'Remove friend failed'
                console.error('[friendStore] removeFriend →', this.error)
                throw e
            }
        }
    }
})
