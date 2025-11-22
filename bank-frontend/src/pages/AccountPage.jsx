import React, { useEffect, useState } from "react";
import axios from "axios";
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
import { useNavigate } from "react-router-dom";

const API_URL = "http://localhost:8080/accounts";

export default function AccountPage() {
  const [accounts, setAccounts] = useState([]);
  const [open, setOpen] = useState(false);
  const [form, setForm] = useState({
    accountType: "",
    balance: "",
    status: "ACTIVE",
    customerId: "",
    branchId: "",
  });
  const [amountDialog, setAmountDialog] = useState(false);
  const [amount, setAmount] = useState("");
  const [selectedAccount, setSelectedAccount] = useState(null);
  const [actionType, setActionType] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    fetchAccounts();
  }, []);

  // ---------------------------
  // FETCH ACCOUNTS (FIXED)
  // ---------------------------
  const fetchAccounts = async () => {
    const token = localStorage.getItem("token");
    try {
      const res = await axios.get(API_URL, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      setAccounts(res.data);
    } catch (err) {
      console.error("Failed to fetch accounts:", err);
    }
  };

  // ---------------------------
  // CREATE ACCOUNT
  // ---------------------------
  const handleCreate = async () => {
    const token = localStorage.getItem("token");

    try {
      await axios.post(
        API_URL,
        {
          accountType: form.accountType,
          balance: parseFloat(form.balance),
          status: form.status,
          customer: { customerId: parseInt(form.customerId) },
          branch: { branchId: parseInt(form.branchId) },
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      setOpen(false);
      fetchAccounts();
    } catch (err) {
      console.error("Error creating account:", err);
    }
  };

  // ---------------------------
  // DEPOSIT/WITHDRAW
  // ---------------------------
  const handleTransaction = async () => {
    const token = localStorage.getItem("token");

    try {
      const url = `${API_URL}/${selectedAccount.accountId}/${actionType}`;

      await axios.post(
        url,
        { amount: parseFloat(amount) },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      setAmount("");
      setAmountDialog(false);
      fetchAccounts();
    } catch (err) {
      console.error("Transaction error:", err);
    }
  };

  // ---------------------------
  // CLOSE / DEACTIVATE / DELETE
  // ---------------------------
  const handleAction = async (id, action) => {
    const token = localStorage.getItem("token");

    try {
      if (action === "close") {
        await axios.put(`${API_URL}/${id}/close`, null, {
          headers: { Authorization: `Bearer ${token}` },
        });
      } else if (action === "delete") {
        await axios.delete(`${API_URL}/${id}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
      }

      fetchAccounts();
    } catch (err) {
      console.error(err);
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
        <Box
          sx={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
            mb: 3,
          }}
        >
          <Typography
            variant="h4"
            sx={{
              fontWeight: 700,
              color: "#7f00ff",
              letterSpacing: "1px",
            }}
          >
            Account Management
          </Typography>

          <Box>
            <Button
              variant="outlined"
              sx={{
                color: "#7f00ff",
                borderColor: "#7f00ff",
                "&:hover": {
                  backgroundColor: "#f3e8ff",
                  borderColor: "#9b2eff",
                },
                borderRadius: 2,
                fontWeight: 600,
                mr: 2,
              }}
              onClick={() => navigate("/dashboard")}
            >
              🏠 Home
            </Button>

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
              + Create Account
            </Button>
          </Box>
        </Box>

        {/* TABLE */}
        <TableContainer component={Paper} sx={{ borderRadius: 3, boxShadow: 3 }}>
          <Table>
            <TableHead sx={{ backgroundColor: "#f3e8ff" }}>
              <TableRow>
                <TableCell sx={{ fontWeight: "bold" }}>ID</TableCell>
                <TableCell sx={{ fontWeight: "bold" }}>Type</TableCell>
                <TableCell sx={{ fontWeight: "bold" }}>Balance</TableCell>
                <TableCell sx={{ fontWeight: "bold" }}>Status</TableCell>
                <TableCell sx={{ fontWeight: "bold" }}>Customer</TableCell>
                <TableCell sx={{ fontWeight: "bold" }}>Branch</TableCell>
                <TableCell sx={{ fontWeight: "bold" }} align="center">
                  Actions
                </TableCell>
              </TableRow>
            </TableHead>

            <TableBody>
              {accounts.map((acc) => (
                <TableRow key={acc.accountId}>
                  <TableCell>{acc.accountId}</TableCell>
                  <TableCell>{acc.accountType}</TableCell>
                  <TableCell>₹{acc.balance}</TableCell>
                  <TableCell>
                    <span
                      style={{
                        color:
                          acc.status === "ACTIVE"
                            ? "#4caf50"
                            : acc.status === "CLOSED"
                            ? "#f44336"
                            : "#9c27b0",
                        fontWeight: "bold",
                      }}
                    >
                      {acc.status}
                    </span>
                  </TableCell>

                  {/* FIXED customer & branch display */}
                  <TableCell>{acc.customerName}</TableCell>
                  <TableCell>{acc.branchName}</TableCell>
                  
                  <TableCell align="center">
                    <Button
                      size="small"
                      sx={{ color: "#7f00ff", fontWeight: 600 }}
                      onClick={() => {
                        setSelectedAccount(acc);
                        setActionType("deposit");
                        setAmountDialog(true);
                      }}
                    >
                      Deposit
                    </Button>

                    <Button
                      size="small"
                      sx={{ color: "#9c27b0", fontWeight: 600 }}
                      onClick={() => {
                        setSelectedAccount(acc);
                        setActionType("withdraw");
                        setAmountDialog(true);
                      }}
                    >
                      Withdraw
                    </Button>

                    <Button
                      size="small"
                      sx={{ color: "#e91e63", fontWeight: 600 }}
                      onClick={() => handleAction(acc.accountId, "close")}
                    >
                      Close
                    </Button>

                    <Button
                      size="small"
                      sx={{ color: "#d32f2f", fontWeight: 600 }}
                      onClick={() => handleAction(acc.accountId, "delete")}
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

      {/* Create Account Dialog */}
      <Dialog open={open} onClose={() => setOpen(false)}>
        <DialogTitle sx={{ color: "#7f00ff", fontWeight: 700 }}>
          Create New Account
        </DialogTitle>

        <DialogContent>
          <TextField
            margin="dense"
            label="Account Type"
            fullWidth
            value={form.accountType}
            onChange={(e) =>
              setForm({ ...form, accountType: e.target.value })
            }
          />

          <TextField
            margin="dense"
            label="Balance"
            type="number"
            fullWidth
            value={form.balance}
            onChange={(e) => setForm({ ...form, balance: e.target.value })}
          />

          <TextField
            margin="dense"
            label="Customer ID"
            type="number"
            fullWidth
            value={form.customerId}
            onChange={(e) => setForm({ ...form, customerId: e.target.value })}
          />

          <TextField
            margin="dense"
            label="Branch ID"
            type="number"
            fullWidth
            value={form.branchId}
            onChange={(e) => setForm({ ...form, branchId: e.target.value })}
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
            Create
          </Button>
        </DialogActions>
      </Dialog>

      {/* Deposit / Withdraw Dialog */}
      <Dialog open={amountDialog} onClose={() => setAmountDialog(false)}>
        <DialogTitle sx={{ color: "#7f00ff", fontWeight: 700 }}>
          {actionType === "deposit" ? "Deposit Amount" : "Withdraw Amount"}
        </DialogTitle>

        <DialogContent>
          <TextField
            margin="dense"
            label="Amount"
            type="number"
            fullWidth
            value={amount}
            onChange={(e) => setAmount(e.target.value)}
          />
        </DialogContent>

        <DialogActions>
          <Button onClick={() => setAmountDialog(false)}>Cancel</Button>
          <Button
            variant="contained"
            sx={{
              backgroundColor: "#7f00ff",
              "&:hover": { backgroundColor: "#9b2eff" },
            }}
            onClick={handleTransaction}
          >
            Confirm
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
