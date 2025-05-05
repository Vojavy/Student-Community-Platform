import SockJS from 'sockjs-client'
import { Client } from '@stomp/stompjs'

const getSockJsEndpoint = () => {
    const custom = import.meta.env.VITE_WS_BASE_URL
    if (custom) {
        return custom.replace(/\/$/, '') + '/ws/chat'
    }
    const apiBase = import.meta.env.VITE_BASE_API_URL.replace(/\/$/, '')
    return apiBase + '/ws/chat'
}

const wsClient = {
    client: null,
    isConnected: false,
    pendingSubscriptions: [],
    requestInterceptors: [],
    messageInterceptors: [],

    addRequestInterceptor(fn) {
        this.requestInterceptors.push(fn)
    },
    addMessageInterceptor(fn) {
        this.messageInterceptors.push(fn)
    },

    connect() {
        if (this.client && this.isConnected) {
            return Promise.resolve()
        }
        return new Promise((resolve, reject) => {
            const token = localStorage.getItem('token')
            const endpoint = getSockJsEndpoint()
            this.client = new Client({
                webSocketFactory: () => new SockJS(endpoint),
                connectHeaders: {
                    Authorization: token ? `Bearer ${token}` : undefined
                },
                debug: msg => console.debug('[STOMP]', msg),
                onConnect: frame => {
                    this.isConnected = true
                    this.pendingSubscriptions.forEach(({ dest, cb }) => {
                        this.subscribe(dest, cb)
                    })
                    this.pendingSubscriptions = []
                    resolve(frame)
                },
                onStompError: frame => {
                    console.error('[STOMP-ERROR]', frame)
                    reject(frame)
                }
            })
            this.client.activate()
        })
    },

    disconnect() {
        if (this.client) {
            this.client.deactivate()
            this.isConnected = false
        }
    },

    subscribe(destination, callback) {
        if (!this.isConnected) {
            this.pendingSubscriptions.push({ dest: destination, cb: callback })
            return
        }
        this.client.subscribe(destination, msg => {
            let body = JSON.parse(msg.body)
            this.messageInterceptors.forEach(fn => {
                const result = fn(body)
                if (result !== undefined) body = result
            })
            callback(body)
        })
    },

    send(destination, payload = {}) {
        let body = payload
        this.requestInterceptors.forEach(fn => {
            const result = fn(body)
            if (result !== undefined) body = result
        })
        this.client.publish({ destination, body: JSON.stringify(body) })
    }
}

export default wsClient
