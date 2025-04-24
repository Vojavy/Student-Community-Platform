export default {
    common: {
        save: 'Save',
        cancel: 'Cancel',
        add: 'Add',
        change: 'Change',
        delete: 'Delete',
        loading: 'Loading...'
    },
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
        studentInfo: 'STAG Info',
        field: {
            name: 'Name',
            osCislo: 'OsCislo',
            titulPred: 'Title (Prefix)',
            titulZa: 'Title (Suffix)',
            year: 'Year',
            faculty: 'Faculty',
            program: 'Program'
        },
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
        email: 'Email',
        active: 'Active',
        yes: 'Yes',
        no: 'No',
        notFound: 'User not found',
        registered: 'Registered',
        detailsTitle: 'Details',
        noDetails: 'No details provided',

        friends: {
            title: 'Friends',
            empty: 'You have no friends yet',
            add: 'Add Friend',
            approve: 'Approve Request',
            decline: 'Decline Request',
            pending: 'Request Pending',
            remove: 'Remove Friend',
            column: {
                name: 'Name',
                status: 'Status',
                actions: 'Actions'
            }
        },

        settings: {
            title: 'Settings',
            save: 'Save',
            updated: 'Settings saved',
            error: 'An error occurred',
            empty:'Not specified',
            contacts:{
                otherTitle: 'Other contacts',
                addOther: 'Add another contact',
                keyPlaceholder: 'Contact method',
                valuePlaceholder: 'Contact details'
            },
            tabs: {
                security: 'Security',
                contacts: 'Contacts',
                personal: 'Personal Info',
                about: 'About'
            },
            security: {
                oldPassword: 'Current password',
                newPassword: 'New password',
                incorrectPassword: 'Incorrect current password',
                deactivate: 'Deactivate account?',
                confirmDeactivate: 'Confirm account deactivation by entering your password. This action cannot be undone.',
            },
            fields: {
                bio: 'Bio',
                status: 'Status',
                skills: 'Skills'
            }
        },

        contacts: {
            inst: 'Instagram',
            tg: 'Telegram',
            fb: 'Facebook',
            steam: 'Steam',
            ln: 'LinkedIn',
            telephone: 'Telephone',
            otherTitle: 'Other contacts',
        },

        personal: {
            birthDate: 'Birth Date',
            languages: 'Languages',
            location: 'Location',
            website: 'Website'
        },

        buttons: {
            settings: 'Profile Settings',
            connectStag: 'Connect STAG',
            addFriend: 'Add Friend',
            removeFriend: 'Remove Friend',
            sendMessage: 'Send Message'
        }
    },
    groups: {
        title: 'Groups',
        tabs: {
            my: 'My Groups',
            browse: 'Browse'
        },
        searchPlaceholder: 'Search by name or topic…',
        noMyGroups: 'You have no groups yet',
        noBrowseResults: 'No results found',

        filters: {
            name: 'Name',
            domain: 'Domain',
            domainAll: 'All domains',
            access: 'Access',
            accessAll: 'All',
            public: 'Public',
            private: 'Private',
            topics: 'Topics',
            apply: 'Apply'
        },

        pagination: {
            prev: '← Prev',
            next: 'Next →',
            size: 'Per page'
        },

        domainUnknown: 'No domain',
        public: 'Public',
        private: 'Private',

        createTitle: 'Create New Group',
        form: {
            domain: 'Domain (optional)',
            domainNone: 'None',
            name: 'Group Name',
            description: 'Description',
            topics: 'Topics (comma separated)',
            createButton: 'Create Group',
            public: 'Public',
        }
    }
}