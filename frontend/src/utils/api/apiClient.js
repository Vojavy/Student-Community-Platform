// apiClient.js
import axios from 'axios'
import { checkTokenIntent } from '@/iam/intents/authIntents'
import { handleAuthIntent } from '@/iam/actions/authActions'
import createAuthModel from '@/iam/models/authModel'
import router from '@/coordinator/router'
import createCoordinator from '@/coordinator/coordinator'

const apiClient = axios.create({
    baseURL: import.meta.env.VITE_BASE_API_URL,
    headers: {
        'Content-Type': 'application/json'
    }
})

apiClient.interceptors.request.use(config => {
    const token = localStorage.getItem('token')
    if (token) {
        config.headers.Authorization = `Bearer ${token}`
    }
    return config
})

apiClient.interceptors.response.use(
    res => res,
    async error => {
        return Promise.reject(error)
    }
)

export default apiClient
