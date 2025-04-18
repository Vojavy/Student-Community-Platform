export const handleStagIntent = async (intent, { model, coordinator }) => {
    switch (intent.type) {
        case 'CHECK_STAG_TICKET':
            return await model.checkStagTicket()

        case 'START_STAG_LOGIN':
            try {
                const { domain } = intent.payload
                const originalUrl = window.location.origin + '/user/stag'
                await model.startStagLogin(domain, originalUrl)
            } catch (e) {
                console.error('STAG login failed', e)
            }
            break

        case 'SAVE_STAG_TICKET':
            try {
                const { ticket, longTicket, domain } = intent.payload
                await model.saveStagTicket(ticket, longTicket, domain)
                window.history.replaceState({}, document.title, window.location.pathname);
            } catch (e) {
                console.error('STAG token saving error', e)
            }
            break

        case 'DELETE_STAG_TICKET':
            try {
                await model.deleteStagTicket()
                window.history.replaceState({}, document.title, window.location.pathname);
            } catch (e) {
                console.error('STAG token saving error', e)
            }
            break
        // TODO ПЕРЕНЕСТИ НА БЭК
        case 'FETCH_STUDENT_INFO':
            return await model.fetchStudentInfo(intent.payload.domain)
    }
}
