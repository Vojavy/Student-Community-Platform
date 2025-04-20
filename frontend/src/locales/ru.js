export default {
    common: {
        save: 'Сохранить',
        cancel: 'Отмена',
        add: 'Добавить',
        change: 'Изменить',
        delete: 'Удалить',
        loading: 'Loading...'
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
        button: 'Зарегистрироваться'
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
            cancel: 'Отменить запрос дружбы',
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
            empty:'Не указано',
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
            otherTitle: 'Другие контакты',
        },

        personal: {
            birthDate: 'Дата рождения',
            languages: 'Языки',
            location: 'Местоположение',
            website: 'Веб‑сайт'
        },

        buttons: {
            settings: 'Настройки профиля',
            connectStag: 'Подключить STAG',
            addFriend: 'Добавить в друзья',
            removeFriend: 'Удалить из друзей',
            sendMessage: 'Написать сообщение'
        }
    },
}
