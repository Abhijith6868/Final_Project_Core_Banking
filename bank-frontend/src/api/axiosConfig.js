import axios from "axios";

// Create axios instance
const api = axios.create({
  baseURL: "http://localhost:8080",
  withCredentials: true, // VERY IMPORTANT → sends refreshToken cookie
});

let isRefreshing = false;
let failedQueue = [];

const processQueue = (error, token = null) => {
  failedQueue.forEach((promise) => {
    if (error) {
      promise.reject(error);
    } else {
      promise.resolve(token);
    }
  });
  failedQueue = [];
};

// ------------------------------
// REQUEST INTERCEPTOR
// ------------------------------
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("accessToken");
    if (token) {
      config.headers["Authorization"] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// ------------------------------
// RESPONSE INTERCEPTOR
// ------------------------------
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // No response from server → network error
    if (!error.response) {
      console.log("❌ Network error");
      return Promise.reject(error);
    }

    const status = error.response.status;

    // Token expired → Need to refresh
    if ((status === 401 || status === 403) && !originalRequest._retry) {
      if (isRefreshing) {
        // Queue the failed requests
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject });
        })
          .then((newToken) => {
            originalRequest.headers["Authorization"] = "Bearer " + newToken;
            return api(originalRequest);
          })
          .catch((err) => Promise.reject(err));
      }

      originalRequest._retry = true;
      isRefreshing = true;

      try {
        // Call refresh endpoint
        const res = await api.post("/api/auth/refresh");

        const newAccessToken = res.data.accessToken;

        // Save new token
        localStorage.setItem("accessToken", newAccessToken);

        api.defaults.headers.common[
          "Authorization"
        ] = `Bearer ${newAccessToken}`;

        processQueue(null, newAccessToken);

        originalRequest.headers["Authorization"] =
          "Bearer " + newAccessToken;

        return api(originalRequest); // retry request
      } catch (refreshError) {
        processQueue(refreshError, null);

        // Refresh failed → logout user
        localStorage.clear();
        sessionStorage.clear();
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
