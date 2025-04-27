import apiClient from '@/utils/api/apiClient.js'

export default function createRoleModel() {
    return {
        async fetchRoles() {
            const resp = await apiClient.get('/roles')
            return resp.data
        }
    }
}
