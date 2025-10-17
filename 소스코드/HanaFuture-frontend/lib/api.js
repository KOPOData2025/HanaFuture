// 환경에 따른 API URL 설정
const getApiBaseUrl = () => {
  // 프로덕션 환경에서는 환경변수 사용, 개발환경에서는 로컬호스트
  if (process.env.NODE_ENV === "production") {
    return (
      process.env.NEXT_PUBLIC_API_BASE_URL || "https://api.hanafuture.com/api"
    );
  }
  return "http://localhost:8080/api";
};

const API_BASE_URL = getApiBaseUrl();

// 토큰 관리
export const tokenManager = {
  getAccessToken: () => {
    if (typeof window !== "undefined") {
      try {
        // auth_token 또는 accessToken 둘 다 확인
        return (
          localStorage.getItem("auth_token") ||
          localStorage.getItem("accessToken")
        );
      } catch (error) {
        console.warn("Failed to access localStorage:", error);
        return null;
      }
    }
    return null;
  },

  getRefreshToken: () => {
    if (typeof window !== "undefined") {
      try {
        return localStorage.getItem("refreshToken");
      } catch (error) {
        console.warn("Failed to access localStorage:", error);
        return null;
      }
    }
    return null;
  },

  setTokens: (accessToken, refreshToken) => {
    if (typeof window !== "undefined") {
      try {
        localStorage.setItem("accessToken", accessToken);
        localStorage.setItem("auth_token", accessToken); // 호환성을 위해 두 방식 모두 저장
        localStorage.setItem("refreshToken", refreshToken);
      } catch (error) {
        console.warn("Failed to save tokens to localStorage:", error);
      }
    }
  },

  setAccessToken: (accessToken) => {
    if (typeof window !== "undefined") {
      try {
        localStorage.setItem("accessToken", accessToken);
        localStorage.setItem("auth_token", accessToken); // 호환성을 위해 두 방식 모두 저장
      } catch (error) {
        console.warn("Failed to save access token to localStorage:", error);
      }
    }
  },

  clearTokens: () => {
    if (typeof window !== "undefined") {
      try {
        localStorage.removeItem("accessToken");
        localStorage.removeItem("auth_token"); // 두 방식 모두 제거
        localStorage.removeItem("refreshToken");
      } catch (error) {
        console.warn("Failed to clear tokens from localStorage:", error);
      }
    }
  },
};

// API 요청 함수
async function apiRequest(endpoint, options = {}) {
  const url = `${API_BASE_URL}${endpoint}`;
  const accessToken = tokenManager.getAccessToken();

  // welfare 관련 API는 인증이 필요하지 않음 (단, AI API는 제외)
  const isWelfareAPI =
    (endpoint.includes("/welfare") ||
      endpoint.includes("/childcare-welfare")) &&
    !endpoint.includes("/ai/welfare"); // AI 복지 추천 API는 인증 필요

  const config = {
    headers: {
      "Content-Type": "application/json",
      ...(accessToken &&
        !isWelfareAPI && { Authorization: `Bearer ${accessToken}` }),
      ...options.headers,
    },
    ...options,
  };

  // 디버깅용 로그
  if (endpoint.includes("/ai/welfare")) {
    console.log("🤖 AI API 요청:", {
      endpoint,
      hasToken: !!accessToken,
      isWelfareAPI,
      willIncludeAuth: !!accessToken && !isWelfareAPI,
      headers: config.headers,
    });
  }

  try {
    const response = await fetch(url, config);

    // 401 에러 시 토큰 재발급 시도 (welfare API 제외, 단 AI API는 포함)
    if (response.status === 401 && accessToken && !isWelfareAPI) {
      const refreshToken = tokenManager.getRefreshToken();
      if (refreshToken) {
        try {
          const refreshResponse = await fetch(`${API_BASE_URL}/auth/refresh`, {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
            },
            body: JSON.stringify({ refreshToken }),
          });

          if (refreshResponse.ok) {
            const refreshData = await refreshResponse.json();
            if (refreshData.success) {
              tokenManager.setTokens(
                refreshData.data.accessToken,
                refreshData.data.refreshToken
              );

              // 원래 요청 재시도
              config.headers.Authorization = `Bearer ${refreshData.data.accessToken}`;
              const retryResponse = await fetch(url, config);
              return await retryResponse.json();
            }
          }
        } catch (refreshError) {
          console.error("Token refresh failed:", refreshError);
          tokenManager.clearTokens();
          throw new Error("인증이 만료되었습니다. 다시 로그인해주세요.");
        }
      }

      if (!isWelfareAPI) {
        tokenManager.clearTokens();
        throw new Error("인증이 필요합니다.");
      }
    }

    const data = await response.json();

    if (!response.ok) {
      throw new Error(data.message || "요청에 실패했습니다.");
    }

    return data;
  } catch (error) {
    console.error("API Request failed:", error);
    throw error;
  }
}

