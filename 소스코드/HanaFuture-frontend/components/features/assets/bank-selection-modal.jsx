"use client";

import { useState } from "react";
import { Check, ArrowLeft, ArrowRight } from "lucide-react";

// 실제 은행 로고 이미지 (두 번째 이미지 스타일로 구현)
const bankLogos = {
  hana: (
    <div className="w-12 h-12 bg-white rounded-full flex items-center justify-center border border-gray-200">
      <div className="w-8 h-8 bg-green-600 rounded-full flex items-center justify-center">
        <span className="text-white font-bold text-xs">하나</span>
      </div>
    </div>
  ),
  kb: (
    <div className="w-12 h-12 bg-white rounded-full flex items-center justify-center border border-gray-200">
      <div className="w-8 h-8 bg-yellow-500 rounded-full flex items-center justify-center">
        <span className="text-white font-bold text-xs">국민</span>
      </div>
    </div>
  ),
  shinhan: (
    <div className="w-12 h-12 bg-white rounded-full flex items-center justify-center border border-gray-200">
      <div className="w-8 h-8 bg-blue-600 rounded-full flex items-center justify-center">
        <span className="text-white font-bold text-xs">신한</span>
      </div>
    </div>
  ),
  woori: (
    <div className="w-12 h-12 bg-white rounded-full flex items-center justify-center border border-gray-200">
      <div className="w-8 h-8 bg-blue-500 rounded-full flex items-center justify-center">
        <span className="text-white font-bold text-xs">우리</span>
      </div>
    </div>
  ),
  nh: (
    <div className="w-12 h-12 bg-white rounded-full flex items-center justify-center border border-gray-200">
      <div className="w-8 h-8 bg-green-500 rounded-full flex items-center justify-center">
        <span className="text-white font-bold text-xs">농협</span>
      </div>
    </div>
  ),
  ibk: (
    <div className="w-12 h-12 bg-white rounded-full flex items-center justify-center border border-gray-200">
      <div className="w-8 h-8 bg-blue-700 rounded-full flex items-center justify-center">
        <span className="text-white font-bold text-xs">기업</span>
      </div>
    </div>
  ),
  imbank: (
    <div className="w-12 h-12 bg-white rounded-full flex items-center justify-center border border-gray-200">
      <div className="w-8 h-8 bg-green-700 rounded-full flex items-center justify-center">
        <span className="text-white font-bold text-xs">iM뱅크</span>
      </div>
    </div>
  ),
  bnk: (
    <div className="w-12 h-12 bg-white rounded-full flex items-center justify-center border border-gray-200">
      <div className="w-8 h-8 bg-red-600 rounded-full flex items-center justify-center">
        <span className="text-white font-bold text-xs">부산</span>
      </div>
    </div>
  ),
  gwangju: (
    <div className="w-12 h-12 bg-white rounded-full flex items-center justify-center border border-gray-200">
      <div className="w-8 h-8 bg-blue-800 rounded-full flex items-center justify-center">
        <span className="text-white font-bold text-xs">광주</span>
      </div>
    </div>
  ),
  jeonbuk: (
    <div className="w-12 h-12 bg-white rounded-full flex items-center justify-center border border-gray-200">
      <div className="w-8 h-8 bg-blue-800 rounded-full flex items-center justify-center">
        <span className="text-white font-bold text-xs">전북</span>
      </div>
    </div>
  ),
  jeju: (
    <div className="w-12 h-12 bg-white rounded-full flex items-center justify-center border border-gray-200">
      <div className="w-8 h-8 bg-blue-600 rounded-full flex items-center justify-center">
        <span className="text-white font-bold text-xs">제주</span>
      </div>
    </div>
  ),
  kdb: (
    <div className="w-12 h-12 bg-white rounded-full flex items-center justify-center border border-gray-200">
      <div className="w-8 h-8 bg-blue-700 rounded-full flex items-center justify-center">
        <span className="text-white font-bold text-xs">산업</span>
      </div>
    </div>
  ),
  suhyup: (
    <div className="w-12 h-12 bg-white rounded-full flex items-center justify-center border border-gray-200">
      <div className="w-8 h-8 bg-blue-600 rounded-full flex items-center justify-center">
        <span className="text-white font-bold text-xs">수협</span>
      </div>
    </div>
  ),
  citi: (
    <div className="w-12 h-12 bg-white rounded-full flex items-center justify-center border border-gray-200">
      <div className="w-8 h-8 bg-blue-700 rounded-full flex items-center justify-center">
        <span className="text-white font-bold text-xs">씨티</span>
      </div>
    </div>
  ),
  sc: (
    <div className="w-12 h-12 bg-white rounded-full flex items-center justify-center border border-gray-200">
      <div className="w-8 h-8 bg-blue-600 rounded-full flex items-center justify-center">
        <span className="text-white font-bold text-xs">SC제일</span>
      </div>
    </div>
  ),
  post: (
    <div className="w-12 h-12 bg-white rounded-full flex items-center justify-center border border-gray-200">
      <div className="w-8 h-8 bg-red-500 rounded-full flex items-center justify-center">
        <span className="text-white font-bold text-xs">우체국</span>
      </div>
    </div>
  ),
  kbank: (
    <div className="w-12 h-12 bg-white rounded-full flex items-center justify-center border border-gray-200">
      <div className="w-8 h-8 bg-blue-500 rounded-full flex items-center justify-center">
        <span className="text-white font-bold text-xs">케이뱅크</span>
      </div>
    </div>
  ),
  kakao: (
    <div className="w-12 h-12 bg-white rounded-full flex items-center justify-center border border-gray-200">
      <div className="w-8 h-8 bg-yellow-400 rounded-full flex items-center justify-center">
        <span className="text-black font-bold text-xs">카카오뱅크</span>
      </div>
    </div>
  ),
  sanlim: (
    <div className="w-12 h-12 bg-white rounded-full flex items-center justify-center border border-gray-200">
      <div className="w-8 h-8 bg-green-600 rounded-full flex items-center justify-center">
        <span className="text-white font-bold text-xs">산림조합</span>
      </div>
    </div>
  ),
  savings: (
    <div className="w-12 h-12 bg-white rounded-full flex items-center justify-center border border-gray-200">
      <div className="w-8 h-8 bg-green-500 rounded-full flex items-center justify-center">
        <span className="text-white font-bold text-xs">저축</span>
      </div>
    </div>
  ),
};

