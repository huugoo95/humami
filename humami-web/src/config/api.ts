import axios from 'axios';

const isBrowser = typeof window !== 'undefined';

export const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL ||
  process.env.NEXT_PUBLIC_API_URL ||
  (isBrowser ? '/api' : '');

const apiClient = axios.create({
  baseURL: API_BASE_URL || undefined,
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
