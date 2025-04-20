import { ref, onMounted, onBeforeUnmount } from 'vue'

export function useIsMobile(breakpoint = 768) {
    const isMobile = ref(window.innerWidth <= breakpoint)

    const update = () => {
        isMobile.value = window.innerWidth <= breakpoint
    }

    onMounted(() => {
        window.addEventListener('resize', update)
    })

    onBeforeUnmount(() => {
        window.removeEventListener('resize', update)
    })

    return isMobile
}