// 은행 데이터
const banks = [
  {
    id: "hana",
    name: "하나은행",
    description: "하나Future의 주거래 은행",
    features: ["실시간 조회", "자동이체", "모임통장 특화"],
    recommended: true,
  },
  {
    id: "kb",
    name: "국민은행",
    description: "KB국민은행 계좌 연결",
    features: ["실시간 조회", "자동이체"],
  },
  {
    id: "shinhan",
    name: "신한은행",
    description: "신한은행 계좌 연결",
    features: ["실시간 조회", "자동이체"],
  },
  {
    id: "woori",
    name: "우리은행",
    description: "우리은행 계좌 연결",
    features: ["실시간 조회", "자동이체"],
  },
  {
    id: "nh",
    name: "농협은행",
    description: "NH농협은행 계좌 연결",
    features: ["실시간 조회", "자동이체"],
  },
  {
    id: "ibk",
    name: "기업은행",
    description: "IBK기업은행 계좌 연결",
    features: ["실시간 조회", "자동이체"],
  },
  {
    id: "imbank",
    name: "iM뱅크(대구)",
    description: "대구은행 계좌 연결",
    features: ["실시간 조회", "자동이체"],
  },
  {
    id: "bnk",
    name: "BNK 부산",
    description: "부산은행 계좌 연결",
    features: ["실시간 조회", "자동이체"],
  },
  {
    id: "gwangju",
    name: "광주은행",
    description: "광주은행 계좌 연결",
    features: ["실시간 조회", "자동이체"],
  },
  {
    id: "jeonbuk",
    name: "전북은행",
    description: "전북은행 계좌 연결",
    features: ["실시간 조회", "자동이체"],
  },
  {
    id: "jeju",
    name: "제주은행",
    description: "제주은행 계좌 연결",
    features: ["실시간 조회", "자동이체"],
  },
  {
    id: "kdb",
    name: "산업은행",
    description: "KDB산업은행 계좌 연결",
    features: ["실시간 조회", "자동이체"],
  },
  {
    id: "suhyup",
    name: "수협은행",
    description: "수협은행 계좌 연결",
    features: ["실시간 조회", "자동이체"],
  },
  {
    id: "citi",
    name: "한국씨티",
    description: "씨티은행 계좌 연결",
    features: ["실시간 조회", "자동이체"],
  },
  {
    id: "sc",
    name: "SC제일",
    description: "SC제일은행 계좌 연결",
    features: ["실시간 조회", "자동이체"],
  },
  {
    id: "post",
    name: "우체국",
    description: "우체국 계좌 연결",
    features: ["실시간 조회", "자동이체"],
  },
  {
    id: "kbank",
    name: "케이뱅크",
    description: "케이뱅크 계좌 연결",
    features: ["실시간 조회", "자동이체"],
  },
  {
    id: "kakao",
    name: "카카오뱅크",
    description: "카카오뱅크 계좌 연결",
    features: ["실시간 조회", "자동이체"],
  },
  {
    id: "sanlim",
    name: "산림조합",
    description: "산림조합 계좌 연결",
    features: ["실시간 조회", "자동이체"],
  },
  {
    id: "savings",
    name: "저축은행",
    description: "저축은행 계좌 연결",
    features: ["실시간 조회", "자동이체"],
  },
];

