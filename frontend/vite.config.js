import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  define: {
    'process.env.NODE_ENV': JSON.stringify('production'),
  },
  build: {
    outDir: '../src/main/resources/static/react/classifica',
    emptyOutDir: true,
    lib: {
      entry: 'src/main.jsx',
      name: 'ClassificaWidget',
      formats: ['iife'],
      fileName: () => 'classifica.js',
    },
  },
})
