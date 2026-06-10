import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
  plugins: [react()],
  css: {
    postcss: "./postcss.config.js",
  },
  server: {
    port: 3000,
    proxy: {
      "/api": {
        target: 'http://26.249.200.184:8404',
        // target: "http://localhost:8404",
        changeOrigin: true,
        secure: false,
      },
    },
  },
});
