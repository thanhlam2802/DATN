import { graphqlRequest } from '../graphqlClient'
import type { Location, LocationResponse } from './types'

// GraphQL Queries
const GET_AVAILABLE_ORIGINS = `
  query GetAvailableOrigins {
    getAvailableOrigins {
      location
      count
    }
  }
`

const GET_AVAILABLE_DESTINATIONS = `
  query GetAvailableDestinations($origin: String!) {
    getAvailableDestinations(origin: $origin) {
      location  
      count
    }
  }
`

export class LocationAPI {
  /**
   * Get all available departure locations
   */
  static async getAvailableOrigins(): Promise<Location[]> {
    try {
      const response = await graphqlRequest({
        query: GET_AVAILABLE_ORIGINS,
        variables: {}
      })

      const origins = response.data?.getAvailableOrigins || []
      
      // Transform to Location format
      return origins.map((item: any, index: number) => ({
        id: `origin_${index}`,
        name: item.location,
        count: item.count
      }))
    } catch (error) {
      console.error('❌ Error fetching origins:', error)
      // Return fallback data
      return this.getFallbackOrigins()
    }
  }

  /**
   * Get available destinations based on selected origin
   */
  static async getAvailableDestinations(origin: string): Promise<Location[]> {
    try {
      const response = await graphqlRequest({
        query: GET_AVAILABLE_DESTINATIONS,
        variables: { origin }
      })

      const destinations = response.data?.getAvailableDestinations || []
      
      // Transform to Location format
      return destinations.map((item: any, index: number) => ({
        id: `dest_${index}`,
        name: item.location,
        count: item.count
      }))
    } catch (error) {
      console.error('❌ Error fetching destinations:', error)
      // Return fallback data
      return this.getFallbackDestinations(origin)
    }
  }

  /**
   * Search locations by name (for autocomplete)
   */
  static async searchLocations(searchTerm: string, type: 'origin' | 'destination' = 'origin'): Promise<Location[]> {
    try {
      if (type === 'origin') {
        const origins = await this.getAvailableOrigins()
        return origins.filter(location => 
          location.name.toLowerCase().includes(searchTerm.toLowerCase())
        )
      } else {
        // For destinations, we need an origin first
        return []
      }
    } catch (error) {
      console.error('❌ Error searching locations:', error)
      return []
    }
  }

  /**
   * Fallback origins when API fails
   */
  private static getFallbackOrigins(): Location[] {
    return [
      { id: 'hn', name: 'Hà Nội', count: 15 },
      { id: 'hcm', name: 'TP. Hồ Chí Minh', count: 20 },
      { id: 'dn', name: 'Đà Nẵng', count: 8 },
      { id: 'hp', name: 'Hải Phòng', count: 5 },
      { id: 'ct', name: 'Cần Thơ', count: 6 },
      { id: 'nt', name: 'Nha Trang', count: 4 },
      { id: 'dl', name: 'Đà Lạt', count: 3 },
      { id: 'qn', name: 'Quảng Ninh', count: 2 }
    ]
  }

  /**
   * Fallback destinations when API fails
   */
  private static getFallbackDestinations(origin: string): Location[] {
    const destinationMap: Record<string, Location[]> = {
      'Hà Nội': [
        { id: 'hcm', name: 'TP. Hồ Chí Minh', count: 8 },
        { id: 'dn', name: 'Đà Nẵng', count: 5 },
        { id: 'hp', name: 'Hải Phòng', count: 3 },
        { id: 'qn', name: 'Quảng Ninh', count: 2 }
      ],
      'TP. Hồ Chí Minh': [
        { id: 'hn', name: 'Hà Nội', count: 8 },
        { id: 'dn', name: 'Đà Nẵng', count: 6 },
        { id: 'nt', name: 'Nha Trang', count: 4 },
        { id: 'dl', name: 'Đà Lạt', count: 3 },
        { id: 'ct', name: 'Cần Thơ', count: 4 }
      ],
      'Đà Nẵng': [
        { id: 'hn', name: 'Hà Nội', count: 5 },
        { id: 'hcm', name: 'TP. Hồ Chí Minh', count: 6 },
        { id: 'nt', name: 'Nha Trang', count: 3 }
      ]
    }

    return destinationMap[origin] || [
      { id: 'hn', name: 'Hà Nội', count: 5 },
      { id: 'hcm', name: 'TP. Hồ Chí Minh', count: 8 }
    ]
  }

  /**
   * Get popular routes (most booked)
   */
  static getPopularRoutes(): { from: string; to: string; count: number }[] {
    return [
      { from: 'Hà Nội', to: 'TP. Hồ Chí Minh', count: 156 },
      { from: 'TP. Hồ Chí Minh', to: 'Đà Nẵng', count: 89 },
      { from: 'Hà Nội', to: 'Đà Nẵng', count: 67 },
      { from: 'TP. Hồ Chí Minh', to: 'Nha Trang', count: 45 },
      { from: 'Hà Nội', to: 'Hải Phòng', count: 34 }
    ]
  }
} 