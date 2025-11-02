// CustomerList.jsx
// Path: src/components/CustomerList.jsx
import React, { useEffect, useState } from "react";
import axios from "axios";
import { DataGrid } from "@mui/x-data-grid";
import { Typography, TextField, Button } from "@mui/material";

export default function CustomerList() {
  const [customers, setCustomers] = useState([]);
  const [filteredCustomers, setFilteredCustomers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchText, setSearchText] = useState("");

  useEffect(() => {
    axios.get("http://localhost:8080/api/customers")
      .then((response) => {
        const rows = response.data.map((c) => ({
          id: c.customerId,
          customerId: c.customerId,
          name: `${c.firstName || ""} ${c.lastName || ""}`.trim(),
          email: c.email,
          phone: c.phone,
          dob: c.dob || "N/A",
          // 👇 Split address into separate fields
          addressLine1: c.addressLine1 || "N/A",
          city: c.city || "N/A",
          state: c.state || "N/A",
          zip: c.zip || "N/A",
          kycDetails: c.kycDetails || "N/A",
          createdAt: c.createdAt ? new Date(c.createdAt).toLocaleString() : "N/A",
          status: c.status || "N/A",
          branch: c.branchName || c.branch?.name || "N/A",
        }));

        setCustomers(rows);
        setFilteredCustomers(rows);
        setLoading(false);
      })
      .catch((error) => {
        console.error("Error fetching customers:", error);
        setLoading(false);
      });
  }, []);

  const handleSearch = (event) => {
    const value = event.target.value.toLowerCase();
    setSearchText(value);
    setFilteredCustomers(
      customers.filter(
        (c) =>
          c.name.toLowerCase().includes(value) ||
          c.email.toLowerCase().includes(value)
      )
    );
  };

  const handleDeleteCustomer = (id) => {
    if (window.confirm("Are you sure you want to delete this customer?")) {
      axios.delete(`http://localhost:8080/api/customers/${id}`)
        .then(() => {
          const updatedCustomers = customers.filter(c => c.id !== id);
          setCustomers(updatedCustomers);
          setFilteredCustomers(updatedCustomers);
        })
        .catch(err => console.error("Error deleting customer:", err));
    }
  };

  if (loading)
    return <Typography className="text-white text-lg">Loading customers...</Typography>;

  const columns = [
    { field: "customerId", headerName: "Customer ID", width: 120 },
    { field: "name", headerName: "Name", width: 200 },
    { field: "email", headerName: "Email", width: 250 },
    { field: "phone", headerName: "Phone", width: 150 },
    { field: "dob", headerName: "Date of Birth", width: 150 },

    // 👇 Separated Address Columns
    { field: "addressLine1", headerName: "Address Line 1", width: 220 },
    { field: "city", headerName: "City", width: 150 },
    { field: "state", headerName: "State", width: 150 },
    { field: "zip", headerName: "ZIP", width: 120 },

    { field: "kycDetails", headerName: "KYC Details", width: 200 },
    { field: "createdAt", headerName: "Created At", width: 180 },
    { field: "status", headerName: "Status", width: 120 },
    { field: "branch", headerName: "Branch", width: 150 },
    {
      field: "actions",
      headerName: "Actions",
      width: 180,
      sortable: false,
      renderCell: (params) => (
        <>
          <Button
            variant="contained"
            color="primary"
            size="small"
            className="mr-2"
            href={`/customers/update/${params.id}`}
          >
            Edit
          </Button>
          <Button
            variant="contained"
            color="error"
            size="small"
            onClick={() => handleDeleteCustomer(params.id)}
          >
            Delete
          </Button>
        </>
      ),
    },
  ];

  return (
    <div className="min-h-screen bg-gradient-to-br from-purple-900 via-purple-800 to-indigo-900 p-10">
      <div className="max-w-7xl mx-auto">
        <Typography variant="h4" className="text-white font-semibold mb-6">
          Customer List
        </Typography>

        {/* Search box */}
        <div className="mb-6">
          <TextField
            label="Search by Name or Email"
            variant="outlined"
            fullWidth
            value={searchText}
            onChange={handleSearch}
            className="bg-white/10 rounded-md"
            InputProps={{
              className: "text-white",
            }}
            InputLabelProps={{
              className: "text-white/80",
            }}
          />
        </div>

        {/* DataGrid card */}
        <div className="bg-white/10 rounded-2xl p-6 shadow-2xl">
          <DataGrid
            rows={filteredCustomers}
            columns={columns}
            pageSize={10}
            rowsPerPageOptions={[10, 25, 50]}
            autoHeight
            disableSelectionOnClick
            sx={{
              "& .MuiDataGrid-root": {
                backgroundColor: "rgba(255, 255, 255, 0.05)",
                borderRadius: "12px",
              },
              "& .MuiDataGrid-cell": {
                color: "#030a12ff",
              },
              "& .MuiDataGrid-columnHeaders": {
                backgroundColor: "rgba(255, 255, 255, 0.1)",
                color: "#070707ff",
                fontWeight: 600,
              },
              "& .MuiDataGrid-footerContainer": {
                backgroundColor: "rgba(255, 255, 255, 0.1)",
                color: "#030303ff",
              },
              "& .MuiDataGrid-row:hover": {
                backgroundColor: "rgba(255,255,255,0.1)",
              },
              border: 0,
              boxShadow: "none",
            }}
          />
        </div>
      </div>
    </div>
  );
}
