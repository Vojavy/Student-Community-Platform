export const fetchUserByIdIntent = (id) => ({
    type: 'FETCH_USER_BY_ID',
    payload: { id }
})

export const fetchUserProfileIntent = (id) => ({
    type: 'FETCH_USER_PROFILE',
    payload: { id }
})

export const fetchUserDetailsIntent = (id) => ({
    type: 'FETCH_USER_DETAILS',
    payload: { id }
})

export const updateUserIntent = (userId, data) => ({
    type: 'UPDATE_USER',
    userId,
    data
})

export const updateUserDetailsIntent = (userId, details) => ({
    type: 'UPDATE_USER_DETAILS',
    userId,
    details
})

export const fetchUserRolesIntent = () => ({
    type: 'FETCH_USER_ROLES'
})
