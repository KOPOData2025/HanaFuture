"use client";

import React, { useState, useEffect } from "react";
import Link from "next/link";
import {
  Search,
  Filter,
  ChevronDown,
  ChevronUp,
  Sparkles,
  ChevronLeft,
  ChevronRight,
} from "lucide-react";
import { hanaFutureWelfareAPI } from "../../../lib/api";
import { BookmarkButton } from "./bookmark-button";
import { useAuth } from "../../../contexts/AuthContext";

export default function GovernmentBenefits() {
  const { user } = useAuth();
  const [benefits, setBenefits] = useState([]);
  const [aiRecommendations, setAiRecommendations] = useState([]);
  const [aiRecommendedTab, setAiRecommendedTab] = useState("central"); // AI 추천 탭
  const [loadingAI, setLoadingAI] = useState(true); // 초기 로딩 상태 true (백그라운드 로드 대기)
  const [aiSectionExpanded, setAiSectionExpanded] = useState(false); // AI 섹션 기본 접힘
  const [aiCurrentPage, setAiCurrentPage] = useState(0); // AI 추천 현재 페이지 (0부터 시작)
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedTab, setSelectedTab] = useState("central");
  const [currentPage, setCurrentPage] = useState(0);
  const [hasMoreData, setHasMoreData] = useState(true);
  const [totalCount, setTotalCount] = useState(0);

  // 컴포넌트 마운트 시 AI 추천 로드
  useEffect(() => {
    if (!user?.id || !user?.token) {
      setLoadingAI(false);
      return;
    }

    const fetchAIRecommendations = async () => {
      const cachedKey = `aiRecommendations_${user.id}`;

      // 1. localStorage 캐시 확인 (5분 유효)
      const cached = localStorage.getItem(cachedKey);
      if (cached) {
        try {
          const parsedCache = JSON.parse(cached);
          const cacheAge = Date.now() - parsedCache.timestamp;

          if (cacheAge < 5 * 60 * 1000) {
            console.log(
              "캐시된 AI 추천 사용:",
              parsedCache.data.length,
              "개"
            );
            setAiRecommendations(parsedCache.data);
            setLoadingAI(false);
            return;
          } else {
            console.log("⏰ 캐시 만료 (5분 경과), 새로 로드합니다...");
          }
        } catch (err) {
          console.error("캐시 파싱 오류:", err);
        }
      }

      // 2. 캐시 없거나 만료되었으면 API 호출
      console.log("🤖 AI 추천 API 호출 시작...");
      setLoadingAI(true);

      try {
        const response = await fetch(
          `http://localhost:8080/api/ai/welfare/recommendations?userId=${user.id}&page=0&size=100`,
          {
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${user.token}`,
            },
          }
        );

        if (response.ok) {
          const result = await response.json();
          const data = result.data?.content || result.data || [];

          // localStorage에 캐시 저장
          localStorage.setItem(
            cachedKey,
            JSON.stringify({
              data: data,
              timestamp: Date.now(),
            })
          );

          console.log("AI 추천 로드 완료:", data.length, "개");
          setAiRecommendations(data);
        } else {
          console.error("AI 추천 API 실패:", response.status);
        }
      } catch (error) {
        console.error("AI 추천 API 오류:", error);
      } finally {
        setLoadingAI(false);
      }
    };

    fetchAIRecommendations();
  }, [user?.id]); // token 제거 - 페이지 진입 시 1번만 호출

  // 탭 변경 시 데이터 새로고침
  useEffect(() => {
    fetchBenefits(0, false);
  }, [selectedTab]);

  // AI 추천은 unified-dashboard에서 백그라운드로 로드됨
  // localStorage 캐시만 사용하므로 더보기 기능 제거

  // AI 추천 탭 변경
  const handleAITabChange = (tab) => {
    setAiRecommendedTab(tab);
    setAiCurrentPage(0); // 탭 변경 시 첫 페이지로 리셋
  };

  // AI 추천 페이지 이동
  const goToPrevAIPage = () => {
    setAiCurrentPage((prev) => Math.max(0, prev - 1));
  };

  const goToNextAIPage = () => {
    setAiCurrentPage((prev) => Math.min(totalAIPages - 1, prev + 1));
  };

  // AI 추천 필터링 (탭별)
  const filteredAIRecommendations = aiRecommendations.filter((benefit) => {
    if (aiRecommendedTab === "all") return true;
    if (aiRecommendedTab === "central")
      return benefit.serviceType === "CENTRAL";
    if (aiRecommendedTab === "local") return benefit.serviceType === "LOCAL";
    return true;
  });

  // AI 추천 페이지네이션 (6개씩 슬라이드)
  const itemsPerPage = 6;
  const totalAIPages = Math.ceil(
    filteredAIRecommendations.length / itemsPerPage
  );
  const startIndex = aiCurrentPage * itemsPerPage;
  const displayedAIRecommendations = filteredAIRecommendations.slice(
    startIndex,
    startIndex + itemsPerPage
  );

  // 혜택 데이터 가져오기
  const fetchBenefits = async (page = 0, append = false) => {
    try {
      setLoading(true);
      setError(null);

      let response;

      // 하나퓨처 맞춤 복지 API 사용 (AI 필터링된 158개 데이터) - 30개씩
      if (selectedTab === "central") {
        response = await hanaFutureWelfareAPI.getCentralBenefits(page, 30);
      } else if (selectedTab === "local") {
        response = await hanaFutureWelfareAPI.getLocalBenefits(page, 30);
      } else {
        response = await hanaFutureWelfareAPI.getAllBenefits(page, 30);
      }

      if (response.success) {
        const data = response.data.content || response.data || [];
        console.log(
          `🎯 하나퓨처 ${selectedTab} 복지 데이터:`,
          data.length,
          "개"
        );

        if (append) {
          setBenefits((prev) => [...prev, ...data]);
        } else {
          setBenefits(data);
        }

        setCurrentPage(page);
        setHasMoreData(data.length === 30);
        setTotalCount(response.data.totalElements || data.length);
      } else {
        console.error("하나퓨처 복지 API 실패:", response);
        setError("혜택 정보를 불러올 수 없습니다. 잠시 후 다시 시도해주세요.");
      }
    } catch (err) {
      console.error("혜택 정보 로딩 오류:", err);
      setError("혜택 정보를 불러올 수 없습니다. 잠시 후 다시 시도해주세요.");
    } finally {
      setLoading(false);
    }
  };

  // 검색 기능
  const handleSearch = (query) => {
    setSearchTerm(query);
  };

  // 더 보기 기능
  const loadMore = () => {
    if (hasMoreData && !loading) {
      fetchBenefits(currentPage + 1, true);
    }
  };

  // 검색어로 필터링된 혜택
  const filteredBenefits = benefits.filter((benefit) => {
    if (!searchTerm) return true;

    const searchLower = searchTerm.toLowerCase();
    return (
      benefit.serviceName?.toLowerCase().includes(searchLower) ||
      benefit.serviceContent?.toLowerCase().includes(searchLower) ||
      benefit.category?.toLowerCase().includes(searchLower) ||
      benefit.lifeCycle?.toLowerCase().includes(searchLower)
    );
  });

  // 혜택 카드 컴포넌트
  const BenefitCard = ({ benefit }) => {
    const handleCardClick = (e) => {
      // 북마크 버튼이나 자세히 보기 버튼을 클릭한 경우가 아니면 페이지 이동
      const target = e.target;
      const isBookmarkClick =
        target.closest(".bookmark-button") ||
        target.closest("[data-bookmark-button]");
      const isDetailButtonClick = target.closest("[data-detail-button]");

      if (!isBookmarkClick && !isDetailButtonClick) {
        window.location.href = `/benefits/${benefit.id}`;
      }
    };

    return (
      <div
        className="bg-white rounded-lg shadow-md p-6 hover:shadow-lg transition-all cursor-pointer flex flex-col h-full"
        onClick={handleCardClick}
      >
        {/* 상단: 제목과 태그 */}
        <div className="flex justify-between items-start gap-3 mb-3">
          <h3 className="text-lg font-semibold text-gray-900 flex-1 line-clamp-2">
            {benefit.serviceName}
          </h3>
          <span
            className={`px-3 py-1 rounded-full text-xs font-medium whitespace-nowrap flex-shrink-0 ${
              benefit.serviceType === "CENTRAL"
                ? "bg-blue-100 text-blue-800"
                : "bg-green-100 text-green-800"
            }`}
          >
            {benefit.serviceType === "CENTRAL" ? "중앙정부" : "지자체"}
          </span>
        </div>

        {/* 설명 */}
        <p className="text-gray-600 text-sm mb-4 line-clamp-3 flex-grow">
          {benefit.serviceContent}
        </p>

        {/* 상세 정보 */}
        <div className="space-y-1.5 mb-4">
          {benefit.category && (
            <div className="flex items-start text-sm text-gray-500">
              <span className="font-medium mr-2 whitespace-nowrap">분야:</span>
              <span className="line-clamp-1">{benefit.category}</span>
            </div>
          )}
          {benefit.lifeCycle && (
            <div className="flex items-start text-sm text-gray-500">
              <span className="font-medium mr-2 whitespace-nowrap">
                생애주기:
              </span>
              <span className="line-clamp-1">{benefit.lifeCycle}</span>
            </div>
          )}
          {benefit.supportAmount && (
            <div className="flex items-start text-sm text-gray-500">
              <span className="font-medium mr-2 whitespace-nowrap">
                지원금액:
              </span>
              <span className="line-clamp-1">
                {benefit.supportAmount.toLocaleString()}원
              </span>
            </div>
          )}
        </div>

        {/* 하단: 기관명과 액션 버튼 */}
        <div className="flex justify-between items-center gap-3 pt-3 border-t border-gray-100 mt-auto">
          <div className="text-sm text-gray-500 truncate flex-1">
            {benefit.jurisdictionName || benefit.receptionAgency}
          </div>
          <div className="flex items-center gap-2 flex-shrink-0">
            <div data-bookmark-button className="bookmark-button">
              <BookmarkButton
                welfareBenefitId={benefit.id}
                size="small"
                showText={false}
              />
            </div>
            <Link
              href={`/benefits/${benefit.id}`}
              data-detail-button
              className="flex items-center text-sm text-blue-600 hover:text-blue-800 font-medium whitespace-nowrap"
              onClick={(e) => e.stopPropagation()}
            >
              자세히 보기 →
            </Link>
          </div>
        </div>
      </div>
    );
  };

  return (
    <div className="max-w-7xl mx-auto p-6">
      {/* 헤더 - 서비스 소개 */}
      <div className="mb-8 bg-white rounded-3xl p-8 border border-gray-200 shadow-sm">
        <div className="flex items-center gap-8">
          <div className="flex-shrink-0">
            <img
              src="/hanacharacter/hanacharacter4.png"
              alt="하나 캐릭터"
              className="w-32 h-32 object-contain"
            />
          </div>
          <div className="flex-1">
            <h1 className="text-3xl font-bold text-gray-900 mb-3">
              하나Future 맞춤 복지혜택
            </h1>
            <p className="text-gray-700 text-lg leading-relaxed">
              <strong className="text-emerald-600">중앙정부</strong>와{" "}
              <strong className="text-teal-600">지자체</strong>의 다양한 복지
              혜택을 한눈에!
              <br />
              <span className="text-gray-600">
                생애주기, 소득 수준, 가족 구성에 맞는 혜택을 쉽고 빠르게
                찾아보세요.
              </span>
            </p>
          </div>
        </div>
      </div>

      {/* AI 맞춤 추천 섹션 */}
      {user && (
        <div className="mb-12 bg-white rounded-2xl p-6 border border-gray-200 shadow-sm">
          <div className="flex items-center justify-between mb-4">
            <div className="flex items-center gap-4">
              <img
                src="/hanacharacter/hanacharacter11.png"
                alt="AI 추천"
                className="w-16 h-16 object-contain"
              />
              <h2 className="text-2xl font-bold text-gray-900">
                {user.name}님을 위한 AI 맞춤 추천
              </h2>
            </div>
            <button
              onClick={() => setAiSectionExpanded(!aiSectionExpanded)}
              className="p-2 hover:bg-emerald-100 rounded-lg transition-colors"
              aria-label={aiSectionExpanded ? "접기" : "펼치기"}
            >
              {aiSectionExpanded ? (
                <ChevronUp className="w-5 h-5 text-emerald-600" />
              ) : (
                <ChevronDown className="w-5 h-5 text-emerald-600" />
              )}
            </button>
          </div>
          <p className="text-gray-600 mb-6 ml-13">
            회원님의 정보를 분석하여 가장 적합한 혜택을 추천해드립니다
          </p>

          {/* 접었을 때는 탭과 내용 숨김 */}
          {aiSectionExpanded && (
            <>
              {/* AI 추천 탭 */}
              <div className="flex space-x-1 bg-white p-1 rounded-lg mb-6">
                {[
                  { key: "central", label: "중앙정부" },
                  { key: "local", label: "지자체" },
                ].map((tab) => (
                  <button
                    key={tab.key}
                    onClick={() => handleAITabChange(tab.key)}
                    className={`px-4 py-2 rounded-md text-sm font-medium transition-colors ${
                      aiRecommendedTab === tab.key
                        ? "bg-emerald-500 text-white shadow-sm"
                        : "text-gray-600 hover:text-gray-900"
                    }`}
                  >
                    {tab.label}
                  </button>
                ))}
              </div>

              {loadingAI ? (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                  {[1, 2, 3, 4, 5, 6].map((i) => (
                    <div
                      key={i}
                      className="bg-white rounded-lg shadow-md p-6 animate-pulse"
                    >
                      <div className="h-6 bg-gray-200 rounded w-3/4 mb-4"></div>
                      <div className="h-4 bg-gray-200 rounded w-full mb-2"></div>
                      <div className="h-4 bg-gray-200 rounded w-5/6 mb-4"></div>
                      <div className="h-4 bg-gray-200 rounded w-1/2"></div>
                    </div>
                  ))}
                </div>
              ) : displayedAIRecommendations.length > 0 ? (
                <>
                  {/* 슬라이드 컨테이너 */}
                  <div className="relative">
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 items-stretch">
                      {displayedAIRecommendations.map((benefit, index) => (
                        <BenefitCard
                          key={`${benefit.id}-${index}`}
                          benefit={benefit}
                        />
                      ))}
                    </div>

                    {/* 좌우 화살표 네비게이션 */}
                    {totalAIPages > 1 && (
                      <div className="flex items-center justify-between mt-8">
                        <button
                          onClick={goToPrevAIPage}
                          disabled={aiCurrentPage === 0}
                          className={`flex items-center gap-2 px-4 py-2 rounded-lg font-medium transition-colors ${
                            aiCurrentPage === 0
                              ? "bg-gray-100 text-gray-400 cursor-not-allowed"
                              : "bg-emerald-600 text-white hover:bg-emerald-700"
                          }`}
                        >
                          <ChevronLeft className="w-5 h-5" />
                          이전
                        </button>

                        <button
                          onClick={goToNextAIPage}
                          disabled={aiCurrentPage === totalAIPages - 1}
                          className={`flex items-center gap-2 px-4 py-2 rounded-lg font-medium transition-colors ${
                            aiCurrentPage === totalAIPages - 1
                              ? "bg-gray-100 text-gray-400 cursor-not-allowed"
                              : "bg-emerald-600 text-white hover:bg-emerald-700"
                          }`}
                        >
                          다음
                          <ChevronRight className="w-5 h-5" />
                        </button>
                      </div>
                    )}
                  </div>
                </>
              ) : filteredAIRecommendations.length === 0 &&
                aiRecommendations.length > 0 ? (
                <div className="text-center py-12">
                  <p className="text-gray-500">
                    선택한 탭에 해당하는 추천 결과가 없습니다
                  </p>
                </div>
              ) : (
                <div className="text-center py-12">
                  <p className="text-gray-500">추천 결과가 없습니다</p>
                </div>
              )}
            </>
          )}
        </div>
      )}

      {/* 검색 및 필터 */}
      <div className="mb-6 space-y-4">
        {/* 검색바 */}
        <div className="relative">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-5 w-5" />
          <input
            type="text"
            placeholder="혜택명, 내용으로 검색..."
            className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            value={searchTerm}
            onChange={(e) => handleSearch(e.target.value)}
          />
        </div>

        {/* 탭 필터 */}
        <div className="flex space-x-1 bg-gray-100 p-1 rounded-lg">
          {[
            { key: "central", label: "중앙정부" },
            { key: "local", label: "지자체" },
          ].map((tab) => (
            <button
              key={tab.key}
              onClick={() => setSelectedTab(tab.key)}
              className={`px-4 py-2 rounded-md text-sm font-medium transition-colors ${
                selectedTab === tab.key
                  ? "bg-white text-blue-600 shadow-sm"
                  : "text-gray-600 hover:text-gray-900"
              }`}
            >
              {tab.label}
            </button>
          ))}
        </div>
      </div>

      {/* 에러 메시지 */}
      {error && (
        <div className="bg-red-50 border border-red-200 rounded-lg p-4 mb-6">
          <p className="text-red-800">{error}</p>
        </div>
      )}

      {/* 로딩 상태 */}
      {loading && benefits.length === 0 && (
        <div className="text-center py-12">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
          <p className="text-gray-600">혜택 정보를 불러오는 중...</p>
        </div>
      )}

      {/* 혜택 목록 */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8 items-stretch">
        {filteredBenefits.map((benefit) => (
          <BenefitCard key={benefit.id} benefit={benefit} />
        ))}
      </div>

      {/* 더 보기 버튼 */}
      {hasMoreData && !loading && filteredBenefits.length > 0 && (
        <div className="text-center">
          <button
            onClick={loadMore}
            className="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
          >
            더 보기
          </button>
        </div>
      )}

      {/* 추가 로딩 */}
      {loading && benefits.length > 0 && (
        <div className="text-center py-4">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600 mx-auto"></div>
        </div>
      )}

      {/* 결과가 없는 경우 */}
      {filteredBenefits.length === 0 && !loading && (
        <div className="text-center py-12">
          <div className="text-gray-400 mb-4">
            <Search className="h-12 w-12 mx-auto" />
          </div>
          <h3 className="text-lg font-medium text-gray-900 mb-2">
            검색 결과가 없습니다
          </h3>
          <p className="text-gray-600">다른 검색어나 필터를 시도해보세요</p>
        </div>
      )}
    </div>
  );
}
