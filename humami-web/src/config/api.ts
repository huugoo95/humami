import axios from 'axios';

export const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL ||
  process.env.NEXT_PUBLIC_API_URL ||
  '';

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
