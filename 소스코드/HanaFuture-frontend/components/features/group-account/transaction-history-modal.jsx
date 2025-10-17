"use client";

import { useState, useEffect } from "react";
import {
  X,
  Calendar,
  ArrowUpRight,
  ArrowDownLeft,
  Filter,
  Search,
  ChevronLeft,
  ChevronRight,
} from "lucide-react";
import { useAuth } from "../../../contexts/AuthContext";

export function TransactionHistoryModal({ isOpen, onClose, groupAccount }) {
  const { user } = useAuth();
  const [transactions, setTransactions] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [filter, setFilter] = useState("ALL"); // ALL, DEPOSIT, WITHDRAWAL
  const [searchTerm, setSearchTerm] = useState("");

  useEffect(() => {
    if (isOpen && groupAccount) {
      fetchTransactions();
    }
  }, [isOpen, groupAccount, currentPage, filter]);

  const fetchTransactions = async () => {
    try {
      setIsLoading(true);

      const response = await fetch(
        `http://localhost:8080/api/group-accounts/${groupAccount.id}/transactions?page=${currentPage}&size=10`,
        {
          headers: {
            "Content-Type": "application/json",
            ...(user?.token && { Authorization: `Bearer ${user.token}` }),
          },
        }
      );

      if (response.ok) {
        const result = await response.json();
        setTransactions(result.data.content || []);
        setTotalPages(result.data.totalPages || 0);
      } else {
        console.error("거래내역 조회 실패");
        setTransactions([]);
      }
    } catch (error) {
      console.error("거래내역 조회 중 오류:", error);
      setTransactions([]);
    } finally {
      setIsLoading(false);
    }
  };

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString("ko-KR", {
      year: "numeric",
      month: "short",
      day: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  const formatAmount = (amount) => {
    return new Intl.NumberFormat("ko-KR").format(amount);
  };

  const getTransactionIcon = (type) => {
    if (
      type === "DEPOSIT" ||
      type === "TRANSFER_IN" ||
      type === "AUTO_TRANSFER"
    ) {
      return <ArrowDownLeft className="w-5 h-5 text-emerald-600" />;
    }
    return <ArrowUpRight className="w-5 h-5 text-red-600" />;
  };

  const getTransactionColor = (type) => {
    if (
      type === "DEPOSIT" ||
      type === "TRANSFER_IN" ||
      type === "AUTO_TRANSFER"
    ) {
      return "text-emerald-600";
    }
    return "text-red-600";
  };

  const getTransactionSign = (type) => {
    if (
      type === "DEPOSIT" ||
      type === "TRANSFER_IN" ||
      type === "AUTO_TRANSFER"
    ) {
      return "+";
    }
    return "-";
  };

  const filteredTransactions = transactions.filter((transaction) => {
    const matchesFilter =
      filter === "ALL" ||
      (filter === "DEPOSIT" &&
        (transaction.transactionType === "DEPOSIT" ||
          transaction.transactionType === "TRANSFER_IN" ||
          transaction.transactionType === "AUTO_TRANSFER")) ||
      (filter === "WITHDRAWAL" &&
        (transaction.transactionType === "WITHDRAWAL" ||
          transaction.transactionType === "TRANSFER_OUT"));

    const matchesSearch =
      searchTerm === "" ||
      transaction.description
        ?.toLowerCase()
        .includes(searchTerm.toLowerCase()) ||
      transaction.sourceBankName
        ?.toLowerCase()
        .includes(searchTerm.toLowerCase()) ||
      transaction.targetBankName
        ?.toLowerCase()
        .includes(searchTerm.toLowerCase());

    return matchesFilter && matchesSearch;
  });

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-4xl max-h-[90vh] overflow-hidden">
        {/* 헤더 */}
        <div className="flex items-center justify-between p-6 border-b border-gray-200">
          <div>
            <h2 className="text-xl font-bold text-gray-900">거래내역</h2>
            <p className="text-gray-600 mt-1">
              {groupAccount?.accountName || groupAccount?.name} •{" "}
              {groupAccount?.accountNumber || groupAccount?.groupAccountNumber}
            </p>
          </div>
          <button
            onClick={onClose}
            className="p-2 hover:bg-gray-100 rounded-lg transition-colors"
          >
            <X className="w-5 h-5 text-gray-500" />
          </button>
        </div>

        {/* 필터 및 검색 */}
        <div className="p-6 border-b border-gray-200 bg-gray-50">
          <div className="flex flex-col sm:flex-row gap-4">
            {/* 필터 버튼들 */}
            <div className="flex gap-2">
              {[
                { key: "ALL", label: "전체" },
                { key: "DEPOSIT", label: "입금" },
                { key: "WITHDRAWAL", label: "출금" },
              ].map((filterOption) => (
                <button
                  key={filterOption.key}
                  onClick={() => setFilter(filterOption.key)}
                  className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors ${
                    filter === filterOption.key
                      ? "bg-emerald-500 text-white"
                      : "bg-white text-gray-600 hover:bg-gray-100"
                  }`}
                >
                  {filterOption.label}
                </button>
              ))}
            </div>

            {/* 검색 */}
            <div className="flex-1 relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-400" />
              <input
                type="text"
                placeholder="거래 내용, 은행명으로 검색..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-emerald-500 focus:border-emerald-500"
              />
            </div>
          </div>
        </div>

        {/* 거래내역 목록 */}
        <div className="flex-1 overflow-y-auto" style={{ maxHeight: "400px" }}>
          {isLoading ? (
            <div className="flex items-center justify-center py-12">
              <div className="flex items-center space-x-2">
                <div className="w-4 h-4 bg-emerald-500 rounded-full animate-bounce"></div>
                <div
                  className="w-4 h-4 bg-emerald-500 rounded-full animate-bounce"
                  style={{ animationDelay: "0.1s" }}
                ></div>
                <div
                  className="w-4 h-4 bg-emerald-500 rounded-full animate-bounce"
                  style={{ animationDelay: "0.2s" }}
                ></div>
              </div>
            </div>
          ) : filteredTransactions.length === 0 ? (
            <div className="text-center py-12">
              <Calendar className="w-12 h-12 text-gray-300 mx-auto mb-4" />
              <h3 className="text-lg font-medium text-gray-900 mb-2">
                거래내역이 없습니다
              </h3>
              <p className="text-gray-600">
                {filter !== "ALL"
                  ? "해당 조건의 거래내역이 없습니다."
                  : "첫 거래를 시작해보세요!"}
              </p>
            </div>
          ) : (
            <div className="divide-y divide-gray-200">
              {filteredTransactions.map((transaction) => (
                <div
                  key={transaction.id}
                  className="p-6 hover:bg-gray-50 transition-colors"
                >
                  <div className="flex items-center justify-between">
                    <div className="flex items-center gap-4">
                      <div className="w-10 h-10 bg-gray-100 rounded-full flex items-center justify-center">
                        {getTransactionIcon(transaction.transactionType)}
                      </div>
                      <div>
                        <p className="font-medium text-gray-900">
                          {transaction.description}
                        </p>
                        <div className="flex items-center gap-2 text-sm text-gray-600 mt-1">
                          <span>{formatDate(transaction.transactionDate)}</span>
                          <span>•</span>
                          <span>
                            {transaction.transactionType === "DEPOSIT" ||
                            transaction.transactionType === "TRANSFER_IN" ||
                            transaction.transactionType === "AUTO_TRANSFER"
                              ? `${
                                  transaction.sourceBankName
                                } ${transaction.sourceAccountNumber?.slice(-4)}`
                              : `${
                                  transaction.targetBankName
                                } ${transaction.targetAccountNumber?.slice(
                                  -4
                                )}`}
                          </span>
                        </div>
                      </div>
                    </div>
                    <div className="text-right">
                      <p
                        className={`text-lg font-bold ${getTransactionColor(
                          transaction.transactionType
                        )}`}
                      >
                        {getTransactionSign(transaction.transactionType)}
                        {formatAmount(transaction.amount)}원
                      </p>
                      <p className="text-sm text-gray-600 mt-1">
                        잔액 {formatAmount(transaction.balanceAfter)}원
                      </p>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* 페이지네이션 */}
        {totalPages > 1 && (
          <div className="p-6 border-t border-gray-200 bg-gray-50">
            <div className="flex items-center justify-between">
              <p className="text-sm text-gray-600">
                페이지 {currentPage + 1} / {totalPages}
              </p>
              <div className="flex gap-2">
                <button
                  onClick={() =>
                    setCurrentPage((prev) => Math.max(0, prev - 1))
                  }
                  disabled={currentPage === 0}
                  className="p-2 border border-gray-300 rounded-lg hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                >
                  <ChevronLeft className="w-4 h-4" />
                </button>
                <button
                  onClick={() =>
                    setCurrentPage((prev) => Math.min(totalPages - 1, prev + 1))
                  }
                  disabled={currentPage >= totalPages - 1}
                  className="p-2 border border-gray-300 rounded-lg hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                >
                  <ChevronRight className="w-4 h-4" />
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

