"use client";

import React, { useState, useEffect } from "react";
import {
  Plus,
  CreditCard,
  User,
  TrendingUp,
  ArrowUpRight,
  ArrowDownRight,
  Settings,
  Pause,
  Play,
  Gift,
  Star,
  Calendar,
  Wallet,
  ShoppingCart,
  BookOpen,
} from "lucide-react";
import { ChildRegistrationFlow } from "./child-registration-flow";
import { AllowanceCardHero } from "./allowance-card-hero";

export function AllowanceCardDashboard({ userInfo }) {
  const [children, setChildren] = useState([]);
  const [selectedChild, setSelectedChild] = useState(null);
  const [showChildRegistration, setShowChildRegistration] = useState(false);
  const [allowanceCards, setAllowanceCards] = useState([]);
  const [isLoading, setIsLoading] = useState(true);

  // Mock 데이터 (실제로는 API에서 가져옴)
  const mockChildren = [
    {
      id: 1,
      name: "김하나",
      nickname: "하나공주",
      age: 8,
      grade: 2,
      classNumber: 3,
      schoolName: "하나초등학교",
      allowanceCycle: "WEEKLY",
      weeklyAllowance: 5000,
      profileImage: "/hanacharacter/hanacharacter4.png",
    },
    {
      id: 2,
      name: "김미래",
      nickname: "미래왕자",
      age: 12,
      grade: 6,
      classNumber: 1,
      schoolName: "퓨처초등학교",
      allowanceCycle: "WEEKLY",
      weeklyAllowance: 8000,
      profileImage: "/hanacharacter/hanacharacter3.png",
    },
  ];

  const mockAllowanceCards = [
    {
      id: 1,
      childId: 1,
      cardNumber: "5432-****-****-1234",
      cardName: "하나공주 아이카드",
      currentBalance: 12500,
      monthlyUsage: 8200,
      todayUsage: 3500,
      status: "ACTIVE",
      recentTransactions: [
        {
          id: 1,
          type: "USAGE",
          amount: 3500,
          merchantName: "GS25 하나점",
          category: "편의점",
          time: "오늘 15:30",
        },
        {
          id: 2,
          type: "CHARGE",
          amount: 10000,
          source: "엄마 모임통장",
          time: "어제 09:00",
        },
      ],
    },
    {
      id: 2,
      childId: 2,
      cardNumber: "5432-****-****-5678",
      cardName: "미래왕자 아이카드",
      currentBalance: 25000,
      monthlyUsage: 15600,
      todayUsage: 0,
      status: "ACTIVE",
      recentTransactions: [
        {
          id: 3,
          type: "USAGE",
          amount: 4200,
          merchantName: "교보문고 강남점",
          category: "문구점",
          time: "2일 전 16:45",
        },
      ],
    },
  ];

  useEffect(() => {
    loadChildrenAndCards();
  }, []);

  const loadChildrenAndCards = async () => {
    try {
      // 실제로는 API 호출
      setChildren(mockChildren);
      setAllowanceCards(mockAllowanceCards);
      if (mockChildren.length > 0) {
        setSelectedChild(mockChildren[0]);
      }
    } catch (error) {
      console.error("데이터 로드 실패:", error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleChildRegistrationComplete = (registrationData) => {
    setChildren([...children, registrationData.child]);
    setShowChildRegistration(false);
    setSelectedChild(registrationData.child);
  };

  const selectedChildCards = allowanceCards.filter(
    (card) => card.childId === selectedChild?.id
  );

  if (showChildRegistration) {
    return (
      <ChildRegistrationFlow
        onComplete={handleChildRegistrationComplete}
        onCancel={() => setShowChildRegistration(false)}
        userInfo={userInfo}
      />
    );
  }

  if (isLoading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-purple-600 mx-auto mb-4"></div>
          <p className="text-gray-600">아이카드 정보를 불러오는 중...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* 히어로 섹션 */}
      <AllowanceCardHero />

      <div className="max-w-6xl mx-auto px-4 py-8">
        {/* 헤더 */}
        <div className="flex items-center justify-end mb-8">
          <button
            onClick={() => setShowChildRegistration(true)}
            className="flex items-center gap-3 bg-gradient-to-r from-purple-600 to-pink-600 text-white px-6 py-3 rounded-2xl hover:from-purple-700 hover:to-pink-700 transition-all duration-300 font-semibold shadow-lg"
          >
            <Plus className="w-5 h-5" />
            자녀 등록
          </button>
        </div>

        {children.length === 0 ? (
          /* 자녀 없음 상태 */
          <div className="text-center py-20">
            <img
              src="/hanacharacter/hanacharacter4.png"
              alt="자녀 등록 안내"
              className="w-32 h-32 object-contain mx-auto mb-6"
            />
            <h2 className="text-2xl font-bold text-gray-900 mb-4">
              첫 번째 자녀를 등록해보세요
            </h2>
            <p className="text-gray-600 mb-8">
              자녀를 등록하고 아이카드를 발급받아
              <br />
              올바른 금융 습관을 길러주세요
            </p>
            <button
              onClick={() => setShowChildRegistration(true)}
              className="bg-gradient-to-r from-purple-600 to-pink-600 text-white px-8 py-4 rounded-2xl hover:from-purple-700 hover:to-pink-700 transition-all duration-300 font-bold text-lg shadow-lg"
            >
              자녀 등록하기
            </button>
          </div>
        ) : (
          <div className="grid lg:grid-cols-4 gap-8">
            {/* 자녀 선택 사이드바 */}
            <div className="lg:col-span-1">
              <div className="bg-white rounded-2xl p-6 shadow-sm border border-gray-100 sticky top-8">
                <h2 className="text-lg font-bold text-gray-900 mb-4">
                  우리 아이들
                </h2>
                <div className="space-y-3">
                  {children.map((child) => (
                    <div
                      key={child.id}
                      onClick={() => setSelectedChild(child)}
                      className={`p-4 rounded-xl cursor-pointer transition-all ${
                        selectedChild?.id === child.id
                          ? "bg-gradient-to-r from-purple-50 to-pink-50 border-2 border-purple-200"
                          : "bg-gray-50 hover:bg-gray-100 border-2 border-transparent"
                      }`}
                    >
                      <div className="flex items-center space-x-3">
                        <img
                          src={child.profileImage}
                          alt={child.name}
                          className="w-12 h-12 object-contain rounded-full bg-purple-100 p-2"
                        />
                        <div>
                          <div className="font-semibold text-gray-900">
                            {child.name}
                          </div>
                          {child.nickname && (
                            <div className="text-sm text-purple-600">
                              {child.nickname}
                            </div>
                          )}
                          <div className="text-xs text-gray-500">
                            만 {child.age}세 • {child.grade}학년
                          </div>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </div>

            {/* 메인 콘텐츠 */}
            <div className="lg:col-span-3">
              {selectedChild && (
                <div className="space-y-6">
                  {/* 선택된 자녀 정보 헤더 */}
                  <div className="bg-gradient-to-r from-purple-500 to-pink-500 rounded-2xl p-6 text-white">
                    <div className="flex items-center justify-between">
                      <div className="flex items-center space-x-4">
                        <img
                          src={selectedChild.profileImage}
                          alt={selectedChild.name}
                          className="w-16 h-16 object-contain rounded-full bg-white/20 p-3"
                        />
                        <div>
                          <h2 className="text-2xl font-bold">
                            {selectedChild.name}
                          </h2>
                          {selectedChild.nickname && (
                            <p className="text-purple-100">
                              {selectedChild.nickname}
                            </p>
                          )}
                          <p className="text-purple-200 text-sm">
                            {selectedChild.schoolName} {selectedChild.grade}학년{" "}
                            {selectedChild.classNumber}반
                          </p>
                        </div>
                      </div>
                      <div className="text-right">
                        <p className="text-purple-100 text-sm">용돈 설정</p>
                        <p className="text-xl font-bold">
                          {selectedChild.allowanceCycle === "WEEKLY"
                            ? `주 ${selectedChild.weeklyAllowance?.toLocaleString()}원`
                            : `월 ${selectedChild.monthlyAllowance?.toLocaleString()}원`}
                        </p>
                      </div>
                    </div>
                  </div>

                  {/* 아이카드 목록 */}
                  <div className="space-y-4">
                    {selectedChildCards.length === 0 ? (
                      <div className="bg-white rounded-2xl p-8 shadow-sm text-center">
                        <img
                          src="/hanacharacter/hanacharacter9.png"
                          alt="아이카드 발급 안내"
                          className="w-20 h-20 object-contain mx-auto mb-4"
                        />
                        <h3 className="text-xl font-bold text-gray-900 mb-2">
                          {selectedChild.name}의 첫 아이카드를 만들어보세요
                        </h3>
                        <p className="text-gray-600 mb-6">
                          선불 충전식 아이카드로 안전하고 편리하게 용돈을
                          관리하세요
                        </p>
                        <button className="bg-gradient-to-r from-purple-600 to-pink-600 text-white px-6 py-3 rounded-xl hover:from-purple-700 hover:to-pink-700 transition-all duration-300 font-semibold">
                          아이카드 발급하기
                        </button>
                      </div>
                    ) : (
                      selectedChildCards.map((card) => (
                        <div
                          key={card.id}
                          className="bg-white rounded-2xl p-6 shadow-sm border border-gray-100"
                        >
                          {/* 카드 헤더 */}
                          <div className="flex items-center justify-between mb-6">
                            <div className="flex items-center space-x-4">
                              <div className="w-16 h-16 bg-gradient-to-br from-purple-500 to-pink-500 rounded-2xl flex items-center justify-center">
                                <CreditCard className="w-8 h-8 text-white" />
                              </div>
                              <div>
                                <h3 className="text-xl font-bold text-gray-900">
                                  {card.cardName}
                                </h3>
                                <p className="text-gray-600">
                                  {card.cardNumber}
                                </p>
                                <span
                                  className={`inline-block px-2 py-1 rounded-full text-xs font-medium ${
                                    card.status === "ACTIVE"
                                      ? "bg-green-100 text-green-700"
                                      : "bg-red-100 text-red-700"
                                  }`}
                                >
                                  {card.status === "ACTIVE"
                                    ? "사용중"
                                    : "정지됨"}
                                </span>
                              </div>
                            </div>
                            <button className="p-2 text-gray-400 hover:text-gray-600">
                              <Settings className="w-5 h-5" />
                            </button>
                          </div>

                          {/* 잔액 및 사용 현황 */}
                          <div className="grid grid-cols-3 gap-4 mb-6">
                            <div className="bg-emerald-50 rounded-xl p-4 text-center border border-emerald-200">
                              <Wallet className="w-6 h-6 text-emerald-600 mx-auto mb-2" />
                              <div className="text-2xl font-bold text-emerald-600">
                                {card.currentBalance.toLocaleString()}원
                              </div>
                              <div className="text-sm text-emerald-700">
                                현재 잔액
                              </div>
                            </div>
                            <div className="bg-blue-50 rounded-xl p-4 text-center border border-blue-200">
                              <ShoppingCart className="w-6 h-6 text-blue-600 mx-auto mb-2" />
                              <div className="text-2xl font-bold text-blue-600">
                                {card.todayUsage.toLocaleString()}원
                              </div>
                              <div className="text-sm text-blue-700">
                                오늘 사용
                              </div>
                            </div>
                            <div className="bg-purple-50 rounded-xl p-4 text-center border border-purple-200">
                              <Calendar className="w-6 h-6 text-purple-600 mx-auto mb-2" />
                              <div className="text-2xl font-bold text-purple-600">
                                {card.monthlyUsage.toLocaleString()}원
                              </div>
                              <div className="text-sm text-purple-700">
                                이번 달
                              </div>
                            </div>
                          </div>

                          {/* 빠른 액션 */}
                          <div className="grid grid-cols-2 gap-3 mb-6">
                            <button className="flex items-center justify-center gap-2 bg-gradient-to-r from-emerald-500 to-teal-500 text-white py-3 px-4 rounded-xl hover:from-emerald-600 hover:to-teal-600 transition-all duration-300 font-medium">
                              <ArrowUpRight className="w-4 h-4" />
                              충전하기
                            </button>
                            <button className="flex items-center justify-center gap-2 bg-gray-100 text-gray-700 py-3 px-4 rounded-xl hover:bg-gray-200 transition-colors font-medium">
                              <Pause className="w-4 h-4" />
                              카드 정지
                            </button>
                          </div>

                          {/* 최근 사용 내역 */}
                          <div>
                            <h4 className="font-semibold text-gray-900 mb-3">
                              최근 내역
                            </h4>
                            <div className="space-y-3">
                              {card.recentTransactions.map((transaction) => (
                                <div
                                  key={transaction.id}
                                  className="flex items-center justify-between p-3 bg-gray-50 rounded-lg"
                                >
                                  <div className="flex items-center space-x-3">
                                    <div
                                      className={`w-8 h-8 rounded-full flex items-center justify-center ${
                                        transaction.type === "CHARGE"
                                          ? "bg-emerald-100 text-emerald-600"
                                          : "bg-blue-100 text-blue-600"
                                      }`}
                                    >
                                      {transaction.type === "CHARGE" ? (
                                        <ArrowUpRight className="w-4 h-4" />
                                      ) : (
                                        <ArrowDownRight className="w-4 h-4" />
                                      )}
                                    </div>
                                    <div>
                                      <div className="font-medium text-gray-900">
                                        {transaction.merchantName ||
                                          transaction.source}
                                      </div>
                                      <div className="text-sm text-gray-500">
                                        {transaction.category} •{" "}
                                        {transaction.time}
                                      </div>
                                    </div>
                                  </div>
                                  <div
                                    className={`font-semibold ${
                                      transaction.type === "CHARGE"
                                        ? "text-emerald-600"
                                        : "text-blue-600"
                                    }`}
                                  >
                                    {transaction.type === "CHARGE" ? "+" : "-"}
                                    {transaction.amount.toLocaleString()}원
                                  </div>
                                </div>
                              ))}
                            </div>
                          </div>

                          {/* 금융 교육 섹션 */}
                          <div className="mt-6 pt-6 border-t border-gray-200">
                            <h4 className="font-semibold text-gray-900 mb-3">
                              금융 교육
                            </h4>
                            <div className="grid grid-cols-2 gap-3">
                              <div className="bg-yellow-50 rounded-xl p-4 border border-yellow-200">
                                <Star className="w-6 h-6 text-yellow-600 mb-2" />
                                <div className="font-semibold text-yellow-900">
                                  리워드 포인트
                                </div>
                                <div className="text-2xl font-bold text-yellow-600">
                                  850P
                                </div>
                              </div>
                              <div className="bg-indigo-50 rounded-xl p-4 border border-indigo-200">
                                <BookOpen className="w-6 h-6 text-indigo-600 mb-2" />
                                <div className="font-semibold text-indigo-900">
                                  교육 레벨
                                </div>
                                <div className="text-2xl font-bold text-indigo-600">
                                  Level 3
                                </div>
                              </div>
                            </div>
                          </div>
                        </div>
                      ))
                    )}
                  </div>
                </div>
              )}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
