"use client";

import { useEffect } from "react";

export function useChunkErrorHandler() {
  useEffect(() => {
    const handleChunkError = (event) => {
      // ChunkLoadError 감지
      if (
        event.error?.name === "ChunkLoadError" ||
        event.error?.message?.includes("Loading chunk") ||
        event.error?.message?.includes("Loading CSS chunk")
      ) {
        console.warn("ChunkLoadError 감지됨. 페이지를 새로고침합니다.");

        // 사용자에게 알림 표시 (선택적)
        if (
          window.confirm("페이지를 새로고침하여 최신 버전을 로드하시겠습니까?")
        ) {
          window.location.reload();
        }
      }
    };

    // 전역 에러 리스너 추가
    window.addEventListener("error", handleChunkError);
    window.addEventListener("unhandledrejection", (event) => {
      if (
        event.reason?.name === "ChunkLoadError" ||
        event.reason?.message?.includes("Loading chunk")
      ) {
        handleChunkError({ error: event.reason });
      }
    });

    // 정리 함수
    return () => {
      window.removeEventListener("error", handleChunkError);
      window.removeEventListener("unhandledrejection", handleChunkError);
    };
  }, []);
}
