import { ref } from "vue";

export function useGeolocation() {
  const location = ref(null);
  const error = ref(null);
  const loading = ref(false);

  const getCurrentLocation = () => {
    return new Promise((resolve, reject) => {
      if (!navigator.geolocation) {
        const err = new Error("Trình duyệt của bạn không hỗ trợ định vị.");
        error.value = err;
        reject(err);
        return;
      }

      loading.value = true;
      error.value = null;

      navigator.geolocation.getCurrentPosition(
        (position) => {
          location.value = {
            latitude: position.coords.latitude,
            longitude: position.coords.longitude,
          };
          loading.value = false;
          resolve(location.value);
        },
        (err) => {
          let errorMessage = "Không thể lấy vị trí của bạn.";
          switch (err.code) {
            case 1:
              errorMessage =
                "Vui lòng cho phép chia sẻ vị trí để sử dụng tính năng này.";
              break;
            case 2:
              errorMessage =
                "Không thể xác định vị trí của bạn. Vui lòng thử lại sau.";
              break;
            case 3:
              errorMessage = "Quá thời gian xác định vị trí. Vui lòng thử lại.";
              break;
          }
          error.value = new Error(errorMessage);
          loading.value = false;
          reject(error.value);
        },
        {
          enableHighAccuracy: true,
          timeout: 5000,
          maximumAge: 0,
        }
      );
    });
  };

  return {
    location,
    error,
    loading,
    getCurrentLocation,
  };
}
