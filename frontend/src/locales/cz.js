export default {
    common: {
        save: 'Uložit',
        cancel: 'Zrušit',
        add: 'Přidat',
        change: 'Změnit',
        delete: 'Smazat',
        loading: 'Načítá se...',
        search: 'Hledat',
        submit: 'Odeslat',
        showFilters: 'Zobrazit filtry',
        hideFilters: 'Skrýt filtry',
    },
    verification: {
        title: 'Ověření e-mailu',
        placeholder: 'Ověřovací kód',
        button: 'Ověřit',
        auto: 'Ověřovací kód se zpracovává automaticky...',
        resend: 'Znovu odeslat ověřovací kód',
        resending: 'Odesílání...',
        sent: 'Ověřovací kód byl znovu odeslán',
        success: '✅ Váš účet byl úspěšně ověřen!',
        redirecting: 'Přesměrovávám na přihlašovací stránku...',
        failed: '❌ Něco se pokazilo. Zkuste to prosím znovu.'
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
        button: 'Na domovskou'
    },
    register: {
        title: 'Registrace',
        email: 'Email',
        password: 'Heslo',
        username: 'Uživatelské jméno',
        button: 'Registrovat se',
        repeat: 'Heslo znovu',
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
        message: 'Nemáte oprávnění k zobrazení této stránky.',
        back: 'Na domovskou'
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
        searchUsers: 'Uzivatele',
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
        message: 'Vaše relace vypršela. Přihlaste se prosím znovu.',
        login: 'Přihlásit se',
        close: 'Na domovskou'
    },
    stag: {
        studentInfo: 'Informace z STAGu',
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
        login: 'Přihlásit se do STAGu',
        status: {
            noToken: 'STAG token nebyl nalezen.',
            invalid: 'STAG token je neplatný.',
            valid: 'STAG token je platný.'
        },
        delete: {
            show: 'Odstranit STAG token',
            confirm: 'Potvrdit smazání',
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
        username: 'username',
        notFound: 'Uživatel nenalezen',
        registered: 'Registrován',
        detailsTitle: 'Detaily',
        noDetails: 'Žádné údaje',
        search:
        {
            placeholderName: "Hledat podle jména nebo příjmení",
            placeholderEmail: "Email",
            placeholderRocnik: "Ročník",
            placeholderFakulta: "Fakulta",
            placeholderObor: "Obor",
            domainAll: "Všechny domény",
            titulAll: "Všechny tituly"
        },
        friends: {
            title: 'Přátelé',
            empty: 'Zatím žádní přátelé',
            add: 'Přidat mezi přátele',
            approve: 'Potvrdit žádost',
            decline: 'Zamítnout žádost',
            pending: 'Žádost odeslána',
            remove: 'Odebrat z přátel',
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
            empty: 'Nezadáno',
            contacts: {
                otherTitle: 'Další kontakty',
                addOther: 'Přidat kontakt',
                keyPlaceholder: 'Typ kontaktu',
                valuePlaceholder: 'Detaily kontaktu'
            },
            tabs: {
                security: 'Zabezpečení',
                contacts: 'Kontakty',
                personal: 'Osobní údaje',
                about: 'O mně'
            },
            security: {
                oldPassword: 'Aktuální heslo',
                newPassword: 'Nové heslo',
                incorrectPassword: 'Špatné aktuální heslo',
                deactivate: 'Deaktivovat účet?',
                confirmDeactivate: 'Pro potvrzení deaktivace zadejte heslo. Tuto akci nelze vrátit zpět.'
            },
            fields: {
                bio: 'O mně',
                status: 'Status',
                skills: 'Dovednosti'
            }
        },
        contacts: {
            show: 'Zobrazit kontakty',
            hide: 'Skrýt kontakty',
            inst: 'Instagram',
            tg: 'Telegram',
            fb: 'Facebook',
            steam: 'Steam',
            ln: 'LinkedIn',
            telephone: 'Telefon',
            otherTitle: 'Další kontakty'
        },
        personal: {
            birthDate: 'Datum narození',
            languages: 'Jazyky',
            location: 'Poloha',
            website: 'Web'
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
        noMyGroups: 'Zatím žádné skupiny',
        noBrowseResults: 'Nebyly nalezeny žádné výsledky',
        filters: {
            name: 'Název',
            domain: 'Doména',
            domainAll: 'Všechny domény',
            access: 'Přístup',
            accessAll: 'Vše',
            public: 'Veřejné',
            private: 'Soukromé',
            topics: 'Témata',
            apply: 'Použít'
        },
        pagination: { prev: '← Předchozí', next: 'Další →', size: 'Na stránku' },
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
        unban: 'Odbanovat',
        role: {
            role: 'Role',
            member: 'Člen',
            helper: 'Pomocník',
            admin: 'Admin',
            owner: 'Vlastník',
            invited: 'Pozván'
        },
        status: {
            status: 'Stav',
            approved: 'Schváleno',
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
        newPost: 'Nový příspěvek',
        title: 'Nadpis',
        titlePlaceholder: 'Zadejte nadpis…',
        topics: 'Témata',
        topicsPlaceholder: 'Témata oddělená čárkou…',
        bold: '🅱️ Tučné',
        italic: '𝘐 Kurzíva',
        underline: '〰️ Podtržené',
        strikethrough: '❌ Přeškrtnuté',
        h1: '🔢 Nadpis 1',
        h2: '🔢 Nadpis 2',
        insertLink: '🔗 Vložit odkaz',
        linkPrompt: 'URL',
        insertImage: '🖼️ Vložit obrázek',
        submit: 'Publikovat',
        expand: 'Rozbalit',
        hide: 'Skrýt',
        by: 'od',
        on: 'dne',
        edit: 'Upravit',
        delete: 'Smazat'
    },
    forum: {
        filters: {
            searchPlaceholder: 'Hledat podle názvu…',
            topicsPlaceholder: 'Témata oddělená čárkou…',
            publicAll: 'Vše',
            public: 'Veřejné',
            private: 'Soukromé',
            domainAll: 'Všechny domény',
            statusAll: 'Všechny stavy',
            visibilityAll: 'Vše'
        },
        sort: {
            newest: 'Nejnovější',
            oldest: 'Nejstarší'
        },
        actions: {
            editForum: 'Upravit fórum',
            archive: 'Archivovat fórum',
            activate: 'Aktivovat fórum',
            deleteForum: 'Smazat fórum',
            reply: 'Odpovědět',
            viewParent: 'Zobrazit rodičovský příspěvek'
        },
        columns: {
            domain: 'Doména',
            visibility: 'Viditelnost',
            public: 'Veřejné',
            private: 'Soukromé',
            status: 'Stav',
            pinned: 'Připnuté',
            createdAt: 'Vytvořeno'
        },
        tabs: {
            search: 'Hledání',
            following: 'Sleduji',
            info: 'Informační',
            archived: 'Archivované',
            banned: 'Zabanované',
            posts: 'Příspěvky',
            newPost: 'Nový příspěvek'
        },
        create: {
            title: 'Vytvořit fórum',
            name: 'Název',
            namePlaceholder: 'Zadejte název fóra…',
            description: 'Popis',
            descriptionPlaceholder: 'Zadejte popis…',
            topics: 'Témata',
            topicsPlaceholder: 'Témata oddělená čárkou…',
            topicsHelper: 'Témata oddělte čárkou',
            domain: 'Doména (volitelně)',
            domainNone: 'Žádná',
            informational: 'Informační',
            public: 'Veřejné',
            pinned: 'Připnout',
            minAllowedRole: 'Min. role pro psaní',
            roleNone: 'Bez omezení',
            minAllowedRoleHelper: 'Uživatelé s touto rolí a výše mohou psát',
            allowedUpTo: 'Až role',
            postPlaceholder: 'Váš příspěvek…',
            submit: 'Vytvořit'
        },
        confirm: {
            deleteForum: 'Opravdu chcete smazat toto fórum?'
        },
        status: {
            active: 'Aktivní',
            informational: 'Informační',
            archived: 'Archivované',
            banned: 'Zabanované'
        },
        detail: {
            createdBy: 'Vytvořil',
            createdAt: 'Vytvořeno',
            status: 'Stav',
            newPostTitle: 'Nový příspěvek',
            newPostPlaceholder: 'Váš příspěvek…',
            replyingTo: 'Odpověď na',
            replyToPost: 'Odpovědět na příspěvek',
            addMessage: 'Přidat zprávu',
            settingsTitle: 'Nastavení',
            closeForum: 'Uzavřít fórum',
            archiveForum: 'Archivovat fórum',
            resolveForum: 'Vyřešit fórum',
            deleteForum: 'Smazat fórum',
            reply: 'Odpovědět',
            inReplyTo: 'Ve odpovědi na'
        }
    }
}
