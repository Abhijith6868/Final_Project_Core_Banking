// Path: src/pages/RepaymentReportPage.jsx
import React, { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate, useParams } from "react-router-dom";

export default function RepaymentReportPage() {
  const { loanId: paramLoanId } = useParams();
  const navigate = useNavigate();

  // State
  const [loanId, setLoanId] = useState(paramLoanId || "");
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [reportData, setReportData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [fetchingPdf, setFetchingPdf] = useState(false);

  const baseURL = "http://localhost:8080/api";

  // Load token debug once
  useEffect(() => {
    const token = localStorage.getItem("token");
    if (token) console.log("🟣 Token loaded from localStorage:", token.slice(0, 25) + "...");
    else console.warn("⚠️ No token found — user may need to log in again");
  }, []);

  // Auto-fetch if loanId passed via URL
  useEffect(() => {
    if (!paramLoanId) return;

    const token = localStorage.getItem("token");
    if (!token) {
      console.warn("⚠️ No token — redirecting to login");
      navigate("/login");
      return;
    }

    console.log(`🟣 Auto-fetching report for Loan ID: ${paramLoanId}`);
    fetchReport(paramLoanId, startDate, endDate, token);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [paramLoanId]);

  // Fetch report (common function)
  const fetchReport = async (loanIdValue, start, end, token) => {
    try {
      setLoading(true);
      console.log(`🟣 Fetching report → LoanID: ${loanIdValue}, Start: ${start || "-"}, End: ${end || "-"}`);

      const response = await axios.get(`${baseURL}/repayments/report`, {
        params: { loanId: loanIdValue, startDate: start || null, endDate: end || null },
        headers: { Authorization: `Bearer ${token}` },
      });

      const data = Array.isArray(response.data) ? response.data : [];
      console.log(`✅ Report fetched successfully (${data.length} records)`);
      setReportData(data);
    } catch (err) {
      console.error("❌ Fetch error:", err);
      if (err.response?.status === 401) {
        console.warn("⚠️ Session expired — redirecting to login");
        localStorage.removeItem("token");
        navigate("/login");
      } else if (err.response?.status === 403) {
        console.warn("🚫 Unauthorized — Access denied for this user");
        alert("You are not authorized to view this report.");
      } else {
        alert("Failed to load repayment report.");
      }
    } finally {
      setLoading(false);
    }
  };

  // Manual fetch triggered by user
  const handleViewReport = async () => {
    const token = localStorage.getItem("token");
    if (!token) {
      navigate("/login");
      return;
    }

    if (!loanId) {
      alert("Please enter a valid Loan ID.");
      return;
    }

    console.log(`🟣 Manual fetch triggered for Loan ID: ${loanId}`);
    fetchReport(loanId, startDate, endDate, token);
  };

  // PDF download
  const handleDownloadPDF = async () => {
    const token = localStorage.getItem("token");
    if (!token) {
      navigate("/login");
      return;
    }

    try {
      setFetchingPdf(true);
      console.log(`🟣 Downloading PDF for Loan ID: ${loanId || "ALL"}`);
      const response = await axios.get(`${baseURL}/repayments/report/pdf`, {
        params: { loanId: loanId || null, startDate: startDate || null, endDate: endDate || null },
        headers: { Authorization: `Bearer ${token}` },
        responseType: "blob",
      });

      const blob = new Blob([response.data], { type: "application/pdf" });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement("a");
      link.href = url;
      link.download = `repayment_report_${loanId || "all"}.pdf`;
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);

      console.log("✅ PDF downloaded successfully");
    } catch (error) {
      console.error("❌ PDF download error:", error);
      if (error.response?.status === 401) {
        localStorage.removeItem("token");
        navigate("/login");
      } else {
        alert("Failed to download PDF.");
      }
    } finally {
      setFetchingPdf(false);
    }
  };

  // Helpers for display formatting
  const fmtDate = (iso) =>
    iso ? new Date(iso).toLocaleDateString("en-IN", { day: "2-digit", month: "short", year: "numeric" }) : "—";
  const fmtCurrency = (n) =>
    n === null || n === undefined ? "—" : Number(n).toLocaleString("en-IN", { maximumFractionDigits: 2 });

  // Loading full-screen (same look as LoanViewPage)
  if (loading && reportData.length === 0) {
    return (
      <div className="flex items-center justify-center min-h-screen bg-gradient-to-br from-purple-900 via-purple-800 to-indigo-900 text-white">
        <p className="text-lg">Loading repayment report...</p>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-purple-900 via-purple-800 to-indigo-900 py-10 text-white">
      <div className="max-w-6xl mx-auto bg-gradient-to-r from-purple-700 via-purple-600 to-purple-700/80 p-8 rounded-2xl shadow-2xl border border-purple-600/30 backdrop-blur-md">
        {/* Back button + title */}
        <div className="flex items-center justify-between mb-6">
          <button
            onClick={() => navigate(-1)}
            className="px-5 py-2 bg-gradient-to-r from-purple-500 to-purple-700 text-white font-semibold rounded-lg shadow hover:from-purple-600 hover:to-purple-800 transition-all duration-300"
          >
            ← Back
          </button>

          <h1 className="text-2xl font-semibold text-center text-blue-100">
            🧾 Repayment Report
          </h1>

          <div className="w-24" /> {/* spacer to keep title centered */}
        </div>

        {/* Filters / Controls */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6 items-end">
          {/* Loan ID */}
          <div>
            <label className="block text-sm text-white/90 mb-1">Loan ID</label>
            <input
              type="number"
              value={loanId}
              onChange={(e) => setLoanId(e.target.value)}
              readOnly={!!paramLoanId}
              className={`w-full px-4 py-2 rounded-lg bg-white/6 placeholder-white/60 text-white outline-none focus:ring-2 focus:ring-purple-400 transition`}
              placeholder="Enter loan id"
            />
          </div>

          {/* Start date */}
          <div>
            <label className="block text-sm text-white/90 mb-1">Start Date</label>
            <input
              type="date"
              value={startDate}
              onChange={(e) => setStartDate(e.target.value)}
              className="w-full px-4 py-2 rounded-lg bg-white/6 text-white outline-none focus:ring-2 focus:ring-purple-400 transition"
            />
          </div>

          {/* End date */}
          <div>
            <label className="block text-sm text-white/90 mb-1">End Date</label>
            <input
              type="date"
              value={endDate}
              onChange={(e) => setEndDate(e.target.value)}
              className="w-full px-4 py-2 rounded-lg bg-white/6 text-white outline-none focus:ring-2 focus:ring-purple-400 transition"
            />
          </div>

          {/* Action buttons */}
          <div className="flex gap-3">
            <button
              onClick={handleViewReport}
              disabled={loading}
              className={`px-4 py-2 rounded-lg font-semibold shadow text-white transition duration-300 ${
                loading
                  ? "bg-white/10 cursor-not-allowed"
                  : "bg-gradient-to-r from-green-600 to-green-700 hover:from-green-700 hover:to-green-800"
              }`}
            >
              {loading ? "Loading..." : "View Report"}
            </button>

            <button
              onClick={handleDownloadPDF}
              disabled={fetchingPdf}
              className={`px-4 py-2 rounded-lg font-semibold shadow text-white transition duration-300 ${
                fetchingPdf
                  ? "bg-white/10 cursor-not-allowed"
                  : "bg-white/10 hover:bg-white/20"
              }`}
            >
              {fetchingPdf ? "Preparing PDF..." : "Download PDF"}
            </button>
          </div>
        </div>

        {/* Report summary */}
        <div className="mb-4 text-sm text-white/80">
          <span className="mr-4">Records: <strong>{reportData.length}</strong></span>
          {loanId && <span className="mr-4">Loan: <strong>{loanId}</strong></span>}
          {(startDate || endDate) && (
            <span>
              Period: <strong>{startDate ? fmtDate(startDate) : "—"}</strong> — <strong>{endDate ? fmtDate(endDate) : "—"}</strong>
            </span>
          )}
        </div>

        {/* Table container */}
        <div className="bg-white/5 rounded-xl p-4 border border-white/10 shadow-inner">
          <div className="overflow-x-auto rounded-lg">
            <table className="min-w-full divide-y divide-white/10">
              <thead className="bg-gradient-to-r from-purple-600/40 to-purple-700/30">
                <tr>
                  <th className="px-4 py-3 text-left text-sm font-medium text-white/90">#</th>
                  <th className="px-4 py-3 text-left text-sm font-medium text-white/90">Loan ID</th>
                  <th className="px-4 py-3 text-left text-sm font-medium text-white/90">Due Date</th>
                  <th className="px-4 py-3 text-left text-sm font-medium text-white/90">Payment Date</th>
                  <th className="px-4 py-3 text-right text-sm font-medium text-white/90">Amount Paid (₹)</th>
                  <th className="px-4 py-3 text-right text-sm font-medium text-white/90">Interest (₹)</th>
                  <th className="px-4 py-3 text-right text-sm font-medium text-white/90">Balance Remaining (₹)</th>
                  <th className="px-4 py-3 text-right text-sm font-medium text-white/90">Outstanding (₹)</th>
                </tr>
              </thead>

              <tbody className="divide-y divide-white/6">
                {reportData.length === 0 ? (
                  <tr>
                    <td colSpan={8} className="px-4 py-8 text-center text-white/70">
                      No records found.
                    </td>
                  </tr>
                ) : (
                  reportData.map((item, idx) => (
                    <tr key={idx} className={idx % 2 === 0 ? "bg-white/2" : ""}>
                      <td className="px-4 py-3 text-sm text-white/90">{idx + 1}</td>
                      <td className="px-4 py-3 text-sm text-white/90">{item.loanId ?? "—"}</td>
                      <td className="px-4 py-3 text-sm text-white/90">{fmtDate(item.dueDate)}</td>
                      <td className="px-4 py-3 text-sm text-white/90">{fmtDate(item.paymentDate)}</td>
                      <td className="px-4 py-3 text-sm text-right text-white/90">{fmtCurrency(item.amountPaid)}</td>
                      <td className="px-4 py-3 text-sm text-right text-white/90">{fmtCurrency(item.interest)}</td>
                      <td className="px-4 py-3 text-sm text-right text-white/90">{fmtCurrency(item.balanceRemaining)}</td>
                      <td className="px-4 py-3 text-sm text-right text-white/90">{fmtCurrency(item.outstanding)}</td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>

        {/* Footer actions */}
        <div className="flex justify-between items-center mt-6">
          <div className="text-sm text-white/80">Tip: Use the date filters to limit results before downloading the PDF.</div>

          <div className="flex gap-3">
            <button
              onClick={() => {
                setStartDate("");
                setEndDate("");
              }}
              className="px-4 py-2 rounded-lg bg-white/10 hover:bg-white/20 transition text-white"
            >
              Clear Dates
            </button>

            <button
              onClick={() => {
                setReportData([]);
                setLoanId(paramLoanId || "");
              }}
              className="px-4 py-2 rounded-lg bg-white/10 hover:bg-white/20 transition text-white"
            >
              Reset
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
