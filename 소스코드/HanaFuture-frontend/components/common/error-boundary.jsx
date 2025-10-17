"use client";

import React from "react";
import { AlertTriangle, RefreshCw, Home } from "lucide-react";

export function ErrorFallback({ error, resetErrorBoundary }) {
  return (
    <div className="min-h-screen bg-gradient-to-br from-red-50 via-white to-orange-50 flex items-center justify-center px-6">
      <div className="text-center space-y-8 max-w-md">
        {/* 에러 아이콘 */}
        <div className="flex justify-center">
          <div className="w-24 h-24 bg-red-100 rounded-full flex items-center justify-center">
            <AlertTriangle className="w-12 h-12 text-red-500" />
          </div>
        </div>

        {/* 에러 메시지 */}
        <div className="space-y-4">
          <h1 className="text-3xl font-black text-gray-900">
            앗, 문제가 발생했어요!
          </h1>
          <p className="text-lg text-gray-600 leading-relaxed">
            예상치 못한 오류가 발생했습니다.
            <br />
            잠시 후 다시 시도해주세요.
          </p>

          {/* 개발 환경에서만 에러 상세 정보 표시 */}
          {process.env.NODE_ENV === "development" && (
            <details className="text-left bg-gray-100 p-4 rounded-lg mt-4">
              <summary className="cursor-pointer font-medium text-gray-700">
                기술적 세부사항 보기
              </summary>
              <pre className="mt-2 text-sm text-red-600 overflow-auto">
                {error.message}
              </pre>
            </details>
          )}
        </div>

        {/* 액션 버튼들 */}
        <div className="flex flex-col sm:flex-row gap-4 justify-center">
          <button
            onClick={resetErrorBoundary}
            className="flex items-center justify-center gap-2 px-6 py-3 bg-gradient-to-r from-emerald-500 to-teal-500 text-white font-bold rounded-xl hover:shadow-lg hover:scale-105 transition-all duration-300"
          >
            <RefreshCw className="w-5 h-5" />
            다시 시도
          </button>

          <button
            onClick={() => (window.location.href = "/")}
            className="flex items-center justify-center gap-2 px-6 py-3 bg-white border-2 border-emerald-500 text-emerald-600 font-bold rounded-xl hover:bg-emerald-50 hover:scale-105 transition-all duration-300"
          >
            <Home className="w-5 h-5" />
            홈으로 이동
          </button>
        </div>

        {/* 도움말 링크 */}
        <div className="text-sm text-gray-500">
          <p>
            문제가 계속 발생하면{" "}
            <a
              href="mailto:support@hanafuture.com"
              className="text-emerald-600 hover:underline font-medium"
            >
              고객지원팀
            </a>
            으로 문의해주세요.
          </p>
        </div>
      </div>
    </div>
  );
}

export function NetworkError({
  onRetry,
  message = "네트워크 연결을 확인해주세요",
}) {
  return (
    <div className="flex flex-col items-center justify-center p-8 text-center space-y-6">
      <div className="w-16 h-16 bg-yellow-100 rounded-full flex items-center justify-center">
        <AlertTriangle className="w-8 h-8 text-yellow-500" />
      </div>

      <div className="space-y-2">
        <h3 className="text-xl font-bold text-gray-900">연결 문제</h3>
        <p className="text-gray-600">{message}</p>
      </div>

      <button
        onClick={onRetry}
        className="flex items-center gap-2 px-4 py-2 bg-emerald-500 text-white font-medium rounded-lg hover:bg-emerald-600 transition-colors"
      >
        <RefreshCw className="w-4 h-4" />
        다시 시도
      </button>
    </div>
  );
}

export function EmptyState({
  title = "데이터가 없습니다",
  description = "표시할 내용이 없습니다",
  action,
  actionLabel = "새로고침",
}) {
  return (
    <div className="flex flex-col items-center justify-center p-12 text-center space-y-6">
      <div className="w-20 h-20 bg-gray-100 rounded-full flex items-center justify-center">
        <div className="w-10 h-10 bg-gray-300 rounded-full"></div>
      </div>

      <div className="space-y-2">
        <h3 className="text-xl font-bold text-gray-900">{title}</h3>
        <p className="text-gray-600 max-w-sm">{description}</p>
      </div>

      {action && (
        <button
          onClick={action}
          className="px-4 py-2 bg-emerald-500 text-white font-medium rounded-lg hover:bg-emerald-600 transition-colors"
        >
          {actionLabel}
        </button>
      )}
    </div>
  );
}
