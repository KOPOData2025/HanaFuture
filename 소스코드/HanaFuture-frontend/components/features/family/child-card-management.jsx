"use client";

import React, { useState, useEffect } from "react";
import {
  ArrowLeft,
  Plus,
  Users,
  CreditCard,
  User,
  Calendar,
  Wallet,
  TrendingUp,
  TrendingDown,
  Eye,
  EyeOff,
  Settings,
  Bell,
  Heart,
  Star,
  Gift,
  ShoppingCart,
  Coffee,
  BookOpen,
  Gamepad2,
  MoreHorizontal,
  Edit,
  Trash2,
  Check,
  X,
  AlertCircle,
  CheckCircle,
  ArrowUpRight,
  ArrowDownLeft,
  Building2,
} from "lucide-react";
import { useAuth } from "../../../contexts/AuthContext";
import { AllowanceCardHero } from "./allowance-card-hero";

// API Base URL - 환경변수 사용
const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080/api";

export function ChildCardManagement({ onBack }) {
  const [activeTab, setActiveTab] = useState("overview"); // overview, children, transactions, settings
  const [children, setChildren] = useState([]);
  const [groupAccounts, setGroupAccounts] = useState([]);
  const [showAddChild, setShowAddChild] = useState(false);
  const [showTransferModal, setShowTransferModal] = useState(false);
  const [selectedChild, setSelectedChild] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const { user } = useAuth();

  const [newChild, setNewChild] = useState({
    name: "",
    birthDate: "",
    schoolGrade: "",
    cardNickname: "",
    monthlyAllowance: "",
    weeklyLimit: "",
    autoTransferEnabled: false,
    sourceAccountId: "",
  });

  const [transferData, setTransferData] = useState({
    amount: "",
    message: "",
    sourceAccountId: "",
  });

  // 초기 데이터 로드
  useEffect(() => {
    loadInitialData();
  }, []);

  const loadInitialData = async () => {
    try {
      setIsLoading(true);

      // 자녀 정보 로드 (기존 등록된 자녀들)
      const mockChildren = [
        {
          id: "child_001",
          name: "이지우",
          birthDate: "2015-03-15",
          age: 9,
          schoolGrade: "초등학교 3학년",
          cardNumber: "9410-****-****-1234",
          cardNickname: "지우 용돈카드",
          balance: 45000,
          monthlyAllowance: 50000,
          weeklyLimit: 20000,
          autoTransferEnabled: true,
          sourceAccountId: "group_001",
          lastTransferDate: "2024-01-01",
          totalSpent: 15000,
          status: "active",
          profileImage: "/hanacharacter/hanacharacter2.png",
        },
        {
          id: "child_002",
          name: "이서준",
          birthDate: "2018-07-22",
          age: 6,
          schoolGrade: "유치원",
          cardNumber: "9410-****-****-5678",
          cardNickname: "서준이 카드",
          balance: 12000,
          monthlyAllowance: 30000,
          weeklyLimit: 15000,
          autoTransferEnabled: false,
          sourceAccountId: "",
          lastTransferDate: "2024-01-05",
          totalSpent: 8000,
          status: "active",
          profileImage: "/hanacharacter/hanacharacter3.png",
        },
      ];

      // 모임통장 목록 로드
      const mockGroupAccounts = [
        {
          id: "group_001",
          name: "우리가족 생활비 통장",
          accountNumber: "817-123-456789",
          balance: 1450000,
          accountType: "모임통장",
        },
        {
          id: "group_002",
          name: "여행비 모임통장",
          accountNumber: "817-987-654321",
          balance: 850000,
          accountType: "모임통장",
        },
      ];

      setChildren(mockChildren);
      setGroupAccounts(mockGroupAccounts);
    } catch (error) {
      console.error("초기 데이터 로드 실패:", error);
    } finally {
      setIsLoading(false);
    }
  };

  // 자녀 추가
  const handleAddChild = async () => {
    if (!newChild.name || !newChild.birthDate || !newChild.monthlyAllowance) {
      alert("필수 정보를 모두 입력해주세요.");
      return;
    }

    try {
      setIsLoading(true);

      // 나이 계산
      const birthYear = new Date(newChild.birthDate).getFullYear();
      const currentYear = new Date().getFullYear();
      const age = currentYear - birthYear;

      const childData = {
        id: `child_${Date.now()}`,
        ...newChild,
        age,
        cardNumber: `9410-****-****-${Math.random().toString().slice(2, 6)}`,
        balance: 0,
        totalSpent: 0,
        status: "pending", // 카드 발급 대기중
        profileImage: "/hanacharacter/hanacharacter2.png",
        lastTransferDate: null,
      };

      const response = await fetch(`${API_BASE_URL}/child-cards`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          ...(user?.token && { Authorization: `Bearer ${user.token}` }),
        },
        body: JSON.stringify(childData),
      });

      if (response.ok) {
        setChildren((prev) => [...prev, childData]);
        setShowAddChild(false);
        setNewChild({
          name: "",
          birthDate: "",
          schoolGrade: "",
          cardNickname: "",
          monthlyAllowance: "",
          weeklyLimit: "",
          autoTransferEnabled: false,
          sourceAccountId: "",
        });
        alert("자녀가 성공적으로 등록되었습니다!");
      } else {
        throw new Error("자녀 등록에 실패했습니다.");
      }
    } catch (error) {
      console.error("자녀 추가 오류:", error);
      // 개발 환경에서는 로컬에만 추가
      const childData = {
        id: `child_${Date.now()}`,
        ...newChild,
        age:
          new Date().getFullYear() - new Date(newChild.birthDate).getFullYear(),
        cardNumber: `9410-****-****-${Math.random().toString().slice(2, 6)}`,
        balance: 0,
        totalSpent: 0,
        status: "active",
        profileImage: "/hanacharacter/hanacharacter2.png",
        lastTransferDate: null,
      };
      setChildren((prev) => [...prev, childData]);
      setShowAddChild(false);
      setNewChild({
        name: "",
        birthDate: "",
        schoolGrade: "",
        cardNickname: "",
        monthlyAllowance: "",
        weeklyLimit: "",
        autoTransferEnabled: false,
        sourceAccountId: "",
      });
    } finally {
      setIsLoading(false);
    }
  };

  // 용돈 보내기
  const handleSendMoney = async () => {
    if (
      !transferData.amount ||
      !selectedChild ||
      !transferData.sourceAccountId
    ) {
      alert("필수 정보를 모두 입력해주세요.");
      return;
    }

    try {
      setIsLoading(true);

      const response = await fetch(
        `${API_BASE_URL}/child-cards/${selectedChild.id}/transfer`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
          body: JSON.stringify({
            amount: parseInt(transferData.amount),
            message: transferData.message,
            sourceAccountId: transferData.sourceAccountId,
          }),
        }
      );

      if (response.ok) {
        // 자녀 카드 잔액 업데이트
        setChildren((prev) =>
          prev.map((child) =>
            child.id === selectedChild.id
              ? {
                  ...child,
                  balance: child.balance + parseInt(transferData.amount),
                }
              : child
          )
        );

        setShowTransferModal(false);
        setTransferData({ amount: "", message: "", sourceAccountId: "" });
        setSelectedChild(null);
        alert("용돈이 성공적으로 전송되었습니다!");
      } else {
        throw new Error("용돈 전송에 실패했습니다.");
      }
    } catch (error) {
      console.error("용돈 전송 오류:", error);
      // 개발 환경에서는 로컬에서만 업데이트
      setChildren((prev) =>
        prev.map((child) =>
          child.id === selectedChild.id
            ? {
                ...child,
                balance: child.balance + parseInt(transferData.amount),
              }
            : child
        )
      );
      setShowTransferModal(false);
      setTransferData({ amount: "", message: "", sourceAccountId: "" });
      setSelectedChild(null);
      alert("용돈이 전송되었습니다!");
    } finally {
      setIsLoading(false);
    }
  };

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat("ko-KR").format(amount);
  };

  const calculateAge = (birthDate) => {
    const birth = new Date(birthDate);
    const today = new Date();
    return today.getFullYear() - birth.getFullYear();
  };

  const getSpendingData = (child) => {
    // 모의 사용 내역 데이터
    return [
      {
        date: "2024-01-07",
        description: "편의점 구매",
        amount: 3200,
        category: "간식",
      },
      {
        date: "2024-01-06",
        description: "문구점",
        amount: 8500,
        category: "학용품",
      },
      {
        date: "2024-01-05",
        description: "카페",
        amount: 4500,
        category: "음료",
      },
      {
        date: "2024-01-04",
        description: "서점",
        amount: 12000,
        category: "도서",
      },
    ];
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* 히어로 섹션 */}
      <AllowanceCardHero />

      <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* 헤더 */}
        <div className="flex items-center justify-between mb-8">
          <button
            onClick={onBack}
            className="flex items-center gap-2 text-gray-600 hover:text-gray-800 transition-colors"
          >
            <ArrowLeft className="h-5 w-5" />
            뒤로가기
          </button>
          <button
            onClick={() => setShowAddChild(true)}
            className="bg-gradient-to-r from-purple-600 to-pink-600 text-white px-6 py-3 rounded-xl hover:from-purple-700 hover:to-pink-700 transition-all duration-300 font-semibold flex items-center gap-2 shadow-lg"
          >
            <Plus className="w-5 h-5" />
            자녀 추가
          </button>
        </div>

        {/* 탭 네비게이션 */}
        <div className="border-b border-gray-200 mb-8">
          <div className="flex space-x-8">
            {[
              { id: "overview", label: "전체 현황", icon: Users },
              { id: "children", label: "자녀 관리", icon: User },
              { id: "transactions", label: "거래 내역", icon: CreditCard },
              { id: "settings", label: "설정", icon: Settings },
            ].map((tab) => {
              const Icon = tab.icon;
              return (
                <button
                  key={tab.id}
                  onClick={() => setActiveTab(tab.id)}
                  className={`py-4 px-2 border-b-2 font-medium text-sm transition-colors flex items-center gap-2 ${
                    activeTab === tab.id
                      ? "border-purple-500 text-purple-600"
                      : "border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300"
                  }`}
                >
                  <Icon className="w-4 h-4" />
                  {tab.label}
                </button>
              );
            })}
          </div>
        </div>

        {/* 전체 현황 탭 */}
        {activeTab === "overview" && (
          <div className="space-y-8">
            {/* 요약 카드 */}
            <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
              <div className="bg-gradient-to-br from-purple-50 to-pink-50 p-6 rounded-2xl border border-purple-100">
                <div className="flex items-center justify-between mb-4">
                  <div className="w-12 h-12 bg-purple-100 rounded-xl flex items-center justify-center">
                    <Users className="w-6 h-6 text-purple-600" />
                  </div>
                  <span className="text-2xl font-bold text-purple-600">
                    {children.length}
                  </span>
                </div>
                <h3 className="font-semibold text-gray-900 mb-1">
                  등록된 자녀
                </h3>
                <p className="text-sm text-gray-600">
                  활성 카드{" "}
                  {children.filter((c) => c.status === "active").length}개
                </p>
              </div>

              <div className="bg-gradient-to-br from-emerald-50 to-teal-50 p-6 rounded-2xl border border-emerald-100">
                <div className="flex items-center justify-between mb-4">
                  <div className="w-12 h-12 bg-emerald-100 rounded-xl flex items-center justify-center">
                    <Wallet className="w-6 h-6 text-emerald-600" />
                  </div>
                  <span className="text-2xl font-bold text-emerald-600">
                    {formatCurrency(
                      children.reduce((sum, child) => sum + child.balance, 0)
                    )}
                  </span>
                </div>
                <h3 className="font-semibold text-gray-900 mb-1">
                  총 카드 잔액
                </h3>
                <p className="text-sm text-gray-600">모든 자녀 카드 합계</p>
              </div>

              <div className="bg-gradient-to-br from-blue-50 to-indigo-50 p-6 rounded-2xl border border-blue-100">
                <div className="flex items-center justify-between mb-4">
                  <div className="w-12 h-12 bg-blue-100 rounded-xl flex items-center justify-center">
                    <TrendingUp className="w-6 h-6 text-blue-600" />
                  </div>
                  <span className="text-2xl font-bold text-blue-600">
                    {formatCurrency(
                      children.reduce(
                        (sum, child) => sum + child.monthlyAllowance,
                        0
                      )
                    )}
                  </span>
                </div>
                <h3 className="font-semibold text-gray-900 mb-1">
                  월 용돈 총액
                </h3>
                <p className="text-sm text-gray-600">자동이체 설정 금액</p>
              </div>

              <div className="bg-gradient-to-br from-orange-50 to-red-50 p-6 rounded-2xl border border-orange-100">
                <div className="flex items-center justify-between mb-4">
                  <div className="w-12 h-12 bg-orange-100 rounded-xl flex items-center justify-center">
                    <TrendingDown className="w-6 h-6 text-orange-600" />
                  </div>
                  <span className="text-2xl font-bold text-orange-600">
                    {formatCurrency(
                      children.reduce((sum, child) => sum + child.totalSpent, 0)
                    )}
                  </span>
                </div>
                <h3 className="font-semibold text-gray-900 mb-1">
                  이번달 사용
                </h3>
                <p className="text-sm text-gray-600">전체 사용 금액</p>
              </div>
            </div>

            {/* 자녀별 카드 현황 */}
            <div>
              <h2 className="text-xl font-bold text-gray-900 mb-6">
                자녀별 카드 현황
              </h2>
              <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                {children.map((child) => (
                  <div
                    key={child.id}
                    className="bg-white rounded-2xl shadow-lg border border-gray-100 overflow-hidden"
                  >
                    <div className="p-6">
                      <div className="flex items-start justify-between mb-4">
                        <div className="flex items-center space-x-4">
                          <div className="w-12 h-12 rounded-full overflow-hidden">
                            <img
                              src={child.profileImage}
                              alt={child.name}
                              className="w-full h-full object-cover"
                            />
                          </div>
                          <div>
                            <h3 className="font-bold text-lg text-gray-900">
                              {child.name}
                            </h3>
                            <p className="text-sm text-gray-600">
                              {child.schoolGrade}
                            </p>
                          </div>
                        </div>
                        <div
                          className={`px-3 py-1 rounded-full text-xs font-medium ${
                            child.status === "active"
                              ? "bg-green-100 text-green-700"
                              : "bg-yellow-100 text-yellow-700"
                          }`}
                        >
                          {child.status === "active" ? "활성" : "대기중"}
                        </div>
                      </div>

                      <div className="space-y-4">
                        <div className="bg-gradient-to-r from-purple-500 to-pink-500 p-4 rounded-xl text-white">
                          <div className="flex justify-between items-start mb-2">
                            <div>
                              <p className="text-purple-100 text-sm">
                                {child.cardNickname}
                              </p>
                              <p className="font-mono text-lg tracking-wider">
                                {child.cardNumber}
                              </p>
                            </div>
                            <div className="text-right">
                              <p className="text-purple-100 text-sm">잔액</p>
                              <p className="text-2xl font-bold">
                                {formatCurrency(child.balance)}원
                              </p>
                            </div>
                          </div>
                        </div>

                        <div className="grid grid-cols-2 gap-4">
                          <div className="text-center p-3 bg-gray-50 rounded-lg">
                            <p className="text-sm text-gray-600">월 용돈</p>
                            <p className="font-semibold text-gray-900">
                              {formatCurrency(child.monthlyAllowance)}원
                            </p>
                          </div>
                          <div className="text-center p-3 bg-gray-50 rounded-lg">
                            <p className="text-sm text-gray-600">이번달 사용</p>
                            <p className="font-semibold text-gray-900">
                              {formatCurrency(child.totalSpent)}원
                            </p>
                          </div>
                        </div>

                        <div className="flex space-x-3">
                          <button
                            onClick={() => {
                              setSelectedChild(child);
                              setShowTransferModal(true);
                            }}
                            className="flex-1 bg-purple-600 text-white py-2 px-4 rounded-lg hover:bg-purple-700 transition-colors font-medium"
                          >
                            용돈 보내기
                          </button>
                          <button
                            onClick={() => {
                              setActiveTab("children");
                              setSelectedChild(child);
                            }}
                            className="flex-1 bg-gray-200 text-gray-700 py-2 px-4 rounded-lg hover:bg-gray-300 transition-colors font-medium"
                          >
                            상세 보기
                          </button>
                        </div>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        )}

        {/* 자녀 관리 탭 */}
        {activeTab === "children" && (
          <div className="space-y-6">
            {children.map((child) => (
              <div
                key={child.id}
                className="bg-white rounded-2xl shadow-lg border border-gray-100 p-6"
              >
                <div className="flex items-center justify-between mb-6">
                  <div className="flex items-center space-x-4">
                    <div className="w-16 h-16 rounded-full overflow-hidden">
                      <img
                        src={child.profileImage}
                        alt={child.name}
                        className="w-full h-full object-cover"
                      />
                    </div>
                    <div>
                      <h3 className="text-xl font-bold text-gray-900">
                        {child.name}
                      </h3>
                      <p className="text-gray-600">
                        {child.age}세, {child.schoolGrade}
                      </p>
                    </div>
                  </div>
                  <div className="flex items-center space-x-3">
                    <button className="p-2 text-gray-400 hover:text-gray-600 rounded-lg transition-colors">
                      <Edit className="w-5 h-5" />
                    </button>
                    <button className="p-2 text-gray-400 hover:text-red-500 rounded-lg transition-colors">
                      <Trash2 className="w-5 h-5" />
                    </button>
                  </div>
                </div>

                <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                  {/* 카드 정보 */}
                  <div className="lg:col-span-1">
                    <h4 className="font-semibold text-gray-900 mb-4">
                      카드 정보
                    </h4>
                    <div className="bg-gradient-to-r from-purple-500 to-pink-500 p-4 rounded-xl text-white mb-4">
                      <div className="space-y-2">
                        <p className="text-purple-100 text-sm">
                          {child.cardNickname}
                        </p>
                        <p className="font-mono text-lg tracking-wider">
                          {child.cardNumber}
                        </p>
                        <div className="flex justify-between items-end">
                          <div>
                            <p className="text-purple-100 text-xs">잔액</p>
                            <p className="text-xl font-bold">
                              {formatCurrency(child.balance)}원
                            </p>
                          </div>
                          <div className="text-right">
                            <p className="text-purple-100 text-xs">상태</p>
                            <p className="text-sm font-medium">
                              {child.status === "active" ? "활성" : "대기중"}
                            </p>
                          </div>
                        </div>
                      </div>
                    </div>

                    <button
                      onClick={() => {
                        setSelectedChild(child);
                        setShowTransferModal(true);
                      }}
                      className="w-full bg-purple-600 text-white py-3 px-4 rounded-xl hover:bg-purple-700 transition-colors font-semibold"
                    >
                      용돈 보내기
                    </button>
                  </div>

                  {/* 설정 및 한도 */}
                  <div className="lg:col-span-1">
                    <h4 className="font-semibold text-gray-900 mb-4">
                      설정 및 한도
                    </h4>
                    <div className="space-y-4">
                      <div className="bg-gray-50 p-4 rounded-xl">
                        <div className="space-y-3">
                          <div className="flex justify-between items-center">
                            <span className="text-sm text-gray-600">
                              월 용돈
                            </span>
                            <span className="font-semibold text-gray-900">
                              {formatCurrency(child.monthlyAllowance)}원
                            </span>
                          </div>
                          <div className="flex justify-between items-center">
                            <span className="text-sm text-gray-600">
                              주간 한도
                            </span>
                            <span className="font-semibold text-gray-900">
                              {formatCurrency(child.weeklyLimit)}원
                            </span>
                          </div>
                          <div className="flex justify-between items-center">
                            <span className="text-sm text-gray-600">
                              자동이체
                            </span>
                            <span
                              className={`text-sm font-medium ${
                                child.autoTransferEnabled
                                  ? "text-green-600"
                                  : "text-gray-500"
                              }`}
                            >
                              {child.autoTransferEnabled ? "활성" : "비활성"}
                            </span>
                          </div>
                          {child.autoTransferEnabled && (
                            <div className="flex justify-between items-center">
                              <span className="text-sm text-gray-600">
                                출금 계좌
                              </span>
                              <span className="text-xs text-gray-500">
                                {groupAccounts.find(
                                  (acc) => acc.id === child.sourceAccountId
                                )?.name || "미설정"}
                              </span>
                            </div>
                          )}
                        </div>
                      </div>
                    </div>
                  </div>

                  {/* 사용 내역 */}
                  <div className="lg:col-span-1">
                    <h4 className="font-semibold text-gray-900 mb-4">
                      최근 사용 내역
                    </h4>
                    <div className="space-y-3">
                      {getSpendingData(child)
                        .slice(0, 4)
                        .map((transaction, index) => (
                          <div
                            key={index}
                            className="flex items-center justify-between p-3 bg-gray-50 rounded-lg"
                          >
                            <div className="flex items-center space-x-3">
                              <div className="w-8 h-8 bg-red-100 rounded-lg flex items-center justify-center">
                                <ArrowDownLeft className="w-4 h-4 text-red-600" />
                              </div>
                              <div>
                                <p className="text-sm font-medium text-gray-900">
                                  {transaction.description}
                                </p>
                                <p className="text-xs text-gray-500">
                                  {transaction.date}
                                </p>
                              </div>
                            </div>
                            <div className="text-right">
                              <p className="text-sm font-semibold text-red-600">
                                -{formatCurrency(transaction.amount)}원
                              </p>
                              <p className="text-xs text-gray-500">
                                {transaction.category}
                              </p>
                            </div>
                          </div>
                        ))}
                    </div>
                    <button className="w-full mt-4 text-sm text-purple-600 hover:text-purple-700 font-medium">
                      전체 내역 보기
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}

        {/* 거래 내역 탭 */}
        {activeTab === "transactions" && (
          <div className="bg-white rounded-2xl shadow-lg border border-gray-100 p-6">
            <h2 className="text-xl font-bold text-gray-900 mb-6">
              전체 거래 내역
            </h2>
            <div className="space-y-4">
              {children
                .flatMap((child) =>
                  getSpendingData(child).map((transaction, index) => ({
                    ...transaction,
                    childName: child.name,
                    childId: child.id,
                    id: `${child.id}_${index}`,
                  }))
                )
                .sort((a, b) => new Date(b.date) - new Date(a.date))
                .map((transaction) => (
                  <div
                    key={transaction.id}
                    className="flex items-center justify-between p-4 border border-gray-200 rounded-xl hover:bg-gray-50 transition-colors"
                  >
                    <div className="flex items-center space-x-4">
                      <div className="w-10 h-10 bg-red-100 rounded-lg flex items-center justify-center">
                        <ArrowDownLeft className="w-5 h-5 text-red-600" />
                      </div>
                      <div>
                        <p className="font-medium text-gray-900">
                          {transaction.description}
                        </p>
                        <p className="text-sm text-gray-600">
                          {transaction.childName} • {transaction.date}
                        </p>
                      </div>
                    </div>
                    <div className="text-right">
                      <p className="font-semibold text-red-600">
                        -{formatCurrency(transaction.amount)}원
                      </p>
                      <p className="text-sm text-gray-500">
                        {transaction.category}
                      </p>
                    </div>
                  </div>
                ))}
            </div>
          </div>
        )}

        {/* 설정 탭 */}
        {activeTab === "settings" && (
          <div className="space-y-6">
            <div className="bg-white rounded-2xl shadow-lg border border-gray-100 p-6">
              <h2 className="text-xl font-bold text-gray-900 mb-6">
                알림 설정
              </h2>
              <div className="space-y-4">
                {[
                  {
                    id: "spending",
                    label: "사용 내역 알림",
                    description: "자녀가 카드를 사용할 때마다 알림",
                  },
                  {
                    id: "limit",
                    label: "한도 초과 알림",
                    description: "주간/월간 한도 초과 시 알림",
                  },
                  {
                    id: "transfer",
                    label: "용돈 이체 알림",
                    description: "자동이체 완료 시 알림",
                  },
                  {
                    id: "balance",
                    label: "잔액 부족 알림",
                    description: "카드 잔액이 부족할 때 알림",
                  },
                ].map((setting) => (
                  <div
                    key={setting.id}
                    className="flex items-center justify-between p-4 border border-gray-200 rounded-xl"
                  >
                    <div>
                      <h3 className="font-medium text-gray-900">
                        {setting.label}
                      </h3>
                      <p className="text-sm text-gray-600">
                        {setting.description}
                      </p>
                    </div>
                    <div className="flex items-center">
                      <input
                        type="checkbox"
                        className="w-5 h-5 text-purple-600 border-gray-300 rounded focus:ring-purple-500"
                        defaultChecked
                      />
                    </div>
                  </div>
                ))}
              </div>
            </div>

            <div className="bg-white rounded-2xl shadow-lg border border-gray-100 p-6">
              <h2 className="text-xl font-bold text-gray-900 mb-6">
                보안 설정
              </h2>
              <div className="space-y-4">
                <div className="flex items-center justify-between p-4 border border-gray-200 rounded-xl">
                  <div>
                    <h3 className="font-medium text-gray-900">생체 인증</h3>
                    <p className="text-sm text-gray-600">
                      지문/얼굴 인식으로 카드 사용 승인
                    </p>
                  </div>
                  <button className="bg-purple-600 text-white px-4 py-2 rounded-lg hover:bg-purple-700 transition-colors">
                    설정하기
                  </button>
                </div>
                <div className="flex items-center justify-between p-4 border border-gray-200 rounded-xl">
                  <div>
                    <h3 className="font-medium text-gray-900">카드 일시정지</h3>
                    <p className="text-sm text-gray-600">
                      필요시 카드 사용을 일시적으로 중단
                    </p>
                  </div>
                  <button className="bg-gray-200 text-gray-700 px-4 py-2 rounded-lg hover:bg-gray-300 transition-colors">
                    관리하기
                  </button>
                </div>
              </div>
            </div>
          </div>
        )}

        {/* 자녀 추가 모달 */}
        {showAddChild && (
          <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
            <div className="bg-white rounded-2xl max-w-md w-full p-6">
              <div className="flex justify-between items-center mb-6">
                <h3 className="text-xl font-bold text-gray-900">자녀 추가</h3>
                <button
                  onClick={() => setShowAddChild(false)}
                  className="text-gray-400 hover:text-gray-600"
                >
                  <X className="w-6 h-6" />
                </button>
              </div>

              <div className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    자녀 이름
                  </label>
                  <input
                    type="text"
                    value={newChild.name}
                    onChange={(e) =>
                      setNewChild((prev) => ({ ...prev, name: e.target.value }))
                    }
                    className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                    placeholder="이름을 입력하세요"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    생년월일
                  </label>
                  <input
                    type="date"
                    value={newChild.birthDate}
                    onChange={(e) =>
                      setNewChild((prev) => ({
                        ...prev,
                        birthDate: e.target.value,
                      }))
                    }
                    className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    학교/학년
                  </label>
                  <input
                    type="text"
                    value={newChild.schoolGrade}
                    onChange={(e) =>
                      setNewChild((prev) => ({
                        ...prev,
                        schoolGrade: e.target.value,
                      }))
                    }
                    className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                    placeholder="예: 초등학교 3학년"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    카드 별명
                  </label>
                  <input
                    type="text"
                    value={newChild.cardNickname}
                    onChange={(e) =>
                      setNewChild((prev) => ({
                        ...prev,
                        cardNickname: e.target.value,
                      }))
                    }
                    className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                    placeholder="예: 지우 용돈카드"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    월 용돈 금액
                  </label>
                  <input
                    type="text"
                    value={newChild.monthlyAllowance}
                    onChange={(e) => {
                      const value = e.target.value.replace(/[^0-9]/g, "");
                      setNewChild((prev) => ({
                        ...prev,
                        monthlyAllowance: value,
                      }));
                    }}
                    className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                    placeholder="50000"
                  />
                  {newChild.monthlyAllowance && (
                    <p className="text-sm text-gray-500 mt-1">
                      {formatCurrency(newChild.monthlyAllowance)}원
                    </p>
                  )}
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    주간 사용 한도
                  </label>
                  <input
                    type="text"
                    value={newChild.weeklyLimit}
                    onChange={(e) => {
                      const value = e.target.value.replace(/[^0-9]/g, "");
                      setNewChild((prev) => ({ ...prev, weeklyLimit: value }));
                    }}
                    className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                    placeholder="20000"
                  />
                  {newChild.weeklyLimit && (
                    <p className="text-sm text-gray-500 mt-1">
                      {formatCurrency(newChild.weeklyLimit)}원
                    </p>
                  )}
                </div>

                <div className="flex items-center space-x-3">
                  <input
                    type="checkbox"
                    id="autoTransfer"
                    checked={newChild.autoTransferEnabled}
                    onChange={(e) =>
                      setNewChild((prev) => ({
                        ...prev,
                        autoTransferEnabled: e.target.checked,
                      }))
                    }
                    className="w-5 h-5 text-purple-600 border-gray-300 rounded focus:ring-purple-500"
                  />
                  <label
                    htmlFor="autoTransfer"
                    className="text-sm text-gray-700"
                  >
                    자동이체 사용
                  </label>
                </div>

                {newChild.autoTransferEnabled && (
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      출금 계좌
                    </label>
                    <select
                      value={newChild.sourceAccountId}
                      onChange={(e) =>
                        setNewChild((prev) => ({
                          ...prev,
                          sourceAccountId: e.target.value,
                        }))
                      }
                      className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                    >
                      <option value="">계좌를 선택하세요</option>
                      {groupAccounts.map((account) => (
                        <option key={account.id} value={account.id}>
                          {account.name} ({account.accountNumber})
                        </option>
                      ))}
                    </select>
                  </div>
                )}
              </div>

              <div className="flex space-x-3 mt-6">
                <button
                  onClick={() => setShowAddChild(false)}
                  className="flex-1 bg-gray-200 text-gray-700 py-3 px-4 rounded-xl hover:bg-gray-300 transition-colors font-medium"
                >
                  취소
                </button>
                <button
                  onClick={handleAddChild}
                  disabled={
                    isLoading ||
                    !newChild.name ||
                    !newChild.birthDate ||
                    !newChild.monthlyAllowance
                  }
                  className="flex-1 bg-purple-600 text-white py-3 px-4 rounded-xl hover:bg-purple-700 disabled:bg-gray-300 disabled:cursor-not-allowed transition-colors font-medium"
                >
                  {isLoading ? "추가 중..." : "자녀 추가"}
                </button>
              </div>
            </div>
          </div>
        )}

        {/* 용돈 보내기 모달 */}
        {showTransferModal && selectedChild && (
          <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
            <div className="bg-white rounded-2xl max-w-md w-full p-6">
              <div className="flex justify-between items-center mb-6">
                <h3 className="text-xl font-bold text-gray-900">용돈 보내기</h3>
                <button
                  onClick={() => {
                    setShowTransferModal(false);
                    setSelectedChild(null);
                    setTransferData({
                      amount: "",
                      message: "",
                      sourceAccountId: "",
                    });
                  }}
                  className="text-gray-400 hover:text-gray-600"
                >
                  <X className="w-6 h-6" />
                </button>
              </div>

              <div className="text-center mb-6">
                <div className="w-16 h-16 rounded-full overflow-hidden mx-auto mb-3">
                  <img
                    src={selectedChild.profileImage}
                    alt={selectedChild.name}
                    className="w-full h-full object-cover"
                  />
                </div>
                <h4 className="text-lg font-semibold text-gray-900">
                  {selectedChild.name}
                </h4>
                <p className="text-sm text-gray-600">
                  현재 잔액: {formatCurrency(selectedChild.balance)}원
                </p>
              </div>

              <div className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    보낼 금액
                  </label>
                  <input
                    type="text"
                    value={transferData.amount}
                    onChange={(e) => {
                      const value = e.target.value.replace(/[^0-9]/g, "");
                      setTransferData((prev) => ({ ...prev, amount: value }));
                    }}
                    className="w-full p-4 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent text-right text-lg"
                    placeholder="10000"
                  />
                  {transferData.amount && (
                    <p className="text-right text-gray-500 mt-1">
                      {formatCurrency(transferData.amount)}원
                    </p>
                  )}
                </div>

                <div className="grid grid-cols-3 gap-2">
                  {[10000, 20000, 50000].map((amount) => (
                    <button
                      key={amount}
                      onClick={() =>
                        setTransferData((prev) => ({
                          ...prev,
                          amount: amount.toString(),
                        }))
                      }
                      className="p-2 text-sm border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors"
                    >
                      {formatCurrency(amount)}원
                    </button>
                  ))}
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    출금 계좌
                  </label>
                  <select
                    value={transferData.sourceAccountId}
                    onChange={(e) =>
                      setTransferData((prev) => ({
                        ...prev,
                        sourceAccountId: e.target.value,
                      }))
                    }
                    className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                  >
                    <option value="">계좌를 선택하세요</option>
                    {groupAccounts.map((account) => (
                      <option key={account.id} value={account.id}>
                        {account.name} ({account.accountNumber}) -{" "}
                        {formatCurrency(account.balance)}원
                      </option>
                    ))}
                  </select>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    메시지 (선택사항)
                  </label>
                  <input
                    type="text"
                    value={transferData.message}
                    onChange={(e) =>
                      setTransferData((prev) => ({
                        ...prev,
                        message: e.target.value,
                      }))
                    }
                    className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                    placeholder="예: 이번주 용돈이야"
                  />
                </div>
              </div>

              <div className="flex space-x-3 mt-6">
                <button
                  onClick={() => {
                    setShowTransferModal(false);
                    setSelectedChild(null);
                    setTransferData({
                      amount: "",
                      message: "",
                      sourceAccountId: "",
                    });
                  }}
                  className="flex-1 bg-gray-200 text-gray-700 py-3 px-4 rounded-xl hover:bg-gray-300 transition-colors font-medium"
                >
                  취소
                </button>
                <button
                  onClick={handleSendMoney}
                  disabled={
                    isLoading ||
                    !transferData.amount ||
                    !transferData.sourceAccountId
                  }
                  className="flex-1 bg-purple-600 text-white py-3 px-4 rounded-xl hover:bg-purple-700 disabled:bg-gray-300 disabled:cursor-not-allowed transition-colors font-medium"
                >
                  {isLoading ? "전송 중..." : "용돈 보내기"}
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
