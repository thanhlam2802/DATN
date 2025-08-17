/**
 * BusCategory API operations
 */
import { graphqlRequest } from '../../graphqlClient';
import {
  CreateBusCategoryInput,
  UpdateBusCategoryInput
} from './types';
import { gql } from '../../graphqlClient';
import { BusCategory } from '../types/common.types';
import { BUS_CATEGORY_FRAGMENT } from '../fragments';

const GET_ALL_BUS_CATEGORIES = gql`
  query GetAllBusCategories {
    getAllBusCategories {
      ...BusCategoryFragment
    }
  }
  ${BUS_CATEGORY_FRAGMENT}
`;

// ❌ REMOVED: getBusCategoriesByOwnerId - BusCategory là global

const CREATE_BUS_CATEGORY = gql`
  mutation CreateBusCategory($input: CreateBusCategoryInput!) {
    createBusCategory(input: $input) {
      ...BusCategoryFragment
    }
  }
  ${BUS_CATEGORY_FRAGMENT}
`;

const UPDATE_BUS_CATEGORY = gql`
  mutation UpdateBusCategory($id: ID!, $input: UpdateBusCategoryInput!) {
    updateBusCategory(id: $id, input: $input) {
      ...BusCategoryFragment
    }
  }
  ${BUS_CATEGORY_FRAGMENT}
`;

const DELETE_BUS_CATEGORY = gql`
  mutation DeleteBusCategory($id: ID!) {
    deleteBusCategory(id: $id)
  }
`;

export const getAllBusCategories = async (): Promise<BusCategory[]> => {
  try {
    const response = await graphqlRequest({ query: GET_ALL_BUS_CATEGORIES });
    return response.data.getAllBusCategories || [];
  } catch (error) {
    throw error;
  }
};

// ❌ REMOVED: getBusCategoriesByOwnerId - BusCategory là global
// Tất cả nhà xe đều sử dụng getAllBusCategories()

export async function createBusCategory(input: CreateBusCategoryInput): Promise<BusCategory> {
  try {
    const response = await graphqlRequest({ query: CREATE_BUS_CATEGORY, variables: { input } });
    return response.data.createBusCategory;
  } catch (error) {
    throw error;
  }
}

export async function updateBusCategory(id: string, input: UpdateBusCategoryInput): Promise<BusCategory> {
    try {
      const response = await graphqlRequest({ query: UPDATE_BUS_CATEGORY, variables: { id, input } });
      return response.data.updateBusCategory;
    } catch (error) {
      throw error;
    }
}

export async function deleteBusCategory(id: string): Promise<boolean> {
    try {
      const response = await graphqlRequest({ query: DELETE_BUS_CATEGORY, variables: { id } });
      return response.data.deleteBusCategory;
    } catch (error) {
      throw error;
    }
} 