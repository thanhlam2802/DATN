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

export const BusAPI = {
  // Simple test query để debug connection
  async testSimpleQuery(): Promise<any> {
    try {
      console.log('🔍 Testing simple bus query...');
      const simpleQuery = `
        query TestSimpleBusQuery {
          findAllBuses {
            id
            name
            licensePlate
          }
        }
      `;
      
      const response = await graphqlRequest({ 
        query: simpleQuery 
      });
      console.log('✅ Simple query success:', response.data);
      return response.data;
    } catch (error) {
      console.error('❌ Simple query failed:', error);
      throw error;
    }
  },

  // Test query with ownerId but simple fields
  async testOwnerQuery(ownerId: string): Promise<any> {
    try {
      console.log('🔍 Testing owner query with ownerId:', ownerId);
      const ownerQuery = `
        query TestOwnerQuery($ownerId: ID!) {
          findBusesByOwnerId(ownerId: $ownerId) {
            id
            name
            licensePlate
            totalSeats
            categoryId
            ownerId
          }
        }
      `;
      
      const response = await graphqlRequest({ 
        query: ownerQuery,
        variables: { ownerId }
      });
      console.log('✅ Owner query success:', response.data);
      return response.data;
    } catch (error) {
      console.error('❌ Owner query failed:', error);
      throw error;
    }
  },

  // Backup query không dùng fragment để debug
  async getBusesByOwnerIdBackup(ownerId: string): Promise<any> {
    try {
      console.log('🔧 Using backup query without fragments...');
      const backupQuery = `
        query GetBusesByOwnerIdBackup($ownerId: ID!) {
          findBusesByOwnerId(ownerId: $ownerId) {
            id
            name
            licensePlate
            totalSeats
            categoryId
            categoryName
            ownerId
            ownerName
            busImages {
              busId
              imageId
              image {
                id
                url
                altText
                
              }
            }
              createdAt
              updatedAt
          }
        }
      `;
      
      const response = await graphqlRequest({ 
        query: backupQuery,
        variables: { ownerId }
      });
      console.log('✅ Backup query success:', response.data);
      return response.data.findBusesByOwnerId || [];
    } catch (error) {
      console.error('❌ Backup query failed:', error);
      throw error;
    }
  },

  // Debug function để test GraphQL connection
  async testConnection(): Promise<{ success: boolean, error?: string, data?: number }> {
    try {
      const simpleQuery = `
        query TestConnection {
          findAllBuses {
            id
            name
          }
        }
      `;
      
      const response = await graphqlRequest({ 
        query: simpleQuery 
      });
      
      return { 
        success: true, 
        data: response.data?.findAllBuses?.length || 0 
      };
    } catch (error: any) {
      console.error('❌ GraphQL Test Connection Failed:', error);
      return { 
        success: false, 
        error: error?.message || 'Unknown error' 
      };
    }
  },

    async getAllBuses(): Promise<Bus[]> {
    try {
      console.log('🚌 Getting all buses...');
      
      // Step 1: Try simple query first
      console.log('🔍 Step 1: Testing simple getAllBuses query...');
      try {
        const simpleResponse = await graphqlRequest({ 
          query: FIND_ALL_BUSES_SIMPLE
        });
        console.log('✅ Step 1 Success: Simple getAllBuses worked, buses found:', simpleResponse.data?.findAllBuses?.length || 0);
        
        if (simpleResponse.data?.findAllBuses) {
          return simpleResponse.data.findAllBuses;
        }
      } catch (simpleError: any) {
        console.error('❌ Step 1 Failed: Simple getAllBuses failed:', simpleError.message);
      }
      
      // Step 2: Try full query with fragments
      console.log('🔍 Step 2: Testing full getAllBuses query with fragments...');
      try {
        const fullResponse = await graphqlRequest({ 
          query: FIND_ALL_BUSES
        });
        console.log('✅ Step 2 Success: Full getAllBuses worked, buses found:', fullResponse.data?.findAllBuses?.length || 0);
        
        if (fullResponse.data?.findAllBuses) {
          return fullResponse.data.findAllBuses;
        }
      } catch (fullError: any) {
        console.error('❌ Step 2 Failed: Full getAllBuses failed:', fullError.message);
      }
      
      console.log('🔄 All getAllBuses methods failed, returning empty array');
      return [];
      
    } catch (error: any) {
      console.error('❌ Critical error in getAllBuses:', error);
      return [];
    }
  },

  async getBusById(id: string): Promise<Bus | null> {
    try {
      const response = await graphqlRequest({ 
        query: FIND_BUS_BY_ID, 
        variables: { id } 
      });
      return response.data.findBusById;
    } catch (error) {
      throw error;
    }
  },

  async getBusesByOwnerId(ownerId: string): Promise<Bus[]> {
    try {
      console.log('🚌 Starting getBusesByOwnerId with ownerId:', ownerId);
      
      // Try full fragment with busImages first
      console.log('🔍 Testing full fragment with busImages...');
      try {
        const fullResponse = await graphqlRequest({ 
          query: FIND_BUSES_BY_OWNER_ID, 
          variables: { ownerId } 
        });
        console.log('✅ Full query Success: Buses with images found:', fullResponse.data?.findBusesByOwnerId?.length || 0);
        
        if (fullResponse.data?.findBusesByOwnerId) {
          return fullResponse.data.findBusesByOwnerId;
        }
      } catch (fullError: any) {
        console.error('❌ Full query failed:', fullError.message);
        console.log('🔄 Falling back to simple query...');
      }
      
      // Fallback: Test with simple fragment 
      console.log('🔍 Fallback: Testing with simple fragment...');
      try {
        const simpleResponse = await graphqlRequest({ 
          query: FIND_BUSES_BY_OWNER_ID_SIMPLE,
          variables: { ownerId }
        });
        console.log('✅ Simple query Success: Basic buses found:', simpleResponse.data?.findBusesByOwnerId?.length || 0);
        
        if (simpleResponse.data?.findBusesByOwnerId) {
          return simpleResponse.data.findBusesByOwnerId;
        }
             } catch (simpleError: any) {
         console.error('❌ Fallback Failed: Simple query failed:', simpleError.message);
         console.error('🔍 Simple error details:', simpleError);
      }
      
      // If all steps fail, return empty array
      console.log('🔄 All query methods failed, returning empty array as fallback');
      return [];
       
    } catch (error: any) {
      console.error('❌ Critical error in getBusesByOwnerId:', error);
      console.error('📊 Error details:', {
        message: error.message,
        response: error.response?.data,
        status: error.response?.status
      });
      
      // Return empty array as ultimate fallback
      console.log('🔄 Returning empty array as fallback');
      return [];
    }
  },

  async createBus(input: CreateBusInput): Promise<Bus> {
    try {
      const response = await graphqlRequest({ 
        query: CREATE_BUS, 
        variables: { input } 
      });
      return response.data.createBus;
    } catch (error) {
      throw error;
    }
  },

  async updateBus(id: string, input: UpdateBusInput): Promise<Bus> {
    try {
      const response = await graphqlRequest({ 
        query: UPDATE_BUS, 
        variables: { id, input } 
      });
      return response.data.updateBus;
    } catch (error) {
      throw error;
    }
  },

  async deleteBus(id: string): Promise<boolean> {
    try {
      const response = await graphqlRequest({ 
        query: DELETE_BUS, 
        variables: { id } 
      });
      return response.data.deleteBus;
    } catch (error) {
      throw error;
    }
  }
};
