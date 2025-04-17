export default function createLocaleModel() {
    return {
        setLocale({ locale }) {
            localStorage.setItem('locale', locale)
        }
    }
}