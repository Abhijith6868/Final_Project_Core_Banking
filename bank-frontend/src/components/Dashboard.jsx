import React, { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import axios from "axios";

export default function Dashboard() {
  const navigate = useNavigate();
  const baseURL = "http://localhost:8080";

  // ✅ Load username directly from localStorage
  const storedUsername = localStorage.getItem("username");
  const username = storedUsername ? storedUsername : "Admin";

  const stats = [
    { id: 1, title: "Active Loans", value: "1,248", delta: "+4.2%" },
    { id: 2, title: "Customers", value: "8,542", delta: "+1.1%" },
    { id: 3, title: "Collateral Value", value: "₹ 14.2M", delta: "+2.8%" },
  ];

  const handleSignOut = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    navigate("/login");
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-purple-900 via-purple-800 to-indigo-900 p-10">
      <div className="max-w-6xl mx-auto">
        {/* 🔝 Top Navigation */}
        <header className="flex items-center justify-between text-sm text-white/90 mb-8">
          <nav className="flex gap-8">
            <Link to="/dashboard" className="hover:underline cursor-pointer">Home</Link>
            <Link to="/customers/list" className="hover:underline cursor-pointer">Customers</Link>
            <Link to="/loans/search" className="hover:underline cursor-pointer">Loans</Link>
            <Link to="/Account/Create" className="hover:underline cursor-pointer">Account</Link>
            <Link to="/jobs" className="hover:underline cursor-pointer">Jobs</Link>
            <Link
              to="/reports/repayments" // ✅ open empty SOA report page
              className="hover:underline cursor-pointer"
            >
              SOA
            </Link>
          </nav>

          {/* ✅ User Section */}
          <div className="flex items-center gap-4">
            <div className="flex items-center gap-2 bg-white/10 px-3 py-2 rounded-full backdrop-blur-sm border border-white/20">
              <div className="h-6 w-6 rounded-full bg-gradient-to-br from-purple-400 to-pink-400 flex items-center justify-center text-white font-bold text-xs shadow-inner">
                {(username.charAt(0) || "A").toUpperCase()}
              </div>
              <span className="text-white/90 text-xs font-semibold tracking-wide">
                {username.charAt(0).toUpperCase() + username.slice(1)}
              </span>
            </div>

            <button
              onClick={handleSignOut}
              className="bg-white/10 hover:bg-white/20 px-4 py-2 rounded-md text-white/90 text-xs font-medium transition"
            >
              Sign out
            </button>
          </div>
        </header>

        {/* 🌟 Main Hero Section */}
        <main className="bg-gradient-to-r from-purple-700 via-purple-600 to-purple-700/80 rounded-xl shadow-2xl p-8 flex flex-col sm:flex-row items-stretch gap-8">
          {/* Left Column */}
          <section className="flex-1 text-white/95 flex flex-col justify-center">
            <h1 className="text-4xl sm:text-5xl font-semibold leading-tight mb-4">
              Financial<br />Security
            </h1>
            <p className="text-sm text-white/80 max-w-xl mb-6">
              A modern dashboard for your banking needs. Track loans, customers and collateral at a glance.
            </p>

            <div className="flex items-center gap-4">
              <Link
                to="/customers"
                className="px-6 py-3 rounded-full border border-white/40 text-white font-medium hover:shadow-lg transition"
              >
                Get Started
              </Link>
              <a
                href="#learn-more"
                className="px-4 py-2 rounded-full bg-white/10 text-white text-sm hover:bg-white/20 transition"
              >
                Learn more
              </a>
            </div>

            {/* Stats Row */}
            <div className="mt-8 grid grid-cols-1 sm:grid-cols-3 gap-3 max-w-xl">
              {stats.map((s) => (
                <div key={s.id} className="bg-white/6 p-3 rounded-lg">
                  <div className="text-xs text-white/70">{s.title}</div>
                  <div className="flex items-baseline justify-between mt-1">
                    <div className="text-xl font-semibold text-white">{s.value}</div>
                    <div className="text-sm text-green-300">{s.delta}</div>
                  </div>
                </div>
              ))}
            </div>
          </section>

          {/* Right Column - Mockup */}
          <aside className="w-full sm:w-96 relative flex items-center justify-center">
            <div className="bg-white rounded-2xl p-4 w-full shadow-inner transform sm:translate-x-6">
              <div className="h-40 bg-gradient-to-b from-white to-gray-100 rounded-lg overflow-hidden p-3">
                <div className="flex justify-between mb-2">
                  <div className="h-3 w-20 bg-white/50 rounded-full" />
                  <div className="h-3 w-10 bg-white/50 rounded-full" />
                </div>

                <div className="h-28 grid grid-rows-3 gap-2">
                  <div className="h-3 w-3/4 bg-purple-100 rounded-full" />
                  <div className="h-3 w-1/2 bg-purple-100 rounded-full" />
                  <div className="h-3 w-2/3 bg-purple-100 rounded-full" />
                </div>
              </div>

              {/* Small Cards */}
              <div className="mt-3 grid grid-cols-3 gap-2">
                <div className="bg-purple-50/70 rounded-md p-2 text-center">
                  <div className="text-xs text-gray-700">Loans</div>
                  <div className="font-semibold">1,248</div>
                </div>
                <div className="bg-purple-50/70 rounded-md p-2 text-center">
                  <div className="text-xs text-gray-700">Due</div>
                  <div className="font-semibold">312</div>
                </div>
                <div className="bg-purple-50/70 rounded-md p-2 text-center">
                  <div className="text-xs text-gray-700">Overdue</div>
                  <div className="font-semibold text-red-600">27</div>
                </div>
              </div>
            </div>

            {/* Decorative Icons */}
            <div className="absolute -left-6 bottom-4 hidden sm:block">
              <div className="bg-yellow-400 rounded-full w-12 h-12 flex items-center justify-center text-white font-bold shadow">
                ₹
              </div>
            </div>
            <div className="absolute -right-6 top-6 hidden sm:block">
              <div className="bg-indigo-600 rounded-md w-12 h-12 flex items-center justify-center text-white font-bold shadow">
                🔒
              </div>
            </div>
          </aside>
        </main>

        {/* 📊 Quick Link Cards */}
        <section className="mt-8 grid grid-cols-1 sm:grid-cols-3 gap-6">
          <Link to="/customers" className="bg-white/6 p-4 rounded-lg shadow hover:bg-white/10 transition">
            <div className="text-sm text-white/80">Customers</div>
            <div className="text-2xl font-semibold text-white mt-2">8,542</div>
            <div className="text-xs text-white/70 mt-2">Manage customers, KYC and accounts</div>
          </Link>

          <Link to="/loans/create" className="bg-white/6 p-4 rounded-lg shadow hover:bg-white/10 transition">
            <div className="text-sm text-white/80">Loans</div>
            <div className="text-2xl font-semibold text-white mt-2">1,248</div>
            <div className="text-xs text-white/70 mt-2">Create, approve and view repayments</div>
          </Link>

          <Link to="/collateral/create" className="bg-white/6 p-4 rounded-lg shadow hover:bg-white/10 transition">
            <div className="text-sm text-white/80">Collateral</div>
            <div className="text-2xl font-semibold text-white mt-2">3,542</div>
            <div className="text-xs text-white/70 mt-2">Attach collateral to loans and view valuation</div>
          </Link>
        </section>
      </div>
    </div>
  );
}
