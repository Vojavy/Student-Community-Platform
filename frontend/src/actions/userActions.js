export const handleUserIntent = async (intent, { model }) => {
    switch (intent.type) {
        case 'FETCH_USER_BY_ID':
            return await model.fetchById(intent.payload.id)
    }
}
