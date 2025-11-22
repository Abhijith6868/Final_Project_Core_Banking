// App.js
import React from "react";
import "./index.css";  // Tailwind CSS
import "./api/axiosConfig";

import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";

// Pages & Components
import Dashboard from "./components/Dashboard";
import LoginPage from "./pages/LoginPage";
import CustomersPage from "./pages/CustomersPage";
import CreateCustomerPage from "./pages/CreateCustomerPage";
import UpdateCustomerPage from "./pages/UpdateCustomerPage";
import CreateLoanPage from "./pages/CreateLoanPage";
import UpdateLoanPage from "./pages/UpdateLoanPage";
import CreateUserPage from "./pages/CreateUserPage";
import CollateralCreatePage from "./pages/CollateralCreatePage";
import LoanViewPage from "./pages/LoanViewPage";
import JobsPage from "./pages/JobsPage";
import LoanSearchPage from "./pages/LoanSearchPage";
import AccountPage from "./pages/AccountPage";
import RepaymentReportPage from "./pages/RepaymentReportPage";


function App() {
  return (
    <Router>
      <Routes>

        {/* 🔐 Authentication */}
        <Route path="/" element={<LoginPage />} />   {/* Default route = Login */}
        <Route path="/login" element={<LoginPage />} />

        {/* 🏠 Dashboard */}
        <Route path="/dashboard" element={<Dashboard />} />

        {/* 👤 Customers */}
        <Route path="/customers/list" element={<CustomersPage />} />
        <Route path="/customers/create" element={<CreateCustomerPage />} />
        <Route path="/customers/update/:id" element={<UpdateCustomerPage />} />

        {/* 💳 Loans */}
        <Route path="/loans/search" element={<LoanSearchPage />} />
        <Route path="/loans/create" element={<CreateLoanPage />} />
        <Route path="/loans/update/:id" element={<UpdateLoanPage />} />
        <Route path="/loans/:loanId" element={<LoanViewPage />} />

        {/* 🏦 Collaterals (mapped via loan) */}
        <Route
          path="/loans/:loanId/collateral/create"
          element={<CollateralCreatePage />}
        />
        <Route path="/jobs" element={<JobsPage />} />

        {/* 👨‍💼 Users */}
        <Route path="/user/create" element={<CreateUserPage />} />

        {/* 🚫 Catch-all */}
        <Route path="*" element={<Navigate to="/" />} />

        <Route path="/Account/Create" element={<AccountPage />} />

        {/* 🧾 Reports */}
        <Route path="/reports/repayments/:loanId" element={<RepaymentReportPage />} />
        <Route path="/reports/repayments" element={<RepaymentReportPage />} />
        
      </Routes>
    </Router>
  );
}

export default App;
