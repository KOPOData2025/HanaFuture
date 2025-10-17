import { apiClient } from "./api-client";

// 개발 중 Mock 모드 설정 (네트워크 문제로 임시 활성화)
const MOCK_MODE = false;

/**
 * 즐겨찾기 API 클라이언트
 */
export const bookmarkAPI = {
  /**
   * 즐겨찾기 추가
   */
  async addBookmark(welfareBenefitId, memo = "") {
    if (MOCK_MODE) {
      await new Promise((resolve) => setTimeout(resolve, 500));
      return {
        success: true,
        data: {
          id: Date.now(),
          welfareBenefit: { id: welfareBenefitId },
          memo,
          createdAt: new Date().toISOString(),
        },
      };
    }

    try {
      const response = await apiClient.post("/bookmarks", {
        welfareBenefitId,
        memo,
      });
      return response;
    } catch (error) {
      console.error("즐겨찾기 추가 실패:", error);
      throw error;
    }
  },

  /**
   * 즐겨찾기 제거
   */
  async removeBookmark(welfareBenefitId) {
    if (MOCK_MODE) {
      await new Promise((resolve) => setTimeout(resolve, 300));
      return { success: true, message: "즐겨찾기가 제거되었습니다." };
    }

    try {
      const response = await apiClient.delete(
        `/bookmarks/welfare/${welfareBenefitId}`
      );
      return response;
    } catch (error) {
      console.error("즐겨찾기 제거 실패:", error);
      throw error;
    }
  },

  /**
   * 즐겨찾기 목록 조회 (페이징)
   */
  async getBookmarks(page = 0, size = 20) {
    if (MOCK_MODE) {
      await new Promise((resolve) => setTimeout(resolve, 500));
      return {
        success: true,
        data: {
          content: [],
          totalElements: 0,
          totalPages: 0,
        },
      };
    }

    try {
      const response = await apiClient.get("/bookmarks", {
        params: { page, size },
      });
      return response.data;
    } catch (error) {
      console.error("즐겨찾기 목록 조회 실패:", error);
      throw error;
    }
  },

  /**
   * 즐겨찾기 목록 조회 (전체)
   */
  async getAllBookmarks() {
    if (MOCK_MODE) {
      await new Promise((resolve) => setTimeout(resolve, 500));
      return {
        success: true,
        data: [],
      };
    }

    try {
      const response = await apiClient.get("/bookmarks/all");
      return response;
    } catch (error) {
      console.error("전체 즐겨찾기 조회 실패:", error);
      throw error;
    }
  },

  /**
   * 카테고리별 즐겨찾기 조회
   */
  async getBookmarksByCategory(category) {
    try {
      const response = await apiClient.get(
        `/bookmarks/category/${encodeURIComponent(category)}`
      );
      return response.data;
    } catch (error) {
      console.error("카테고리별 즐겨찾기 조회 실패:", error);
      throw error;
    }
  },

  /**
   * 생애주기별 즐겨찾기 조회
   */
  async getBookmarksByLifeCycle(lifeCycle) {
    try {
      const response = await apiClient.get(
        `/bookmarks/lifecycle/${encodeURIComponent(lifeCycle)}`
      );
      return response.data;
    } catch (error) {
      console.error("생애주기별 즐겨찾기 조회 실패:", error);
      throw error;
    }
  },

  /**
   * 즐겨찾기 여부 확인
   */
  async checkBookmark(welfareBenefitId) {
    if (MOCK_MODE) {
      await new Promise((resolve) => setTimeout(resolve, 200));
      return { success: true, data: false };
    }

    try {
      const response = await apiClient.get(
        `/bookmarks/check/${welfareBenefitId}`
      );
      return response;
    } catch (error) {
      console.error("즐겨찾기 확인 실패:", error);
      throw error;
    }
  },

  /**
   * 즐겨찾기 개수 조회
   */
  async getBookmarkCount() {
    if (MOCK_MODE) {
      await new Promise((resolve) => setTimeout(resolve, 200));
      return { success: true, data: 0 };
    }

    try {
      const response = await apiClient.get("/bookmarks/count");
      return response.data;
    } catch (error) {
      console.error("즐겨찾기 개수 조회 실패:", error);
      throw error;
    }
  },

  /**
   * 즐겨찾기 메모 업데이트
   */
  async updateMemo(bookmarkId, memo) {
    try {
      const response = await apiClient.put(`/bookmarks/${bookmarkId}/memo`, {
        memo,
      });
      return response.data;
    } catch (error) {
      console.error("즐겨찾기 메모 업데이트 실패:", error);
      throw error;
    }
  },

  // ==================== HanaFuture 전용 메서드 ====================

  /**
   * HanaFuture 혜택 즐겨찾기 추가
   */
  async addHanaFutureBookmark(hanaFutureBenefitId) {
    if (MOCK_MODE) {
      await new Promise((resolve) => setTimeout(resolve, 500));
      return {
        success: true,
        data: {
          id: Date.now(),
          hanaFutureBenefitId,
          createdAt: new Date().toISOString(),
        },
      };
    }

    try {
      const response = await apiClient.post(
        `/bookmarks/hana-future/${hanaFutureBenefitId}`
      );
      return response;
    } catch (error) {
      console.error("HanaFuture 즐겨찾기 추가 실패:", error);
      throw error;
    }
  },

  /**
   * HanaFuture 혜택 즐겨찾기 제거
   */
  async removeHanaFutureBookmark(hanaFutureBenefitId) {
    if (MOCK_MODE) {
      await new Promise((resolve) => setTimeout(resolve, 300));
      return { success: true, message: "즐겨찾기가 제거되었습니다." };
    }

    try {
      const response = await apiClient.delete(
        `/bookmarks/hana-future/${hanaFutureBenefitId}`
      );
      return response;
    } catch (error) {
      console.error("HanaFuture 즐겨찾기 제거 실패:", error);
      throw error;
    }
  },

  /**
   * HanaFuture 혜택 즐겨찾기 여부 확인
   */
  async checkHanaFutureBookmark(hanaFutureBenefitId) {
    if (MOCK_MODE) {
      await new Promise((resolve) => setTimeout(resolve, 200));
      return { success: true, data: false };
    }

    try {
      const response = await apiClient.get(
        `/bookmarks/hana-future/check/${hanaFutureBenefitId}`
      );
      return response;
    } catch (error) {
      console.error("HanaFuture 즐겨찾기 확인 실패:", error);
      throw error;
    }
  },
};
