// src/models/domainModel.js
import apiClient from '@/utils/api/apiClient.js'

export default function createDomainModel() {
    return {
        /** GET /university-domains */
        async fetchDomains() {
            const response = await apiClient.get('/university-domains')
            return response.data
        },

        /** GET /university-domains/{id} */
        async fetchDomainById(id) {
            const response = await apiClient.get(`/university-domains/${id}`)
            return response.data
        },

        /** GET /university-domains/code/{domain} */
        async fetchDomainByCode(domainCode) {
            const response = await apiClient.get(`/university-domains/code/${domainCode}`)
            return response.data
        }
    }
}
