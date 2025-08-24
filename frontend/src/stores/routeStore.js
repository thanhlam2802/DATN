import { ref } from 'vue'

// Global state để lưu dữ liệu route
const routeData = ref(null)

export const useRouteStore = () => {
  const setRouteData = (data) => {
    routeData.value = data
ư  }

  const getRouteData = () => {
    return routeData.value
  }

  const clearRouteData = () => {
    routeData.value = null
  }

  return {
    routeData,
    setRouteData,
    getRouteData,
    clearRouteData
  }
}
