"use client";

import React from "react";
import { useState } from "react";
import { useAuth } from "../../../contexts/AuthContext";
import { ExtendedSignupForm } from "./enhanced-signup-form";

export function AuthScreen({ onLogin, onSignupComplete }) {
  const [isLogin, setIsLogin] = useState(true);
  const [showExtendedSignup, setShowExtendedSignup] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [formData, setFormData] = useState({
    email: "",
    password: "",
  });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const { login } = useAuth();

  // 소셜 로그인 핸들러
  const handleSocialLogin = (provider) => {
    const baseUrl =
      process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080/api";
    // API_BASE_URL에서 /api 제거
    const serverUrl = baseUrl.replace("/api", "");
    window.location.href = `${serverUrl}/oauth2/authorization/${provider}`;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");

    try {
      if (isLogin) {
        const result = await login({
          email: formData.email,
          password: formData.password,
        });

        if (result.success) {
          onLogin();
        } else {
          setError(result.message || "로그인에 실패했습니다.");
        }
      } else {
        // 회원가입 시 확장 폼으로 이동
        setShowExtendedSignup(true);
        return;
      }
    } catch (err) {
      setError(err.message || "오류가 발생했습니다.");
    } finally {
      setLoading(false);
    }
  };

  const handleInputChange = (field, value) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
    setError("");
  };

  return (
    <div className="py-12 px-4">
      <div className="w-full max-w-6xl mx-auto grid grid-cols-1 lg:grid-cols-2 gap-12 items-center">
        {/* Left side - Branding (로고 제거, 텍스트만 유지) */}
        <div className="hidden lg:block space-y-8">
          <div className="space-y-6">
            <h2 className="font-heading font-bold text-5xl text-foreground leading-tight">
              가족의 금융을
              <br />
              <span className="bg-gradient-to-r from-primary to-primary/70 bg-clip-text text-transparent">
                스마트하게 관리하세요
              </span>
            </h2>
            <p className="text-xl text-muted-foreground leading-relaxed">
              하나Future와 함께 모임통장부터 자녀 용돈관리까지,
              <br />
              가족 맞춤형 금융 서비스를 경험하세요.
            </p>
          </div>
        </div>

        {/* Right side - Auth Form */}
        <div className="w-full max-w-md mx-auto">
          <div className="bg-card rounded-xl border shadow-2xl p-8">
            <div className="text-center pb-8">
              <h2 className="font-heading text-3xl text-foreground font-bold mb-2">
                {isLogin ? "로그인" : "회원가입"}
              </h2>
              <p className="text-lg text-muted-foreground">
                {isLogin
                  ? "계정에 로그인하여 서비스를 이용하세요"
                  : "새 계정을 만들어 시작하세요"}
              </p>
            </div>

            {error && (
              <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-xl mb-4">
                {error}
              </div>
            )}

            {isLogin ? (
              // 로그인 폼
              <form onSubmit={handleSubmit} className="space-y-6">
                <div className="space-y-2">
                  <label htmlFor="email" className="text-base font-semibold">
                    이메일
                  </label>
                  <input
                    id="email"
                    type="email"
                    placeholder="example@hana.com"
                    value={formData.email}
                    onChange={(e) => handleInputChange("email", e.target.value)}
                    className="w-full h-12 text-base rounded-xl border border-border/50 focus:border-primary px-4 bg-background text-foreground placeholder:text-muted-foreground"
                    required
                  />
                </div>

                <div className="space-y-2">
                  <label htmlFor="password" className="text-base font-semibold">
                    비밀번호
                  </label>
                  <input
                    id="password"
                    type={showPassword ? "text" : "password"}
                    placeholder="비밀번호를 입력하세요"
                    value={formData.password}
                    onChange={(e) =>
                      handleInputChange("password", e.target.value)
                    }
                    className="w-full h-12 text-base rounded-xl border border-border/50 focus:border-primary px-4 bg-background text-foreground placeholder:text-muted-foreground"
                    required
                  />
                </div>

                <button
                  type="submit"
                  disabled={loading}
                  className={`w-full h-12 text-base font-medium shadow-xl hover:shadow-2xl transition-all duration-300 rounded-xl ${
                    loading
                      ? "bg-gray-400 cursor-not-allowed"
                      : "bg-primary text-primary-foreground"
                  }`}
                >
                  {loading ? "처리중..." : "로그인"}
                </button>

                {/* 소셜 로그인 구분선 */}
                <div className="relative my-6">
                  <div className="absolute inset-0 flex items-center">
                    <div className="w-full border-t border-border"></div>
                  </div>
                  <div className="relative flex justify-center text-sm">
                    <span className="px-4 text-muted-foreground font-medium">
                      또는
                    </span>
                  </div>
                </div>

                {/* 소셜 로그인 버튼들 */}
                <div className="space-y-3">
                  <button
                    type="button"
                    onClick={() => handleSocialLogin("kakao")}
                    disabled={loading}
                    className="w-full h-12 flex items-center justify-center gap-3 bg-[#FEE500] hover:bg-[#FEE500]/90 rounded-xl transition-colors font-medium"
                  >
                    <svg className="w-5 h-5" viewBox="0 0 24 24">
                      <path
                        fill="#000"
                        d="M12 3C7.03 3 3 6.14 3 10.1c0 2.52 1.65 4.73 4.1 6.09l-.95 3.57c-.1.38.29.68.62.48l4.15-2.73c.36.03.72.04 1.08.04 4.97 0 9-3.14 9-7.1S16.97 3 12 3z"
                      />
                    </svg>
                    <span className="text-gray-900">카카오로 로그인</span>
                  </button>
                </div>

                <div className="text-center">
                  <button
                    type="button"
                    onClick={() => setIsLogin(false)}
                    className="text-base text-muted-foreground hover:text-primary"
                  >
                    계정이 없으신가요? 회원가입
                  </button>
                </div>
              </form>
            ) : (
              // 회원가입 버튼
              <div className="space-y-6">
                <button
                  onClick={() => setShowExtendedSignup(true)}
                  className="w-full h-12 text-base font-medium shadow-xl hover:shadow-2xl transition-all duration-300 rounded-xl bg-primary text-primary-foreground"
                >
                  이메일로 회원가입
                </button>

                {/* 소셜 로그인 구분선 */}
                <div className="relative my-6">
                  <div className="absolute inset-0 flex items-center">
                    <div className="w-full border-t border-border"></div>
                  </div>
                  <div className="relative flex justify-center text-sm">
                    <span className="px-4 text-muted-foreground font-medium">
                      또는
                    </span>
                  </div>
                </div>

                {/* 소셜 로그인 버튼들 */}
                <div className="space-y-3">
                  <button
                    type="button"
                    onClick={() => handleSocialLogin("kakao")}
                    disabled={loading}
                    className="w-full h-12 flex items-center justify-center gap-3 bg-[#FEE500] hover:bg-[#FEE500]/90 rounded-xl transition-colors font-medium"
                  >
                    <svg className="w-5 h-5" viewBox="0 0 24 24">
                      <path
                        fill="#000"
                        d="M12 3C7.03 3 3 6.14 3 10.1c0 2.52 1.65 4.73 4.1 6.09l-.95 3.57c-.1.38.29.68.62.48l4.15-2.73c.36.03.72.04 1.08.04 4.97 0 9-3.14 9-7.1S16.97 3 12 3z"
                      />
                    </svg>
                    <span className="text-gray-900">카카오로 회원가입</span>
                  </button>
                </div>

                <div className="text-center">
                  <button
                    type="button"
                    onClick={() => setIsLogin(true)}
                    className="text-base text-muted-foreground hover:text-primary"
                  >
                    이미 계정이 있으신가요? 로그인
                  </button>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>

      {/* 확장 회원가입 모달 */}
      {showExtendedSignup && (
        <ExtendedSignupForm
          onComplete={() => {
            setShowExtendedSignup(false);
            onLogin(); // 회원가입 완료 후 로그인 처리
          }}
          onSuccess={() => {
            setShowExtendedSignup(false);
            if (onSignupComplete) {
              onSignupComplete(); // 회원가입 완료 후 오픈뱅킹 플로우 시작
            } else {
              onLogin();
            }
          }}
          onCancel={() => setShowExtendedSignup(false)}
          initialEmail={formData.email}
          initialPassword={formData.password}
        />
      )}
    </div>
  );
}
