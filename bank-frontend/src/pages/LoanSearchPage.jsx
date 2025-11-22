import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import {
  Box,
  Paper,
  Typography,
  TextField,
  Button,
} from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";

export default function LoanSearchPage() {
  const [loans, setLoans] = useState([]);
  const [filteredLoans, setFilteredLoans] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchText, setSearchText] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem("token");

    if (!token) {
      console.error("⚠️ No token found. Please log in first.");
      setLoading(false);
      return;
    }

    axios
      .get("http://localhost:8080/api/loans", {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      .then((response) => {
        const loanList = Array.isArray(response.data)
          ? response.data
          : response.data.content || [];

        const rows = loanList.map((l) => ({
          id: l.loanId,
          loanId: l.loanId,
          loanNo: l.loanNo || "N/A",
          customerName: l.customerName || "N/A",
          loanType: l.loanType || "N/A",
          principal: l.principal || 0,
          interestRate: l.interestRate ? `${l.interestRate}%` : "N/A",
          tenureMonths: l.tenureMonths || "N/A",
          startDate: l.startDate
            ? new Date(l.startDate).toLocaleDateString()
            : "N/A",
          maturityDate: l.maturityDate
            ? new Date(l.maturityDate).toLocaleDateString()
            : "N/A",
          status: l.status || "N/A",
        }));

        setLoans(rows);
        setFilteredLoans(rows);
        setLoading(false);
      })
      .catch((error) => {
        console.error("❌ Error fetching loans:", error);
        if (error.response?.status === 401) {
          alert("Unauthorized! Please log in again.");
        }
        setLoading(false);
      });
  }, []);

      const handleSearch = (event) => {
      const value = event.target.value;
      setSearchText(value);

      const lowerValue = value.toLowerCase();

      setFilteredLoans(
        loans.filter(
          (l) =>
            (l.customerName && l.customerName.toLowerCase().includes(lowerValue)) ||
            (l.loanId && l.loanId.toString().toLowerCase().includes(lowerValue)) ||
            (l.loanNo && l.loanNo.toLowerCase().includes(lowerValue)) || // ✅ Added this line
            (l.status && l.status.toLowerCase().includes(lowerValue))
        )
      );
    };

  const handleDeleteLoan = (id) => {
    const token = localStorage.getItem("token");
    if (!token) {
      alert("Unauthorized! Please log in first.");
      return;
    }

    if (window.confirm("Are you sure you want to delete this loan?")) {
      axios
        .delete(`http://localhost:8080/api/loans/${id}`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        })
        .then(() => {
          const updatedLoans = loans.filter((l) => l.id !== id);
          setLoans(updatedLoans);
          setFilteredLoans(updatedLoans);
        })
        .catch((err) => {
          console.error("Error deleting loan:", err);
          if (err.response?.status === 403)
            alert("Access denied. Only Admins can delete loans.");
        });
    }
  };

  const columns = [
    { field: "loanId", headerName: "Loan ID", width: 120 },
    { field: "loanNo", headerName: "Loan No", width: 150 },
    { field: "customerName", headerName: "Customer Name", width: 200 },
    { field: "loanType", headerName: "Loan Type", width: 150 },
    { field: "principal", headerName: "Principal", width: 130 },
    { field: "interestRate", headerName: "Interest Rate", width: 150 },
    { field: "tenureMonths", headerName: "Tenure (Months)", width: 150 },
    { field: "startDate", headerName: "Start Date", width: 150 },
    { field: "maturityDate", headerName: "Maturity Date", width: 160 },
    { field: "status", headerName: "Status", width: 120 },
    {
      field: "actions",
      headerName: "Actions",
      width: 200,
      sortable: false,
      renderCell: (params) => (
        <>
          <Button
            size="small"
            sx={{ color: "#7f00ff", fontWeight: 600, mr: 1 }}
            onClick={() => navigate(`/loans/${params.id}`)} // ✅ match route exactly
          >
            Details
          </Button>
          <Button
            size="small"
            sx={{ color: "#d32f2f", fontWeight: 600 }}
            onClick={() => handleDeleteLoan(params.id)}
          >
            Delete
          </Button>
        </>
      ),
    },
  ];

  return (
    <Box
      sx={{
        p: 4,
        background: "linear-gradient(to right, #7f00ff, #e100ff)",
        minHeight: "100vh",
      }}
    >
      <Paper
        elevation={6}
        sx={{
          p: 4,
          borderRadius: 4,
          backgroundColor: "rgba(255,255,255,0.95)",
        }}
      >
        {/* ✅ Back Button */}
        <Button
          variant="contained"
          onClick={() => window.history.back()}
          sx={{
            backgroundColor: "#7f00ff",
            "&:hover": { backgroundColor: "#9b2eff" },
            borderRadius: 2,
            fontWeight: 600,
            mb: 2,
          }}
        >
          ← Back
        </Button>

      <Typography
          variant="h4"
          sx={{
            mb: 3,
            fontWeight: 700,
            textAlign: "center",
            color: "#7f00ff",
            letterSpacing: "1px",
          }}
        >
          Loan Management
      </Typography>


        {/* Search and Add button row */}
        <Box
          sx={{
            display: "flex",
            justifyContent: "space-between",
            mb: 2,
          }}
        >
          <TextField
            label="Search by Loan ID, Customer, or Status"
            variant="outlined"
            value={searchText}
            onChange={handleSearch}
            sx={{
              flex: 1,
              backgroundColor: "#f3e8ff",
              borderRadius: 2,
              mr: 2,
            }}
          />
          <Button
            variant="contained"
            sx={{
              backgroundColor: "#7f00ff",
              "&:hover": { backgroundColor: "#9b2eff" },
              borderRadius: 2,
              fontWeight: 600,
            }}
            href="/loans/create"
          >
            + Add Loan
          </Button>
        </Box>

        {/* DataGrid */}
        <div style={{ width: "100%" }}>
          <DataGrid
            rows={filteredLoans}
            columns={columns}
            autoHeight
            pageSize={10}
            rowsPerPageOptions={[10, 25, 50]}
            disableSelectionOnClick
            sx={{
              backgroundColor: "white",
              borderRadius: 3,
              boxShadow: 3,
              "& .MuiDataGrid-columnHeaders": {
                backgroundColor: "#f3e8ff",
                color: "#4a148c",
                fontWeight: 600,
              },
              "& .MuiDataGrid-row:hover": {
                backgroundColor: "#f8f5ff",
              },
              "& .MuiDataGrid-footerContainer": {
                backgroundColor: "#f3e8ff",
              },
            }}
          />
        </div>
      </Paper>
    </Box>
  );
}
