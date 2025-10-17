"use client";

import React, { useState, useEffect } from "react";
import {
  Shield,
  Heart,
  Baby,
  GraduationCap,
  Home,
  Car,
  Users,
  Star,
  CheckCircle,
  Info,
  Calculator,
  ArrowRight,
  TrendingUp,
  Clock,
  Award,
} from "lucide-react";
import { useAuth } from "../../../contexts/AuthContext";

export function InsuranceRecommendations() {
  const [selectedCategory, setSelectedCategory] = useState("family");
  const [recommendations, setRecommendations] = useState([]);
  const [loading, setLoading] = useState(true);
  const { user } = useAuth();

  const categories = [
    {
      id: "family",
      label: "가족보험",
      icon: "/hanacharacter/hanacharacter14.png",
      description: "가족 전체 보장",
    },
    {
      id: "child",
      label: "자녀보험",
      icon: "/hana3dIcon/hanaIcon3d_3_65.png",
      description: "자녀 전용 보장",
    },
    {
      id: "health",
      label: "건강보험",
      icon: "/hana3dIcon/hanaIcon3d_4_65.png",
      description: "의료비 보장",
    },
    {
      id: "life",
      label: "생명보험",
      icon: "/hana3dIcon/hanaIcon3d_5_65.png",
      description: "생명 보장",
    },
  ];

  useEffect(() => {
    loadRecommendations();
  }, [selectedCategory, user]);

  const loadRecommendations = async () => {
    try {
      setLoading(true);

      // 실제 보험 추천 API 호출 (추후 구현 시)
      // const response = await apiClient.insurance.getRecommendations(selectedCategory);
      // if (response.success) {
      //   setRecommendations(response.data);
      //   return;
      // }

      // API가 없으므로 실제 데이터 기반 목 데이터 사용
      const mockData = {
        family: [
          {
            id: 1,
            name: "하나 가족사랑 종합보험",
            company: "하나생명",
            monthlyPremium: 150000,
            coverage: 50000000,
            features: [
              "가족 전체 보장",
              "질병/상해",
              "입원비 지원",
              "수술비 보장",
            ],
            rating: 4.8,
            isRecommended: true,
            description: "가족 구성원 모두를 보장하는 종합보험",
          },
          {
            id: 2,
            name: "우리가족 안심보험",
            company: "하나생명",
            monthlyPremium: 120000,
            coverage: 30000000,
            features: ["기본 질병보장", "상해보장", "입원일당", "수술비"],
            rating: 4.5,
            isRecommended: false,
            description: "기본적인 가족 보장을 제공하는 보험",
          },
        ],
        child: [
          {
            id: 3,
            name: "하나 자녀사랑 보험",
            company: "하나생명",
            monthlyPremium: 85000,
            coverage: 20000000,
            features: ["자녀 전용", "교육비 지원", "상해보장", "질병보장"],
            rating: 4.9,
            isRecommended: true,
            description: "자녀의 건강과 교육을 함께 보장",
          },
          {
            id: 4,
            name: "키즈 건강보험",
            company: "하나생명",
            monthlyPremium: 65000,
            coverage: 15000000,
            features: ["소아과 특화", "예방접종", "응급실비", "입원비"],
            rating: 4.6,
            isRecommended: false,
            description: "소아 전문 의료보장 보험",
          },
        ],
        health: [
          {
            id: 5,
            name: "하나 건강플러스 보험",
            company: "하나생명",
            monthlyPremium: 95000,
            coverage: 30000000,
            features: ["3대 질병", "암보장", "뇌혈관", "심장질환"],
            rating: 4.7,
            isRecommended: true,
            description: "주요 질병에 대한 집중 보장",
          },
        ],
        life: [
          {
            id: 6,
            name: "하나 생명보험",
            company: "하나생명",
            monthlyPremium: 200000,
            coverage: 100000000,
            features: ["사망보장", "고도장애", "생활비 지원", "유족연금"],
            rating: 4.8,
            isRecommended: true,
            description: "가장을 위한 든든한 생명보장",
          },
        ],
      };

      setRecommendations(mockData[selectedCategory] || []);

      // 실제 API 호출 (추후 구현)
      // const response = await apiClient.insurance.getRecommendations(selectedCategory);
      // setRecommendations(response.data);
    } catch (error) {
      console.error("보험 추천 로딩 실패:", error);
    } finally {
      setLoading(false);
    }
  };

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat("ko-KR").format(amount);
  };

  return (
    <div className="space-y-8">
      {/* 헤더 */}
      <div className="bg-white rounded-3xl p-6 border border-gray-100">
        <div className="flex items-center justify-between mb-6">
          <div>
            <h2 className="text-3xl font-black text-gray-900">보험 추천</h2>
            <p className="text-gray-600">
              가족 상황에 맞는 최적의 보험 상품을 찾아보세요
            </p>
          </div>
          <img
            src="/hanacharacter/hanacharacter13.png"
            alt="보험 추천 하나 캐릭터"
            className="w-20 h-20 object-contain"
          />
        </div>

        {/* 카테고리 탭 */}
        <div className="flex gap-2 overflow-x-auto">
          {categories.map((category) => (
            <button
              key={category.id}
              onClick={() => setSelectedCategory(category.id)}
              className={`flex items-center gap-2 px-6 py-3 rounded-2xl transition-all duration-200 whitespace-nowrap ${
                selectedCategory === category.id
                  ? "bg-gray-900 text-white"
                  : "bg-gray-100 text-gray-600 hover:bg-gray-200"
              }`}
            >
              <img
                src={category.icon}
                alt={category.label}
                className="w-4 h-4 object-contain"
              />
              <div className="text-left">
                <div className="font-semibold">{category.label}</div>
                <div className="text-xs opacity-80">{category.description}</div>
              </div>
            </button>
          ))}
        </div>
      </div>

      {/* 추천 보험 상품 */}
      {loading ? (
        <div className="flex items-center justify-center py-12">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-gray-900"></div>
        </div>
      ) : (
        <div className="grid md:grid-cols-2 gap-6">
          {recommendations.map((insurance) => (
            <div
              key={insurance.id}
              className="bg-white rounded-3xl p-6 border border-gray-100 hover:shadow-lg transition-all duration-300"
            >
              {/* 헤더 */}
              <div className="flex items-start justify-between mb-6">
                <div className="flex items-center gap-3">
                  <div className="w-12 h-12 bg-gradient-to-br from-orange-50 to-red-50 rounded-2xl flex items-center justify-center">
                    <Shield className="h-6 w-6 text-orange-600" />
                  </div>
                  <div>
                    <h3 className="font-bold text-gray-900">
                      {insurance.name}
                    </h3>
                    <p className="text-sm text-gray-500">{insurance.company}</p>
                  </div>
                </div>
                {insurance.isRecommended && (
                  <div className="bg-emerald-100 text-emerald-700 text-xs px-2 py-1 rounded-full font-medium">
                    추천
                  </div>
                )}
              </div>

              {/* 보험료 및 보장금액 */}
              <div className="grid grid-cols-2 gap-4 mb-6">
                <div className="text-center p-4 bg-gray-50 rounded-2xl">
                  <p className="text-sm text-gray-500 mb-1">월 보험료</p>
                  <p className="text-lg font-bold text-gray-900">
                    ₩{formatCurrency(insurance.monthlyPremium)}
                  </p>
                </div>
                <div className="text-center p-4 bg-gray-50 rounded-2xl">
                  <p className="text-sm text-gray-500 mb-1">보장금액</p>
                  <p className="text-lg font-bold text-gray-900">
                    ₩{formatCurrency(insurance.coverage)}
                  </p>
                </div>
              </div>

              {/* 특징 */}
              <div className="mb-6">
                <p className="font-semibold text-gray-900 mb-3">주요 보장</p>
                <div className="grid grid-cols-2 gap-2">
                  {insurance.features.map((feature, index) => (
                    <div key={index} className="flex items-center gap-2">
                      <CheckCircle className="h-4 w-4 text-emerald-600" />
                      <span className="text-sm text-gray-700">{feature}</span>
                    </div>
                  ))}
                </div>
              </div>

              {/* 평점 */}
              <div className="flex items-center justify-between mb-6">
                <div className="flex items-center gap-2">
                  <Star className="h-4 w-4 text-yellow-400 fill-current" />
                  <span className="font-semibold text-gray-900">
                    {insurance.rating}
                  </span>
                  <span className="text-sm text-gray-500">고객 만족도</span>
                </div>
                <div className="flex items-center gap-1 text-emerald-600">
                  <TrendingUp className="h-4 w-4" />
                  <span className="text-sm font-medium">인기 상품</span>
                </div>
              </div>

              {/* 설명 */}
              <p className="text-gray-600 mb-6 leading-relaxed">
                {insurance.description}
              </p>

              {/* 액션 버튼 */}
              <div className="flex gap-3">
                <button className="flex-1 bg-gray-100 text-gray-700 py-3 rounded-2xl font-semibold hover:bg-gray-200 transition-colors">
                  상세보기
                </button>
                <button className="flex-1 bg-gray-900 text-white py-3 rounded-2xl font-semibold hover:bg-gray-800 transition-colors flex items-center justify-center gap-2">
                  상담신청
                  <ArrowRight className="h-4 w-4" />
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* 보험 가이드 */}
      <div className="bg-gradient-to-br from-blue-50 to-indigo-50 rounded-3xl p-6">
        <div className="flex items-center gap-3 mb-4">
          <Info className="h-6 w-6 text-blue-600" />
          <h3 className="text-xl font-bold text-gray-900">보험 선택 가이드</h3>
        </div>

        <div className="grid md:grid-cols-3 gap-6">
          {[
            {
              title: "가족 구성 고려",
              description: "자녀 수, 연령대에 맞는 보장 선택",
              icon: "/hana3dIcon/hanaIcon3d_6_47.png",
            },
            {
              title: "보장 금액 설정",
              description: "소득의 10-15% 수준으로 적정 보험료",
              icon: "/hana3dIcon/hanaIcon3d_6_83.png",
            },
            {
              title: "전문가 상담",
              description: "하나생명 전문가와 1:1 맞춤 상담",
              icon: "/hana3dIcon/hanaIcon3d_6_47.png",
            },
          ].map((guide, index) => (
            <div key={index} className="bg-white rounded-2xl p-4">
              <div className="flex items-center gap-3 mb-3">
                <img
                  src={guide.icon}
                  alt={guide.title}
                  className="w-6 h-6 object-contain"
                />
                <h4 className="font-semibold text-gray-900">{guide.title}</h4>
              </div>
              <p className="text-sm text-gray-600">{guide.description}</p>
            </div>
          ))}
        </div>
      </div>

      {/* 보험료 계산기 */}
      <div className="bg-white rounded-3xl p-6 border border-gray-100">
        <div className="flex items-center justify-between mb-6">
          <div>
            <h3 className="text-xl font-bold text-gray-900">보험료 계산기</h3>
            <p className="text-gray-600">
              간단한 정보로 예상 보험료를 계산해보세요
            </p>
          </div>
          <Calculator className="h-8 w-8 text-gray-400" />
        </div>

        <div className="grid md:grid-cols-4 gap-4 mb-6">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              나이
            </label>
            <input
              type="number"
              placeholder="35"
              className="w-full p-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-emerald-500"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              성별
            </label>
            <select className="w-full p-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-emerald-500">
              <option>남성</option>
              <option>여성</option>
            </select>
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              자녀 수
            </label>
            <select className="w-full p-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-emerald-500">
              <option>0명</option>
              <option>1명</option>
              <option>2명</option>
              <option>3명 이상</option>
            </select>
          </div>
          <div className="flex items-end">
            <button className="w-full bg-emerald-600 text-white py-3 rounded-xl font-semibold hover:bg-emerald-700 transition-colors">
              계산하기
            </button>
          </div>
        </div>

        <div className="bg-emerald-50 rounded-2xl p-4">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">예상 월 보험료</p>
              <p className="text-2xl font-bold text-emerald-600">₩125,000</p>
            </div>
            <div className="text-right">
              <p className="text-sm text-gray-600">연간 보험료</p>
              <p className="text-lg font-semibold text-gray-900">₩1,500,000</p>
            </div>
          </div>
        </div>
      </div>

      {/* 보험 혜택 안내 */}
      <div className="bg-white rounded-3xl p-6 border border-gray-100">
        <h3 className="text-xl font-bold text-gray-900 mb-6">
          하나생명 고객 혜택
        </h3>

        <div className="grid md:grid-cols-2 gap-6">
          {[
            {
              title: "하나금융그룹 우대",
              description: "하나은행 고객 특별 할인 혜택",
              discount: "최대 10%",
              icon: "/hana3dIcon/hanaIcon3d_2_47.png",
            },
            {
              title: "건강관리 서비스",
              description: "정기 건강검진 및 상담 서비스",
              discount: "무료 제공",
              icon: "/hana3dIcon/hanaIcon3d_3_47.png",
            },
            {
              title: "교육비 지원",
              description: "자녀 교육비 추가 지원 혜택",
              discount: "연 200만원",
              icon: "/hana3dIcon/hanaIcon3d_4_47.png",
            },
            {
              title: "24시간 상담",
              description: "언제든 보험 상담 및 청구 지원",
              discount: "365일",
              icon: "/hana3dIcon/hanaIcon3d_5_47.png",
            },
          ].map((benefit, index) => (
            <div
              key={index}
              className="flex items-center gap-4 p-4 bg-gray-50 rounded-2xl"
            >
              <img
                src={benefit.icon}
                alt={benefit.title}
                className="w-8 h-8 object-contain"
              />
              <div className="flex-1">
                <h4 className="font-semibold text-gray-900">{benefit.title}</h4>
                <p className="text-sm text-gray-600">{benefit.description}</p>
              </div>
              <div className="text-right">
                <p className="font-bold text-emerald-600">{benefit.discount}</p>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
