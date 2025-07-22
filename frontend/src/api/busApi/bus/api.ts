/**
 * Bus API operations
 */

import { graphqlRequest } from '../../graphqlClient';
import { FIND_ALL_BUSES, FIND_BUS_BY_ID, FIND_BUSES_BY_OWNER_ID } from './queries';
import { CREATE_BUS, UPDATE_BUS, DELETE_BUS } from './mutations';
import type { Bus, CreateBusInput, UpdateBusInput } from './types';

export const BusAPI = {
    async getAllBuses(): Promise<Bus[]> {
    try {
      const response = await graphqlRequest({ query: FIND_ALL_BUSES });
      return response.data.findAllBuses || [];
    } catch (error) {
      throw error;
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
      const response = await graphqlRequest({ 
        query: FIND_BUSES_BY_OWNER_ID, 
        variables: { ownerId } 
      });
      return response.data.findBusesByOwnerId || [];
    } catch (error) {
      throw error;
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
