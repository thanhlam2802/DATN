export const TOKEN_KEY = "t_";
export const REFRESH_TOKEN_KEY = "rt_";
export const saveAccessToken = (token) => {
  localStorage.setItem(TOKEN_KEY, token);
};

export const saveRefreshToken = (token) => {
  localStorage.setItem(REFRESH_TOKEN_KEY, token);
};

export const getAccessToken = () => {
  return localStorage.getItem(TOKEN_KEY);
};

export const clearToken = () => {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(REFRESH_TOKEN_KEY);
};

export const getBearerToken = () => {

  const token = "Bearer " + getAccessToken();
  console.log("Bearer token", token);
  return token;
};


