"use client";

import React, { useState, useEffect } from "react";
import {
  ArrowLeft,
  ArrowRight,
  Check,
  CreditCard,
  Wallet,
  Shield,
  ChevronRight,
} from "lucide-react";

export function AccountSelection({
  onAccountSelect,
  onBack,
  userInfo,
  selectedBanks,
  availableAccounts = [], // props로 받은 계좌 목록 사용
}) {
  const [accounts, setAccounts] = useState([]);
  const [selectedAccounts, setSelectedAccounts] = useState([]); // 단일 → 다중 선택으로 변경
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState("");

  // 컴포넌트 마운트 시 스크롤 최상단으로 이동
  useEffect(() => {
    window.scrollTo({ top: 0, behavior: "smooth" });
  }, []);

  // props로 받은 계좌 데이터를 포맷팅하여 사용
  useEffect(() => {
    if (availableAccounts && availableAccounts.length > 0) {
      const formattedAccounts = availableAccounts.map((account, index) => ({
        accountNum: account.accountNum,
        accountAlias: account.productName || account.accountName,
        balanceAmt: parseInt(
          account.balanceAmt || account.balance || 0
        ).toLocaleString(),
        productName: account.productName,
        bankName: account.bankName || "하나은행",
        fintechUseNum: account.fintechUseNum,
        accountType:
          account.accountType === "1"
            ? "입출금"
            : account.accountType === "2"
            ? "적금"
            : "예금",
        icon: `/hana3dIcon/hanaIcon3d_${(index % 6) + 1}_101.png`,
        bankCode: account.bankCode || "081",
        bankColor: "from-emerald-500 to-teal-500",
      }));
      setAccounts(formattedAccounts);
      console.log(`${formattedAccounts.length}개 계좌 표시 준비 완료`);
    } else {
      setError("조회된 계좌가 없습니다.");
      setAccounts([]);
    }
  }, [availableAccounts]);

  const handleAccountSelect = (account) => {
    // 다중 선택 토글
    setSelectedAccounts((prev) => {
      const isAlreadySelected = prev.some(
        (acc) => acc.accountNum === account.accountNum
      );
      if (isAlreadySelected) {
        return prev.filter((acc) => acc.accountNum !== account.accountNum);
      } else {
        return [...prev, account];
      }
    });
  };

  const handleContinue = () => {
    if (selectedAccounts.length > 0) {
      onAccountSelect(selectedAccounts); // 배열로 전달
    }
  };

  const getAccountTypeColor = (type) => {
    switch (type) {
      case "입출금":
        return "bg-blue-100 text-blue-800";
      case "적금":
        return "bg-green-100 text-green-800";
      case "예금":
        return "bg-purple-100 text-purple-800";
      default:
        return "bg-gray-100 text-gray-800";
    }
  };

  if (isLoading) {
    return (
      <div className="bg-gray-50 flex items-center justify-center p-4 py-20">
        <div className="text-center">
          <div className="w-16 h-16 bg-emerald-100 rounded-full flex items-center justify-center mx-auto mb-4">
            <img
              src="/hana3dIcon/hanaIcon3d_4_101.png"
              alt="로딩"
              className="w-12 h-12 object-contain animate-pulse"
            />
          </div>
          <h2 className="text-xl font-semibold text-gray-900 mb-2">
            계좌 조회 중...
          </h2>
          <p className="text-gray-600">
            하나은행에서 계좌 정보를 가져오고 있습니다.
          </p>
        </div>
      </div>
    );
  }

  return (
    <div className="bg-gray-50 pb-20">
      {/* 헤더 */}
      <div className="bg-white shadow-sm sticky top-0 z-10">
        <div className="max-w-2xl mx-auto px-4 py-4">
          <div className="flex items-center justify-between">
            <button
              onClick={onBack}
              className="flex items-center text-gray-600 hover:text-gray-900"
            >
              <ArrowLeft className="w-5 h-5 mr-2" />
              이전
            </button>
            <h1 className="text-lg font-semibold text-gray-900">계좌 선택</h1>
            <div className="w-16"></div>
          </div>
        </div>
      </div>

      <div className="max-w-2xl mx-auto p-4 space-y-6">
        {/* 안내 섹션 */}
        <div className="bg-white rounded-2xl p-6 shadow-sm">
          <div>
            <h2 className="text-xl font-bold text-gray-900 mb-2">
              연결할 계좌를 선택하세요
            </h2>
            {selectedBanks && selectedBanks.length > 0 && (
              <div className="mt-3 flex flex-wrap gap-2">
                {selectedBanks.map((bank) => (
                  <span
                    key={bank.code}
                    className="bg-emerald-100 text-emerald-700 px-2 py-1 rounded-full text-xs font-medium"
                  >
                    {bank.name} 연결됨
                  </span>
                ))}
              </div>
            )}
          </div>
        </div>

        {/* 계좌 목록 */}
        <div className="space-y-4">
          {error ? (
            <div className="bg-red-50 border border-red-200 rounded-2xl p-6 text-center">
              <div className="w-12 h-12 bg-red-100 rounded-xl flex items-center justify-center mx-auto mb-3">
                <Shield className="w-6 h-6 text-red-600" />
              </div>
              <p className="text-red-700 font-medium mb-2">계좌 조회 실패</p>
              <p className="text-red-600 text-sm">{error}</p>
              <button
                onClick={fetchUserAccounts}
                className="mt-4 px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors"
              >
                다시 시도
              </button>
            </div>
          ) : (
            accounts.map((account) => (
              <div
                key={account.accountNum}
                onClick={() => handleAccountSelect(account)}
                className={`bg-white rounded-2xl p-6 shadow-sm border-2 transition-all duration-200 cursor-pointer hover:shadow-md ${
                  selectedAccounts.some(
                    (acc) => acc.accountNum === account.accountNum
                  )
                    ? "border-emerald-500 bg-emerald-50"
                    : "border-transparent hover:border-gray-200"
                }`}
              >
                <div className="flex items-start justify-between">
                  <div className="flex items-start space-x-4 flex-1">
                    {/* 계좌 아이콘 - HanaLogo.png로 변경 */}
                    <div className="w-14 h-14 bg-gradient-to-br from-emerald-100 to-teal-100 rounded-xl flex items-center justify-center flex-shrink-0">
                      <img
                        src="/bank-logos/HanaLogo.png"
                        alt="하나은행"
                        className="w-10 h-10 object-contain"
                      />
                    </div>

                    {/* 계좌 정보 */}
                    <div className="flex-1">
                      <div className="flex items-center space-x-2 mb-2">
                        <h3 className="font-semibold text-gray-900">
                          {account.accountAlias}
                        </h3>
                        <span
                          className={`px-2 py-1 rounded-full text-xs font-medium ${getAccountTypeColor(
                            account.accountType
                          )}`}
                        >
                          {account.accountType}
                        </span>
                      </div>

                      <p className="text-sm text-gray-600 mb-2">
                        {account.productName}
                      </p>

                      <div className="flex items-center space-x-2 mb-3">
                        <span className="text-sm font-mono text-gray-700">
                          {account.accountNum}
                        </span>
                        <span className="text-xs text-gray-500">
                          {account.bankName}
                        </span>
                      </div>

                      {/* 잔액 */}
                      <div className="flex items-center space-x-2">
                        <span className="text-sm text-gray-500">잔액:</span>
                        <span className="font-semibold text-gray-900">
                          {account.balanceAmt}원
                        </span>
                      </div>
                    </div>
                  </div>

                  {/* 선택 표시 */}
                  <div className="flex items-center ml-4">
                    {selectedAccounts.some(
                      (acc) => acc.accountNum === account.accountNum
                    ) ? (
                      <div className="w-6 h-6 bg-emerald-500 rounded-full flex items-center justify-center">
                        <Check className="w-4 h-4 text-white" />
                      </div>
                    ) : (
                      <div className="w-6 h-6 border-2 border-gray-300 rounded-full"></div>
                    )}
                  </div>
                </div>
              </div>
            ))
          )}
        </div>

        {/* 선택된 계좌 요약 */}
        {selectedAccounts.length > 0 && (
          <div className="bg-white border border-gray-200 rounded-2xl p-6">
            <div className="flex items-start space-x-4">
              <div className="w-12 h-12 bg-gray-100 rounded-xl flex items-center justify-center">
                <img
                  src="/hana3dIcon/hanaIcon3d_6_101.png"
                  alt="선택됨"
                  className="w-8 h-8 object-contain"
                />
              </div>
              <div className="flex-1">
                <h3 className="font-semibold text-gray-900 mb-2">
                  선택된 계좌 ({selectedAccounts.length}개)
                </h3>
                <div className="space-y-1">
                  {selectedAccounts.map((account) => (
                    <p
                      key={account.accountNum}
                      className="text-gray-700 text-sm"
                    >
                      <span className="font-medium">
                        {account.accountAlias}
                      </span>{" "}
                      ({account.accountNum})
                    </p>
                  ))}
                </div>
                <p className="text-sm text-gray-600 mt-2">
                  선택한 계좌들이 하나Future 서비스와 연동됩니다.
                </p>
              </div>
            </div>
          </div>
        )}

        {/* 계속하기 버튼 */}
        <div className="pt-4 pb-8">
          <button
            onClick={handleContinue}
            disabled={selectedAccounts.length === 0}
            className={`w-full flex items-center justify-center px-6 py-4 rounded-2xl font-semibold text-lg transition-all duration-200 ${
              selectedAccounts.length > 0
                ? "bg-emerald-600 text-white hover:bg-emerald-700 shadow-lg hover:shadow-xl"
                : "bg-gray-300 text-gray-500 cursor-not-allowed"
            }`}
          >
            <span>
              {selectedAccounts.length > 0
                ? `${selectedAccounts.length}개 계좌 연결하기`
                : "계좌를 선택해주세요"}
            </span>
            {selectedAccounts.length > 0 && (
              <ArrowRight className="w-5 h-5 ml-2" />
            )}
          </button>
        </div>
      </div>
    </div>
  );
}
