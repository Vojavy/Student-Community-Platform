export const handleRoleIntent = async (intent, { model }) => {
    switch (intent.type) {
        case 'FETCH_ROLES':
            return await model.fetchRoles()
        default:
            throw new Error(`Unknown role intent: ${intent.type}`)
    }
}
