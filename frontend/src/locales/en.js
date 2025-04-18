export default {
    verification: {
        title: 'Email Verification',
        placeholder: 'Verification code',
        button: 'Verify',
        auto: 'Verification code is being processed automatically...',
        resend: 'Resend verification code',
        resending: 'Sending...',
        sent: 'Verification code has been resent',
        success: '✅ Your account has been successfully verified!',
        redirecting: 'Redirecting you to the login page...',
        failed: '❌ Oops, something went wrong. Please try again.'
    },
    login: {
        title: 'Login',
        email: 'Email',
        password: 'Password',
        button: 'Sign in'
    },
    logout: {
        title: 'Logged Out',
        message: 'You have successfully logged out.',
        button: 'Go to Home'
    },
    register: {
        title: 'Registration',
        email: 'Email',
        password: 'Password',
        username: 'Username',
        button: 'Register'
    },
    errors: {
        invalidEmail: 'Invalid email address',
        emptyEmail: 'Please enter your email',
        emptyPassword: 'Please enter your password',
        emptyUsername: 'Please enter your username',
        passwordMismatch: 'Passwords do not match',
        invalidCredentials: 'Invalid email or password'
    },
    accessDenied: {
        title: 'Access Denied',
        message: 'You do not have permission to view this page.',
        back: 'Go to Home'
    },
    navBar: {
        login: 'Login',
        registration: 'Register',
        signout: 'Sign out',
        index: 'Home',
        forum: 'Forum',
        actions: 'Events',
        profile: 'Profile',
        market: 'Marketplace',
        calendar: 'Calendar',
        groups: 'Groups',
        messages: 'Private Messages',
        mobile: {
            index: 'Home',
            market: 'Marketplace',
            forum: 'Forum',
            groups: 'Groups',
            messages: 'Private Messages',
            burger: {
                calendar: 'Calendar',
                profile: 'Profile',
                actions: 'Events'
            }
        }
    },
    sessionExpired: {
        title: 'Session Expired',
        message: 'Your session has expired. Please log in again.',
        login: 'Sign In',
        close: 'Go to Home'
    },
    stag: {
        selectUniversity: 'Select a university',
        login: 'Login to STAG',
        status: {
            noToken: 'STAG token not found.',
            invalid: 'STAG token is invalid.',
            valid: 'STAG token is valid.'
        },
        delete: {
            show: 'Delete STAG token',
            confirm: 'Confirm deletion',
            cancel: 'Cancel'
        },
        universities: {
            upce: 'University of Pardubice',
            zcu: 'University of West Bohemia in Pilsen'
        }
    },
    profile: {
        defaultName: 'My Profile',
        registered: 'Registered on',
        buttons: {
            settings: 'Profile settings',
            connectStag: 'Connect STAG'
        }
    },
    loading: 'Loading...',
}
