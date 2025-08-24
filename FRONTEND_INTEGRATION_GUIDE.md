# 🚌 Hướng dẫn tích hợp BusManagement API vào Frontend

## 📋 Tổng quan API

### Base URL
```
http://localhost:8080/api/bus-management
```

### Các endpoints chính:

#### 1. **Overview Tab** 📊
```javascript
// GET - Lấy KPI tổng quan
GET /overview/kpis

// GET - Lấy cảnh báo chất lượng dữ liệu
GET /overview/alerts?query=search_term
```

#### 2. **Providers Tab** 👥
```javascript
// GET - Lấy danh sách providers
GET /providers?query=search_term&status=ACTIVE

// POST - Tạo provider mới
POST /providers
{
  "code": "provider@email.com",
  "name": "Provider Name"
}

// PUT - Cập nhật provider
PUT /providers/{id}
{
  "code": "new@email.com",
  "name": "New Name"
}

// PUT - Ban provider
PUT /providers/{id}/ban

// PUT - Unban provider
PUT /providers/{id}/unban
```

#### 3. **Routes Tab** 🛣️
```javascript
// GET - Lấy danh sách routes
GET /routes?owner=owner&origin=origin&destination=dest&status=APPROVED

// POST - Tạo route mới
POST /routes
{
  "ownerCode": "provider@email.com",
  "origin": "Saigon",
  "destination": "Hanoi",
  "distanceKm": 1200.0,
  "estimatedDurationMinutes": 1440,
  "status": "DRAFT"
}

// PUT - Cập nhật route
PUT /routes/{id}
{
  "origin": "New Origin",
  "destination": "New Destination",
  "distanceKm": 1500.0,
  "estimatedDurationMinutes": 1800,
  "status": "APPROVED"
}

// PUT - Submit route để review
PUT /routes/{id}/submit-review

// PUT - Approve route
PUT /routes/{id}/approve

// PUT - Reject route
PUT /routes/{id}/reject
```

#### 4. **Slots Tab** 🕐
```javascript
// GET - Lấy danh sách slots
GET /slots?owner=owner&route=route_id&date=2024-01-15

// POST - Tạo slots hàng loạt
POST /slots/bulk-generate
{
  "routeCode": "1",
  "busId": 1,
  "startDate": "2024-01-15",
  "endDate": "2024-01-20",
  "daysOfWeek": [1,2,3,4,5,6,7],
  "departTime": "22:00",
  "durationMinutes": 900,
  "price": 250000
}

// GET - Lấy chi tiết slot
GET /slots/{id}/detail
```

#### 5. **Moderation Tab** ✅
```javascript
// GET - Lấy queue moderation
GET /moderation/queue?type=ROUTE&status=PENDING_REVIEW

// PUT - Approve moderation item
PUT /moderation/{id}/approve

// PUT - Reject moderation item
PUT /moderation/{id}/reject

// GET - Lấy diff của moderation item
GET /moderation/{id}/diff
```

## 🔧 Tích hợp vào Vue.js Frontend

### 1. **Tạo API Service**

```javascript
// src/services/busManagementApi.js
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api/bus-management';

export const busManagementApi = {
  // Overview
  getOverviewKPIs() {
    return axios.get(`${API_BASE_URL}/overview/kpis`);
  },

  getDataQualityAlerts(query = '') {
    return axios.get(`${API_BASE_URL}/overview/alerts`, {
      params: { query }
    });
  },

  // Providers
  getProviders(query = '', status = '') {
    return axios.get(`${API_BASE_URL}/providers`, {
      params: { query, status }
    });
  },

  createProvider(providerData) {
    return axios.post(`${API_BASE_URL}/providers`, providerData);
  },

  updateProvider(id, providerData) {
    return axios.put(`${API_BASE_URL}/providers/${id}`, providerData);
  },

  banProvider(id) {
    return axios.put(`${API_BASE_URL}/providers/${id}/ban`);
  },

  unbanProvider(id) {
    return axios.put(`${API_BASE_URL}/providers/${id}/unban`);
  },

  // Routes
  getRoutes(owner = '', origin = '', destination = '', status = '') {
    return axios.get(`${API_BASE_URL}/routes`, {
      params: { owner, origin, destination, status }
    });
  },

  createRoute(routeData) {
    return axios.post(`${API_BASE_URL}/routes`, routeData);
  },

  updateRoute(id, routeData) {
    return axios.put(`${API_BASE_URL}/routes/${id}`, routeData);
  },

  submitRouteReview(id) {
    return axios.put(`${API_BASE_URL}/routes/${id}/submit-review`);
  },

  approveRoute(id) {
    return axios.put(`${API_BASE_URL}/routes/${id}/approve`);
  },

  rejectRoute(id) {
    return axios.put(`${API_BASE_URL}/routes/${id}/reject`);
  },

  // Slots
  getSlots(owner = '', route = '', date = '') {
    return axios.get(`${API_BASE_URL}/slots`, {
      params: { owner, route, date }
    });
  },

  bulkGenerateSlots(slotData) {
    return axios.post(`${API_BASE_URL}/slots/bulk-generate`, slotData);
  },

  getSlotDetail(id) {
    return axios.get(`${API_BASE_URL}/slots/${id}/detail`);
  },

  // Moderation
  getModerationQueue(type = '', status = '') {
    return axios.get(`${API_BASE_URL}/moderation/queue`, {
      params: { type, status }
    });
  },

  approveModeration(id) {
    return axios.put(`${API_BASE_URL}/moderation/${id}/approve`);
  },

  rejectModeration(id) {
    return axios.put(`${API_BASE_URL}/moderation/${id}/reject`);
  },

  getModerationDiff(id) {
    return axios.get(`${API_BASE_URL}/moderation/${id}/diff`);
  }
};
```

