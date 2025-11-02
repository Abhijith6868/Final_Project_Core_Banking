// App.js
import React from "react";

// import './styles/global.css';
import './index.css';  // Tailwind CSS
// ✅ Add react-router-dom imports at the top
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import UpdateCustomerPage from "./pages/UpdateCustomerPage";
// Import your pages
import CustomersPage from "./pages/CustomersPage";
// import LoansPage from "./pages/LoansPage"; // you can add more pages later
import CreateLoanPage from "./pages/CreateLoanPage";
import CreateCustomerPage from "./pages/CreateCustomerPage";
import Dashboard from "./components/Dashboard";
import UpdateLoanPage from "./pages/UpdateLoanPage";
import CreateUserPage from "./pages/CreateUserPage";

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<CustomersPage />} />   {/* Default route */}
        <Route path="/customers" element={<CustomersPage />} />
        <Route path="/customers/create" element={<CreateCustomerPage />} />
        <Route path="/customers/update/:id" element={<UpdateCustomerPage />} />
        <Route path="/loans/create" element={<CreateLoanPage />} />
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/loans/update/:id" element={<UpdateLoanPage/>} />
        <Route path="/user/create" element={<CreateUserPage/>} />
        {/* Add more routes here */}
      </Routes>
    </Router>
  );
}

export default App;
