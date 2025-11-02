// Path: src/pages/CreateCustomerPage.jsx
import React, { useState, useEffect } from "react";
import axios from "axios";
import CustomerStepper from "../components/CustomerStepper";
import { states } from "../data/india-states-cities";

export default function CreateCustomerPage() {
  const [currentStep, setCurrentStep] = useState(1);
  const [branches, setBranches] = useState([]);
  const [customer, setCustomer] = useState({
    firstName: "",
    lastName: "",
    dob: "",
    email: "",
    phone: "",
    kycDetails: "",
    addressLine1: "",
    city: "",
    state: "",
    zip: "",
    branchId: "",
  });

  useEffect(() => {
    axios.get("http://localhost:8080/branches")
      .then(res => setBranches(res.data))
      .catch(err => console.error(err));
  }, []);

  const handleNext = () => currentStep < 4 && setCurrentStep(currentStep + 1);
  const handlePrev = () => currentStep > 1 && setCurrentStep(currentStep - 1);

  const handleChange = (e) => {
    setCustomer({ ...customer, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await axios.post("http://localhost:8080/customers", customer);
      alert("✅ Customer created successfully!");
    } catch (error) {
      console.error("Error creating customer:", error);
      alert("❌ Failed to create customer.");
    }
  };

  const selectedState = states.find(s => s.name === customer.state);

  return (
    <div className="min-h-screen bg-gradient-to-br from-purple-900 via-purple-800 to-indigo-900 py-10">
      <div className="max-w-3xl mx-auto bg-gradient-to-r from-purple-700 via-purple-600 to-purple-700/80 p-10 rounded-2xl shadow-2xl border border-purple-600/30 backdrop-blur-md">
        {/* Stepper */}
        <CustomerStepper currentStep={currentStep} totalSteps={4} />

        <form onSubmit={handleSubmit} className="mt-10 space-y-8">

          {/* Step 1: Personal Info */}
          {currentStep === 1 && (
            <div className="animate-fadeIn scale-up">
              <h2 className="text-xl font-semibold text-blue-400 mb-4 border-b border-white/30 pb-2">
                👤 Personal Information
              </h2>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <input
                  type="text"
                  name="firstName"
                  placeholder="First Name"
                  value={customer.firstName}
                  onChange={handleChange}
                  className="p-3 border border-white/30 rounded-lg bg-white/10 text-white placeholder-white/70 focus:ring-2 focus:ring-purple-500 focus:outline-none"
                />
                <input
                  type="text"
                  name="lastName"
                  placeholder="Last Name"
                  value={customer.lastName}
                  onChange={handleChange}
                  className="p-3 border border-white/30 rounded-lg bg-white/10 text-white placeholder-white/70 focus:ring-2 focus:ring-purple-500 focus:outline-none"
                />
                <input
                  type="date"
                  name="dob"
                  placeholder="Date of Birth"
                  value={customer.dob}
                  onChange={handleChange}
                  className="p-3 border border-white/30 rounded-lg bg-white/10 text-white placeholder-white/70 focus:ring-2 focus:ring-purple-500 focus:outline-none"
                />
                <input
                  type="email"
                  name="email"
                  placeholder="Email Address"
                  value={customer.email}
                  onChange={handleChange}
                  className="p-3 border border-white/30 rounded-lg bg-white/10 text-white placeholder-white/70 focus:ring-2 focus:ring-purple-500 focus:outline-none"
                />
              </div>
            </div>
          )}

          {/* Step 2: Address Info */}
          {currentStep === 2 && (
            <div className="animate-fadeIn scale-up">
              <h2 className="text-xl font-semibold text-blue-400 mb-4 border-b border-white/30 pb-2">
                🏠 Address Information
              </h2>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <input
                  type="text"
                  name="addressLine1"
                  placeholder="Address Line 1"
                  value={customer.addressLine1}
                  onChange={handleChange}
                  className="p-3 border border-white/30 rounded-lg bg-white text-black focus:ring-2 focus:ring-purple-500 focus:outline-none"
                />
                <input
                  type="text"
                  name="zip"
                  placeholder="Zip / PIN"
                  value={customer.zip}
                  onChange={handleChange}
                  className="p-3 border border-white/30 rounded-lg bg-white text-black focus:ring-2 focus:ring-purple-500 focus:outline-none"
                />
                <select
                  name="state"
                  value={customer.state}
                  onChange={handleChange}
                  className="p-3 border border-white/30 rounded-lg bg-white text-black focus:ring-2 focus:ring-purple-500 focus:outline-none"
                >
                  <option value="">Select State</option>
                  {states.map((s) => (
                    <option key={s.name} value={s.name} className="text-black">
                      {s.name}
                    </option>
                  ))}
                </select>

                <select
                  name="city"
                  value={customer.city}
                  onChange={handleChange}
                  className="p-3 border border-white/30 rounded-lg bg-white text-black focus:ring-2 focus:ring-purple-500 focus:outline-none"
                >
                  <option value="">Select City</option>
                  {selectedState?.cities.map((c) => (
                    <option key={c} value={c} className="text-black">
                      {c}
                    </option>
                  ))}
                </select>
              </div>
            </div>
          )}

          {/* Step 3: Contact & KYC */}
          {currentStep === 3 && (
            <div className="animate-fadeIn scale-up">
              <h2 className="text-xl font-semibold text-blue-400 mb-4 border-b border-white/30 pb-2">
                📞 Contact & KYC
              </h2>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <input
                  type="text"
                  name="kycDetails"
                  placeholder="KYC Details"
                  value={customer.kycDetails}
                  onChange={handleChange}
                  className="p-3 border border-white/30 rounded-lg bg-white/10 text-white placeholder-white/70 focus:ring-2 focus:ring-purple-500 focus:outline-none"
                />
                <input
                  type="text"
                  name="phone"
                  placeholder="Phone Number"
                  value={customer.phone}
                  onChange={handleChange}
                  className="p-3 border border-white/30 rounded-lg bg-white/10 text-white placeholder-white/70 focus:ring-2 focus:ring-purple-500 focus:outline-none"
                />
                <select
                  name="branchId"
                  value={customer.branchId}
                  onChange={handleChange}
                  className="p-3 border border-white/30 rounded-lg bg-white/10 text-white placeholder-white/70 focus:ring-2 focus:ring-purple-500 focus:outline-none"
                >
                  <option value="">Select Branch</option>
                  {branches.map(branch => (
                    <option key={branch.branchId} value={branch.branchId}>
                      {branch.name}
                    </option>
                  ))}
                </select>
              </div>
            </div>
          )}

          {/* Step 4: Review & Submit */}
          {currentStep === 4 && (
            <div className="animate-fadeIn scale-up">
              <h2 className="text-xl font-semibold text-blue-400 mb-4 border-b border-white/30 pb-2">
                📝 Review & Submit
              </h2>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-white">
                <p><strong>Name:</strong> {customer.firstName} {customer.lastName}</p>
                <p><strong>Date of Birth:</strong> {customer.dob}</p>
                <p><strong>Email:</strong> {customer.email}</p>
                <p><strong>Address:</strong> {customer.addressLine1}, {customer.city}, {customer.state}, {customer.zip}</p>
                <p><strong>Phone:</strong> {customer.phone}</p>
                <p><strong>KYC:</strong> {customer.kycDetails}</p>
                <p><strong>Branch:</strong> {branches.find(b => b.branchId === Number(customer.branchId))?.name || "-"}</p>
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
            {currentStep < 4 ? (
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
