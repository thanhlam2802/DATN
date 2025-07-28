/**
 * Bus API operations
 */

import { graphqlRequest } from '../../graphqlClient';
import { 
  FIND_ALL_BUSES, 
  FIND_BUS_BY_ID, 
  FIND_BUSES_BY_OWNER_ID, 
  FIND_BUSES_BY_OWNER_ID_SIMPLE,
  FIND_BUSES_BY_OWNER_ID_WITH_SIMPLE_FRAGMENT,
  FIND_ALL_BUSES_SIMPLE
} from './queries';
import { CREATE_BUS, UPDATE_BUS, DELETE_BUS } from './mutations';
import type { Bus, CreateBusInput, UpdateBusInput } from './types';

export class BusAPI {
  /**
   * Test simple GraphQL query for debugging
   */
  static async testSimpleQuery() {
    try {
      const response = await graphqlRequest({
        query: `
          query TestSimpleQuery {
            __typename
          }
        `,
        variables: {}
      });

      return response.data;
    } catch (error) {
      console.error('❌ Simple query failed:', error);
      throw error;
    }
  }

  /**
   * Test owner-specific GraphQL query
   */
  static async testOwnerQuery(ownerId: string) {
    try {
      const response = await graphqlRequest({
        query: FIND_BUSES_BY_OWNER_ID_SIMPLE,
        variables: { ownerId }
      });

      return response.data?.findBusesByOwnerId || [];
    } catch (error) {
      console.error('❌ Owner query failed:', error);
      throw error;
    }
  }

  /**
   * Test backup query without fragments
   */
  static async testBackupQuery() {
    try {
      const response = await graphqlRequest({
        query: `
          query TestBackupQuery {
            findAllBuses {
              id
              name
              licensePlate
              totalSeats
              categoryName
            }
          }
        `,
        variables: {}
      });

      return response.data?.findAllBuses || [];
    } catch (error) {
      console.error('❌ Backup query failed:', error);
      throw error;
    }
  }

  /**
   * Get all buses with multiple fallback strategies
   */
  static async getAllBuses() {
    try {
      // Try simple query first
      try {
        const simpleResponse = await graphqlRequest({
          query: FIND_ALL_BUSES_SIMPLE,
          variables: {}
        });

        if (simpleResponse.data?.findAllBuses) {
          return simpleResponse.data.findAllBuses;
        }
      } catch (error) {
        // Continue to next method
      }

      // Try full query with fragments
      try {
        const fullResponse = await graphqlRequest({
          query: FIND_ALL_BUSES,
          variables: {}
        });

        if (fullResponse.data?.findAllBuses) {
          return fullResponse.data.findAllBuses;
        }
      } catch (error) {
        // Continue to next method
      }

      // If all fail, return empty array
      return [];
    } catch (error) {
      console.error('❌ Error in getAllBuses:', error);
      return [];
    }
  }

  /**
   * Get buses by owner ID with multiple query strategies
   */
  static async getBusesByOwnerId(ownerId: string) {
    try {
      // Try full fragment query first (includes busImages)
      try {
        const fullResponse = await graphqlRequest({
          query: FIND_BUSES_BY_OWNER_ID,
          variables: { ownerId }
        });

        if (fullResponse.data?.findBusesByOwnerId && Array.isArray(fullResponse.data.findBusesByOwnerId)) {
          return fullResponse.data.findBusesByOwnerId;
        }
      } catch (error) {
        // Try fallback query
      }

      // Fallback to simple query
      try {
        const simpleResponse = await graphqlRequest({
          query: FIND_BUSES_BY_OWNER_ID_SIMPLE,
          variables: { ownerId }
        });

        if (simpleResponse.data?.findBusesByOwnerId && Array.isArray(simpleResponse.data.findBusesByOwnerId)) {
          return simpleResponse.data.findBusesByOwnerId;
        }
      } catch (error) {
        console.error('❌ Simple query also failed:', error);
      }

      // If both fail, return empty array
      return [];

    } catch (error) {
      console.error('❌ Error in getBusesByOwnerId:', error);
      return [];
    }
  }

  static async getBusById(id: string): Promise<Bus | null> {
    try {
      const response = await graphqlRequest({ 
        query: FIND_BUS_BY_ID, 
        variables: { id } 
      });
      return response.data.findBusById;
    } catch (error) {
      throw error;
    }
  }

  static async createBus(input: CreateBusInput): Promise<Bus> {
    try {
      const response = await graphqlRequest({ 
        query: CREATE_BUS, 
        variables: { input } 
      });
      return response.data.createBus;
    } catch (error) {
      console.error('❌ Error creating bus:', error);
      throw error;
    }
  }

  static async updateBus(id: string, input: UpdateBusInput): Promise<Bus> {
    try {
      const response = await graphqlRequest({ 
        query: UPDATE_BUS, 
        variables: { id, input } 
      });
      return response.data.updateBus;
    } catch (error) {
      console.error('❌ Error updating bus:', error);
      throw error;
    }
  }

  static async deleteBus(id: string): Promise<boolean> {
    try {
      const response = await graphqlRequest({ 
        query: DELETE_BUS, 
        variables: { id } 
      });
      return response.data.deleteBus;
    } catch (error) {
      console.error('❌ Error deleting bus:', error);
      throw error;
    }
  }
};
