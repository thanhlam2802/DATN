import { ApolloClient, InMemoryCache, createHttpLink, gql } from '@apollo/client/core'
import { setContext } from '@apollo/client/link/context'
import { print } from 'graphql'

interface GraphQLRequestPayload {
  query: any; // Có thể là string hoặc DocumentNode từ gql
  variables?: Record<string, any>;
}

// Chỉ định rõ địa chỉ gốc của server backend
const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';
const GRAPHQL_ENDPOINT = `${API_BASE_URL}/graphql`;

// Create HTTP link
const httpLink = createHttpLink({
  uri: GRAPHQL_ENDPOINT,
});

// Create auth link for handling authentication
const authLink = setContext((_, { headers }) => {
  // Get auth token from localStorage (if needed)
  const token = localStorage.getItem('authToken');
  
  return {
    headers: {
      ...headers,
      'Content-Type': 'application/json',
      ...(token && { Authorization: `Bearer ${token}` }),
    }
  };
});

// Create Apollo Client instance
export const apolloClient = new ApolloClient({
  link: authLink.concat(httpLink),
  cache: new InMemoryCache({
    // Configure cache policies if needed
    typePolicies: {
      Query: {
        fields: {
          // Example: Configure how to merge arrays
                     findAllBuses: {
             merge(_existing = [], incoming) {
               return incoming;
             },
           },
          findAllBusSlots: {
            merge(_existing = [], incoming) {
              return incoming;
            },
          },
        },
      },
    },
  }),
  defaultOptions: {
    watchQuery: {
      errorPolicy: 'all',
    },
    query: {
      errorPolicy: 'all',
    },
  },
});

// DEV MODE: Enhanced logging
console.log('🔧 [DEV] Apollo Client initialized for Vue.js:', GRAPHQL_ENDPOINT);

// Legacy graphqlRequest function for backward compatibility
export const graphqlRequest = async ({ query, variables }: GraphQLRequestPayload) => {
  try {
    // Convert DocumentNode to string if needed
    const queryString = typeof query === 'string' ? query : print(query);
    
    console.log('🔧 [DEV] GraphQL Request:', {
      query: queryString.substring(0, 100) + '...',
      variables
    });

    const result = await apolloClient.query({
      query: typeof query === 'string' ? gql(query) : query,
      variables,
      fetchPolicy: 'network-only', // Always fetch fresh data
    });
    
    console.log('✅ [DEV] GraphQL Response received:', result.data);
    return result; // Return full result object with { data, loading, error }
    
  } catch (error: any) {
    console.error('❌ [DEV] GraphQL Error:', error);
    
    // Enhanced error handling
    if (error.graphQLErrors) {
      console.error('GraphQL errors:', error.graphQLErrors);
    }
    if (error.networkError) {
      console.error('Network error:', error.networkError);
    }
    
    // Re-throw the error for handling by components
    throw new Error(error.message || 'GraphQL request failed');
  }
};

// Function for mutation operations
export const graphqlMutation = async ({ query, variables }: GraphQLRequestPayload) => {
  try {
    // Convert DocumentNode to string if needed
    const queryString = typeof query === 'string' ? query : print(query);
    
    console.log('🔧 [DEV] GraphQL Mutation:', {
      mutation: queryString.substring(0, 100) + '...',
      variables
    });

    const result = await apolloClient.mutate({
      mutation: typeof query === 'string' ? gql(query) : query,
      variables,
      fetchPolicy: 'no-cache', // Don't cache mutations
    });
    
    console.log('✅ [DEV] GraphQL Mutation completed:', result.data);
    return result; // Return full result object
    
  } catch (error: any) {
    console.error('❌ [DEV] GraphQL Mutation Error:', error);
    
    // Enhanced error handling
    if (error.graphQLErrors) {
      console.error('GraphQL errors:', error.graphQLErrors);
    }
    if (error.networkError) {
      console.error('Network error:', error.networkError);
    }
    
    // Re-throw the error for handling by components
    throw new Error(error.message || 'GraphQL mutation failed');
  }
};

// Helper function to set authorization token
export const setAuthToken = (token: string) => {
  localStorage.setItem('authToken', token);
  console.log('🔧 [DEV] Auth token set');
};

// Helper function to remove authorization token
export const clearAuthToken = () => {
  localStorage.removeItem('authToken');
  console.log('🔧 [DEV] Auth token cleared');
};

// Export client for Vue plugin setup
export default apolloClient;