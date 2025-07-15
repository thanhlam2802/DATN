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