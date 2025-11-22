import React, { useState, useEffect } from "react";
import axios from "axios";
import CollateralStepper from "../components/CollateralStepper";
import { useParams } from "react-router-dom";

export default function CollateralCreatePage() {
  const { loanId } = useParams(); // route: /loan/:loanId/collateral/create

  const [currentStep, setCurrentStep] = useState(1);
  const [loanNumber, setLoanNumber] = useState("");
  const [customerId, setCustomerId] = useState("");
  const [customerName, setCustomerName] = useState("");

  const [collateral, setCollateral] = useState({
    collateralType: "",
    description: "",
    estimatedValue: "",
    pledgedDate: new Date().toISOString().split("T")[0],
    status: "Active",
  });

  // 🔄 Fetch Loan + Customer Info
  useEffect(() => {
    if (loanId) {
      axios
        .get(`http://localhost:8080/api/loans/${loanId}`)
        .then((res) => {
          const loan = res.data;
          setLoanNumber(loan.loanNumber);
          setCustomerId(loan.customer.customerId);
          setCustomerName(`${loan.customer.firstName} ${loan.customer.lastName}`);
        })
        .catch((err) => console.error("Error fetching loan:", err));
    }
  }, [loanId]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setCollateral({ ...collateral, [name]: value });
  };

  const handleNext = () => currentStep < 3 && setCurrentStep(currentStep + 1);
  const handlePrev = () => currentStep > 1 && setCurrentStep(currentStep - 1);

  // ✅ Option 3: Guarded handleSubmit to prevent premature submit
  const handleSubmit = async (e) => {
    e.preventDefault();

    // Block submit until last step
    if (currentStep < 3) {
      console.log("⏸️ Ignoring premature submit — not on final step yet.");
      return;
    }

    const payload = {
      ...collateral,
      loanId: Number(loanId),
      customerId,
      loanNumber,
      customerName,
    };

    try {
      await axios.post("http://localhost:8080/api/collaterals", payload);
      alert("✅ Collateral created successfully!");

      // Reset form
      setCollateral({
        collateralType: "",
        description: "",
        estimatedValue: "",
        pledgedDate: new Date().toISOString().split("T")[0],
        status: "Active",
      });
      setCurrentStep(1);
    } catch (error) {
      console.error("Error creating collateral:", error);
      alert("❌ Failed to create collateral.");
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-purple-900 via-purple-800 to-indigo-900 py-10">
      <div className="max-w-3xl mx-auto bg-gradient-to-r from-purple-700 via-purple-600 to-purple-700/80 p-10 rounded-2xl shadow-2xl border border-purple-600/30 backdrop-blur-md">
        
        {/* Stepper */}
        <CollateralStepper currentStep={currentStep} />

        <form onSubmit={handleSubmit} className="mt-10 space-y-8">
          {/* Step 1: Loan & Customer Info */}
          {currentStep === 1 && (
            <div className="animate-fadeIn scale-up">
              <h2 className="text-xl font-semibold text-blue-400 mb-4 border-b border-white/30 pb-2">
                🧾 Loan & Customer Info
              </h2>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6 text-white">
                <p><strong>Loan ID:</strong> {loanId}</p>
                <p><strong>Loan Number:</strong> {loanNumber || "Loading..."}</p>
                <p><strong>Customer ID:</strong> {customerId || "Loading..."}</p>
                <p><strong>Customer Name:</strong> {customerName || "Loading..."}</p>
              </div>
              <p className="text-sm text-white/60 mt-3">
                These details are automatically fetched from the loan.
              </p>
            </div>
          )}

          {/* Step 2: Collateral Details */}
          {currentStep === 2 && (
            <div className="animate-fadeIn scale-up">
              <h2 className="text-xl font-semibold text-blue-400 mb-4 border-b border-white/30 pb-2 flex items-center gap-2">
                <span role="img" aria-label="bank">🏦</span> Collateral Details
              </h2>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <select
                  name="collateralType"
                  value={collateral.collateralType}
                  onChange={handleChange}
                  className="w-full p-3 border border-white/30 rounded-lg bg-white text-black focus:ring-2 focus:ring-purple-500 focus:outline-none"
                  required
                >
                  <option value="">Select Collateral Type</option>
                  <option value="Property">Property</option>
                  <option value="Vehicle">Vehicle</option>
                  <option value="Gold">Gold</option>
                  <option value="Shares">Shares</option>
                </select>

                <input
                  type="number"
                  name="estimatedValue"
                  placeholder="Estimated Value (₹)"
                  value={collateral.estimatedValue}
                  onChange={handleChange}
                  className="w-full p-3 border border-white/30 rounded-lg bg-white text-black focus:ring-2 focus:ring-purple-500 focus:outline-none"
                  required
                />

                <input
                  type="date"
                  name="pledgedDate"
                  value={collateral.pledgedDate}
                  onChange={handleChange}
                  className="w-full p-3 border border-white/30 rounded-lg bg-white text-black focus:ring-2 focus:ring-purple-500 focus:outline-none"
                  required
                />

                <select
                  name="status"
                  value={collateral.status}
                  onChange={handleChange}
                  className="w-full p-3 border border-white/30 rounded-lg bg-white text-black focus:ring-2 focus:ring-purple-500 focus:outline-none"
                >
                  <option value="Active">Active</option>
                  <option value="Released">Released</option>
                  <option value="Seized">Seized</option>
                </select>

                {/* Make description full-width */}
                <div className="md:col-span-2">
                  <textarea
                    name="description"
                    placeholder="Description"
                    value={collateral.description}
                    onChange={handleChange}
                    rows="3"
                    className="w-full p-3 border border-white/30 rounded-lg bg-white text-black focus:ring-2 focus:ring-purple-500 focus:outline-none"
                    required
                  ></textarea>
                </div>
              </div>
            </div>
          )}

          {/* Step 3: Review & Submit */}
          {currentStep === 3 && (
            <div className="animate-fadeIn scale-up">
              <h2 className="text-xl font-semibold text-blue-400 mb-4 border-b border-white/30 pb-2">
                📝 Review & Submit
              </h2>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-white">
                <p><strong>Loan ID:</strong> {loanId}</p>
                <p><strong>Loan Number:</strong> {loanNumber}</p>
                <p><strong>Customer Name:</strong> {customerName}</p>
                <p><strong>Collateral Type:</strong> {collateral.collateralType}</p>
                <p><strong>Estimated Value:</strong> ₹{collateral.estimatedValue}</p>
                <p><strong>Pledged Date:</strong> {collateral.pledgedDate}</p>
                <p><strong>Status:</strong> {collateral.status}</p>
                <p><strong>Description:</strong> {collateral.description}</p>
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
            {currentStep < 3 ? (
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
