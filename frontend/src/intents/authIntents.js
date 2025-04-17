export const loginIntent = (email, password) => ({
    type: 'LOGIN',
    payload: { email, password }
})

export const registerIntent = (userData) => ({
    type: 'REGISTER',
    payload: userData
})

export const verifyIntent = (email, verificationCode) => ({
    type: 'VERIFY',
    payload: {email, verificationCode}
})

export const resendIntent = (email) => ({
    type: 'RESEND_CODE',
    payload: { email }
})

export const checkTokenIntent = () => ({
    type: 'CHECK_TOKEN'
})

export const logoutIntent = () => ({
    type: 'LOGOUT'
})
