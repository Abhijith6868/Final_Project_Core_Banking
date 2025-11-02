// Path: src/pages/CreateLoanPage.jsx
import React, { useState, useEffect } from "react";
import axios from "axios";
import LoanStepper from "../components/LoanStepper";

export default function CreateLoanPage() {
  const [currentStep, setCurrentStep] = useState(1);
  const [branches, setBranches] = useState([]);
  const [customers, setCustomers] = useState([]);
  const [loan, setLoan] = useState({
    customerId: "",
    branchId: "",
    loanType: "",
    principal: "",
    interestRate: "",
    tenureMonths: "",
    startDate: "",
  });

  // Fetch branches & customers
  useEffect(() => {
    axios.get("http://localhost:8080/branches")
      .then(res => setBranches(res.data))
      .catch(err => console.error(err));

    axios.get("http://localhost:8080/customers")
      .then(res => setCustomers(res.data))
      .catch(err => console.error(err));
  }, []);

  const handleNext = () => currentStep < 2 && setCurrentStep(currentStep + 1);
  const handlePrev = () => currentStep > 1 && setCurrentStep(currentStep - 1);

  const handleChange = (e) => {
    setLoan({ ...loan, [e.target.name]: e.target.value });
  };

  // Calculate maturity date (visual only)
  const calculateMaturityDate = () => {
    if (!loan.startDate || !loan.tenureMonths) return "";
    const start = new Date(loan.startDate);
    start.setMonth(start.getMonth() + parseInt(loan.tenureMonths));
    return start.toISOString().split("T")[0];
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const payload = {
      customerId: Number(loan.customerId),
      branchId: Number(loan.branchId),
      loanType: loan.loanType,
      principal: parseFloat(loan.principal),
      interestRate: parseFloat(loan.interestRate),
      tenureMonths: parseInt(loan.tenureMonths),
      startDate: loan.startDate,
    };

    try {
      await axios.post("http://localhost:8080/api/loans", payload);
      alert("✅ Loan created successfully!");
    } catch (error) {
      console.error("Error creating loan:", error);
      alert("❌ Failed to create loan.");
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-purple-900 via-purple-800 to-indigo-900 py-10">
      <div className="max-w-3xl mx-auto bg-gradient-to-r from-purple-700 via-purple-600 to-purple-700/80 p-10 rounded-2xl shadow-2xl border border-purple-600/30 backdrop-blur-md">

        {/* Stepper */}
        <LoanStepper currentStep={currentStep} />

        <form onSubmit={handleSubmit} className="mt-10 space-y-8">

          {/* Step 1: Loan Details */}
          {currentStep === 1 && (
            <div className="animate-fadeIn scale-up">
              <h2 className="text-xl font-semibold text-blue-400 mb-4 border-b border-white/30 pb-2">
                💰 Loan Details
              </h2>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                {/* Customer */}
                <select
                  name="customerId"
                  value={loan.customerId}
                  onChange={handleChange}
                  className="p-3 border border-white/30 rounded-lg bg-white/10 text-white placeholder-white/70 focus:ring-2 focus:ring-purple-500 focus:outline-none"
                >
                  <option value="">Select Customer</option>
                  {customers.map((c) => (
                    <option key={c.customerId} value={c.customerId}>
                      {c.firstName} {c.lastName}
                    </option>
                  ))}
                </select>

                {/* Branch */}
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

                {/* Loan Type */}
                <input
                  type="text"
                  name="loanType"
                  placeholder="Loan Type (e.g., Home Loan)"
                  value={loan.loanType}
                  onChange={handleChange}
                  className="p-3 border border-white/30 rounded-lg bg-white/10 text-white placeholder-white/70 focus:ring-2 focus:ring-purple-500 focus:outline-none"
                />

                {/* Principal */}
                <input
                  type="number"
                  name="principal"
                  placeholder="Principal Amount"
                  value={loan.principal}
                  onChange={handleChange}
                  className="p-3 border border-white/30 rounded-lg bg-white/10 text-white placeholder-white/70 focus:ring-2 focus:ring-purple-500 focus:outline-none"
                />

                {/* Interest Rate */}
                <input
                  type="number"
                  name="interestRate"
                  placeholder="Interest Rate (%)"
                  value={loan.interestRate}
                  onChange={handleChange}
                  className="p-3 border border-white/30 rounded-lg bg-white/10 text-white placeholder-white/70 focus:ring-2 focus:ring-purple-500 focus:outline-none"
                />

                {/* Tenure */}
                <input
                  type="number"
                  name="tenureMonths"
                  placeholder="Tenure (in months)"
                  value={loan.tenureMonths}
                  onChange={handleChange}
                  className="p-3 border border-white/30 rounded-lg bg-white/10 text-white placeholder-white/70 focus:ring-2 focus:ring-purple-500 focus:outline-none"
                />

                {/* Start Date */}
                <input
                  type="date"
                  name="startDate"
                  value={loan.startDate}
                  onChange={handleChange}
                  className="p-3 border border-white/30 rounded-lg bg-white/10 text-white focus:ring-2 focus:ring-purple-500 focus:outline-none"
                />

                {/* Maturity Date (calculated only) */}
                <div className="p-3 border border-white/30 rounded-lg bg-white/10 text-white">
                  <label className="block text-sm text-white/70">Calculated Maturity Date:</label>
                  <p className="mt-1 text-lg font-semibold text-green-400">
                    {calculateMaturityDate() || "—"}
                  </p>
                </div>
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
                <p><strong>Customer:</strong> {customers.find(c => c.customerId === Number(loan.customerId))?.firstName || "-"} {customers.find(c => c.customerId === Number(loan.customerId))?.lastName || ""}</p>
                <p><strong>Branch:</strong> {branches.find(b => b.branchId === Number(loan.branchId))?.name || "-"}</p>
                <p><strong>Loan Type:</strong> {loan.loanType}</p>
                <p><strong>Principal:</strong> ₹{loan.principal}</p>
                <p><strong>Interest Rate:</strong> {loan.interestRate}%</p>
                <p><strong>Tenure:</strong> {loan.tenureMonths} months</p>
                <p><strong>Start Date:</strong> {loan.startDate}</p>
                <p><strong>Maturity Date:</strong> {calculateMaturityDate()}</p>
              </div>
            </div>
          )}

          {/* Navigation Buttons */}
          <div className="flex justify-between mt-8">
            {currentStep > 1 && (
              <button
                type="button"
                onClick={handlePrev}
                className="px-6 py-2 bg-white/10 text-white rounded-lg shadow hover:bg-white/20 transition-all duration-300"
              >
                ← Previous
              </button>
            )}
            {currentStep < 2 ? (
              <button
                type="button"
                onClick={handleNext}
                className="ml-auto px-6 py-2 bg-gradient-to-r from-purple-600 to-purple-700 text-white rounded-lg shadow hover:from-purple-700 hover:to-purple-800 transition-all duration-300"
              >
                Next →
              </button>
            ) : (
              <button
                type="submit"
                className="ml-auto px-6 py-2 bg-gradient-to-r from-green-600 to-green-700 text-white rounded-lg shadow hover:from-green-700 hover:to-green-800 transition-all duration-300"
              >
                Submit ✅
              </button>
            )}
          </div>

        </form>
      </div>
    </div>
  );
}
