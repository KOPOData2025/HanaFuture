"use client";

import React, { useState, useEffect } from "react";
import { ArrowLeft, Check } from "lucide-react";
import { useAuth } from "../../../contexts/AuthContext";

// API Base URL - 환경변수 사용
const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080/api";

export default function TogetherSavingsCreationFlow({ onComplete, onCancel }) {
  const { user } = useAuth();
  const [step, setStep] = useState(1);
  const [accounts, setAccounts] = useState([]);
  const [selectedAccount, setSelectedAccount] = useState(null);
  const [loading, setLoading] = useState(true);

  const [formData, setFormData] = useState({
    accountId: "",
    accountName: "",
    goalAmount: "",
    duration: 12,
    monthlyDeposit: "",
    autoTransferDate: "",
    interestPaymentMethod: "만기일시지급식",
    maturityAction: "만기시 자동해지",
    smsNotification: true,
    inviteFamilyMember: false,
    familyMemberName: "",
  });

  // 계좌 목록 불러오기
  useEffect(() => {
    const fetchAccounts = async () => {
      if (!user?.id) return;

      try {
        setLoading(true);
        const response = await fetch(
          `${API_BASE_URL}/integrated-banking/accounts/withdrawable/user/${user.id}`,
          {
            headers: {
              "Content-Type": "application/json",
              ...(user?.token && { Authorization: `Bearer ${user.token}` }),
            },
          }
        );

        if (response.ok) {
          const result = await response.json();
          console.log("출금 가능 계좌 조회 결과:", result);

          // API 응답 구조: { success: true, message: "...", data: [...] }
          // withdrawable 엔드포인트는 data가 바로 배열
          const accountList = Array.isArray(result?.data) ? result.data : [];
          setAccounts(accountList);

          // 첫 번째 계좌를 기본 선택
          if (accountList.length > 0) {
            setSelectedAccount(accountList[0]);
            setFormData({ ...formData, accountId: accountList[0].accountNum });
          }
        } else {
          console.error("계좌 목록 불러오기 실패");
          setAccounts([]);
        }
      } catch (error) {
        console.error("계좌 목록 불러오기 오류:", error);
        setAccounts([]);
      } finally {
        setLoading(false);
      }
    };

    fetchAccounts();
  }, [user?.id]);

  // 계좌 선택 핸들러
  const handleAccountChange = (accountNum) => {
    const account = accounts.find((acc) => acc.accountNum === accountNum);
    setSelectedAccount(account);
    setFormData({ ...formData, accountId: accountNum });
  };

  // 금액 포맷팅
  const formatNumber = (num) => {
    if (!num) return "0";
    return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
  };

  // 금액 입력 처리
  const handleAmountChange = (field, value) => {
    const numericValue = value.replace(/[^0-9]/g, "");
    setFormData({ ...formData, [field]: numericValue });
  };

  // 금액 버튼 클릭 처리
  const handleAmountButton = (field, amount) => {
    const currentValue = parseInt(formData[field] || 0);
    const newValue = currentValue + amount;
    setFormData({ ...formData, [field]: newValue.toString() });
  };

  // 입력 페이지
  const renderInputPage = () => (
    <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="bg-white rounded-2xl shadow-lg p-8">
        {/* 기본정보 */}
        <div className="mb-10">
          <h3 className="text-2xl font-bold text-gray-900 mb-6 pb-4 border-b-2 border-gray-200">
            기본정보
          </h3>
          <div className="space-y-6">
            {/* 출금계좌번호 */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 items-center">
              <label className="text-gray-700 font-semibold">
                출금계좌번호 <span className="text-red-500">*</span>
              </label>
              <div className="md:col-span-2">
                {loading ? (
                  <div className="text-gray-500">
                    계좌 목록을 불러오는 중...
                  </div>
                ) : accounts.length === 0 ? (
                  <div className="text-red-500">
                    연결된 계좌가 없습니다. 오픈뱅킹에서 계좌를 연결해주세요.
                  </div>
                ) : (
                  <>
                    <select
                      value={formData.accountId}
                      onChange={(e) => handleAccountChange(e.target.value)}
                      className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-teal-500"
                    >
                      {accounts.map((account, index) => (
                        <option key={index} value={account.accountNum}>
                          {account.accountNum} [
                          {account.productName || account.bankName}]
                        </option>
                      ))}
                    </select>
                    {selectedAccount && (
                      <>
                        <p className="text-sm text-teal-600 mt-2">
                          계좌 이름:{" "}
                          {selectedAccount.productName ||
                            selectedAccount.bankName}
                        </p>
                        <p className="text-sm text-teal-600">
                          잔액: {formatNumber(selectedAccount.balanceAmt || 0)}{" "}
                          원
                        </p>
                      </>
                    )}
                  </>
                )}
              </div>
            </div>

            {/* 계좌 비밀번호 */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 items-center">
              <label className="text-gray-700 font-semibold">
                계좌 비밀번호 <span className="text-red-500">*</span>
              </label>
              <div className="md:col-span-2">
                <input
                  type="password"
                  placeholder="••••"
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-teal-500"
                  maxLength={4}
                />
              </div>
            </div>
          </div>
        </div>

        {/* 상품정보 */}
        <div className="mb-10">
          <h3 className="text-2xl font-bold text-gray-900 mb-6 pb-4 border-b-2 border-gray-200">
            상품정보
          </h3>
          <div className="space-y-6">
            {/* 적금 별명 */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 items-center">
              <label className="text-gray-700 font-semibold">
                적금 별명 <span className="text-red-500">*</span>
              </label>
              <div className="md:col-span-2">
                <input
                  type="text"
                  value={formData.accountName}
                  onChange={(e) =>
                    setFormData({ ...formData, accountName: e.target.value })
                  }
                  placeholder="별명을 입력하세요"
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-teal-500"
                />
              </div>
            </div>

            {/* 목표 금액 */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 items-center">
              <label className="text-gray-700 font-semibold">
                목표 금액 <span className="text-red-500">*</span>
              </label>
              <div className="md:col-span-2 space-y-3">
                <div className="flex gap-2">
                  <input
                    type="text"
                    value={formatNumber(formData.goalAmount)}
                    onChange={(e) =>
                      handleAmountChange("goalAmount", e.target.value)
                    }
                    placeholder="0"
                    className="flex-1 px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-teal-500 text-right"
                  />
                  <span className="flex items-center text-gray-700 font-semibold px-3">
                    원
                  </span>
                </div>
              </div>
            </div>

            {/* 가입 기간 */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 items-center">
              <label className="text-gray-700 font-semibold">
                가입 기간 <span className="text-red-500">*</span>
              </label>
              <div className="md:col-span-2">
                <select
                  value={formData.duration}
                  onChange={(e) =>
                    setFormData({
                      ...formData,
                      duration: parseInt(e.target.value),
                    })
                  }
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-teal-500"
                >
                  <option value={3}>3개월</option>
                  <option value={6}>6개월</option>
                  <option value={12}>12개월</option>
                  <option value={18}>18개월</option>
                  <option value={24}>24개월</option>
                  <option value={36}>36개월</option>
                </select>
              </div>
            </div>

            {/* 신규금액 */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 items-center">
              <label className="text-gray-700 font-semibold">
                신규금액 <span className="text-red-500">*</span>
              </label>
              <div className="md:col-span-2 space-y-3">
                <div className="flex flex-wrap gap-2">
                  <button
                    onClick={() =>
                      handleAmountButton("monthlyDeposit", 1000000)
                    }
                    className="px-4 py-2 bg-gray-100 hover:bg-teal-50 border border-gray-300 rounded-lg transition-colors"
                  >
                    +100만원
                  </button>
                  <button
                    onClick={() => handleAmountButton("monthlyDeposit", 500000)}
                    className="px-4 py-2 bg-gray-100 hover:bg-teal-50 border border-gray-300 rounded-lg transition-colors"
                  >
                    +50만원
                  </button>
                  <button
                    onClick={() => handleAmountButton("monthlyDeposit", 100000)}
                    className="px-4 py-2 bg-gray-100 hover:bg-teal-50 border border-gray-300 rounded-lg transition-colors"
                  >
                    +10만원
                  </button>
                  <button
                    onClick={() => handleAmountButton("monthlyDeposit", 50000)}
                    className="px-4 py-2 bg-gray-100 hover:bg-teal-50 border border-gray-300 rounded-lg transition-colors"
                  >
                    +5만원
                  </button>
                  <button
                    onClick={() => handleAmountButton("monthlyDeposit", 10000)}
                    className="px-4 py-2 bg-gray-100 hover:bg-teal-50 border border-gray-300 rounded-lg transition-colors"
                  >
                    +1만원
                  </button>
                  <button
                    onClick={() =>
                      setFormData({ ...formData, monthlyDeposit: "" })
                    }
                    className="px-4 py-2 bg-gray-100 hover:bg-red-50 border border-gray-300 rounded-lg transition-colors"
                  >
                    정정
                  </button>
                </div>
                <div className="flex gap-2">
                  <input
                    type="text"
                    value={formatNumber(formData.monthlyDeposit)}
                    onChange={(e) =>
                      handleAmountChange("monthlyDeposit", e.target.value)
                    }
                    placeholder="0"
                    className="flex-1 px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-teal-500 text-right"
                  />
                  <span className="flex items-center text-gray-700 font-semibold px-3">
                    원
                  </span>
                </div>
              </div>
            </div>

            {/* 자동이체일 */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 items-center">
              <label className="text-gray-700 font-semibold">
                자동이체일 <span className="text-red-500">*</span>
              </label>
              <div className="md:col-span-2">
                <select
                  value={formData.autoTransferDate}
                  onChange={(e) =>
                    setFormData({
                      ...formData,
                      autoTransferDate: e.target.value,
                    })
                  }
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-teal-500"
                >
                  <option value="">선택하세요</option>
                  {Array.from({ length: 28 }, (_, i) => i + 1).map((day) => (
                    <option key={day} value={day}>
                      {day}일
                    </option>
                  ))}
                </select>
              </div>
            </div>

            {/* 이자 지급 방식 */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 items-center">
              <label className="text-gray-700 font-semibold">
                이자 지급 방식 <span className="text-red-500">*</span>
              </label>
              <div className="md:col-span-2">
                <select
                  value={formData.interestPaymentMethod}
                  onChange={(e) =>
                    setFormData({
                      ...formData,
                      interestPaymentMethod: e.target.value,
                    })
                  }
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-teal-500"
                >
                  <option value="만기일시지급식">만기일시지급식</option>
                  <option value="월지급식">월지급식</option>
                </select>
              </div>
            </div>

            {/* 만기 해지 구분 */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 items-center">
              <label className="text-gray-700 font-semibold">
                만기 해지 구분 <span className="text-red-500">*</span>
              </label>
              <div className="md:col-span-2 flex gap-4">
                <label className="flex items-center gap-2 cursor-pointer">
                  <input
                    type="radio"
                    name="maturityAction"
                    value="직접해지"
                    checked={formData.maturityAction === "직접해지"}
                    onChange={(e) =>
                      setFormData({
                        ...formData,
                        maturityAction: e.target.value,
                      })
                    }
                    className="w-5 h-5"
                  />
                  <span>직접해지</span>
                </label>
                <label className="flex items-center gap-2 cursor-pointer">
                  <input
                    type="radio"
                    name="maturityAction"
                    value="만기시 자동해지"
                    checked={formData.maturityAction === "만기시 자동해지"}
                    onChange={(e) =>
                      setFormData({
                        ...formData,
                        maturityAction: e.target.value,
                      })
                    }
                    className="w-5 h-5"
                  />
                  <span>만기시 자동해지</span>
                </label>
              </div>
            </div>

            {/* 예/적금 만기 SMS 통보 */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 items-center">
              <label className="text-gray-700 font-semibold">
                예/적금 만기 SMS 통보 <span className="text-red-500">*</span>
              </label>
              <div className="md:col-span-2 flex gap-4">
                <label className="flex items-center gap-2 cursor-pointer">
                  <input
                    type="radio"
                    name="smsNotification"
                    checked={formData.smsNotification === true}
                    onChange={() =>
                      setFormData({ ...formData, smsNotification: true })
                    }
                    className="w-5 h-5"
                  />
                  <span>신청함</span>
                </label>
                <label className="flex items-center gap-2 cursor-pointer">
                  <input
                    type="radio"
                    name="smsNotification"
                    checked={formData.smsNotification === false}
                    onChange={() =>
                      setFormData({ ...formData, smsNotification: false })
                    }
                    className="w-5 h-5"
                  />
                  <span>신청안함</span>
                </label>
              </div>
            </div>
          </div>
        </div>

        {/* 함께 할 가족 구성원 */}
        <div className="mb-8">
          <h3 className="text-2xl font-bold text-gray-900 mb-6 pb-4 border-b-2 border-gray-200">
            함께 할 가족 구성원
          </h3>
          <div className="space-y-4">
            <div className="flex gap-4">
              <button
                onClick={() =>
                  setFormData({ ...formData, inviteFamilyMember: true })
                }
                className={`flex-1 p-6 rounded-xl border-2 transition-all ${
                  formData.inviteFamilyMember
                    ? "border-teal-500 bg-teal-50"
                    : "border-gray-300"
                }`}
              >
                <div className="flex items-center justify-center gap-3">
                  <img
                    src="/hanacharacter/hanacharacter5.png"
                    alt="가족 초대"
                    className="w-20 h-20"
                  />
                  <div className="text-left">
                    <p className="font-bold text-lg">가족 초대하기</p>
                    <p className="text-sm text-gray-600">+0.3% 우대금리</p>
                  </div>
                </div>
              </button>
            </div>

            {formData.inviteFamilyMember && (
              <div className="p-6 bg-gray-50 rounded-xl border border-gray-200">
                <h4 className="font-bold text-gray-900 mb-4">가족 정보 입력</h4>
                <div className="space-y-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      이름 <span className="text-red-500">*</span>
                    </label>
                    <input
                      type="text"
                      value={formData.familyMemberName}
                      onChange={(e) =>
                        setFormData({
                          ...formData,
                          familyMemberName: e.target.value,
                        })
                      }
                      placeholder="가족 구성원의 이름을 입력하세요"
                      className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-teal-500"
                    />
                  </div>
                </div>
              </div>
            )}
          </div>
        </div>

        {/* 버튼 */}
        <div className="flex justify-center gap-4 pt-8">
          <button
            onClick={onCancel}
            className="px-8 py-3 bg-gray-200 hover:bg-gray-300 text-gray-700 rounded-lg font-semibold transition-colors"
          >
            이전
          </button>
          <button
            onClick={() => setStep(2)}
            disabled={
              !formData.accountName ||
              !formData.goalAmount ||
              !formData.monthlyDeposit ||
              !formData.autoTransferDate ||
              (formData.inviteFamilyMember && !formData.familyMemberName)
            }
            className={`px-8 py-3 rounded-lg font-semibold transition-colors ${
              formData.accountName &&
              formData.goalAmount &&
              formData.monthlyDeposit &&
              formData.autoTransferDate &&
              (!formData.inviteFamilyMember || formData.familyMemberName)
                ? "bg-teal-600 hover:bg-teal-700 text-white"
                : "bg-gray-300 text-gray-500 cursor-not-allowed"
            }`}
          >
            확인
          </button>
        </div>
      </div>
    </div>
  );

  // 확인 페이지
  const renderConfirmPage = () => (
    <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="bg-white rounded-2xl shadow-lg p-8">
        <h2 className="text-3xl font-bold text-gray-900 mb-8 text-center">
          정보 확인
        </h2>

        {/* 기본정보 */}
        <div className="mb-8">
          <h3 className="text-xl font-bold text-gray-900 mb-4 pb-3 border-b border-gray-300">
            기본정보
          </h3>
          <table className="w-full">
            <tbody>
              <tr className="border-b border-gray-200">
                <td className="py-4 px-4 bg-gray-50 font-semibold w-1/3">
                  출금계좌번호
                </td>
                <td className="py-4 px-4">
                  {selectedAccount?.accountNum || ""}
                </td>
              </tr>
              <tr className="border-b border-gray-200">
                <td className="py-4 px-4 bg-gray-50 font-semibold">계좌명</td>
                <td className="py-4 px-4">
                  {selectedAccount?.productName ||
                    selectedAccount?.bankName ||
                    ""}
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        {/* 상품정보 */}
        <div className="mb-8">
          <h3 className="text-xl font-bold text-gray-900 mb-4 pb-3 border-b border-gray-300">
            상품정보
          </h3>
          <table className="w-full">
            <tbody>
              <tr className="border-b border-gray-200">
                <td className="py-4 px-4 bg-gray-50 font-semibold w-1/4">
                  상품종류
                </td>
                <td className="py-4 px-4 w-1/4">함께 목표 적금</td>
                <td className="py-4 px-4 bg-gray-50 font-semibold w-1/4">
                  적금 계좌번호
                </td>
                <td className="py-4 px-4 w-1/4">
                  257-6366-6137
                  <span className="text-xs text-gray-500 ml-2">(자동생성)</span>
                </td>
              </tr>
              <tr className="border-b border-gray-200">
                <td className="py-4 px-4 bg-gray-50 font-semibold">
                  계좌 별명
                </td>
                <td className="py-4 px-4">{formData.accountName}</td>
                <td className="py-4 px-4 bg-gray-50 font-semibold">가입기간</td>
                <td className="py-4 px-4">{formData.duration}개월</td>
              </tr>
              <tr className="border-b border-gray-200">
                <td className="py-4 px-4 bg-gray-50 font-semibold">목표금액</td>
                <td className="py-4 px-4">
                  {formatNumber(formData.goalAmount)}원
                </td>
                <td className="py-4 px-4 bg-gray-50 font-semibold">신규금액</td>
                <td className="py-4 px-4">
                  {formatNumber(formData.monthlyDeposit)} 원
                </td>
              </tr>
              <tr className="border-b border-gray-200">
                <td className="py-4 px-4 bg-gray-50 font-semibold">
                  현재 적용금리
                </td>
                <td className="py-4 px-4">
                  {formData.inviteFamilyMember ? "4.3%" : "4.0%"}
                </td>
                <td className="py-4 px-4 bg-gray-50 font-semibold">
                  이자지급방식
                </td>
                <td className="py-4 px-4">{formData.interestPaymentMethod}</td>
              </tr>
              <tr className="border-b border-gray-200">
                <td className="py-4 px-4 bg-gray-50 font-semibold">
                  만기해지구분
                </td>
                <td className="py-4 px-4">{formData.maturityAction}</td>
                <td className="py-4 px-4 bg-gray-50 font-semibold">
                  자동이체일
                </td>
                <td className="py-4 px-4">{formData.autoTransferDate}일</td>
              </tr>
            </tbody>
          </table>
        </div>

        {/* 가족 구성원 확인 */}
        {formData.inviteFamilyMember && (
          <div className="mb-8">
            <h3 className="text-xl font-bold text-gray-900 mb-4 pb-3 border-b border-gray-300">
              가족 구성원 확인
            </h3>
            <table className="w-full">
              <tbody>
                <tr className="border-b border-gray-200">
                  <td className="py-4 px-4 bg-gray-50 font-semibold w-1/3">
                    가족 구성원 1
                  </td>
                  <td className="py-4 px-4">{formData.familyMemberName}</td>
                </tr>
              </tbody>
            </table>
          </div>
        )}

        {/* 금리 정보 */}
        <div className="mb-8 p-6 bg-gradient-to-r from-teal-50 to-green-50 rounded-xl">
          <div className="flex items-center justify-between mb-4">
            <span className="font-bold text-lg text-gray-900">적용 금리</span>
            <span className="text-3xl font-bold text-teal-600">
              {formData.inviteFamilyMember ? "4.3%" : "4.0%"}
            </span>
          </div>
          <div className="text-sm text-gray-700 space-y-1">
            <p>• 기본금리: 4.0%</p>
            {formData.inviteFamilyMember && <p>• 가족 초대: +0.3%</p>}
            <p className="text-teal-600 font-semibold mt-2">
              * 목표 달성 시 +0.5% 추가 (최대 4.8%)
            </p>
          </div>
        </div>

        {/* 버튼 */}
        <div className="flex justify-center gap-4">
          <button
            onClick={() => setStep(1)}
            className="px-8 py-3 bg-gray-200 hover:bg-gray-300 text-gray-700 rounded-lg font-semibold transition-colors"
          >
            이전
          </button>
          <button
            onClick={() => {
              console.log("함께 적금 개설 완료:", formData);
              onComplete(formData);
            }}
            className="px-8 py-3 bg-teal-600 hover:bg-teal-700 text-white rounded-lg font-semibold transition-colors flex items-center gap-2"
          >
            <Check className="w-5 h-5" />
            가입실행
          </button>
          <button
            onClick={onCancel}
            className="px-8 py-3 bg-gray-200 hover:bg-gray-300 text-gray-700 rounded-lg font-semibold transition-colors"
          >
            취소
          </button>
        </div>
      </div>
    </div>
  );

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Hero Section - 고정 */}
      <section className="bg-gradient-to-r from-green-500 to-teal-500 text-white py-12">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
          <h2 className="text-3xl md:text-4xl font-bold mb-3">
            함께 모으면
            <br />더 큰 꿈이 됩니다
          </h2>
          <p className="text-lg md:text-xl text-green-50 mb-6">
            가족이 함께하는 목표 달성 적금
          </p>
          <div className="flex flex-wrap justify-center gap-3 text-base font-semibold">
            <div className="bg-white/20 backdrop-blur-sm px-5 py-2 rounded-lg">
              기본금리 <span className="text-yellow-300">4.0%</span>
            </div>
            <div className="bg-white/20 backdrop-blur-sm px-5 py-2 rounded-lg">
              가족초대 <span className="text-yellow-300">+0.3%</span>
            </div>
            <div className="bg-white/20 backdrop-blur-sm px-5 py-2 rounded-lg">
              목표달성 <span className="text-yellow-300">+0.5%</span>
            </div>
          </div>
        </div>
      </section>

      {step === 1 ? renderInputPage() : renderConfirmPage()}
    </div>
  );
}
