import React, { useState, useEffect } from "react";
import axios from "axios";
import { useParams, useNavigate } from "react-router-dom";
import { states as indiaStates } from "../data/india-states-cities";

export default function UpdateCustomerPage() {
  const { id } = useParams();
  const navigate = useNavigate();

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

  const [cities, setCities] = useState([]);

  // ✅ Load customer details
  useEffect(() => {
    axios
      .get(`http://localhost:8080/api/customers/${id}`)
      .then((res) => {
        const data = res.data;
        setCustomer(data);

        // Prefill cities if a state exists
        if (data.state) {
          const matchedState = indiaStates.find((s) => s.name === data.state);
          if (matchedState) setCities(matchedState.cities);
        }
      })
      .catch((err) => console.error("Error loading customer:", err));
  }, [id]);

  // ✅ When state changes, update city list
  const handleStateChange = (e) => {
    const selectedState = e.target.value;
    setCustomer({ ...customer, state: selectedState, city: "" });

    const matchedState = indiaStates.find((s) => s.name === selectedState);
    setCities(matchedState ? matchedState.cities : []);
  };

  const handleChange = (e) => {
    setCustomer({ ...customer, [e.target.name]: e.target.value });
  };

  // ✅ Update customer
  const handleSubmit = (e) => {
    e.preventDefault();
    axios
      .put(`http://localhost:8080/api/customers/${id}`, customer)
      .then(() => {
        alert("✅ Customer updated successfully!");
        navigate("/customers");
      })
      .catch((err) => console.error("Error updating customer:", err));
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-purple-900 via-purple-800 to-indigo-900 py-10 px-4 flex justify-center items-start">
      <div className="w-full max-w-5xl">
        {/* Header */}
        <div className="bg-gradient-to-r from-purple-700 via-purple-600 to-indigo-700 text-white px-6 py-4 rounded-t-xl shadow-lg font-semibold text-lg">
          Update Customer Details
        </div>

        {/* Form */}
        <form
          onSubmit={handleSubmit}
          className="bg-white/10 backdrop-blur-md border border-white/20 text-white p-8 rounded-b-xl shadow-lg"
        >
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-x-8 gap-y-6">
            {/* First Name */}
            <div>
              <label className="block text-sm font-medium text-white/80 mb-1">
                First Name
              </label>
              <input
                type="text"
                name="firstName"
                value={customer.firstName}
                onChange={handleChange}
                className="w-full p-2 rounded bg-white/20 text-white placeholder-white/60 focus:outline-none focus:ring-2 focus:ring-indigo-400"
                placeholder="Enter first name"
              />
            </div>

            {/* Last Name */}
            <div>
              <label className="block text-sm font-medium text-white/80 mb-1">
                Last Name
              </label>
              <input
                type="text"
                name="lastName"
                value={customer.lastName}
                onChange={handleChange}
                className="w-full p-2 rounded bg-white/20 text-white placeholder-white/60 focus:outline-none focus:ring-2 focus:ring-indigo-400"
                placeholder="Enter last name"
              />
            </div>

            {/* DOB */}
            <div>
              <label className="block text-sm font-medium text-white/80 mb-1">
                Date of Birth
              </label>
              <input
                type="date"
                name="dob"
                value={customer.dob ? customer.dob.slice(0, 10) : ""}
                onChange={handleChange}
                className="w-full p-2 rounded bg-white/20 text-white focus:outline-none focus:ring-2 focus:ring-indigo-400"
              />
            </div>

            {/* Email */}
            <div>
              <label className="block text-sm font-medium text-white/80 mb-1">
                Email
              </label>
              <input
                type="email"
                name="email"
                value={customer.email}
                onChange={handleChange}
                className="w-full p-2 rounded bg-white/20 text-white placeholder-white/60 focus:outline-none focus:ring-2 focus:ring-indigo-400"
                placeholder="Enter email"
              />
            </div>

            {/* Phone */}
            <div>
              <label className="block text-sm font-medium text-white/80 mb-1">
                Phone
              </label>
              <input
                type="text"
                name="phone"
                value={customer.phone}
                onChange={handleChange}
                className="w-full p-2 rounded bg-white/20 text-white placeholder-white/60 focus:outline-none focus:ring-2 focus:ring-indigo-400"
                placeholder="Enter phone"
              />
            </div>

            {/* KYC */}
            <div>
              <label className="block text-sm font-medium text-white/80 mb-1">
                KYC Details
              </label>
              <input
                type="text"
                name="kycDetails"
                value={customer.kycDetails}
                onChange={handleChange}
                className="w-full p-2 rounded bg-white/20 text-white placeholder-white/60 focus:outline-none focus:ring-2 focus:ring-indigo-400"
                placeholder="Enter KYC details"
              />
            </div>

            {/* Address Line 1 */}
            <div>
              <label className="block text-sm font-medium text-white/80 mb-1">
                Address Line 1
              </label>
              <input
                type="text"
                name="addressLine1"
                value={customer.addressLine1}
                onChange={handleChange}
                className="w-full p-2 rounded bg-white/20 text-white placeholder-white/60 focus:outline-none focus:ring-2 focus:ring-indigo-400"
                placeholder="Enter address"
              />
            </div>

            {/* State Dropdown */}
            <div>
              <label className="block text-sm font-medium text-white/80 mb-1">
                State
              </label>
              <select
                name="state"
                value={customer.state}
                onChange={handleStateChange}
                className="w-full p-2 rounded bg-white/20 text-white focus:outline-none focus:ring-2 focus:ring-indigo-400"
              >
                <option value="">Select State</option>
                {indiaStates.map((s) => (
                  <option key={s.name} value={s.name} className="text-black">
                    {s.name}
                  </option>
                ))}
              </select>
            </div>

            {/* City Dropdown */}
            <div>
              <label className="block text-sm font-medium text-white/80 mb-1">
                City
              </label>
              <select
                name="city"
                value={customer.city}
                onChange={handleChange}
                className="w-full p-2 rounded bg-white/20 text-white focus:outline-none focus:ring-2 focus:ring-indigo-400"
              >
                <option value="">Select City</option>
                {cities.map((c) => (
                  <option key={c} value={c} className="text-black">
                    {c}
                  </option>
                ))}
              </select>
            </div>

            {/* ZIP */}
            <div>
              <label className="block text-sm font-medium text-white/80 mb-1">
                ZIP Code
              </label>
              <input
                type="text"
                name="zip"
                value={customer.zip}
                onChange={handleChange}
                className="w-full p-2 rounded bg-white/20 text-white placeholder-white/60 focus:outline-none focus:ring-2 focus:ring-indigo-400"
                placeholder="Enter ZIP"
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
                value={customer.branchId}
                onChange={handleChange}
                className="w-full p-2 rounded bg-white/20 text-white placeholder-white/60 focus:outline-none focus:ring-2 focus:ring-indigo-400"
                placeholder="Enter Branch ID"
              />
            </div>
          </div>

          {/* Buttons */}
          <div className="flex justify-end space-x-4 mt-8">
            <button
              type="button"
              onClick={() => navigate("/customers")}
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
