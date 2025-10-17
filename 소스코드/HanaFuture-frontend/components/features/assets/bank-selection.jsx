"use client";

import React, { useState, useEffect } from "react";
import {
  ArrowLeft,
  ArrowRight,
  Building2,
  CheckCircle,
  Shield,
  Star,
  Sparkles,
} from "lucide-react";

export function BankSelection({
  onBankSelect,
  onBack,
  userInfo,
  errorMessage,
}) {
  const [selectedBanks, setSelectedBanks] = useState([]);
  const [isLoading, setIsLoading] = useState(false);

  // 컴포넌트 마운트 시 스크롤 최상단으로 이동
  useEffect(() => {
    window.scrollTo({ top: 0, behavior: "smooth" });
  }, []);

  // 실제 은행 정보 (깔끔한 스타일)
  const availableBanks = [
    {
      code: "081",
      name: "하나은행",
      logo: "/bank-logos/HanaLogo.png",
      description: "하나Future 주거래 은행",
      isRecommended: true,
    },
    {
      code: "003",
      name: "기업은행",
      logo: "/bank-logos/IBKLogo.png",
      description: "IBK기업은행",
      isRecommended: false,
    },
    {
      code: "090",
      name: "카카오뱅크",
      logo: "/bank-logos/KaKaoBankLogo.jpg",
      description: "카카오뱅크",
      isRecommended: false,
    },
    {
      code: "004",
      name: "국민은행",
      logo: "/bank-logos/KBLogo.png",
      description: "KB국민은행",
      isRecommended: false,
    },
    {
      code: "089",
      name: "케이뱅크",
      logo: "/bank-logos/KBankLogo.png",
      description: "K뱅크",
      isRecommended: false,
    },
    {
      code: "011",
      name: "농협은행",
      logo: "/bank-logos/NHLogo.png",
      description: "NH농협은행",
      isRecommended: false,
    },
    {
      code: "088",
      name: "신한은행",
      logo: "/bank-logos/ShinhanLogo.png",
      description: "신한은행",
      isRecommended: false,
    },
    {
      code: "007",
      name: "수협은행",
      logo: "/bank-logos/SHLogo.png",
      description: "수협은행",
      isRecommended: false,
    },
    {
      code: "020",
      name: "우리은행",
      logo: "/bank-logos/WooriLogo.png",
      description: "우리은행",
      isRecommended: false,
    },
  ];

  const handleBankToggle = (bankCode) => {
    setSelectedBanks((prev) => {
      if (prev.includes(bankCode)) {
        return prev.filter((code) => code !== bankCode);
      } else {
        return [...prev, bankCode];
      }
    });
  };

  const handleContinue = async () => {
    if (selectedBanks.length === 0) return;

    setIsLoading(true);
    try {
      const selectedBankInfo = availableBanks.filter((bank) =>
        selectedBanks.includes(bank.code)
      );

      await new Promise((resolve) => setTimeout(resolve, 1000));

      onBankSelect(selectedBankInfo);
    } catch (error) {
      console.error("은행 선택 처리 오류:", error);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="bg-white">
      <div className="max-w-4xl mx-auto px-6 py-8 space-y-8">
        {/* 안내 섹션 */}
        <div className="text-center space-y-4">
          <div>
            <h2 className="text-3xl font-bold text-gray-900 mb-3">
              연결할 은행을 선택하세요
            </h2>
            <p className="text-lg text-gray-600 leading-relaxed max-w-2xl mx-auto">
              오픈뱅킹을 통해 안전하게 계좌 정보를 가져옵니다.
              <br />
              여러 은행을 선택할 수 있습니다.
            </p>
          </div>

          {/* 에러 메시지 */}
          {errorMessage && (
            <div className="bg-red-50 border border-red-200 rounded-xl p-4 max-w-2xl mx-auto">
              <p className="text-red-700 text-sm font-medium">{errorMessage}</p>
            </div>
          )}
        </div>

        {/* 은행 목록 - 3열 그리드 */}
        <div className="grid grid-cols-3 gap-4">
          {availableBanks.map((bank) => (
            <div
              key={bank.code}
              onClick={() => handleBankToggle(bank.code)}
              className={`relative bg-white rounded-2xl p-4 border-2 cursor-pointer transition-all duration-300 hover:shadow-lg group ${
                selectedBanks.includes(bank.code)
                  ? "border-emerald-500 bg-emerald-50"
                  : "border-gray-200 hover:border-gray-300"
              }`}
            >
              {/* 추천 뱃지 */}
              {bank.isRecommended && (
                <div className="absolute -top-2 -right-2">
                  <div className="bg-gradient-to-r from-emerald-500 to-teal-500 text-white px-3 py-1 rounded-full text-xs font-bold flex items-center shadow-lg">
                    <Sparkles className="w-3 h-3 mr-1" />
                    추천
                  </div>
                </div>
              )}

              <div className="text-center space-y-3">
                {/* 은행 로고 */}
                <div className="w-20 h-20 bg-gray-50 rounded-xl flex items-center justify-center mx-auto">
                  <img
                    src={bank.logo}
                    alt={`${bank.name} 로고`}
                    className="w-16 h-16 object-contain"
                  />
                </div>

                {/* 은행 이름만 표시 */}
                <h3 className="text-sm font-bold text-gray-900">{bank.name}</h3>

                {/* 선택 상태 */}
                <div className="flex justify-center">
                  {selectedBanks.includes(bank.code) ? (
                    <div className="w-6 h-6 bg-gradient-to-r from-emerald-500 to-teal-500 rounded-full flex items-center justify-center shadow-lg">
                      <CheckCircle className="w-4 h-4 text-white" />
                    </div>
                  ) : (
                    <div className="w-6 h-6 border-2 border-gray-300 rounded-full group-hover:border-gray-400 transition-colors"></div>
                  )}
                </div>
              </div>
            </div>
          ))}
        </div>

        {/* 선택 요약 */}
        {selectedBanks.length > 0 ? (
          <div className="bg-white rounded-2xl p-6 border border-gray-200">
            <div className="flex items-center space-x-3 mb-4">
              <div className="w-10 h-10 bg-gradient-to-r from-emerald-500 to-teal-500 rounded-xl flex items-center justify-center">
                <CheckCircle className="w-6 h-6 text-white" />
              </div>
              <h3 className="text-xl font-bold text-gray-900">선택한 은행</h3>
            </div>
            <div className="grid grid-cols-2 md:grid-cols-3 gap-3">
              {selectedBanks.map((bankCode) => {
                const bank = availableBanks.find((b) => b.code === bankCode);
                return (
                  <div
                    key={bankCode}
                    className="bg-gray-50 px-4 py-3 rounded-xl border border-gray-200 flex items-center space-x-3"
                  >
                    <img
                      src={bank?.logo}
                      alt={`${bank?.name} 로고`}
                      className="w-8 h-8 object-contain"
                    />
                    <div>
                      <span className="text-sm font-bold text-gray-900">
                        {bank?.name}
                      </span>
                    </div>
                  </div>
                );
              })}
            </div>
            {!selectedBanks.includes("081") && (
              <div className="mt-4 p-4 bg-gray-50 border border-gray-200 rounded-xl">
                <div className="flex items-center space-x-2">
                  <Sparkles className="w-5 h-5 text-gray-600" />
                  <p className="text-gray-700 text-sm">
                    <strong>하나은행</strong>을 선택하시면 모임통장 특화 기능을
                    이용하실 수 있습니다.
                  </p>
                </div>
              </div>
            )}
          </div>
        ) : (
          <div className="bg-gray-50 rounded-2xl p-8 border border-gray-200 text-center">
            <div className="w-16 h-16 bg-gray-200 rounded-2xl flex items-center justify-center mx-auto mb-4">
              <Building2 className="w-8 h-8 text-gray-400" />
            </div>
            <h3 className="text-xl font-bold text-gray-900 mb-2">
              은행을 선택해주세요
            </h3>
            <p className="text-gray-600">
              연결할 은행을 하나 이상 선택하시면 해당 은행의 계좌를 조회할 수
              있습니다.
            </p>
          </div>
        )}

        {/* 계속하기 버튼 */}
        <div className="pt-4 pb-8">
          <button
            onClick={handleContinue}
            disabled={selectedBanks.length === 0 || isLoading}
            className={`w-full py-5 rounded-2xl font-bold text-lg transition-all duration-300 flex items-center justify-center ${
              selectedBanks.length > 0 && !isLoading
                ? "bg-gradient-to-r from-emerald-500 to-teal-500 text-white hover:from-emerald-600 hover:to-teal-600 shadow-lg hover:shadow-xl transform hover:scale-105"
                : "bg-gray-200 text-gray-500 cursor-not-allowed"
            }`}
          >
            {isLoading ? (
              <>
                <div className="w-6 h-6 border-2 border-white border-t-transparent rounded-full animate-spin mr-3" />
                연결 중...
              </>
            ) : (
              <>
                <span>선택한 은행 연결하기</span>
                <ArrowRight className="w-6 h-6 ml-3" />
              </>
            )}
          </button>
        </div>
      </div>
    </div>
  );
}
