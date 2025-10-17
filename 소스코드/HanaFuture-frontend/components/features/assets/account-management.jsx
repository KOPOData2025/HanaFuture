"use client";

import { useState, useEffect } from "react";
import { accountAPI } from "../../../lib/api";

export function AccountManagement() {
  const [accountSummary, setAccountSummary] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    fetchAccountSummary();
  }, []);

  const fetchAccountSummary = async () => {
    try {
      setLoading(true);
      const response = await accountAPI.getAccountSummary();
      if (response.success) {
        setAccountSummary(response.data);
      } else {
        setError(response.message || "계좌 정보를 불러오는데 실패했습니다.");
      }
    } catch (err) {
      setError(err.message || "계좌 정보를 불러오는데 실패했습니다.");
    } finally {
      setLoading(false);
    }
  };

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat("ko-KR", {
      style: "currency",
      currency: "KRW",
    }).format(amount);
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString("ko-KR", {
      year: "numeric",
      month: "long",
      day: "numeric",
    });
  };

  const getTransactionColor = (type) => {
    const withdrawalTypes = [
      "WITHDRAWAL",
      "TRANSFER_OUT",
      "AUTO_TRANSFER",
      "FEE",
      "CARD_PAYMENT",
      "ONLINE_PAYMENT",
      "ATM_WITHDRAWAL",
    ];
    const depositTypes = [
      "DEPOSIT",
      "TRANSFER_IN",
      "SALARY",
      "GOVERNMENT_BENEFIT",
      "INTEREST",
      "REFUND",
    ];

    if (withdrawalTypes.includes(type)) {
      return "text-red-600";
    } else if (depositTypes.includes(type)) {
      return "text-green-600";
    } else {
      return "text-blue-600";
    }
  };

  const getTransactionSign = (type) => {
    const withdrawalTypes = [
      "WITHDRAWAL",
      "TRANSFER_OUT",
      "AUTO_TRANSFER",
      "FEE",
      "CARD_PAYMENT",
      "ONLINE_PAYMENT",
      "ATM_WITHDRAWAL",
    ];
    return withdrawalTypes.includes(type) ? "-" : "+";
  };

  if (loading) {
    return (
      <div className="space-y-6">
        <div className="flex items-center space-x-4">
          <img
            src="/hanacharacter/hanacharacter16.png"
            alt="계좌 관리 하나 캐릭터"
            className="w-28 h-28 object-contain"
          />
          <div>
            <h2 className="font-heading font-bold text-2xl text-foreground mb-2">
              계좌 관리
            </h2>
            <p className="text-muted-foreground">
              가족의 모든 계좌를 한 곳에서 관리하세요
            </p>
          </div>
        </div>
        <div className="flex items-center justify-center py-12">
          <div className="text-center">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary mx-auto mb-4"></div>
            <p className="text-muted-foreground">계좌 정보를 불러오는 중...</p>
          </div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="space-y-6">
        <div className="flex items-center space-x-4">
          <img
            src="/hanacharacter/hanacharacter16.png"
            alt="계좌 관리 하나 캐릭터"
            className="w-28 h-28 object-contain"
          />
          <div>
            <h2 className="font-heading font-bold text-2xl text-foreground mb-2">
              계좌 관리
            </h2>
            <p className="text-muted-foreground">
              가족의 모든 계좌를 한 곳에서 관리하세요
            </p>
          </div>
        </div>
        <div className="bg-red-50 border border-red-200 rounded-lg p-4">
          <p className="text-red-600">{error}</p>
          <button
            onClick={fetchAccountSummary}
            className="mt-2 bg-red-600 text-white px-4 py-2 rounded-lg hover:bg-red-700"
          >
            다시 시도
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div>
        <h2 className="font-heading font-bold text-2xl text-foreground mb-2">
          계좌 관리
        </h2>
        <p className="text-muted-foreground">
          가족의 모든 계좌를 한 곳에서 관리하세요
        </p>
      </div>

      {/* 총 자산 요약 */}
      <div className="bg-gradient-to-r from-primary/10 to-primary/5 rounded-xl border p-6">
        <h3 className="font-semibold mb-2 text-primary">총 자산</h3>
        <p className="text-3xl font-bold text-primary">
          {formatCurrency(accountSummary?.totalBalance || 0)}
        </p>
        <p className="text-sm text-muted-foreground mt-1">
          {accountSummary?.accountCount || 0}개 계좌
        </p>
      </div>

      {/* 계좌 목록 */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {accountSummary?.accounts?.map((account) => (
          <div
            key={account.id}
            className="p-6 bg-card rounded-xl border shadow-sm"
          >
            <h3 className="font-semibold mb-2">{account.accountName}</h3>
            <p className="text-2xl font-bold">
              {formatCurrency(account.balance)}
            </p>
            <p className="text-sm text-muted-foreground">
              {account.accountNumber}
            </p>
            <p className="text-xs text-muted-foreground mt-1">
              {account.bankName}
            </p>
          </div>
        ))}
      </div>

      {/* 최근 거래내역 */}
      <div className="bg-card rounded-xl border shadow-sm p-6">
        <h3 className="font-semibold mb-4">최근 거래내역</h3>
        <div className="space-y-3">
          {accountSummary?.recentTransactions?.length > 0 ? (
            accountSummary.recentTransactions.map((transaction) => (
              <div
                key={transaction.id}
                className="flex justify-between items-center py-2 border-b"
              >
                <div>
                  <p className="font-medium">{transaction.description}</p>
                  <p className="text-sm text-muted-foreground">
                    {formatDate(transaction.transactionDate)}
                  </p>
                  {transaction.counterparty && (
                    <p className="text-xs text-muted-foreground">
                      {transaction.counterparty}
                    </p>
                  )}
                </div>
                <p
                  className={`font-semibold ${getTransactionColor(
                    transaction.transactionType
                  )}`}
                >
                  {getTransactionSign(transaction.transactionType)}
                  {formatCurrency(transaction.amount)}
                </p>
              </div>
            ))
          ) : (
            <p className="text-muted-foreground text-center py-4">
              거래내역이 없습니다.
            </p>
          )}
        </div>
      </div>
    </div>
  );
}
