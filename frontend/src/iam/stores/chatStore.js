import { defineStore } from 'pinia'
import createChatModel from '@/iam/models/chatModel.js'

const model = createChatModel()

export const useChatStore = defineStore('chatStore', {
    state: () => ({
        messages: [],
        connected: false,
        error: null
    }),
    actions: {
        async connect() {
            try {
                await model.connect()
                this.connected = true
            } catch (e) {
                this.error = e
                throw e
            }
        },
        disconnect() {
            model.disconnect()
            this.connected = false
        },
        clearMessages() {
            this.messages = []
        },
        subscribe() {
            model.subscribeToMessages(m => this.messages.push(m))
            model.subscribeToReads(m => {
                const msg = this.messages.find(x => x.id === m.id)
                if (msg) {
                    msg.read = true
                    msg.readAt = m.readAt
                }
            })
            model.subscribeToHistory(history => {
                this.clearMessages()
                this.messages.push(...history)
            })
        },
        requestHistory(otherUserId) {
            model.requestHistory(otherUserId)
        },
        sendMessage(payload) {
            model.sendMessage(payload)
        },
        markAsRead(messageId) {
            model.markAsRead(messageId)
        }
    }
})