// 인증 API
export const authAPI = {
  // 회원가입
  signUp: async (userData) => {
    return apiRequest("/auth/signup", {
      method: "POST",
      body: JSON.stringify(userData),
    });
  },

  // 확장 회원가입 (복지 혜택 맞춤 추천용)
  extendedSignUp: async (userData) => {
    const response = await apiRequest("/auth/signup/extended", {
      method: "POST",
      body: JSON.stringify(userData),
    });

    if (response.success) {
      tokenManager.setTokens(
        response.data.accessToken,
        response.data.refreshToken
      );
    }

    return response;
  },

  // 로그인
  login: async (credentials) => {
    const response = await apiRequest("/auth/login", {
      method: "POST",
      body: JSON.stringify(credentials),
    });

    if (response.success) {
      tokenManager.setTokens(
        response.data.accessToken,
        response.data.refreshToken
      );
    }

    return response;
  },

  // 로그아웃
  logout: async () => {
    try {
      await apiRequest("/auth/logout", {
        method: "POST",
      });
    } finally {
      tokenManager.clearTokens();
    }
  },

  // 사용자 정보 조회
  getUserInfo: async () => {
    return apiRequest("/auth/me");
  },

  // 사용자 정보 업데이트 (OAuth2 사용자용)
  updateUserInfo: async (userData) => {
    return apiRequest("/auth/update-info", {
      method: "PUT",
      body: JSON.stringify(userData),
    });
  },

  registerOAuth2User: async (userData, tempToken) => {
    return apiRequest(
      `/auth/oauth2/register?tempToken=${encodeURIComponent(tempToken)}`,
      {
        method: "POST",
        body: JSON.stringify(userData),
        headers: {
          "Content-Type": "application/json",
        },
      }
    );
  },

  // 토큰 재발급
  refreshToken: async () => {
    const refreshToken = tokenManager.getRefreshToken();
    if (!refreshToken) {
      throw new Error("리프레시 토큰이 없습니다.");
    }

    const response = await apiRequest("/auth/refresh", {
      method: "POST",
      body: JSON.stringify({ refreshToken }),
    });

    if (response.success) {
      tokenManager.setTokens(
        response.data.accessToken,
        response.data.refreshToken
      );
    }

    return response;
  },
};

