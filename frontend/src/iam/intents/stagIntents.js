export const checkStagTicketIntent = () => ({
    type: 'CHECK_STAG_TICKET'
})

export const startStagLoginIntent = (domain) => ({
    type: 'START_STAG_LOGIN',
    payload: { domain }
})

export const saveStagToken = (ticket, longTicket, domain) => ({
    type: 'SAVE_STAG_TICKET',
    payload: { ticket, longTicket, domain }
})

export const deleteStagTicket = () => ({
    type: 'DELETE_STAG_TICKET',
})