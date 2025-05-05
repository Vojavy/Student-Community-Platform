export default {
    common: {
        save: 'Save',
        cancel: 'Cancel',
        add: 'Add',
        change: 'Change',
        delete: 'Delete',
        loading: 'Loading...',
        search: 'Search',
        submit: 'Submit',
        showFilters: 'Show filters',
        hideFilters: 'Hide filters',
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
        button: 'Register',
        repeat: 'Repeat password',
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
        searchUsers: 'Users',
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
        username: 'username',
        notFound: 'User not found',
        registered: 'Registered',
        detailsTitle: 'Details',
        noDetails: 'No details provided',
        search:
        {
            placeholderName: "Search by first or last name",
            placeholderEmail: "Email",
            placeholderRocnik: "Year",
            placeholderFakulta: "Faculty",
            placeholderObor: "Program",
            domainAll: "All domains",
            titulAll: "All titles"
        },
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
            empty: 'Not specified',
            contacts: {
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
                confirmDeactivate: 'Confirm account deactivation by entering your password. This action cannot be undone.'
            },
            fields: {
                bio: 'Bio',
                status: 'Status',
                skills: 'Skills'
            }
        },
        contacts: {
            show: 'Show contacts',
            hide: 'Hide contacts',
            inst: 'Instagram',
            tg: 'Telegram',
            fb: 'Facebook',
            steam: 'Steam',
            ln: 'LinkedIn',
            telephone: 'Telephone',
            otherTitle: 'Other contacts'
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
    chat: {
        title: 'Chat',
        send: 'Send',
        reply: 'Reply',
        replyingTo: 'Replying to message',
        replyPreviewDeleted: 'Message deleted',
        readAt: 'Read at {{time}}',
        placeholder: 'Type a message...'
    },
    groups: {
        title: 'Groups',
        domain: 'Domain',
        name: 'Name',
        description: 'Description',
        topics: 'Topics',
        overview: 'Overview',
        calendar: 'Calendar',
        settings: 'Settings',
        posts: 'Posts',
        tabs: { my: 'My Groups', browse: 'Browse' },
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
        pagination: { prev: '← Prev', next: 'Next →', size: 'Per page' },
        domainUnknown: 'No domain',
        public: 'Public',
        private: 'Private',
        createdAt: 'Created',
        owner: 'Owner',
        admins: 'Admins',
        helpers: 'Helpers',
        deleteGroup: 'Delete Group',
        leave: 'Leave',
        join: 'Join',
        members: 'Members',
        filterStatus: 'Status',
        filterRole: 'Role',
        approve: 'Approve',
        decline: 'Decline',
        cancelInvitation: 'Cancel invitation',
        kick: 'Kick',
        ban: 'Ban',
        unban: 'Unban',
        role: {
            role: 'Role',
            member: 'Member',
            helper: 'Helper',
            admin: 'Admin',
            owner: 'Owner',
            invited: 'Invited'
        },
        status: {
            status: 'Status',
            approved: 'Member',
            pending: 'Pending',
            banned: 'Banned'
        },
        pending: 'Membership request sent',
        banned: 'Banned',
        notFound: 'Group not found',
        minRoleForPosts: 'Min. role to post',
        minRoleForEvents: 'Min. role to create events',
        noDomain: 'No domain',
        createTitle: 'Create New Group',
        form: {
            domain: 'Domain (optional)',
            domainNone: 'None',
            name: 'Group Name',
            description: 'Description',
            topics: 'Topics (comma separated)',
            createButton: 'Create Group',
            public: 'Public'
        }
    },
    posts: {
        newPost: 'New post',
        title: 'Title',
        titlePlaceholder: 'Enter a title…',
        topics: 'Topics',
        topicsPlaceholder: 'Comma-separated topics…',
        bold: '🅱️ Bold',
        italic: '𝘐 Italic',
        underline: '〰️ Underline',
        strikethrough: '❌ Strikethrough',
        h1: '🔢 Heading 1',
        h2: '🔢 Heading 2',
        insertLink: '🔗 Insert link',
        linkPrompt: 'URL',
        insertImage: '🖼️ Insert image',
        submit: 'Publish',
        expand: 'Expand',
        hide: 'Hide',
        by: 'by',
        on: 'on',
        edit: 'Edit',
        delete: 'Delete'
    },
    forum: {
        filters: {
            searchPlaceholder: 'Search by name…',
            topicsPlaceholder: 'Comma-separated topics…',
            publicAll: 'All',
            public: 'Public',
            private: 'Private',
            domainAll: 'All domains',
            statusAll: 'All statuses',
            visibilityAll: 'All'
        },
        sort: {
            newest: 'Newest first',
            oldest: 'Oldest first'
        },
        actions: {
            editForum: 'Edit forum',
            archive: 'Archive forum',
            activate: 'Activate forum',
            deleteForum: 'Delete forum',
            reply: 'Reply',
            viewParent: 'View parent post'
        },
        columns: {
            domain: 'Domain',
            visibility: 'Visibility',
            public: 'Public',
            private: 'Private',
            status: 'Status',
            pinned: 'Pinned',
            createdAt: 'Created at'
        },
        tabs: {
            search: 'Search',
            following: 'Following',
            info: 'Informational',
            archived: 'Archived',
            banned: 'Banned',
            posts: 'Posts',
            newPost: 'New Post'
        },
        create: {
            title: 'Create Forum',
            name: 'Name',
            namePlaceholder: 'Enter forum name…',
            description: 'Description',
            descriptionPlaceholder: 'Enter description…',
            topics: 'Topics',
            topicsPlaceholder: 'Comma-separated topics…',
            topicsHelper: 'Separate topics with commas',
            domain: 'Domain (optional)',
            domainNone: 'None',
            informational: 'Informational',
            public: 'Public',
            pinned: 'Pinned',
            minAllowedRole: 'Min. role to post',
            roleNone: 'No restriction',
            minAllowedRoleHelper: 'Users with this role and above can post',
            allowedUpTo: 'Up to roles',
            postPlaceholder: 'Your message…',
            submit: 'Create'
        },
        confirm: {
            deleteForum: 'Are you sure you want to delete this forum?'
        },
        status: {
            active: 'Active',
            informational: 'Informational',
            archived: 'Archived',
            banned: 'Banned'
        },
        detail: {
            createdBy: 'Created by',
            createdAt: 'Created on',
            status: 'Status',
            newPostTitle: 'New Post',
            newPostPlaceholder: 'Your message…',
            replyingTo: 'Replying to',
            replyToPost: 'Reply to post',
            addMessage: 'Add message',
            settingsTitle: 'Settings',
            closeForum: 'Close forum',
            archiveForum: 'Archive forum',
            resolveForum: 'Resolve forum',
            deleteForum: 'Delete forum',
            reply: 'Reply',
            inReplyTo: 'In reply to'
        }
    }
}
