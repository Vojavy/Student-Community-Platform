import wsClient from '@/utils/ws/wsClient.js'

export default function createChatModel() {
    return {
        connect: () => wsClient.connect(),
        disconnect: () => wsClient.disconnect(),

        subscribeToMessages: cb => wsClient.subscribe('/user/queue/messages', cb),
        subscribeToReads: cb => wsClient.subscribe('/user/queue/messages/read', cb),
        subscribeToHistory: cb => wsClient.subscribe('/user/queue/history', cb),

        requestHistory: otherUserId =>
            wsClient.send('/app/chat.history', { otherUserId }),

        sendMessage: payload =>
            wsClient.send('/app/chat.send', payload),

        markAsRead: messageId =>
            wsClient.send(`/app/chat.read.${messageId}`, {}),

        addRequestInterceptor: fn => wsClient.addRequestInterceptor(fn),
        addMessageInterceptor: fn => wsClient.addMessageInterceptor(fn),
    }
}
