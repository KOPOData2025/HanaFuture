"use client";

import React, { useState, useEffect, useRef } from "react";
import { Search, Filter, X, Clock, Trash2 } from "lucide-react";
import { StorageManager } from "../lib/storage";

export function SearchFilter({
  onSearch,
  onFilter,
  placeholder = "혜택을 검색해보세요...",
}) {
  const [searchTerm, setSearchTerm] = useState("");
  const [isFilterOpen, setIsFilterOpen] = useState(false);
  const [showRecentSearches, setShowRecentSearches] = useState(false);
  const [recentSearches, setRecentSearches] = useState([]);
  const [filters, setFilters] = useState({
    serviceType: "all", // all, CENTRAL, LOCAL
    lifeCycle: "all", // all, 임신, 출산, 영유아, 아동
    region: "all", // all, 서울, 경기, 부산 등
    supportAmount: "all", // all, under1M, 1M-5M, over5M
    sortBy: "relevance", // relevance, amount, recent
  });

  const searchInputRef = useRef(null);
  const filterRef = useRef(null);

  useEffect(() => {
    loadRecentSearches();
  }, []);

  useEffect(() => {
    // 검색어가 변경될 때마다 검색 실행 (디바운싱)
    const debounceTimer = setTimeout(() => {
      if (searchTerm.trim()) {
        handleSearch(searchTerm);
      }
    }, 300);

    return () => clearTimeout(debounceTimer);
  }, [searchTerm]);

  useEffect(() => {
    // 필터가 변경될 때마다 필터링 실행
    onFilter(filters);
  }, [filters, onFilter]);

  const loadRecentSearches = () => {
    const recent = StorageManager.getRecentSearches();
    setRecentSearches(recent);
  };

  const handleSearch = (term) => {
    if (term.trim()) {
      StorageManager.addRecentSearch(term.trim());
      loadRecentSearches();
      onSearch(term.trim());
    }
  };

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    if (searchTerm.trim()) {
      handleSearch(searchTerm);
      setShowRecentSearches(false);
    }
  };

  const selectRecentSearch = (term) => {
    setSearchTerm(term);
    handleSearch(term);
    setShowRecentSearches(false);
  };

  const clearRecentSearches = () => {
    StorageManager.clearRecentSearches();
    setRecentSearches([]);
  };

  const updateFilter = (key, value) => {
    setFilters((prev) => ({ ...prev, [key]: value }));
  };

  const resetFilters = () => {
    setFilters({
      serviceType: "all",
      lifeCycle: "all",
      region: "all",
      supportAmount: "all",
      sortBy: "relevance",
    });
  };

  const getActiveFilterCount = () => {
    return Object.entries(filters).filter(
      ([key, value]) => key !== "sortBy" && value !== "all"
    ).length;
  };

  return (
    <div className="relative space-y-4">
      {/* 검색 바 */}
      <div className="relative">
        <form onSubmit={handleSearchSubmit} className="relative">
          <div className="relative">
            <Search className="absolute left-4 top-1/2 transform -translate-y-1/2 h-5 w-5 text-muted-foreground" />
            <input
              ref={searchInputRef}
              type="text"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              onFocus={() => setShowRecentSearches(true)}
              placeholder={placeholder}
              className="w-full pl-12 pr-12 py-4 bg-white dark:bg-gray-800 border border-border rounded-2xl text-lg focus:outline-none focus:ring-2 focus:ring-primary/50 focus:border-primary transition-all"
            />
            {searchTerm && (
              <button
                type="button"
                onClick={() => {
                  setSearchTerm("");
                  onSearch("");
                }}
                className="absolute right-4 top-1/2 transform -translate-y-1/2 p-1 hover:bg-muted rounded-full"
              >
                <X className="h-4 w-4 text-muted-foreground" />
              </button>
            )}
          </div>
        </form>

        {/* 최근 검색어 드롭다운 */}
        {showRecentSearches && recentSearches.length > 0 && (
          <>
            <div
              className="fixed inset-0 z-10"
              onClick={() => setShowRecentSearches(false)}
            />
            <div className="absolute top-full left-0 right-0 mt-2 bg-white dark:bg-gray-800 border border-border rounded-xl shadow-lg z-20 max-h-60 overflow-y-auto">
              <div className="p-3 border-b border-border flex items-center justify-between">
                <span className="text-sm font-medium text-muted-foreground">
                  최근 검색어
                </span>
                <button
                  onClick={clearRecentSearches}
                  className="text-xs text-muted-foreground hover:text-foreground flex items-center gap-1"
                >
                  <Trash2 className="h-3 w-3" />
                  전체 삭제
                </button>
              </div>
              {recentSearches.map((term, index) => (
                <button
                  key={index}
                  onClick={() => selectRecentSearch(term)}
                  className="w-full px-4 py-3 text-left hover:bg-muted/50 flex items-center gap-3 transition-colors"
                >
                  <Clock className="h-4 w-4 text-muted-foreground" />
                  <span>{term}</span>
                </button>
              ))}
            </div>
          </>
        )}
      </div>

      {/* 필터 버튼 */}
      <div className="flex items-center gap-3">
        <button
          onClick={() => setIsFilterOpen(!isFilterOpen)}
          className={`flex items-center gap-2 px-4 py-2 rounded-xl border transition-all ${
            isFilterOpen || getActiveFilterCount() > 0
              ? "bg-primary text-primary-foreground border-primary"
              : "bg-white dark:bg-gray-800 border-border hover:bg-muted/50"
          }`}
        >
          <Filter className="h-4 w-4" />
          <span>필터</span>
          {getActiveFilterCount() > 0 && (
            <span className="bg-white/20 text-xs px-2 py-1 rounded-full">
              {getActiveFilterCount()}
            </span>
          )}
        </button>

        {getActiveFilterCount() > 0 && (
          <button
            onClick={resetFilters}
            className="text-sm text-muted-foreground hover:text-foreground"
          >
            필터 초기화
          </button>
        )}
      </div>

      {/* 필터 패널 */}
      {isFilterOpen && (
        <div
          ref={filterRef}
          className="bg-white dark:bg-gray-800 border border-border rounded-xl p-6 space-y-6 shadow-lg"
        >
          <div className="flex items-center justify-between">
            <h3 className="font-bold text-lg">상세 필터</h3>
            <button
              onClick={() => setIsFilterOpen(false)}
              className="p-1 hover:bg-muted rounded-lg"
            >
              <X className="h-4 w-4" />
            </button>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {/* 서비스 유형 */}
            <div>
              <label className="block text-sm font-medium mb-3">
                서비스 유형
              </label>
              <select
                value={filters.serviceType}
                onChange={(e) => updateFilter("serviceType", e.target.value)}
                className="w-full p-3 border border-border rounded-lg bg-background focus:outline-none focus:ring-2 focus:ring-primary/50"
              >
                <option value="all">전체</option>
                <option value="CENTRAL">중앙정부</option>
                <option value="LOCAL">지자체</option>
              </select>
            </div>

            {/* 생애주기 */}
            <div>
              <label className="block text-sm font-medium mb-3">생애주기</label>
              <select
                value={filters.lifeCycle}
                onChange={(e) => updateFilter("lifeCycle", e.target.value)}
                className="w-full p-3 border border-border rounded-lg bg-background focus:outline-none focus:ring-2 focus:ring-primary/50"
              >
                <option value="all">전체</option>
                <option value="임신">임신</option>
                <option value="출산">출산</option>
                <option value="영유아">영유아</option>
                <option value="아동">아동</option>
              </select>
            </div>

            {/* 지역 */}
            <div>
              <label className="block text-sm font-medium mb-3">지역</label>
              <select
                value={filters.region}
                onChange={(e) => updateFilter("region", e.target.value)}
                className="w-full p-3 border border-border rounded-lg bg-background focus:outline-none focus:ring-2 focus:ring-primary/50"
              >
                <option value="all">전체</option>
                <option value="서울">서울특별시</option>
                <option value="경기">경기도</option>
                <option value="부산">부산광역시</option>
                <option value="대구">대구광역시</option>
                <option value="인천">인천광역시</option>
              </select>
            </div>

            {/* 지원금액 */}
            <div>
              <label className="block text-sm font-medium mb-3">지원금액</label>
              <select
                value={filters.supportAmount}
                onChange={(e) => updateFilter("supportAmount", e.target.value)}
                className="w-full p-3 border border-border rounded-lg bg-background focus:outline-none focus:ring-2 focus:ring-primary/50"
              >
                <option value="all">전체</option>
                <option value="under1M">100만원 미만</option>
                <option value="1M-5M">100만원 - 500만원</option>
                <option value="over5M">500만원 이상</option>
              </select>
            </div>
          </div>

          {/* 정렬 */}
          <div>
            <label className="block text-sm font-medium mb-3">정렬 기준</label>
            <div className="flex flex-wrap gap-2">
              {[
                { value: "relevance", label: "관련도순" },
                { value: "amount", label: "지원금액순" },
                { value: "recent", label: "최신순" },
                { value: "deadline", label: "마감임박순" },
              ].map((option) => (
                <button
                  key={option.value}
                  onClick={() => updateFilter("sortBy", option.value)}
                  className={`px-4 py-2 rounded-lg border transition-all ${
                    filters.sortBy === option.value
                      ? "bg-primary text-primary-foreground border-primary"
                      : "bg-background border-border hover:bg-muted/50"
                  }`}
                >
                  {option.label}
                </button>
              ))}
            </div>
          </div>

          {/* 필터 적용 버튼 */}
          <div className="flex gap-3 pt-4 border-t border-border">
            <button
              onClick={resetFilters}
              className="flex-1 px-4 py-3 border border-border rounded-lg hover:bg-muted/50 transition-colors"
            >
              초기화
            </button>
            <button
              onClick={() => setIsFilterOpen(false)}
              className="flex-1 px-4 py-3 bg-primary text-primary-foreground rounded-lg hover:bg-primary/90 transition-colors"
            >
              적용
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
