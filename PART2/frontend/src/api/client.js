import axios from 'axios';

const client = axios.create({
  baseURL: import.meta.env.VITE_API_BASE || 'http://localhost:8083'
});

client.interceptors.request.use((config) => {
  const token = localStorage.getItem('auth_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default client;
