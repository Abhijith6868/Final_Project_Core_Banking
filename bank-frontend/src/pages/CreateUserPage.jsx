import React, { useState, useEffect } from "react";
import axios from "axios";
import UserStepper from "../components/UserStepper";

export default function CreateUserPage() {
  const [currentStep, setCurrentStep] = useState(1);
  const [branches, setBranches] = useState([]);

  const [user, setUser] = useState({
    username: "",
    firstName: "",
    lastName: "",
    email: "",
    phoneNumber: "",
    role: "",
    password: "",
    branchId: "",
  });

  useEffect(() => {
    axios
      .get("http://localhost:8080/branches")
      .then((res) => setBranches(res.data))
      .catch((err) => console.error(err));
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setUser((prev) => ({
      ...prev,
      [name]: name === "accountLocked" ? value === "true" : value,
    }));
  };

  const handleNext = () => currentStep < 2 && setCurrentStep(currentStep + 1);
  const handlePrev = () => currentStep > 1 && setCurrentStep(currentStep - 1);

  const handleSubmit = async () => {
    try {
      await axios.post("http://localhost:8080/api/staff/create", user);
      alert("✅ User created successfully!");
    } catch (error) {
      console.error("Error creating user:", error);
      alert("❌ Failed to create user.");
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-purple-900 via-purple-800 to-indigo-900 py-10">
      <div className="max-w-3xl mx-auto bg-gradient-to-r from-purple-700 via-purple-600 to-purple-700/80 p-10 rounded-2xl shadow-2xl border border-purple-600/30 backdrop-blur-md">

        {/* Stepper */}
        <UserStepper currentStep={currentStep} />

        {/* Form */}
        <form
          className="mt-10 space-y-8"
          onKeyDown={(e) => {
            // Prevent auto-submit when pressing Enter inside any input
            if (e.key === "Enter") e.preventDefault();
          }}
        >

          {/* Step 1: User Details */}
          {currentStep === 1 && (
            <div className="animate-fadeIn scale-up">
              <h2 className="text-xl font-semibold text-blue-400 mb-4 border-b border-white/30 pb-2">
                👤 User Details
              </h2>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">

                <input
                  type="text"
                  name="username"
                  placeholder="Username"
                  value={user.username}
                  onChange={handleChange}
                  className="p-3 border border-white/30 rounded-lg bg-white/10 text-white placeholder-white/70 focus:ring-2 focus:ring-purple-500 focus:outline-none"
                />

                <input
                  type="text"
                  name="firstName"
                  placeholder="First Name"
                  value={user.firstName}
                  onChange={handleChange}
                  className="p-3 border border-white/30 rounded-lg bg-white/10 text-white placeholder-white/70 focus:ring-2 focus:ring-purple-500 focus:outline-none"
                />

                <input
                  type="text"
                  name="lastName"
                  placeholder="Last Name"
                  value={user.lastName}
                  onChange={handleChange}
                  className="p-3 border border-white/30 rounded-lg bg-white/10 text-white placeholder-white/70 focus:ring-2 focus:ring-purple-500 focus:outline-none"
                />

                <input
                  type="email"
                  name="email"
                  placeholder="Email"
                  value={user.email}
                  onChange={handleChange}
                  className="p-3 border border-white/30 rounded-lg bg-white/10 text-white placeholder-white/70 focus:ring-2 focus:ring-purple-500 focus:outline-none"
                />

                <input
                  type="text"
                  name="phoneNumber"
                  placeholder="Phone Number"
                  value={user.phoneNumber}
                  onChange={handleChange}
                  className="p-3 border border-white/30 rounded-lg bg-white/10 text-white placeholder-white/70 focus:ring-2 focus:ring-purple-500 focus:outline-none"
                />

                <select
                  name="role"
                  value={user.role}
                  onChange={handleChange}
                  className="p-3 border border-white/30 rounded-lg bg-white/10 text-white focus:ring-2 focus:ring-purple-500 focus:outline-none"
                >
                  <option value="">Select Role</option>
                  <option value="AUDITOR">AUDITOR</option>
                  <option value="OFFICER">OFFICER</option>
                  <option value="MANAGER">MANAGER</option>
                </select>

                <select
                  name="branchId"
                  value={user.branchId}
                  onChange={handleChange}
                  className="p-3 border border-white/30 rounded-lg bg-white/10 text-white focus:ring-2 focus:ring-purple-500 focus:outline-none"
                >
                  <option value="">Select Branch</option>
                  {branches.map((b) => (
                    <option key={b.branchId} value={b.branchId}>
                      {b.name}
                    </option>
                  ))}
                </select>

                <input
                  type="password"
                  name="password"
                  placeholder="Password"
                  value={user.password}
                  onChange={handleChange}
                  className="p-3 border border-white/30 rounded-lg bg-white/10 text-white placeholder-white/70 focus:ring-2 focus:ring-purple-500 focus:outline-none"
                />
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
                {Object.entries(user).map(([key, value]) => (
                  <p key={key}>
                    <strong>{key}:</strong> {String(value || "-")}
                  </p>
                ))}
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
                type="button"
                onClick={handleSubmit}
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
