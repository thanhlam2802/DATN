import { graphqlRequest, gql } from '@/api/graphqlClient';

const GET_KPI_OVERVIEW = gql`
  query GetKpiOverview($ownerId: ID!, $startDate: String!, $endDate: String!) {
    kpiOverview(ownerId: $ownerId, startDate: $startDate, endDate: $endDate) {
      totalRevenue
      totalBookings
      totalTrips
      occupancyRate
    }
  }
`;

const GET_REVENUE_OVER_TIME = gql`
    query GetRevenueOverTime($ownerId: ID!, $startDate: String!, $endDate: String!) {
        revenueOverTime(ownerId: $ownerId, startDate: $startDate, endDate: $endDate) {
            labels
            data
        }
    }
`;

const GET_OCCUPANCY_BY_ROUTE = gql`
    query GetOccupancyByRoute($ownerId: ID!, $startDate: String!, $endDate: String!) {
        occupancyByRoute(ownerId: $ownerId, startDate: $startDate, endDate: $endDate) {
            labels
            data
        }
    }
`;

const GET_TOP_PERFORMING_ROUTES = gql`
    query GetTopPerformingRoutes($ownerId: ID!, $startDate: String!, $endDate: String!) {
        topPerformingRoutes(ownerId: $ownerId, startDate: $startDate, endDate: $endDate) {
            id
            name
            revenue
            bookings
        }
    }
`;


export const StatisticsAPI = {
  async getKpiOverview(ownerId, dateRange) {
    const [startDate, endDate] = dateRange.map(date => date.toISOString().split('T')[0]);
    const response = await graphqlRequest({
      query: GET_KPI_OVERVIEW,
      variables: { ownerId, startDate, endDate }
    });
    return response.data.kpiOverview;
  },

  async getRevenueOverTime(ownerId, dateRange) {
    const [startDate, endDate] = dateRange.map(date => date.toISOString().split('T')[0]);
    const response = await graphqlRequest({
        query: GET_REVENUE_OVER_TIME,
        variables: { ownerId, startDate, endDate }
    });
    return response.data.revenueOverTime;
  },

    async getOccupancyByRoute(ownerId, dateRange) {
        const [startDate, endDate] = dateRange.map(date => date.toISOString().split('T')[0]);
        const response = await graphqlRequest({
            query: GET_OCCUPANCY_BY_ROUTE,
            variables: { ownerId, startDate, endDate }
        });
        return response.data.occupancyByRoute;
    },

    async getTopPerformingRoutes(ownerId, dateRange) {
        const [startDate, endDate] = dateRange.map(date => date.toISOString().split('T')[0]);
        const response = await graphqlRequest({
            query: GET_TOP_PERFORMING_ROUTES,
            variables: { ownerId, startDate, endDate }
        });
        return response.data.topPerformingRoutes;
    }
};
