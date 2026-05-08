import client from './client';

export const login = (payload) => client.post('/api/local/auth/login', payload);
export const listLocalClasses = () => client.get('/api/local/classes');
export const listSelectedCourses = () => client.get('/api/local/choices');
export const selectLocalCourse = (payload) => client.post('/api/local/choices', payload);
export const dropLocalCourse = (payload) => client.delete('/api/local/choices', { data: payload });
export const listSharedClasses = (target) => client.get('/api/cross/classes', { params: { target } });
export const selectCrossCourse = (payload) => client.post('/api/cross/choices', payload);
export const dropCrossCourse = (payload) => client.delete('/api/cross/choices', { data: payload });
