/**
 * This module centralizes and exports all APIs and types
 * related to Bus and BusCategory.
 */

import { BusAPI as BusAPIObject } from './api';
import * as BusCategoryAPI from './categoryApi';

// Re-export all types from this module for easy access
export * from './types';

// Export the APIs properly
export const BusAPI = BusAPIObject;
export { BusCategoryAPI };

// ❌ REMOVED: getBusCategoriesByOwnerId export - BusCategory là global 