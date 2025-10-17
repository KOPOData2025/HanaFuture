"use client";

import { useState, useEffect } from "react";
import {
  Heart,
  Search,
  Filter,
  Calendar,
  MapPin,
  ExternalLink,
  Trash2,
  BookOpen,
  Star,
  ChevronDown,
  Gift,
  Baby,
  GraduationCap,
  Home,
  Users,
  TrendingUp,
  ArrowRight,
  Plus,
} from "lucide-react";
import { bookmarkAPI } from "../../../lib/bookmark-api";
import { useAuth } from "../../../contexts/AuthContext";
import { BookmarkButton } from "./bookmark-button";
import { toast } from "sonner";

/**
 * 즐겨찾기 목록 페이지 컴포넌트
 */
export function BookmarksPage() {
  const [bookmarks, setBookmarks] = useState([]);
  const [filteredBookmarks, setFilteredBookmarks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedCategory, setSelectedCategory] = useState("all");
  const [selectedLifeCycle, setSelectedLifeCycle] = useState("all");
  const [showFilters, setShowFilters] = useState(false);
  const { user } = useAuth();

  // 카테고리 및 생애주기 옵션
  const categories = [
    ...new Set(bookmarks.map((b) => b.welfareBenefit.category).filter(Boolean)),
  ];
  const lifeCycles = [
    ...new Set(
      bookmarks.map((b) => b.welfareBenefit.lifeCycle).filter(Boolean)
    ),
  ];

  useEffect(() => {
    if (user) {
      loadBookmarks();
    }
  }, [user]);

  // 페이지가 포커스될 때마다 새로고침
  useEffect(() => {
    const handleFocus = () => {
      if (user) {
        loadBookmarks();
      }
    };

    window.addEventListener("focus", handleFocus);
    return () => window.removeEventListener("focus", handleFocus);
  }, [user]);

  // 즐겨찾기 상태 변경 이벤트 수신
  useEffect(() => {
    const handleBookmarkAdded = () => {
      if (user) {
        loadBookmarks();
      }
    };

    const handleBookmarkRemoved = () => {
      if (user) {
        loadBookmarks();
      }
    };

    window.addEventListener("bookmarkAdded", handleBookmarkAdded);
    window.addEventListener("bookmarkRemoved", handleBookmarkRemoved);

    return () => {
      window.removeEventListener("bookmarkAdded", handleBookmarkAdded);
      window.removeEventListener("bookmarkRemoved", handleBookmarkRemoved);
    };
  }, [user]);

  useEffect(() => {
    filterBookmarks();
  }, [bookmarks, searchTerm, selectedCategory, selectedLifeCycle]);

  /**
   * 즐겨찾기 목록 로드
   */
  const loadBookmarks = async () => {
    try {
      setLoading(true);
      const response = await bookmarkAPI.getAllBookmarks();

      if (response.success) {
        setBookmarks(response.data);
      } else {
        setBookmarks([]);
      }
    } catch (error) {
      console.error("즐겨찾기 로드 실패:", error);
      toast.error("즐겨찾기 목록을 불러오는데 실패했습니다.");
      setBookmarks([]);
    } finally {
      setLoading(false);
    }
  };

  /**
   * 즐겨찾기 필터링
   */
  const filterBookmarks = () => {
    let filtered = bookmarks;

    // 검색어 필터
    if (searchTerm) {
      filtered = filtered.filter(
        (bookmark) =>
          bookmark.welfareBenefit.serviceName
            .toLowerCase()
            .includes(searchTerm.toLowerCase()) ||
          bookmark.welfareBenefit.targetDescription
            ?.toLowerCase()
            .includes(searchTerm.toLowerCase()) ||
          bookmark.memo?.toLowerCase().includes(searchTerm.toLowerCase())
      );
    }

    // 카테고리 필터
    if (selectedCategory !== "all") {
      filtered = filtered.filter(
        (bookmark) => bookmark.welfareBenefit.category === selectedCategory
      );
    }

    // 생애주기 필터
    if (selectedLifeCycle !== "all") {
      filtered = filtered.filter(
        (bookmark) => bookmark.welfareBenefit.lifeCycle === selectedLifeCycle
      );
    }

    setFilteredBookmarks(filtered);
  };

  /**
   * 즐겨찾기 제거 후 목록에서 제거
   */
  const handleBookmarkRemoved = (welfareBenefitId) => {
    setBookmarks((prev) =>
      prev.filter((bookmark) => bookmark.welfareBenefit.id !== welfareBenefitId)
    );
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-emerald-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">즐겨찾기를 불러오는 중...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-6xl mx-auto px-4 py-8">
        {/* 헤더 */}
        <div className="mb-8">
          <div className="flex items-center justify-between mb-4">
            <div className="flex items-center gap-3">
              <img
                src="/hanacharacter/hanacharacter12.png"
                alt="즐겨찾기 하나 캐릭터"
                className="w-24 h-24 object-contain"
              />
              <div>
                <h1 className="text-3xl font-bold text-gray-900">즐겨찾기</h1>
                <p className="text-gray-600">저장한 복지 혜택을 관리하세요</p>
              </div>
            </div>
          </div>

          {/* 검색 및 필터 */}
          <div className="bg-white rounded-2xl p-6 shadow-sm border border-gray-100">
            <div className="flex flex-col lg:flex-row gap-4">
              {/* 검색 */}
              <div className="flex-1 relative">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
                <input
                  type="text"
                  placeholder="즐겨찾기 검색..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className="w-full pl-10 pr-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-transparent"
                />
              </div>

              {/* 필터 토글 버튼 */}
              <button
                onClick={() => setShowFilters(!showFilters)}
                className="flex items-center gap-2 px-4 py-3 bg-gray-100 text-gray-700 rounded-xl hover:bg-gray-200 transition-colors"
              >
                <Filter className="h-5 w-5" />
                필터
                <ChevronDown
                  className={`h-4 w-4 transition-transform ${
                    showFilters ? "rotate-180" : ""
                  }`}
                />
              </button>
            </div>

            {/* 필터 옵션 */}
            {showFilters && (
              <div className="mt-4 pt-4 border-t border-gray-200">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  {/* 카테고리 필터 */}
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      카테고리
                    </label>
                    <select
                      value={selectedCategory}
                      onChange={(e) => setSelectedCategory(e.target.value)}
                      className="w-full px-3 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500"
                    >
                      <option value="all">전체 카테고리</option>
                      {categories.map((category) => (
                        <option key={category} value={category}>
                          {category}
                        </option>
                      ))}
                    </select>
                  </div>

                  {/* 생애주기 필터 */}
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      생애주기
                    </label>
                    <select
                      value={selectedLifeCycle}
                      onChange={(e) => setSelectedLifeCycle(e.target.value)}
                      className="w-full px-3 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500"
                    >
                      <option value="all">전체 생애주기</option>
                      {lifeCycles.map((lifeCycle) => (
                        <option key={lifeCycle} value={lifeCycle}>
                          {lifeCycle}
                        </option>
                      ))}
                    </select>
                  </div>
                </div>
              </div>
            )}
          </div>
        </div>

        {/* 즐겨찾기 목록 */}
        {filteredBookmarks.length === 0 ? (
          bookmarks.length === 0 ? (
            <EmptyBookmarksState />
          ) : (
            <div className="text-center py-16">
              <div className="w-24 h-24 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-4">
                <Heart className="h-12 w-12 text-gray-400" />
              </div>
              <h3 className="text-xl font-semibold text-gray-900 mb-2">
                검색 결과가 없습니다
              </h3>
              <p className="text-gray-600">다른 검색어나 필터를 사용해보세요</p>
            </div>
          )
        ) : (
          <div className="grid gap-6">
            {filteredBookmarks.map((bookmark) => (
              <div
                key={bookmark.id}
                className="bg-white rounded-2xl p-6 shadow-sm border border-gray-100 hover:shadow-md transition-shadow"
              >
                <div className="flex justify-between items-start mb-4">
                  <div className="flex-1">
                    <div className="flex items-start justify-between">
                      <div>
                        <h3 className="text-xl font-bold text-gray-900 mb-2">
                          {bookmark.welfareBenefit.serviceName}
                        </h3>
                        <div className="flex flex-wrap gap-2 mb-3">
                          {bookmark.welfareBenefit.category && (
                            <span className="px-3 py-1 bg-blue-100 text-blue-800 text-sm font-medium rounded-full">
                              {bookmark.welfareBenefit.category}
                            </span>
                          )}
                          {bookmark.welfareBenefit.lifeCycle && (
                            <span className="px-3 py-1 bg-green-100 text-green-800 text-sm font-medium rounded-full">
                              {bookmark.welfareBenefit.lifeCycle}
                            </span>
                          )}
                          {bookmark.welfareBenefit.serviceType && (
                            <span className="px-3 py-1 bg-purple-100 text-purple-800 text-sm font-medium rounded-full">
                              {bookmark.welfareBenefit.serviceTypeDisplayName ||
                                bookmark.welfareBenefit.serviceType}
                            </span>
                          )}
                        </div>
                      </div>
                      <BookmarkButton
                        welfareBenefitId={bookmark.welfareBenefit.id}
                        initialIsBookmarked={true}
                        useHanaFuture={true} // HanaFuture API 사용
                        size="small"
                        showText={false}
                        onBookmarkChange={(isBookmarked) => {
                          if (!isBookmarked) {
                            handleBookmarkRemoved(bookmark.welfareBenefit.id);
                          }
                        }}
                      />
                    </div>

                    {/* 대상 및 내용 */}
                    {bookmark.welfareBenefit.targetDescription && (
                      <div className="mb-3">
                        <p className="text-gray-700 text-sm leading-relaxed">
                          <span className="font-medium">대상:</span>{" "}
                          {bookmark.welfareBenefit.targetDescription}
                        </p>
                      </div>
                    )}

                    {/* 지원 금액 */}
                    {bookmark.welfareBenefit.supportAmount && (
                      <div className="mb-3">
                        <p className="text-emerald-700 font-semibold">
                          지원금액:{" "}
                          {bookmark.welfareBenefit.supportAmount.toLocaleString()}
                          원
                        </p>
                      </div>
                    )}

                    {/* 소관기관 및 지역 정보 */}
                    <div className="flex flex-wrap gap-4 text-sm text-gray-600 mb-4">
                      {bookmark.welfareBenefit.jurisdictionName && (
                        <div className="flex items-center gap-1">
                          <BookOpen className="h-4 w-4" />
                          {bookmark.welfareBenefit.jurisdictionName}
                        </div>
                      )}
                      {(bookmark.welfareBenefit.sidoName ||
                        bookmark.welfareBenefit.sigunguName) && (
                        <div className="flex items-center gap-1">
                          <MapPin className="h-4 w-4" />
                          {[
                            bookmark.welfareBenefit.sidoName,
                            bookmark.welfareBenefit.sigunguName,
                          ]
                            .filter(Boolean)
                            .join(" ")}
                        </div>
                      )}
                      <div className="flex items-center gap-1">
                        <Calendar className="h-4 w-4" />
                        {new Date(bookmark.createdAt).toLocaleDateString()}
                      </div>
                    </div>

                    {/* 링크 */}
                    {bookmark.welfareBenefit.inquiryUrl && (
                      <div className="mt-4">
                        <a
                          href={bookmark.welfareBenefit.inquiryUrl}
                          target="_blank"
                          rel="noopener noreferrer"
                          className="inline-flex items-center gap-2 text-emerald-600 hover:text-emerald-700 font-medium"
                        >
                          신청 링크
                          <ExternalLink className="h-4 w-4" />
                        </a>
                      </div>
                    )}
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}

// 즐겨찾기 빈 상태 컴포넌트
function EmptyBookmarksState() {
  const welfareCategories = [
    {
      icon: Baby,
      title: "육아 지원",
      description: "출산, 육아, 보육 관련 혜택",
      count: "156개",
      color: "from-pink-500 to-rose-500",
      examples: ["아동수당", "육아휴직급여", "보육료 지원"],
    },
    {
      icon: GraduationCap,
      title: "교육 지원",
      description: "학비, 장학금, 교육비 관련",
      count: "89개",
      color: "from-blue-500 to-cyan-500",
      examples: ["교육급여", "대학등록금", "방과후 지원"],
    },
    {
      icon: Home,
      title: "주거 지원",
      description: "주택, 전세, 월세 관련 혜택",
      count: "124개",
      color: "from-emerald-500 to-teal-500",
      examples: ["주거급여", "전세자금대출", "청년주택"],
    },
    {
      icon: Users,
      title: "취업 지원",
      description: "구직, 창업, 직업훈련 관련",
      count: "203개",
      color: "from-purple-500 to-indigo-500",
      examples: ["구직급여", "청년창업", "직업훈련"],
    },
  ];

  return (
    <div className="space-y-8">
      {/* 메인 히어로 섹션 */}
      <div className="relative overflow-hidden bg-gradient-to-br from-red-50 via-pink-50 to-rose-50 rounded-3xl p-8 lg:p-12">
        {/* 배경 장식 */}
        <div className="absolute top-0 right-0 w-64 h-64 bg-gradient-to-br from-red-200 to-pink-300 rounded-full opacity-20 transform translate-x-32 -translate-y-32"></div>
        <div className="absolute bottom-0 left-0 w-48 h-48 bg-gradient-to-tr from-rose-200 to-pink-300 rounded-full opacity-20 transform -translate-x-24 translate-y-24"></div>

        <div className="relative z-10 max-w-4xl mx-auto text-center">
          <div className="space-y-6">
            <div className="inline-flex items-center gap-2 px-4 py-2 bg-red-100 text-red-700 rounded-full text-sm font-medium">
              <Heart className="h-4 w-4" />
              나만의 혜택 모음
            </div>
            <h2 className="text-4xl lg:text-5xl font-black text-gray-900 leading-tight">
              관심 있는 혜택을
              <br />
              <span className="bg-gradient-to-r from-red-600 to-pink-600 bg-clip-text text-transparent">
                즐겨찾기에 모아보세요
              </span>
            </h2>
            <p className="text-lg text-gray-600 leading-relaxed max-w-2xl mx-auto">
              정부혜택 페이지에서 하트 버튼을 눌러 즐겨찾기에 추가하면,
              <br />
              언제든 쉽게 다시 찾아볼 수 있어요.
            </p>

            <div className="flex flex-col sm:flex-row gap-4 justify-center">
              <button
                onClick={() => (window.location.hash = "#benefits")}
                className="flex items-center justify-center gap-3 px-8 py-4 bg-gradient-to-r from-red-600 to-pink-600 text-white rounded-2xl hover:from-red-700 hover:to-pink-700 transition-all duration-300 transform hover:scale-105 shadow-lg hover:shadow-xl font-semibold text-lg"
              >
                <Gift className="h-5 w-5" />
                정부혜택 둘러보기
              </button>
              <button className="flex items-center justify-center gap-3 px-8 py-4 bg-white text-gray-700 rounded-2xl border-2 border-gray-200 hover:border-red-300 hover:bg-red-50 transition-all duration-300 font-semibold text-lg">
                <Search className="h-5 w-5" />
                혜택 검색하기
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* 인기 복지 카테고리 */}
      <div className="bg-white rounded-2xl p-8 border border-gray-100">
        <div className="text-center mb-8">
          <h3 className="text-2xl font-bold text-gray-900 mb-3">
            이런 혜택들을 확인해보세요
          </h3>
          <p className="text-gray-600">
            카테고리별로 다양한 정부 혜택들이 준비되어 있어요
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          {welfareCategories.map((category, index) => (
            <div
              key={index}
              className="group bg-gradient-to-br from-gray-50 to-white rounded-2xl p-6 border border-gray-100 hover:shadow-lg hover:border-red-200 transition-all duration-300 cursor-pointer"
            >
              <div className="text-center space-y-4">
                <div
                  className={`w-16 h-16 bg-gradient-to-br ${category.color} rounded-2xl flex items-center justify-center mx-auto group-hover:scale-110 transition-transform duration-300`}
                >
                  <category.icon className="h-8 w-8 text-white" />
                </div>

                <div>
                  <h4 className="font-bold text-gray-900 mb-1 group-hover:text-red-600 transition-colors">
                    {category.title}
                  </h4>
                  <p className="text-sm text-gray-600 mb-3">
                    {category.description}
                  </p>
                  <div className="inline-flex items-center gap-1 px-3 py-1 bg-red-100 text-red-700 rounded-full text-sm font-medium">
                    <Gift className="h-3 w-3" />
                    {category.count}
                  </div>
                </div>

                <div className="space-y-1">
                  {category.examples.map((example, idx) => (
                    <div
                      key={idx}
                      className="text-xs text-gray-500 bg-gray-50 rounded-lg px-2 py-1"
                    >
                      {example}
                    </div>
                  ))}
                </div>

                <div className="flex items-center justify-center gap-1 text-red-600 font-medium text-sm group-hover:gap-2 transition-all">
                  <span>둘러보기</span>
                  <ArrowRight className="h-4 w-4 group-hover:translate-x-0.5 transition-transform" />
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* 즐겨찾기 사용법 안내 */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="bg-white rounded-2xl p-8 border border-gray-100 hover:shadow-lg transition-all duration-300">
          <div className="w-16 h-16 bg-gradient-to-br from-red-500 to-pink-500 rounded-2xl flex items-center justify-center mb-6">
            <Search className="h-8 w-8 text-white" />
          </div>
          <h3 className="text-xl font-bold text-gray-900 mb-3">쉬운 검색</h3>
          <p className="text-gray-600 leading-relaxed mb-4">
            키워드, 지역, 연령대별로 나에게 맞는 복지 혜택을 쉽게 찾을 수
            있어요.
          </p>
          <div className="flex items-center gap-2 text-red-600 font-medium">
            <TrendingUp className="h-4 w-4" />
            <span>572개 혜택 검색 가능</span>
          </div>
        </div>

        <div className="bg-white rounded-2xl p-8 border border-gray-100 hover:shadow-lg transition-all duration-300">
          <div className="w-16 h-16 bg-gradient-to-br from-pink-500 to-rose-500 rounded-2xl flex items-center justify-center mb-6">
            <Heart className="h-8 w-8 text-white" />
          </div>
          <h3 className="text-xl font-bold text-gray-900 mb-3">간편 저장</h3>
          <p className="text-gray-600 leading-relaxed mb-4">
            관심 있는 혜택에 하트 버튼을 누르면 즐겨찾기에 바로 저장됩니다.
          </p>
          <div className="flex items-center gap-2 text-pink-600 font-medium">
            <Plus className="h-4 w-4" />
            <span>원클릭 즐겨찾기</span>
          </div>
        </div>
      </div>

      {/* CTA 섹션 */}
      <div className="bg-gradient-to-r from-red-600 to-pink-600 rounded-2xl p-8 text-white">
        <div className="max-w-4xl mx-auto text-center">
          <h3 className="text-2xl font-bold mb-4">
            지금 바로 혜택을 찾아보세요
          </h3>
          <p className="text-red-100 mb-6 text-lg">
            놓치고 있던 혜택이 있을 수도 있어요
          </p>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
            <div className="bg-white/10 rounded-xl p-4 backdrop-blur-sm">
              <p className="text-3xl font-bold mb-1">572개</p>
              <p className="text-red-100 text-sm">정부 혜택</p>
            </div>
            <div className="bg-white/10 rounded-xl p-4 backdrop-blur-sm">
              <p className="text-3xl font-bold mb-1">AI</p>
              <p className="text-red-100 text-sm">맞춤 추천</p>
            </div>
            <div className="bg-white/10 rounded-xl p-4 backdrop-blur-sm">
              <p className="text-3xl font-bold mb-1">실시간</p>
              <p className="text-red-100 text-sm">업데이트</p>
            </div>
          </div>

          <button
            onClick={() => (window.location.hash = "#benefits")}
            className="inline-flex items-center gap-3 px-8 py-4 bg-white text-red-600 rounded-2xl hover:bg-red-50 transition-all duration-300 transform hover:scale-105 shadow-lg font-semibold text-lg"
          >
            <Gift className="h-5 w-5" />
            정부혜택 페이지로 이동
          </button>
        </div>
      </div>
    </div>
  );
}
