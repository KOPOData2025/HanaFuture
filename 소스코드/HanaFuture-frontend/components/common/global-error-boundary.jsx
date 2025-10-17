"use client";

import React from "react";
import { AlertTriangle, RefreshCw, Home } from "lucide-react";

class GlobalErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  static getDerivedStateFromError(error) {
    return { hasError: true, error };
  }

  componentDidCatch(error, errorInfo) {
    console.error("Global Error Boundary caught an error:", error, errorInfo);

    // ChunkLoadError 자동 처리
    if (
      error?.name === "ChunkLoadError" ||
      error?.message?.includes("Loading chunk")
    ) {
      
      setTimeout(() => {
        window.location.reload();
      }, 3000);
    }
  }

  render() {
    if (this.state.hasError) {
      const isChunkError =
        this.state.error?.name === "ChunkLoadError" ||
        this.state.error?.message?.includes("Loading chunk");

      if (isChunkError) {
        return (
          <div className="min-h-screen bg-gray-50 flex items-center justify-center p-4">
            <div className="max-w-md w-full bg-white rounded-2xl p-8 shadow-xl text-center">
              <div className="w-16 h-16 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-6">
                <RefreshCw className="h-8 w-8 text-blue-600 animate-spin" />
              </div>

              <h2 className="text-2xl font-bold text-gray-900 mb-4">
                페이지를 새로고침하고 있어요
              </h2>

              <p className="text-gray-600 mb-6 leading-relaxed">
                최신 버전으로 업데이트 중입니다.
                <br />
                잠시만 기다려주세요...
              </p>

              <div className="bg-blue-50 p-4 rounded-xl">
                <p className="text-sm text-blue-700">
                  3초 후 자동으로 새로고침됩니다
                </p>
              </div>
            </div>
          </div>
        );
      }

      return (
        <div className="min-h-screen bg-gray-50 flex items-center justify-center p-4">
          <div className="max-w-md w-full bg-white rounded-2xl p-8 shadow-xl text-center">
            <div className="w-16 h-16 bg-red-100 rounded-full flex items-center justify-center mx-auto mb-6">
              <AlertTriangle className="h-8 w-8 text-red-600" />
            </div>

            <h2 className="text-2xl font-bold text-gray-900 mb-4">
              앗! 문제가 발생했어요
            </h2>

            <p className="text-gray-600 mb-6 leading-relaxed">
              일시적인 오류가 발생했습니다.
              <br />
              잠시 후 다시 시도해주세요.
            </p>

            <div className="space-y-3">
              <button
                onClick={() => window.location.reload()}
                className="w-full flex items-center justify-center gap-2 px-6 py-3 bg-emerald-600 text-white rounded-xl hover:bg-emerald-700 transition-colors font-semibold"
              >
                <RefreshCw className="h-4 w-4" />
                페이지 새로고침
              </button>

              <button
                onClick={() => (window.location.href = "/")}
                className="w-full flex items-center justify-center gap-2 px-6 py-3 bg-gray-100 text-gray-700 rounded-xl hover:bg-gray-200 transition-colors font-semibold"
              >
                <Home className="h-4 w-4" />
                홈으로 이동
              </button>
            </div>

            {process.env.NODE_ENV === "development" && (
              <details className="mt-6 text-left">
                <summary className="cursor-pointer text-sm text-gray-500 hover:text-gray-700">
                  개발자 정보 (클릭하여 확장)
                </summary>
                <pre className="mt-2 text-xs text-red-600 bg-red-50 p-3 rounded overflow-auto">
                  {this.state.error?.toString()}
                </pre>
              </details>
            )}
          </div>
        </div>
      );
    }

    return this.props.children;
  }
}

export default GlobalErrorBoundary;
