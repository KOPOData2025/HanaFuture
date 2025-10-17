"use client";

import React from "react";
import { useRouter } from "next/navigation";
import {
  Users,
  Target,
  TrendingUp,
  Calendar,
  Shield,
  ChevronRight,
  Heart,
  Gift,
  Sparkles,
} from "lucide-react";

export default function TogetherSavingsIntro({ onStartCreation }) {
  const router = useRouter();

  return (
    <div className="min-h-screen bg-gradient-to-b from-green-50 via-white to-teal-50">
      {/* Hero Section */}
      <section className="relative overflow-hidden bg-gradient-to-r from-green-500 to-teal-500 text-white py-20">
        <div className="absolute inset-0 flex items-center justify-center opacity-10">
          <img
            src="/hanacharacter/hanafamily.png"
            alt=""
            className="w-auto h-full max-w-none object-contain"
          />
        </div>
        <div className="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
          <h2 className="text-4xl md:text-5xl font-bold mb-4">
            함께 모으면
            <br />더 큰 꿈이 됩니다
          </h2>
          <p className="text-xl md:text-2xl text-green-50 mb-8">
            가족이 함께하는 목표 달성 적금
          </p>
          <div className="flex flex-wrap justify-center gap-4 text-lg font-semibold">
            <div className="bg-white/20 backdrop-blur-sm px-6 py-3 rounded-lg">
              기본금리 <span className="text-yellow-300">4.0%</span>
            </div>
            <div className="bg-white/20 backdrop-blur-sm px-6 py-3 rounded-lg">
              가족초대 <span className="text-yellow-300">+0.3%</span>
            </div>
            <div className="bg-white/20 backdrop-blur-sm px-6 py-3 rounded-lg">
              목표달성 <span className="text-yellow-300">+0.5%</span>
            </div>
          </div>
        </div>
      </section>

      {/* Key Features - 레퍼런스 스타일 */}
      <section className="py-16 bg-white">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-12">
            <h3 className="text-3xl font-bold text-gray-900 mb-4">
              함께 적금의 특별한 혜택
            </h3>
            <p className="text-lg text-gray-600">
              가족과 함께라면 더 많은 금리 혜택을 받을 수 있어요
            </p>
          </div>

          {/* 3개 주요 정보 */}
          <div className="grid grid-cols-3 gap-8 mb-12">
            {/* 최소기간 */}
            <div className="text-center">
              <div className="w-20 h-20 mx-auto mb-4 bg-teal-100 rounded-full flex items-center justify-center">
                <Calendar className="w-10 h-10 text-teal-600" />
              </div>
              <div className="text-gray-600 text-sm mb-1">최소기간</div>
              <div className="text-2xl font-bold text-gray-900">3개월</div>
            </div>

            {/* 금액 */}
            <div className="text-center">
              <div className="w-20 h-20 mx-auto mb-4 bg-teal-100 rounded-full flex items-center justify-center">
                <svg
                  className="w-10 h-10 text-teal-600"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
                  />
                </svg>
              </div>
              <div className="text-gray-600 text-sm mb-1">금액</div>
              <div className="text-2xl font-bold text-gray-900">1만원 이상</div>
            </div>

            {/* 금리 */}
            <div className="text-center">
              <div className="w-20 h-20 mx-auto mb-4 bg-teal-100 rounded-full flex items-center justify-center">
                <TrendingUp className="w-10 h-10 text-teal-600" />
              </div>
              <div className="text-gray-600 text-sm mb-1">금리 연</div>
              <div className="text-2xl font-bold text-gray-900">3.00%</div>
              <div className="text-lg text-teal-600 font-semibold">~ 4.00%</div>
            </div>
          </div>

          {/* 가입하기 버튼 */}
          <div className="text-center">
            <button
              onClick={onStartCreation}
              className="bg-teal-600 hover:bg-teal-700 text-white px-16 py-4 rounded-lg text-lg font-bold transition-colors shadow-lg hover:shadow-xl"
            >
              가입하기
            </button>
          </div>
        </div>
      </section>

      {/* Interest Rate Section */}
      <section className="py-16 bg-gradient-to-r from-green-50 to-teal-50">
        <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-12">
            <h3 className="text-3xl font-bold text-gray-900 mb-4">금리 안내</h3>
            <p className="text-lg text-gray-600">
              조건에 따라 추가 우대금리를 받으세요
            </p>
          </div>

          <div className="bg-white rounded-2xl shadow-xl overflow-hidden">
            <div className="bg-gradient-to-r from-green-500 to-teal-500 text-white p-8 text-center">
              <p className="text-lg mb-2">최대 적용 금리</p>
              <p className="text-6xl font-bold">
                4.8<span className="text-3xl">%</span>
              </p>
            </div>

            <div className="p-8">
              <div className="space-y-6">
                {/* Base Rate */}
                <div className="flex items-center justify-between p-6 bg-gray-50 rounded-xl">
                  <div>
                    <p className="text-lg font-bold text-gray-900">기본금리</p>
                    <p className="text-sm text-gray-600 mt-1">
                      함께 적금 가입 시 기본 제공
                    </p>
                  </div>
                  <div className="text-3xl font-bold text-green-600">4.0%</div>
                </div>

                {/* Family Bonus */}
                <div className="flex items-center justify-between p-6 bg-green-50 rounded-xl border-2 border-green-200">
                  <div>
                    <p className="text-lg font-bold text-gray-900 flex items-center gap-2">
                      <Users className="w-5 h-5 text-green-600" />
                      가족 초대 우대금리
                    </p>
                    <p className="text-sm text-gray-600 mt-1">
                      가족이 함께 적금할 때
                    </p>
                  </div>
                  <div className="text-3xl font-bold text-green-600">+0.3%</div>
                </div>

                {/* Goal Achievement */}
                <div className="flex items-center justify-between p-6 bg-teal-50 rounded-xl border-2 border-teal-200">
                  <div>
                    <p className="text-lg font-bold text-gray-900 flex items-center gap-2">
                      <Target className="w-5 h-5 text-teal-600" />
                      목표 달성 우대금리
                    </p>
                    <p className="text-sm text-gray-600 mt-1">
                      설정한 목표 금액 달성 시
                    </p>
                  </div>
                  <div className="text-3xl font-bold text-teal-600">+0.5%</div>
                </div>
              </div>

              <div className="mt-8 p-6 bg-yellow-50 rounded-xl border border-yellow-200">
                <p className="text-sm text-gray-700 leading-relaxed">
                  <strong className="text-yellow-700">💡 우대금리 안내:</strong>{" "}
                  우대금리는 목표 달성 시에만 적용됩니다. 만기까지 꾸준히
                  저축하여 목표를 달성하시면 최대 4.8%의 금리 혜택을 받으실 수
                  있습니다.
                </p>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* How It Works */}
      <section className="py-16 bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-12">
            <h3 className="text-3xl font-bold text-gray-900 mb-4">
              함께 적금 시작하기
            </h3>
            <p className="text-lg text-gray-600">
              간단한 4단계로 가족과 함께 저축을 시작하세요
            </p>
          </div>

          <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-8">
            <div className="text-center">
              <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4 text-2xl font-bold text-green-600">
                1
              </div>
              <h4 className="text-lg font-bold text-gray-900 mb-2">
                목표 설정
              </h4>
              <p className="text-gray-600">
                원하는 목표 금액과 저축 기간을 설정하세요
              </p>
            </div>

            <div className="text-center">
              <div className="w-16 h-16 bg-teal-100 rounded-full flex items-center justify-center mx-auto mb-4 text-2xl font-bold text-teal-600">
                2
              </div>
              <h4 className="text-lg font-bold text-gray-900 mb-2">
                가족 초대
              </h4>
              <p className="text-gray-600">
                함께 저축할 가족 구성원 1명을 초대하세요
              </p>
            </div>

            <div className="text-center">
              <div className="w-16 h-16 bg-cyan-100 rounded-full flex items-center justify-center mx-auto mb-4 text-2xl font-bold text-cyan-600">
                3
              </div>
              <h4 className="text-lg font-bold text-gray-900 mb-2">
                자동 이체
              </h4>
              <p className="text-gray-600">
                각자 원하는 날짜에 고정 금액을 자동 이체하세요
              </p>
            </div>

            <div className="text-center">
              <div className="w-16 h-16 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-4 text-2xl font-bold text-blue-600">
                4
              </div>
              <h4 className="text-lg font-bold text-gray-900 mb-2">
                목표 달성
              </h4>
              <p className="text-gray-600">
                만기 시 목표 달성과 함께 우대금리를 받으세요
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* Example Calculation */}
      <section className="py-16 bg-gradient-to-b from-gray-50 to-white">
        <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-12">
            <h3 className="text-3xl font-bold text-gray-900 mb-4">
              예상 수령액 예시
            </h3>
            <p className="text-lg text-gray-600">
              함께 적금으로 얼마나 받을 수 있을까요?
            </p>
          </div>

          <div className="bg-white rounded-2xl shadow-xl p-8">
            <div className="mb-8 p-6 bg-gradient-to-r from-green-50 to-teal-50 rounded-xl">
              <h4 className="text-lg font-bold text-gray-900 mb-4">
                가입 조건 예시
              </h4>
              <div className="grid md:grid-cols-2 gap-4">
                <div className="flex justify-between">
                  <span className="text-gray-600">참여 인원</span>
                  <span className="font-semibold text-gray-900">2명</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-600">1인당 월 납입액</span>
                  <span className="font-semibold text-gray-900">250,000원</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-600">저축 기간</span>
                  <span className="font-semibold text-gray-900">12개월</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-600">목표 금액</span>
                  <span className="font-semibold text-gray-900">
                    6,000,000원
                  </span>
                </div>
              </div>
            </div>

            <div className="space-y-4">
              <div className="flex justify-between items-center p-4 bg-gray-50 rounded-lg">
                <span className="text-gray-700">총 원금</span>
                <span className="text-xl font-bold text-gray-900">
                  6,000,000원
                </span>
              </div>
              <div className="flex justify-between items-center p-4 bg-green-50 rounded-lg">
                <span className="text-gray-700">적용 금리 (4.8%)</span>
                <span className="text-xl font-bold text-green-600">
                  약 156,000원
                </span>
              </div>
              <div className="flex justify-between items-center p-4 bg-gray-50 rounded-lg">
                <span className="text-gray-700">세금 (15.4%)</span>
                <span className="text-xl font-bold text-gray-900">
                  -24,024원
                </span>
              </div>
              <div className="h-px bg-gray-300 my-2"></div>
              <div className="flex justify-between items-center p-6 bg-gradient-to-r from-green-500 to-teal-500 rounded-xl text-white">
                <span className="text-lg font-semibold">예상 만기 수령액</span>
                <span className="text-3xl font-bold">6,131,976원</span>
              </div>
            </div>

            <p className="text-sm text-gray-500 mt-6 text-center">
              * 위 계산은 예시이며, 실제 수령액은 가입 조건과 금리에 따라 달라질
              수 있습니다.
            </p>
          </div>
        </div>
      </section>

      {/* Important Notes */}
      <section className="py-16 bg-white">
        <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-12">
            <h3 className="text-3xl font-bold text-gray-900 mb-4">유의사항</h3>
          </div>

          <div className="bg-gray-50 rounded-2xl p-8">
            <ul className="space-y-4">
              <li className="flex gap-3">
                <ChevronRight className="w-6 h-6 text-green-600 flex-shrink-0 mt-0.5" />
                <p className="text-gray-700">
                  함께 적금은 최대 2명까지 참여할 수 있으며, 가족 구성원 누구나
                  개설 가능합니다.
                </p>
              </li>
              <li className="flex gap-3">
                <ChevronRight className="w-6 h-6 text-green-600 flex-shrink-0 mt-0.5" />
                <p className="text-gray-700">
                  월 납입액은 계좌 개설 시 설정한 고정 금액으로, 추가 납입은
                  불가능합니다.
                </p>
              </li>
              <li className="flex gap-3">
                <ChevronRight className="w-6 h-6 text-green-600 flex-shrink-0 mt-0.5" />
                <p className="text-gray-700">
                  중도 해지가 불가능하므로, 만기까지 꾸준히 저축하셔야 합니다.
                </p>
              </li>
              <li className="flex gap-3">
                <ChevronRight className="w-6 h-6 text-green-600 flex-shrink-0 mt-0.5" />
                <p className="text-gray-700">
                  목표 금액을 달성하지 못할 경우, 목표 달성 우대금리(+0.5%)는
                  적용되지 않습니다.
                </p>
              </li>
              <li className="flex gap-3">
                <ChevronRight className="w-6 h-6 text-green-600 flex-shrink-0 mt-0.5" />
                <p className="text-gray-700">
                  계좌 생성 후 추가 초대는 불가능하며, 참여자의 중도 탈퇴도
                  불가능합니다.
                </p>
              </li>
              <li className="flex gap-3">
                <ChevronRight className="w-6 h-6 text-green-600 flex-shrink-0 mt-0.5" />
                <p className="text-gray-700">
                  만기 시 원금과 이자는 계좌 개설자(대표자)의 계좌로 입금됩니다.
                </p>
              </li>
              <li className="flex gap-3">
                <ChevronRight className="w-6 h-6 text-green-600 flex-shrink-0 mt-0.5" />
                <p className="text-gray-700">
                  이자 소득에 대해서는 소득세 및 지방소득세(합계 15.4%)가
                  원천징수됩니다.
                </p>
              </li>
            </ul>
          </div>
        </div>
      </section>

      {/* Footer Spacing */}
      <div className="h-20"></div>
    </div>
  );
}
