import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";

export default function LoanViewPage() {
  const { loanId } = useParams();
  const navigate = useNavigate();

  const [loan, setLoan] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    console.log("🔍 Loan ID from URL:", loanId);

    const fetchLoan = async () => {
      const token = localStorage.getItem("token");
      if (!token) {
        alert("Unauthorized! Please log in first.");
        navigate("/login");
        return;
      }

      try {
        const endpoint = `http://localhost:8080/api/loans/${loanId}`;
        console.log("🔑 Token:", token);
        console.log("🔍 Fetching from:", endpoint);

        const res = await axios.get(endpoint, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        console.log("✅ Loan fetched successfully:", res.data);
        setLoan(res.data);
      } catch (err) {
        console.error(
          "❌ Error fetching loan:",
          err.response?.status,
          err.response?.data || err.message
        );

        if (err.response?.status === 404) {
          setLoan(null);
        } else if (err.response?.status === 401) {
          alert("Session expired. Please log in again.");
          navigate("/login");
        } else {
          alert("Failed to load loan details. Please try again.");
        }
      } finally {
        setLoading(false);
      }
    };

    if (loanId) fetchLoan();
  }, [loanId, navigate]);

  // 🕐 Loading UI
  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen bg-gradient-to-br from-purple-900 via-purple-800 to-indigo-900 text-white">
        <p className="text-lg">Loading loan details...</p>
      </div>
    );
  }

  // ❌ Loan not found UI
  if (!loan) {
    return (
      <div className="flex flex-col items-center justify-center min-h-screen bg-gradient-to-br from-purple-900 via-purple-800 to-indigo-900 text-white gap-4">
        <p className="text-lg">❌ Loan not found.</p>
        <button
          onClick={() => navigate(-1)}
          className="px-4 py-2 bg-white/10 text-white rounded-md hover:bg-white/20 transition-all"
        >
          ← Back
        </button>
      </div>
    );
  }

  // ✅ Loan Details UI
  return (
    <div className="min-h-screen bg-gradient-to-br from-purple-900 via-purple-800 to-indigo-900 py-10 text-white">
      <div className="max-w-4xl mx-auto bg-gradient-to-r from-purple-700 via-purple-600 to-purple-700/80 p-10 rounded-2xl shadow-2xl border border-purple-600/30 backdrop-blur-md">

        {/* 🔙 Back button */}
        <button
          onClick={() => navigate(-1)}
          className="mb-6 px-5 py-2 bg-gradient-to-r from-purple-500 to-purple-700 text-white font-semibold rounded-lg shadow hover:from-purple-600 hover:to-purple-800 transition-all duration-300"
        >
          ← Back
        </button>

        <h1 className="text-2xl font-semibold text-center text-blue-300 mb-8 border-b border-white/30 pb-2">
          📄 Loan Details
        </h1>

        {/* Loan Details Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6 text-white/90">
          <p><strong>Loan ID:</strong> {loan.loanId}</p>
          <p><strong>Loan Number:</strong> {loan.loanNo || "N/A"}</p>
          <p><strong>Customer Name:</strong> {loan.customerName || "N/A"}</p>
          <p><strong>Branch Name:</strong> {loan.branchName || "N/A"}</p>
          <p><strong>Loan Type:</strong> {loan.loanType || "N/A"}</p>
          <p><strong>Principal:</strong> ₹{loan.principal?.toLocaleString() || 0}</p>
          <p><strong>Interest Rate:</strong> {loan.interestRate ? `${loan.interestRate}%` : "N/A"}</p>
          <p><strong>Tenure:</strong> {loan.tenureMonths || "N/A"} months</p>
          <p>
            <strong>Start Date:</strong>{" "}
            {loan.startDate
              ? new Date(loan.startDate).toLocaleDateString("en-IN", {
                  day: "2-digit",
                  month: "short",
                  year: "numeric",
                })
              : "N/A"}
          </p>
          <p>
            <strong>Maturity Date:</strong>{" "}
            {loan.maturityDate
              ? new Date(loan.maturityDate).toLocaleDateString("en-IN", {
                  day: "2-digit",
                  month: "short",
                  year: "numeric",
                })
              : "N/A"}
          </p>
          <p>
            <strong>Status:</strong>{" "}
            <span
              className={`ml-2 px-3 py-1 rounded-md text-sm ${
                loan.status === "Approved" || loan.status === "Active"
                  ? "bg-green-600/70"
                  : loan.status === "Pending"
                  ? "bg-yellow-500/70"
                  : "bg-red-600/70"
              }`}
            >
              {loan.status}
            </span>
          </p>
        </div>

        {/* Action Buttons */}
        <div className="flex justify-between mt-8">
          <button
            onClick={() => navigate(`/loan/${loanId}/collateral`)}
            className="px-6 py-2 bg-white/10 text-white rounded-lg shadow hover:bg-white/20 transition-all duration-300"
          >
            👁️ View Collateral
          </button>

          {loan.status !== "Approved" && (
            <button
              onClick={() => navigate(`/loan/${loanId}/collateral/create`)}
              className="ml-auto px-6 py-2 bg-gradient-to-r from-green-600 to-green-700 text-white rounded-lg shadow hover:from-green-700 hover:to-green-800 transition-all duration-300"
            >
              ➕ Add Collateral
            </button>
          )}
        </div>
      </div>
    </div>
  );
}
