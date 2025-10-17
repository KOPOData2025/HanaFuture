"use client";

import { createContext, useContext, useEffect, useState } from "react";
import { authAPI, isAuthenticated, tokenManager } from "../lib/api";
import { LoadingScreen } from "../components/common/loading-screen";

const AuthContext = createContext({});

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  // 초기 인증 상태 확인
  useEffect(() => {
    const checkAuthStatus = async () => {
      try {
        if (isAuthenticated()) {
          // 타임아웃 설정 (5초)
          const timeoutPromise = new Promise((_, reject) =>
            setTimeout(() => reject(new Error("Request timeout")), 5000)
          );

          const response = await Promise.race([
            authAPI.getUserInfo(),
            timeoutPromise,
          ]);

          if (response.success) {
            // 토큰을 사용자 정보에 포함
            const userWithToken = {
              ...response.data,
              token: tokenManager.getAccessToken(),
            };
            setUser(userWithToken);
            setIsLoggedIn(true);
          } else {
            tokenManager.clearTokens();
            setIsLoggedIn(false);
          }
        } else {
          // 토큰이 없으면 바로 로딩 완료
          setIsLoggedIn(false);
        }
      } catch (error) {
        console.error("Auth check failed:", error);
        tokenManager.clearTokens();
        setIsLoggedIn(false);
      } finally {
        setLoading(false);
      }
    };

    // 약간의 지연 후 실행 (SSR 문제 방지)
    const timer = setTimeout(checkAuthStatus, 100);
    return () => clearTimeout(timer);
  }, []);

  // 로그인
  const login = async (credentials) => {
    try {
      setLoading(true);
      const response = await authAPI.login(credentials);

      if (response.success) {
        // 사용자 정보 가져오기
        const userResponse = await authAPI.getUserInfo();
        if (userResponse.success) {
          // 토큰을 사용자 정보에 포함
          const userWithToken = {
            ...userResponse.data,
            token: tokenManager.getAccessToken(), // 토큰 추가!
          };
          setUser(userWithToken);
          setIsLoggedIn(true);
          console.log(
            "🎫 로그인 완료 - 사용자 정보에 토큰 포함:",
            userWithToken.token?.substring(0, 50) + "..."
          );
          return { success: true };
        }
      }

      return { success: false, message: response.message };
    } catch (error) {
      console.error("Login failed:", error);
      return { success: false, message: error.message };
    } finally {
      setLoading(false);
    }
  };

  // 회원가입
  const signUp = async (userData) => {
    try {
      setLoading(true);
      const response = await authAPI.signUp(userData);

      if (response.success) {
        // 회원가입 성공 시 토큰 저장
        tokenManager.setTokens(
          response.data.accessToken,
          response.data.refreshToken
        );

        // 사용자 정보 가져오기
        const userResponse = await authAPI.getUserInfo();
        if (userResponse.success) {
          setUser(userResponse.data);
          setIsLoggedIn(true);
        }
      }

      return { success: response.success, message: response.message };
    } catch (error) {
      console.error("SignUp failed:", error);
      return { success: false, message: error.message };
    } finally {
      setLoading(false);
    }
  };

  // 로그아웃
  const logout = async () => {
    try {
      await authAPI.logout();
    } catch (error) {
      console.error("Logout failed:", error);
    } finally {
      setUser(null);
      setIsLoggedIn(false);
    }
  };

  // 확장 회원가입 (복지 혜택 맞춤 추천용)
  const extendedSignUp = async (userData) => {
    try {
      setLoading(true);
      const response = await authAPI.extendedSignUp(userData);

      if (response.success) {
        // 사용자 정보 가져오기
        const userResponse = await authAPI.getUserInfo();
        if (userResponse.success) {
          setUser(userResponse.data);
          setIsLoggedIn(true);
        }
      }

      return response;
    } catch (error) {
      console.error("Extended SignUp failed:", error);
      return { success: false, message: error.message };
    } finally {
      setLoading(false);
    }
  };

  // 소셜 로그인 처리
  const handleSocialLogin = async (provider) => {
    try {
      setLoading(true);
      // 백엔드의 OAuth2 엔드포인트로 리다이렉트
      const API_BASE_URL =
        process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";
      const authUrl = `${API_BASE_URL}/oauth2/authorization/${provider}`;
      window.location.href = authUrl;
    } catch (error) {
      console.error("Social login failed:", error);
      setLoading(false);
      return { success: false, message: error.message };
    }
  };

  // OAuth2 리다이렉트 처리
  const handleOAuth2Redirect = async (urlParams) => {
    try {
      setLoading(true);
      const token = urlParams.get("token");
      const refreshToken = urlParams.get("refresh");
      const name = decodeURIComponent(urlParams.get("name") || "");
      const email = decodeURIComponent(urlParams.get("email") || "");
      const provider = urlParams.get("provider");
      const needsAdditionalInfo =
        urlParams.get("needsAdditionalInfo") === "true";

      if (token && refreshToken) {
        // 토큰 저장
        tokenManager.setTokens(token, refreshToken);

        // 사용자 정보 설정
        const userData = { name, email, provider };
        setUser(userData);
        setIsLoggedIn(true);

        return {
          success: true,
          needsAdditionalInfo,
          user: userData,
        };
      }

      return { success: false, message: "토큰을 받지 못했습니다." };
    } catch (error) {
      console.error("OAuth2 redirect handling failed:", error);
      return { success: false, message: error.message };
    } finally {
      setLoading(false);
    }
  };

  const value = {
    user,
    loading,
    isLoggedIn,
    login,
    signUp,
    extendedSignUp,
    logout,
    handleSocialLogin,
    handleOAuth2Redirect,
  };

  // 로딩 중일 때 로딩 화면 표시
  if (loading) {
    return <LoadingScreen message="로딩 중입니다. 잠시만 기다려주세요..." />;
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
}
