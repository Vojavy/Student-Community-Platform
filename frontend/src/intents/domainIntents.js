// src/intents/domainIntents.js

export const fetchDomainsIntent = () => ({
    type: 'FETCH_DOMAINS'
})

export const fetchDomainByIdIntent = (id) => ({
    type: 'FETCH_DOMAIN_BY_ID',
    payload: { id }
})

export const fetchDomainByCodeIntent = (domainCode) => ({
    type: 'FETCH_DOMAIN_BY_CODE',
    payload: { domainCode }
})
