/**
 * Route API operations
 */

import { graphqlRequest } from '../../graphqlClient';
import { FIND_ALL_ROUTES, FIND_ROUTE_BY_ID, GET_ROUTES_BY_OWNER_ID, GET_POPULAR_ROUTES } from './queries';
import { CREATE_ROUTE, UPDATE_ROUTE, DELETE_ROUTE } from './mutations';
import type { Route, CreateRouteInput, UpdateRouteInput, RouteCard } from './types';

export const RouteAPI = {
  async getAllRoutes(): Promise<Route[]> {
    try {
      const response = await graphqlRequest({ query: FIND_ALL_ROUTES });
      return response.data.findAllRoutes || [];
    } catch (error) {
      
      throw error;
    }
  },

  // ✅ UPDATED: Thực sự lấy routes theo ownerId từ backend
  async getRoutesByOwnerId(ownerId: string): Promise<Route[]> {
    try {
      const response = await graphqlRequest({ 
        query: GET_ROUTES_BY_OWNER_ID, 
        variables: { ownerId } 
      });
      return response.data.getRoutesByOwnerId || [];
    } catch (error) {
      throw error;
    }
  },

  async getRouteById(id: string): Promise<Route | null> {
    try {
      const response = await graphqlRequest({ 
        query: FIND_ROUTE_BY_ID, 
        variables: { id } 
      });
      return response.data.findRouteById;
    } catch (error) {
      throw error;
    }
  },

  async createRoute(input: CreateRouteInput): Promise<Route> {
    try {
      const response = await graphqlRequest({
        query: CREATE_ROUTE,
        variables: { input }
      });
      return response.data.createRoute;
    } catch (error) {
      throw error;
    }
  },

  async updateRoute(id: string, input: UpdateRouteInput): Promise<Route> {
    try {
      const response = await graphqlRequest({
        query: UPDATE_ROUTE,
        variables: { id, input }
      });
      return response.data.updateRoute;
    } catch (error) {
      throw error;
    }
  },

  async deleteRoute(id: string): Promise<boolean> {
    try {
      const response = await graphqlRequest({
        query: DELETE_ROUTE,
        variables: { id }
      });
      return response.data.deleteRoute;
    } catch (error) {
      throw error;
    }
  },

  // ✅ THÊM MỚI: Lấy popular routes cho trang chủ
  async getPopularRoutes(limit: number = 10): Promise<RouteCard[]> {
    try {
      const response = await graphqlRequest({
        query: GET_POPULAR_ROUTES,
        variables: { limit }
      });
      return response.data.popularRoutes || [];
    } catch (error) {
      throw error;
    }
  }
}; 