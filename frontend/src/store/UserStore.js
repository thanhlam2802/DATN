import {defineStore} from 'pinia'
import {AccountApi} from '@/api/AccountApi.js'
import {clearToken, saveAccessToken} from '@/services/TokenService.js'

export const useUserStore = defineStore('user', {
    state: () => ({
        isLoggedIn: false,
        user: null,
        token: null
    }),
    actions: {
        login(userData, token) {
            this.isLoggedIn = true;
            this.user = userData;
            this.token = token;
            saveAccessToken(token);
        },
        logout() {
            this.isLoggedIn = false;
            this.user = null;
            this.token = null;
            clearToken();
        },
        updateUser(userData) {
            this.user = userData;
        },
        async restoreUserFromToken() {
            console.log('restoreUserFromToken - starting...');
            const token = localStorage.getItem('t_');
            console.log('restoreUserFromToken - token:', token ? 'exists' : 'not found');
            
            if (token) {
                try {
                    console.log('restoreUserFromToken - calling AccountApi.getProfile()...');
                    const profileRes = await AccountApi.getProfile();
                    console.log('restoreUserFromToken - profileRes:', profileRes);
                    
                    if (!profileRes.errorCode) {
                        console.log('restoreUserFromToken - setting user data:', profileRes.data);
                        this.isLoggedIn = true;
                        this.user = profileRes.data;
                        this.token = token;
                        return true;
                    } else {
                        console.log('restoreUserFromToken - errorCode:', profileRes.errorCode);
                        clearToken();
                    }
                } catch (error) {
                    console.error('Failed to restore user from token:', error);
                    clearToken();
                }
            }
            console.log('restoreUserFromToken - failed to restore');
            return false;
        }
    },
})
