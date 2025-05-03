import { defineStore } from 'pinia'
import createFriendModel from '@/iam/models/friendModel.js'

const model = createFriendModel()

export const useFriendStore = defineStore('friendStore', {
    state: () => ({
        friends: [],
        myFriends: [],
        incomingRequests: [],
        loading: false,
        error: null
    }),
    actions: {
        async fetchFriends(userId) {
            this.loading = true
            this.error = null
            try {
                this.friends = await model.fetchFriends(userId)
                return this.friends
            } catch (e) {
                this.error = e.response?.data?.message || e.message || 'Fetch friends failed'
                throw e
            } finally {
                this.loading = false
            }
        },
        async fetchMyFriends() {
            this.loading = true
            this.error = null
            try {
                this.myFriends = await model.fetchMyFriends()
                return this.myFriends
            } catch (e) {
                this.error = e.response?.data?.message || e.message || 'Fetch my friends failed'
                throw e
            } finally {
                this.loading = false
            }
        },
        async fetchIncomingRequests() {
            this.loading = true
            this.error = null
            try {
                this.incomingRequests = await model.fetchIncomingRequests()
                return this.incomingRequests
            } catch (e) {
                this.error = e.response?.data?.message || e.message || 'Fetch incoming requests failed'
                throw e
            } finally {
                this.loading = false
            }
        },
        async sendRequest(targetId) {
            this.error = null
            try {
                return await model.sendFriendRequest(targetId)
            } catch (e) {
                this.error = e.response?.data?.message || e.message || 'Send friend request failed'
                throw e
            }
        },
        async approveRequest(fromId) {
            this.error = null
            try {
                return await model.approveFriendRequest(fromId)
            } catch (e) {
                this.error = e.response?.data?.message || e.message || 'Approve friend request failed'
                throw e
            }
        },
        async declineRequest(fromId) {
            this.error = null
            try {
                return await model.declineFriendRequest(fromId)
            } catch (e) {
                this.error = e.response?.data?.message || e.message || 'Decline friend request failed'
                throw e
            }
        },
        async removeFriend(friendId) {
            this.error = null
            try {
                return await model.removeFriend(friendId)
            } catch (e) {
                this.error = e.response?.data?.message || e.message || 'Remove friend failed'
                throw e
            }
        }
    }
})
