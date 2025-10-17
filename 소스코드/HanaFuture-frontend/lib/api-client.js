// API 클라이언트 유틸리티
import { tokenManager } from "./api";

const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080/api";

class ApiClient {
  constructor() {
    this.baseURL = API_BASE_URL;
  }

  // 토큰 가져오기 (tokenManager 사용)
  getToken() {
    return tokenManager.getAccessToken();
  }

  // 토큰 설정하기
  setToken(token) {
    if (token) {
      tokenManager.setAccessToken(token);
    } else {
      tokenManager.clearTokens();
    }
  }

  // 기본 헤더 생성
  getHeaders() {
    const headers = {
      "Content-Type": "application/json",
    };

    const token = this.getToken();
    if (token) {
      headers.Authorization = `Bearer ${token}`;
    }

    return headers;
  }

  // 기본 fetch 래퍼
  async request(endpoint, options = {}) {
    const url = `${this.baseURL}${endpoint}`;
    const config = {
      headers: this.getHeaders(),
      ...options,
    };

    try {
      const response = await fetch(url, config);

      // 401 에러 시 토큰 제거 및 홈페이지로 리다이렉트
      if (response.status === 401) {
        tokenManager.clearTokens();
        if (
          typeof window !== "undefined" &&
          process.env.NODE_ENV === "production"
        ) {
          // 프로덕션 환경에서만 리다이렉트
          window.location.href = "/";
        }
        throw new Error("인증이 필요합니다.");
      }

      const data = await response.json();

      if (!response.ok) {
        throw new Error(
          data.message || `HTTP error! status: ${response.status}`
        );
      }

      return data;
    } catch (error) {
      console.error("API 요청 실패:", error);
      throw error;
    }
  }

  // GET 요청
  async get(endpoint) {
    return this.request(endpoint, { method: "GET" });
  }

  // POST 요청
  async post(endpoint, data) {
    return this.request(endpoint, {
      method: "POST",
      body: JSON.stringify(data),
    });
  }

  // PUT 요청
  async put(endpoint, data) {
    return this.request(endpoint, {
      method: "PUT",
      body: JSON.stringify(data),
    });
  }

  // DELETE 요청
  async delete(endpoint) {
    return this.request(endpoint, { method: "DELETE" });
  }

  // 인증 API
  auth = {
    // 로그인
    login: async (credentials) => {
      const response = await this.post("/auth/login", credentials);
      if (response.data?.token) {
        this.setToken(response.data.token);
      }
      return response;
    },

    // 회원가입
    register: async (userData) => {
      return this.post("/auth/register", userData);
    },

    // 로그아웃
    logout: async () => {
      try {
        await this.post("/auth/logout");
      } finally {
        this.setToken(null);
      }
    },

    // 현재 사용자 정보
    me: async () => {
      return this.get("/auth/me");
    },

    // 프로필 업데이트
    updateProfile: async (profileData) => {
      return this.put("/auth/profile", profileData);
    },
  };

  // 모임통장 API
  groupAccounts = {
    // 내 모임통장 목록
    list: async () => {
      return this.get("/api/group-accounts");
    },

    // 모임통장 상세 조회
    get: async (accountId) => {
      return this.get(`/api/group-accounts/${accountId}`);
    },

    // 모임통장 생성
    create: async (accountData) => {
      return this.post("/api/group-accounts", accountData);
    },

    // 모임통장 수정
    update: async (accountId, accountData) => {
      return this.put(`/api/group-accounts/${accountId}`, accountData);
    },

    // 멤버 초대
    inviteMember: async (accountId, memberData) => {
      return this.post(`/api/group-accounts/${accountId}/members`, memberData);
    },

    // 입금
    deposit: async (accountId, transactionData) => {
      return this.post(
        `/api/group-accounts/${accountId}/deposit`,
        transactionData
      );
    },

    // 출금
    withdraw: async (accountId, transactionData) => {
      return this.post(
        `/api/group-accounts/${accountId}/withdraw`,
        transactionData
      );
    },

    // 거래내역 조회
    getTransactions: async (accountId, page = 0, size = 20) => {
      return this.get(
        `/api/group-accounts/${accountId}/transactions?page=${page}&size=${size}`
      );
    },
  };

  // 자산연동 API
  assets = {
    // 지원 은행 목록
    getSupportedBanks: async () => {
      return this.get("/assets/supported-banks");
    },

    // 내 자산 목록
    list: async () => {
      return this.get("/assets");
    },

    // 자산 요약 정보
    getSummary: async () => {
      return this.get("/assets/summary");
    },

    // 계좌 연결
    connect: async (assetData) => {
      return this.post("/assets/connect", assetData);
    },

    // 자산 동기화
    sync: async (assetId) => {
      return this.post(`/assets/${assetId}/sync`);
    },

    // 전체 자산 동기화
    syncAll: async () => {
      return this.post("/assets/sync-all");
    },

    // 자산 설정 업데이트
    updateSettings: async (assetId, settings) => {
      return this.put(`/assets/${assetId}/settings`, settings);
    },

    // 자산 연결 해제
    disconnect: async (assetId) => {
      return this.delete(`/assets/${assetId}`);
    },

    // 자산 거래내역
    getTransactions: async (assetId, page = 0, size = 20) => {
      return this.get(
        `/assets/${assetId}/transactions?page=${page}&size=${size}`
      );
    },
  };

