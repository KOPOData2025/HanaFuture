"use client";

import { useParams, useRouter } from "next/navigation";
import { useState, useEffect } from "react";
import {
  ArrowLeft,
  Calendar,
  User,
  Phone,
  MapPin,
  FileText,
  ExternalLink,
  Bookmark,
  BookmarkCheck,
} from "lucide-react";
import { hanaFutureWelfareAPI } from "../../../lib/api";
import { bookmarkAPI } from "../../../lib/bookmark-api";
import { useAuth } from "../../../contexts/AuthContext";

export default function BenefitDetailClient() {
  const params = useParams();
  const router = useRouter();
  const { user } = useAuth();
  const benefitId = params.id;

  const [benefit, setBenefit] = useState(null);
  const [loading, setLoading] = useState(true);
  const [isBookmarked, setIsBookmarked] = useState(false);

  useEffect(() => {
    if (benefitId) {
      loadBenefitDetail();
      checkBookmark();
    }
  }, [benefitId]);

  const loadBenefitDetail = async () => {
    try {
      setLoading(true);
      const response = await hanaFutureWelfareAPI.getBenefitById(benefitId);
      if (response.success) {
        setBenefit(response.data);
      } else {
        console.error("혜택 상세 정보 로드 실패:", response.message);
      }
    } catch (error) {
      console.error("혜택 상세 정보 로드 오류:", error);
    } finally {
      setLoading(false);
    }
  };

  const checkBookmark = async () => {
    if (!user?.id) return;
    try {
      // HanaFuture 전용 API 사용
      const result = await bookmarkAPI.checkHanaFutureBookmark(benefitId);
      setIsBookmarked(result.data || false);
    } catch (error) {
      console.error("북마크 확인 오류:", error);
      setIsBookmarked(false);
    }
  };

  const toggleBookmark = async () => {
    if (!user?.id) {
      alert("로그인이 필요합니다.");
      return;
    }

    try {
      if (isBookmarked) {
        // HanaFuture 전용 API 사용
        await bookmarkAPI.removeHanaFutureBookmark(benefitId);
        setIsBookmarked(false);
      } else {
        // HanaFuture 전용 API 사용
        await bookmarkAPI.addHanaFutureBookmark(benefitId);
        setIsBookmarked(true);
      }
    } catch (error) {
      console.error("북마크 토글 오류:", error);
    }
  };

  const handleApplicationClick = () => {
    let applicationUrl;

    if (benefit.serviceType === "LOCAL") {
      applicationUrl = "https://www.bokjiro.go.kr";
    } else {
      applicationUrl =
        benefit.applicationUrl || benefit.detailUrl || benefit.inquiryUrl;
    }

    console.log("신청 링크 정보:", {
      serviceType: benefit.serviceType,
      applicationUrl: benefit.applicationUrl,
      detailUrl: benefit.detailUrl,
      inquiryUrl: benefit.inquiryUrl,
      finalUrl: applicationUrl,
    });

    if (applicationUrl) {
      window.open(applicationUrl, "_blank");
    } else {
      alert("신청 링크가 제공되지 않습니다. 담당 부서로 문의해주세요.");
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-emerald-600 mx-auto mb-4"></div>
          <p className="text-gray-600">로딩 중...</p>
        </div>
      </div>
    );
  }

  if (!benefit) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <p className="text-gray-600 mb-4">혜택 정보를 찾을 수 없습니다.</p>
          <button
            onClick={() => router.push("/government-benefits")}
            className="px-4 py-2 bg-emerald-600 text-white rounded-lg hover:bg-emerald-700"
          >
            목록으로 돌아가기
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* 헤더 */}
      <div className="bg-white border-b border-gray-200 sticky top-0 z-10">
        <div className="max-w-4xl mx-auto px-4 py-4">
          <button
            onClick={() => {
              // 정부혜택 페이지로 이동
              router.push("/?tab=benefits");
            }}
            className="flex items-center gap-2 text-gray-600 hover:text-gray-900"
          >
            <ArrowLeft className="w-5 h-5" />
            <span className="font-medium">뒤로가기</span>
          </button>
        </div>
      </div>

      {/* 메인 컨텐츠 */}
      <div className="max-w-4xl mx-auto px-4 py-8">
        {/* 타이틀 & 태그 */}
        <div className="bg-white rounded-2xl p-8 shadow-sm mb-6">
          <div className="flex items-start gap-3 mb-4">
            <span
              className={`px-3 py-1 rounded-full text-sm font-medium ${
                benefit.serviceType === "CENTRAL"
                  ? "bg-blue-100 text-blue-700"
                  : "bg-green-100 text-green-700"
              }`}
            >
              {benefit.serviceType === "CENTRAL" ? "중앙정부" : "지자체"}
            </span>
            {benefit.category && (
              <span className="px-3 py-1 rounded-full text-sm font-medium bg-gray-100 text-gray-700">
                {benefit.category}
              </span>
            )}
          </div>

          <h1 className="text-3xl font-bold text-gray-900 mb-4">
            {benefit.serviceName || benefit.benefitName}
          </h1>

          {(benefit.servicePurpose || benefit.summary) && (
            <p className="text-gray-600 text-lg leading-relaxed mb-6">
              {benefit.servicePurpose || benefit.summary}
            </p>
          )}
        </div>

        {/* 지원 대상 */}
        {benefit.targetDescription && (
          <div className="bg-white rounded-2xl p-8 shadow-sm mb-6">
            <div className="flex items-center gap-2 mb-4">
              <User className="w-6 h-6 text-emerald-600" />
              <h2 className="text-2xl font-bold text-gray-900">지원 대상</h2>
            </div>
            <div className="prose max-w-none">
              <p className="text-gray-700 leading-relaxed whitespace-pre-wrap">
                {benefit.targetDescription}
              </p>
            </div>
          </div>
        )}

        {/* 선정 기준 */}
        {benefit.selectionCriteria && (
          <div className="bg-white rounded-2xl p-8 shadow-sm mb-6">
            <div className="flex items-center gap-2 mb-4">
              <FileText className="w-6 h-6 text-emerald-600" />
              <h2 className="text-2xl font-bold text-gray-900">선정 기준</h2>
            </div>
            <div className="prose max-w-none">
              <p className="text-gray-700 leading-relaxed whitespace-pre-wrap">
                {benefit.selectionCriteria}
              </p>
            </div>
          </div>
        )}

        {/* 지원 내용 */}
        {benefit.serviceContent && (
          <div className="bg-white rounded-2xl p-8 shadow-sm mb-6">
            <div className="flex items-center gap-2 mb-4">
              <FileText className="w-6 h-6 text-emerald-600" />
              <h2 className="text-2xl font-bold text-gray-900">지원 내용</h2>
            </div>
            <div className="prose max-w-none">
              <p className="text-gray-700 leading-relaxed whitespace-pre-wrap">
                {benefit.serviceContent}
              </p>
            </div>
          </div>
        )}

        {/* 신청 방법 */}
        {benefit.applicationMethod && (
          <div className="bg-white rounded-2xl p-8 shadow-sm mb-6">
            <div className="flex items-center gap-2 mb-4">
              <Phone className="w-6 h-6 text-emerald-600" />
              <h2 className="text-2xl font-bold text-gray-900">신청 방법</h2>
            </div>
            <div className="prose max-w-none">
              <p className="text-gray-700 leading-relaxed whitespace-pre-wrap mb-6">
                {benefit.applicationMethod}
              </p>

              {/* 신청 링크 버튼 */}
              <button
                onClick={handleApplicationClick}
                className="w-full py-4 bg-gradient-to-r from-emerald-600 to-teal-600 text-white rounded-xl font-bold text-lg hover:from-emerald-700 hover:to-teal-700 transition-all shadow-lg hover:shadow-xl flex items-center justify-center gap-2"
              >
                <ExternalLink className="w-5 h-5" />
                온라인 신청하기
              </button>
            </div>
          </div>
        )}

        {/* 신청 기간 */}
        {benefit.applicationPeriod && (
          <div className="bg-white rounded-2xl p-8 shadow-sm mb-6">
            <div className="flex items-center gap-2 mb-4">
              <Calendar className="w-6 h-6 text-emerald-600" />
              <h2 className="text-2xl font-bold text-gray-900">신청 기간</h2>
            </div>
            <div className="prose max-w-none">
              <p className="text-gray-700 leading-relaxed whitespace-pre-wrap">
                {benefit.applicationPeriod}
              </p>
            </div>
          </div>
        )}

        {/* 제출 서류 */}
        {benefit.requiredDocuments && (
          <div className="bg-white rounded-2xl p-8 shadow-sm mb-6">
            <div className="flex items-center gap-2 mb-4">
              <FileText className="w-6 h-6 text-emerald-600" />
              <h2 className="text-2xl font-bold text-gray-900">제출 서류</h2>
            </div>
            <div className="prose max-w-none">
              <p className="text-gray-700 leading-relaxed whitespace-pre-wrap">
                {benefit.requiredDocuments}
              </p>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
