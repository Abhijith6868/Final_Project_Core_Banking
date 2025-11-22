// Path: src/pages/CreateLoanPage.jsx
import React, { useState, useEffect } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import LoanStepper from "../components/LoanStepper";


export default function CreateLoanPage() {
  const navigate = useNavigate();

  const [currentStep, setCurrentStep] = useState(1);
  const [branches, setBranches] = useState([]);
  const [loan, setLoan] = useState({
    customerId: "",
    branchId: "",
    loanType: "",
    principal: "",
    interestRate: "",
    tenureMonths: "",
  });

  // ✅ Fetch branches safely with token
  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) {
      navigate("/login");
      return;
    }

    axios
      .get("http://localhost:8080/branches", {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((res) => setBranches(res.data))
      .catch((err) => {
        console.error("❌ Failed to load branches:", err);
        if (err.response?.status === 401) {
          localStorage.removeItem("token");
          navigate("/loans/search/");
        }
      });
  }, [navigate]);

  // 🔹 Step controls
  const handleNext = () => currentStep < 2 && setCurrentStep(currentStep + 1);
  const handlePrev = () => currentStep > 1 && setCurrentStep(currentStep - 1);

  // 🔹 Field change handler
  const handleChange = (e) => {
    setLoan({ ...loan, [e.target.name]: e.target.value });
  };

  // 🔹 Prevent Enter key from submitting early
  const handleKeyDown = (e) => {
    if (e.key === "Enter" && currentStep < 2) {
      e.preventDefault();
    }
  };

  // ✅ Safe Back Navigation
  const handleBack = () => {
    const token = localStorage.getItem("token");
    if (!token) {
      alert("⚠️ Session expired. Please log in again.");
      navigate("/login", { replace: true });
      return;
    }

    // ✅ Navigate back to loans list safely
    navigate("/loans", { replace: true });
  };

  // 🔹 Submit handler
  const handleSubmit = async (e) => {
    e.preventDefault();

    if (
      !loan.customerId ||
      !loan.branchId ||
      !loan.loanType ||
      !loan.principal ||
      !loan.interestRate ||
      !loan.tenureMonths
    ) {
      alert("⚠️ Please fill in all required fields before submitting.");
      return;
    }

    const payload = {
      customerId: Number(loan.customerId),
      branchId: Number(loan.branchId),
      loanType: loan.loanType,
      principal: parseFloat(loan.principal),
      interestRate: parseFloat(loan.interestRate),
      tenureMonths: parseInt(loan.tenureMonths),
      // ⛔️ startDate omitted — will be set when approved
    };

    try {
      const token = localStorage.getItem("token");
      const res = await axios.post("http://localhost:8080/api/loans", payload, {
        headers: { Authorization: `Bearer ${token}` },
      });

      console.log("✅ Loan created successfully:", res.data);
      alert("✅ Loan created successfully!");
      navigate("/loans", { replace: true });
    } catch (error) {
      console.error("❌ Error creating loan:", error.response || error);
      if (error.response?.status === 401) {
        localStorage.removeItem("token");
        navigate("/login");
      } else {
        alert("❌ Failed to create loan.");
      }
    }
  };

  // ✅ UI
  return (
    <div className="min-h-screen bg-gradient-to-br from-purple-900 via-purple-800 to-indigo-900 py-10">
      <div className="max-w-3xl mx-auto bg-gradient-to-r from-purple-700 via-purple-600 to-purple-700/80 p-10 rounded-2xl shadow-2xl border border-purple-600/30 backdrop-blur-md">

        {/* Stepper */}
        <LoanStepper currentStep={currentStep} />

        <form
          onSubmit={handleSubmit}
          onKeyDown={handleKeyDown}
          className="mt-10 space-y-8"
        >

          {/* Step 1: Loan Details */}
          {currentStep === 1 && (
            <div className="animate-fadeIn scale-up">
              <h2 className="text-xl font-semibold text-blue-400 mb-4 border-b border-white/30 pb-2">
                💰 Loan Details
              </h2>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <input
                  type="number"
                  name="customerId"
                  placeholder="Enter Customer ID"
                  value={loan.customerId}
                  onChange={handleChange}
                  className="p-3 border border-white/30 rounded-lg bg-white/10 text-white placeholder-white/70 focus:ring-2 focus:ring-purple-500 focus:outline-none"
                />

                <select
                  name="branchId"
                  value={loan.branchId}
                  onChange={handleChange}
                  className="p-3 border border-white/30 rounded-lg bg-white/10 text-white placeholder-white/70 focus:ring-2 focus:ring-purple-500 focus:outline-none"
                >
                  <option value="">Select Branch</option>
                  {branches.map((b) => (
                    <option key={b.branchId} value={b.branchId}>
                      {b.name}
                    </option>
                  ))}
                </select>

                <input
                  type="text"
                  name="loanType"
                  placeholder="Loan Type (e.g., Home Loan)"
                  value={loan.loanType}
                  onChange={handleChange}
                  className="p-3 border border-white/30 rounded-lg bg-white/10 text-white placeholder-white/70 focus:ring-2 focus:ring-purple-500 focus:outline-none"
                />

                <input
                  type="number"
                  name="principal"
                  placeholder="Principal Amount"
                  value={loan.principal}
                  onChange={handleChange}
                  className="p-3 border border-white/30 rounded-lg bg-white/10 text-white placeholder-white/70 focus:ring-2 focus:ring-purple-500 focus:outline-none"
                />

                <input
                  type="number"
                  name="interestRate"
                  placeholder="Interest Rate (%)"
                  value={loan.interestRate}
                  onChange={handleChange}
                  className="p-3 border border-white/30 rounded-lg bg-white/10 text-white placeholder-white/70 focus:ring-2 focus:ring-purple-500 focus:outline-none"
                />

                <input
                  type="number"
                  name="tenureMonths"
                  placeholder="Tenure (in months)"
                  value={loan.tenureMonths}
                  onChange={handleChange}
                  className="p-3 border border-white/30 rounded-lg bg-white/10 text-white placeholder-white/70 focus:ring-2 focus:ring-purple-500 focus:outline-none"
                />

                <div className="p-3 border border-white/30 rounded-lg bg-white/10 text-white">
                  <label className="block text-sm text-white/70">
                    Maturity Date:
                  </label>
                  <p className="mt-1 text-lg font-semibold text-yellow-400">
                    — Pending Approval —
                  </p>
                </div>
              </div>

              {/* 🔹 Navigation Buttons */}
              <div className="flex justify-between mt-8">
                <button
                  type="button"
                  onClick={handleBack}
                  className="px-6 py-2 bg-white/10 text-white rounded-lg shadow hover:bg-white/20 transition-all duration-300"
                >
                  ← Back
                </button>

                <button
                  type="button"
                  onClick={handleNext}
                  className="px-6 py-2 bg-gradient-to-r from-purple-600 to-purple-700 text-white rounded-lg shadow hover:from-purple-700 hover:to-purple-800 transition-all duration-300"
                >
                  Next →
                </button>
              </div>
            </div>
          )}

          {/* Step 2: Review & Submit */}
          {currentStep === 2 && (
            <div className="animate-fadeIn scale-up">
              <h2 className="text-xl font-semibold text-blue-400 mb-4 border-b border-white/30 pb-2">
                📝 Review & Submit
              </h2>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-white">
                <p><strong>Customer ID:</strong> {loan.customerId || "-"}</p>
                <p><strong>Branch:</strong> {branches.find(b => b.branchId === Number(loan.branchId))?.name || "-"}</p>
                <p><strong>Loan Type:</strong> {loan.loanType}</p>
                <p><strong>Principal:</strong> ₹{loan.principal}</p>
                <p><strong>Interest Rate:</strong> {loan.interestRate}%</p>
                <p><strong>Tenure:</strong> {loan.tenureMonths} months</p>
                <p><strong>Start Date:</strong> <span className="text-yellow-400">— Will be assigned on approval —</span></p>
                <p><strong>Maturity Date:</strong> <span className="text-yellow-400">— Pending Approval —</span></p>
              </div>

              {/* Navigation Buttons */}
              <div className="flex justify-between mt-8">
                <button
                  type="button"
                  onClick={() => navigate("/loans/search", { replace: true })}
                  className="px-6 py-2 bg-white/10 text-white rounded-lg shadow hover:bg-white/20 transition-all duration-300"
                >
                  ← Previous
                </button>
                <button
                  type="submit"
                  className="px-6 py-2 bg-gradient-to-r from-green-600 to-green-700 text-white rounded-lg shadow hover:from-green-700 hover:to-green-800 transition-all duration-300"
                >
                  Submit ✅
                </button>
              </div>
            </div>
          )}
        </form>
      </div>
    </div>
  );
}
