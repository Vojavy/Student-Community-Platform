// src/actions/domainActions.js

export const handleDomainIntent = async (intent, { model }) => {
    switch (intent.type) {
        case 'FETCH_DOMAINS':
            return await model.fetchDomains()

        case 'FETCH_DOMAIN_BY_ID': {
            const { id } = intent.payload
            return await model.fetchDomainById(id)
        }

        case 'FETCH_DOMAIN_BY_CODE': {
            const { domainCode } = intent.payload
            return await model.fetchDomainByCode(domainCode)
        }

        default:
            throw new Error(`Unknown domain intent: ${intent.type}`)
    }
}