// 계좌 관리 API
export const accountAPI = {
  // 계좌 요약 정보 조회
  getAccountSummary: async () => {
    return apiRequest("/accounts/summary");
  },

  // 계좌 목록 조회
  getAccounts: async () => {
    return apiRequest("/accounts");
  },

  // 특정 계좌 조회
  getAccount: async (accountId) => {
    return apiRequest(`/accounts/${accountId}`);
  },

  // 계좌 생성
  createAccount: async (accountData) => {
    return apiRequest("/accounts", {
      method: "POST",
      body: JSON.stringify(accountData),
    });
  },

  // 계좌 정보 수정
  updateAccount: async (accountId, accountData) => {
    return apiRequest(`/accounts/${accountId}`, {
      method: "PUT",
      body: JSON.stringify(accountData),
    });
  },

  // 계좌 비활성화
  deactivateAccount: async (accountId) => {
    return apiRequest(`/accounts/${accountId}`, {
      method: "DELETE",
    });
  },

  // 계좌 거래내역 조회
  getAccountTransactions: async (accountId, page = 0, size = 20) => {
    return apiRequest(
      `/accounts/${accountId}/transactions?page=${page}&size=${size}`
    );
  },

  // 전체 거래내역 조회
  getAllTransactions: async (page = 0, size = 20) => {
    return apiRequest(`/accounts/transactions?page=${page}&size=${size}`);
  },

  // === 통합 뱅킹 API (오픈뱅킹 + 하나은행 Mock 통합) ===

  // 사용자의 모든 계좌 조회 (통합)
  getAllUserAccounts: async (userId) => {
    return apiRequest(`/integrated-banking/accounts/user/${userId}`);
  },

  // 출금 가능한 계좌 목록 조회 (통합)
  getWithdrawableAccounts: async (userId) => {
    return apiRequest(
      `/integrated-banking/accounts/withdrawable/user/${userId}`
    );
  },

  // 계좌 상세 조회
  getAccountDetail: async (accountId, userId) => {
    return apiRequest(
      `/integrated-banking/accounts/${accountId}/user/${userId}`
    );
  },

  // 계좌 연동 (시뮬레이션)
  linkAccount: async (linkData) => {
    return apiRequest("/integrated-banking/accounts/link", {
      method: "POST",
      body: JSON.stringify(linkData),
    });
  },

  // 계좌 잔액 새로고침
  refreshBalances: async (userId) => {
    return apiRequest(`/integrated-banking/accounts/refresh/user/${userId}`, {
      method: "POST",
    });
  },

  // === 레거시 호환성을 위한 별칭들 ===

  // 연결된 계좌 목록 조회 (레거시)
  getConnectedAccounts: async (userId) => {
    return accountAPI.getAllUserAccounts(userId);
  },

  // 마이데이터 계좌 목록 조회 (레거시)
  getMyDataAccounts: async (userId) => {
    return apiRequest("/mydata/accounts");
  },
};

// 복지 혜택 API
export const welfareAPI = {
  // 전체 복지 혜택 조회
  getAllBenefits: async (page = 0, size = 20) => {
    return apiRequest(`/welfare?page=${page}&size=${size}`);
  },

  // 🤖 AI 필터링된 복지 혜택 API (새로운 기능)
  // AI가 하나퓨처 서비스에 맞는 출산·양육·교육 혜택만 선별
  getAIFilteredCentralBenefits: async (page = 0, size = 20) => {
    return apiRequest(`/smart-welfare/central?page=${page}&size=${size}`);
  },

  getAIFilteredLocalBenefits: async (page = 0, size = 20) => {
    return apiRequest(`/smart-welfare/local?page=${page}&size=${size}`);
  },

  getAIFilteredAllBenefits: async (page = 0, size = 20) => {
    return apiRequest(`/smart-welfare/all?page=${page}&size=${size}`);
  },

  // 특정 혜택 상세 조회
  getBenefitDetail: async (benefitId) => {
    return apiRequest(`/welfare/${benefitId}`);
  },

  // 즐겨찾기 토글
  toggleBookmark: async (benefitId) => {
    return apiRequest(`/welfare/${benefitId}/bookmark`, {
      method: "POST",
    });
  },

  // 육아 관련 복지 혜택 조회
  getChildcareBenefits: async (page = 0, size = 20) => {
    return apiRequest(`/childcare-welfare?page=${page}&size=${size}`);
  },

  // 임신·출산 복지 혜택
  getPregnancyBenefits: async (page = 0, size = 20) => {
    return apiRequest(`/childcare-welfare/pregnancy?page=${page}&size=${size}`);
  },

  // 영유아 복지 혜택
  getInfantBenefits: async (page = 0, size = 20) => {
    return apiRequest(`/childcare-welfare/infant?page=${page}&size=${size}`);
  },

  // 보육 복지 혜택
  getChildcareSupportBenefits: async (page = 0, size = 20) => {
    return apiRequest(`/childcare-welfare/childcare?page=${page}&size=${size}`);
  },

  // 교육 복지 혜택
  getEducationBenefits: async (page = 0, size = 20) => {
    return apiRequest(`/childcare-welfare/education?page=${page}&size=${size}`);
  },

  // 서비스 유형별 복지 혜택 조회 (중앙정부/지자체)
  getBenefitsByType: async (serviceType, page = 0, size = 20) => {
    return apiRequest(`/welfare/type/${serviceType}?page=${page}&size=${size}`);
  },

  // 높은 지원금 복지 혜택
  getTopSupportBenefits: async (page = 0, size = 10) => {
    return apiRequest(
      `/childcare-welfare/top-support?page=${page}&size=${size}`
    );
  },

  // 맞춤 복지 혜택 추천
  getRecommendedBenefits: async (
    age,
    hasChild,
    isPregnant,
    page = 0,
    size = 20
  ) => {
    const params = new URLSearchParams({
      page: page.toString(),
      size: size.toString(),
    });

    if (age) params.append("age", age.toString());
    if (hasChild !== undefined) params.append("hasChild", hasChild.toString());
    if (isPregnant !== undefined)
      params.append("isPregnant", isPregnant.toString());

    return apiRequest(`/childcare-welfare/recommend?${params.toString()}`);
  },

  // 키워드 검색
  searchBenefits: async (keyword, page = 0, size = 20) => {
    return apiRequest(
      `/childcare-welfare/search?keyword=${encodeURIComponent(
        keyword
      )}&page=${page}&size=${size}`
    );
  },

  // 복지 혜택 상세 조회
  getBenefitDetail: async (benefitId) => {
    return apiRequest(`/welfare/${benefitId}`);
  },
};

