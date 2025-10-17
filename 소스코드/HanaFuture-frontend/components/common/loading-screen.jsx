"use client";

import React from "react";

export function LoadingScreen({
  message = "로딩 중입니다. 잠시만 기다려주세요...",
}) {
  return (
    <div className="fixed inset-0 bg-gradient-to-br from-slate-50 via-white to-emerald-50 flex items-center justify-center z-50">
      <div className="text-center space-y-8 px-6">
        {/* 로고 컨테이너 - 완전 중앙 정렬 */}
        <div className="flex flex-col items-center justify-center space-y-6">
          {/* 메인 로고 애니메이션 */}
          <div className="relative">
            {/* 외부 회전 링 */}
            <div className="absolute inset-0 w-32 h-32 rounded-full border-4 border-transparent border-t-emerald-500 border-r-emerald-300 animate-spin"></div>

            {/* 내부 회전 링 (반대 방향) */}
            <div
              className="absolute inset-2 w-28 h-28 rounded-full border-3 border-transparent border-b-emerald-400 border-l-emerald-200 animate-spin"
              style={{
                animationDirection: "reverse",
                animationDuration: "1.5s",
              }}
            ></div>

            {/* 로고 컨테이너 */}
            <div className="relative w-32 h-32 rounded-3xl bg-white flex items-center justify-center shadow-2xl shadow-emerald-500/30 p-6 loading-pulse">
              <img
                src="/hanafuture/HanaFuture-logo.png"
                alt="HanaFuture 로고"
                className="w-full h-full object-contain"
              />
            </div>
          </div>

          {/* 브랜드명 */}
          <div className="space-y-2">
            <h1 className="font-black text-4xl bg-gradient-to-r from-emerald-600 via-emerald-500 to-teal-500 bg-clip-text text-transparent">
              하나Future
            </h1>
          </div>
        </div>

        {/* 로딩 메시지 */}
        <div className="space-y-4">
          <p className="text-lg text-slate-600 max-w-sm mx-auto leading-relaxed font-medium">
            {message}
          </p>

          {/* 진행률 바 */}
          <div className="w-64 h-2 bg-slate-200 rounded-full mx-auto overflow-hidden">
            <div className="h-full bg-gradient-to-r from-emerald-500 to-teal-500 rounded-full progress-bar-animation"></div>
          </div>

          {/* 로딩 점들 */}
          <div className="flex justify-center space-x-2 mt-6">
            <div className="w-3 h-3 bg-emerald-500 rounded-full animate-bounce"></div>
            <div
              className="w-3 h-3 bg-emerald-400 rounded-full animate-bounce"
              style={{ animationDelay: "0.1s" }}
            ></div>
            <div
              className="w-3 h-3 bg-emerald-300 rounded-full animate-bounce"
              style={{ animationDelay: "0.2s" }}
            ></div>
          </div>
        </div>
      </div>
    </div>
  );
}

// 간단한 로딩 스피너
export function LoadingSpinner({ size = "md", className = "" }) {
  const sizeClasses = {
    sm: "w-4 h-4",
    md: "w-6 h-6",
    lg: "w-8 h-8",
    xl: "w-12 h-12",
  };

  return (
    <div
      className={`${sizeClasses[size]} ${className} flex items-center justify-center`}
    >
      <div className="relative w-full h-full">
        {/* 회전하는 링 */}
        <div className="w-full h-full rounded-full border-2 border-transparent border-t-emerald-500 border-r-emerald-300 animate-spin"></div>

        {/* 중앙 로고 */}
        <div className="absolute inset-2 flex items-center justify-center">
          <img
            src="/hanafuture/HanaFuture-logo.png"
            alt="HanaFuture"
            className="w-full h-full object-contain opacity-70"
          />
        </div>
      </div>
    </div>
  );
}
