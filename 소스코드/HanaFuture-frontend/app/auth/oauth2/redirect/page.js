"use client";

import { useEffect, useState } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import { useAuth } from "../../../../contexts/AuthContext";
import { LoadingScreen } from "../../../../components/common/loading-screen";

export default function OAuth2RedirectPage() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const { handleOAuth2Redirect } = useAuth();
  const [message, setMessage] = useState("소셜 로그인을 처리하고 있습니다...");

  useEffect(() => {
    const processOAuth2Redirect = async () => {
      try {
        const result = await handleOAuth2Redirect(searchParams);

        if (result.success) {
          if (result.needsAdditionalInfo) {
            setMessage("추가 정보 입력 페이지로 이동합니다...");
            // 추가 정보 입력이 필요한 경우
            setTimeout(() => {
              router.push("/auth/additional-info");
            }, 1000);
          } else {
            setMessage("로그인 성공! 대시보드로 이동합니다...");
            // 바로 대시보드로 이동
            setTimeout(() => {
              router.push("/");
            }, 1000);
          }
        } else {
          setMessage("로그인에 실패했습니다. 다시 시도해주세요.");
          setTimeout(() => {
            router.push("/");
          }, 2000);
        }
      } catch (error) {
        console.error("OAuth2 redirect processing failed:", error);
        setMessage("로그인 처리 중 오류가 발생했습니다.");
        setTimeout(() => {
          router.push("/");
        }, 2000);
      }
    };

    processOAuth2Redirect();
  }, [searchParams, handleOAuth2Redirect, router]);

  return <LoadingScreen message={message} />;
}
