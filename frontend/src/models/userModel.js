import apiClient from '@/utils/api/apiClient'

export default function createUserModel() {
    return {
        async fetchById(id) {
            const response = await apiClient.get(`/api/users/${id}`)
            return response.data
        }
    }
}
