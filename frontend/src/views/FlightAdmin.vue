<template>
  <div class="flight-admin">
    <div class="admin-header">
      <h1>Flight Management</h1>
    </div>
    
    <div class="admin-tabs">
      <el-tabs v-model="activeTab" type="border-card">
        <el-tab-pane label="Flights" name="flights">
          <FlightsTab />
        </el-tab-pane>
        <el-tab-pane label="Add Flight" name="add-flight">
          <AddFlightTab />
        </el-tab-pane>
        <el-tab-pane label="Statistics" name="statistics">
          <FlightStatisticsTab />
        </el-tab-pane>
        <el-tab-pane label="Bookings" name="bookings">
          <FlightBookingsTab />
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import FlightsTab from './flight/FlightsTab.vue'
import AddFlightTab from './flight/AddFlightTab.vue'
import FlightStatisticsTab from './flight/FlightStatisticsTab.vue'
import FlightBookingsTab from './flight/FlightBookingsTab.vue'

const activeTab = ref('flights')
</script>

<style scoped>
.flight-admin {
  padding: 20px;
}

.admin-header {
  margin-bottom: 20px;
}

.admin-header h1 {
  color: #2c3e50;
  font-size: 24px;
}

.admin-tabs {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
</style> 