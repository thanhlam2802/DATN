export const TOKEN_KEY = "t_"
export const saveAccessToken = (token) => {
    localStorage.setItem(TOKEN_KEY, token);
}

export const getAccessToken = (token) => {
    return localStorage.getItem(TOKEN_KEY);
}

export const clearToken = () => {
    localStorage.removeItem(TOKEN_KEY);
}

export const getBearerToken = () => {
    return "Bearer " + getAccessToken();
}

// ✅ THÊM FUNCTION NÀY:
export const getUserIdFromToken = () => {
    try {
        const token = getAccessToken();
        if (!token) return null;
        
        // Decode JWT payload (base64)
        const payload = JSON.parse(atob(token.split('.')[1]));
        return payload.userId || null;
    } catch (error) {
        console.warn('Cannot decode token:', error);
        return null;
    }
}

// ✅ THÊM FUNCTION NÀY:
export const isTokenValid = () => {
    try {
        const token = getAccessToken();
        if (!token) return false;
        
        const payload = JSON.parse(atob(token.split('.')[1]));
        const currentTime = Date.now() / 1000;
        
        return payload.exp > currentTime;
    } catch (error) {
        return false;
    }
}