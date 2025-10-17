"use client";

import { Suspense, useEffect } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import { useAuth } from "../../../../contexts/AuthContext";
import { LoadingScreen } from "../../../../components/common/loading-screen";

function OAuth2CallbackContent() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const { login } = useAuth();

  useEffect(() => {
    const handleOAuth2Callback = async () => {
      try {
        const token = searchParams.get("token");
        const refreshToken = searchParams.get("refresh");
        const tempToken = searchParams.get("tempToken");
        const isNewUser = searchParams.get("isNewUser");
        const error = searchParams.get("error");

        if (error) {
          console.error("OAuth2 인증 오류:", error);
          router.push("/auth/error?error=" + error);
          return;
        }

        // 신규 OAuth2 사용자인 경우 - additional-info 페이지로 리다이렉트
        if (tempToken && isNewUser === "true") {
          console.log("신규 OAuth2 사용자 - 추가 정보 입력 페이지로 이동");
          router.push(
            `/auth/additional-info?tempToken=${tempToken}&isNewUser=true`
          );
          return;
        }

        if (token && refreshToken) {
          // 토큰을 로컬 스토리지에 저장
          if (typeof window !== "undefined") {
            localStorage.setItem("accessToken", token);
            localStorage.setItem("auth_token", token);
            localStorage.setItem("refreshToken", refreshToken);
          }

          // 사용자 정보 가져오기
          try {
            const { authAPI } = await import("../../../../lib/api");
            const userResponse = await authAPI.getUserInfo();

            if (userResponse.success) {
              // AuthContext 업데이트는 자동으로 될 것임
              router.push("/");
            } else {
              throw new Error("사용자 정보를 가져올 수 없습니다.");
            }
          } catch (userError) {
            console.error("사용자 정보 조회 실패:", userError);
            router.push("/auth/error?error=user_info_failed");
          }
        } else {
          router.push("/auth/error?error=missing_tokens");
        }
      } catch (error) {
        console.error("OAuth2 콜백 처리 중 오류:", error);
        router.push("/auth/error?error=callback_failed");
      }
    };

    handleOAuth2Callback();
  }, [searchParams, router, login]);

  return (
    <div className="min-h-screen flex items-center justify-center">
      <div className="text-center">
        <LoadingScreen />
        <p className="mt-4 text-muted-foreground">로그인 처리 중입니다...</p>
      </div>
    </div>
  );
}

export default function OAuth2CallbackPage() {
  return (
    <Suspense
      fallback={
        <div className="min-h-screen flex items-center justify-center">
          <div className="text-center">
            <LoadingScreen />
            <p className="mt-4 text-muted-foreground">페이지를 로드하고 있습니다...</p>
          </div>
        </div>
      }
    >
      <OAuth2CallbackContent />
    </Suspense>
  );
}
