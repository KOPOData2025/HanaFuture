"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { AlertCircle, RefreshCw, Home } from "lucide-react";

export default function Error({ error, reset }) {
  const router = useRouter();

  useEffect(() => {
    // 에러 로깅
    console.error("Application Error:", error);
  }, [error]);

  return (
    <div className="min-h-screen bg-gradient-to-br from-red-50 via-white to-orange-50 flex items-center justify-center px-4">
      <div className="max-w-2xl w-full text-center">
        {/* 에러 아이콘 */}
        <div className="mb-8 flex justify-center">
          <div className="relative">
            <div className="w-32 h-32 bg-red-100 rounded-full flex items-center justify-center">
              <AlertCircle className="w-16 h-16 text-red-600" />
            </div>
            <div className="absolute inset-0 w-32 h-32 bg-red-200 rounded-full animate-ping opacity-20"></div>
          </div>
        </div>

        {/* 메시지 */}
        <div className="space-y-4 mb-8">
          <h2 className="text-3xl font-bold text-gray-900">
            문제가 발생했습니다
          </h2>
          <p className="text-gray-600 text-lg">
            일시적인 오류로 페이지를 불러올 수 없습니다.
            <br />
            잠시 후 다시 시도해주세요.
          </p>

          {/* 개발 환경에서만 에러 메시지 표시 */}
          {process.env.NODE_ENV === "development" && error?.message && (
            <div className="mt-6 p-4 bg-red-50 border border-red-200 rounded-lg">
              <p className="text-sm text-red-700 font-mono text-left">
                {error.message}
              </p>
            </div>
          )}
        </div>

        {/* 액션 버튼 */}
        <div className="flex flex-col sm:flex-row gap-4 justify-center items-center">
          <button
            onClick={() => reset()}
            className="flex items-center gap-2 px-6 py-3 bg-white border-2 border-red-600 text-red-600 rounded-xl font-semibold hover:bg-red-50 transition-all shadow-sm hover:shadow-md"
          >
            <RefreshCw className="w-5 h-5" />
            다시 시도
          </button>

          <button
            onClick={() => router.push("/dashboard")}
            className="flex items-center gap-2 px-6 py-3 bg-gradient-to-r from-red-600 to-orange-600 text-white rounded-xl font-semibold hover:from-red-700 hover:to-orange-700 transition-all shadow-lg hover:shadow-xl"
          >
            <Home className="w-5 h-5" />
            홈으로 가기
          </button>
        </div>

        {/* 도움말 */}
        <div className="mt-12 pt-8 border-t border-gray-200">
          <p className="text-gray-500 text-sm">
            문제가 지속되면{" "}
            <a
              href="mailto:support@hanafuture.com"
              className="text-red-600 hover:text-red-700 font-medium underline"
            >
              고객센터
            </a>
            로 문의해주세요.
          </p>
        </div>
      </div>
    </div>
  );
}

