"use client";

import { Suspense, useEffect, useState } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import { ExtendedSignupForm } from "../../../components/features/auth/enhanced-signup-form";
import { LoadingScreen } from "../../../components/common/loading-screen";

function AdditionalInfoContent() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const [loading, setLoading] = useState(true);
  const [userInfo, setUserInfo] = useState(null);

  useEffect(() => {
    const token = searchParams.get("token");
    const refreshToken = searchParams.get("refresh");
    const tempToken = searchParams.get("tempToken");
    const isNewUser = searchParams.get("isNewUser");
    const needsAdditionalInfo = searchParams.get("needsAdditionalInfo");

    console.log(" Additional Info Page - URL 파라미터:", {
      token,
      refreshToken,
      tempToken,
      isNewUser,
      needsAdditionalInfo,
    });

    // 신규 OAuth2 사용자인 경우
    if (tempToken && isNewUser === "true") {
      console.log(" 신규 OAuth2 사용자 감지");

      // 임시 토큰을 세션 스토리지에 저장
      if (typeof window !== "undefined") {
        sessionStorage.setItem("tempOAuth2Token", tempToken);
      }

      // 임시 사용자 정보 설정 (토큰에서 추출된 기본 정보)
      setUserInfo({
        email: null, // 임시 토큰에서 추출 가능하지만 보안상 프론트에서는 숨김
        name: null, // 임시 토큰에서 추출 가능
        isNewOAuth2User: true,
      });
      setLoading(false);
      return;
    }

    // 기존 사용자인 경우 (기존 로직 유지)
    if (!token || !refreshToken) {
      console.error("❌ 토큰이 없습니다:", {
        token,
        refreshToken,
        tempToken,
        isNewUser,
      });

      // tempToken도 없으면 에러 페이지로
      if (!tempToken) {
        router.push("/auth/error?error=missing_tokens");
        return;
      }
    }

    // 토큰을 로컬 스토리지에 저장
    if (typeof window !== "undefined") {
      localStorage.setItem("accessToken", token);
      localStorage.setItem("auth_token", token);
      localStorage.setItem("refreshToken", refreshToken);
    }

    // 사용자 정보 가져오기
    const fetchUserInfo = async () => {
      try {
        const { authAPI } = await import("../../../lib/api");
        const response = await authAPI.getUserInfo();

        if (response.success) {
          setUserInfo(response.data);

          // 추가 정보가 필요 없으면 메인 페이지로
          if (needsAdditionalInfo !== "true") {
            router.push("/");
            return;
          }
        } else {
          router.push("/auth/error?error=user_info_failed");
        }
      } catch (error) {
        console.error("사용자 정보 조회 실패:", error);
        router.push("/auth/error?error=user_info_failed");
      } finally {
        setLoading(false);
      }
    };

    fetchUserInfo();
  }, [searchParams, router]);

  const handleAdditionalInfoComplete = () => {
    // 추가 정보 입력 완료 후 메인 페이지로 이동
    router.push("/");
  };

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <LoadingScreen />
          <p className="mt-4 text-muted-foreground">
            사용자 정보를 확인하고 있습니다...
          </p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-primary/5 via-background to-primary/10">
      <div className="container mx-auto px-4 py-8">
        <div className="max-w-2xl mx-auto">
          <div className="text-center mb-8">
            <h1 className="text-3xl font-bold mb-2">추가 정보 입력</h1>
            <p className="text-muted-foreground">
              맞춤형 복지 혜택 추천을 위해 몇 가지 정보를 더 입력해주세요.
            </p>
          </div>

          <ExtendedSignupForm
            initialData={userInfo}
            isOAuth2User={true}
            onSuccess={handleAdditionalInfoComplete}
            onSkip={handleAdditionalInfoComplete}
          />
        </div>
      </div>
    </div>
  );
}

export default function AdditionalInfoPage() {
  return (
    <Suspense
      fallback={
        <div className="min-h-screen flex items-center justify-center">
          <div className="text-center">
            <LoadingScreen />
            <p className="mt-4 text-muted-foreground">
              페이지를 로드하고 있습니다...
            </p>
          </div>
        </div>
      }
    >
      <AdditionalInfoContent />
    </Suspense>
  );
}
