"use client";

import { useState, useEffect } from "react";
import {
  Plus,
  PiggyBank,
  Target,
  TrendingUp,
  Calendar,
  Users,
  Heart,
  Sparkles,
  ArrowRight,
} from "lucide-react";
import { useAuth } from "../../../contexts/AuthContext";

export function SavingsProducts({ onStartTogetherFlow }) {
  const { user } = useAuth();
  const [savingGoals, setSavingGoals] = useState([]);
  const [loading, setLoading] = useState(true);

  // 컴포넌트 마운트 시 적금 목록 조회
  useEffect(() => {
    fetchSavingGoals();
  }, [user]);

  const fetchSavingGoals = async () => {
    try {
      setLoading(true);

      if (!user?.id) {
        console.log("사용자 정보가 없어 적금 목록을 불러올 수 없습니다.");
        setSavingGoals([]);
        return;
      }

      // 백엔드 API 호출
      const response = await fetch(
        `http://localhost:8080/api/savings/user/${user.id}`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
          },
        }
      );

      if (response.ok) {
        const result = await response.json();
        console.log("적금 목록 조회 성공:", result);
        setSavingGoals(result.data || []);
      } else {
        console.error("적금 목록 조회 실패:", response.status);
        setSavingGoals([]);
      }
    } catch (error) {
      console.error("적금 목록 조회 중 오류:", error);
      setSavingGoals([]);
    } finally {
      setLoading(false);
    }
  };

  const getCategoryIcon = (category) => {
    const icons = {
      family: Heart,
      travel: TrendingUp,
      home: Target,
      education: Users,
      emergency: PiggyBank,
      other: Target,
    };
    return icons[category] || Target;
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-2 border-gray-200 border-t-blue-500"></div>
      </div>
    );
  }

  return (
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* 페이지 헤더 */}
      <div className="text-center mb-8">
        <div className="inline-flex items-center gap-3 mb-4">
              <img
                src="/hanacharacter/hanacharacter8.png"
            alt="함께 적금 캐릭터"
            className="w-16 h-16 object-contain"
          />
          <h1 className="text-3xl font-black text-gray-900">함께 적금</h1>
        </div>
        <p className="text-lg text-gray-600 mb-6">
                  가족과 함께 목표를 달성하는 스마트한 저축
                </p>
            <button
          onClick={() => onStartTogetherFlow && onStartTogetherFlow()}
          className="inline-flex items-center gap-2 bg-gradient-to-r from-blue-600 to-indigo-600 text-white px-6 py-3 rounded-2xl hover:from-blue-700 hover:to-indigo-700 transition-all duration-300 font-semibold shadow-lg"
            >
          <Plus className="w-5 h-5" />
          함께 적금 가입하기
            </button>
      </div>

        {savingGoals.length === 0 ? (
        <div className="bg-white rounded-3xl shadow-lg border border-gray-100 p-12 text-center">
          <div className="w-24 h-24 bg-gradient-to-br from-blue-50 to-indigo-50 rounded-full flex items-center justify-center mx-auto mb-6">
              <PiggyBank className="w-12 h-12 text-blue-600" />
            </div>
            <h3 className="text-2xl font-bold text-gray-900 mb-4">
              첫 번째 적금 목표를 만들어보세요
            </h3>
          <p className="text-gray-600 mb-8 max-w-md mx-auto">
              가족과 함께 달성하는 저축 목표로 더 큰 꿈을 이루어보세요
            </p>
            <button
            onClick={() => onStartTogetherFlow && onStartTogetherFlow()}
            className="bg-gradient-to-r from-blue-600 to-indigo-600 text-white px-8 py-4 rounded-2xl hover:from-blue-700 hover:to-indigo-700 transition-all duration-300 font-bold text-lg shadow-lg"
            >
            함께 적금 가입하기
            </button>
          </div>
        ) : (
        <div className="space-y-8">
          {/* 적금 목표 카드들 */}
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                  {savingGoals.map((goal) => {
                    const IconComponent = getCategoryIcon(goal.category);
              const achievementRate = Math.round(
                ((goal.currentAmount || 0) / goal.targetAmount) * 100
              );

                    return (
                      <div
                        key={goal.id}
                  className="bg-gradient-to-br from-blue-50 to-indigo-50 p-6 rounded-3xl border border-blue-100 hover:shadow-xl transition-all duration-300 cursor-pointer relative"
                >
                  <div className="absolute top-4 right-4 w-12 h-12">
                    <img
                      src="/hanacharacter/hanacharacter8.png"
                      alt="적금 캐릭터"
                      className="w-full h-full object-contain"
                    />
                  </div>

                  <div className="flex items-center gap-3 mb-4">
                    <div className="w-10 h-10 bg-gradient-to-br from-blue-500 to-indigo-600 rounded-2xl flex items-center justify-center">
                            <IconComponent className="w-5 h-5 text-white" />
                          </div>
                          <div>
                      <h3 className="text-lg font-bold text-gray-900">
                              {goal.goalName}
                            </h3>
                      <p className="text-sm text-gray-600">
                              {goal.participants.length}명 참여
                      </p>
                          </div>
                        </div>

                  <div className="space-y-3 mb-4">
                    <div className="flex justify-between items-center">
                      <span className="text-sm text-gray-600">목표 금액</span>
                      <span className="font-semibold text-gray-900">
                        {new Intl.NumberFormat("ko-KR").format(
                          goal.targetAmount
                        )}
                        원
                      </span>
                    </div>
                    <div className="flex justify-between items-center">
                      <span className="text-sm text-gray-600">현재 잔액</span>
                      <span className="font-semibold text-blue-600">
                        {new Intl.NumberFormat("ko-KR").format(
                          goal.currentAmount || 0
                        )}
                        원
                      </span>
                    </div>
                    <div className="flex justify-between items-center">
                      <span className="text-sm text-gray-600">월 납입액</span>
                      <span className="font-semibold text-gray-900">
                        {new Intl.NumberFormat("ko-KR").format(
                          goal.monthlyAmount
                        )}
                        원
                      </span>
                    </div>
                    <div className="flex justify-between items-center">
                      <span className="text-sm text-gray-600">남은 기간</span>
                      <span className="font-semibold text-gray-900">
                        {goal.remainingMonths}개월
                            </span>
                    </div>
                  </div>

                  {/* 진행률 바 */}
                  <div className="mb-4">
                    <div className="flex justify-between items-center mb-2">
                      <span className="text-sm text-gray-600">달성률</span>
                            <span className="text-sm font-bold text-blue-600">
                        {achievementRate}%
                            </span>
                          </div>
                    <div className="w-full bg-blue-200 rounded-full h-2">
                            <div
                        className="bg-gradient-to-r from-blue-500 to-indigo-600 h-2 rounded-full transition-all duration-500"
                              style={{
                                width: `${Math.min(achievementRate, 100)}%`,
                              }}
                            ></div>
                          </div>
                        </div>

                  {/* 참여자 목록 */}
                  <div className="flex items-center gap-2">
                    <Users className="w-4 h-4 text-gray-500" />
                    <div className="flex -space-x-2">
                      {goal.participants
                        .slice(0, 3)
                        .map((participant, index) => (
                          <div
                            key={index}
                            className="w-6 h-6 bg-gradient-to-br from-blue-400 to-indigo-500 rounded-full flex items-center justify-center text-xs text-white font-medium border-2 border-white"
                          >
                            {participant.charAt(0)}
                          </div>
                        ))}
                      {goal.participants.length > 3 && (
                        <div className="w-6 h-6 bg-gray-300 rounded-full flex items-center justify-center text-xs text-gray-600 font-medium border-2 border-white">
                          +{goal.participants.length - 3}
                        </div>
                      )}
                    </div>
                  </div>
    </div>
  );
            })}
          </div>

          {/* 추가 적금 가입 카드 */}
          <div
            className="bg-gradient-to-br from-gray-50 to-slate-50 p-8 rounded-3xl border border-gray-200 hover:shadow-xl transition-all duration-300 cursor-pointer text-center"
            onClick={() => onStartTogetherFlow && onStartTogetherFlow()}
          >
            <div className="w-16 h-16 bg-gradient-to-br from-blue-100 to-indigo-100 rounded-full flex items-center justify-center mx-auto mb-4">
              <Plus className="w-8 h-8 text-blue-600" />
            </div>
            <h3 className="text-xl font-bold text-gray-900 mb-2">
              새로운 적금 목표 만들기
            </h3>
            <p className="text-gray-600 mb-4">
              가족과 함께 새로운 목표를 설정해보세요
            </p>
            <div className="inline-flex items-center gap-2 text-blue-600 font-medium">
              <span>시작하기</span>
              <ArrowRight className="w-4 h-4" />
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
