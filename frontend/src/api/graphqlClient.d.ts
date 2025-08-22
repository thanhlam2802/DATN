/**
 * Type declarations for graphqlClient.js
 */

export interface GraphQLClient {
  request: <T = any>(query: string, variables?: any) => Promise<T>
  setHeaders: (headers: Record<string, string>) => void
  options: {
    headers: Record<string, string>
  }
}

export const graphqlClient: GraphQLClient

export function setAuthToken(token?: string): void

export function handleGraphQLError(error: any): {
  isGraphQLError?: boolean
  isHTTPError?: boolean  
  isNetworkError?: boolean
  errors?: any[]
  messages?: string[]
  message: string
  status?: number
}

export function graphqlRequest<T = any>(query: string, variables?: any): Promise<T>

export function checkGraphQLHealth(): Promise<{
  healthy: boolean
  endpoint: string
  error?: string
}>

export function buildFragment(name: string, typeName: string, fields: string[]): string

export function buildQuery(name: string, args?: string[], fragment?: string): string

export function buildMutation(name: string, args?: string[], fragment?: string): string

export const GraphQLTypes: {
  BusCategoryInput: Record<string, string>
  BusInput: Record<string, string>
  RouteInput: Record<string, string>
  BusRouteInput: Record<string, string>
}

export default GraphQLClient 