"use client";

import { useState, useEffect } from "react";
import { welfareAPI } from "../../../lib/api";
import { useRouter } from "next/navigation";
import {
  ArrowLeft,
  Building2,
  MapPin,
  Calendar,
  Phone,
  ExternalLink,
  Star,
  Users,
  DollarSign,
  Clock,
  CheckCircle,
  AlertCircle,
} from "lucide-react";

export function WelfareBenefitDetail({ benefitId }) {
  const [benefit, setBenefit] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const router = useRouter();

  useEffect(() => {
    if (benefitId) {
      fetchBenefitDetail();
    }
  }, [benefitId]);

  const fetchBenefitDetail = async () => {
    try {
      setLoading(true);
      const response = await welfareAPI.getBenefitDetail(benefitId);
      if (response.success) {
        setBenefit(response.data);
      } else {
        setError(response.message || "혜택 정보를 불러올 수 없습니다.");
      }
    } catch (err) {
      setError("혜택 정보를 불러오는 중 오류가 발생했습니다.");
    } finally {
      setLoading(false);
    }
  };

  const handleBack = () => {
    router.back();
  };

  const handleBookmark = async () => {
    if (!benefit) return;

    try {
      const response = await welfareAPI.toggleBookmark(benefit.id);
      if (response.success) {
        setBenefit((prev) => ({
          ...prev,
          isBookmarked: !prev.isBookmarked,
        }));
      }
    } catch (err) {
      console.error("즐겨찾기 토글 실패:", err);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-teal-600 mx-auto mb-4"></div>
          <p className="text-gray-600">혜택 정보를 불러오는 중...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <AlertCircle className="h-12 w-12 text-red-500 mx-auto mb-4" />
          <p className="text-red-600 mb-4">{error}</p>
          <button
            onClick={handleBack}
            className="px-4 py-2 bg-teal-600 text-white rounded-lg hover:bg-teal-700 transition-colors"
          >
            돌아가기
          </button>
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
            onClick={handleBack}
            className="px-4 py-2 bg-teal-600 text-white rounded-lg hover:bg-teal-700 transition-colors"
          >
            돌아가기
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* 헤더 */}
      <div className="bg-white shadow-sm border-b border-gray-200">
        <div className="max-w-4xl mx-auto px-4 py-4">
          <div className="flex items-center justify-between">
            <button
              onClick={handleBack}
              className="flex items-center text-gray-600 hover:text-gray-900 transition-colors"
            >
              <ArrowLeft className="h-5 w-5 mr-2" />
              돌아가기
            </button>

            <button
              onClick={handleBookmark}
              className={`flex items-center px-3 py-2 rounded-lg transition-colors ${
                benefit.isBookmarked
                  ? "bg-teal-100 text-teal-700"
                  : "bg-gray-100 text-gray-600 hover:bg-gray-200"
              }`}
            >
              <Star
                className={`h-4 w-4 mr-1 ${
                  benefit.isBookmarked ? "fill-current" : ""
                }`}
              />
              즐겨찾기
            </button>
          </div>
        </div>
      </div>

      {/* 메인 콘텐츠 */}
      <div className="max-w-4xl mx-auto px-4 py-8">
        {/* 혜택 기본 정보 */}
        <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-8 mb-6">
          <div className="flex items-start justify-between mb-6">
            <div className="flex-1">
              <div className="flex items-center mb-3">
                {benefit.serviceType === "CENTRAL" ? (
                  <Building2 className="h-5 w-5 text-blue-600 mr-2" />
                ) : (
                  <MapPin className="h-5 w-5 text-green-600 mr-2" />
                )}
                <span
                  className={`px-2 py-1 rounded-full text-xs font-medium ${
                    benefit.serviceType === "CENTRAL"
                      ? "bg-blue-100 text-blue-700"
                      : "bg-green-100 text-green-700"
                  }`}
                >
                  {benefit.serviceType === "CENTRAL" ? "중앙정부" : "지자체"}
                </span>
              </div>

              <h1 className="text-2xl font-bold text-gray-900 mb-2">
                {benefit.serviceName}
              </h1>

              <p className="text-gray-600 text-lg leading-relaxed">
                {benefit.serviceContent}
              </p>
            </div>
          </div>

          {/* 지원 정보 */}
          {(benefit.supportAmount > 0 || benefit.supportAmountDescription) && (
            <div className="bg-teal-50 border border-teal-200 rounded-lg p-4 mb-6">
              <div className="flex items-center mb-2">
                <DollarSign className="h-5 w-5 text-teal-600 mr-2" />
                <h3 className="font-semibold text-teal-800">지원 내용</h3>
              </div>
              <p className="text-teal-700 font-medium text-lg">
                {benefit.supportAmountDescription ||
                  (benefit.supportAmount
                    ? `${benefit.supportAmount.toLocaleString()}원`
                    : "지원금액 정보 없음")}
              </p>
            </div>
          )}

          {/* 기본 정보 그리드 */}
          <div className="grid md:grid-cols-2 gap-6">
            <div className="space-y-4">
              <div className="flex items-start">
                <Users className="h-5 w-5 text-gray-500 mr-3 mt-0.5" />
                <div>
                  <h4 className="font-medium text-gray-900 mb-1">
                    서비스 대상
                  </h4>
                  <p className="text-gray-600">{benefit.targetDescription}</p>
                </div>
              </div>

              <div className="flex items-start">
                <Building2 className="h-5 w-5 text-gray-500 mr-3 mt-0.5" />
                <div>
                  <h4 className="font-medium text-gray-900 mb-1">소관기관</h4>
                  <p className="text-gray-600">{benefit.jurisdictionName}</p>
                </div>
              </div>

              {benefit.sidoName && (
                <div className="flex items-start">
                  <MapPin className="h-5 w-5 text-gray-500 mr-3 mt-0.5" />
                  <div>
                    <h4 className="font-medium text-gray-900 mb-1">지역</h4>
                    <p className="text-gray-600">
                      {benefit.sidoName}
                      {benefit.sigunguName && ` ${benefit.sigunguName}`}
                    </p>
                  </div>
                </div>
              )}
            </div>

            <div className="space-y-4">
              <div className="flex items-start">
                <Clock className="h-5 w-5 text-gray-500 mr-3 mt-0.5" />
                <div>
                  <h4 className="font-medium text-gray-900 mb-1">지원주기</h4>
                  <p className="text-gray-600">
                    {benefit.supportCycle || "정보 없음"}
                  </p>
                </div>
              </div>

              <div className="flex items-start">
                <CheckCircle className="h-5 w-5 text-gray-500 mr-3 mt-0.5" />
                <div>
                  <h4 className="font-medium text-gray-900 mb-1">
                    온라인 신청
                  </h4>
                  <p className="text-gray-600">
                    {benefit.inquiryUrl ? "가능" : "불가능"}
                  </p>
                </div>
              </div>

              <div className="flex items-start">
                <Calendar className="h-5 w-5 text-gray-500 mr-3 mt-0.5" />
                <div>
                  <h4 className="font-medium text-gray-900 mb-1">생애주기</h4>
                  <p className="text-gray-600">{benefit.lifeCycle}</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* 신청방법 및 관련 정보 */}
        <div className="grid md:grid-cols-2 gap-6 mb-6">
          {/* 신청방법 */}
          <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-8">
            <h2 className="text-xl font-bold text-gray-900 mb-4">신청방법</h2>
            <div className="prose max-w-none">
              <p className="text-gray-700 leading-relaxed whitespace-pre-line">
                {benefit.applicationMethod || "신청방법 정보가 없습니다."}
              </p>
            </div>
          </div>

          {/* 문의처 및 관련 링크 */}
          <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-8">
            <h2 className="text-xl font-bold text-gray-900 mb-4">
              문의처 및 신청
            </h2>
            <div className="space-y-4">
              {benefit.inquiryUrl && (
                <div className="flex items-center">
                  <Phone className="h-5 w-5 text-gray-500 mr-3" />
                  <a
                    href={benefit.inquiryUrl}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="flex items-center text-teal-600 hover:text-teal-700 transition-colors font-medium"
                  >
                    <ExternalLink className="h-4 w-4 mr-2" />
                    온라인 신청
                  </a>
                </div>
              )}

              {!benefit.inquiryUrl && (
                <p className="text-gray-500">문의처 정보가 없습니다.</p>
              )}
            </div>
          </div>
        </div>

        {/* 서비스 상세 정보 */}
        {(benefit.serviceProvisionType ||
          benefit.serviceFirstRegistrationDate) && (
          <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-8">
            <h2 className="text-xl font-bold text-gray-900 mb-6">
              서비스 상세 정보
            </h2>
            <div className="grid md:grid-cols-2 gap-6">
              {benefit.serviceProvisionType && (
                <div>
                  <h3 className="font-semibold text-gray-900 mb-2">
                    서비스 제공 형태
                  </h3>
                  <p className="text-gray-600 text-sm leading-relaxed">
                    {benefit.serviceProvisionType}
                  </p>
                </div>
              )}

              {benefit.serviceFirstRegistrationDate && (
                <div>
                  <h3 className="font-semibold text-gray-900 mb-2">
                    서비스 등록일
                  </h3>
                  <p className="text-gray-600 text-sm leading-relaxed">
                    {benefit.serviceFirstRegistrationDate}
                  </p>
                </div>
              )}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