// ========================================
// AI 기반 복지 추천 API
// ========================================
export const aiWelfareAPI = {
  // AI 맞춤형 복지 혜택 추천
  getAIRecommendations: async (userId = null, page = 0, size = 10) => {
    const params = new URLSearchParams({
      page: page.toString(),
      size: size.toString(),
    });

    if (userId) params.append("userId", userId.toString());

    return apiRequest(`/ai/welfare/recommendations?${params.toString()}`);
  },

  // AI 추천 테스트
  testAIRecommendation: async (
    age,
    childrenCount = 0,
    region = "서울특별시",
    page = 0,
    size = 10
  ) => {
    const params = new URLSearchParams({
      age: age.toString(),
      childrenCount: childrenCount.toString(),
      region: region,
      page: page.toString(),
      size: size.toString(),
    });

    return apiRequest(`/ai/welfare/test?${params.toString()}`, {
      method: "POST",
    });
  },
};

// 개인 맞춤 복지 추천 API
export const personalizedWelfareAPI = {
  // 개인 맞춤 추천 API
  getPersonalizedRecommendations: async (page = 0, size = 10) => {
    return apiRequest(
      `/personalized-welfare/recommendations?page=${page}&size=${size}`
    );
  },

  // 생애주기별 맞춤 추천
  getLifeCycleRecommendations: async (page = 0, size = 10) => {
    return apiRequest(
      `/personalized-welfare/lifecycle-recommendations?page=${page}&size=${size}`
    );
  },

  // 지역 기반 맞춤 추천
  getRegionalRecommendations: async (page = 0, size = 10) => {
    return apiRequest(
      `/personalized-welfare/regional-recommendations?page=${page}&size=${size}`
    );
  },

  // 추천 이유 조회
  getRecommendationReason: async (benefitId) => {
    return apiRequest(
      `/personalized-welfare/recommendation-reason/${benefitId}`
    );
  },
};

// 생애주기 API
export const lifecycleAPI = {
  // 사용자의 모든 생애주기 이벤트 조회
  getUserLifecycleEvents: async () => {
    return apiRequest("/lifecycle/events");
  },

  // 미처리 생애주기 이벤트 조회
  getUnprocessedEvents: async () => {
    return apiRequest("/lifecycle/events/unprocessed");
  },

  // 생애주기 이벤트 기반 복지 혜택 추천
  getEventRecommendations: async (eventId) => {
    return apiRequest(`/lifecycle/events/${eventId}/recommendations`);
  },

  // 모든 미처리 이벤트에 대한 통합 추천
  getAllRecommendations: async () => {
    return apiRequest("/lifecycle/recommendations");
  },

  // 긴급 추천 (즉시 신청해야 하는 혜택)
  getUrgentRecommendations: async () => {
    return apiRequest("/lifecycle/recommendations/urgent");
  },

  // 생애주기 통계 조회
  getLifecycleStatistics: async () => {
    return apiRequest("/lifecycle/statistics");
  },

  // 생애주기 이벤트 수동 생성
  generateLifecycleEvents: async () => {
    return apiRequest("/lifecycle/events/generate", {
      method: "POST",
    });
  },

  // 생애주기 이벤트 처리 완료 표시
  markEventAsCompleted: async (eventId) => {
    return apiRequest(`/lifecycle/events/${eventId}/complete`, {
      method: "PUT",
    });
  },
};

