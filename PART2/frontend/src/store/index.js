import { defineStore } from 'pinia';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    loggedIn: Boolean(localStorage.getItem('auth_token')),
    user: localStorage.getItem('auth_student_id')
      ? { studentId: localStorage.getItem('auth_student_id'), username: localStorage.getItem('auth_username') }
      : null,
    token: localStorage.getItem('auth_token')
  }),
  actions: {
    setLogin(user) {
      this.loggedIn = true;
      this.user = user;
      this.token = user?.token || null;
    },
    logout() {
      this.loggedIn = false;
      this.user = null;
      this.token = null;
    }
  }
});