  // 복지혜택 API
  welfare = {
    // 혜택 목록 조회
    list: async (params = {}) => {
      const queryString = new URLSearchParams(params).toString();
      return this.get(`/welfare${queryString ? `?${queryString}` : ""}`);
    },

    // 개인화된 혜택 조회
    getPersonalized: async () => {
      return this.get("/welfare/personalized");
    },

    // 혜택 상세 조회
    get: async (benefitId) => {
      return this.get(`/welfare/${benefitId}`);
    },

    // 혜택 신청
    apply: async (benefitId, applicationData) => {
      return this.post(`/welfare/${benefitId}/apply`, applicationData);
    },
  };

  // 사용자 API
  users = {
    // 프로필 조회
    getProfile: async () => {
      return this.get("/api/user/profile");
    },

    // 프로필 업데이트
    updateProfile: async (profileData) => {
      return this.put("/api/user/profile", profileData);
    },

    // 사용자 정보 조회
    getInfo: async () => {
      return this.get("/api/user/info");
    },

    // 사용자 정보 업데이트
    updateInfo: async (infoData) => {
      return this.put("/api/user/info", infoData);
    },

    // 가족 정보 업데이트
    updateFamily: async (familyData) => {
      return this.put("/api/user/family", familyData);
    },

    // 생애주기 정보 업데이트
    updateLifecycle: async (lifecycleData) => {
      return this.put("/api/user/lifecycle", lifecycleData);
    },
  };

  // 계좌 연동 관련 API
  bankAccounts = {
    // 연동된 계좌 목록 조회
    list: async () => {
      return this.get("/api/user/bank-accounts");
    },

    // 출금 가능 계좌 목록 조회
    getWithdrawable: async () => {
      return this.get("/api/user/bank-accounts/withdrawable");
    },

    // 계좌 연동
    link: async (data) => {
      return this.post("/api/user/bank-accounts/link", data);
    },

    // 계좌 연동 해제
    unlink: async (accountId) => {
      return this.delete(`/api/user/bank-accounts/${accountId}`);
    },

    // 주계좌 설정
    setPrimary: async (accountId) => {
      return this.put(`/api/user/bank-accounts/${accountId}/primary`);
    },

    // 계좌 동기화
    sync: async (accountId) => {
      return this.post(`/api/user/bank-accounts/${accountId}/sync`);
    },

    // 모든 계좌 동기화
    syncAll: async () => {
      return this.post("/api/user/bank-accounts/sync-all");
    },

    // 계좌 연동 가이드
    getGuide: async () => {
      return this.get("/api/user/bank-accounts/guide");
    },
  };

  // 적금 상품 관련 API
  savings = {
    // 전체 적금 상품 조회
    getAllProducts: async () => {
      return this.get("/savings/products");
    },

    // 육아 적금 상품 조회
    getChildcareProducts: async () => {
      return this.get("/savings/products/childcare");
    },

    // 개인 맞춤 추천 상품
    getRecommendations: async () => {
      return this.get("/savings/products/recommendations");
    },

    // 상품 상세 조회
    getProductDetail: async (productId) => {
      return this.get(`/savings/products/${productId}`);
    },

    // 상품 비교
    compareProducts: async (productIds) => {
      return this.post("/savings/products/compare", productIds);
    },

    // 육아 적금 계좌 생성
    createChildcareSavings: async (data) => {
      return this.post("/savings/accounts/childcare", data);
    },
  };

  // 마이데이터 연동 API
  mydata = {
    // 마이데이터 계좌 연동
    connect: async (data) => {
      return this.post("/mydata/connect", data);
    },

    // 연동된 계좌 목록 조회
    getAccounts: async () => {
      return this.get("/mydata/accounts");
    },

    // 계좌 연동 해제
    disconnect: async (accountId) => {
      return this.post(`/mydata/disconnect/${accountId}`);
    },

    // 지원 은행 목록 조회
    getSupportedBanks: async () => {
      return this.get("/mydata/supported-banks");
    },

    // 계좌 동기화
    sync: async () => {
      return this.post("/mydata/sync");
    },
  };
}

// 싱글톤 인스턴스 생성
const apiClient = new ApiClient();

// 브라우저 환경에서 토큰 초기화는 tokenManager가 자동으로 처리함

export default apiClient;
export { apiClient };