// 은행 통합 API
export const bankingAPI = {
  // 사용자 계좌 목록 조회
  getUserBankAccounts: async () => {
    return apiRequest("/integrated-banking/accounts");
  },

  // 육아 적금 상품 가입
  createChildcareSavings: async (productType, monthlyAmount, periodMonths) => {
    return apiRequest("/integrated-banking/create-childcare-savings", {
      method: "POST",
      body: JSON.stringify({
        productType,
        monthlyAmount,
        periodMonths,
      }),
    });
  },

  // 육아 상품 추천
  getChildcareProductRecommendations: async () => {
    return apiRequest("/integrated-banking/childcare-savings-recommendations");
  },
};

// 모임통장 API
export const groupAccountAPI = {
  // 모임통장 개설
  createGroupAccount: async (accountData) => {
    return apiRequest("/group-accounts", {
      method: "POST",
      body: JSON.stringify(accountData),
    });
  },

  // 사용자 모임통장 목록 조회
  getUserGroupAccounts: async (userId) => {
    return apiRequest(`/group-accounts/user/${userId}`);
  },

  // 공동계좌 생성 (SharedAccount)
  createSharedAccount: async (accountData) => {
    return apiRequest("/shared-accounts", {
      method: "POST",
      body: JSON.stringify(accountData),
    });
  },

  // 내 공동계좌 목록 조회
  getMySharedAccounts: async () => {
    return apiRequest("/shared-accounts");
  },

  // 공동계좌 상세 조회
  getSharedAccountDetail: async (accountId) => {
    return apiRequest(`/shared-accounts/${accountId}`);
  },
};

// 초대 API
// 카카오톡 초대는 프론트엔드에서 JavaScript SDK로 직접 처리
// SMS 초대가 필요한 경우에만 백엔드 API 사용
export const inviteAPI = {
  // SMS 초대 메시지 발송 (카카오톡 실패 시 대체용)
  sendSmsInvite: async (inviteData) => {
    return apiRequest("/invites/group-account", {
      method: "POST",
      body: JSON.stringify(inviteData),
    });
  },
};

// 하나퓨처 맞춤 복지 API (AI 필터링된 131개 데이터)
export const hanaFutureWelfareAPI = {
  // 전체 하나퓨처 맞춤 복지 혜택 조회
  getAllBenefits: async (page = 0, size = 20) => {
    return apiRequest(`/hana-future-welfare/all?page=${page}&size=${size}`);
  },

  // 중앙정부 하나퓨처 맞춤 복지 혜택 조회
  getCentralBenefits: async (page = 0, size = 20) => {
    return apiRequest(`/hana-future-welfare/central?page=${page}&size=${size}`);
  },

  // 지자체 하나퓨처 맞춤 복지 혜택 조회
  getLocalBenefits: async (page = 0, size = 20) => {
    return apiRequest(`/hana-future-welfare/local?page=${page}&size=${size}`);
  },

  // 하나퓨처 맞춤 복지 혜택 검색
  searchBenefits: async (keyword, serviceType = null, page = 0, size = 20) => {
    const params = new URLSearchParams({
      keyword,
      page: page.toString(),
      size: size.toString(),
    });

    if (serviceType) {
      params.append("serviceType", serviceType);
    }

    return apiRequest(`/hana-future-welfare/search?${params.toString()}`);
  },

  // 혜택 상세 조회 (ID로)
  getBenefitById: async (id) => {
    return apiRequest(`/hana-future-welfare/${id}`);
  },

  // 하나퓨처 맞춤 복지 혜택 통계
  getStats: async () => {
    return apiRequest("/hana-future-welfare/stats");
  },

  // AI 필터링 데이터 이관 (관리자용)
  migrateData: async () => {
    return apiRequest("/hana-future-welfare/migrate", {
      method: "POST",
    });
  },
};

// 인증 상태 확인
export const isAuthenticated = () => {
  return !!tokenManager.getAccessToken();
};
