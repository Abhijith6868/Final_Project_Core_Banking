import React, { useEffect, useState } from "react";
import api from "../api/api"; // 🔥 uses refresh-token axios instance
import { Link } from "react-router-dom";

export default function JobsPage() {
  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(true);

  // -------------------------------
  // FETCH JOBS
  // -------------------------------
  const fetchJobs = async () => {
    try {
      const res = await api.get("/api/jobs/master"); // no manual headers
      setJobs(res.data);
    } catch (err) {
      console.error("❌ Error fetching jobs:", err);
    } finally {
      setLoading(false);
    }
  };

  // -------------------------------
  // RUN JOB
  // -------------------------------
  const runJob = async (jobid) => {
    try {
      await api.post(`/api/jobs/run/${jobid}`, {}); // auto handles token
      alert("Job triggered successfully!");
      fetchJobs();
    } catch (err) {
      console.error("❌ Error running job:", err);
      alert("Failed to run job!");
    }
  };

  useEffect(() => {
    fetchJobs();
  }, []);

  // -------------------------------
  // STATUS COLORS
  // -------------------------------
  const statusColor = (status) => {
    switch (status) {
      case "RUNNING":
        return "text-blue-400";
      case "COMPLETED":
      case "SUCCESS":
        return "text-green-400";
      case "FAILED":
        return "text-red-400";
      case "NEVER_RUN":
        return "text-gray-400";
      default:
        return "text-gray-300";
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-purple-900 via-purple-800 to-indigo-900 p-10 text-white">
      <div className="max-w-6xl mx-auto">
        {/* 🔝 Navigation */}
        <header className="flex items-center justify-between text-sm text-white/90 mb-8">
          <nav className="flex gap-8">
            <Link to="/dashboard" className="hover:underline cursor-pointer">
              Home
            </Link>
            <Link to="/jobs" className="underline text-white font-semibold">
              Jobs
            </Link>
          </nav>
        </header>

        {/* 📊 Jobs Table */}
        <main className="bg-gradient-to-r from-purple-700 via-purple-600 to-purple-700/80 rounded-xl shadow-2xl p-8">
          <div className="flex flex-col sm:flex-row justify-between items-center mb-6">
            <h2 className="text-2xl font-semibold text-white/95">
              Scheduled Jobs
            </h2>
            <button
              onClick={fetchJobs}
              className="mt-4 sm:mt-0 bg-white/10 px-4 py-2 rounded-md hover:bg-white/20 transition text-sm"
            >
              🔄 Refresh
            </button>
          </div>

          {loading ? (
            <div className="text-center py-12 text-white/70">
              <span className="animate-spin inline-block h-8 w-8 border-t-2 border-b-2 border-white rounded-full"></span>
              <p className="mt-3">Loading jobs...</p>
            </div>
          ) : jobs.length === 0 ? (
            <p className="text-center text-white/70">No jobs found.</p>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-sm text-left border border-white/10 rounded-lg">
                <thead className="bg-white/10 text-white uppercase">
                  <tr>
                    <th className="px-4 py-3">Job ID</th>
                    <th className="px-4 py-3">Job Name</th>
                    <th className="px-4 py-3">Description</th>
                    <th className="px-4 py-3">Last Status</th>
                    <th className="px-4 py-3">Last Run</th>
                    <th className="px-4 py-3 text-center">Action</th>
                  </tr>
                </thead>
                <tbody>
                  {jobs.map((job) => (
                    <tr
                      key={job.jobid}
                      className="border-b border-white/10 hover:bg-white/5 transition"
                    >
                      <td className="px-4 py-3 text-white/80">{job.jobid}</td>
                      <td className="px-4 py-3 text-white/90 font-medium">
                        {job.jobName}
                      </td>
                      <td className="px-4 py-3 text-white/80">
                        {job.description}
                      </td>
                      <td
                        className={`px-4 py-3 font-semibold ${statusColor(
                          job.lastStatus
                        )}`}
                      >
                        {job.lastStatus}
                      </td>
                      <td className="px-4 py-3 text-white/70">
                        {job.lastRunTime || "—"}
                      </td>
                      <td className="px-4 py-3 text-center">
                        <button
                          onClick={() => runJob(job.jobid)}
                          className="bg-white/10 hover:bg-white/20 text-white text-xs px-4 py-2 rounded-lg transition"
                        >
                          ▶ Run Now
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </main>
      </div>
    </div>
  );
}
