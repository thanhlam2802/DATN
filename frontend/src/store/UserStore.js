import { defineStore } from "pinia";
import { AccountApi } from "@/api/AccountApi.js";
import { clearToken, saveAccessToken } from "@/services/TokenService.js";

export const useUserStore = defineStore("user", {
  state: () => ({
    isLoggedIn: false,
    user: null,
    token: null,
  }),
  actions: {
    async handleRegistration(authData) {
      console.log("handleRegistration - Bắt đầu xử lý sau đăng ký...");

      saveAccessToken(authData.accessToken);
      console.log("handleRegistration - Đã lưu access token.");

      this.token = authData.accessToken;

      try {
        console.log("handleRegistration - Đang gọi getProfile...");
        const profileRes = await AccountApi.getProfile();
        console.log("handleRegistration - Nhận được profile:", profileRes);

        if (profileRes && !profileRes.errorCode) {
          // BƯỚC 4: CẬP NHẬT TOÀN BỘ TRẠNG THÁI
          this.isLoggedIn = true;
          this.user = profileRes.data;
          console.log("handleRegistration - Xử lý thành công!");
        } else {
          throw new Error(
            profileRes.message || "Lỗi khi lấy profile sau đăng ký."
          );
        }
      } catch (error) {
        console.error(
          "Lỗi nghiêm trọng trong quá trình handleRegistration:",
          error
        );
        this.logout();
        throw error;
      }
    },
    login(userData, token) {
      this.isLoggedIn = true;
      this.user = userData;
      this.token = token;
      saveAccessToken(token);
    },
    logout() {
      this.isLoggedIn = false;
      this.user = null;
      this.token = null;
      clearToken();
    },
    updateUser(userData) {
      this.user = userData;
    },
    async restoreUserFromToken() {
      console.log("restoreUserFromToken - starting...");
      const token = localStorage.getItem("t_");
      console.log(
        "restoreUserFromToken - token:",
        token ? "exists" : "not found"
      );

      if (token) {
        try {
          console.log(
            "restoreUserFromToken - calling AccountApi.getProfile()..."
          );
          const profileRes = await AccountApi.getProfile();
          console.log("restoreUserFromToken - profileRes:", profileRes);

          if (!profileRes.errorCode) {
            console.log(
              "restoreUserFromToken - setting user data:",
              profileRes.data
            );
            this.isLoggedIn = true;
            this.user = profileRes.data;
            this.token = token;
            return true;
          } else {
            console.log(
              "restoreUserFromToken - errorCode:",
              profileRes.errorCode
            );
            clearToken();
          }
        } catch (error) {
          console.error("Failed to restore user from token:", error);
          clearToken();
        }
      }
      console.log("restoreUserFromToken - failed to restore");
      return false;
    },
  },
});
