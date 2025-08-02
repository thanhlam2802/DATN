import axios from 'axios'
// @ts-ignore
import { getBearerToken } from "@/services/TokenService.js";

interface GraphQLRequestPayload {
  query: string | any;
  variables?: Record<string, any>;
}

interface GraphQLResponse<T = any> {
  data?: T;
  errors?: Array<{
    message: string;
    locations?: Array<{ line: number; column: number }>;
    path?: Array<string | number>;
  }>;
}

// API Configuration
const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';
const GRAPHQL_ENDPOINT = `${API_BASE_URL}/graphql`;

// Create axios instance with default config
const graphqlAxios = axios.create({
  baseURL: GRAPHQL_ENDPOINT,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor for auth token - sử dụng TokenService
graphqlAxios.interceptors.request.use((config) => {
  const token = getBearerToken();
  if (token && token !== 'Bearer null') {
    config.headers.Authorization = token;
  }
  return config;
});

// Response interceptor for error handling
graphqlAxios.interceptors.response.use(
  (response) => response,
  (error) => {
    // Handle authentication errors
    if (error.response?.status === 401) {
      // Clear invalid token
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      // Redirect to login if needed
      if (window.location.pathname !== '/login') {
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);

// Helper function to convert DocumentNode to string
const getQueryString = (query: string | any): string => {
  if (typeof query === 'string') {
    return query;
  }
  
  // If it's a DocumentNode (from gql``), extract the query string
  if (query && query.loc && query.loc.source && query.loc.source.body) {
    return query.loc.source.body;
  }
  
  // If it has definitions (GraphQL AST)
  if (query && query.definitions) {
    // Simple conversion - in production you might want to use print() from graphql
    return query.definitions[0]?.loc?.source?.body || String(query);
  }
  
  return String(query);
};

// Main GraphQL request function
export const graphqlRequest = async ({ query, variables = {} }: GraphQLRequestPayload) => {
  try {
    const queryString = getQueryString(query);
    
    const response = await graphqlAxios.post('', {
      query: queryString,
      variables
    });

    const result: GraphQLResponse = response.data;

    // Check for GraphQL errors
    if (result.errors && result.errors.length > 0) {
      throw new Error(result.errors[0].message);
    }

    // Return in consistent format 
    return {
      data: result.data,
      loading: false,
      error: null
    };
    
  } catch (error: any) {
    // Log response details if available
    if (error.response) {
      const status = error.response.status;
      const message = error.response.data?.message || error.message;
      throw new Error(`GraphQL Error (${status}): ${message}`);
    }
    
    throw new Error(error.message || 'GraphQL request failed');
  }
};

// GraphQL mutation function  
export const graphqlMutation = async ({ query, variables = {} }: GraphQLRequestPayload) => {
  try {
    const queryString = getQueryString(query);
    

    
    const response = await graphqlAxios.post('', {
      query: queryString,
      variables
    });

    const result: GraphQLResponse = response.data;

    // Check for GraphQL errors
    if (result.errors && result.errors.length > 0) {
      throw new Error(result.errors[0].message);
    }

    // Return in consistent format
    return {
      data: result.data,
      loading: false,
      error: null
    };
    
  } catch (error: any) {
    // Handle network errors
    if (error.response) {
      const status = error.response.status;
      const message = error.response.data?.message || error.message;
      throw new Error(`GraphQL Error (${status}): ${message}`);
    }
    
    throw new Error(error.message || 'GraphQL mutation failed');
  }
};

// Helper function to set authorization token
export const setAuthToken = (token: string) => {
  localStorage.setItem('accessToken', token);
};

// Helper function to remove authorization token
export const clearAuthToken = () => {
  localStorage.removeItem('accessToken');
  localStorage.removeItem('refreshToken');
};

// Simple gql template literal tag (for compatibility)
export const gql = (strings: TemplateStringsArray, ...values: any[]) => {
  let result = strings[0];
  for (let i = 1; i < strings.length; i++) {
    result += values[i - 1] + strings[i];
  }
  return result;
};



// Default export for compatibility
export default {
  request: graphqlRequest,
  mutate: graphqlMutation,
  setAuthToken,
  clearAuthToken
};