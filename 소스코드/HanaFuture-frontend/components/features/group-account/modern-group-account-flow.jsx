"use client";

import React, { useState, useEffect } from "react";
import {
  ArrowLeft,
  Users,
  Target,
  Calendar,
  CreditCard,
  Check,
  Plus,
  UserPlus,
  Phone,
  Mail,
  ChevronRight,
  AlertCircle,
  CheckCircle,
  Wallet,
  PiggyBank,
  Heart,
  Home,
  GraduationCap,
  Plane,
  Shield,
  MoreHorizontal,
  X,
  Camera,
  Upload,
} from "lucide-react";
import { useAuth } from "../../../contexts/AuthContext";

// API Base URL - 환경변수 사용
const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080/api";

export function ModernGroupAccountFlow({ onComplete, onCancel }) {
  const [currentStep, setCurrentStep] = useState(1);
  const [isLoading, setIsLoading] = useState(false);
  const [showInviteModal, setShowInviteModal] = useState(false);
  const [inviteTab, setInviteTab] = useState("direct"); // "direct" or "family"
  const [familyMembers, setFamilyMembers] = useState([]);
  const [inviteForm, setInviteForm] = useState({
    name: "",
    phone: "",
  });
  const { user } = useAuth();

  // 가족 목록 로드
  useEffect(() => {
    if (user?.id && currentStep === 3) {
      loadFamilyMembers();
    }
  }, [user, currentStep]);

  const loadFamilyMembers = async () => {
    try {
      const response = await fetch(
        `${API_BASE_URL}/family/members?userId=${user.id}`,
        {
          headers: {
            Authorization: `Bearer ${user.token}`,
          },
        }
      );

      if (response.ok) {
        const result = await response.json();
        setFamilyMembers(result.data || []);
      }
    } catch (error) {
      console.error("가족 목록 조회 실패:", error);
    }
  };

  // Base64 인코딩 함수
  const btoa = (str) => {
    return Buffer.from(str, "utf-8").toString("base64");
  };

  const [formData, setFormData] = useState({
    // 1단계: 모임 기본 정보
    groupName: "",
    groupPurpose: "",

    // 2단계: 계좌 설정
    accountPassword: "",
    confirmPassword: "",

    // 3단계: 약관 동의
    agreements: {
      service: false,
      privacy: false,
      marketing: false,
      banking: false,
      groupAccount: false,
    },
  });

  const [invitedMembers, setInvitedMembers] = useState([]);
  const [createdGroupAccountId, setCreatedGroupAccountId] = useState(null);
  const [createdAccountNumber, setCreatedAccountNumber] = useState(null);

  const steps = [
    { id: 1, title: "모임 정보", desc: "이름과 용도 설정", icon: Target },
    { id: 2, title: "비밀번호 설정", desc: "계좌 비밀번호", icon: CreditCard },
    { id: 3, title: "모임원 초대", desc: "친구 초대하기", icon: UserPlus },
    { id: 4, title: "개설 완료", desc: "모임통장 완성", icon: Check },
  ];

  const purposes = [
    {
      id: "family",
      title: "생활비",
      desc: "가족 생활비",
      icon: Heart,
      color: "bg-pink-500",
      emoji: "👨‍👩‍👧‍👦",
    },
    {
      id: "travel",
      title: "여행비",
      desc: "즐거운 여행을 위해",
      icon: Plane,
      color: "bg-blue-500",
      emoji: "✈️",
    },
    {
      id: "housing",
      title: "주거비",
      desc: "안정적인 주거 환경",
      icon: Home,
      color: "bg-green-500",
      emoji: "🏠",
    },
    {
      id: "education",
      title: "교육비",
      desc: "더 나은 미래를 위한 투자",
      icon: GraduationCap,
      color: "bg-purple-500",
      emoji: "🎓",
    },
    {
      id: "emergency",
      title: "비상자금",
      desc: "안전한 미래 준비",
      icon: Shield,
      color: "bg-orange-500",
      emoji: "🛡️",
    },
    {
      id: "other",
      title: "기타",
      desc: "우리만의 특별한 모임",
      icon: MoreHorizontal,
      color: "bg-gray-500",
      emoji: "💫",
    },
  ];

  const handleInputChange = (field, value) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
  };

  const handleAgreementChange = (field, checked) => {
    setFormData((prev) => ({
      ...prev,
      agreements: { ...prev.agreements, [field]: checked },
    }));
  };

  const nextStep = async () => {
    if (currentStep < 3) {
      setCurrentStep((prev) => prev + 1);
    } else if (currentStep === 3) {
      // 3단계 → 4단계: 모임통장 개설 + PENDING 멤버 생성 + 초대
      setIsLoading(true);
      try {
        // 1. 모임통장 개설
        console.log("🎯 모임통장 생성 요청 시작 - 사용자:", user?.id);

        const requestBody = {
          name: formData.groupName,
          description: `${formData.groupPurpose} 모임통장`,
          purpose: formData.groupPurpose.toUpperCase(),
          targetAmount: 0,
          monthlyTarget: 0,
          primaryUserId: user?.id || 1,
          groupAccountName: formData.groupName,
          primaryFintechUseNum: "MOCK_FINTECH_NUM",
          withdrawAccountId: 1,
          initialDepositAmount: 0,
          autoTransferEnabled: false,
          bankCode: "081",
          bankName: "하나은행",
          accountPassword: formData.accountPassword,
        };

        console.log("📤 요청 데이터:", requestBody);
        console.log("🔐 계좌 비밀번호:", formData.accountPassword);

        const response = await fetch(`${API_BASE_URL}/group-accounts`, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            ...(user?.token && { Authorization: `Bearer ${user.token}` }),
          },
          body: JSON.stringify(requestBody),
        });

        if (!response.ok) {
          let errorText = "알 수 없는 오류가 발생했습니다.";
          try {
            errorText = await response.text();
          } catch (textError) {
            console.warn("응답 텍스트 파싱 실패:", textError);
          }
          console.error("모임통장 생성 실패:", response.status, errorText);
          throw new Error("모임통장 개설에 실패했습니다.");
        }

        const result = await response.json();
        console.log("모임통장 생성 성공:", result);
        const groupAccountId = result.data.id;
        const accountNumber =
          result.data.accountNumber || result.data.accountNum;
        setCreatedGroupAccountId(groupAccountId);
        setCreatedAccountNumber(accountNumber);
        console.log("계좌번호:", accountNumber);

        // 2. 초대할 멤버가 있으면 PENDING 멤버 생성 + 카카오톡 초대
        if (invitedMembers.length > 0) {
          console.log(`📨 ${invitedMembers.length}명의 멤버 초대 시작`);

          for (const member of invitedMembers) {
            try {
              // PENDING 멤버 생성
              const inviteResponse = await fetch(
                `${API_BASE_URL}/group-accounts/${groupAccountId}/create-pending-invite`,
                {
                  method: "POST",
                  headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${user.token}`,
                  },
                  body: JSON.stringify({
                    name: member.name,
                    phone: member.phone,
                    inviterId: user.id,
                  }),
                }
              );

              if (inviteResponse.ok) {
                const inviteResult = await inviteResponse.json();
                console.log(
                  `✅ ${member.name}님 PENDING 멤버 생성 완료`,
                  inviteResult
                );
                console.log(
                  `📢 백엔드에서 자동으로 알림 생성됨 (userId: ${
                    member.userId || "phone 기반"
                  })`
                );
              } else {
                const errorText = await inviteResponse.text();
                console.error(
                  `❌ ${member.name}님 PENDING 멤버 생성 실패:`,
                  errorText
                );
              }
            } catch (error) {
              console.error(`${member.name}님 초대 처리 오류:`, error);
            }
          }
        }

        // 3. 4단계(완료)로 이동
        setCurrentStep(4);
      } catch (error) {
        console.error("모임통장 개설 오류:", error);
        alert(`모임통장 개설에 실패했습니다: ${error.message}`);
        return;
      } finally {
        setIsLoading(false);
      }
    }
  };

  const prevStep = () => {
    if (currentStep > 1) {
      setCurrentStep((prev) => prev - 1);
    } else {
      onCancel();
    }
  };

  const canProceed = () => {
    switch (currentStep) {
      case 1:
        // 모임 정보
        return formData.groupName && formData.groupPurpose;
      case 2:
        // 비밀번호 설정
        return (
          formData.accountPassword &&
          formData.confirmPassword &&
          formData.accountPassword === formData.confirmPassword &&
          formData.accountPassword.length >= 4
        );
      default:
        return true;
    }
  };

  const formatCurrency = (value) => {
    if (!value) return "";
    return parseInt(value).toLocaleString();
  };

  const selectedPurpose = purposes.find((p) => p.id === formData.groupPurpose);

  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* 뒤로가기 버튼 */}
      <div className="mb-6">
        <button
          onClick={prevStep}
          className="flex items-center gap-2 text-gray-600 hover:text-gray-800 transition-colors"
        >
          <ArrowLeft className="h-5 w-5" />
          {currentStep === 1 ? "취소" : "이전"}
        </button>
      </div>

      {/* 진행 단계 표시 */}
      <div className="mb-8">
        <div className="flex items-center justify-center mb-6">
          {steps.map((step, index) => {
            const Icon = step.icon;
            return (
              <div key={step.id} className="flex items-center">
                <div className="flex flex-col items-center">
                  <div
                    className={`w-12 h-12 rounded-full flex items-center justify-center text-sm font-medium transition-all duration-300 ${
                      currentStep > step.id
                        ? "bg-teal-600 text-white shadow-lg"
                        : currentStep === step.id
                        ? "bg-teal-100 text-teal-600 border-2 border-teal-500 shadow-md"
                        : "bg-gray-100 text-gray-400"
                    }`}
                  >
                    {currentStep > step.id ? (
                      <Check className="w-6 h-6" />
                    ) : (
                      <Icon className="w-6 h-6" />
                    )}
                  </div>
                  <div className="mt-3 text-center">
                    <p
                      className={`text-sm font-medium transition-colors ${
                        currentStep >= step.id
                          ? "text-gray-900"
                          : "text-gray-400"
                      }`}
                    >
                      {step.title}
                    </p>
                    <p className="text-xs text-gray-500 mt-1">{step.desc}</p>
                  </div>
                </div>
                {index < steps.length - 1 && (
                  <div
                    className={`w-20 h-1 mx-3 transition-colors duration-300 ${
                      currentStep > step.id ? "bg-teal-600" : "bg-gray-200"
                    }`}
                  />
                )}
              </div>
            );
          })}
        </div>
      </div>

      {/* 메인 콘텐츠 */}
      <div className="bg-white rounded-2xl shadow-lg border border-gray-200 p-8">
        {/* 2단계: 비밀번호 설정 */}
        {currentStep === 2 && (
          <div>
            <div className="text-center mb-8">
              <div className="w-20 h-20 bg-gradient-to-br from-purple-400 to-purple-600 rounded-full flex items-center justify-center mx-auto mb-6 shadow-lg">
                <CreditCard className="w-10 h-10 text-white" />
              </div>
              <h2 className="text-3xl font-bold text-gray-900 mb-3">
                계좌 비밀번호를 설정해주세요
              </h2>
              <p className="text-gray-600">
                모임통장 사용을 위한 4자리 비밀번호를 설정해주세요
              </p>
            </div>

            <div className="space-y-6">
              {/* 계좌 비밀번호 설정 */}
              <div className="space-y-4">
                <div>
                  <label className="block text-sm font-semibold text-gray-700 mb-3">
                    계좌 비밀번호 (4자리)
                  </label>
                  <input
                    type="password"
                    value={formData.accountPassword}
                    onChange={(e) =>
                      handleInputChange("accountPassword", e.target.value)
                    }
                    placeholder="4자리 숫자 입력"
                    maxLength={4}
                    className="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-teal-500 focus:border-transparent text-center text-2xl tracking-widest"
                  />
                </div>

                <div>
                  <label className="block text-sm font-semibold text-gray-700 mb-3">
                    비밀번호 확인
                  </label>
                  <input
                    type="password"
                    value={formData.confirmPassword}
                    onChange={(e) =>
                      handleInputChange("confirmPassword", e.target.value)
                    }
                    placeholder="비밀번호 다시 입력"
                    maxLength={4}
                    className="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-teal-500 focus:border-transparent text-center text-2xl tracking-widest"
                  />
                </div>

                {/* 비밀번호 일치 확인 */}
                {formData.confirmPassword &&
                  formData.accountPassword !== formData.confirmPassword && (
                    <div className="flex items-center gap-2 text-red-600 text-sm">
                      <AlertCircle className="w-4 h-4" />
                      비밀번호가 일치하지 않습니다
                    </div>
                  )}
                {formData.confirmPassword &&
                  formData.accountPassword === formData.confirmPassword && (
                    <div className="flex items-center gap-2 text-teal-600 text-sm">
                      <CheckCircle className="w-4 h-4" />
                      비밀번호가 일치합니다
                    </div>
                  )}
              </div>

              {/* 보안 안내 */}
              <div className="bg-white border border-gray-200 rounded-xl p-4">
                <div className="flex items-start gap-3">
                  <AlertCircle className="w-5 h-5 text-gray-600 mt-0.5" />
                  <div className="text-sm text-gray-700">
                    <p className="font-medium mb-1">보안 안내</p>
                    <p>
                      계좌 비밀번호는 모임통장 출금 시 사용됩니다. 안전한
                      비밀번호를 설정하고 타인에게 노출되지 않도록 주의하세요.
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        )}

        {/* 1단계: 모임 정보 */}
        {currentStep === 1 && (
          <div>
            <div className="text-center mb-8">
              <div className="w-20 h-20 bg-gradient-to-br from-teal-400 to-teal-600 rounded-full flex items-center justify-center mx-auto mb-6 shadow-lg">
                <Target className="w-10 h-10 text-white" />
              </div>
              <h2 className="text-3xl font-bold text-gray-900 mb-3">
                모임 통장 정보를 설정해주세요
              </h2>
              <p className="text-gray-600">
                함께 저축할 모임 통장의 이름과 목적을 정해보세요
              </p>
            </div>

            {/* 모임 통장 이름 */}
            <div className="space-y-6">
              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-3">
                  모임 통장 이름
                </label>
                <input
                  type="text"
                  value={formData.groupName}
                  onChange={(e) =>
                    handleInputChange("groupName", e.target.value)
                  }
                  placeholder="예: 우리 가족 여행 통장"
                  className="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-teal-500 focus:border-transparent"
                />
              </div>

              {/* 모임 통장 목적 */}
              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-3">
                  모임 통장 목적
                </label>
                <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
                  {purposes.map((purpose) => {
                    const Icon = purpose.icon;
                    return (
                      <button
                        key={purpose.id}
                        onClick={() =>
                          handleInputChange("groupPurpose", purpose.id)
                        }
                        className={`p-4 rounded-xl border-2 transition-all ${
                          formData.groupPurpose === purpose.id
                            ? "border-teal-500 bg-teal-50"
                            : "border-gray-200 hover:border-gray-300"
                        }`}
                      >
                        <div className="flex flex-col items-center space-y-2">
                          <Icon className="w-8 h-8 text-teal-600" />
                          <div className="text-center">
                            <div className="font-semibold text-gray-900">
                              {purpose.title}
                            </div>
                            <div className="text-xs text-gray-600">
                              {purpose.desc}
                            </div>
                          </div>
                        </div>
                      </button>
                    );
                  })}
                </div>
              </div>
            </div>
          </div>
        )}

        {/* 3단계: 모임원 초대 */}
        {currentStep === 3 && (
          <div>
            <div className="text-center mb-8">
              <div className="w-20 h-20 bg-gradient-to-br from-yellow-400 to-orange-500 rounded-full flex items-center justify-center mx-auto mb-6">
                <UserPlus className="w-10 h-10 text-white" />
              </div>
              <h2 className="text-3xl font-bold text-gray-900 mb-4">
                모임원 초대하기
              </h2>
              <p className="text-gray-600">
                함께 모임통장을 사용할 친구들을 초대해주세요
                <br />
                <span className="text-sm text-gray-500">
                  (선택사항 - 나중에 추가할 수도 있습니다)
                </span>
              </p>
            </div>

            {/* 초대된 멤버 목록 */}
            {invitedMembers.length > 0 && (
              <div className="mb-6">
                <h3 className="font-semibold text-gray-900 mb-3">
                  초대 목록 ({invitedMembers.length}명)
                </h3>
                <div className="space-y-2">
                  {invitedMembers.map((member, index) => (
                    <div
                      key={index}
                      className="flex items-center justify-between bg-gray-50 p-4 rounded-xl"
                    >
                      <div className="flex items-center gap-3">
                        <div className="w-10 h-10 bg-emerald-100 rounded-full flex items-center justify-center">
                          <Users className="w-5 h-5 text-emerald-600" />
                        </div>
                        <div>
                          <p className="font-medium text-gray-900">
                            {member.name}
                          </p>
                          <p className="text-sm text-gray-600">
                            {member.phone}
                          </p>
                        </div>
                      </div>
                      <button
                        onClick={() => {
                          setInvitedMembers(
                            invitedMembers.filter((_, i) => i !== index)
                          );
                        }}
                        className="p-2 hover:bg-gray-200 rounded-lg transition-colors"
                      >
                        <X className="w-4 h-4 text-gray-600" />
                      </button>
                    </div>
                  ))}
                </div>
              </div>
            )}

            {/* 탭 네비게이션 */}
            <div className="flex gap-2 mb-6">
              <button
                onClick={() => setInviteTab("direct")}
                className={`flex-1 py-3 px-4 rounded-xl font-medium transition-all ${
                  inviteTab === "direct"
                    ? "bg-emerald-500 text-white shadow-md"
                    : "bg-gray-100 text-gray-600 hover:bg-gray-200"
                }`}
              >
                직접 입력
              </button>
              <button
                onClick={() => setInviteTab("family")}
                className={`flex-1 py-3 px-4 rounded-xl font-medium transition-all flex items-center justify-center gap-2 ${
                  inviteTab === "family"
                    ? "bg-emerald-500 text-white shadow-md"
                    : "bg-gray-100 text-gray-600 hover:bg-gray-200"
                }`}
              >
                <Heart className="w-4 h-4" />내 가족
              </button>
            </div>

            {/* 직접 입력 탭 */}
            {inviteTab === "direct" && (
              <div className="bg-white border-2 border-dashed border-gray-300 rounded-xl p-6 mb-6">
                <div className="space-y-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      친구 이름
                    </label>
                    <input
                      type="text"
                      value={inviteForm.name}
                      onChange={(e) =>
                        setInviteForm({ ...inviteForm, name: e.target.value })
                      }
                      placeholder="예: 홍길동"
                      className="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-emerald-500 focus:border-transparent"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      전화번호
                    </label>
                    <input
                      type="tel"
                      value={inviteForm.phone}
                      onChange={(e) =>
                        setInviteForm({ ...inviteForm, phone: e.target.value })
                      }
                      placeholder="010-1234-5678"
                      className="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-emerald-500 focus:border-transparent"
                    />
                  </div>
                  <button
                    onClick={() => {
                      if (inviteForm.name && inviteForm.phone) {
                        setInvitedMembers([
                          ...invitedMembers,
                          { ...inviteForm },
                        ]);
                        setInviteForm({ name: "", phone: "" });
                      }
                    }}
                    className="w-full py-3 bg-emerald-500 text-white rounded-xl hover:bg-emerald-600 font-medium transition-colors flex items-center justify-center gap-2"
                  >
                    <Plus className="w-5 h-5" />
                    초대 목록에 추가
                  </button>
                </div>
              </div>
            )}

            {/* 내 가족 탭 */}
            {inviteTab === "family" && (
              <div className="bg-white border border-gray-300 rounded-xl p-6 mb-6">
                {familyMembers.length > 0 ? (
                  <div className="space-y-3">
                    {familyMembers.map((member) => {
                      const isAdded = invitedMembers.some(
                        (m) => m.phone === member.phoneNumber
                      );
                      return (
                        <div
                          key={member.id}
                          className={`flex items-center justify-between p-4 rounded-xl border-2 transition-all ${
                            isAdded
                              ? "border-emerald-500 bg-emerald-50"
                              : "border-gray-200 bg-white hover:border-emerald-300"
                          }`}
                        >
                          <div className="flex items-center gap-3">
                            <div className="w-10 h-10 bg-gradient-to-br from-pink-400 to-red-500 rounded-full flex items-center justify-center">
                              <Users className="w-5 h-5 text-white" />
                            </div>
                            <div>
                              <p className="font-medium text-gray-900">
                                {member.name}
                              </p>
                              <p className="text-sm text-gray-600">
                                {member.phoneNumber}
                              </p>
                            </div>
                          </div>
                          {isAdded ? (
                            <button
                              onClick={() => {
                                setInvitedMembers(
                                  invitedMembers.filter(
                                    (m) => m.phone !== member.phoneNumber
                                  )
                                );
                              }}
                              className="px-4 py-2 bg-gray-200 text-gray-700 rounded-lg font-medium hover:bg-gray-300 transition-colors"
                            >
                              취소
                            </button>
                          ) : (
                            <button
                              onClick={() => {
                                setInvitedMembers([
                                  ...invitedMembers,
                                  {
                                    name: member.name,
                                    phone: member.phoneNumber,
                                    userId: member.userId || member.id,
                                  },
                                ]);
                              }}
                              className="px-4 py-2 bg-emerald-500 text-white rounded-lg font-medium hover:bg-emerald-600 transition-colors"
                            >
                              추가
                            </button>
                          )}
                        </div>
                      );
                    })}
                  </div>
                ) : (
                  <div className="text-center py-8">
                    <Heart className="w-12 h-12 text-gray-300 mx-auto mb-3" />
                    <p className="text-gray-600 mb-2">등록된 가족이 없습니다</p>
                    <p className="text-sm text-gray-500">
                      마이페이지에서 가족을 먼저 등록해주세요
                    </p>
                  </div>
                )}
              </div>
            )}

            {/* 안내 메시지 */}
            <div className="bg-white border border-gray-200 rounded-xl p-4 mb-6">
              <p className="text-sm text-gray-700">
                💡 여기서 초대 목록에 추가하면, 개설 완료 후 바로 카카오톡으로
                초대할 수 있습니다
              </p>
            </div>
          </div>
        )}

        {/* 4단계: 개설 완료 */}
        {currentStep === 4 && (
          <div className="text-center">
            <div className="mb-8">
              <div className="w-24 h-24 bg-gradient-to-br from-emerald-400 to-emerald-600 rounded-full flex items-center justify-center mx-auto mb-6 shadow-lg animate-pulse">
                <CheckCircle className="w-12 h-12 text-white" />
              </div>
              <h2 className="text-3xl font-bold text-gray-900 mb-4">
                모임통장 개설이 완료되었습니다!
              </h2>
              <p className="text-gray-600 text-lg">
                {formData.groupName} 모임통장이 성공적으로 개설되었습니다.
              </p>
            </div>

            {/* 개설 정보 요약 */}
            <div className="bg-white p-8 rounded-2xl border border-gray-200 mb-8">
              <h3 className="text-xl font-bold text-gray-900 mb-6">
                개설된 모임통장 정보
              </h3>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6 text-left">
                <div>
                  <span className="text-gray-600">모임 통장 이름:</span>
                  <span className="font-semibold text-gray-900 ml-2">
                    {formData.groupName}
                  </span>
                </div>
                <div>
                  <span className="text-gray-600">모임 통장 목적:</span>
                  <span className="font-semibold text-gray-900 ml-2">
                    {selectedPurpose?.title || formData.groupPurpose}
                  </span>
                </div>
                <div className="md:col-span-2">
                  <span className="text-gray-600">모임원:</span>
                  <span className="font-semibold text-gray-900 ml-2">
                    {user?.name}
                    {invitedMembers.length > 0 &&
                      invitedMembers
                        .map((member, idx) => `, ${member.name}`)
                        .join("")}
                  </span>
                </div>
                <div>
                  <span className="text-gray-600">계좌번호:</span>
                  <span className="font-semibold text-gray-900 ml-2">
                    {createdAccountNumber || "조회 중..."}
                  </span>
                </div>
              </div>
            </div>

            {/* 다음 단계 안내 */}
            <div className="bg-white border border-gray-200 rounded-xl p-6 mb-8">
              <h3 className="text-lg font-bold text-gray-900 mb-4">
                다음 단계
              </h3>
              <div className="text-left space-y-3">
                <div className="flex items-center gap-3">
                  <div className="w-6 h-6 bg-emerald-500 rounded-full flex items-center justify-center text-white text-sm font-bold">
                    1
                  </div>
                  <span className="text-gray-700">
                    모임원들에게 초대 메시지를 보내주세요
                  </span>
                </div>
                <div className="flex items-center gap-3">
                  <div className="w-6 h-6 bg-emerald-500 rounded-full flex items-center justify-center text-white text-sm font-bold">
                    2
                  </div>
                  <span className="text-gray-700">
                    첫 입금을 통해 모임통장을 활성화하세요
                  </span>
                </div>
                <div className="flex items-center gap-3">
                  <div className="w-6 h-6 bg-emerald-500 rounded-full flex items-center justify-center text-white text-sm font-bold">
                    3
                  </div>
                  <span className="text-gray-700">
                    모임통장이 개설되었습니다!
                  </span>
                </div>
              </div>
            </div>

            {/* 완료 버튼들 */}
            <div className="space-y-4">
              {/* 카카오톡으로 초대하기 버튼 (초대 멤버가 있을 경우) */}
              {invitedMembers.length > 0 && (
                <button
                  onClick={() => {
                    // 카카오톡으로 공유 (로그인 페이지로 연결)
                    const inviteUrl = `${window.location.origin}?auth=true`;

                    if (window.Kakao && window.Kakao.Share) {
                      window.Kakao.Share.sendDefault({
                        objectType: "feed",
                        content: {
                          title: `${formData.groupName} 모임통장에 초대되었습니다!`,
                          description: `${
                            user?.name || "사용자"
                          }님이 ${invitedMembers
                            .map((m) => m.name)
                            .join(", ")}님을 '${
                            formData.groupName
                          }' 모임통장에 초대했습니다.\n\n하나Future 앱 로그인 후 알림에서 초대를 수락해주세요!`,
                          imageUrl:
                            "https://www.hanafn.com/assets/img/ko/info/img-hana-symbol.png",
                          link: {
                            mobileWebUrl: inviteUrl,
                            webUrl: inviteUrl,
                          },
                        },
                        buttons: [
                          {
                            title: "하나Future 열기",
                            link: {
                              mobileWebUrl: inviteUrl,
                              webUrl: inviteUrl,
                            },
                          },
                        ],
                      });
                    } else {
                      alert("카카오톡 공유 기능을 사용할 수 없습니다.");
                    }
                  }}
                  className="w-full py-4 bg-yellow-400 hover:bg-yellow-500 text-gray-900 rounded-xl font-bold text-lg transition-all duration-300 shadow-lg hover:shadow-xl flex items-center justify-center gap-3 group"
                >
                  <svg
                    className="w-6 h-6 group-hover:scale-110 transition-transform"
                    viewBox="0 0 24 24"
                    fill="currentColor"
                  >
                    <path d="M12 3C6.48 3 2 6.58 2 11c0 2.5 1.37 4.77 3.5 6.37v3.63l3.37-1.84c1.03.28 2.12.42 3.13.42 5.52 0 10-3.58 10-8s-4.48-8-10-8z" />
                  </svg>
                  <span>카카오톡으로 초대하기</span>
                </button>
              )}

              <button
                onClick={() =>
                  onComplete({ ...formData, id: createdGroupAccountId })
                }
                className="w-full py-4 bg-gradient-to-r from-emerald-500 to-emerald-600 text-white rounded-xl hover:from-emerald-600 hover:to-emerald-700 font-semibold transition-all shadow-md hover:shadow-lg flex items-center justify-center gap-2"
              >
                <PiggyBank className="w-5 h-5" />
                모임통장 관리하기
              </button>
              <button
                onClick={() =>
                  onComplete({ ...formData, id: createdGroupAccountId })
                }
                className="w-full py-3 border-2 border-gray-300 text-gray-700 rounded-xl hover:bg-gray-50 font-medium transition-colors"
              >
                나중에 설정하기
              </button>
            </div>
          </div>
        )}

        {/* 하단 버튼 */}
        {currentStep < 4 && (
          <div className="flex gap-4 mt-8 pt-6 border-t border-gray-200">
            <button
              onClick={prevStep}
              className="flex-1 py-4 border-2 border-gray-300 text-gray-700 rounded-xl hover:bg-gray-100 font-medium transition-colors"
            >
              {currentStep === 1 ? "취소" : "이전"}
            </button>
            <button
              onClick={nextStep}
              disabled={!canProceed() || isLoading}
              className="flex-1 py-4 bg-gradient-to-r from-teal-600 to-emerald-600 text-white rounded-xl hover:from-teal-700 hover:to-emerald-700 disabled:from-gray-300 disabled:to-gray-400 disabled:cursor-not-allowed font-semibold transition-all shadow-md hover:shadow-lg flex items-center justify-center gap-2"
            >
              {isLoading ? (
                <>
                  <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                  처리 중...
                </>
              ) : (
                <>
                  {currentStep === 3 ? "모임통장 개설하기" : "다음 단계"}
                  <ChevronRight className="w-4 h-4" />
                </>
              )}
            </button>
          </div>
        )}
      </div>

      {/* 친구 초대 모달 */}
      {showInviteModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl p-6 w-full max-w-md shadow-2xl">
            <div className="flex justify-between items-center mb-6">
              <h3 className="text-xl font-bold text-gray-900">친구 초대하기</h3>
              <button
                onClick={() => {
                  setShowInviteModal(false);
                  setInviteForm({ name: "", phone: "" });
                }}
                className="text-gray-400 hover:text-gray-600 transition-colors"
              >
                <X className="w-6 h-6" />
              </button>
            </div>

            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  친구 이름 <span className="text-red-500">*</span>
                </label>
                <input
                  type="text"
                  value={inviteForm.name}
                  onChange={(e) =>
                    setInviteForm((prev) => ({ ...prev, name: e.target.value }))
                  }
                  placeholder="초대할 친구의 이름을 입력하세요"
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-emerald-500 focus:border-emerald-500 transition-colors"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  전화번호 <span className="text-red-500">*</span>
                </label>
                <input
                  type="tel"
                  value={inviteForm.phone}
                  onChange={(e) =>
                    setInviteForm((prev) => ({
                      ...prev,
                      phone: e.target.value,
                    }))
                  }
                  placeholder="010-0000-0000"
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-emerald-500 focus:border-emerald-500 transition-colors"
                />
              </div>
            </div>

            <div className="flex gap-3 mt-8">
              <button
                onClick={() => {
                  setShowInviteModal(false);
                  setInviteForm({ name: "", phone: "" });
                }}
                className="flex-1 py-3 px-4 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors font-medium"
              >
                취소
              </button>
              <button
                onClick={() => {
                  if (!inviteForm.name.trim() || !inviteForm.phone.trim()) {
                    alert("친구 이름과 전화번호를 모두 입력해주세요.");
                    return;
                  }

                  // 토큰 생성 (친구 정보 포함)
                  const inviteToken = btoa(
                    JSON.stringify({
                      groupName: formData.groupName,
                      inviteeName: inviteForm.name,
                      inviteePhone: inviteForm.phone,
                      createdAt: new Date().toISOString(),
                    })
                  );

                  // 초대 링크는 로그인 페이지로 연결 (알림을 통해 수락)
                  const inviteUrl = `${window.location.origin}?auth=true`;

                  if (window.Kakao) {
                    window.Kakao.Share.sendDefault({
                      objectType: "feed",
                      content: {
                        title: `${formData.groupName} 모임통장에 초대되었습니다!`,
                        description: `${user?.name || "사용자"}님이 ${
                          inviteForm.name
                        }님을 '${
                          formData.groupName
                        }' 모임통장에 초대했습니다.\n\n하나Future 앱 로그인 후 알림에서 초대를 수락해주세요!`,
                        imageUrl:
                          "https://www.hanafn.com/assets/img/ko/info/img-hana-symbol.png",
                        link: {
                          mobileWebUrl: inviteUrl,
                          webUrl: inviteUrl,
                        },
                      },
                      buttons: [
                        {
                          title: "하나Future 열기",
                          link: {
                            mobileWebUrl: inviteUrl,
                            webUrl: inviteUrl,
                          },
                        },
                      ],
                    });
                  } else {
                    // 카카오톡이 없는 경우 링크 복사
                    navigator.clipboard.writeText(inviteUrl);
                    alert("초대 링크가 복사되었습니다!");
                  }

                  // 모달 닫기
                  setShowInviteModal(false);
                  setInviteForm({ name: "", phone: "" });
                }}
                disabled={!inviteForm.name.trim() || !inviteForm.phone.trim()}
                className="flex-1 py-3 px-4 bg-gradient-to-r from-yellow-400 to-yellow-500 text-gray-900 rounded-lg hover:from-yellow-500 hover:to-yellow-600 disabled:from-gray-300 disabled:to-gray-400 disabled:cursor-not-allowed font-medium transition-all"
              >
                카카오톡으로 초대하기
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
