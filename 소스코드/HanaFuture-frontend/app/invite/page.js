"use client";

import { useEffect, useState } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import { Users, Heart, Shield, CheckCircle, X } from "lucide-react";
import { useAuth } from "../../contexts/AuthContext";

export default function GroupAccountInvitePage() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const { user, isLoggedIn } = useAuth();

  const [inviteData, setInviteData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [accepting, setAccepting] = useState(false);
  const [acceptSuccess, setAcceptSuccess] = useState(false);
  const [acceptError, setAcceptError] = useState(null);

  const groupId = searchParams.get("group");
  const token = searchParams.get("token");
  const inviteeName = searchParams.get("name"); // 초대받은 사람 이름

  // Base64 디코딩 함수
  const atob = (str) => {
    return Buffer.from(str, "base64").toString("utf-8");
  };

  useEffect(() => {
    if (groupId && token) {
      fetchInviteData();
    } else {
      setError("유효하지 않은 초대 링크입니다.");
      setLoading(false);
    }
  }, [groupId, token]);

  const fetchInviteData = async () => {
    try {
      setLoading(true);

      // API 없이 URL 파라미터로 초대 정보 구성
      let groupName = "알 수 없는 모임";

      try {
        // 토큰에서 모임 이름 추출 시도
        const decodedToken = atob(token);
        if (decodedToken.includes("_")) {
          // 새로운 형식: "이름_타임스탬프"
          groupName = decodedToken.split("_")[0] + "님의 모임";
        } else {
          // 기존 형식: 모임 이름
          groupName = decodedToken;
        }
      } catch (e) {
        console.warn("토큰 디코딩 실패, 기본값 사용");
      }

      // Backend API로 실제 모임통장 정보 조회
      try {
        const groupResponse = await fetch(
          `http://localhost:8080/api/group-accounts/${groupId}`
        );
        if (groupResponse.ok) {
          const groupData = await groupResponse.json();
          setInviteData({
            id: groupId,
            name: groupData.data?.name || groupName,
            purpose: groupData.data?.purpose || "가족비",
            memberCount: groupData.data?.memberCount || 1,
            createdAt: groupData.data?.createdAt || new Date().toISOString(),
            creator: {
              name: groupData.data?.primaryUserName || "모임통장 생성자",
              email: "",
            },
            inviteeName: inviteeName || "친구",
          });
          return;
        }
      } catch (apiError) {
        console.warn("API 조회 실패, 기본값 사용:", apiError);
      }

      // API 실패 시 기본값 설정
      setInviteData({
        id: groupId,
        name: groupName,
        purpose: "가족비",
        memberCount: 1,
        createdAt: new Date().toISOString(),
        creator: {
          name: "모임통장 생성자",
          email: "",
        },
        inviteeName: inviteeName || "친구",
      });
    } catch (err) {
      console.error("초대 정보 구성 실패:", err);
      setError("초대 정보를 불러오는데 실패했습니다.");
    } finally {
      setLoading(false);
    }
  };

  const handleAcceptInvite = async () => {
    if (!isLoggedIn) {
      // 로그인 페이지로 리다이렉트 (초대 정보 유지)
      router.push(`/auth?redirect=/invite?group=${groupId}&token=${token}`);
      return;
    }

    try {
      setAccepting(true);

      // 초대 수락 API 호출
      const response = await fetch(
        `/api/group-accounts/${groupId}/accept-invite`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${user.token}`, // JWT 토큰 추가
          },
          body: JSON.stringify({
            userId: user.id,
            token: token,
          }),
        }
      );

      if (response.ok) {
        console.log("모임통장 초대 수락 성공");
        setAcceptSuccess(true);
        setAcceptError(null);

        // 3초 후 대시보드로 이동
        setTimeout(() => {
          router.push("/dashboard?tab=manage-group-account");
        }, 3000);
      } else {
        let errorMessage = "초대 수락에 실패했습니다.";
        try {
          const errorText = await response.text();
          errorMessage = errorText || errorMessage;
        } catch (textError) {
          console.warn("에러 텍스트 파싱 실패:", textError);
        }
        setAcceptError(errorMessage);
        setAcceptSuccess(false);
      }
    } catch (err) {
      console.error("초대 수락 실패:", err);
      setAcceptError("초대 수락에 실패했습니다. 다시 시도해주세요.");
      setAcceptSuccess(false);
    } finally {
      setAccepting(false);
    }
  };

  const handleDeclineInvite = () => {
    router.push("/");
  };

  if (acceptSuccess) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-green-50 to-emerald-100">
        <div className="container mx-auto px-4 py-12">
          <div className="max-w-2xl mx-auto">
            <div className="bg-white rounded-2xl shadow-xl p-8 text-center">
              <div className="w-20 h-20 bg-green-500 rounded-full flex items-center justify-center mx-auto mb-6">
                <CheckCircle className="w-10 h-10 text-white" />
              </div>
              <h1 className="text-3xl font-bold text-gray-900 mb-4">
                🎉 참여 완료!
              </h1>
              <p className="text-lg text-gray-600 mb-6">
                <strong>{inviteData?.name}</strong> 모임통장에 성공적으로
                참여했습니다!
              </p>
              <div className="bg-green-50 rounded-xl p-4 mb-6">
                <p className="text-green-800">
                  이제 모임통장의 멤버로서 함께 목표를 달성해보세요.
                </p>
              </div>
              <p className="text-sm text-gray-500 mb-4">
                3초 후 모임통장 관리 페이지로 이동합니다...
              </p>
              <button
                onClick={() =>
                  router.push("/dashboard?tab=manage-group-account")
                }
                className="px-6 py-3 bg-green-500 text-white rounded-xl hover:bg-green-600 font-medium transition-colors"
              >
                지금 바로 이동하기
              </button>
            </div>
          </div>
        </div>
      </div>
    );
  }

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
          <p className="text-gray-600">초대 정보를 확인하는 중...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="max-w-md mx-auto text-center p-8">
          <div className="w-16 h-16 bg-red-100 rounded-full flex items-center justify-center mx-auto mb-4">
            <X className="w-8 h-8 text-red-600" />
          </div>
          <h1 className="text-xl font-bold text-gray-900 mb-2">
            초대 링크 오류
          </h1>
          <p className="text-gray-600 mb-6">{error}</p>
          <button
            onClick={() => router.push("/")}
            className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
          >
            홈으로 돌아가기
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100">
      <div className="container mx-auto px-4 py-12">
        <div className="max-w-2xl mx-auto">
          {/* 헤더 */}
          <div className="text-center mb-8">
            <div className="w-20 h-20 bg-gradient-to-br from-blue-500 to-indigo-600 rounded-full flex items-center justify-center mx-auto mb-6 shadow-lg">
              <Users className="w-10 h-10 text-white" />
            </div>
            <h1 className="text-3xl font-bold text-gray-900 mb-2">
              {inviteData?.inviteeName}님, 모임통장 초대
            </h1>
            <p className="text-gray-600">
              {inviteData?.inviteeName}님을 함께 목표를 달성할 모임통장에
              초대되었습니다
            </p>
          </div>

          {/* 모임통장 정보 카드 */}
          <div className="bg-white rounded-2xl shadow-xl p-8 mb-8">
            {inviteData && typeof inviteData === 'object' && !Array.isArray(inviteData) ? (
              <>
                <div className="text-center mb-6">
                  <h2 className="text-2xl font-bold text-gray-900 mb-2">
                    {typeof inviteData.name === "string"
                      ? inviteData.name
                      : "모임통장"}
                  </h2>
                  <div className="flex items-center justify-center gap-4 text-sm text-gray-600">
                    <span>
                      목적:{" "}
                      {typeof inviteData.purpose === "string"
                        ? inviteData.purpose
                        : "가족비"}
                    </span>
                    <span>•</span>
                    <span>멤버: {inviteData.memberCount || 1}명</span>
                  </div>
                </div>
              </>
            ) : (
              <div className="text-center mb-6">
                <h2 className="text-2xl font-bold text-gray-900 mb-2">모임통장 초대</h2>
                <p className="text-gray-600">초대 정보를 불러오는 중...</p>
              </div>
            )}

            {/* 초대자 정보 */}
            {inviteData?.creator && (
              <div className="bg-gray-50 rounded-xl p-4 mb-6">
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 bg-blue-500 rounded-full flex items-center justify-center">
                    <Users className="w-5 h-5 text-white" />
                  </div>
                  <div>
                    <p className="font-medium text-gray-900">
                      {inviteData.creator.name || "모임통장 생성자"}님이 초대했습니다
                    </p>
                    {inviteData.creator.email && (
                      <p className="text-sm text-gray-600">
                        {inviteData.creator.email}
                      </p>
                    )}
                  </div>
                </div>
              </div>
            )}

            {/* 혜택 안내 */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-8">
              <div className="text-center p-4">
                <div className="w-12 h-12 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-3">
                  <CheckCircle className="w-6 h-6 text-green-600" />
                </div>
                <h3 className="font-semibold text-gray-900 mb-1">
                  투명한 관리
                </h3>
                <p className="text-sm text-gray-600">
                  모든 거래내역을 실시간으로 확인
                </p>
              </div>
              <div className="text-center p-4">
                <div className="w-12 h-12 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-3">
                  <Heart className="w-6 h-6 text-blue-600" />
                </div>
                <h3 className="font-semibold text-gray-900 mb-1">함께 저축</h3>
                <p className="text-sm text-gray-600">
                  목표 달성을 위한 협력적 저축
                </p>
              </div>
              <div className="text-center p-4">
                <div className="w-12 h-12 bg-purple-100 rounded-full flex items-center justify-center mx-auto mb-3">
                  <Shield className="w-6 h-6 text-purple-600" />
                </div>
                <h3 className="font-semibold text-gray-900 mb-1">
                  안전한 관리
                </h3>
                <p className="text-sm text-gray-600">
                  하나은행의 안전한 시스템
                </p>
              </div>
            </div>

            {/* 버튼 영역 */}
            <div className="flex gap-4">
              <button
                onClick={handleDeclineInvite}
                className="flex-1 py-3 border-2 border-gray-300 text-gray-700 rounded-xl hover:bg-gray-50 font-medium transition-colors"
              >
                거절하기
              </button>
              <button
                onClick={handleAcceptInvite}
                disabled={accepting}
                className="flex-1 py-3 bg-gradient-to-r from-blue-500 to-indigo-600 text-white rounded-xl hover:from-blue-600 hover:to-indigo-700 disabled:from-gray-300 disabled:to-gray-400 disabled:cursor-not-allowed font-medium transition-colors flex items-center justify-center gap-2"
              >
                {accepting ? (
                  <>
                    <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                    처리 중...
                  </>
                ) : (
                  <>{!isLoggedIn ? "로그인 후 " : ""}참여하기</>
                )}
              </button>
            </div>

            {/* 에러 메시지 표시 */}
            {acceptError && (
              <div className="mt-4 p-4 bg-red-50 border border-red-200 rounded-xl">
                <div className="flex items-center gap-2">
                  <X className="w-5 h-5 text-red-500" />
                  <p className="text-red-800 font-medium">오류 발생</p>
                </div>
                <p className="text-red-700 mt-1 text-sm">{acceptError}</p>
                <button
                  onClick={() => setAcceptError(null)}
                  className="mt-2 text-red-600 hover:text-red-800 text-sm underline"
                >
                  다시 시도하기
                </button>
              </div>
            )}

            {!isLoggedIn && (
              <p className="text-center text-sm text-gray-500 mt-4">
                모임통장 참여를 위해 로그인이 필요합니다
              </p>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
