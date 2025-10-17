"use client";

import { useState, useEffect } from "react";
import {
  ArrowLeft,
  ArrowRight,
  Check,
  PiggyBank,
  Target,
  Calendar,
  CreditCard,
  Users,
  Shield,
  TrendingUp,
  Home,
  Baby,
  GraduationCap,
  Plane,
  Heart,
  Gift,
  Star,
  Crown,
  Award,
  ChevronRight,
  Info,
  Calculator,
  Banknote,
  Clock,
} from "lucide-react";
import { useAuth } from "../../../contexts/AuthContext";

// API Base URL - 환경변수 사용
const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080/api";

export function HanaSavingsSubscription({ onComplete, onCancel }) {
  const { user } = useAuth();
  const [currentStep, setCurrentStep] = useState(1);
  const [isLoading, setIsLoading] = useState(false);
  const [groupAccounts, setGroupAccounts] = useState([]);

  // 적금 가입 정보
  const [subscriptionData, setSubscriptionData] = useState({
    // 1단계: 적금 상품 선택
    selectedProduct: null,

    // 2단계: 가입 조건 설정
    goalName: "",
    description: "",
    targetAmount: "",
    monthlyAmount: "",
    termMonths: 12,
    category: "PURPOSE",

    // 3단계: 출금계좌 설정
    withdrawalType: "", // "GROUP_ACCOUNT" or "PERSONAL_ACCOUNT"
    selectedGroupAccount: null,
    personalAccountNumber: "",
    personalBankName: "",
    autoTransferDay: 25,

    // 4단계: 약관 동의
    agreements: {
      terms: false,
      privacy: false,
      marketing: false,
      autoTransfer: false,
    },
  });

  // 하나은행 적금 상품 목록
  const savingsProducts = [
    {
      id: 1,
      name: "하나Future 적금",
      description: "미래를 위한 체계적인 저축",
      baseRate: 3.2,
      maxRate: 4.5,
      minAmount: 10000,
      maxAmount: 50000000,
      minTerm: 6,
      maxTerm: 36,
      features: ["우대금리 최대 1.3%p", "자유납입 가능", "중도해지 가능"],
      icon: "/hana3dIcon/hanaIcon3d_89.png",
      color: "from-emerald-400 to-emerald-600",
      popular: true,
    },
    {
      id: 2,
      name: "청년도약계좌",
      description: "청년층을 위한 특별 적금",
      baseRate: 4.0,
      maxRate: 5.0,
      minAmount: 100000,
      maxAmount: 700000,
      minTerm: 12,
      maxTerm: 60,
      features: ["정부 매칭 지원", "세제 혜택", "청년 전용"],
      icon: "/hana3dIcon/hanaIcon3d_6_89.png",
      color: "from-blue-400 to-blue-600",
      targetAge: "만 19~34세",
    },
    {
      id: 3,
      name: "주택청약종합저축",
      description: "내 집 마련의 첫걸음",
      baseRate: 1.8,
      maxRate: 2.5,
      minAmount: 20000,
      maxAmount: 500000,
      minTerm: 6,
      maxTerm: 240,
      features: ["청약 가점", "소득공제", "무주택자 우대"],
      icon: "/hana3dIcon/hanaIcon3d_5_89.png",
      color: "from-orange-400 to-orange-600",
    },
    {
      id: 4,
      name: "목적별 적금",
      description: "꿈을 위한 맞춤형 저축",
      baseRate: 2.8,
      maxRate: 3.8,
      minAmount: 50000,
      maxAmount: 30000000,
      minTerm: 12,
      maxTerm: 60,
      features: ["목적별 관리", "달성 알림", "보상 혜택"],
      icon: "/hana3dIcon/hanaIcon3d_4_89.png",
      color: "from-purple-400 to-purple-600",
    },
  ];

  // 적금 카테고리
  const categories = [
    { id: "PURPOSE", name: "목적 저축", icon: Target, emoji: "🎯" },
    { id: "HOUSING", name: "주택 마련", icon: Home, emoji: "🏠" },
    { id: "EDUCATION", name: "교육비", icon: GraduationCap, emoji: "🎓" },
    { id: "WEDDING", name: "결혼 자금", icon: Heart, emoji: "💒" },
    { id: "TRAVEL", name: "여행 자금", icon: Plane, emoji: "✈️" },
    { id: "BIRTH_PREPARATION", name: "출산 준비", icon: Baby, emoji: "👶" },
    { id: "RETIREMENT", name: "노후 준비", icon: Shield, emoji: "🛡️" },
    { id: "EMERGENCY", name: "비상 자금", icon: Star, emoji: "⭐" },
  ];

  // 컴포넌트 마운트 시 모임통장 목록 조회
  useEffect(() => {
    fetchGroupAccounts();
  }, []);

  const fetchGroupAccounts = async () => {
    try {
      const response = await fetch(
        `${API_BASE_URL}/group-accounts/user/${user?.id || 1}`
      );
      if (response.ok) {
        const result = await response.json();
        setGroupAccounts(result.data || []);
      }
    } catch (error) {
      console.error("모임통장 목록 조회 오류:", error);
    }
  };

  const handleInputChange = (field, value) => {
    setSubscriptionData((prev) => ({
      ...prev,
      [field]: value,
    }));
  };

  const handleAgreementChange = (field, checked) => {
    setSubscriptionData((prev) => ({
      ...prev,
      agreements: {
        ...prev.agreements,
        [field]: checked,
      },
    }));
  };

  const nextStep = () => {
    if (currentStep < 5) {
      setCurrentStep((prev) => prev + 1);
    } else {
      handleSubscription();
    }
  };

  const prevStep = () => {
    if (currentStep > 1) {
      setCurrentStep((prev) => prev - 1);
    }
  };

  const canProceed = () => {
    switch (currentStep) {
      case 1:
        return subscriptionData.selectedProduct !== null;
      case 2:
        return (
          subscriptionData.goalName &&
          subscriptionData.targetAmount &&
          subscriptionData.monthlyAmount
        );
      case 3:
        return (
          subscriptionData.withdrawalType &&
          (subscriptionData.withdrawalType === "GROUP_ACCOUNT"
            ? subscriptionData.selectedGroupAccount
            : subscriptionData.personalAccountNumber &&
              subscriptionData.personalBankName)
        );
      case 4:
        return Object.values(subscriptionData.agreements).every(
          (agreed) => agreed
        );
      default:
        return true;
    }
  };

  const handleSubscription = async () => {
    setIsLoading(true);
    try {
      const requestData = {
        userId: user?.id || 1,
        goalName: subscriptionData.goalName,
        description: subscriptionData.description,
        targetAmount: parseInt(subscriptionData.targetAmount),
        monthlyTarget: parseInt(subscriptionData.monthlyAmount),
        category: subscriptionData.category,
        startDate: new Date().toISOString().split("T")[0],
        endDate: new Date(
          Date.now() + subscriptionData.termMonths * 30 * 24 * 60 * 60 * 1000
        )
          .toISOString()
          .split("T")[0],
        interestRate: subscriptionData.selectedProduct.maxRate,
        sourceType: subscriptionData.withdrawalType,
        sourceAccountId:
          subscriptionData.withdrawalType === "GROUP_ACCOUNT"
            ? subscriptionData.selectedGroupAccount?.id?.toString()
            : subscriptionData.personalAccountNumber,
        autoTransferDay: subscriptionData.autoTransferDay,
      };

      const response = await fetch(`${API_BASE_URL}/savings/goals`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          ...(user?.token && { Authorization: `Bearer ${user.token}` }),
        },
        body: JSON.stringify(requestData),
      });

      if (response.ok) {
        const result = await response.json();
        setCurrentStep(5); // 완료 단계로 이동
        setTimeout(() => {
          onComplete && onComplete(result.data);
        }, 2000);
      } else {
        throw new Error("적금 가입에 실패했습니다.");
      }
    } catch (error) {
      console.error("적금 가입 오류:", error);
      alert("적금 가입 중 오류가 발생했습니다.");
    } finally {
      setIsLoading(false);
    }
  };

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat("ko-KR").format(amount);
  };

  const calculateExpectedAmount = () => {
    if (
      !subscriptionData.monthlyAmount ||
      !subscriptionData.termMonths ||
      !subscriptionData.selectedProduct
    ) {
      return 0;
    }

    const monthly = parseInt(subscriptionData.monthlyAmount);
    const principal = monthly * subscriptionData.termMonths;
    const rate = subscriptionData.selectedProduct.maxRate / 100 / 12;
    const interest = (principal * rate * subscriptionData.termMonths) / 2; // 단순 계산

    return principal + interest;
  };

  // 단계별 렌더링
  const renderStep = () => {
    switch (currentStep) {
      case 1:
        return renderProductSelection();
      case 2:
        return renderSubscriptionDetails();
      case 3:
        return renderWithdrawalAccount();
      case 4:
        return renderAgreements();
      case 5:
        return renderCompletion();
      default:
        return null;
    }
  };

  // 1단계: 적금 상품 선택
  const renderProductSelection = () => (
    <div className="space-y-6">
      <div className="text-center">
        <div className="w-20 h-20 mx-auto mb-4 bg-gradient-to-br from-emerald-400 to-emerald-600 rounded-2xl flex items-center justify-center">
          <PiggyBank className="w-10 h-10 text-white" />
        </div>
        <h2 className="text-2xl font-bold text-gray-900 mb-2">
          적금 상품 선택
        </h2>
        <p className="text-gray-600">목적에 맞는 적금 상품을 선택해주세요</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {savingsProducts.map((product) => (
          <div
            key={product.id}
            onClick={() => handleInputChange("selectedProduct", product)}
            className={`relative p-6 rounded-2xl border-2 cursor-pointer transition-all duration-300 ${
              subscriptionData.selectedProduct?.id === product.id
                ? "border-emerald-500 bg-emerald-50 shadow-lg"
                : "border-gray-200 hover:border-emerald-300 hover:shadow-md"
            }`}
          >
            {product.popular && (
              <div className="absolute -top-2 -right-2 bg-red-500 text-white text-xs px-2 py-1 rounded-full">
                인기
              </div>
            )}

            <div className="flex items-start gap-4">
              <div
                className={`w-16 h-16 rounded-xl bg-gradient-to-br ${product.color} flex items-center justify-center flex-shrink-0`}
              >
                <img
                  src={product.icon}
                  alt={product.name}
                  className="w-10 h-10"
                />
              </div>

              <div className="flex-1">
                <h3 className="font-bold text-lg text-gray-900 mb-1">
                  {product.name}
                </h3>
                <p className="text-gray-600 text-sm mb-3">
                  {product.description}
                </p>

                <div className="space-y-2">
                  <div className="flex justify-between text-sm">
                    <span className="text-gray-500">기본금리</span>
                    <span className="font-semibold text-emerald-600">
                      {product.baseRate}%
                    </span>
                  </div>
                  <div className="flex justify-between text-sm">
                    <span className="text-gray-500">최대금리</span>
                    <span className="font-bold text-emerald-600">
                      {product.maxRate}%
                    </span>
                  </div>
                  <div className="flex justify-between text-sm">
                    <span className="text-gray-500">가입기간</span>
                    <span className="text-gray-700">
                      {product.minTerm}~{product.maxTerm}개월
                    </span>
                  </div>
                </div>

                <div className="mt-3 flex flex-wrap gap-1">
                  {product.features.map((feature, index) => (
                    <span
                      key={index}
                      className="text-xs bg-gray-100 text-gray-600 px-2 py-1 rounded"
                    >
                      {feature}
                    </span>
                  ))}
                </div>

                {product.targetAge && (
                  <div className="mt-2 text-xs text-blue-600 font-medium">
                    {product.targetAge}
                  </div>
                )}
              </div>
            </div>

            {subscriptionData.selectedProduct?.id === product.id && (
              <div className="absolute top-4 right-4">
                <div className="w-6 h-6 bg-emerald-500 rounded-full flex items-center justify-center">
                  <Check className="w-4 h-4 text-white" />
                </div>
              </div>
            )}
          </div>
        ))}
      </div>
    </div>
  );

  // 2단계: 가입 조건 설정
  const renderSubscriptionDetails = () => (
    <div className="space-y-6">
      <div className="text-center">
        <div className="w-20 h-20 mx-auto mb-4 bg-gradient-to-br from-blue-400 to-blue-600 rounded-2xl flex items-center justify-center">
          <Target className="w-10 h-10 text-white" />
        </div>
        <h2 className="text-2xl font-bold text-gray-900 mb-2">
          적금 조건 설정
        </h2>
        <p className="text-gray-600">목표와 납입 조건을 설정해주세요</p>
      </div>

      <div className="bg-white rounded-2xl p-6 border border-gray-200">
        <div className="space-y-6">
          {/* 적금 이름 */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              적금 이름 <span className="text-red-500">*</span>
            </label>
            <input
              type="text"
              value={subscriptionData.goalName}
              onChange={(e) => handleInputChange("goalName", e.target.value)}
              placeholder="예: 내집마련 적금, 여행자금 모으기"
              className="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-emerald-500 focus:border-emerald-500"
            />
          </div>

          {/* 설명 */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              설명 (선택)
            </label>
            <textarea
              value={subscriptionData.description}
              onChange={(e) => handleInputChange("description", e.target.value)}
              placeholder="적금의 목적이나 설명을 입력해주세요"
              rows={3}
              className="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-emerald-500 focus:border-emerald-500"
            />
          </div>

          {/* 카테고리 선택 */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-3">
              적금 목적
            </label>
            <div className="grid grid-cols-2 md:grid-cols-4 gap-3">
              {categories.map((category) => (
                <button
                  key={category.id}
                  onClick={() => handleInputChange("category", category.id)}
                  className={`p-3 rounded-xl border-2 transition-all duration-200 ${
                    subscriptionData.category === category.id
                      ? "border-emerald-500 bg-emerald-50"
                      : "border-gray-200 hover:border-emerald-300"
                  }`}
                >
                  <div className="text-2xl mb-1">{category.emoji}</div>
                  <div className="text-xs font-medium text-gray-700">
                    {category.name}
                  </div>
                </button>
              ))}
            </div>
          </div>

          {/* 목표 금액 */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              목표 금액 <span className="text-red-500">*</span>
            </label>
            <div className="relative">
              <input
                type="number"
                value={subscriptionData.targetAmount}
                onChange={(e) =>
                  handleInputChange("targetAmount", e.target.value)
                }
                placeholder="10000000"
                className="w-full px-4 py-3 pr-12 border border-gray-300 rounded-xl focus:ring-2 focus:ring-emerald-500 focus:border-emerald-500"
              />
              <span className="absolute right-4 top-1/2 -translate-y-1/2 text-gray-500">
                원
              </span>
            </div>
            {subscriptionData.targetAmount && (
              <p className="text-sm text-gray-500 mt-1">
                {formatCurrency(subscriptionData.targetAmount)}원
              </p>
            )}
          </div>

          {/* 월 납입 금액 */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              월 납입 금액 <span className="text-red-500">*</span>
            </label>
            <div className="relative">
              <input
                type="number"
                value={subscriptionData.monthlyAmount}
                onChange={(e) =>
                  handleInputChange("monthlyAmount", e.target.value)
                }
                placeholder="500000"
                className="w-full px-4 py-3 pr-12 border border-gray-300 rounded-xl focus:ring-2 focus:ring-emerald-500 focus:border-emerald-500"
              />
              <span className="absolute right-4 top-1/2 -translate-y-1/2 text-gray-500">
                원
              </span>
            </div>
            {subscriptionData.monthlyAmount && (
              <p className="text-sm text-gray-500 mt-1">
                매월 {formatCurrency(subscriptionData.monthlyAmount)}원 납입
              </p>
            )}
          </div>

          {/* 가입 기간 */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              가입 기간
            </label>
            <div className="flex gap-3">
              {[6, 12, 18, 24, 36].map((months) => (
                <button
                  key={months}
                  onClick={() => handleInputChange("termMonths", months)}
                  className={`flex-1 py-3 px-4 rounded-xl border-2 transition-all duration-200 ${
                    subscriptionData.termMonths === months
                      ? "border-emerald-500 bg-emerald-50 text-emerald-700"
                      : "border-gray-200 hover:border-emerald-300"
                  }`}
                >
                  <div className="font-semibold">{months}개월</div>
                  <div className="text-xs text-gray-500">
                    {Math.floor(months / 12)}년 {months % 12}개월
                  </div>
                </button>
              ))}
            </div>
          </div>

          {/* 예상 만기 금액 */}
          {subscriptionData.monthlyAmount &&
            subscriptionData.selectedProduct && (
              <div className="bg-emerald-50 rounded-xl p-4">
                <div className="flex items-center gap-2 mb-2">
                  <Calculator className="w-5 h-5 text-emerald-600" />
                  <span className="font-medium text-emerald-800">
                    예상 만기 금액
                  </span>
                </div>
                <div className="text-2xl font-bold text-emerald-700">
                  {formatCurrency(calculateExpectedAmount())}원
                </div>
                <div className="text-sm text-emerald-600 mt-1">
                  원금{" "}
                  {formatCurrency(
                    parseInt(subscriptionData.monthlyAmount) *
                      subscriptionData.termMonths
                  )}
                  원 + 예상 이자{" "}
                  {formatCurrency(
                    calculateExpectedAmount() -
                      parseInt(subscriptionData.monthlyAmount) *
                        subscriptionData.termMonths
                  )}
                  원
                </div>
              </div>
            )}
        </div>
      </div>
    </div>
  );

  // 3단계: 출금계좌 설정
  const renderWithdrawalAccount = () => (
    <div className="space-y-6">
      <div className="text-center">
        <div className="w-20 h-20 mx-auto mb-4 bg-gradient-to-br from-purple-400 to-purple-600 rounded-2xl flex items-center justify-center">
          <CreditCard className="w-10 h-10 text-white" />
        </div>
        <h2 className="text-2xl font-bold text-gray-900 mb-2">출금계좌 설정</h2>
        <p className="text-gray-600">매월 자동이체할 출금계좌를 선택해주세요</p>
      </div>

      <div className="space-y-4">
        {/* 모임통장 선택 */}
        <div
          className={`border-2 rounded-2xl p-6 cursor-pointer transition-all duration-300 ${
            subscriptionData.withdrawalType === "GROUP_ACCOUNT"
              ? "border-emerald-500 bg-emerald-50"
              : "border-gray-200 hover:border-emerald-300"
          }`}
          onClick={() => handleInputChange("withdrawalType", "GROUP_ACCOUNT")}
        >
          <div className="flex items-center gap-4">
            <div className="w-12 h-12 bg-gradient-to-br from-emerald-400 to-emerald-600 rounded-xl flex items-center justify-center">
              <Users className="w-6 h-6 text-white" />
            </div>
            <div className="flex-1">
              <h3 className="font-bold text-lg text-gray-900">
                모임통장에서 출금
              </h3>
              <p className="text-gray-600 text-sm">
                등록된 모임통장에서 자동이체
              </p>
            </div>
            {subscriptionData.withdrawalType === "GROUP_ACCOUNT" && (
              <Check className="w-6 h-6 text-emerald-500" />
            )}
          </div>

          {subscriptionData.withdrawalType === "GROUP_ACCOUNT" && (
            <div className="mt-4 space-y-3">
              {groupAccounts.length > 0 ? (
                groupAccounts.map((account) => (
                  <div
                    key={account.id}
                    onClick={(e) => {
                      e.stopPropagation();
                      handleInputChange("selectedGroupAccount", account);
                    }}
                    className={`p-4 rounded-xl border cursor-pointer transition-all duration-200 ${
                      subscriptionData.selectedGroupAccount?.id === account.id
                        ? "border-emerald-500 bg-white"
                        : "border-gray-200 hover:border-emerald-300 bg-gray-50"
                    }`}
                  >
                    <div className="flex justify-between items-center">
                      <div>
                        <div className="font-medium text-gray-900">
                          {account.groupAccountName}
                        </div>
                        <div className="text-sm text-gray-500">
                          {account.groupAccountNumber}
                        </div>
                        <div className="text-sm text-emerald-600 font-medium">
                          잔액: {formatCurrency(account.currentBalance || 0)}원
                        </div>
                      </div>
                      {subscriptionData.selectedGroupAccount?.id ===
                        account.id && (
                        <Check className="w-5 h-5 text-emerald-500" />
                      )}
                    </div>
                  </div>
                ))
              ) : (
                <div className="text-center py-4 text-gray-500">
                  등록된 모임통장이 없습니다.
                </div>
              )}
            </div>
          )}
        </div>

        {/* 개인계좌 선택 */}
        <div
          className={`border-2 rounded-2xl p-6 cursor-pointer transition-all duration-300 ${
            subscriptionData.withdrawalType === "PERSONAL_ACCOUNT"
              ? "border-emerald-500 bg-emerald-50"
              : "border-gray-200 hover:border-emerald-300"
          }`}
          onClick={() =>
            handleInputChange("withdrawalType", "PERSONAL_ACCOUNT")
          }
        >
          <div className="flex items-center gap-4">
            <div className="w-12 h-12 bg-gradient-to-br from-blue-400 to-blue-600 rounded-xl flex items-center justify-center">
              <Banknote className="w-6 h-6 text-white" />
            </div>
            <div className="flex-1">
              <h3 className="font-bold text-lg text-gray-900">
                개인계좌에서 출금
              </h3>
              <p className="text-gray-600 text-sm">
                다른 은행 계좌에서 자동이체
              </p>
            </div>
            {subscriptionData.withdrawalType === "PERSONAL_ACCOUNT" && (
              <Check className="w-6 h-6 text-emerald-500" />
            )}
          </div>

          {subscriptionData.withdrawalType === "PERSONAL_ACCOUNT" && (
            <div className="mt-4 space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  계좌번호 <span className="text-red-500">*</span>
                </label>
                <input
                  type="text"
                  value={subscriptionData.personalAccountNumber}
                  onChange={(e) =>
                    handleInputChange("personalAccountNumber", e.target.value)
                  }
                  placeholder="123-456-789012"
                  className="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-emerald-500 focus:border-emerald-500"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  은행명 <span className="text-red-500">*</span>
                </label>
                <select
                  value={subscriptionData.personalBankName}
                  onChange={(e) =>
                    handleInputChange("personalBankName", e.target.value)
                  }
                  className="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-emerald-500 focus:border-emerald-500"
                >
                  <option value="">은행을 선택해주세요</option>
                  <option value="하나은행">하나은행</option>
                  <option value="국민은행">국민은행</option>
                  <option value="신한은행">신한은행</option>
                  <option value="우리은행">우리은행</option>
                  <option value="IBK기업은행">IBK기업은행</option>
                  <option value="NH농협은행">NH농협은행</option>
                  <option value="카카오뱅크">카카오뱅크</option>
                  <option value="토스뱅크">토스뱅크</option>
                </select>
              </div>
            </div>
          )}
        </div>

        {/* 자동이체일 설정 */}
        <div className="bg-gray-50 rounded-2xl p-6">
          <div className="flex items-center gap-2 mb-4">
            <Clock className="w-5 h-5 text-gray-600" />
            <span className="font-medium text-gray-800">자동이체일 설정</span>
          </div>
          <div className="grid grid-cols-5 gap-2">
            {[5, 10, 15, 20, 25].map((day) => (
              <button
                key={day}
                onClick={() => handleInputChange("autoTransferDay", day)}
                className={`py-2 px-3 rounded-lg transition-all duration-200 ${
                  subscriptionData.autoTransferDay === day
                    ? "bg-emerald-500 text-white"
                    : "bg-white border border-gray-200 hover:border-emerald-300"
                }`}
              >
                매월 {day}일
              </button>
            ))}
          </div>
          <p className="text-sm text-gray-500 mt-2">
            선택한 날짜에 매월 자동으로 이체됩니다.
          </p>
        </div>
      </div>
    </div>
  );

  // 4단계: 약관 동의
  const renderAgreements = () => (
    <div className="space-y-6">
      <div className="text-center">
        <div className="w-20 h-20 mx-auto mb-4 bg-gradient-to-br from-orange-400 to-orange-600 rounded-2xl flex items-center justify-center">
          <Shield className="w-10 h-10 text-white" />
        </div>
        <h2 className="text-2xl font-bold text-gray-900 mb-2">약관 동의</h2>
        <p className="text-gray-600">적금 가입을 위한 약관에 동의해주세요</p>
      </div>

      <div className="space-y-4">
        {[
          { key: "terms", title: "적금 상품 약관", required: true },
          { key: "privacy", title: "개인정보 수집·이용 동의", required: true },
          {
            key: "autoTransfer",
            title: "자동이체 서비스 약관",
            required: true,
          },
          { key: "marketing", title: "마케팅 정보 수신 동의", required: false },
        ].map((agreement) => (
          <div
            key={agreement.key}
            className="bg-white rounded-xl p-4 border border-gray-200"
          >
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-3">
                <input
                  type="checkbox"
                  id={agreement.key}
                  checked={subscriptionData.agreements[agreement.key]}
                  onChange={(e) =>
                    handleAgreementChange(agreement.key, e.target.checked)
                  }
                  className="w-5 h-5 text-emerald-600 border-gray-300 rounded focus:ring-emerald-500"
                />
                <label
                  htmlFor={agreement.key}
                  className="font-medium text-gray-900"
                >
                  {agreement.title}
                  {agreement.required && (
                    <span className="text-red-500 ml-1">*</span>
                  )}
                </label>
              </div>
              <button className="text-emerald-600 hover:text-emerald-700">
                <ChevronRight className="w-5 h-5" />
              </button>
            </div>
          </div>
        ))}
      </div>

      {/* 전체 동의 */}
      <div className="bg-emerald-50 rounded-xl p-4">
        <div className="flex items-center gap-3">
          <input
            type="checkbox"
            id="allAgree"
            checked={Object.values(subscriptionData.agreements).every(
              (agreed) => agreed
            )}
            onChange={(e) => {
              const allChecked = e.target.checked;
              Object.keys(subscriptionData.agreements).forEach((key) => {
                handleAgreementChange(key, allChecked);
              });
            }}
            className="w-5 h-5 text-emerald-600 border-gray-300 rounded focus:ring-emerald-500"
          />
          <label htmlFor="allAgree" className="font-bold text-emerald-800">
            전체 약관에 동의합니다
          </label>
        </div>
      </div>
    </div>
  );

  // 5단계: 완료
  const renderCompletion = () => (
    <div className="text-center space-y-6">
      <div className="w-24 h-24 mx-auto bg-gradient-to-br from-emerald-400 to-emerald-600 rounded-full flex items-center justify-center">
        <Check className="w-12 h-12 text-white" />
      </div>

      <div>
        <h2 className="text-3xl font-bold text-gray-900 mb-2">
          적금 가입 완료!
        </h2>
        <p className="text-gray-600 text-lg">
          {subscriptionData.goalName} 적금이 성공적으로 개설되었습니다.
        </p>
      </div>

      <div className="bg-white rounded-2xl p-6 border border-gray-200 space-y-4">
        <div className="flex justify-between">
          <span className="text-gray-600">적금 상품</span>
          <span className="font-semibold">
            {subscriptionData.selectedProduct?.name}
          </span>
        </div>
        <div className="flex justify-between">
          <span className="text-gray-600">목표 금액</span>
          <span className="font-semibold">
            {formatCurrency(subscriptionData.targetAmount)}원
          </span>
        </div>
        <div className="flex justify-between">
          <span className="text-gray-600">월 납입액</span>
          <span className="font-semibold">
            {formatCurrency(subscriptionData.monthlyAmount)}원
          </span>
        </div>
        <div className="flex justify-between">
          <span className="text-gray-600">자동이체일</span>
          <span className="font-semibold">
            매월 {subscriptionData.autoTransferDay}일
          </span>
        </div>
        <div className="flex justify-between">
          <span className="text-gray-600">출금계좌</span>
          <span className="font-semibold">
            {subscriptionData.withdrawalType === "GROUP_ACCOUNT"
              ? subscriptionData.selectedGroupAccount?.groupAccountName
              : `${subscriptionData.personalBankName} ${subscriptionData.personalAccountNumber}`}
          </span>
        </div>
      </div>

      <div className="flex items-center justify-center gap-2 text-emerald-600">
        <img
          src="/hanacharacter/hanacharacter1.png"
          alt="하나캐릭터"
          className="w-12 h-12"
        />
        <span className="font-medium">
          첫 자동이체는 다음 달 {subscriptionData.autoTransferDay}일에
          시작됩니다!
        </span>
      </div>
    </div>
  );

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 via-white to-emerald-50">
      <div className="max-w-4xl mx-auto px-4 py-8">
        {/* 헤더 */}
        <div className="flex items-center justify-between mb-8">
          <button
            onClick={currentStep === 1 ? onCancel : prevStep}
            className="flex items-center gap-2 text-gray-600 hover:text-gray-800 transition-colors"
          >
            <ArrowLeft className="w-5 h-5" />
            {currentStep === 1 ? "취소" : "이전"}
          </button>

          <div className="flex items-center gap-2">
            <img
              src="/bank-logos/HanaLogo.png"
              alt="하나Future"
              className="w-8 h-8"
            />
            <span className="font-bold text-xl text-gray-900">
              하나Future 적금
            </span>
          </div>

          <div className="text-sm text-gray-500">{currentStep}/4 단계</div>
        </div>

        {/* 진행 상태 바 */}
        {currentStep < 5 && (
          <div className="mb-8">
            <div className="flex justify-between mb-2">
              {["상품선택", "조건설정", "출금계좌", "약관동의"].map(
                (step, index) => (
                  <div
                    key={index}
                    className={`text-sm font-medium ${
                      index + 1 <= currentStep
                        ? "text-emerald-600"
                        : "text-gray-400"
                    }`}
                  >
                    {step}
                  </div>
                )
              )}
            </div>
            <div className="w-full bg-gray-200 rounded-full h-2">
              <div
                className="bg-emerald-500 h-2 rounded-full transition-all duration-500"
                style={{ width: `${(currentStep / 4) * 100}%` }}
              />
            </div>
          </div>
        )}

        {/* 단계별 콘텐츠 */}
        <div className="bg-white rounded-2xl shadow-lg border border-gray-100 p-8">
          {renderStep()}
        </div>

        {/* 하단 버튼 */}
        {currentStep < 5 && (
          <div className="mt-8 flex gap-4">
            {currentStep > 1 && (
              <button
                onClick={prevStep}
                className="flex-1 py-4 px-6 border border-gray-300 text-gray-700 rounded-xl font-semibold hover:bg-gray-50 transition-colors"
              >
                이전
              </button>
            )}
            <button
              onClick={nextStep}
              disabled={!canProceed() || isLoading}
              className={`flex-1 py-4 px-6 rounded-xl font-semibold transition-colors flex items-center justify-center gap-2 ${
                canProceed() && !isLoading
                  ? "bg-emerald-500 text-white hover:bg-emerald-600"
                  : "bg-gray-300 text-gray-500 cursor-not-allowed"
              }`}
            >
              {isLoading ? (
                <>
                  <div className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin" />
                  처리중...
                </>
              ) : currentStep === 4 ? (
                "가입하기"
              ) : (
                <>
                  다음
                  <ArrowRight className="w-5 h-5" />
                </>
              )}
            </button>
          </div>
        )}
      </div>
    </div>
  );
}
