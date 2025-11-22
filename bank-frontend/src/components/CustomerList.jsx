// Path: src/components/CustomerList.jsx
import React, { useEffect, useState } from "react";
import api from "../api/api"; // ✅ use your axios instance with token interceptor
import {
  Button,
  TextField,
  Typography,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Box,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
} from "@mui/material";

const API_URL = "/api/customers"; // ✅ relative path since baseURL is already set

console.log("🔍 Fetching customers...");
console.log("Token being sent:", localStorage.getItem("token"));

export default function CustomerList() {
  const [customers, setCustomers] = useState([]);
  const [filteredCustomers, setFilteredCustomers] = useState([]);
  const [searchText, setSearchText] = useState("");
  const [open, setOpen] = useState(false);
  const [form, setForm] = useState({
    firstName: "",
    lastName: "",
    email: "",
    phone: "",
    addressLine1: "",
    city: "",
    state: "",
    zip: "",
    kycDetails: "",
  });

  useEffect(() => {
    fetchCustomers();
  }, []);

  // ✅ Token automatically sent by interceptor in api.js
  const fetchCustomers = async () => {
    try {
      const res = await api.get(API_URL);
      console.log("Response status:", res.status);
      console.log("Response data:", res.data);

      const rows = res.data.map((c) => ({
        id: c.customerId,
        customerId: c.customerId,
        name: `${c.firstName || ""} ${c.lastName || ""}`.trim(),
        email: c.email,
        phone: c.phone,
        dob: c.dob || "N/A",
        addressLine1: c.addressLine1 || "N/A",
        city: c.city || "N/A",
        state: c.state || "N/A",
        zip: c.zip || "N/A",
        kycDetails: c.kycDetails || "N/A",
        createdAt: c.createdAt
          ? new Date(c.createdAt).toLocaleString()
          : "N/A",
        status: c.status || "N/A",
        branch: c.branchName || c.branch?.name || "N/A",
      }));
      setCustomers(rows);
      setFilteredCustomers(rows);
    } catch (err) {
      console.error("Error fetching customers:", err);
    }
  };

  const handleSearch = (event) => {
    const value = event.target.value.toLowerCase();
    setSearchText(value);
    setFilteredCustomers(
      customers.filter(
        (c) =>
          c.name.toLowerCase().includes(value) ||
          c.email.toLowerCase().includes(value) ||
          (c.phone && c.phone.toLowerCase().includes(value))
      )
    );
  };

  // ✅ Token auto-attached here as well
  const handleCreate = async () => {
    try {
      await api.post(API_URL, form);
      setOpen(false);
      fetchCustomers();
    } catch (err) {
      console.error(err);
    }
  };

  // ✅ Token auto-attached for delete too
  const handleDeleteCustomer = async (id) => {
    if (window.confirm("Are you sure you want to delete this customer?")) {
      try {
        await api.delete(`${API_URL}/${id}`);
        fetchCustomers();
      } catch (err) {
        console.error("Error deleting customer:", err);
      }
    }
  };

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
          Customer Management
        </Typography>

        <Box
          sx={{
            display: "flex",
            justifyContent: "space-between",
            mb: 2,
          }}
        >
          <TextField
            label="Search by Name, Email, or Phone"
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
            onClick={() => setOpen(true)}
          >
            + Add Customer
          </Button>
        </Box>

        <TableContainer component={Paper} sx={{ borderRadius: 3, boxShadow: 3 }}>
          <Table>
            <TableHead sx={{ backgroundColor: "#f3e8ff" }}>
              <TableRow>
                <TableCell sx={{ fontWeight: "bold" }}>ID</TableCell>
                <TableCell sx={{ fontWeight: "bold" }}>Name</TableCell>
                <TableCell sx={{ fontWeight: "bold" }}>Email</TableCell>
                <TableCell sx={{ fontWeight: "bold" }}>Phone</TableCell>
                <TableCell sx={{ fontWeight: "bold" }}>City</TableCell>
                <TableCell sx={{ fontWeight: "bold" }}>State</TableCell>
                <TableCell sx={{ fontWeight: "bold" }}>Status</TableCell>
                <TableCell sx={{ fontWeight: "bold" }}>Branch</TableCell>
                <TableCell sx={{ fontWeight: "bold" }} align="center">
                  Actions
                </TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {filteredCustomers.map((c) => (
                <TableRow
                  key={c.customerId}
                  sx={{
                    "&:hover": { backgroundColor: "#f8f5ff" },
                    transition: "0.2s",
                  }}
                >
                  <TableCell>{c.customerId}</TableCell>
                  <TableCell>{c.name}</TableCell>
                  <TableCell>{c.email}</TableCell>
                  <TableCell>{c.phone}</TableCell>
                  <TableCell>{c.city}</TableCell>
                  <TableCell>{c.state}</TableCell>
                  <TableCell>
                    <span
                      style={{
                        color:
                          c.status === "ACTIVE"
                            ? "#4caf50"
                            : c.status === "INACTIVE"
                            ? "#f44336"
                            : "#9c27b0",
                        fontWeight: "bold",
                      }}
                    >
                      {c.status}
                    </span>
                  </TableCell>
                  <TableCell>{c.branch}</TableCell>
                  <TableCell align="center">
                    <Button
                      size="small"
                      sx={{ color: "#7f00ff", fontWeight: 600, mr: 1 }}
                      href={`/customers/update/${c.customerId}`}
                    >
                      Edit
                    </Button>
                    <Button
                      size="small"
                      sx={{ color: "#d32f2f", fontWeight: 600 }}
                      onClick={() => handleDeleteCustomer(c.customerId)}
                    >
                      Delete
                    </Button>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      </Paper>

      {/* Create Customer Dialog */}
      <Dialog open={open} onClose={() => setOpen(false)}>
        <DialogTitle sx={{ color: "#7f00ff", fontWeight: 700 }}>
          Add New Customer
        </DialogTitle>
        <DialogContent>
          <TextField
            margin="dense"
            label="First Name"
            fullWidth
            value={form.firstName}
            onChange={(e) => setForm({ ...form, firstName: e.target.value })}
          />
          <TextField
            margin="dense"
            label="Last Name"
            fullWidth
            value={form.lastName}
            onChange={(e) => setForm({ ...form, lastName: e.target.value })}
          />
          <TextField
            margin="dense"
            label="Email"
            fullWidth
            value={form.email}
            onChange={(e) => setForm({ ...form, email: e.target.value })}
          />
          <TextField
            margin="dense"
            label="Phone"
            fullWidth
            value={form.phone}
            onChange={(e) => setForm({ ...form, phone: e.target.value })}
          />
          <TextField
            margin="dense"
            label="Address"
            fullWidth
            value={form.addressLine1}
            onChange={(e) => setForm({ ...form, addressLine1: e.target.value })}
          />
          <TextField
            margin="dense"
            label="City"
            fullWidth
            value={form.city}
            onChange={(e) => setForm({ ...form, city: e.target.value })}
          />
          <TextField
            margin="dense"
            label="State"
            fullWidth
            value={form.state}
            onChange={(e) => setForm({ ...form, state: e.target.value })}
          />
          <TextField
            margin="dense"
            label="ZIP"
            fullWidth
            value={form.zip}
            onChange={(e) => setForm({ ...form, zip: e.target.value })}
          />
          <TextField
            margin="dense"
            label="KYC Details"
            fullWidth
            value={form.kycDetails}
            onChange={(e) =>
              setForm({ ...form, kycDetails: e.target.value })
            }
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpen(false)}>Cancel</Button>
          <Button
            variant="contained"
            sx={{
              backgroundColor: "#7f00ff",
              "&:hover": { backgroundColor: "#9b2eff" },
            }}
            onClick={handleCreate}
          >
            Add
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
