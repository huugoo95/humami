// src/config/api.ts
import axios from 'axios';

const apiClient = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_BASE_URL,
  headers: {
    Accept: 'application/json',
  },
});

apiClient.interceptors.request.use(config => {
  if (config.data instanceof FormData) {
    delete config.headers?.['Content-Type'];
  }
  return config;
});

export default apiClient;