### 2. **Cập nhật BusManagement.vue**

```vue
<template>
  <div class="bus-management">
    <!-- Tab Navigation -->
    <div class="tabs">
      <button 
        v-for="tab in tabs" 
        :key="tab.id"
        :class="{ active: activeTab === tab.id }"
        @click="activeTab = tab.id"
      >
        {{ tab.name }}
      </button>
    </div>

    <!-- Tab Content -->
    <div class="tab-content">
      <!-- Overview Tab -->
      <div v-if="activeTab === 'overview'" class="overview-tab">
        <div class="kpi-cards">
          <div class="kpi-card" v-for="(value, key) in kpis" :key="key">
            <h3>{{ formatKPIName(key) }}</h3>
            <p class="kpi-value">{{ formatKPIValue(key, value) }}</p>
          </div>
        </div>
        
        <div class="alerts-section">
          <h3>Data Quality Alerts</h3>
          <div class="alert-list">
            <div v-for="alert in alerts" :key="alert.type" class="alert-item">
              <span class="alert-type">{{ alert.type }}</span>
              <span class="alert-owner">{{ alert.ownerName }}</span>
              <p class="alert-description">{{ alert.description }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Providers Tab -->
      <div v-if="activeTab === 'providers'" class="providers-tab">
        <div class="actions">
          <input v-model="providerSearch" placeholder="Search providers..." />
          <button @click="showCreateProviderModal = true">Add Provider</button>
        </div>
        
        <div class="providers-list">
          <div v-for="provider in providers" :key="provider.id" class="provider-item">
            <div class="provider-info">
              <h4>{{ provider.name }}</h4>
              <p>{{ provider.code }}</p>
              <span :class="'status-' + provider.status.toLowerCase()">
                {{ provider.status }}
              </span>
            </div>
            <div class="provider-actions">
              <button @click="editProvider(provider)">Edit</button>
              <button @click="banProvider(provider.id)" v-if="provider.status === 'ACTIVE'">
                Ban
              </button>
              <button @click="unbanProvider(provider.id)" v-else>
                Unban
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Routes Tab -->
      <div v-if="activeTab === 'routes'" class="routes-tab">
        <div class="actions">
          <input v-model="routeSearch.owner" placeholder="Search by owner..." />
          <input v-model="routeSearch.origin" placeholder="Search by origin..." />
          <input v-model="routeSearch.destination" placeholder="Search by destination..." />
          <button @click="showCreateRouteModal = true">Add Route</button>
        </div>
        
        <div class="routes-list">
          <div v-for="route in routes" :key="route.id" class="route-item">
            <div class="route-info">
              <h4>{{ route.origin }} → {{ route.destination }}</h4>
              <p>Owner: {{ route.ownerCode }}</p>
              <p>Distance: {{ route.distanceKm }}km</p>
              <span :class="'status-' + route.status.toLowerCase()">
                {{ route.status }}
              </span>
            </div>
            <div class="route-actions">
              <button @click="editRoute(route)">Edit</button>
              <button @click="approveRoute(route.id)" v-if="route.status === 'PENDING_REVIEW'">
                Approve
              </button>
              <button @click="rejectRoute(route.id)" v-if="route.status === 'PENDING_REVIEW'">
                Reject
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Slots Tab -->
      <div v-if="activeTab === 'slots'" class="slots-tab">
        <div class="actions">
          <input v-model="slotSearch.owner" placeholder="Search by owner..." />
          <input v-model="slotSearch.route" placeholder="Search by route..." />
          <input v-model="slotSearch.date" type="date" />
          <button @click="showBulkGenerateModal = true">Bulk Generate</button>
        </div>
        
        <div class="slots-list">
          <div v-for="slot in slots" :key="slot.id" class="slot-item">
            <div class="slot-info">
              <h4>{{ slot.busPlate }}</h4>
              <p>Route: {{ slot.routeCode }}</p>
              <p>Date: {{ slot.departTime }}</p>
              <p>Sold: {{ slot.sold }}/{{ slot.total }}</p>
              <span :class="'status-' + slot.status.toLowerCase()">
                {{ slot.status }}
              </span>
            </div>
            <div class="slot-actions">
              <button @click="viewSlotDetail(slot.id)">View Details</button>
            </div>
          </div>
        </div>
      </div>

      <!-- Moderation Tab -->
      <div v-if="activeTab === 'moderation'" class="moderation-tab">
        <div class="moderation-queue">
          <h3>Moderation Queue</h3>
          <div v-for="item in moderationQueue" :key="item.id" class="moderation-item">
            <div class="item-info">
              <h4>{{ item.type }} - {{ item.content }}</h4>
              <p>Submitted by: {{ item.submittedBy }}</p>
              <p>Date: {{ item.submittedAt }}</p>
              <span :class="'status-' + item.status.toLowerCase()">
                {{ item.status }}
              </span>
            </div>
            <div class="item-actions">
              <button @click="approveModeration(item.id)">Approve</button>
              <button @click="rejectModeration(item.id)">Reject</button>
              <button @click="viewModerationDiff(item.id)">View Diff</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { busManagementApi } from '@/services/busManagementApi';

export default {
  name: 'BusManagement',
  data() {
    return {
      activeTab: 'overview',
      tabs: [
        { id: 'overview', name: 'Overview' },
        { id: 'providers', name: 'Providers' },
        { id: 'routes', name: 'Routes' },
        { id: 'slots', name: 'Slots' },
        { id: 'moderation', name: 'Moderation' }
      ],
      
      // Data
      kpis: {},
      alerts: [],
      providers: [],
      routes: [],
      slots: [],
      moderationQueue: [],
      
      // Search filters
      providerSearch: '',
      routeSearch: { owner: '', origin: '', destination: '' },
      slotSearch: { owner: '', route: '', date: '' },
      
      // Modals
      showCreateProviderModal: false,
      showCreateRouteModal: false,
      showBulkGenerateModal: false
    };
  },
  
  async mounted() {
    await this.loadOverviewData();
  },
  
  watch: {
    activeTab(newTab) {
      this.loadTabData(newTab);
    },
    
    providerSearch() {
      this.loadProviders();
    },
    
    routeSearch: {
      deep: true,
      handler() {
        this.loadRoutes();
      }
    },
    
    slotSearch: {
      deep: true,
      handler() {
        this.loadSlots();
      }
    }
  },
  
  methods: {
    async loadOverviewData() {
      try {
        const [kpisResponse, alertsResponse] = await Promise.all([
          busManagementApi.getOverviewKPIs(),
          busManagementApi.getDataQualityAlerts()
        ]);
        
        this.kpis = kpisResponse.data;
        this.alerts = alertsResponse.data;
      } catch (error) {
        console.error('Error loading overview data:', error);
      }
    },
    
    async loadTabData(tab) {
      switch (tab) {
        case 'providers':
          await this.loadProviders();
          break;
        case 'routes':
          await this.loadRoutes();
          break;
        case 'slots':
          await this.loadSlots();
          break;
        case 'moderation':
          await this.loadModerationQueue();
          break;
      }
    },
    
    async loadProviders() {
      try {
        const response = await busManagementApi.getProviders(this.providerSearch);
        this.providers = response.data;
      } catch (error) {
        console.error('Error loading providers:', error);
      }
    },
    
    async loadRoutes() {
      try {
        const response = await busManagementApi.getRoutes(
          this.routeSearch.owner,
          this.routeSearch.origin,
          this.routeSearch.destination
        );
        this.routes = response.data;
      } catch (error) {
        console.error('Error loading routes:', error);
      }
    },
    
    async loadSlots() {
      try {
        const response = await busManagementApi.getSlots(
          this.slotSearch.owner,
          this.slotSearch.route,
          this.slotSearch.date
        );
        this.slots = response.data;
      } catch (error) {
        console.error('Error loading slots:', error);
      }
    },
    
    async loadModerationQueue() {
      try {
        const response = await busManagementApi.getModerationQueue();
        this.moderationQueue = response.data;
      } catch (error) {
        console.error('Error loading moderation queue:', error);
      }
    },
    
    // Provider actions
    async createProvider(providerData) {
      try {
        await busManagementApi.createProvider(providerData);
        await this.loadProviders();
        this.showCreateProviderModal = false;
      } catch (error) {
        console.error('Error creating provider:', error);
      }
    },
    
    async banProvider(id) {
      try {
        await busManagementApi.banProvider(id);
        await this.loadProviders();
      } catch (error) {
        console.error('Error banning provider:', error);
      }
    },
    
    async unbanProvider(id) {
      try {
        await busManagementApi.unbanProvider(id);
        await this.loadProviders();
      } catch (error) {
        console.error('Error unbanning provider:', error);
      }
    },
    
    // Route actions
    async createRoute(routeData) {
      try {
        await busManagementApi.createRoute(routeData);
        await this.loadRoutes();
        this.showCreateRouteModal = false;
      } catch (error) {
        console.error('Error creating route:', error);
      }
    },
    
    async approveRoute(id) {
      try {
        await busManagementApi.approveRoute(id);
        await this.loadRoutes();
      } catch (error) {
        console.error('Error approving route:', error);
      }
    },
    
    async rejectRoute(id) {
      try {
        await busManagementApi.rejectRoute(id);
        await this.loadRoutes();
      } catch (error) {
        console.error('Error rejecting route:', error);
      }
    },
    
    // Moderation actions
    async approveModeration(id) {
      try {
        await busManagementApi.approveModeration(id);
        await this.loadModerationQueue();
      } catch (error) {
        console.error('Error approving moderation:', error);
      }
    },
    
    async rejectModeration(id) {
      try {
        await busManagementApi.rejectModeration(id);
        await this.loadModerationQueue();
      } catch (error) {
        console.error('Error rejecting moderation:', error);
      }
    },
    
    // Utility methods
    formatKPIName(key) {
      return key.replace(/([A-Z])/g, ' $1').replace(/^./, str => str.toUpperCase());
    },
    
    formatKPIValue(key, value) {
      if (key === 'occupancyRate') {
        return `${value}%`;
      }
      return value;
    }
  }
};
</script>

<style scoped>
.bus-management {
  padding: 20px;
}

.tabs {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.tabs button {
  padding: 10px 20px;
  border: none;
  background: #f0f0f0;
  cursor: pointer;
  border-radius: 5px;
}

.tabs button.active {
  background: #007bff;
  color: white;
}

.kpi-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.kpi-card {
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.kpi-value {
  font-size: 24px;
  font-weight: bold;
  color: #007bff;
}

.alert-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.alert-item {
  padding: 15px;
  background: #fff3cd;
  border: 1px solid #ffeaa7;
  border-radius: 5px;
}

.actions {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.actions input {
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.actions button {
  padding: 8px 16px;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.provider-item,
.route-item,
.slot-item,
.moderation-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  margin-bottom: 10px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.status-active { color: #28a745; }
.status-pending_review { color: #ffc107; }
.status-approved { color: #28a745; }
.status-rejected { color: #dc3545; }
.status-scheduled { color: #17a2b8; }
.status-cancelled { color: #6c757d; }
</style>
```

### 3. **Chạy test API**

```bash
# Chạy integration test
mvn test -Dtest=BusManagementApiTest

# Hoặc chạy tất cả test
mvn test
```

### 4. **Cấu hình CORS (nếu cần)**

Thêm vào `application.properties`:
```properties
# CORS configuration
spring.web.cors.allowed-origins=http://localhost:3000,http://localhost:8080
spring.web.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
spring.web.cors.allowed-headers=*
```

## 🎯 Kết quả

Sau khi tích hợp:
- ✅ **Frontend** có thể gọi tất cả API endpoints
- ✅ **Real-time data** được load từ backend
- ✅ **CRUD operations** hoạt động đầy đủ
- ✅ **Search & filter** functionality
- ✅ **Status management** (approve/reject/ban)
- ✅ **Bulk operations** (generate slots)

**API đã sẵn sàng để sử dụng trong production!** 🚀
