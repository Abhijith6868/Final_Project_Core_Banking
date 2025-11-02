import React from "react";

// Dashboard.jsx
// Place this file in: src/components/Dashboard.jsx
// This component uses Tailwind CSS for styling. See the companion instructions (below) for
// how to add Tailwind and how to wire this into your App.js.

export default function Dashboard() {
  const stats = [
    { id: 1, title: "Active Loans", value: "1,248", delta: "+4.2%" },
    { id: 2, title: "Customers", value: "8,542", delta: "+1.1%" },
    { id: 3, title: "Collateral Value", value: "₹ 14.2M", delta: "+2.8%" },
  ];

  return (
    <div className="min-h-screen bg-gradient-to-br from-purple-900 via-purple-800 to-indigo-900 p-10">
      <div className="max-w-6xl mx-auto">
        {/* Top navigation */}
        <header className="flex items-center justify-between text-sm text-white/90 mb-8">
          <nav className="flex gap-8">
            <a className="hover:underline cursor-pointer">Home</a>
            <a className="hover:underline cursor-pointer">About Us</a>
            <a className="hover:underline cursor-pointer">Services</a>
            <a className="hover:underline cursor-pointer">Contact</a>
          </nav>
          <div className="flex items-center gap-4">
            <div className="hidden sm:flex items-center gap-2 bg-white/10 px-3 py-2 rounded-full">
              <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 text-white/90" viewBox="0 0 20 20" fill="currentColor">
                <path d="M10 2a6 6 0 100 12A6 6 0 0010 2z" />
              </svg>
              <span className="text-white/90 text-xs">Admin</span>
            </div>
            <button className="bg-white/10 px-3 py-2 rounded-md text-white/90 text-xs">Sign out</button>
          </div>
        </header>

        {/* Main hero card */}
        <main className="bg-gradient-to-r from-purple-700 via-purple-600 to-purple-700/80 rounded-xl shadow-2xl p-8 flex flex-col sm:flex-row items-stretch gap-8">
          {/* Left column - copy from the image */}
          <section className="flex-1 text-white/95 flex flex-col justify-center">
            <h1 className="text-4xl sm:text-5xl font-semibold leading-tight mb-4">Financial<br/>Security</h1>
            <p className="text-sm text-white/80 max-w-xl mb-6">
              A modern dashboard for your banking needs. Track loans, customers and collateral at a glance. This layout is
              inspired by your shared hero image — gradient, soft rounded card and a prominent illustration area.
            </p>

            <div className="flex items-center gap-4">
              <button className="px-6 py-3 rounded-full border border-white/40 text-white font-medium hover:shadow-lg transition">Get Started</button>
              <button className="px-4 py-2 rounded-full bg-white/10 text-white text-sm hover:bg-white/20 transition">Learn more</button>
            </div>

            {/* small stats row (responsive) */}
            <div className="mt-8 grid grid-cols-1 sm:grid-cols-3 gap-3 max-w-xl">
              {stats.map(s => (
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

          {/* Right column - illustration + dashboard mockup */}
          <aside className="w-full sm:w-96 relative flex items-center justify-center">
            {/* Outer mockup card */}
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

              {/* small cards under the illustration to mimic dashboard numbers */}
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

            {/* Decorative coins / padlock as absolute elements to echo the reference image */}
            <div className="absolute -left-6 bottom-4 hidden sm:block">
              <div className="bg-yellow-400 rounded-full w-12 h-12 flex items-center justify-center text-white font-bold shadow">₹</div>
            </div>
            <div className="absolute -right-6 top-6 hidden sm:block">
              <div className="bg-indigo-600 rounded-md w-12 h-12 flex items-center justify-center text-white font-bold shadow">🔒</div>
            </div>
          </aside>
        </main>

        {/* Below hero: quick links / cards for navigation to modules */}
        <section className="mt-8 grid grid-cols-1 sm:grid-cols-3 gap-6">
          <div className="bg-white/6 p-4 rounded-lg shadow">
            <div className="text-sm text-white/80">Customers</div>
            <div className="text-2xl font-semibold text-white mt-2">8,542</div>
            <div className="text-xs text-white/70 mt-2">Manage customers, KYC and accounts</div>
          </div>

          <div className="bg-white/6 p-4 rounded-lg shadow">
            <div className="text-sm text-white/80">Loans</div>
            <div className="text-2xl font-semibold text-white mt-2">1,248</div>
            <div className="text-xs text-white/70 mt-2">Create, approve and view repayments</div>
          </div>

          <div className="bg-white/6 p-4 rounded-lg shadow">
            <div className="text-sm text-white/80">Collateral</div>
            <div className="text-2xl font-semibold text-white mt-2">3,542</div>
            <div className="text-xs text-white/70 mt-2">Attach collateral to loans and view valuation</div>
          </div>
        </section>
      </div>
    </div>
  );
}
