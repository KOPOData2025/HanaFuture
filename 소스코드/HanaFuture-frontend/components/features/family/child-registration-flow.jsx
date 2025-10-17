"use client";

import React, { useState } from "react";
import {
  ArrowLeft,
  ArrowRight,
  CheckCircle,
  User,
  Calendar,
  GraduationCap,
  Heart,
  Camera,
  Gift,
} from "lucide-react";

// API Base URL - 환경변수 사용
const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080/api";

export function ChildRegistrationFlow({ onComplete, onCancel, userInfo }) {
  const [currentStep, setCurrentStep] = useState(1); // 1: 기본정보, 2: 학교정보, 3: 용돈설정, 4: 확인
  const [formData, setFormData] = useState({
    name: "",
    birthDate: "",
    gender: "",
    nickname: "",
    schoolName: "",
    grade: "",
    classNumber: "",
    weeklyAllowance: "",
    monthlyAllowance: "",
    allowanceCycle: "WEEKLY",
    profileImage: null,
  });
  const [isLoading, setIsLoading] = useState(false);

  const handleSubmit = async () => {
    if (currentStep < 4) return;

    setIsLoading(true);
    try {
      // 자녀 등록 API 호출
      const response = await fetch(`${API_BASE_URL}/children`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          ...(user?.token && { Authorization: `Bearer ${user.token}` }),
        },
        body: JSON.stringify({
          parentUserId: userInfo?.userId || 1,
          name: formData.name,
          birthDate: formData.birthDate,
          gender: formData.gender,
          nickname: formData.nickname,
          schoolName: formData.schoolName,
          grade: parseInt(formData.grade) || null,
          classNumber: parseInt(formData.classNumber) || null,
          weeklyAllowance: parseFloat(formData.weeklyAllowance) || null,
          monthlyAllowance: parseFloat(formData.monthlyAllowance) || null,
          allowanceCycle: formData.allowanceCycle,
        }),
      });

      const result = await response.json();

      if (result.success) {
        onComplete({
          child: result.data,
          formData,
        });
      } else {
        alert(
          "자녀 등록에 실패했습니다: " + (result.message || "알 수 없는 오류")
        );
      }
    } catch (error) {
      console.error("자녀 등록 오류:", error);
      alert("자녀 등록 중 오류가 발생했습니다.");
    } finally {
      setIsLoading(false);
    }
  };

  const validateStep1 = () => {
    return formData.name && formData.birthDate && formData.gender;
  };

  const validateStep2 = () => {
    return true; // 학교 정보는 선택사항
  };

  const validateStep3 = () => {
    if (formData.allowanceCycle === "WEEKLY") {
      return formData.weeklyAllowance;
    } else if (formData.allowanceCycle === "MONTHLY") {
      return formData.monthlyAllowance;
    }
    return true;
  };

  const handleNextStep = () => {
    if (currentStep === 1 && validateStep1()) {
      setCurrentStep(2);
    } else if (currentStep === 2 && validateStep2()) {
      setCurrentStep(3);
    } else if (currentStep === 3 && validateStep3()) {
      setCurrentStep(4);
    }
  };

  const handlePrevStep = () => {
    if (currentStep > 1) {
      setCurrentStep(currentStep - 1);
    } else {
      onCancel();
    }
  };

  const calculateAge = (birthDate) => {
    if (!birthDate) return "";
    const today = new Date();
    const birth = new Date(birthDate);
    const age = today.getFullYear() - birth.getFullYear();
    const monthDiff = today.getMonth() - birth.getMonth();

    if (
      monthDiff < 0 ||
      (monthDiff === 0 && today.getDate() < birth.getDate())
    ) {
      return age - 1;
    }
    return age;
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
            <h1 className="text-lg font-semibold text-gray-900">자녀 등록</h1>
            <div className="text-sm text-gray-500">{currentStep}/4</div>
          </div>
        </div>
      </div>

      {/* 진행 단계 */}
      <div className="max-w-2xl mx-auto px-4 py-4">
        <div className="flex items-center justify-between mb-6">
          {[1, 2, 3, 4].map((num) => (
            <div key={num} className="flex items-center">
              <div
                className={`w-8 h-8 rounded-full flex items-center justify-center text-sm font-semibold ${
                  num <= currentStep
                    ? "bg-purple-600 text-white"
                    : "bg-gray-200 text-gray-400"
                }`}
              >
                {num < currentStep ? <CheckCircle className="w-4 h-4" /> : num}
              </div>
              {num < 4 && (
                <div
                  className={`w-12 h-0.5 mx-2 ${
                    num < currentStep ? "bg-purple-600" : "bg-gray-200"
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
                src="/hanacharacter/hanacharacter4.png"
                alt="자녀 등록 캐릭터"
                className="w-24 h-24 object-contain mx-auto mb-4"
              />
              <h2 className="text-xl font-bold text-gray-900 mb-2">
                자녀 정보를 입력해주세요
              </h2>
              <p className="text-gray-600">
                아이카드 발급을 위한 기본 정보입니다
              </p>
            </div>

            {/* 자녀 이름 */}
            <div className="bg-white rounded-2xl p-6 shadow-sm">
              <label className="block text-sm font-medium text-gray-700 mb-3">
                자녀 이름 *
              </label>
              <input
                type="text"
                value={formData.name}
                onChange={(e) =>
                  setFormData({ ...formData, name: e.target.value })
                }
                placeholder="예: 김하나"
                className="w-full h-12 px-4 border border-gray-300 rounded-xl focus:border-purple-500 focus:outline-none"
                required
              />
            </div>

            {/* 생년월일 */}
            <div className="bg-white rounded-2xl p-6 shadow-sm">
              <label className="block text-sm font-medium text-gray-700 mb-3">
                생년월일 *
              </label>
              <input
                type="date"
                value={formData.birthDate}
                onChange={(e) =>
                  setFormData({ ...formData, birthDate: e.target.value })
                }
                className="w-full h-12 px-4 border border-gray-300 rounded-xl focus:border-purple-500 focus:outline-none"
                required
              />
              {formData.birthDate && (
                <p className="text-sm text-purple-600 mt-2">
                  만 {calculateAge(formData.birthDate)}세
                </p>
              )}
            </div>

            {/* 성별 */}
            <div className="bg-white rounded-2xl p-6 shadow-sm">
              <label className="block text-sm font-medium text-gray-700 mb-3">
                성별 *
              </label>
              <div className="grid grid-cols-2 gap-3">
                <button
                  type="button"
                  onClick={() => setFormData({ ...formData, gender: "MALE" })}
                  className={`p-4 rounded-xl border-2 transition-all ${
                    formData.gender === "MALE"
                      ? "border-blue-500 bg-blue-50"
                      : "border-gray-200 hover:border-gray-300"
                  }`}
                >
                  <div className="text-2xl mb-2">👦</div>
                  <div className="font-semibold text-gray-900">남자</div>
                </button>
                <button
                  type="button"
                  onClick={() => setFormData({ ...formData, gender: "FEMALE" })}
                  className={`p-4 rounded-xl border-2 transition-all ${
                    formData.gender === "FEMALE"
                      ? "border-pink-500 bg-pink-50"
                      : "border-gray-200 hover:border-gray-300"
                  }`}
                >
                  <div className="text-2xl mb-2">👧</div>
                  <div className="font-semibold text-gray-900">여자</div>
                </button>
              </div>
            </div>

            {/* 닉네임 */}
            <div className="bg-white rounded-2xl p-6 shadow-sm">
              <label className="block text-sm font-medium text-gray-700 mb-3">
                닉네임 (선택)
              </label>
              <input
                type="text"
                value={formData.nickname}
                onChange={(e) =>
                  setFormData({ ...formData, nickname: e.target.value })
                }
                placeholder="예: 하나공주, 미래왕자"
                className="w-full h-12 px-4 border border-gray-300 rounded-xl focus:border-purple-500 focus:outline-none"
              />
              <p className="text-xs text-gray-500 mt-2">
                아이카드에 표시될 귀여운 이름을 지어주세요
              </p>
            </div>
          </>
        )}

        {/* 2단계: 학교 정보 */}
        {currentStep === 2 && (
          <>
            <div className="bg-white rounded-2xl p-6 shadow-sm text-center">
              <img
                src="/hanacharacter/hanacharacter5.png"
                alt="학교 정보 캐릭터"
                className="w-24 h-24 object-contain mx-auto mb-4"
              />
              <h2 className="text-xl font-bold text-gray-900 mb-2">
                학교 정보를 입력해주세요
              </h2>
              <p className="text-gray-600">
                학교 관련 서비스 제공을 위한 정보입니다 (선택사항)
              </p>
            </div>

            {/* 학교명 */}
            <div className="bg-white rounded-2xl p-6 shadow-sm">
              <label className="block text-sm font-medium text-gray-700 mb-3">
                학교명
              </label>
              <input
                type="text"
                value={formData.schoolName}
                onChange={(e) =>
                  setFormData({ ...formData, schoolName: e.target.value })
                }
                placeholder="예: 하나초등학교"
                className="w-full h-12 px-4 border border-gray-300 rounded-xl focus:border-purple-500 focus:outline-none"
              />
            </div>

            {/* 학년, 반 */}
            <div className="bg-white rounded-2xl p-6 shadow-sm">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    학년
                  </label>
                  <select
                    value={formData.grade}
                    onChange={(e) =>
                      setFormData({ ...formData, grade: e.target.value })
                    }
                    className="w-full h-12 px-4 border border-gray-300 rounded-xl focus:border-purple-500 focus:outline-none"
                  >
                    <option value="">선택하세요</option>
                    {[1, 2, 3, 4, 5, 6].map((grade) => (
                      <option key={grade} value={grade}>
                        {grade}학년
                      </option>
                    ))}
                  </select>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    반
                  </label>
                  <input
                    type="number"
                    value={formData.classNumber}
                    onChange={(e) =>
                      setFormData({ ...formData, classNumber: e.target.value })
                    }
                    placeholder="예: 3"
                    className="w-full h-12 px-4 border border-gray-300 rounded-xl focus:border-purple-500 focus:outline-none"
                    min="1"
                    max="20"
                  />
                </div>
              </div>
            </div>
          </>
        )}

        {/* 3단계: 용돈 설정 */}
        {currentStep === 3 && (
          <>
            <div className="bg-white rounded-2xl p-6 shadow-sm text-center">
              <img
                src="/hanacharacter/hanacharacter9.png"
                alt="용돈 설정 캐릭터"
                className="w-24 h-24 object-contain mx-auto mb-4"
              />
              <h2 className="text-xl font-bold text-gray-900 mb-2">
                용돈을 설정해주세요
              </h2>
              <p className="text-gray-600">
                자녀의 나이와 상황에 맞는 용돈을 설정하세요
              </p>
            </div>

            {/* 용돈 주기 선택 */}
            <div className="bg-white rounded-2xl p-6 shadow-sm">
              <label className="block text-sm font-medium text-gray-700 mb-3">
                용돈 주기 *
              </label>
              <div className="grid grid-cols-2 gap-3">
                <button
                  type="button"
                  onClick={() =>
                    setFormData({ ...formData, allowanceCycle: "WEEKLY" })
                  }
                  className={`p-4 rounded-xl border-2 transition-all ${
                    formData.allowanceCycle === "WEEKLY"
                      ? "border-purple-500 bg-purple-50"
                      : "border-gray-200 hover:border-gray-300"
                  }`}
                >
                  <Calendar className="w-6 h-6 mx-auto mb-2 text-purple-600" />
                  <div className="font-semibold text-gray-900">매주</div>
                  <div className="text-xs text-gray-600">초등학생 추천</div>
                </button>
                <button
                  type="button"
                  onClick={() =>
                    setFormData({ ...formData, allowanceCycle: "MONTHLY" })
                  }
                  className={`p-4 rounded-xl border-2 transition-all ${
                    formData.allowanceCycle === "MONTHLY"
                      ? "border-purple-500 bg-purple-50"
                      : "border-gray-200 hover:border-gray-300"
                  }`}
                >
                  <Calendar className="w-6 h-6 mx-auto mb-2 text-blue-600" />
                  <div className="font-semibold text-gray-900">매월</div>
                  <div className="text-xs text-gray-600">중고생 추천</div>
                </button>
              </div>
            </div>

            {/* 용돈 금액 */}
            <div className="bg-white rounded-2xl p-6 shadow-sm">
              {formData.allowanceCycle === "WEEKLY" ? (
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    주간 용돈 *
                  </label>
                  <input
                    type="number"
                    value={formData.weeklyAllowance}
                    onChange={(e) =>
                      setFormData({
                        ...formData,
                        weeklyAllowance: e.target.value,
                      })
                    }
                    placeholder="예: 5000 (초등 저학년 평균)"
                    className="w-full h-12 px-4 border border-gray-300 rounded-xl focus:border-purple-500 focus:outline-none"
                    required
                  />
                  <p className="text-xs text-gray-500 mt-2">
                    초등 저학년 평균: 주 5,000원 / 초등 고학년 평균: 주 8,000원
                  </p>
                </div>
              ) : (
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    월간 용돈 *
                  </label>
                  <input
                    type="number"
                    value={formData.monthlyAllowance}
                    onChange={(e) =>
                      setFormData({
                        ...formData,
                        monthlyAllowance: e.target.value,
                      })
                    }
                    placeholder="예: 50000 (중학생 평균)"
                    className="w-full h-12 px-4 border border-gray-300 rounded-xl focus:border-purple-500 focus:outline-none"
                    required
                  />
                  <p className="text-xs text-gray-500 mt-2">
                    중학생 평균: 월 50,000원 / 고등학생 평균: 월 80,000원
                  </p>
                </div>
              )}
            </div>

            {/* 용돈 가이드 */}
            <div className="bg-purple-50 rounded-2xl p-6 border border-purple-200">
              <h4 className="font-semibold text-purple-900 mb-3">
                💡 용돈 가이드
              </h4>
              <div className="space-y-2 text-sm text-purple-700">
                <p>
                  • <strong>초등 저학년</strong>: 주 3,000~7,000원 (평균
                  5,000원)
                </p>
                <p>
                  • <strong>초등 고학년</strong>: 주 5,000~10,000원 (평균
                  8,000원)
                </p>
                <p>
                  • <strong>중학생</strong>: 월 30,000~70,000원 (평균 50,000원)
                </p>
                <p>
                  • <strong>고등학생</strong>: 월 50,000~120,000원 (평균
                  80,000원)
                </p>
              </div>
            </div>
          </>
        )}

        {/* 4단계: 확인 및 등록 */}
        {currentStep === 4 && (
          <>
            <div className="bg-white rounded-2xl p-6 shadow-sm text-center">
              <img
                src="/hanacharacter/hanacharacter15.png"
                alt="등록 완료 캐릭터"
                className="w-24 h-24 object-contain mx-auto mb-4"
              />
              <h2 className="text-xl font-bold text-gray-900 mb-2">
                입력하신 정보를 확인해주세요
              </h2>
              <p className="text-gray-600">
                자녀 등록 후 아이카드를 발급받을 수 있습니다
              </p>
            </div>

            {/* 입력 정보 요약 */}
            <div className="bg-white rounded-2xl p-6 shadow-sm">
              <h3 className="font-semibold text-gray-900 mb-4">자녀 정보</h3>
              <div className="space-y-4">
                <div className="flex justify-between">
                  <span className="text-gray-600">이름</span>
                  <span className="font-semibold">{formData.name}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-600">나이</span>
                  <span className="font-semibold">
                    만 {calculateAge(formData.birthDate)}세
                  </span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-600">성별</span>
                  <span className="font-semibold">
                    {formData.gender === "MALE" ? "남자" : "여자"}
                  </span>
                </div>
                {formData.nickname && (
                  <div className="flex justify-between">
                    <span className="text-gray-600">닉네임</span>
                    <span className="font-semibold">{formData.nickname}</span>
                  </div>
                )}
                {formData.schoolName && (
                  <div className="flex justify-between">
                    <span className="text-gray-600">학교</span>
                    <span className="font-semibold">
                      {formData.schoolName}
                      {formData.grade && ` ${formData.grade}학년`}
                      {formData.classNumber && ` ${formData.classNumber}반`}
                    </span>
                  </div>
                )}
              </div>
            </div>

            <div className="bg-white rounded-2xl p-6 shadow-sm">
              <h3 className="font-semibold text-gray-900 mb-4">용돈 설정</h3>
              <div className="space-y-4">
                <div className="flex justify-between">
                  <span className="text-gray-600">용돈 주기</span>
                  <span className="font-semibold">
                    {formData.allowanceCycle === "WEEKLY" ? "매주" : "매월"}
                  </span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-600">용돈 금액</span>
                  <span className="font-semibold text-purple-600">
                    {formData.allowanceCycle === "WEEKLY"
                      ? `주 ${parseInt(
                          formData.weeklyAllowance || 0
                        ).toLocaleString()}원`
                      : `월 ${parseInt(
                          formData.monthlyAllowance || 0
                        ).toLocaleString()}원`}
                  </span>
                </div>
              </div>
            </div>

            <div className="bg-purple-50 rounded-2xl p-6 border border-purple-200">
              <div className="flex items-center space-x-3 mb-3">
                <Gift className="w-5 h-5 text-purple-600" />
                <h4 className="font-semibold text-purple-900">등록 후 혜택</h4>
              </div>
              <div className="space-y-2 text-sm text-purple-700">
                <p>• 아이카드 무료 발급</p>
                <p>• 부모 계좌/모임통장에서 간편 충전</p>
                <p>• 사용 내역 실시간 알림</p>
                <p>• 금융 교육 프로그램 참여</p>
                <p>• 리워드 포인트 적립</p>
              </div>
            </div>
          </>
        )}

        {/* 버튼 영역 */}
        <div className="sticky bottom-4">
          {currentStep < 4 ? (
            <button
              onClick={handleNextStep}
              disabled={
                (currentStep === 1 && !validateStep1()) ||
                (currentStep === 2 && !validateStep2()) ||
                (currentStep === 3 && !validateStep3())
              }
              className={`w-full py-4 rounded-2xl font-semibold text-lg transition-all duration-300 flex items-center justify-center ${
                (currentStep === 1 && validateStep1()) ||
                (currentStep === 2 && validateStep2()) ||
                (currentStep === 3 && validateStep3())
                  ? "bg-gradient-to-r from-purple-500 to-pink-500 text-white hover:from-purple-600 hover:to-pink-600 shadow-lg"
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
                  등록 중...
                </>
              ) : (
                <>
                  <CheckCircle className="w-5 h-5 mr-2" />
                  자녀 등록 완료
                </>
              )}
            </button>
          )}
        </div>
      </div>
    </div>
  );
}
