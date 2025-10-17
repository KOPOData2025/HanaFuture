"use client";

import React, { useState, useEffect } from "react";
import {
  Shield,
  Phone,
  User,
  IdCard,
  ArrowRight,
  ArrowLeft,
  Clock,
  AlertCircle,
  CheckCircle,
} from "lucide-react";

// API Base URL - 환경변수 사용
const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080/api";

export function IdentityVerification({ onVerificationComplete, onBack }) {
  const [formData, setFormData] = useState({
    name: "",
    residentNumber: "", // 실제 값 (123456-1234567)
    phoneNumber: "",
    carrier: "", // 통신사
  });
  const [displayValue, setDisplayValue] = useState(""); // 화면에만 표시되는 마스킹된 값
  const [smsData, setSmsData] = useState({
    verificationCode: "",
    isCodeSent: false,
    timeLeft: 300, // 5분
    isVerifying: false,
    verificationId: null, // SMS 인증 ID
  });
  const [errors, setErrors] = useState({});
  const [isLoading, setIsLoading] = useState(false);

  // 컴포넌트 마운트 시 스크롤 최상단으로 이동
  useEffect(() => {
    window.scrollTo({ top: 0, behavior: "smooth" });
  }, []);

  // SMS 타이머
  useEffect(() => {
    let timer;
    if (smsData.isCodeSent && smsData.timeLeft > 0) {
      timer = setInterval(() => {
        setSmsData((prev) => ({ ...prev, timeLeft: prev.timeLeft - 1 }));
      }, 1000);
    }
    return () => clearInterval(timer);
  }, [smsData.isCodeSent, smsData.timeLeft]);

  const formatTime = (seconds) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, "0")}`;
  };

  const validateForm = () => {
    const newErrors = {};

    if (!formData.name.trim()) {
      newErrors.name = "이름을 입력해주세요";
    }

    if (!formData.residentNumber.match(/^\d{6}-\d{7}$/)) {
      newErrors.residentNumber =
        "주민등록번호를 정확히 입력해주세요 (예: 123456-1234567)";
    }

    if (!formData.phoneNumber.match(/^010-\d{4}-\d{4}$/)) {
      newErrors.phoneNumber =
        "휴대폰 번호를 정확히 입력해주세요 (예: 010-1234-5678)";
    }

    if (!formData.carrier) {
      newErrors.carrier = "통신사를 선택해주세요";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleInputChange = (field, value) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
    if (errors[field]) {
      setErrors((prev) => ({ ...prev, [field]: null }));
    }
  };

  const formatResidentNumber = (value) => {
    const numbers = value.replace(/[^\d]/g, "");
    if (numbers.length <= 6) {
      return numbers;
    }
    return `${numbers.slice(0, 6)}-${numbers.slice(6, 13)}`;
  };

  const maskForDisplay = (value) => {
    if (!value) return "";
    const parts = value.split("-");
    if (parts.length === 2 && parts[1].length > 1) {
      const front = parts[0];
      const back = parts[1];
      // 뒷자리 첫 번째만 보이고, 나머지는 입력한 만큼만 * 표시
      const visiblePart = back.charAt(0);
      const maskedPart = "*".repeat(back.length - 1);
      return `${front}-${visiblePart}${maskedPart}`;
    }
    return value;
  };

  const formatPhoneNumber = (value) => {
    const numbers = value.replace(/[^\d]/g, "");
    if (numbers.length <= 3) {
      return numbers;
    } else if (numbers.length <= 7) {
      return `${numbers.slice(0, 3)}-${numbers.slice(3)}`;
    }
    return `${numbers.slice(0, 3)}-${numbers.slice(3, 7)}-${numbers.slice(
      7,
      11
    )}`;
  };

  const handleSendSMS = async () => {
    if (!validateForm()) return;

    setIsLoading(true);
    try {
      console.log("SMS 발송 요청:", {
        name: formData.name,
        phoneNumber: formData.phoneNumber,
        residentNumber: formData.residentNumber.replace("-", ""),
        carrier: formData.carrier,
      });

      // 토큰 확인 (디버깅)
      const rawToken =
        localStorage.getItem("token") ||
        localStorage.getItem("auth_token") ||
        localStorage.getItem("accessToken");
      console.log("🔑 저장된 토큰:", rawToken);
      console.log("🔑 토큰 길이:", rawToken?.length);
      console.log("🔑 토큰 타입:", typeof rawToken);

      // JWT 토큰 형식 검증 (최소한의 검증: 2개의 점이 있어야 함)
      const isValidJwtFormat =
        rawToken &&
        typeof rawToken === "string" &&
        rawToken.split(".").length === 3;
      console.log("🔑 토큰 형식 검증:", isValidJwtFormat);

      const token = isValidJwtFormat ? rawToken : null;

      // 실제 SMS 발송 API 호출
      const response = await fetch(`${API_BASE_URL}/auth/send-sms`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          ...(token && { Authorization: `Bearer ${token}` }),
        },
        body: JSON.stringify({
          name: formData.name,
          phoneNumber: formData.phoneNumber,
          residentNumber: formData.residentNumber.replace("-", ""), // 하이픈 제거
          carrier: formData.carrier,
        }),
      });

      const result = await response.json();

      if (result.success) {
        setSmsData((prev) => ({
          ...prev,
          isCodeSent: true,
          timeLeft: 300,
          verificationId: result.data.verificationId, // 인증 ID 저장
        }));
        // setStep(2) 제거 - 한 화면에서 처리
      } else {
        setErrors({ submit: result.message || "SMS 발송에 실패했습니다." });
      }
    } catch (error) {
      console.error("SMS 발송 중 오류:", error);
      setErrors({ submit: "SMS 발송에 실패했습니다. 다시 시도해주세요." });
    } finally {
      setIsLoading(false);
    }
  };

  const handleVerifyCode = async () => {
    if (!smsData.verificationCode || smsData.verificationCode.length !== 6) {
      setErrors({ verificationCode: "인증번호 6자리를 입력해주세요" });
      return;
    }

    setSmsData((prev) => ({ ...prev, isVerifying: true }));

    try {
      // 토큰 확인 (디버깅)
      const rawToken =
        localStorage.getItem("token") ||
        localStorage.getItem("auth_token") ||
        localStorage.getItem("accessToken");
      const isValidJwtFormat =
        rawToken &&
        typeof rawToken === "string" &&
        rawToken.split(".").length === 3;
      const token = isValidJwtFormat ? rawToken : null;
      console.log("🔑 인증 확인 시 토큰:", token);

      // SMS 인증번호 확인 API 호출
      const response = await fetch(
        `http://localhost:8080/api/auth/verify-sms?phoneNumber=${encodeURIComponent(
          formData.phoneNumber
        )}&code=${smsData.verificationCode}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            ...(token && { Authorization: `Bearer ${token}` }),
          },
        }
      );

      const result = await response.json();

      if (result.success && result.data === true) {
        // SMS 인증 성공 - 본인 인증 완료 처리
        const userCi = await generateCiFromResidentNumber(
          formData.residentNumber.replace("-", "")
        );

        onVerificationComplete({
          userCi: userCi,
          verificationId: smsData.verificationId,
          userInfo: {
            name: formData.name,
            phoneNumber: formData.phoneNumber,
            residentNumber: formData.residentNumber,
          },
        });
      } else {
        setErrors({
          verificationCode: result.message || "인증번호가 올바르지 않습니다.",
        });
      }
    } catch (error) {
      console.error("SMS 인증 확인 중 오류:", error);
      setErrors({
        verificationCode: "인증 중 오류가 발생했습니다. 다시 시도해주세요.",
      });
    } finally {
      setSmsData((prev) => ({ ...prev, isVerifying: false }));
    }
  };

  // CI 생성 함수 (SHA-256 해시 사용)
  const generateCiFromResidentNumber = async (residentNumber) => {
    try {
      // SHA-256 해시 생성 (Web Crypto API 사용)
      const encoder = new TextEncoder();
      const data = encoder.encode(residentNumber);
      const hashBuffer = await crypto.subtle.digest("SHA-256", data);
      const hashArray = Array.from(new Uint8Array(hashBuffer));
      const hashHex = hashArray
        .map((b) => b.toString(16).padStart(2, "0"))
        .join("");
      return hashHex;
    } catch (error) {
      console.error("CI 생성 중 오류:", error);
      // 오류 발생 시 서버에서 생성하도록 요청
      return null;
    }
  };

  const handleResendSMS = async () => {
    setIsLoading(true);
    try {
      await new Promise((resolve) => setTimeout(resolve, 1000)); // 임시 딜레이
      setSmsData((prev) => ({
        ...prev,
        timeLeft: 300,
        verificationCode: "",
      }));
      setErrors({});
    } catch (error) {
      setErrors({ submit: "SMS 재발송에 실패했습니다." });
    } finally {
      setIsLoading(false);
    }
  };

  const carriers = [
    {
      code: "SKT",
      name: "SKT",
      color: "bg-red-50 border-red-200 text-red-700",
    },
    {
      code: "KT",
      name: "KT",
      color: "bg-orange-50 border-orange-200 text-orange-700",
    },
    {
      code: "LGU",
      name: "LG U+",
      color: "bg-purple-50 border-purple-200 text-purple-700",
    },
    {
      code: "SKT_MVNO",
      name: "SKT 알뜰폰",
      color: "bg-pink-50 border-pink-200 text-pink-700",
    },
    {
      code: "KT_MVNO",
      name: "KT 알뜰폰",
      color: "bg-yellow-50 border-yellow-200 text-yellow-700",
    },
    {
      code: "LGU_MVNO",
      name: "LG U+ 알뜰폰",
      color: "bg-indigo-50 border-indigo-200 text-indigo-700",
    },
  ];

  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center p-4">
      <div className="w-full max-w-md">
        {/* 헤더 */}
        <div className="bg-white rounded-t-2xl p-8 text-center">
          <h1 className="text-2xl font-bold text-gray-900 mb-2">본인 인증</h1>
          <p className="text-gray-600">
            개인정보를 입력하고 휴대폰 인증을 완료해주세요.
          </p>
        </div>

        {/* 폼 */}
        <div className="bg-white p-8">
          <div className="space-y-6">
            {/* 이름 */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                👤 이름
              </label>
              <input
                type="text"
                value={formData.name}
                onChange={(e) => handleInputChange("name", e.target.value)}
                className={`w-full px-4 py-3 border rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 ${
                  errors.name ? "border-red-300" : "border-gray-300"
                }`}
                placeholder="실명을 입력해주세요"
              />
              {errors.name && (
                <p className="mt-1 text-sm text-red-600 flex items-center">
                  <AlertCircle className="w-4 h-4 mr-1" />
                  {errors.name}
                </p>
              )}
            </div>

            {/* 주민등록번호 */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                🆔 주민등록번호
              </label>
              <div className="relative">
                {/* 실제 입력 필드 (투명) */}
                <input
                  type="text"
                  value={formData.residentNumber}
                  onChange={(e) => {
                    const realValue = formatResidentNumber(e.target.value);
                    handleInputChange("residentNumber", realValue);
                  }}
                  className="absolute inset-0 w-full px-4 py-3 border border-transparent rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 bg-transparent z-20 text-transparent caret-black"
                  placeholder=""
                  maxLength={14}
                />

                {/* 마스킹된 표시 필드 */}
                <div
                  className={`w-full px-4 py-3 border rounded-xl bg-white pointer-events-none relative z-10 ${
                    errors.residentNumber ? "border-red-300" : "border-gray-300"
                  }`}
                >
                  <span className="text-gray-900">
                    {formData.residentNumber
                      ? maskForDisplay(formData.residentNumber)
                      : ""}
                  </span>
                  {!formData.residentNumber && (
                    <span className="text-gray-400">123456-1234567</span>
                  )}
                </div>
              </div>
              {errors.residentNumber && (
                <p className="mt-1 text-sm text-red-600 flex items-center">
                  <AlertCircle className="w-4 h-4 mr-1" />
                  {errors.residentNumber}
                </p>
              )}
            </div>

            {/* 통신사 선택 */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-3">
                📶 통신사
              </label>
              <div className="grid grid-cols-3 gap-2">
                {carriers.map((carrier) => (
                  <button
                    key={carrier.code}
                    type="button"
                    onClick={() => handleInputChange("carrier", carrier.code)}
                    className={`p-3 rounded-xl border-2 text-center text-sm font-medium transition-colors ${
                      formData.carrier === carrier.code
                        ? carrier.color
                        : "border-gray-200 hover:border-gray-300 text-gray-700"
                    }`}
                  >
                    {carrier.name}
                  </button>
                ))}
              </div>
              {errors.carrier && (
                <p className="mt-1 text-sm text-red-600 flex items-center">
                  <AlertCircle className="w-4 h-4 mr-1" />
                  {errors.carrier}
                </p>
              )}
            </div>

            {/* 휴대폰 번호 */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                📱 휴대폰 번호
              </label>
              <div className="flex space-x-2">
                <input
                  type="text"
                  value={formData.phoneNumber}
                  onChange={(e) =>
                    handleInputChange(
                      "phoneNumber",
                      formatPhoneNumber(e.target.value)
                    )
                  }
                  className={`flex-1 px-4 py-3 border rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 ${
                    errors.phoneNumber ? "border-red-300" : "border-gray-300"
                  }`}
                  placeholder="010-1234-5678"
                  maxLength={13}
                />
                <button
                  type="button"
                  onClick={handleSendSMS}
                  disabled={
                    isLoading || !formData.phoneNumber || !formData.carrier
                  }
                  className="px-4 py-3 bg-blue-600 text-white rounded-xl hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors text-sm font-medium"
                >
                  {isLoading ? "발송중..." : "인증요청"}
                </button>
              </div>
              {errors.phoneNumber && (
                <p className="mt-1 text-sm text-red-600 flex items-center">
                  <AlertCircle className="w-4 h-4 mr-1" />
                  {errors.phoneNumber}
                </p>
              )}
            </div>

            {/* SMS 인증번호 입력 (SMS 발송 후 표시) */}
            {smsData.isCodeSent && (
              <div className="bg-white border border-gray-200 rounded-xl p-4">
                <div className="text-center mb-4">
                  <p className="text-sm text-gray-700">
                    <strong>{formData.phoneNumber}</strong>로 인증번호를
                    발송했습니다.
                  </p>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    인증번호 6자리
                  </label>
                  <div className="flex space-x-2">
                    <input
                      type="text"
                      value={smsData.verificationCode}
                      onChange={(e) => {
                        const value = e.target.value
                          .replace(/[^\d]/g, "")
                          .slice(0, 6);
                        setSmsData((prev) => ({
                          ...prev,
                          verificationCode: value,
                        }));
                        if (errors.verificationCode) {
                          setErrors((prev) => ({
                            ...prev,
                            verificationCode: null,
                          }));
                        }
                      }}
                      className={`flex-1 px-4 py-3 border rounded-xl text-center text-lg font-mono tracking-widest focus:outline-none focus:ring-2 focus:ring-blue-500 ${
                        errors.verificationCode
                          ? "border-red-300"
                          : "border-gray-300"
                      }`}
                      placeholder="000000"
                      maxLength={6}
                    />
                    <button
                      onClick={handleResendSMS}
                      disabled={smsData.timeLeft > 240 || isLoading}
                      className="px-4 py-3 text-sm font-medium text-blue-600 border border-blue-600 rounded-xl hover:bg-blue-50 disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                      재발송
                    </button>
                  </div>

                  {smsData.timeLeft > 0 ? (
                    <p className="mt-2 text-sm text-gray-600 flex items-center justify-center">
                      ⏰ 남은 시간: {formatTime(smsData.timeLeft)}
                    </p>
                  ) : (
                    <p className="mt-2 text-sm text-red-600 flex items-center justify-center">
                      <AlertCircle className="w-4 h-4 mr-1" />
                      인증 시간이 만료되었습니다. 재발송 버튼을 눌러주세요.
                    </p>
                  )}

                  {errors.verificationCode && (
                    <p className="mt-2 text-sm text-red-600 flex items-center justify-center">
                      <AlertCircle className="w-4 h-4 mr-1" />
                      {errors.verificationCode}
                    </p>
                  )}
                </div>
              </div>
            )}

            {errors.submit && (
              <p className="text-sm text-red-600 flex items-center">
                <AlertCircle className="w-4 h-4 mr-1" />
                {errors.submit}
              </p>
            )}
          </div>
        </div>

        {/* 버튼 */}
        <div className="bg-white rounded-b-2xl p-8 border-t border-gray-100">
          <div className="flex space-x-4">
            <button
              onClick={onBack}
              className="flex items-center justify-center px-6 py-3 border border-gray-300 text-gray-700 rounded-xl hover:bg-gray-50 transition-colors"
            >
              <ArrowLeft className="w-4 h-4 mr-2" />
              이전
            </button>

            <button
              onClick={handleVerifyCode}
              disabled={
                !smsData.isCodeSent ||
                !smsData.verificationCode ||
                smsData.verificationCode.length !== 6 ||
                smsData.isVerifying ||
                smsData.timeLeft <= 0
              }
              className="flex-1 flex items-center justify-center px-6 py-3 bg-blue-600 text-white rounded-xl hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            >
              {smsData.isVerifying ? (
                <>
                  <div className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin mr-2" />
                  인증 중...
                </>
              ) : (
                <>
                  <CheckCircle className="w-4 h-4 mr-2" />
                  인증 완료
                </>
              )}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
