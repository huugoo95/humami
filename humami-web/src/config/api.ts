import axios from 'axios';

const isBrowser = typeof window !== 'undefined';

// Important: NEXT_PUBLIC_* values are baked into the frontend bundle at build time.
// In browser/runtime deployments behind nginx, a relative `/api` fallback is safer
// than relying on a build-time absolute public API URL.
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
