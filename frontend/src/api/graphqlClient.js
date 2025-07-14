/**
 * GraphQL Client Configuration
 * Quản lý kết nối và requests tới GraphQL backend
 */

import { GraphQLClient } from 'graphql-request'

// GraphQL endpoint configuration
const GRAPHQL_ENDPOINT = import.meta.env.VITE_GRAPHQL_ENDPOINT || 'http://localhost:8080/graphql'

// Create GraphQL client instance
export const graphqlClient = new GraphQLClient(GRAPHQL_ENDPOINT, {
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
  },
  // Add error handling
  errorPolicy: 'all'
})

// Request interceptor to add authentication token
graphqlClient.setHeaders = (newHeaders) => {
  graphqlClient.options.headers = {
    ...graphqlClient.options.headers,
    ...newHeaders
  }
}

// Add authentication token when available
export const setAuthToken = (token) => {
  if (token) {
    graphqlClient.setHeaders({
      'Authorization': `Bearer ${token}`
    })
    console.log('🔐 [GraphQL] Auth token set successfully')
  } else {
    // Remove auth header if no token
    const { Authorization, ...headers } = graphqlClient.options.headers
    graphqlClient.options.headers = headers
    console.log('🔓 [GraphQL] Auth token removed')
  }
}

// Error handler wrapper
export const handleGraphQLError = (error) => {
  console.error('❌ [GraphQL] Request failed:', error)
  
  if (error.response?.errors) {
    // GraphQL errors
    const graphqlErrors = error.response.errors
    const errorMessages = graphqlErrors.map(err => err.message)
    
    console.error('🔴 [GraphQL] GraphQL Errors:', errorMessages)
    
    return {
      isGraphQLError: true,
      errors: graphqlErrors,
      messages: errorMessages,
      message: errorMessages[0] || 'GraphQL request failed'
    }
  }
  
  if (error.response?.status) {
    // HTTP errors
    console.error('🔴 [GraphQL] HTTP Error:', error.response.status)
    
    return {
      isHTTPError: true,
      status: error.response.status,
      message: `HTTP Error: ${error.response.status}`
    }
  }
  
  // Network or other errors
  return {
    isNetworkError: true,
    message: error.message || 'Network error occurred'
  }
}

// Enhanced request wrapper with better error handling
export const graphqlRequest = async (query, variables = {}) => {
  try {
    console.log('📤 [GraphQL] Sending request:', { query: query.substring(0, 100) + '...', variables })
    
    const response = await graphqlClient.request(query, variables)
    
    console.log('✅ [GraphQL] Request successful:', response)
    
    return response
  } catch (error) {
    const handledError = handleGraphQLError(error)
    throw handledError
  }
}

// Health check function
export const checkGraphQLHealth = async () => {
  try {
    const healthQuery = `
      query HealthCheck {
        __schema {
          types {
            name
          }
        }
      }
    `
    
    await graphqlClient.request(healthQuery)
    console.log('✅ [GraphQL] Health check passed')
    
    return { healthy: true, endpoint: GRAPHQL_ENDPOINT }
  } catch (error) {
    console.warn('⚠️ [GraphQL] Health check failed:', error.message)
    
    return { 
      healthy: false, 
      endpoint: GRAPHQL_ENDPOINT,
      error: error.message 
    }
  }
}

// Utility functions for common GraphQL patterns
export const buildFragment = (name, typeName, fields) => {
  return `
    fragment ${name} on ${typeName} {
      ${fields.join('\n      ')}
    }
  `
}

export const buildQuery = (name, args, fragment) => {
  const argsString = args && args.length > 0 ? `(${args.join(', ')})` : ''
  
  return `
    query ${name}${argsString} {
      ${fragment}
    }
  `
}

export const buildMutation = (name, args, fragment) => {
  const argsString = args && args.length > 0 ? `(${args.join(', ')})` : ''
  
  return `
    mutation ${name}${argsString} {
      ${fragment}
    }
  `
}

// Type definitions for better development experience
export const GraphQLTypes = {
  // Input types
  BusCategoryInput: {
    name: 'String!',
    description: 'String'
  },
  
  BusInput: {
    name: 'String!',
    categoryId: 'ID!',
    origin: 'String!',
    destination: 'String!',
    departureTime: 'DateTime!',
    arrivalTime: 'DateTime!',
    ownerId: 'ID'
  },
  
  RouteInput: {
    origin: 'String!',
    destination: 'String!',
    distanceKm: 'Float!',
    estimatedDurationMinutes: 'Int!'
  },
  
  BusRouteInput: {
    busId: 'ID!',
    routeId: 'ID!',
    travelDate: 'Date!',
    price: 'Float!',
    status: 'String'
  }
}

// Export default client for direct usage
export default graphqlClient

// Development utilities
if (import.meta.env.DEV) {
  // Auto health check in development
  checkGraphQLHealth().then(result => {
    if (result.healthy) {
      console.log('🚀 [GraphQL] Connected to:', result.endpoint)
    } else {
      console.warn('⚠️ [GraphQL] Failed to connect to:', result.endpoint)
      console.warn('🔄 [GraphQL] Falling back to mock responses')
    }
  })
  
  // Expose client for debugging
  window.__graphqlClient = graphqlClient
  window.__checkGraphQLHealth = checkGraphQLHealth
} 