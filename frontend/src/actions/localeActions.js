// actions/localeActions.js
export const handleLocaleIntent = async (intent, { model }) => {
    switch (intent.type) {
        case 'CHANGE_LOCALE':
            try {
                model.setLocale(intent.payload)
            } catch (e) {
                console.error('Locale change failed', e)
            }
            break
    }
}