export function BankSelectionModal({ onClose, onSuccess }) {
  const [selectedBanks, setSelectedBanks] = useState(["hana"]); // 기본적으로 하나은행 선택

  const handleBankSelect = (bankId) => {
    setSelectedBanks((prev) =>
      prev.includes(bankId)
        ? prev.filter((id) => id !== bankId)
        : [...prev, bankId]
    );
  };

  const handleConnect = () => {
    // 실제로는 오픈뱅킹 API 호출
    onSuccess(selectedBanks);
  };

  const getSelectedBankNames = () => {
    return selectedBanks
      .map((id) => banks.find((bank) => bank.id === id)?.name)
      .join(", ");
  };

  const getSelectedBankCount = () => {
    return selectedBanks.length;
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
      <div className="bg-white rounded-3xl max-w-2xl w-full max-h-[90vh] overflow-hidden">
        {/* 헤더 */}
        <div className="flex items-center justify-between p-6 border-b border-gray-200">
          <div className="flex items-center gap-4">
            <button
              onClick={onClose}
              className="flex items-center gap-2 text-gray-600 hover:text-gray-800"
            >
              <ArrowLeft className="w-5 h-5" />
              <span>이전</span>
            </button>
            <h2 className="text-2xl font-bold text-gray-900">은행 선택</h2>
          </div>
          <div className="text-sm text-gray-500">2/4</div>
        </div>

        {/* 안내 메시지 */}
        <div className="p-6 bg-blue-50 border-b border-gray-200">
          <div className="flex items-start gap-4">
            <div className="w-12 h-12 bg-blue-100 rounded-full flex items-center justify-center">
              <span className="text-2xl">🌱</span>
            </div>
            <div>
              <h3 className="font-semibold text-gray-900 mb-1">
                연결할 은행을 선택하세요
              </h3>
              <p className="text-sm text-gray-600">
                오픈뱅킹을 통해 안전하게 계좌 정보를 가져옵니다. 여러 은행을
                선택할 수 있습니다.
              </p>
            </div>
          </div>
        </div>

        {/* 은행 목록 */}
        <div className="p-6 max-h-96 overflow-y-auto">
          <div className="space-y-3">
            {banks.slice(0, 5).map((bank) => (
              <div
                key={bank.id}
                onClick={() => handleBankSelect(bank.id)}
                className={`p-4 rounded-2xl border-2 cursor-pointer transition-all duration-200 ${
                  selectedBanks.includes(bank.id)
                    ? "border-green-500 bg-green-50"
                    : "border-gray-200 hover:border-gray-300 hover:bg-gray-50"
                } ${bank.recommended ? "ring-2 ring-green-200" : ""}`}
              >
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-4">
                    {bankLogos[bank.id]}
                    <div>
                      <div className="flex items-center gap-2">
                        <h4 className="font-semibold text-gray-900">
                          {bank.name}
                        </h4>
                        {bank.recommended && (
                          <span className="px-2 py-1 bg-green-100 text-green-700 text-xs font-medium rounded-full">
                            추천
                          </span>
                        )}
                      </div>
                      <p className="text-sm text-gray-600 mb-2">
                        {bank.description}
                      </p>
                      <div className="flex gap-2">
                        {bank.features.map((feature, index) => (
                          <span
                            key={index}
                            className="px-2 py-1 bg-gray-100 text-gray-600 text-xs rounded-full"
                          >
                            {feature}
                          </span>
                        ))}
                      </div>
                    </div>
                  </div>
                  <div className="flex items-center">
                    {selectedBanks.includes(bank.id) ? (
                      <div className="w-6 h-6 bg-green-500 rounded-full flex items-center justify-center">
                        <Check className="w-4 h-4 text-white" />
                      </div>
                    ) : (
                      <div className="w-6 h-6 border-2 border-gray-300 rounded-full"></div>
                    )}
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* 하단 요약 및 버튼 */}
        <div className="p-6 bg-blue-50 border-t border-gray-200">
          <div className="mb-4">
            <h4 className="font-semibold text-gray-900 mb-2">선택한 은행</h4>
            <p className="text-sm text-gray-600">
              {getSelectedBankCount() > 0
                ? `${getSelectedBankNames()} ${getSelectedBankCount()}개 상품`
                : "은행을 선택해주세요"}
            </p>
          </div>
          <button
            onClick={handleConnect}
            disabled={selectedBanks.length === 0}
            className={`w-full py-4 px-6 rounded-2xl font-semibold transition-colors flex items-center justify-center gap-2 ${
              selectedBanks.length === 0
                ? "bg-gray-300 text-gray-500 cursor-not-allowed"
                : "bg-green-600 hover:bg-green-700 text-white"
            }`}
          >
            선택한 은행 연결하기
            <ArrowRight className="w-5 h-5" />
          </button>
        </div>
      </div>
    </div>
  );
}
