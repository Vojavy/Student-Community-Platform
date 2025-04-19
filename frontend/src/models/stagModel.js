import apiClient from '@/utils/api/apiClient'

export default function createStagModel() {
    return {
        async checkStagTicket() {
            const response = await apiClient.get('/auth/check/uni')
            return {
                hasValidTicket: response.data.hasValidToken, // соответствие названий
                ticket: response.data.ticket
            }
        },
        async startStagLogin(domain, returnTo) {
            const redirectUrl = encodeURIComponent(`${returnTo}?domain=${domain}`);
            const stagLoginUrl = `https://stag-ws.${domain}.cz/ws/login?originalURL=${redirectUrl}`;
            window.location.href = stagLoginUrl;
        },
        async saveStagTicket(stagUserTicket, longTicket, domain) {
            const response = await apiClient.post('/stag/ticket', {
                stagUserTicket: stagUserTicket,
                longTicket: longTicket,
                domain: domain
            })
            return response.data
        },
        async deleteStagTicket() {
            const response = await apiClient.delete('/stag/ticket', )
            return response.data
        },
    }
}
