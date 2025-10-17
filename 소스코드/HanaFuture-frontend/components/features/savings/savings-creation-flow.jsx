"use client";

import React, { useState, useEffect } from "react";
import {
  ArrowLeft,
  ArrowRight,
  CheckCircle,
  Wallet,
  Users,
  PiggyBank,
  Target,
  Calendar,
  Building2,
  Plus,
} from "lucide-react";
import { useAuth } from "../../../contexts/AuthContext";

// API Base URL - 환경변수 사용
const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080/api";

export function SavingsCreationFlow({ onComplete, onCancel, userInfo }) {
  const { user } = useAuth();
  const [currentStep, setCurrentStep] = useState(1); // 1: 기본정보, 2: 계좌선택, 3: 확인
  const [formData, setFormData] = useState({
    goalName: "",
    description: "",
    targetAmount: "",
    monthlyTarget: "",
    category: "",
    endDate: "",
    sourceType: "PERSONAL_ACCOUNT", // PERSONAL_ACCOUNT or GROUP_ACCOUNT
    sourceAccountId: "",
    autoTransferDay: 25,
  });
  const [availableAccounts, setAvailableAccounts] = useState([]);
  const [groupAccounts, setGroupAccounts] = useState([]);
  const [isLoading, setIsLoading] = useState(false);

  // 적금 카테고리
  const categories = [
    {
      id: "BIRTH_PREPARATION",
      name: "출산 준비",
      icon: "/hanacharacter/hanacharacter3.png",
      description: "출산 관련 비용 준비",
      color: "from-pink-500 to-rose-500",
    },
    {
      id: "CHILDCARE",
      name: "육아 자금",
      icon: "/hanacharacter/hanacharacter4.png",
      description: "육아용품, 육아휴직 대비",
      color: "from-purple-500 to-pink-500",
    },
    {
      id: "EDUCATION",
      name: "교육비",
      icon: "/hanacharacter/hanacharacter5.png",
      description: "자녀 교육비 마련",
      color: "from-blue-500 to-indigo-500",
    },
    {
      id: "HOUSING",
      name: "내집 마련",
      icon: "/hanacharacter/hanacharacter6.png",
      description: "주택 구입 자금",
      color: "from-emerald-500 to-teal-500",
    },
    {
      id: "EMERGENCY",
      name: "비상 자금",
      icon: "/hanacharacter/hanacharacter7.png",
      description: "응급 상황 대비",
      color: "from-orange-500 to-red-500",
    },
    {
      id: "VACATION",
      name: "가족 여행",
      icon: "/hanacharacter/hanacharacter8.png",
      description: "가족 여행 경비",
      color: "from-sky-500 to-blue-500",
    },
  ];

  // 사용 가능한 계좌들 로드
  useEffect(() => {
    loadAvailableAccounts();
  }, []);

  const loadAvailableAccounts = async () => {
    try {
      // 1. 사용자 개인 계좌 조회 (입출금 계좌만)
      // 1. 개인 계좌 조회 (통합 뱅킹 API 사용)
      const personalAccountsResponse = await fetch(
        `${API_BASE_URL}/banking/accounts/user/${user?.id || 1}`,
        {
          headers: {
            "Content-Type": "application/json",
            ...(user?.token && { Authorization: `Bearer ${user.token}` }),
          },
        }
      );

      let personalAccounts = [];
      if (personalAccountsResponse.ok) {
        const personalData = await personalAccountsResponse.json();
        const allAccounts = personalData.data?.accounts || [];

        // 출금 가능한 계좌만 필터링 (입출금 계좌)
        personalAccounts = allAccounts
          .filter((account) => {
            // accountType이 "1" 또는 "입출금"이고 isWithdrawable이 true인 계좌
            const isCheckingAccount =
              account.accountType === "1" ||
              account.accountType === "입출금" ||
              account.isWithdrawable === true;

            console.log(
              `계좌 필터링: ${account.productName} - accountType: ${account.accountType}, isWithdrawable: ${account.isWithdrawable}, 통과: ${isCheckingAccount}`
            );
            return isCheckingAccount;
          })
          .map((account) => ({
            id: account.id || account.fintechUseNum,
            accountNumber: account.accountNum,
            name: account.productName,
            balance: new Intl.NumberFormat("ko-KR").format(
              account.balanceAmt || 0
            ),
            bankName: account.bankName || "하나은행",
            type: "입출금",
            source: account.source || "UNKNOWN",
          }));
      }

      // 2. 모임통장 조회
      const groupAccountsResponse = await fetch(
        `http://localhost:8080/api/group-accounts/user/${user?.id || 1}`,
        {
          headers: {
            "Content-Type": "application/json",
            ...(user?.token && { Authorization: `Bearer ${user.token}` }),
          },
        }
      );

      let groupAccounts = [];
      if (groupAccountsResponse.ok) {
        const groupData = await groupAccountsResponse.json();
        const userGroupAccounts = groupData.data || [];

        groupAccounts = userGroupAccounts.map((account) => ({
          id: account.id,
          accountNumber: account.accountNumber,
          name: account.name,
          balance: new Intl.NumberFormat("ko-KR").format(
            account.currentBalance || 0
          ),
          bankName: account.bankName || "하나은행",
          type: "모임통장",
        }));
      }

      setAvailableAccounts(personalAccounts);
      setGroupAccounts(groupAccounts);

      console.log("함께적금용 계좌 로드 완료:", {
        personal: personalAccounts.length,
        group: groupAccounts.length,
        personalAccounts: personalAccounts,
        groupAccounts: groupAccounts,
      });
    } catch (error) {
      console.error("계좌 정보 로드 실패:", error);
      // 오류 시 빈 배열로 설정
      setAvailableAccounts([]);
      setGroupAccounts([]);
    }
  };

  const handleSubmit = async () => {
    if (currentStep < 3) return;

    setIsLoading(true);
    try {
      // 함께 적금 생성 API 호출
      const response = await fetch(`${API_BASE_URL}/savings/create`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          ...(user?.token && { Authorization: `Bearer ${user.token}` }),
        },
        body: JSON.stringify({
          userId: user?.id || 1,
          goalName: formData.goalName,
          description: formData.description,
          targetAmount: parseInt(formData.targetAmount),
          monthlyTarget: parseInt(formData.monthlyTarget),
          category: formData.category.toUpperCase(),
          startDate: new Date().toISOString().split("T")[0],
          endDate: formData.endDate,
          interestRate: 4.2, // 기본 이자율
          sourceType: formData.sourceType,
          sourceAccountId: formData.sourceAccountId,
          autoTransferDay: formData.autoTransferDay,
          isJointSavings: true,
        }),
      });

      const result = await response.json();

      if (result.success) {
        // 전역 이벤트 발생 - 마이페이지 새로고침 트리거
        window.dispatchEvent(
          new CustomEvent("savingsCreated", {
            detail: result.data,
          })
        );

        onComplete({
          savingsGoal: result.data,
          formData,
        });
      } else {
        alert(
          "적금 생성에 실패했습니다: " + (result.message || "알 수 없는 오류")
        );
      }
    } catch (error) {
      console.error("적금 생성 오류:", error);
      alert("적금 생성 중 오류가 발생했습니다.");
    } finally {
      setIsLoading(false);
    }
  };

  const validateStep1 = () => {
    return (
      formData.goalName &&
      formData.category &&
      formData.targetAmount &&
      formData.endDate
    );
  };

  const validateStep2 = () => {
    return formData.sourceAccountId;
  };

  const handleNextStep = () => {
    if (currentStep === 1 && validateStep1()) {
      setCurrentStep(2);
    } else if (currentStep === 2 && validateStep2()) {
      setCurrentStep(3);
    }
  };

  const handlePrevStep = () => {
    if (currentStep > 1) {
      setCurrentStep(currentStep - 1);
    } else {
      onCancel();
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* 헤더 */}
      <div className="bg-white shadow-sm sticky top-0 z-10">
        <div className="max-w-2xl mx-auto px-4 py-4">
          <div className="flex items-center justify-between">
            <button
              onClick={handlePrevStep}
              className="flex items-center text-gray-600 hover:text-gray-900"
            >
              <ArrowLeft className="w-5 h-5 mr-2" />
              이전
            </button>
            <h1 className="text-lg font-semibold text-gray-900">
              함께 적금 만들기
            </h1>
            <div className="text-sm text-gray-500">{currentStep}/3</div>
          </div>
        </div>
      </div>

      {/* 진행 단계 */}
      <div className="max-w-2xl mx-auto px-4 py-4">
        <div className="flex items-center justify-between mb-6">
          {[1, 2, 3].map((num) => (
            <div key={num} className="flex items-center">
              <div
                className={`w-8 h-8 rounded-full flex items-center justify-center text-sm font-semibold ${
                  num <= currentStep
                    ? "bg-blue-600 text-white"
                    : "bg-gray-200 text-gray-400"
                }`}
              >
                {num < currentStep ? <CheckCircle className="w-4 h-4" /> : num}
              </div>
              {num < 3 && (
                <div
                  className={`w-16 h-0.5 mx-2 ${
                    num < currentStep ? "bg-blue-600" : "bg-gray-200"
                  }`}
                />
              )}
            </div>
          ))}
        </div>
      </div>

      <div className="max-w-2xl mx-auto p-4 space-y-6">
        {/* 1단계: 기본 정보 */}
        {currentStep === 1 && (
          <>
            <div className="bg-white rounded-2xl p-6 shadow-sm text-center">
              <img
                src="/hanacharacter/hanacharacter8.png"
                alt="함께 적금 캐릭터"
                className="w-20 h-20 object-contain mx-auto mb-4"
              />
              <h2 className="text-xl font-bold text-gray-900 mb-2">
                함께 적금 목표를 설정하세요
              </h2>
              <p className="text-gray-600">
                가족과 함께 달성할 저축 목표를 만들어보세요
              </p>
            </div>

            {/* 적금명 */}
            <div className="bg-white rounded-2xl p-6 shadow-sm">
              <label className="block text-sm font-medium text-gray-700 mb-3">
                적금 목표 이름 *
              </label>
              <input
                type="text"
                value={formData.goalName}
                onChange={(e) =>
                  setFormData({ ...formData, goalName: e.target.value })
                }
                placeholder="예: 우리 아기 출산 준비"
                className="w-full h-12 px-4 border border-gray-300 rounded-xl focus:border-blue-500 focus:outline-none"
                required
              />
            </div>

            {/* 카테고리 선택 */}
            <div className="bg-white rounded-2xl p-6 shadow-sm">
              <label className="block text-sm font-medium text-gray-700 mb-3">
                적금 목적 *
              </label>
              <div className="grid grid-cols-2 gap-3">
                {categories.map((category) => (
                  <button
                    key={category.id}
                    type="button"
                    onClick={() =>
                      setFormData({ ...formData, category: category.id })
                    }
                    className={`p-4 rounded-xl border-2 transition-all ${
                      formData.category === category.id
                        ? "border-blue-500 bg-blue-50"
                        : "border-gray-200 hover:border-gray-300"
                    }`}
                  >
                    <img
                      src={category.icon}
                      alt={category.name}
                      className="w-8 h-8 mx-auto mb-2 object-contain"
                    />
                    <div className="text-sm font-semibold text-gray-900">
                      {category.name}
                    </div>
                    <div className="text-xs text-gray-600 mt-1">
                      {category.description}
                    </div>
                  </button>
                ))}
              </div>
            </div>

            {/* 목표 금액 */}
            <div className="bg-white rounded-2xl p-6 shadow-sm">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    목표 금액 (원) *
                  </label>
                  <input
                    type="number"
                    value={formData.targetAmount}
                    onChange={(e) =>
                      setFormData({ ...formData, targetAmount: e.target.value })
                    }
                    placeholder="예: 5000000"
                    className="w-full h-12 px-4 border border-gray-300 rounded-xl focus:border-blue-500 focus:outline-none"
                    required
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    월 목표 금액 (원)
                  </label>
                  <input
                    type="number"
                    value={formData.monthlyTarget}
                    onChange={(e) =>
                      setFormData({
                        ...formData,
                        monthlyTarget: e.target.value,
                      })
                    }
                    placeholder="예: 500000"
                    className="w-full h-12 px-4 border border-gray-300 rounded-xl focus:border-blue-500 focus:outline-none"
                  />
                </div>
              </div>
            </div>

            {/* 목표 달성일 */}
            <div className="bg-white rounded-2xl p-6 shadow-sm">
              <label className="block text-sm font-medium text-gray-700 mb-2">
                목표 달성일 *
              </label>
              <input
                type="date"
                value={formData.endDate}
                onChange={(e) =>
                  setFormData({ ...formData, endDate: e.target.value })
                }
                className="w-full h-12 px-4 border border-gray-300 rounded-xl focus:border-blue-500 focus:outline-none"
                required
              />
            </div>

            {/* 설명 */}
            <div className="bg-white rounded-2xl p-6 shadow-sm">
              <label className="block text-sm font-medium text-gray-700 mb-2">
                설명 (선택)
              </label>
              <textarea
                value={formData.description}
                onChange={(e) =>
                  setFormData({ ...formData, description: e.target.value })
                }
                placeholder="이 적금의 목적을 설명해주세요"
                className="w-full h-20 px-4 py-3 border border-gray-300 rounded-xl focus:border-blue-500 focus:outline-none resize-none"
              />
            </div>
          </>
        )}

        {/* 2단계: 출금 계좌 선택 */}
        {currentStep === 2 && (
          <>
            <div className="bg-white rounded-2xl p-6 shadow-sm text-center">
              <img
                src="/hanacharacter/hanacharacter12.png"
                alt="계좌 선택 캐릭터"
                className="w-20 h-20 object-contain mx-auto mb-4"
              />
              <h2 className="text-xl font-bold text-gray-900 mb-2">
                자동이체 출금 계좌를 선택하세요
              </h2>
              <p className="text-gray-600">
                매월{" "}
                {formData.monthlyTarget
                  ? parseInt(formData.monthlyTarget).toLocaleString()
                  : "목표"}
                원이 자동으로 적금에 납입됩니다
              </p>
            </div>

            {/* 출금 계좌 유형 선택 */}
            <div className="bg-white rounded-2xl p-6 shadow-sm">
              <label className="block text-sm font-medium text-gray-700 mb-3">
                출금 계좌 유형 선택
              </label>
              <div className="grid grid-cols-2 gap-3">
                <button
                  type="button"
                  onClick={() =>
                    setFormData({
                      ...formData,
                      sourceType: "PERSONAL_ACCOUNT",
                      sourceAccountId: "",
                    })
                  }
                  className={`p-4 rounded-xl border-2 transition-all ${
                    formData.sourceType === "PERSONAL_ACCOUNT"
                      ? "border-blue-500 bg-blue-50"
                      : "border-gray-200 hover:border-gray-300"
                  }`}
                >
                  <Wallet className="w-6 h-6 mx-auto mb-2 text-blue-600" />
                  <div className="font-semibold text-gray-900">개인 계좌</div>
                  <div className="text-xs text-gray-600">
                    개인 계좌에서 출금
                  </div>
                </button>
                <button
                  type="button"
                  onClick={() =>
                    setFormData({
                      ...formData,
                      sourceType: "GROUP_ACCOUNT",
                      sourceAccountId: "",
                    })
                  }
                  className={`p-4 rounded-xl border-2 transition-all ${
                    formData.sourceType === "GROUP_ACCOUNT"
                      ? "border-blue-500 bg-blue-50"
                      : "border-gray-200 hover:border-gray-300"
                  }`}
                >
                  <Users className="w-6 h-6 mx-auto mb-2 text-purple-600" />
                  <div className="font-semibold text-gray-900">모임통장</div>
                  <div className="text-xs text-gray-600">
                    모임통장에서 공동 출금
                  </div>
                </button>
              </div>
            </div>

            {/* 계좌 선택 */}
            <div className="bg-white rounded-2xl p-6 shadow-sm">
              <label className="block text-sm font-medium text-gray-700 mb-3">
                {formData.sourceType === "GROUP_ACCOUNT"
                  ? "모임통장 선택"
                  : "개인 계좌 선택"}
              </label>
              <div className="space-y-3">
                {(formData.sourceType === "GROUP_ACCOUNT"
                  ? groupAccounts
                  : availableAccounts
                ).length === 0 ? (
                  <div className="p-6 text-center">
                    <div className="w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-4">
                      <Wallet className="w-8 h-8 text-gray-400" />
                    </div>
                    <h3 className="font-semibold text-gray-900 mb-2">
                      {formData.sourceType === "GROUP_ACCOUNT"
                        ? "등록된 모임통장이 없습니다"
                        : "등록된 출금 계좌가 없습니다"}
                    </h3>
                    <p className="text-sm text-gray-600 mb-4">
                      {formData.sourceType === "GROUP_ACCOUNT"
                        ? "먼저 모임통장을 개설하거나 다른 계좌 유형을 선택해주세요"
                        : "먼저 개인 계좌를 연결하거나 모임통장을 선택해주세요"}
                    </p>
                    <button
                      type="button"
                      onClick={() => {
                        console.log("계좌 연결 또는 모임통장 개설 안내");
                        alert(
                          formData.sourceType === "GROUP_ACCOUNT"
                            ? "모임통장을 먼저 개설해주세요"
                            : "개인 계좌를 먼저 연결해주세요"
                        );
                      }}
                      className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
                    >
                      {formData.sourceType === "GROUP_ACCOUNT"
                        ? "모임통장 개설하기"
                        : "계좌 연결하기"}
                    </button>
                  </div>
                ) : (
                  (formData.sourceType === "GROUP_ACCOUNT"
                    ? groupAccounts
                    : availableAccounts
                  ).map((account) => (
                    <div
                      key={account.id}
                      onClick={() =>
                        setFormData({
                          ...formData,
                          sourceAccountId: account.id,
                        })
                      }
                      className={`p-4 rounded-xl border-2 cursor-pointer transition-all ${
                        formData.sourceAccountId === account.id
                          ? "border-blue-500 bg-blue-50"
                          : "border-gray-200 hover:border-gray-300"
                      }`}
                    >
                      <div className="flex items-center justify-between">
                        <div>
                          <div className="font-semibold text-gray-900">
                            {account.name}
                          </div>
                          <div className="text-sm text-gray-600">
                            {account.accountNumber} • {account.bankName}
                          </div>
                          <div className="text-xs text-gray-500">
                            {account.type}
                          </div>
                        </div>
                        <div className="text-right">
                          <div className="font-semibold text-gray-900">
                            {account.balance}원
                          </div>
                          <div className="text-xs text-emerald-600">잔액</div>
                        </div>
                      </div>
                    </div>
                  ))
                )}
              </div>
            </div>

            {/* 자동이체일 설정 */}
            <div className="bg-white rounded-2xl p-6 shadow-sm">
              <label className="block text-sm font-medium text-gray-700 mb-2">
                매월 자동이체일
              </label>
              <select
                value={formData.autoTransferDay}
                onChange={(e) =>
                  setFormData({
                    ...formData,
                    autoTransferDay: parseInt(e.target.value),
                  })
                }
                className="w-full h-12 px-4 border border-gray-300 rounded-xl focus:border-blue-500 focus:outline-none"
              >
                {[1, 5, 10, 15, 20, 25, 28].map((day) => (
                  <option key={day} value={day}>
                    매월 {day}일
                  </option>
                ))}
              </select>
            </div>
          </>
        )}

        {/* 3단계: 확인 및 생성 */}
        {currentStep === 3 && (
          <>
            <div className="bg-white rounded-2xl p-6 shadow-sm text-center">
              <img
                src="/hanacharacter/hanacharacter9.png"
                alt="적금 생성 완료 캐릭터"
                className="w-20 h-20 object-contain mx-auto mb-4"
              />
              <h2 className="text-xl font-bold text-gray-900 mb-2">
                적금 정보를 확인하고 생성하세요
              </h2>
              <p className="text-gray-600">
                새로운 하나은행 적금 계좌가 개설됩니다
              </p>
            </div>

            {/* 입력 정보 요약 */}
            <div className="bg-white rounded-2xl p-6 shadow-sm">
              <h3 className="font-semibold text-gray-900 mb-4">
                생성될 적금 정보
              </h3>
              <div className="space-y-4">
                <div className="flex justify-between">
                  <span className="text-gray-600">적금명</span>
                  <span className="font-semibold">{formData.goalName}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-600">목적</span>
                  <span className="font-semibold">
                    {categories.find((c) => c.id === formData.category)?.name}
                  </span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-600">목표 금액</span>
                  <span className="font-semibold text-blue-600">
                    {parseInt(formData.targetAmount || 0).toLocaleString()}원
                  </span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-600">월 납입액</span>
                  <span className="font-semibold text-purple-600">
                    {parseInt(formData.monthlyTarget || 0).toLocaleString()}원
                  </span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-600">목표 달성일</span>
                  <span className="font-semibold">{formData.endDate}</span>
                </div>
              </div>
            </div>

            <div className="bg-white rounded-2xl p-6 shadow-sm">
              <h3 className="font-semibold text-gray-900 mb-4">
                자동이체 설정
              </h3>
              <div className="space-y-4">
                <div className="flex justify-between">
                  <span className="text-gray-600">출금 유형</span>
                  <span className="font-semibold">
                    {formData.sourceType === "GROUP_ACCOUNT"
                      ? "모임통장"
                      : "개인계좌"}
                  </span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-600">출금 계좌</span>
                  <span className="font-semibold">
                    {formData.sourceType === "GROUP_ACCOUNT"
                      ? groupAccounts.find(
                          (acc) => acc.id === formData.sourceAccountId
                        )?.name
                      : availableAccounts.find(
                          (acc) => acc.id === formData.sourceAccountId
                        )?.name}
                  </span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-600">자동이체일</span>
                  <span className="font-semibold">
                    매월 {formData.autoTransferDay}일
                  </span>
                </div>
              </div>
            </div>

            <div className="bg-emerald-50 rounded-2xl p-6 border border-emerald-200">
              <div className="flex items-center space-x-3 mb-3">
                <PiggyBank className="w-5 h-5 text-emerald-600" />
                <h4 className="font-semibold text-emerald-900">예상 혜택</h4>
              </div>
              <div className="space-y-2 text-sm">
                <div className="flex justify-between">
                  <span className="text-emerald-700">예상 적립액</span>
                  <span className="font-semibold text-emerald-900">
                    {parseInt(formData.targetAmount || 0).toLocaleString()}원
                  </span>
                </div>
                <div className="flex justify-between">
                  <span className="text-emerald-700">예상 이자 (연 4.2%)</span>
                  <span className="font-semibold text-emerald-900">
                    약{" "}
                    {Math.round(
                      parseInt(formData.targetAmount || 0) * 0.042
                    ).toLocaleString()}
                    원
                  </span>
                </div>
              </div>
            </div>
          </>
        )}

        {/* 버튼 영역 */}
        <div className="sticky bottom-4">
          {currentStep < 3 ? (
            <button
              onClick={handleNextStep}
              disabled={
                (currentStep === 1 && !validateStep1()) ||
                (currentStep === 2 && !validateStep2())
              }
              className={`w-full py-4 rounded-2xl font-semibold text-lg transition-all duration-300 flex items-center justify-center ${
                (currentStep === 1 && validateStep1()) ||
                (currentStep === 2 && validateStep2())
                  ? "bg-gradient-to-r from-blue-500 to-purple-500 text-white hover:from-blue-600 hover:to-purple-600 shadow-lg"
                  : "bg-gray-200 text-gray-500 cursor-not-allowed"
              }`}
            >
              <span>다음 단계</span>
              <ArrowRight className="w-5 h-5 ml-2" />
            </button>
          ) : (
            <button
              onClick={handleSubmit}
              disabled={isLoading}
              className="w-full py-4 rounded-2xl font-semibold text-lg transition-all duration-300 flex items-center justify-center bg-gradient-to-r from-emerald-500 to-teal-500 text-white hover:from-emerald-600 hover:to-teal-600 shadow-lg"
            >
              {isLoading ? (
                <>
                  <div className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin mr-2" />
                  적금 생성 중...
                </>
              ) : (
                <>
                  <CheckCircle className="w-5 h-5 mr-2" />
                  함께 적금 생성하기
                </>
              )}
            </button>
          )}
        </div>
      </div>
    </div>
  );
}
