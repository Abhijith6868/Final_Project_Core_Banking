import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080",
  withCredentials: true, // 🔥 send HttpOnly refreshToken cookie
});

let isRefreshing = false;
let failedQueue = [];

const processQueue = (error, token = null) => {
  failedQueue.forEach((prom) => {
    if (error) prom.reject(error);
    else prom.resolve(token);
  });
  failedQueue = [];
};

// --------------------------------------------------
// REQUEST INTERCEPTOR
// --------------------------------------------------
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("accessToken"); // ✔ correct storage key

    if (token) {
      config.headers["Authorization"] = `Bearer ${token}`;
    } else {
      console.warn("⚠ No accessToken found in localStorage");
    }

    return config;
  },
  (error) => Promise.reject(error)
);

// --------------------------------------------------
// RESPONSE INTERCEPTOR (TOKEN REFRESH LOGIC)
// --------------------------------------------------
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    if (!error.response) {
      console.error("❌ Network error");
      return Promise.reject(error);
    }

    const status = error.response.status;

    // 🔥 Token expired (401 or 403)
    if ((status === 401 || status === 403) && !originalRequest._retry) {

      // Prevent multiple refresh calls
      if (isRefreshing) {
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject });
        })
          .then((token) => {
            originalRequest.headers["Authorization"] = `Bearer ${token}`;
            return api(originalRequest);
          })
          .catch((err) => Promise.reject(err));
      }

      originalRequest._retry = true;
      isRefreshing = true;

      try {
        console.log("🔄 Refreshing access token...");

        // Call backend refresh endpoint
        const res = await api.post("/api/auth/refresh");

        // ✔ Correct field name from backend:
        const newToken = res.data.accessToken;

        if (!newToken) {
          throw new Error("No new access token received!");
        }

        // Save new token
        localStorage.setItem("accessToken", newToken);

        // Update axios default headers
        api.defaults.headers.common["Authorization"] = `Bearer ${newToken}`;

        // Resolve queued requests
        processQueue(null, newToken);

        // Retry original request with new token
        originalRequest.headers["Authorization"] = `Bearer ${newToken}`;
        return api(originalRequest);

      } catch (refreshError) {
        // Fail queued requests
        processQueue(refreshError, null);

        console.error("❌ Refresh token failed — Logging out...");

        localStorage.clear();
        window.location.href = "/login";

        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    }

    return Promise.reject(error);
  }
);

export default api;
