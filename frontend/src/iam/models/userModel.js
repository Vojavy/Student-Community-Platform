import apiClient from '@/utils/api/apiClient.js'

export default function createUserModel() {
    return {
        async fetchById({id}) {
            const response= await apiClient.get(`/users/${id}`)
            return response.data
        },
        async fetchProfile({id}) {
            const response = await apiClient.get(`/users/${id}/profile`)
            return response.data
        },
        async updateUser(userId, data) {
            const response = await apiClient.put(`/users/${userId}`, data)
            return response.data
        },
        async updateUserDetails(userId, details) {
            const response = await apiClient.put(`/users/${userId}/details`, details)
            return response.data
        },
        async fetchUserRoles(){
            const response = await apiClient.get('/users/roles')
            return response.data
        }
    }
}
