export const handleAuthIntent = async (intent, { model, coordinator }) => {
    switch (intent.type) {
        case 'LOGIN':
            try {
                const result = await model.login(intent.payload)
                coordinator.navigateToHome()
            } catch (e) {
                console.error('Login failed', e)
                throw e
            }
            break

        case 'REGISTER':
            try {
                await model.register(intent.payload)
                coordinator.navigateToVerification(intent.payload.email)
            } catch (e) {
                console.error('Register failed', e)
            }
            break

        case 'VERIFY':
            try {
                await model.verify(intent.payload.email, intent.payload.verificationCode)
                coordinator.navigateToHome()
            } catch (e) {
                console.error('Verification failed', e)
            }
            break

        case 'RESEND_CODE':
            try {
                await model.resend(intent.payload.email)
            } catch (e) {
                console.error('Resend failed', e)
            }
            break

        case 'CHECK_TOKEN':
            try {
                const response = await model.checkToken()
            } catch (e) {
                console.error(e)
                localStorage.removeItem('token')
                window.dispatchEvent(new CustomEvent('jwt-expired'))
            }
            break
        case 'LOGOUT':
            try {
                await model.logout()
                window.dispatchEvent(new CustomEvent('logout-success'))
            } catch (e) {
                console.error('Logout failed', e)
            }
            break
    }
}