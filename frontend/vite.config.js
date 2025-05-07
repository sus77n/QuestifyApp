import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
    plugins: [react()],
    server: {
        port: 3000, // Frontend runs on 3000
        proxy: {
            '/api': {
                target: 'http://192.168.1.114:8080', // Your backend URL
                changeOrigin: true,
                secure: false,
            }
        }
    }
});