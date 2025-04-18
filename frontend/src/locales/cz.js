export default {
    verification: {
        title: 'Ověření e‑mailu',
        placeholder: 'Ověřovací kód',
        button: 'Ověřit',
        auto: 'Ověřovací kód se zpracovává automaticky...',
        resend: 'Znovu odeslat ověřovací kód',
        resending: 'Odesílání...',
        sent: 'Ověřovací kód byl znovu odeslán',
        success: '✅ Váš účet byl úspěšně ověřen!',
        redirecting: 'Za chvíli budete přesměrováni na přihlašovací stránku...',
        failed: '❌ Oops, něco se pokazilo. Zkuste to prosím znovu.'
    },
    login: {
        title: 'Přihlášení',
        email: 'Email',
        password: 'Heslo',
        button: 'Přihlásit se'
    },
    logout: {
        title: 'Odhlášení',
        message: 'Byli jste úspěšně odhlášeni.',
        button: 'Zpět'
    },
    register: {
        title: 'Registrace',
        email: 'Email',
        password: 'Heslo',
        username: 'Uživatelské jméno',
        button: 'Registrovat se'
    },
    errors: {
        invalidEmail: 'Neplatná emailová adresa',
        emptyEmail: 'Zadejte email',
        emptyPassword: 'Zadejte heslo',
        emptyUsername: 'Zadejte uživatelské jméno',
        passwordMismatch: 'Hesla se neshodují',
        invalidCredentials: 'Neplatný email nebo heslo'
    },
    accessDenied: {
        title: 'Přístup odepřen',
        message: 'Nemáte oprávnění pro přístup na tuto stránku.',
        back: 'Na hlavní'
    },
    navBar: {
        login: 'Přihlášení',
        registration: 'Registrace',
        signout: 'Odhlásit se',
        index: 'Domů',
        forum: 'Fórum',
        actions: 'Události',
        profile: 'Profil',
        market: 'Tržiště',
        calendar: 'Kalendář',
        groups: 'Skupiny',
        messages: 'Soukromé zprávy',
        mobile: {
            index: 'Domů',
            market: 'Tržiště',
            forum: 'Fórum',
            groups: 'Skupiny',
            messages: 'Soukromé zprávy',
            burger: {
                calendar: 'Kalendář',
                profile: 'Profil',
                actions: 'Události'
            }
        }
    },
    sessionExpired: {
        title: 'Platnost relace vypršela',
        message: 'Vaše relace vypršela. Přihlaste se znovu.',
        login: 'Přihlásit se',
        close: 'Na hlavní stránku'
    },
    stag: {
        selectUniversity: 'Vyberte univerzitu',
        login: 'Přihlásit se do STAG',
        status: {
            noToken: 'STAG token nebyl nalezen.',
            invalid: 'STAG token je neplatný.',
            valid: 'STAG token je platný.'
        },
        delete: {
            show: 'Odstranit STAG token',
            confirm: 'Potvrdit odstranění',
            cancel: 'Zrušit'
        },
        universities: {
            upce: 'Univerzita Pardubice',
            zcu: 'Západočeská univerzita v Plzni'
        }
    },
    profile: {
        defaultName: 'Můj profil',
        registered: 'Registrován',
        buttons: {
            settings: 'Nastavení profilu',
            connectStag: 'Připojit STAG'
        }
    },
    loading: 'Načítání...',
}
