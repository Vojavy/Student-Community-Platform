export default {
    common: {
        save: 'Сохранить',
        cancel: 'Отмена',
        add: 'Добавить',
        change: 'Изменить',
        delete: 'Удалить',
        loading: 'Загрузка...',
        search: 'Поиск',
        submit: 'Отправить'
    },
    verification: {
        title: 'Подтверждение Email',
        placeholder: 'Код подтверждения',
        button: 'Подтвердить',
        auto: 'Код подтверждения обрабатывается автоматически...',
        resend: 'Прислать код подтверждения снова',
        resending: 'Отправка...',
        sent: 'Код был отправлен повторно',
        success: '✅ Ваш аккаунт успешно подтверждён!',
        redirecting: 'Сейчас вы будете перенаправлены на страницу входа...',
        failed: '❌ Что-то пошло не так. Попробуйте ещё раз.'
    },
    login: {
        title: 'Вход',
        email: 'Email',
        password: 'Пароль',
        button: 'Войти'
    },
    logout: {
        title: 'Вы вышли из аккаунта',
        message: 'Вы успешно вышли. Хотите вернуться на главную?',
        button: 'Вернуться'
    },
    register: {
        title: 'Регистрация',
        email: 'Email',
        password: 'Пароль',
        username: 'Имя пользователя',
        button: 'Зарегистрироваться',
        repeat: 'Повторите пароль',
    },
    errors: {
        invalidEmail: 'Некорректный email',
        emptyEmail: 'Введите email',
        emptyPassword: 'Введите пароль',
        emptyUsername: 'Введите имя пользователя',
        passwordMismatch: 'Пароли не совпадают',
        invalidCredentials: 'Неверный email или пароль'
    },
    accessDenied: {
        title: 'Доступ запрещён',
        message: 'У вас нет прав для доступа к этой странице.',
        back: 'На главную'
    },
    navBar: {
        login: 'Вход',
        registration: 'Регистрация',
        signout: 'Выйти',
        index: 'Главная',
        forum: 'Форум',
        actions: 'События',
        profile: 'Профиль',
        market: 'Базар',
        calendar: 'Календарь',
        groups: 'Группы',
        messages: 'Личные сообщения',
        mobile: {
            index: 'Главная',
            market: 'Базар',
            forum: 'Форум',
            groups: 'Группы',
            messages: 'Личные сообщения',
            burger: {
                calendar: 'Календарь',
                profile: 'Профиль',
                actions: 'События'
            }
        }
    },
    sessionExpired: {
        title: 'Сессия истекла',
        message: 'Ваша сессия завершилась. Пожалуйста, войдите снова.',
        login: 'Войти',
        close: 'На главную'
    },
    stag: {
        studentInfo: 'Данные STAG',
        field: {
            name: 'Имя',
            osCislo: 'OsCislo',
            titulPred: 'Титул (перед)',
            titulZa: 'Титул (после)',
            year: 'Курс',
            faculty: 'Факультет',
            program: 'Специальность'
        },
        selectUniversity: 'Выберите университет',
        login: 'Подключиться к STAG',
        status: {
            noToken: 'STAG токен не найден.',
            invalid: 'STAG токен недействителен.',
            valid: 'STAG токен действителен.'
        },
        delete: {
            show: 'Удалить STAG токен',
            confirm: 'Подтвердить удаление',
            cancel: 'Отмена'
        },
        universities: {
            upce: 'Университет Пардубице',
            zcu: 'Западночешский университет в Пльзени'
        }
    },
    profile: {
        defaultName: 'Мой профиль',
        email: 'Email',
        active: 'Активен',
        yes: 'Да',
        no: 'Нет',
        notFound: 'Пользователь не найден',
        registered: 'Зарегистрирован',
        detailsTitle: 'Детали',
        noDetails: 'Нет указанных данных',
        friends: {
            title: 'Друзья',
            empty: 'У вас пока нет друзей',
            add: 'Добавить в друзья',
            approve: 'Принять запрос',
            decline: 'Отклонить запрос',
            pending: 'Запрос отправлен',
            remove: 'Удалить из друзей',
            column: {
                name: 'Имя',
                status: 'Статус',
                actions: 'Действия'
            }
        },
        settings: {
            title: 'Настройки',
            save: 'Сохранить',
            updated: 'Настройки сохранены',
            error: 'Произошла ошибка',
            empty: 'Не указано',
            contacts: {
                otherTitle: 'Другие контакты',
                addOther: 'Добавить контакт',
                keyPlaceholder: 'Способ связи',
                valuePlaceholder: 'Данные контакта'
            },
            tabs: {
                security: 'Безопасность',
                contacts: 'Контакты',
                personal: 'Личные данные',
                about: 'О себе'
            },
            security: {
                oldPassword: 'Текущий пароль',
                newPassword: 'Новый пароль',
                incorrectPassword: 'Неверный текущий пароль',
                deactivate: 'Деактивировать аккаунт?',
                confirmDeactivate: 'Подтвердите деактивацию аккаунта введя ваш пароль. Это действие нельзя отменить.'
            },
            fields: {
                bio: 'О себе',
                status: 'Статус',
                skills: 'Навыки'
            }
        },
        contacts: {
            inst: 'Instagram',
            tg: 'Telegram',
            fb: 'Facebook',
            steam: 'Steam',
            ln: 'LinkedIn',
            telephone: 'Телефон',
            otherTitle: 'Другие контакты'
        },
        personal: {
            birthDate: 'Дата рождения',
            languages: 'Языки',
            location: 'Местоположение',
            website: 'Веб-сайт'
        },
        buttons: {
            settings: 'Настройки профиля',
            connectStag: 'Подключить STAG',
            addFriend: 'Добавить в друзья',
            removeFriend: 'Удалить из друзей',
            sendMessage: 'Написать сообщение'
        }
    },
    groups: {
        title: 'Группы',
        domain: 'Домен',
        name: 'Название',
        description: 'Описание',
        topics: 'Темы',
        overview: 'Обзор',
        calendar: 'Календарь',
        settings: 'Настройки',
        posts: 'Посты',
        tabs: { my: 'Мои группы', browse: 'Обзор' },
        searchPlaceholder: 'Поиск по названию или теме…',
        noMyGroups: 'У вас ещё нет групп',
        noBrowseResults: 'Ничего не найдено',
        filters: {
            name: 'Название',
            domain: 'Домен',
            domainAll: 'Все домены',
            access: 'Доступ',
            accessAll: 'Любой',
            public: 'Открытые',
            private: 'Закрытые',
            topics: 'Темы',
            apply: 'Применить'
        },
        pagination: { prev: '← Назад', next: 'Вперёд →', size: 'На странице' },
        domainUnknown: 'Без домена',
        public: 'Открытая',
        private: 'Закрытая',
        createdAt: 'Создана',
        owner: 'Владелец',
        admins: 'Админы',
        helpers: 'Помощники',
        deleteGroup: 'Удалить группу',
        leave: 'Выйти',
        join: 'Присоединиться',
        members: 'Участники',
        filterStatus: 'Статус',
        filterRole: 'Роль',
        approve: 'Принять',
        decline: 'Отклонить',
        cancelInvitation: 'Отменить приглашение',
        kick: 'Кикнуть',
        ban: 'Забанить',
        unban: 'Разбанить',
        role: {
            role: 'Роль',
            member: 'Участник',
            helper: 'Помощник',
            admin: 'Админ',
            owner: 'Владелец',
            invited: 'Приглашён'
        },
        status: {
            status: 'Статус',
            approved: 'Участник',
            pending: 'Ожидает',
            banned: 'Забанен'
        },
        pending: 'Заявка отправлена',
        banned: 'Забанена',
        notFound: 'Группа не найдена',
        minRoleForPosts: 'Мин. роль для постов',
        minRoleForEvents: 'Мин. роль для событий',
        noDomain: 'Без домена',
        createTitle: 'Создать новую группу',
        form: {
            domain: 'Домен (опционально)',
            domainNone: 'Без домена',
            name: 'Название группы',
            description: 'Описание',
            topics: 'Темы (через запятую)',
            createButton: 'Создать группу',
            public: 'Открытая'
        }
    },
    posts: {
        newPost: 'Новый пост',
        title: 'Заголовок',
        titlePlaceholder: 'Введите заголовок…',
        topics: 'Темы',
        topicsPlaceholder: 'Темы через запятую…',
        bold: '🅱️ Жирный',
        italic: '𝘐 Курсив',
        underline: '〰️ Подчёркнутый',
        strikethrough: '❌ Зачёркнутый',
        h1: '🔢 Заголовок 1',
        h2: '🔢 Заголовок 2',
        insertLink: '🔗 Вставить ссылку',
        linkPrompt: 'URL-адрес',
        insertImage: '🖼️ Вставить изображение',
        submit: 'Опубликовать',
        expand: 'Развернуть',
        hide: 'Скрыть',
        by: 'Автор:',
        on: 'дата',
        edit: 'Редактировать',
        delete: 'Удалить'
    },
    forum: {
        filters: {
            searchPlaceholder: 'Поиск по названию…',
            topicsPlaceholder: 'Темы через запятую…',
            publicAll: 'Все',
            public: 'Открытые',
            private: 'Закрытые',
            domainAll: 'Все домены',
            statusAll: 'Все статусы',
            visibilityAll: 'Все'
        },
        sort: {
            newest: 'Сначала новые',
            oldest: 'Сначала старые'
        },
        actions: {
            showFilters: 'Показать фильтры',
            hideFilters: 'Скрыть фильтры',
            editForum: 'Редактировать форум',
            archive: 'Архивировать форум',
            activate: 'Активировать форум',
            deleteForum: 'Удалить форум',
            reply: 'Ответить',
            viewParent: 'Перейти к родительскому сообщению'
        },
        columns: {
            domain: 'Домен',
            visibility: 'Доступность',
            public: 'Открытый',
            private: 'Закрытый',
            status: 'Статус',
            pinned: 'Закреплён',
            createdAt: 'Создан'
        },
        tabs: {
            search: 'Поиск',
            following: 'Подписки',
            info: 'Информационные',
            archived: 'Архивированные',
            banned: 'Забаненные',
            posts: 'Посты',
            newPost: 'Новый пост'
        },
        create: {
            title: 'Создать форум',
            name: 'Название',
            namePlaceholder: 'Введите название форума…',
            description: 'Описание',
            descriptionPlaceholder: 'Введите описание…',
            topics: 'Темы',
            topicsPlaceholder: 'Введите темы через запятую…',
            topicsHelper: 'Темы разделяются запятой',
            domain: 'Домен (опционально)',
            domainNone: 'Без домена',
            informational: 'Информационный',
            public: 'Публичный',
            pinned: 'Закрепить',
            minAllowedRole: 'Мин. роль для публикации',
            roleNone: 'Без ограничения',
            minAllowedRoleHelper: 'Роли от выбранной и выше могут писать',
            allowedUpTo: 'До ролей',
            postPlaceholder: 'Ваше сообщение…',
            submit: 'Создать'
        },
        confirm: {
            deleteForum: 'Вы уверены, что хотите удалить этот форум?'
        },
        status: {
            active: 'Активный',
            informational: 'Информационный',
            archived: 'Архивный',
            banned: 'Забанен'
        },
        detail: {
            createdBy: 'Создал(а)',
            createdAt: 'Создано',
            status: 'Статус',
            newPostTitle: 'Новый пост',
            newPostPlaceholder: 'Ваше сообщение…',
            replyingTo: 'Вы отвечаете на',
            replyToPost: 'Ответить на сообщение',
            addMessage: 'Добавить сообщение',
            settingsTitle: 'Настройки',
            closeForum: 'Закрыть форум',
            archiveForum: 'Архивировать форум',
            resolveForum: 'Решить форум',
            deleteForum: 'Удалить форум',
            reply: 'Ответить',
            inReplyTo: 'В ответ на'
        }
    }
}
