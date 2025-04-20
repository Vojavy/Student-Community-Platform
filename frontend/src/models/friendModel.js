import apiClient from '@/utils/api/apiClient'

export default function createFriendModel() {
    return {
        async fetchFriends(userId) {
            const response = await apiClient.get('/users/friends', {
                params: { id: userId }
            })
            return response.data  // Array<Friendresponseponse>
        },
        
        async fetchMyFriends() {
            const response = await apiClient.get('/users/friends')
            return response.data  // Array<Friendresponseponse>
        },
        
        async fetchIncomingRequests() {
            const response = await apiClient.get('/users/friends/incoming')
            return response.data  // Array<Friendresponseponse> с status = 'pending'
        },
        
        async sendFriendRequest(targetId) {
            const response = await apiClient.post(`/users/friends/${targetId}/request`)
            return response.data
        },
        
        async approveFriendRequest(fromId) {
            const response = await apiClient.post(`/users/friends/${fromId}/approve`)
            return response.data
        },
        
        async declineFriendRequest(fromId) {
            const response = await apiClient.post(`/users/friends/${fromId}/decline`)
            return response.data
        },
        
        async removeFriend(friendId) {
            const response = await apiClient.delete(`/users/friends/${friendId}`)
            return response.data
        }
    }
}
