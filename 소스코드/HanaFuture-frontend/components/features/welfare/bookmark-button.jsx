"use client";

import { useState, useEffect } from "react";
import { Heart } from "lucide-react";
import { bookmarkAPI } from "../../../lib/bookmark-api";
import { useAuth } from "../../../contexts/AuthContext";
import { toast } from "sonner";

/**
 * 즐겨찾기 버튼 컴포넌트
 * - useHanaFuture: true이면 HanaFuture 전용 API 사용 (기본값: true)
 * - useHanaFuture: false이면 기존 WelfareBenefit API 사용
 */
export function BookmarkButton({
  welfareBenefitId,
  initialIsBookmarked = false,
  size = "default",
  showText = true,
  onBookmarkChange = null,
  useHanaFuture = true, // 기본값: HanaFuture API 사용
}) {
  const [isBookmarked, setIsBookmarked] = useState(initialIsBookmarked);
  const [isLoading, setIsLoading] = useState(false);
  const [hasChecked, setHasChecked] = useState(false); // 이미 체크했는지 여부
  const { user } = useAuth();

  // 사이즈별 스타일
  const sizeStyles = {
    small: {
      button: "p-1.5",
      icon: "h-4 w-4",
      text: "text-xs",
    },
    default: {
      button: "p-2",
      icon: "h-5 w-5",
      text: "text-sm",
    },
    large: {
      button: "p-3",
      icon: "h-6 w-6",
      text: "text-base",
    },
  };

  const currentSize = sizeStyles[size] || sizeStyles.default;

  // 컴포넌트 마운트 시 즐겨찾기 상태 확인 (최초 1회만)
  useEffect(() => {
    // 이미 체크했거나, 유저가 없거나, ID가 없으면 스킵
    if (hasChecked || !user || !welfareBenefitId) return;

    const checkStatus = async () => {
      try {
        let response;
        if (useHanaFuture) {
          response = await bookmarkAPI.checkHanaFutureBookmark(
            welfareBenefitId
          );
        } else {
          response = await bookmarkAPI.checkBookmark(welfareBenefitId);
        }

        if (response.success) {
          setIsBookmarked(response.data);
        }
      } catch (error) {
        console.error("즐겨찾기 상태 확인 실패:", error);
      } finally {
        setHasChecked(true); // 체크 완료 표시
      }
    };

    checkStatus();
  }, [user, welfareBenefitId, hasChecked, useHanaFuture]);

  /**
   * 즐겨찾기 토글
   */
  const toggleBookmark = async () => {
    if (!user) {
      toast.error("로그인이 필요합니다.");
      return;
    }

    if (isLoading) return;

    setIsLoading(true);

    try {
      if (isBookmarked) {
        // 즐겨찾기 제거
        let response;
        if (useHanaFuture) {
          response = await bookmarkAPI.removeHanaFutureBookmark(
            welfareBenefitId
          );
        } else {
          response = await bookmarkAPI.removeBookmark(welfareBenefitId);
        }

        if (response.success) {
          setIsBookmarked(false);
          toast.success("즐겨찾기에서 제거되었습니다.");
          onBookmarkChange?.(false);
        }
      } else {
        // 즐겨찾기 추가
        let response;
        if (useHanaFuture) {
          response = await bookmarkAPI.addHanaFutureBookmark(welfareBenefitId);
        } else {
          response = await bookmarkAPI.addBookmark(welfareBenefitId);
        }

        if (response.success) {
          setIsBookmarked(true);
          toast.success("즐겨찾기에 추가되었습니다.");
          onBookmarkChange?.(true);
        }
      }
    } catch (error) {
      console.error("즐겨찾기 토글 실패:", error);

      // 중복 추가 에러 처리
      if (
        error.message?.includes("이미 즐겨찾기에 추가된") ||
        error.message?.includes("BOOKMARK_ALREADY_EXISTS")
      ) {
        setIsBookmarked(true);
        toast.info("이미 즐겨찾기에 추가된 항목입니다.");
        onBookmarkChange?.(true);
      } else if (error.response?.data?.message) {
        toast.error(error.response.data.message);
      } else {
        toast.error("즐겨찾기 처리 중 오류가 발생했습니다.");
      }
    } finally {
      setIsLoading(false);
    }
  };

  if (!user) {
    return null; // 로그인하지 않은 경우 버튼 숨김
  }

  return (
    <button
      onClick={toggleBookmark}
      disabled={isLoading}
      data-welfare-id={welfareBenefitId}
      className={`
        ${currentSize.button}
        flex items-center gap-2 rounded-full transition-all duration-200
        ${
          isBookmarked
            ? "bg-red-50 text-red-600 hover:bg-red-100 border-red-200"
            : "bg-gray-50 text-gray-600 hover:bg-gray-100 border-gray-200"
        }
        ${isLoading ? "opacity-50 cursor-not-allowed" : "hover:bg-opacity-80"}
        border
      `}
      title={isBookmarked ? "즐겨찾기에서 제거" : "즐겨찾기에 추가"}
    >
      <Heart
        className={`
          ${currentSize.icon}
          transition-all duration-200
          ${isBookmarked ? "fill-current text-red-600" : "text-gray-600"}
          ${isLoading ? "animate-pulse" : ""}
        `}
      />
      {showText && (
        <span className={`${currentSize.text} font-medium`}>
          {isLoading ? "처리중..." : isBookmarked ? "즐겨찾기됨" : "즐겨찾기"}
        </span>
      )}
    </button>
  );
}
