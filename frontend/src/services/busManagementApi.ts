import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api/bus-management';

// Types
export interface Provider {
  id: number;
  code: string;
  name: string;
  status: string;
  routesCount?: number;
  next7dSlots?: number;
}

export interface Route {
  id: number;
  ownerCode: string;
  routeCode: string;
  origin: string;
  destination: string;
  status: string;
  futureSlots?: number;
}

export interface Slot {
  id: number;
  ownerCode: string;
  routeCode: string;
  departTime: string;
  busPlate: string;
  sold: number;
  total: number;
  status: string;
}

export interface KPIs {
  bookingsToday?: number;
  occupancyRate?: number;
  cancellations?: number;
  activeProviders?: number;
}

export interface Alert {
  type: string;
  ownerName: string;
  description: string;
  suggestion: string;
  at: string;
}

export interface ModerationItem {
  id: number;
  type: string;
  ownerCode: string;
  submittedBy: string;
  submittedAt: string;
  status: string;
}

export const busManagementApi = {
  // Overview
  getOverviewKPIs(): Promise<{ data: KPIs }> {
    return axios.get(`${API_BASE_URL}/overview/kpis`);
  },

  getDataQualityAlerts(query = ''): Promise<{ data: Alert[] }> {
    return axios.get(`${API_BASE_URL}/overview/alerts`, {
      params: { query }
    });
  },

  // Providers
  getProviders(query = '', status = ''): Promise<{ data: Provider[] }> {
    return axios.get(`${API_BASE_URL}/providers`, {
      params: { query, status }
    });
  },

  createProvider(providerData: Partial<Provider>): Promise<{ data: Provider }> {
    return axios.post(`${API_BASE_URL}/providers`, providerData);
  },

  updateProvider(id: number, providerData: Partial<Provider>): Promise<{ data: Provider }> {
    return axios.put(`${API_BASE_URL}/providers/${id}`, providerData);
  },

  banProvider(id: number): Promise<any> {
    return axios.put(`${API_BASE_URL}/providers/${id}/ban`);
  },

  unbanProvider(id: number): Promise<any> {
    return axios.put(`${API_BASE_URL}/providers/${id}/unban`);
  },

  // Routes
  getRoutes(owner = '', origin = '', destination = '', status = ''): Promise<{ data: Route[] }> {
    return axios.get(`${API_BASE_URL}/routes`, {
      params: { owner, origin, destination, status }
    });
  },

  createRoute(routeData: Partial<Route>): Promise<{ data: Route }> {
    return axios.post(`${API_BASE_URL}/routes`, routeData);
  },

  updateRoute(id: number, routeData: Partial<Route>): Promise<{ data: Route }> {
    return axios.put(`${API_BASE_URL}/routes/${id}`, routeData);
  },

  submitRouteReview(id: number): Promise<any> {
    return axios.put(`${API_BASE_URL}/routes/${id}/submit-review`);
  },

  approveRoute(id: number): Promise<any> {
    return axios.put(`${API_BASE_URL}/routes/${id}/approve`);
  },

  rejectRoute(id: number): Promise<any> {
    return axios.put(`${API_BASE_URL}/routes/${id}/reject`);
  },

  // Slots
  getSlots(owner = '', route = '', date = ''): Promise<{ data: Slot[] }> {
    return axios.get(`${API_BASE_URL}/slots`, {
      params: { owner, route, date }
    });
  },

  bulkGenerateSlots(slotData: any): Promise<{ data: Slot[] }> {
    return axios.post(`${API_BASE_URL}/slots/bulk-generate`, slotData);
  },

  getSlotDetail(id: number): Promise<{ data: any }> {
    return axios.get(`${API_BASE_URL}/slots/${id}/detail`);
  },

  // Moderation
  getModerationQueue(type = '', status = ''): Promise<{ data: ModerationItem[] }> {
    return axios.get(`${API_BASE_URL}/moderation/queue`, {
      params: { type, status }
    });
  },

  approveModeration(id: number): Promise<any> {
    return axios.put(`${API_BASE_URL}/moderation/${id}/approve`);
  },

  rejectModeration(id: number): Promise<any> {
    return axios.put(`${API_BASE_URL}/moderation/${id}/reject`);
  },

  getModerationDiff(id: number): Promise<{ data: any }> {
    return axios.get(`${API_BASE_URL}/moderation/${id}/diff`);
  }
};
