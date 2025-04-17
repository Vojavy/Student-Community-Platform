export default function createAuthModel(apiClient) {
    return {
        async login({ email, password }) {
            const response = await apiClient.post('/auth/login', { email, password })
            console.log('Server response:', response.data)

            const token = response.data.token
            if (token) {
                localStorage.setItem('token', token)
                console.log('Token saved:', token)
            } else {
                console.warn('No token in response')
            }

            return response.data
        },
        async register(userData) {
            return await apiClient.post('/auth/signup', userData)
        },
        async verify(email, verificationCode) {
            return await apiClient.post('/auth/verify', {email, verificationCode})
        },
        async resend(email) {
            return await apiClient.post('/auth/resend?email=' + email,)
        },
        async checkToken() {
            return await apiClient.get('/auth/check')
        },
        async logout() {
            await apiClient.delete('/auth/logout')
            localStorage.removeItem('token')
            return true
        },
    }
}
