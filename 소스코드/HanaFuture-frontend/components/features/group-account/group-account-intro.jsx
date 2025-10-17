"use client";

import React from "react";
import {
  Users,
  Shield,
  Bell,
  Heart,
  Target,
  TrendingUp,
  ChevronRight,
  Smartphone,
  PiggyBank,
  Send,
} from "lucide-react";

export function GroupAccountIntro({ onStartCreation, onCancel }) {
  const handleStartCreation = () => {
    window.scrollTo({ top: 0, behavior: "smooth" });
    onStartCreation();
  };

  return (
    <div className="min-h-screen bg-gradient-to-b from-emerald-50 via-white to-teal-50">
      {/* Hero Section */}
      <section className="relative overflow-hidden bg-gradient-to-r from-emerald-500 to-teal-500 text-white py-20">
        <div className="absolute inset-0 flex items-center justify-center opacity-10">
          <img
            src="/hanacharacter/hanafamily.png"
            alt=""
            className="w-auto h-full max-w-none object-contain"
          />
        </div>
        <div className="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
          <h2 className="text-4xl md:text-5xl font-bold mb-4">
            우리 가족만의
            <br />
            똑똑한 돈 관리
          </h2>
          <p className="text-xl md:text-2xl text-emerald-50">
            투명하고 편리한 가족 공동 계좌 서비스
          </p>
        </div>
      </section>

      {/* Important Notes */}
      <section className="py-16 bg-gradient-to-b from-gray-50 to-white">
        <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-12">
            <h3 className="text-3xl font-bold text-gray-900 mb-4">유의사항</h3>
          </div>

          <div className="bg-white rounded-2xl shadow-lg p-8 border border-gray-200">
            <ul className="space-y-4">
              <li className="flex gap-3">
                <ChevronRight className="w-6 h-6 text-emerald-600 flex-shrink-0 mt-0.5" />
                <p className="text-gray-700">
                  하나 모임통장은 가족 구성원 누구나 개설할 수 있으며, 가족
                  초대는 무제한입니다.
                </p>
              </li>
              <li className="flex gap-3">
                <ChevronRight className="w-6 h-6 text-emerald-600 flex-shrink-0 mt-0.5" />
                <p className="text-gray-700">
                  모임통장은 자유 입출금 계좌이므로 언제든지 입금 및 출금이
                  가능합니다.
                </p>
              </li>
              <li className="flex gap-3">
                <ChevronRight className="w-6 h-6 text-emerald-600 flex-shrink-0 mt-0.5" />
                <p className="text-gray-700">
                  모든 거래 내역은 실시간으로 가족 구성원 모두에게 알림으로
                  전송됩니다.
                </p>
              </li>
              <li className="flex gap-3">
                <ChevronRight className="w-6 h-6 text-emerald-600 flex-shrink-0 mt-0.5" />
                <p className="text-gray-700">
                  모임통장 개설 및 관리에는 수수료가 부과되지 않습니다.
                </p>
              </li>
              <li className="flex gap-3">
                <ChevronRight className="w-6 h-6 text-emerald-600 flex-shrink-0 mt-0.5" />
                <p className="text-gray-700">
                  가족 구성원은 모임통장의 모든 거래 내역을 조회할 수 있으나,
                  입출금은 각자 연결된 계좌를 통해서만 가능합니다.
                </p>
              </li>
              <li className="flex gap-3">
                <ChevronRight className="w-6 h-6 text-emerald-600 flex-shrink-0 mt-0.5" />
                <p className="text-gray-700">
                  모임통장 해지는 계좌 개설자(대표자)만 가능하며, 잔액이 0원일
                  때만 해지할 수 있습니다.
                </p>
              </li>
              <li className="flex gap-3">
                <ChevronRight className="w-6 h-6 text-emerald-600 flex-shrink-0 mt-0.5" />
                <p className="text-gray-700">
                  모임통장은 예금자보호법에 따라 1인당 최고 5천만원까지
                  보호됩니다.
                </p>
              </li>
            </ul>
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-16 bg-gradient-to-r from-emerald-500 to-teal-500">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 text-center text-white">
          <h3 className="text-3xl md:text-4xl font-bold mb-4">
            지금 바로 시작하세요
          </h3>
          <p className="text-xl text-emerald-50 mb-8">
            가족과 함께하는 똑똑한 돈 관리, 하나 모임통장
          </p>
          <button
            onClick={handleStartCreation}
            className="bg-white text-emerald-600 px-12 py-4 rounded-lg text-lg font-bold hover:bg-emerald-50 transition-colors shadow-xl hover:shadow-2xl"
          >
            모임통장 개설하기
          </button>
        </div>
      </section>
    </div>
  );
}
