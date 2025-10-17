"use client";

import { useState, useEffect } from "react";
import {
  Plus,
  Target,
  TrendingUp,
  Calendar,
  Users,
  Baby,
  GraduationCap,
  Home,
  Shield,
  Plane,
  Heart,
  Award,
  Sparkles,
  PiggyBank,
  ArrowRight,
  Crown,
  Gift,
  Star,
  CreditCard,
  Banknote,
  Calculator,
  Settings,
  Eye,
  ChevronRight,
  Wallet,
  Clock,
  TrendingDown,
} from "lucide-react";
import { HanaSavingsSubscription } from "./hana-savings-subscription";
import { useAuth } from "../../../contexts/AuthContext";

/**
 * 현대적인 적금 대시보드 - 하나Future 스타일
 */
export function ModernSavingsDashboard() {
  const { user } = useAuth();
  const [savingGoals, setSavingGoals] = useState([]);
  const [selectedGoal, setSelectedGoal] = useState(null);
  const [showSubscriptionFlow, setShowSubscriptionFlow] = useState(false);
  const [showDepositForm, setShowDepositForm] = useState(false);
  const [loading, setLoading] = useState(true);

  // 컴포넌트 마운트 시 적금 목록 조회
  useEffect(() => {
    fetchSavingGoals();
  }, [user]);

  const fetchSavingGoals = async () => {
    try {
      setLoading(true);
      const response = await fetch(
        `http://localhost:8080/api/savings/goals?userId=${user?.id || 1}`
      );
      if (response.ok) {
        const result = await response.json();
        setSavingGoals(result.data || []);
      } else {
        console.error("적금 목록 조회 실패");
        setSavingGoals([]);
      }
    } catch (error) {
      console.error("적금 목록 조회 중 오류:", error);
      setSavingGoals([]);
    } finally {
      setLoading(false);
    }
  };

  const handleSubscriptionComplete = (newSavingGoal) => {
    setShowSubscriptionFlow(false);
    fetchSavingGoals(); // 목록 새로고침
  };

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat("ko-KR").format(amount);
  };

  const getCategoryIcon = (category) => {
    const icons = {
      PURPOSE: Target,
      HOUSING: Home,
      EDUCATION: GraduationCap,
      WEDDING: Heart,
      TRAVEL: Plane,
      BIRTH_PREPARATION: Baby,
      RETIREMENT: Shield,
      EMERGENCY: Star,
    };
    return icons[category] || Target;
  };

  const getCategoryEmoji = (category) => {
    const emojis = {
      PURPOSE: "🎯",
      HOUSING: "🏠",
      EDUCATION: "🎓",
      WEDDING: "💒",
      TRAVEL: "✈️",
      BIRTH_PREPARATION: "👶",
      RETIREMENT: "🛡️",
      EMERGENCY: "⭐",
    };
    return emojis[category] || "🎯";
  };

  const getAchievementRate = (goal) => {
    if (!goal.targetAmount || goal.targetAmount === 0) return 0;
    return Math.min((goal.currentAmount / goal.targetAmount) * 100, 100);
  };

  const calculateTotalSavings = () => {
    return savingGoals.reduce(
      (total, goal) => total + (goal.currentAmount || 0),
      0
    );
  };

  const calculateTotalTarget = () => {
    return savingGoals.reduce(
      (total, goal) => total + (goal.targetAmount || 0),
      0
    );
  };

  const getActiveGoalsCount = () => {
    return savingGoals.filter((goal) => goal.status === "ACTIVE").length;
  };

  if (showSubscriptionFlow) {
    return (
      <HanaSavingsSubscription
        onComplete={handleSubscriptionComplete}
        onCancel={() => setShowSubscriptionFlow(false)}
      />
    );
  }

  if (loading) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-slate-50 via-white to-emerald-50 flex items-center justify-center">
        <div className="text-center">
          <div className="w-16 h-16 border-4 border-emerald-200 border-t-emerald-500 rounded-full animate-spin mx-auto mb-4"></div>
          <p className="text-gray-600">적금 정보를 불러오는 중...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 via-white to-emerald-50">
      <div className="max-w-7xl mx-auto px-4 py-8">
        {/* 헤더 */}
        <div className="bg-white rounded-3xl shadow-lg border border-gray-100 p-8 mb-8">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-6">
              <div className="relative">
                <div className="w-24 h-24 bg-gradient-to-br from-emerald-400 to-emerald-600 rounded-3xl flex items-center justify-center shadow-lg">
                  <PiggyBank className="w-12 h-12 text-white" />
                </div>
                <div className="absolute -top-2 -right-2 w-8 h-8 bg-blue-500 rounded-full flex items-center justify-center">
                  <span className="text-white text-xs font-bold">
                    {getActiveGoalsCount()}
                  </span>
                </div>
              </div>
              <div>
                <h1 className="text-4xl font-black text-gray-900 mb-2">
                  하나Future 적금
                </h1>
                <p className="text-xl text-gray-600 mb-3">
                  목표를 향한 체계적인 저축 여정 ✨
                </p>
                <div className="flex items-center gap-6 text-sm text-gray-500">
                  <span>💰 안전한 저축</span>
                  <span>📈 우대금리</span>
                  <span>🎯 목표 달성 알림</span>
                  <span>📱 간편한 관리</span>
                </div>
              </div>
            </div>
            <button
              onClick={() => setShowSubscriptionFlow(true)}
              className="flex items-center gap-3 px-8 py-4 bg-emerald-500 text-white rounded-2xl font-bold text-lg hover:bg-emerald-600 transition-colors shadow-lg hover:shadow-xl"
            >
              <Plus className="w-6 h-6" />새 적금 가입
            </button>
          </div>
        </div>

        {savingGoals.length === 0 ? (
          // 빈 상태
          <div className="bg-white rounded-3xl shadow-lg border border-gray-100 overflow-hidden">
            {/* 3D 일러스트레이션 영역 */}
            <div className="bg-gradient-to-br from-emerald-50 to-blue-50 px-8 py-16">
              <div className="text-center">
                <div
                  className="relative mx-auto mb-8"
                  style={{ width: "240px", height: "180px" }}
                >
                  {/* 3D 스타일 적금통 일러스트 */}
                  <div className="absolute inset-0 bg-gradient-to-br from-emerald-400 to-emerald-600 rounded-3xl transform rotate-6 shadow-2xl opacity-80"></div>
                  <div className="absolute inset-0 bg-white rounded-3xl transform -rotate-6 shadow-2xl flex items-center justify-center">
                    <PiggyBank className="w-24 h-24 text-emerald-500 opacity-70" />
                  </div>
                  <div className="absolute -bottom-6 left-1/2 -translate-x-1/2 w-20 h-20 bg-yellow-300 rounded-full flex items-center justify-center shadow-xl animate-bounce">
                    <Plus className="w-10 h-10 text-white" />
                  </div>
                  <div className="absolute top-6 -left-8 w-16 h-16 bg-blue-300 rounded-full flex items-center justify-center shadow-lg animate-pulse">
                    <Target className="w-8 h-8 text-white" />
                  </div>
                  <div
                    className="absolute top-12 -right-8 w-14 h-14 bg-purple-300 rounded-full flex items-center justify-center shadow-lg animate-pulse"
                    style={{ animationDelay: "0.5s" }}
                  >
                    <TrendingUp className="w-7 h-7 text-white" />
                  </div>
                </div>
                <h3 className="text-3xl font-bold text-gray-900 mb-4">
                  첫 번째 적금을 시작해보세요!
                </h3>
                <p className="text-gray-600 text-lg mb-8">
                  체계적인 저축으로 목표를 달성하고 더 나은 미래를 만들어보세요.
                </p>
                <div className="grid grid-cols-1 md:grid-cols-3 gap-6 max-w-2xl mx-auto mb-8">
                  <div className="text-center">
                    <div className="w-16 h-16 bg-emerald-100 rounded-2xl flex items-center justify-center mx-auto mb-3">
                      <Target className="w-8 h-8 text-emerald-600" />
                    </div>
                    <h4 className="font-semibold text-gray-900 mb-1">
                      목표 설정
                    </h4>
                    <p className="text-sm text-gray-600">
                      명확한 목표와 기간 설정
                    </p>
                  </div>
                  <div className="text-center">
                    <div className="w-16 h-16 bg-blue-100 rounded-2xl flex items-center justify-center mx-auto mb-3">
                      <Calculator className="w-8 h-8 text-blue-600" />
                    </div>
                    <h4 className="font-semibold text-gray-900 mb-1">
                      자동 납입
                    </h4>
                    <p className="text-sm text-gray-600">
                      매월 자동으로 안전하게
                    </p>
                  </div>
                  <div className="text-center">
                    <div className="w-16 h-16 bg-purple-100 rounded-2xl flex items-center justify-center mx-auto mb-3">
                      <Award className="w-8 h-8 text-purple-600" />
                    </div>
                    <h4 className="font-semibold text-gray-900 mb-1">
                      목표 달성
                    </h4>
                    <p className="text-sm text-gray-600">
                      이자와 함께 목표 완성
                    </p>
                  </div>
                </div>
              </div>
            </div>

            {/* 액션 버튼 */}
            <div className="p-8 bg-gray-50 border-t border-gray-100">
              <button
                onClick={() => setShowSubscriptionFlow(true)}
                className="w-full py-4 bg-emerald-500 text-white rounded-2xl font-bold text-lg hover:bg-emerald-600 transition-colors shadow-md flex items-center justify-center gap-3"
              >
                <PiggyBank className="w-6 h-6" />
                적금 가입하기
              </button>
            </div>
          </div>
        ) : (
          // 적금 목록이 있는 경우
          <div className="space-y-8">
            {/* 요약 카드 */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
              <div className="bg-white rounded-2xl p-6 shadow-lg border border-gray-100">
                <div className="flex items-center gap-4">
                  <div className="w-12 h-12 bg-emerald-100 rounded-xl flex items-center justify-center">
                    <Wallet className="w-6 h-6 text-emerald-600" />
                  </div>
                  <div>
                    <p className="text-sm text-gray-600">총 적립 금액</p>
                    <p className="text-2xl font-bold text-gray-900">
                      {formatCurrency(calculateTotalSavings())}원
                    </p>
                  </div>
                </div>
              </div>

              <div className="bg-white rounded-2xl p-6 shadow-lg border border-gray-100">
                <div className="flex items-center gap-4">
                  <div className="w-12 h-12 bg-blue-100 rounded-xl flex items-center justify-center">
                    <Target className="w-6 h-6 text-blue-600" />
                  </div>
                  <div>
                    <p className="text-sm text-gray-600">총 목표 금액</p>
                    <p className="text-2xl font-bold text-gray-900">
                      {formatCurrency(calculateTotalTarget())}원
                    </p>
                  </div>
                </div>
              </div>

              <div className="bg-white rounded-2xl p-6 shadow-lg border border-gray-100">
                <div className="flex items-center gap-4">
                  <div className="w-12 h-12 bg-purple-100 rounded-xl flex items-center justify-center">
                    <TrendingUp className="w-6 h-6 text-purple-600" />
                  </div>
                  <div>
                    <p className="text-sm text-gray-600">활성 적금</p>
                    <p className="text-2xl font-bold text-gray-900">
                      {getActiveGoalsCount()}개
                    </p>
                  </div>
                </div>
              </div>
            </div>

            {/* 적금 목록 */}
            <div className="grid grid-cols-1 lg:grid-cols-2 xl:grid-cols-3 gap-6">
              {savingGoals.map((goal) => {
                const IconComponent = getCategoryIcon(goal.category);
                const achievementRate = getAchievementRate(goal);
                const emoji = getCategoryEmoji(goal.category);

                return (
                  <div
                    key={goal.id}
                    className="bg-white rounded-2xl shadow-lg border border-gray-100 overflow-hidden hover:shadow-xl transition-shadow duration-300"
                  >
                    <div className="p-6 bg-gradient-to-br from-emerald-50 to-blue-50">
                      <div className="flex items-center justify-between mb-4">
                        <div className="flex items-center gap-3">
                          <div className="w-12 h-12 bg-emerald-500 rounded-xl flex items-center justify-center text-white text-xl">
                            {emoji}
                          </div>
                          <div>
                            <h3 className="font-bold text-gray-900 text-lg">
                              {goal.goalName}
                            </h3>
                            <p className="text-sm text-gray-600">
                              {goal.description}
                            </p>
                          </div>
                        </div>
                        <Settings className="w-5 h-5 text-gray-400 hover:text-gray-600 cursor-pointer" />
                      </div>

                      <div className="space-y-3">
                        <div className="flex justify-between items-center">
                          <span className="text-sm text-gray-600">
                            현재 적립
                          </span>
                          <span className="font-bold text-emerald-600 text-lg">
                            {formatCurrency(goal.currentAmount || 0)}원
                          </span>
                        </div>
                        <div className="flex justify-between items-center">
                          <span className="text-sm text-gray-600">
                            목표 금액
                          </span>
                          <span className="font-semibold text-gray-900">
                            {formatCurrency(goal.targetAmount || 0)}원
                          </span>
                        </div>
                        <div className="w-full bg-gray-200 rounded-full h-3">
                          <div
                            className="bg-emerald-500 h-3 rounded-full transition-all duration-500"
                            style={{ width: `${achievementRate}%` }}
                          ></div>
                        </div>
                        <div className="flex justify-between items-center text-sm">
                          <span className="text-gray-600">달성률</span>
                          <span className="font-semibold text-emerald-600">
                            {achievementRate.toFixed(1)}%
                          </span>
                        </div>
                      </div>
                    </div>

                    <div className="p-6 space-y-4">
                      <div className="flex justify-between text-sm">
                        <span className="text-gray-600">월 납입액</span>
                        <span className="font-semibold text-gray-900">
                          {formatCurrency(goal.monthlyTarget || 0)}원
                        </span>
                      </div>
                      <div className="flex justify-between text-sm">
                        <span className="text-gray-600">적금 계좌</span>
                        <span className="font-semibold text-gray-900">
                          {goal.savingsAccountNumber || "N/A"}
                        </span>
                      </div>
                      <div className="flex justify-between text-sm">
                        <span className="text-gray-600">금리</span>
                        <span className="font-semibold text-emerald-600">
                          {goal.interestRate || 0}%
                        </span>
                      </div>
                    </div>

                    <div className="p-6 border-t border-gray-100 flex gap-3">
                      <button className="flex-1 py-3 bg-blue-50 text-blue-600 rounded-xl font-semibold text-sm hover:bg-blue-100 transition-colors flex items-center justify-center gap-2">
                        <Plus className="w-4 h-4" />
                        납입
                      </button>
                      <button className="flex-1 py-3 bg-purple-50 text-purple-600 rounded-xl font-semibold text-sm hover:bg-purple-100 transition-colors flex items-center justify-center gap-2">
                        <Eye className="w-4 h-4" />
                        상세
                      </button>
                    </div>
                  </div>
                );
              })}

              {/* 새 적금 추가 카드 */}
              <div
                onClick={() => setShowSubscriptionFlow(true)}
                className="bg-gradient-to-br from-emerald-50 to-blue-50 rounded-2xl border-2 border-dashed border-emerald-300 hover:border-emerald-400 cursor-pointer transition-all duration-300 flex items-center justify-center min-h-[400px] group"
              >
                <div className="text-center">
                  <div className="w-16 h-16 bg-emerald-100 rounded-2xl flex items-center justify-center mx-auto mb-4 group-hover:bg-emerald-200 transition-colors">
                    <Plus className="w-8 h-8 text-emerald-600" />
                  </div>
                  <h3 className="font-bold text-gray-900 text-lg mb-2">
                    새 적금 추가
                  </h3>
                  <p className="text-gray-600">
                    새로운 목표를 위한
                    <br />
                    적금을 시작해보세요
                  </p>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}











