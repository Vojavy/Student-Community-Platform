export default {
    common: {
        save: 'Uložit',
        cancel: 'Zrušit',
        add: 'Přidat',
        change: 'Změnit',
        delete: 'Smazat',
        loading: 'Loading...',
        search: 'Hledat'
    },
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
        studentInfo: 'Informace o STAG',
        field: {
            name: 'Jméno',
            osCislo: 'OsCislo',
            titulPred: 'Titul před',
            titulZa: 'Titul za',
            year: 'Ročník',
            faculty: 'Fakulta',
            program: 'Studijní program'
        },
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
        email: 'Email',
        active: 'Aktivní',
        yes: 'Ano',
        no: 'Ne',
        notFound: 'Uživatel nenalezen',
        registered: 'Registrován',
        detailsTitle: 'Detaily',
        noDetails: 'Žádné uvedené údaje',

        friends: {
            title: 'Přátelé',
            empty: 'Zatím nemáte žádné přátele',
            add: 'Přidat mezi přátele',
            approve: 'Potvrdit žádost',
            decline: 'Zamítnout žádost',
            pending: 'Žádost odeslána',
            remove: 'Odstranit z přátel',
            column: {
                name: 'Jméno',
                status: 'Stav',
                actions: 'Akce'
            }
        },

        settings: {
            title: 'Nastavení',
            save: 'Uložit',
            updated: 'Nastavení uloženo',
            error: 'Došlo k chybě',
            empty:'Nenalezeno',
            contacts:{
                otherTitle: 'Další kontakty',
                addOther: 'Přidat kontakt',
                keyPlaceholder: 'Způsob kontaktu',
                valuePlaceholder: 'Detail kontaktu'
            },
            tabs: {
                security: 'Zabezpečení',
                contacts: 'Kontakty',
                personal: 'Osobní údaje',
                about: 'O mně'
            },
            security: {
                oldPassword: 'Současné heslo',
                newPassword: 'Nové heslo',
                incorrectPassword: 'Nesprávné aktuální heslo',
                deactivate: 'Deaktivovat učet?',
                confirmDeactivate: 'Deaktivaci účtu potvrďte zadáním hesla. Tuto akci nelze zrušit.',
            },
            fields: {
                bio: 'O mně',
                status: 'Status',
                skills: 'Dovednosti'
            }
        },

        contacts: {
            inst: 'Instagram',
            tg: 'Telegram',
            fb: 'Facebook',
            steam: 'Steam',
            ln: 'LinkedIn',
            telephone: 'Telefon',
            otherTitle: 'Další kontakty',
        },

        personal: {
            birthDate: 'Datum narození',
            languages: 'Jazyky',
            location: 'Místo',
            website: 'Webová stránka'
        },

        buttons: {
            settings: 'Nastavení profilu',
            connectStag: 'Připojit STAG',
            addFriend: 'Přidat mezi přátele',
            removeFriend: 'Odebrat z přátel',
            sendMessage: 'Poslat zprávu'
        }
    },
    groups: {
        title: 'Skupiny',
        domain: 'Doména',
        name: 'Název',
        description: 'Popis',
        topics: 'Témata',

        overview: 'Přehled',
        calendar: 'Kalendář',
        settings: 'Nastavení',
        posts: 'Příspěvky',

        tabs: { my: 'Mé skupiny', browse: 'Procházet' },
        searchPlaceholder: 'Hledat podle názvu nebo tématu…',
        noMyGroups: 'Zatím nemáte žádné skupiny',
        noBrowseResults: 'Nebyly nalezeny žádné výsledky',

        filters: {
            name: 'Název',
            domain: 'Doména',
            domainAll: 'Všechny domény',
            access: 'Přístup',
            accessAll: 'Všechny',
            public: 'Veřejné',
            private: 'Soukromé',
            topics: 'Témata',
            apply: 'Použít'
        },
        pagination: { prev: '← Předch.', next: 'Další →', size: 'Na stránce' },

        domainUnknown: 'Bez domény',
        public: 'Veřejná',
        private: 'Soukromá',
        createdAt: 'Vytvořeno',
        owner: 'Vlastník',
        admins: 'Administrátoři',
        helpers: 'Pomocníci',

        deleteGroup: 'Smazat skupinu',
        leave: 'Opustit',
        join: 'Připojit se',

        members: 'Členové',
        filterStatus: 'Stav',
        filterRole: 'Role',
        approve: 'Schválit',
        decline: 'Odmítnout',
        cancelInvitation: 'Zrušit pozvání',
        kick: 'Vyloučit',
        ban: 'Zabanovat',
        unban: 'Odblokovat',

        role: {
            role: 'Role',
            member: 'Člen',
            helper: 'Pomocník',
            admin: 'Administrátor',
            owner: 'Vlastník',
            invited: 'Pozván',
        },
        status: {
            status: 'Stav',
            approved: 'Člen',
            pending: 'Čeká na schválení',
            banned: 'Zabanován'
        },

        pending: 'Žádost o členství odeslána',
        banned: 'Zabanováno',
        notFound: 'Skupina nenalezena',

        minRoleForPosts: 'Min. role pro příspěvky',
        minRoleForEvents: 'Min. role pro události',
        noDomain: 'Bez domény',

        createTitle: 'Vytvořit novou skupinu',
        form: {
            domain: 'Doména (volitelně)',
            domainNone: 'Žádná',
            name: 'Název skupiny',
            description: 'Popis',
            topics: 'Témata (oddělená čárkou)',
            createButton: 'Vytvořit skupinu',
            public: 'Veřejná'
        }
    },
    posts: {
        newPost:        'Nový příspěvek',
        title:          'Nadpis',
        titlePlaceholder:'Zadejte nadpis…',
        topics:         'Témata',
        topicsPlaceholder:'Témata oddělená čárkou…',
        bold:            '🅱️ Tučné',
        italic:          '𝘐 Kurzíva',
        underline:       '〰️ Podtržené',
        strikethrough:   '❌ Přeškrtnuté',
        h1:              '🔢 Nadpis 1',
        h2:              '🔢 Nadpis 2',
        insertLink:      '🔗 Vložit odkaz',
        linkPrompt:      'URL adresa',
        insertImage:     '🖼️ Vložit obrázek',
        submit:         'Publikovat',
        expand:         'Rozbalit',
        hide:           'Skrýt',
        by:             'od',
        on:             'dne',
        edit:           'Upravit',
        delete:         'Smazat'
    },
}
