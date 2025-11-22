import React, { useState } from "react";
import api from "../api/api";   // ✔ use our custom axios instance
import { useNavigate } from "react-router-dom";
import { jwtDecode } from "jwt-decode";

export default function LoginPage() {
  const navigate = useNavigate();

  const [credentials, setCredentials] = useState({ username: "", password: "" });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [showPassword, setShowPassword] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setCredentials({ ...credentials, [name]: value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");

    try {
      const res = await api.post(
        "/api/auth/login",
        {
          username: credentials.username,
          password: credentials.password,
        },
        {
          withCredentials: true, // ✔ sends refresh token cookie
        }
      );

      console.log("🟣 Login response:", res.data);

      // ✔ Backend returns `token`
      const token = res.data.token;

      // ✔ Save access token correctly
      localStorage.setItem("accessToken", token);

      // Optional: Decode username
      const decoded = jwtDecode(token);
      const username = decoded.sub;
      localStorage.setItem("username", username);

      alert("✅ Login successful!");
      navigate("/dashboard");

    } catch (err) {
      console.error("Login failed:", err);
      setError("Invalid username or password. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-purple-900 via-purple-800 to-indigo-900 px-4">
      <div className="w-[340px] bg-gradient-to-b from-purple-700 via-purple-600 to-purple-700/80 p-8 rounded-2xl shadow-2xl border border-purple-500/30 backdrop-blur-md">

        <h1 className="text-2xl font-semibold text-center text-blue-300 mb-8 flex items-center justify-center gap-2">
          🔐 Core Banking Login
        </h1>

        <form
          onSubmit={handleSubmit}
          className="grid grid-cols-1 gap-5 justify-items-center"
        >
          {/* Username */}
          <div className="w-[90%]">
            <label className="block text-white mb-2 text-sm font-medium">
              Username
            </label>
            <input
              type="text"
              name="username"
              value={credentials.username}
              onChange={handleChange}
              className="w-full p-3 rounded-lg bg-white/10 border border-white/30 text-white placeholder-white/70 focus:outline-none focus:ring-2 focus:ring-purple-500 transition-all"
              placeholder="Enter your username"
              required
            />
          </div>

          {/* Password */}
          <div className="w-[90%]">
            <label className="block text-white mb-2 text-sm font-medium">
              Password
            </label>
            <div className="relative">
              <input
                type={showPassword ? "text" : "password"}
                name="password"
                value={credentials.password}
                onChange={handleChange}
                className="w-full p-3 pr-14 rounded-lg bg-white/10 border border-white/30 text-white placeholder-white/70 focus:outline-none focus:ring-2 focus:ring-purple-500 transition-all"
                placeholder="Enter your password"
                required
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                className="absolute right-3 top-3 text-sm text-blue-300 hover:text-blue-400 transition-all"
              >
                {showPassword ? "Hide" : "Show"}
              </button>
            </div>
          </div>

          {/* Error */}
          {error && (
            <p className="text-red-400 text-sm text-center w-[90%]">{error}</p>
          )}

          {/* Submit */}
          <button
            type="submit"
            disabled={loading}
            className={`w-[90%] py-3 rounded-lg text-white font-semibold transition-all duration-300 ${
              loading
                ? "bg-purple-500/60 cursor-not-allowed"
                : "bg-gradient-to-r from-purple-600 to-purple-700 hover:from-purple-700 hover:to-purple-800 shadow-md"
            }`}
          >
            {loading ? "Logging in..." : "Login →"}
          </button>
        </form>

        <p className="text-white/60 text-center text-sm mt-6">
          Don’t have an account?{" "}
          <a
            href="/register"
            className="text-blue-300 hover:text-blue-400 underline"
          >
            Register here
          </a>
        </p>

      </div>
    </div>
  );
}
