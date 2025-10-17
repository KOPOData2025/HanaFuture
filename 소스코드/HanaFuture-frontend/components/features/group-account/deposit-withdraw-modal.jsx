"use client";

import { useState, useEffect } from "react";
import {
  X,
  CreditCard,
  ArrowRight,
  CheckCircle,
  AlertCircle,
  DollarSign,
} from "lucide-react";
import Image from "next/image";
import { useAuth } from "../../../contexts/AuthContext";

// API Base URL - 환경변수 사용
const API_SERVER_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL?.replace("/api", "") ||
  "http://localhost:8080";

export function DepositWithdrawModal({
  isOpen,
  onClose,
  groupAccount,
  type = "deposit", // "deposit" or "withdraw"
  onSuccess,
}) {
  const { user } = useAuth();
  const [step, setStep] = useState(1); // 1: 금액입력, 2: 계좌선택, 3: 비밀번호, 4: 확인, 5: 완료
  const [amount, setAmount] = useState("");
  const [selectedAccount, setSelectedAccount] = useState(null);
  const [password, setPassword] = useState("");
  const [description, setDescription] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState("");
  const [userAccounts, setUserAccounts] = useState([]);

  // 사용자 계좌 목록 로드
  useEffect(() => {
    if (isOpen) {
      setStep(1);
      setAmount("");
      setSelectedAccount(null);
      setPassword("");
      setDescription("");
      setError("");
      loadUserAccounts();
    }
  }, [isOpen]);

  const loadUserAccounts = async () => {
    try {
      console.log("🔍 사용자 계좌 조회 시작 - userId:", user?.id);

      // 사용자 연동 계좌만 조회
      const response = await fetch(
        `http://localhost:8080/api/user/bank-accounts/user/${user?.id}`,
        {
          headers: {
            "Content-Type": "application/json",
            ...(user?.token && { Authorization: `Bearer ${user.token}` }),
          },
        }
      );

      console.log("📡 API 응답 상태:", response.status);

      if (response.ok) {
        const data = await response.json();
        console.log("📦 API 응답 데이터:", data);

        // UserBankAccount 목록 직접 사용
        const allAccounts = data.data || [];

        console.log("🔍 전체 계좌 수:", allAccounts.length);
        console.log("📋 계좌 원본 데이터:", allAccounts);

        // 간단하게 매핑만 (UserBankAccountResponse 필드 사용)
        const accounts = allAccounts.map((account) => ({
          id: account.id,
          bankName: account.bankName || "하나은행",
          accountNumber: account.accountNumber,
          accountName: account.accountName,
          balance: account.balance || 0,
          bankCode: account.bankCode || "081",
        }));

        console.log(`총 ${accounts.length}개 계좌 로드 완료`);
        console.log("📋 최종 계좌 목록:", accounts);

        setUserAccounts(accounts);
      } else {
        console.error("계좌 조회 실패");
        setUserAccounts([]);
      }
    } catch (error) {
      console.error("계좌 로드 중 오류:", error);
      setUserAccounts([]);
    }
  };

  const formatCurrency = (value) => {
    if (!value) return "";
    return parseInt(value.replace(/,/g, "")).toLocaleString();
  };

  const handleAmountChange = (e) => {
    const value = e.target.value.replace(/[^0-9]/g, "");
    setAmount(formatCurrency(value));
  };

  const getAmountValue = () => {
    return parseInt(amount.replace(/,/g, "")) || 0;
  };

  const canProceedStep1 = () => {
    const amountValue = getAmountValue();
    return amountValue > 0 && amountValue >= 1000; // 최소 1,000원
  };

  const canProceedStep2 = () => {
    return selectedAccount !== null;
  };

  const handleNext = () => {
    if (step === 1 && canProceedStep1()) {
      setStep(2);
    } else if (step === 2 && canProceedStep2()) {
      setStep(3);
    } else if (step === 3 && password.length === 4) {
      setStep(4);
    } else if (step === 4) {
      handleTransaction();
    }
  };

  const handleTransaction = async () => {
    setIsLoading(true);
    setError("");

    console.log("🚀 거래 처리 시작:", {
      type,
      groupAccountId: groupAccount.id,
      userId: user?.id,
      amount: getAmountValue(),
      password,
    });

    try {
      const endpoint =
        type === "deposit"
          ? `/api/group-accounts/${groupAccount.id}/deposit`
          : `/api/group-accounts/${groupAccount.id}/withdraw`;

      const requestBody =
        type === "deposit"
          ? {
              userId: user?.id || 1,
              amount: getAmountValue(),
              password: password, // 비밀번호 추가
              sourceAccountNumber: selectedAccount.accountNumber,
              sourceBankName: selectedAccount.bankName,
              description:
                description ||
                `${type === "deposit" ? "채우기" : "보내기"} - ${
                  selectedAccount.accountName
                }`,
            }
          : {
              userId: user?.id || 1,
              amount: getAmountValue(),
              password: password, // 비밀번호 추가
              targetAccountNumber: selectedAccount.accountNumber,
              targetBankName: selectedAccount.bankName,
              description:
                description ||
                `${type === "deposit" ? "채우기" : "보내기"} - ${
                  selectedAccount.accountName
                }`,
            };

      console.log("📤 API 요청:", endpoint, requestBody);

      const response = await fetch(`${API_SERVER_URL}${endpoint}`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          ...(user?.token && { Authorization: `Bearer ${user.token}` }),
        },
        body: JSON.stringify(requestBody),
      });

      console.log("📡 API 응답 상태:", response.status);

      if (response.ok) {
        const result = await response.json();
        console.log(`${type} 성공:`, result);

        // 즉시 데이터 새로고침 (UI 업데이트)
        if (onSuccess) {
          await onSuccess(result.data);
        }

        setStep(5); // 5단계(완료)로 이동

        // 2초 후 모달 닫기 (사용자에게 완료 메시지 표시)
        setTimeout(() => {
          onClose();
        }, 2000);
      } else {
        const errorData = await response.json();
        console.error("API 오류 응답:", errorData);
        throw new Error(errorData.message || "거래 처리 실패");
      }
    } catch (err) {
      console.error(`${type} 실패:`, err);
      setError(
        err.message ||
          `${
            type === "deposit" ? "채우기" : "보내기"
          }에 실패했습니다. 다시 시도해주세요.`
      );
    } finally {
      setIsLoading(false);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-md max-h-[90vh] overflow-y-auto">
        {/* 헤더 */}
        <div className="flex items-center justify-between p-6 border-b border-gray-200">
          <h2 className="text-xl font-bold text-gray-900">
            {type === "deposit" ? "💰 모임통장 채우기" : "💸 모임통장 보내기"}
          </h2>
          <button
            onClick={onClose}
            className="p-2 hover:bg-gray-100 rounded-lg transition-colors"
          >
            <X className="w-5 h-5 text-gray-500" />
          </button>
        </div>

        {/* 진행 단계 */}
        <div className="px-6 py-4 bg-gray-50">
          <div className="flex items-center justify-between">
            {[1, 2, 3, 4].map((stepNum) => (
              <div key={stepNum} className="flex items-center">
                <div
                  className={`w-8 h-8 rounded-full flex items-center justify-center text-sm font-medium ${
                    step >= stepNum
                      ? "bg-emerald-500 text-white"
                      : "bg-gray-200 text-gray-500"
                  }`}
                >
                  {step > stepNum ? (
                    <CheckCircle className="w-4 h-4" />
                  ) : (
                    stepNum
                  )}
                </div>
                {stepNum < 4 && (
                  <div
                    className={`w-8 h-1 mx-2 ${
                      step > stepNum ? "bg-emerald-500" : "bg-gray-200"
                    }`}
                  />
                )}
              </div>
            ))}
          </div>
          <div className="flex justify-between mt-2 text-xs text-gray-600">
            <span>금액</span>
            <span>계좌</span>
            <span>확인</span>
            <span>완료</span>
          </div>
        </div>

        {/* 콘텐츠 */}
        <div className="p-6">
          {step === 1 && (
            <div className="space-y-6">
              <div className="text-center">
                <h3 className="text-lg font-semibold text-gray-900 mb-2">
                  {type === "deposit"
                    ? "채울 금액을 입력해주세요"
                    : "보낼 금액을 입력해주세요"}
                </h3>
                <p className="text-gray-600">
                  {type === "deposit"
                    ? "연결된 계좌에서 모임통장으로 이체됩니다"
                    : "모임통장에서 선택한 계좌로 이체됩니다"}
                </p>
              </div>

              <div className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    {type === "deposit" ? "채울 금액" : "보낼 금액"}
                  </label>
                  <div className="relative">
                    <input
                      type="text"
                      value={amount}
                      onChange={handleAmountChange}
                      placeholder="0"
                      className="w-full px-4 py-3 text-right text-2xl font-bold border border-gray-300 rounded-xl focus:ring-2 focus:ring-emerald-500 focus:border-emerald-500"
                    />
                  </div>
                  {getAmountValue() > 0 && getAmountValue() < 1000 && (
                    <p className="text-sm text-red-500 mt-1">
                      최소 1,000원 이상 입력해주세요
                    </p>
                  )}
                </div>

                <div className="flex gap-2">
                  {[10000, 50000, 100000, 500000].map((preset) => (
                    <button
                      key={preset}
                      onClick={() => setAmount(preset.toLocaleString())}
                      className="flex-1 py-2 px-3 text-sm border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors"
                    >
                      {preset.toLocaleString()}원
                    </button>
                  ))}
                </div>
              </div>
            </div>
          )}

          {step === 2 && (
            <div className="space-y-6">
              <div className="text-center">
                <h3 className="text-lg font-semibold text-gray-900 mb-2">
                  {type === "deposit"
                    ? "출금할 계좌를 선택해주세요"
                    : "받을 계좌를 선택해주세요"}
                </h3>
                <p className="text-gray-600">연결된 계좌 목록에서 선택하세요</p>
              </div>

              <div className="space-y-3">
                {userAccounts.map((account) => (
                  <div
                    key={account.id}
                    onClick={() => setSelectedAccount(account)}
                    className={`p-4 border rounded-xl cursor-pointer transition-all ${
                      selectedAccount?.id === account.id
                        ? "border-emerald-500 bg-emerald-50"
                        : "border-gray-200 hover:border-gray-300"
                    }`}
                  >
                    <div className="flex items-center justify-between">
                      <div className="flex items-center gap-3">
                        <div className="w-10 h-10 bg-white rounded-lg flex items-center justify-center border border-gray-200">
                          <Image
                            src="/bank-logos/HanaLogo.png"
                            alt="하나은행"
                            width={32}
                            height={32}
                            className="object-contain"
                          />
                        </div>
                        <div>
                          <p className="font-medium text-gray-900">
                            {account.accountName}
                          </p>
                          <p className="text-sm text-gray-600">
                            {account.bankName}{" "}
                            {account.accountNumber.replace(
                              /(\d{5})\d{6}(\d{3})/,
                              "$1******$2"
                            )}
                          </p>
                        </div>
                      </div>
                      <div className="text-right">
                        <p className="text-sm font-medium text-gray-900">
                          {account.balance.toLocaleString()}원
                        </p>
                        {type === "deposit" &&
                          getAmountValue() > account.balance && (
                            <p className="text-xs text-red-500">잔액 부족</p>
                          )}
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {step === 3 && (
            <div className="space-y-6">
              <div className="text-center">
                <h3 className="text-lg font-semibold text-gray-900 mb-2">
                  모임통장 비밀번호를 입력해주세요
                </h3>
                <p className="text-gray-600">
                  모임통장 개설 시 설정한 4자리 비밀번호
                </p>
              </div>

              <div className="flex justify-center gap-3">
                {[0, 1, 2, 3].map((index) => (
                  <div
                    key={index}
                    className="w-14 h-14 border-2 border-gray-300 rounded-xl flex items-center justify-center text-2xl font-bold"
                  >
                    {password[index] ? "●" : ""}
                  </div>
                ))}
              </div>

              <div className="grid grid-cols-3 gap-3">
                {[1, 2, 3, 4, 5, 6, 7, 8, 9].map((num) => (
                  <button
                    key={num}
                    onClick={() => {
                      if (password.length < 4) {
                        setPassword(password + num);
                      }
                    }}
                    className="py-4 text-xl font-bold border border-gray-300 rounded-xl hover:bg-gray-50 transition-colors"
                  >
                    {num}
                  </button>
                ))}
                <button
                  onClick={() => setPassword("")}
                  className="py-4 text-sm border border-gray-300 rounded-xl hover:bg-gray-50 transition-colors"
                >
                  전체삭제
                </button>
                <button
                  onClick={() => {
                    if (password.length < 4) {
                      setPassword(password + "0");
                    }
                  }}
                  className="py-4 text-xl font-bold border border-gray-300 rounded-xl hover:bg-gray-50 transition-colors"
                >
                  0
                </button>
                <button
                  onClick={() => setPassword(password.slice(0, -1))}
                  className="py-4 text-sm border border-gray-300 rounded-xl hover:bg-gray-50 transition-colors"
                >
                  ←
                </button>
              </div>
            </div>
          )}

          {step === 4 && (
            <div className="space-y-6">
              <div className="text-center">
                <h3 className="text-lg font-semibold text-gray-900 mb-2">
                  {type === "deposit"
                    ? "채우기 정보를 확인해주세요"
                    : "보내기 정보를 확인해주세요"}
                </h3>
              </div>

              <div className="bg-gray-50 rounded-xl p-4 space-y-4">
                <div className="flex justify-between items-center">
                  <span className="text-gray-600">
                    {type === "deposit" ? "채울 금액" : "보낼 금액"}
                  </span>
                  <span className="font-bold text-lg text-gray-900">
                    {amount}원
                  </span>
                </div>

                <div className="flex justify-between items-center">
                  <span className="text-gray-600">
                    {type === "deposit" ? "출금 계좌" : "받을 계좌"}
                  </span>
                  <div className="text-right">
                    <p className="font-medium text-gray-900">
                      {selectedAccount?.accountName}
                    </p>
                    <p className="text-sm text-gray-600">
                      {selectedAccount?.bankName}{" "}
                      {selectedAccount?.accountNumber.replace(
                        /(\d{5})\d{6}(\d{3})/,
                        "$1******$2"
                      )}
                    </p>
                  </div>
                </div>

                <div className="flex justify-between items-center">
                  <span className="text-gray-600">
                    {type === "deposit" ? "받을 계좌" : "출금 계좌"}
                  </span>
                  <div className="text-right">
                    <p className="font-medium text-gray-900">
                      {groupAccount?.accountName || groupAccount?.name}
                    </p>
                    <p className="text-sm text-gray-600">
                      하나은행{" "}
                      {groupAccount?.accountNumber ||
                        groupAccount?.groupAccountNumber}
                    </p>
                  </div>
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  메모 (선택사항)
                </label>
                <input
                  type="text"
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                  placeholder="거래 메모를 입력하세요"
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-emerald-500 focus:border-emerald-500"
                />
              </div>

              {error && (
                <div className="flex items-center gap-2 p-3 bg-red-50 border border-red-200 rounded-lg">
                  <AlertCircle className="w-5 h-5 text-red-500" />
                  <p className="text-sm text-red-600">{error}</p>
                </div>
              )}
            </div>
          )}

          {step === 5 && (
            <div className="text-center space-y-6">
              <div className="w-16 h-16 bg-emerald-100 rounded-full flex items-center justify-center mx-auto">
                <CheckCircle className="w-8 h-8 text-emerald-600" />
              </div>
              <div>
                <h3 className="text-lg font-semibold text-gray-900 mb-2">
                  {type === "deposit"
                    ? "채우기가 완료되었습니다!"
                    : "보내기가 완료되었습니다!"}
                </h3>
                <p className="text-gray-600">
                  거래내역에서 확인하실 수 있습니다.
                </p>
              </div>
            </div>
          )}
        </div>

        {/* 버튼 */}
        {step < 5 && (
          <div className="px-6 pb-6">
            <div className="flex gap-3">
              {step > 1 && (
                <button
                  onClick={() => setStep(step - 1)}
                  className="flex-1 py-3 border border-gray-300 text-gray-700 rounded-xl hover:bg-gray-50 font-medium transition-colors"
                >
                  이전
                </button>
              )}
              <button
                onClick={handleNext}
                disabled={
                  isLoading ||
                  (step === 1 && !canProceedStep1()) ||
                  (step === 2 && !canProceedStep2()) ||
                  (step === 3 && password.length !== 4) ||
                  (step === 2 &&
                    type === "deposit" &&
                    getAmountValue() > (selectedAccount?.balance || 0))
                }
                className="flex-1 py-3 bg-emerald-500 text-white rounded-xl hover:bg-emerald-600 disabled:bg-gray-300 disabled:cursor-not-allowed font-medium transition-colors flex items-center justify-center gap-2"
              >
                {isLoading ? (
                  <>
                    <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                    처리 중...
                  </>
                ) : (
                  <>
                    {step === 4
                      ? type === "deposit"
                        ? "채우기 확인"
                        : "보내기 확인"
                      : "다음"}
                    {step < 4 && <ArrowRight className="w-4 h-4" />}
                  </>
                )}
              </button>
            </div>
          </div>
        )}

        {/* 완료 화면 닫기 버튼 */}
        {step === 5 && (
          <div className="px-6 pb-6">
            <button
              onClick={() => {
                onClose();
                if (onSuccess) onSuccess();
              }}
              className="w-full py-3 bg-emerald-500 text-white rounded-xl hover:bg-emerald-600 font-medium transition-colors"
            >
              확인
            </button>
          </div>
        )}
      </div>
    </div>
  );
}
