export function getUserIdFromToken() {
    try {
        const token = localStorage.getItem('token')
        const payload = JSON.parse(atob(token.split('.')[1]))
        return payload.id
    } catch {
        return null
    }
}
