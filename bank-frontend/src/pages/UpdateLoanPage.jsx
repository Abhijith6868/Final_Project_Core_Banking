import React, { useState, useEffect } from "react";
import axios from "axios";
import { useParams, useNavigate } from "react-router-dom";

export default function UpdateLoanPage() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [loan, setLoan] = useState({
    loanType: "",
    loanAmount: "",
    interestRate: "",
    startDate: "",
    endDate: "",
    tenure: "",
    customerId: "",
    branchId: "",
    // status: "",
  });

  // ✅ Load loan details
  useEffect(() => {
    axios
      .get(`http://localhost:8080/api/loans/${id}`)
      .then((res) => setLoan(res.data))
      .catch((err) => console.error("Error loading loan:", err));
  }, [id]);

  const handleChange = (e) => {
    setLoan({ ...loan, [e.target.name]: e.target.value });
  };

  // ✅ Update loan details
  const handleSubmit = (e) => {
    e.preventDefault();
    axios
      .put(`http://localhost:8080/api/loans/${id}`, loan)
      .then(() => {
        alert("✅ Loan updated successfully!");
        navigate("/loans");
      })
      .catch((err) => console.error("Error updating loan:", err));
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-purple-900 via-purple-800 to-indigo-900 py-10 px-4 flex justify-center items-start">
      <div className="w-full max-w-5xl">
        {/* Header */}
        <div className="bg-gradient-to-r from-purple-700 via-purple-600 to-indigo-700 text-white px-6 py-4 rounded-t-xl shadow-lg font-semibold text-lg">
          Update Loan Details
        </div>

        {/* Form */}
        <form
          onSubmit={handleSubmit}
          className="bg-white/10 backdrop-blur-md border border-white/20 text-white p-8 rounded-b-xl shadow-lg"
        >
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-x-8 gap-y-6">
            {/* Loan Type */}
            <div>
              <label className="block text-sm font-medium text-white/80 mb-1">
                Loan Type
              </label>
              <input
                type="text"
                name="loanType"
                value={loan.loanType}
                onChange={handleChange}
                placeholder="Enter loan type"
                className="w-full p-2 rounded bg-white/20 text-white placeholder-white/60 focus:outline-none focus:ring-2 focus:ring-indigo-400"
              />
            </div>

            {/* Loan Amount */}
            <div>
              <label className="block text-sm font-medium text-white/80 mb-1">
                Loan Amount
              </label>
              <input
                type="number"
                name="loanAmount"
                value={loan.loanAmount}
                onChange={handleChange}
                placeholder="Enter loan amount"
                className="w-full p-2 rounded bg-white/20 text-white placeholder-white/60 focus:outline-none focus:ring-2 focus:ring-indigo-400"
              />
            </div>

            {/* Interest Rate */}
            <div>
              <label className="block text-sm font-medium text-white/80 mb-1">
                Interest Rate (%)
              </label>
              <input
                type="number"
                step="0.01"
                name="interestRate"
                value={loan.interestRate}
                onChange={handleChange}
                placeholder="Enter interest rate"
                className="w-full p-2 rounded bg-white/20 text-white placeholder-white/60 focus:outline-none focus:ring-2 focus:ring-indigo-400"
              />
            </div>

            {/* Start Date */}
            <div>
              <label className="block text-sm font-medium text-white/80 mb-1">
                Start Date
              </label>
              <input
                type="date"
                name="startDate"
                value={loan.startDate ? loan.startDate.slice(0, 10) : ""}
                onChange={handleChange}
                className="w-full p-2 rounded bg-white/20 text-white focus:outline-none focus:ring-2 focus:ring-indigo-400"
              />
            </div>

            {/* End Date */}
            <div>
              <label className="block text-sm font-medium text-white/80 mb-1">
                End Date
              </label>
              <input
                type="date"
                name="endDate"
                value={loan.endDate ? loan.endDate.slice(0, 10) : ""}
                onChange={handleChange}
                className="w-full p-2 rounded bg-white/20 text-white focus:outline-none focus:ring-2 focus:ring-indigo-400"
              />
            </div>

            {/* Tenure */}
            <div>
              <label className="block text-sm font-medium text-white/80 mb-1">
                Tenure (Months)
              </label>
              <input
                type="number"
                name="tenure"
                value={loan.tenure}
                onChange={handleChange}
                placeholder="Enter tenure"
                className="w-full p-2 rounded bg-white/20 text-white placeholder-white/60 focus:outline-none focus:ring-2 focus:ring-indigo-400"
              />
            </div>

            {/* Customer ID */}
            <div>
              <label className="block text-sm font-medium text-white/80 mb-1">
                Customer ID
              </label>
              <input
                type="text"
                name="customerId"
                value={loan.customerId}
                onChange={handleChange}
                placeholder="Enter customer ID"
                className="w-full p-2 rounded bg-white/20 text-white placeholder-white/60 focus:outline-none focus:ring-2 focus:ring-indigo-400"
              />
            </div>

            {/* Branch ID */}
            <div>
              <label className="block text-sm font-medium text-white/80 mb-1">
                Branch ID
              </label>
              <input
                type="text"
                name="branchId"
                value={loan.branchId}
                onChange={handleChange}
                placeholder="Enter branch ID"
                className="w-full p-2 rounded bg-white/20 text-white placeholder-white/60 focus:outline-none focus:ring-2 focus:ring-indigo-400"
              />
            </div>

            {/* Status */}
            {/* <div>
              <label className="block text-sm font-medium text-white/80 mb-1">
                Status
              </label>
              <select
                name="status"
                value={loan.status}
                onChange={handleChange}
                className="w-full p-2 rounded bg-white/20 text-white focus:outline-none focus:ring-2 focus:ring-indigo-400"
              >
                <option value="">Select Status</option>
                <option value="Active" className="text-black">
                  Active
                </option>
                <option value="Closed" className="text-black">
                  Closed
                </option>
                <option value="Pending" className="text-black">
                  Pending
                </option>
                <option value="Rejected" className="text-black">
                  Rejected
                </option>
              </select>
            </div> */}
          </div>

          {/* Buttons */}
          <div className="flex justify-end space-x-4 mt-8">
            <button
              type="button"
              onClick={() => navigate("/loans/search")}
              className="bg-white/10 text-white px-5 py-2 rounded-md hover:bg-white/20 transition"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="bg-indigo-500 hover:bg-indigo-600 text-white px-6 py-2 rounded-md shadow-md transition"
            >
              Update
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
