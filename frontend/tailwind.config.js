// tailwind.config.js
/** @type {import('tailwindcss').Config} */
export default {
    content: [
        './index.html',
        './src/**/*.{vue,js,ts,jsx,tsx}',
    ],
    theme: {
        extend: {
            colors: {
                primary: 'var(--color-primary-bg)',
                secondary: 'var(--color-secondary-bg)',
                text: 'var(--color-primary-text)',
                accent: {
                    primary: 'var(--color-accent-primary)',
                    secondary: 'var(--color-accent-secondary)',
                },
                success: 'var(--color-success)',
                error: 'var(--color-error)',
            },
        }
    },
    plugins: []
}
