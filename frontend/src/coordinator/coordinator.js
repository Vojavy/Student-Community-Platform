export default function createCoordinator(router) {
    return {
        navigateToLogin() {
            router.push('/login')
        },
        navigateToRegister() {
            router.push('/register')
        },
        navigateToVerification(email, code = null) {
            const query = { email }
            if (code) query.code = code

            router.push({ path: '/verify', query })
        },
        navigateToHome() {
            router.push('/home') // если главная авторизованного — /home
        },
        navigateToLanding() {
            router.push('/')
        }
    }
}
