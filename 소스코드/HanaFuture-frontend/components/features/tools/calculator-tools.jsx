"use client";

import React, { useState, useEffect } from "react";
import {
  Calculator,
  PiggyBank,
  Home,
  TrendingUp,
  DollarSign,
} from "lucide-react";

// 대출 계산기
export function LoanCalculator() {
  const [loanAmount, setLoanAmount] = useState(100000000); // 1억원
  const [interestRate, setInterestRate] = useState(3.5); // 3.5%
  const [loanTerm, setLoanTerm] = useState(30); // 30년
  const [result, setResult] = useState(null);

  useEffect(() => {
    calculateLoan();
  }, [loanAmount, interestRate, loanTerm]);

  const calculateLoan = () => {
    const monthlyRate = interestRate / 100 / 12;
    const totalMonths = loanTerm * 12;

    if (monthlyRate === 0) {
      const monthlyPayment = loanAmount / totalMonths;
      setResult({
        monthlyPayment,
        totalPayment: loanAmount,
        totalInterest: 0,
      });
      return;
    }

    const monthlyPayment =
      (loanAmount * (monthlyRate * Math.pow(1 + monthlyRate, totalMonths))) /
      (Math.pow(1 + monthlyRate, totalMonths) - 1);
    const totalPayment = monthlyPayment * totalMonths;
    const totalInterest = totalPayment - loanAmount;

    setResult({
      monthlyPayment,
      totalPayment,
      totalInterest,
    });
  };

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat("ko-KR").format(Math.round(amount));
  };

  return (
    <div className="bg-white dark:bg-gray-800 rounded-xl p-6 border border-border">
      <div className="flex items-center gap-3 mb-6">
        <div className="p-2 bg-blue-100 rounded-lg">
          <Home className="h-6 w-6 text-blue-600" />
        </div>
        <h3 className="text-xl font-bold">주택담보대출 계산기</h3>
      </div>

      <div className="space-y-6">
        {/* 입력 필드들 */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div>
            <label className="block text-sm font-medium mb-2">
              대출금액 (원)
            </label>
            <input
              type="number"
              value={loanAmount}
              onChange={(e) => setLoanAmount(Number(e.target.value))}
              className="w-full p-3 border border-border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50"
              min="0"
              step="1000000"
            />
          </div>

          <div>
            <label className="block text-sm font-medium mb-2">연이율 (%)</label>
            <input
              type="number"
              value={interestRate}
              onChange={(e) => setInterestRate(Number(e.target.value))}
              className="w-full p-3 border border-border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50"
              min="0"
              max="20"
              step="0.1"
            />
          </div>

          <div>
            <label className="block text-sm font-medium mb-2">
              대출기간 (년)
            </label>
            <input
              type="number"
              value={loanTerm}
              onChange={(e) => setLoanTerm(Number(e.target.value))}
              className="w-full p-3 border border-border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50"
              min="1"
              max="50"
            />
          </div>
        </div>

        {/* 결과 표시 */}
        {result && (
          <div className="bg-muted/30 rounded-lg p-6">
            <h4 className="font-bold text-lg mb-4">계산 결과</h4>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div className="text-center">
                <p className="text-sm text-muted-foreground mb-1">월 상환액</p>
                <p className="text-2xl font-bold text-primary">
                  ₩{formatCurrency(result.monthlyPayment)}
                </p>
              </div>
              <div className="text-center">
                <p className="text-sm text-muted-foreground mb-1">총 상환액</p>
                <p className="text-2xl font-bold text-foreground">
                  ₩{formatCurrency(result.totalPayment)}
                </p>
              </div>
              <div className="text-center">
                <p className="text-sm text-muted-foreground mb-1">총 이자</p>
                <p className="text-2xl font-bold text-red-600">
                  ₩{formatCurrency(result.totalInterest)}
                </p>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

// 적금 계산기
export function SavingsCalculator() {
  const [monthlyAmount, setMonthlyAmount] = useState(500000); // 50만원
  const [interestRate, setInterestRate] = useState(4.0); // 4.0%
  const [savingsPeriod, setSavingsPeriod] = useState(36); // 36개월
  const [result, setResult] = useState(null);

  useEffect(() => {
    calculateSavings();
  }, [monthlyAmount, interestRate, savingsPeriod]);

  const calculateSavings = () => {
    const monthlyRate = interestRate / 100 / 12;
    const totalDeposit = monthlyAmount * savingsPeriod;

    // 복리 계산
    let totalAmount = 0;
    for (let i = 1; i <= savingsPeriod; i++) {
      totalAmount +=
        monthlyAmount * Math.pow(1 + monthlyRate, savingsPeriod - i + 1);
    }

    const totalInterest = totalAmount - totalDeposit;

    setResult({
      totalDeposit,
      totalAmount,
      totalInterest,
      effectiveRate: (totalInterest / totalDeposit) * 100,
    });
  };

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat("ko-KR").format(Math.round(amount));
  };

  return (
    <div className="bg-white dark:bg-gray-800 rounded-xl p-6 border border-border">
      <div className="flex items-center gap-3 mb-6">
        <div className="p-2 bg-green-100 rounded-lg">
          <PiggyBank className="h-6 w-6 text-green-600" />
        </div>
        <h3 className="text-xl font-bold">적금 계산기</h3>
      </div>

      <div className="space-y-6">
        {/* 입력 필드들 */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div>
            <label className="block text-sm font-medium mb-2">
              월 납입액 (원)
            </label>
            <input
              type="number"
              value={monthlyAmount}
              onChange={(e) => setMonthlyAmount(Number(e.target.value))}
              className="w-full p-3 border border-border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50"
              min="0"
              step="10000"
            />
          </div>

          <div>
            <label className="block text-sm font-medium mb-2">연이율 (%)</label>
            <input
              type="number"
              value={interestRate}
              onChange={(e) => setInterestRate(Number(e.target.value))}
              className="w-full p-3 border border-border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50"
              min="0"
              max="10"
              step="0.1"
            />
          </div>

          <div>
            <label className="block text-sm font-medium mb-2">
              적금기간 (개월)
            </label>
            <input
              type="number"
              value={savingsPeriod}
              onChange={(e) => setSavingsPeriod(Number(e.target.value))}
              className="w-full p-3 border border-border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50"
              min="1"
              max="120"
            />
          </div>
        </div>

        {/* 결과 표시 */}
        {result && (
          <div className="bg-muted/30 rounded-lg p-6">
            <h4 className="font-bold text-lg mb-4">계산 결과</h4>
            <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
              <div className="text-center">
                <p className="text-sm text-muted-foreground mb-1">총 납입액</p>
                <p className="text-xl font-bold text-foreground">
                  ₩{formatCurrency(result.totalDeposit)}
                </p>
              </div>
              <div className="text-center">
                <p className="text-sm text-muted-foreground mb-1">
                  만기 수령액
                </p>
                <p className="text-xl font-bold text-primary">
                  ₩{formatCurrency(result.totalAmount)}
                </p>
              </div>
              <div className="text-center">
                <p className="text-sm text-muted-foreground mb-1">이자 수익</p>
                <p className="text-xl font-bold text-green-600">
                  ₩{formatCurrency(result.totalInterest)}
                </p>
              </div>
              <div className="text-center">
                <p className="text-sm text-muted-foreground mb-1">수익률</p>
                <p className="text-xl font-bold text-blue-600">
                  {result.effectiveRate.toFixed(2)}%
                </p>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

// 육아비 계산기
export function ChildcareCalculator() {
  const [childAge, setChildAge] = useState(0); // 0-7세
  const [daycareCost, setDaycareCost] = useState(400000); // 어린이집비
  const [extraCosts, setExtraCosts] = useState({
    food: 200000, // 식비
    clothes: 100000, // 의류
    medical: 50000, // 의료비
    education: 150000, // 교육비
    others: 100000, // 기타
  });
  const [result, setResult] = useState(null);

  useEffect(() => {
    calculateChildcareCosts();
  }, [childAge, daycareCost, extraCosts]);

  const calculateChildcareCosts = () => {
    const totalMonthlyCost =
      daycareCost +
      Object.values(extraCosts).reduce((sum, cost) => sum + cost, 0);
    const totalYearlyCost = totalMonthlyCost * 12;

    // 연령별 예상 총 비용 (0세부터 현재 나이까지)
    let totalLifetimeCost = 0;
    for (let age = 0; age <= childAge; age++) {
      // 나이에 따른 비용 증가 반영
      const ageMultiplier = 1 + age * 0.1; // 매년 10%씩 증가
      totalLifetimeCost += totalYearlyCost * ageMultiplier;
    }

    setResult({
      monthlyTotal: totalMonthlyCost,
      yearlyTotal: totalYearlyCost,
      lifetimeTotal: totalLifetimeCost,
      breakdown: {
        daycare: daycareCost,
        ...extraCosts,
      },
    });
  };

  const updateExtraCost = (category, value) => {
    setExtraCosts((prev) => ({ ...prev, [category]: Number(value) }));
  };

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat("ko-KR").format(Math.round(amount));
  };

  const costCategories = [
    { key: "food", label: "식비", icon: "🍼" },
    { key: "clothes", label: "의류비", icon: "👶" },
    { key: "medical", label: "의료비", icon: "🏥" },
    { key: "education", label: "교육비", icon: "📚" },
    { key: "others", label: "기타", icon: "🎯" },
  ];

  return (
    <div className="bg-white dark:bg-gray-800 rounded-xl p-6 border border-border">
      <div className="flex items-center gap-3 mb-6">
        <div className="p-2 bg-pink-100 rounded-lg">
          <DollarSign className="h-6 w-6 text-pink-600" />
        </div>
        <h3 className="text-xl font-bold">육아비 계산기</h3>
      </div>

      <div className="space-y-6">
        {/* 기본 정보 */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium mb-2">자녀 나이</label>
            <select
              value={childAge}
              onChange={(e) => setChildAge(Number(e.target.value))}
              className="w-full p-3 border border-border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50"
            >
              {Array.from({ length: 8 }, (_, i) => (
                <option key={i} value={i}>
                  {i}세
                </option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium mb-2">
              어린이집/유치원비 (월)
            </label>
            <input
              type="number"
              value={daycareCost}
              onChange={(e) => setDaycareCost(Number(e.target.value))}
              className="w-full p-3 border border-border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50"
              min="0"
              step="10000"
            />
          </div>
        </div>

        {/* 추가 비용 항목들 */}
        <div>
          <h4 className="font-medium mb-4">월별 추가 비용</h4>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {costCategories.map((category) => (
              <div key={category.key}>
                <label className="block text-sm font-medium mb-2">
                  {category.icon} {category.label}
                </label>
                <input
                  type="number"
                  value={extraCosts[category.key]}
                  onChange={(e) =>
                    updateExtraCost(category.key, e.target.value)
                  }
                  className="w-full p-3 border border-border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50"
                  min="0"
                  step="10000"
                />
              </div>
            ))}
          </div>
        </div>

        {/* 결과 표시 */}
        {result && (
          <div className="bg-muted/30 rounded-lg p-6">
            <h4 className="font-bold text-lg mb-4">육아비 계산 결과</h4>

            {/* 총 비용 요약 */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
              <div className="text-center">
                <p className="text-sm text-muted-foreground mb-1">
                  월 총 육아비
                </p>
                <p className="text-2xl font-bold text-primary">
                  ₩{formatCurrency(result.monthlyTotal)}
                </p>
              </div>
              <div className="text-center">
                <p className="text-sm text-muted-foreground mb-1">
                  연 총 육아비
                </p>
                <p className="text-2xl font-bold text-foreground">
                  ₩{formatCurrency(result.yearlyTotal)}
                </p>
              </div>
              <div className="text-center">
                <p className="text-sm text-muted-foreground mb-1">
                  누적 육아비
                </p>
                <p className="text-2xl font-bold text-red-600">
                  ₩{formatCurrency(result.lifetimeTotal)}
                </p>
              </div>
            </div>

            {/* 비용 구성 */}
            <div>
              <h5 className="font-medium mb-3">월별 비용 구성</h5>
              <div className="space-y-2">
                <div className="flex justify-between items-center p-2 bg-background rounded">
                  <span>🏫 어린이집/유치원</span>
                  <span className="font-medium">
                    ₩{formatCurrency(result.breakdown.daycare)}
                  </span>
                </div>
                {costCategories.map((category) => (
                  <div
                    key={category.key}
                    className="flex justify-between items-center p-2 bg-background rounded"
                  >
                    <span>
                      {category.icon} {category.label}
                    </span>
                    <span className="font-medium">
                      ₩{formatCurrency(result.breakdown[category.key])}
                    </span>
                  </div>
                ))}
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

// 통합 계산기 컴포넌트
export function CalculatorTools() {
  const [activeCalculator, setActiveCalculator] = useState("loan");

  const calculators = [
    { id: "loan", name: "대출 계산기", icon: Home, component: LoanCalculator },
    {
      id: "savings",
      name: "적금 계산기",
      icon: PiggyBank,
      component: SavingsCalculator,
    },
    {
      id: "childcare",
      name: "육아비 계산기",
      icon: DollarSign,
      component: ChildcareCalculator,
    },
  ];

  const ActiveComponent = calculators.find(
    (calc) => calc.id === activeCalculator
  )?.component;

  return (
    <div className="space-y-6">
      <div className="flex items-center gap-3 mb-6">
        <img
          src="/hanacharacter/hanacharacter13.png"
          alt="금융 계산기 하나 캐릭터"
          className="w-24 h-24 object-contain"
        />
        <h2 className="text-2xl font-bold">금융 계산기</h2>
      </div>

      {/* 계산기 탭 */}
      <div className="flex flex-wrap gap-2 mb-6">
        {calculators.map((calc) => {
          const Icon = calc.icon;
          return (
            <button
              key={calc.id}
              onClick={() => setActiveCalculator(calc.id)}
              className={`flex items-center gap-2 px-4 py-3 rounded-xl border transition-all ${
                activeCalculator === calc.id
                  ? "bg-primary text-primary-foreground border-primary"
                  : "bg-background border-border hover:bg-muted/50"
              }`}
            >
              <Icon className="h-5 w-5" />
              {calc.name}
            </button>
          );
        })}
      </div>

      {/* 활성 계산기 */}
      {ActiveComponent && <ActiveComponent />}
    </div>
  );
}
