// src/iam/stores/chatStore.js
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
            // получение истории
            model.subscribeToHistory(history => {
                this.clearMessages()
                this.messages.push(...history)
            })
            // новые сообщения
            model.subscribeToMessages(msg => {
                this.messages.push(msg)
            })
            // отметки прочитанных
            model.subscribeToReads(msg => {
                const found = this.messages.find(m => m.id === msg.id)
                if (found) {
                    found.read   = true
                    found.readAt = msg.readAt
                }
            })
        },

        requestHistory(otherUserId) {
            model.requestHistory(otherUserId)
        },

        /**
         * Отправка сообщения.
         * @param {number} recipientId — ID получателя
         * @param {{text?:string, base64?:string, parentMessageId?:number}} opts
         */
        async sendMessage(recipientId, { text = '', base64 = null, parentMessageId = null } = {}) {
            const payload = {
                recipientId,
                contentText:   text || null,
                contentBase64: base64,
                parentMessageId
            }
            try {
                await model.sendMessage(payload)
            } catch (e) {
                this.error = e
                throw e
            }
        },

        async markAsRead(messageId) {
            try {
                await model.markAsRead(messageId)
            } catch (e) {
                this.error = e
                throw e
            }
        }
    }
})
