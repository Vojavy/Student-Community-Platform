export const handleUserIntent = async (intent, { model }) => {
    switch (intent.type) {
        case 'FETCH_USER_BY_ID':
            return await model.fetchById(intent.payload)

        case 'FETCH_USER_PROFILE':
            return await model.fetchProfile(intent.payload)

        case 'FETCH_USER_DETAILS':
            const profile = await model.fetchProfile(intent.payload)
            return profile.details || {}

        case 'UPDATE_USER':
            return await model.updateUser(intent.userId, intent.data)

        case 'UPDATE_USER_DETAILS':
            return await model.updateUserDetails(intent.userId, intent.details)
    }
}
