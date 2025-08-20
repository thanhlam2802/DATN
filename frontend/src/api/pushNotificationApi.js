import axios from "axios";
import { getBearerToken, getAccessToken } from "@/services/TokenService.js";

const API_BASE_URL = `${import.meta.env.VITE_API_URL}/api/v1/admin/push-subscriptions`;

export const savePushSubscription = (subscription) => {
    const headers = { Authorization: getBearerToken() };
    
    const token = getAccessToken();
    let userId = null;
    
    if (token) {
        try {
            const payloadBase64 = token.split(".")[1];
            const decodedJson = atob(
                payloadBase64.replace(/-/g, "+").replace(/_/g, "/")
            );
            const userData = JSON.parse(decodedJson);
            userId = userData.userId || userData.sub;
            console.log('User data from token:', userData);
            console.log('UserId from token:', userId);
        } catch (error) {
            console.error('Error parsing token:', error);
        }
    }
    
    console.log('Saving subscription for user:', userId);
    console.log('Subscription data:', subscription.toJSON());
    
    return axios.post(API_BASE_URL, {
        subscription: subscription.toJSON(),
        userId: parseInt(userId)
    }, { headers });
};

export const removePushSubscription = (subscription) => {
    const headers = { Authorization: getBearerToken() };
    
    const token = getAccessToken();
    let userId = null;
    
    if (token) {
        try {
            const payloadBase64 = token.split(".")[1];
            const decodedJson = atob(
                payloadBase64.replace(/-/g, "+").replace(/_/g, "/")
            );
            const userData = JSON.parse(decodedJson);
            userId = userData.userId || userData.sub;
        } catch (error) {
            console.error('Error parsing token:', error);
        }
    }
    
    return axios.delete(API_BASE_URL, {
        headers,
        data: {
            subscription: subscription.toJSON(),
            userId: parseInt(userId)
        }
    });
};

export const getPushSubscriptionStatus = () => {
    const headers = { Authorization: getBearerToken() };
    return axios.get(`${API_BASE_URL}/status`, { headers });
};

export const testPushNotification = () => {
    const headers = { Authorization: getBearerToken() };
    return axios.post(`${API_BASE_URL}/test`, {}, { headers });
};

export default {
    savePushSubscription,
    removePushSubscription,
    getPushSubscriptionStatus,
    testPushNotification
}; 