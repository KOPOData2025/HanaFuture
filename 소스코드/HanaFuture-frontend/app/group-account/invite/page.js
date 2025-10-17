"use client";

import { useEffect, useState } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import { CheckCircle, Loader2, XCircle, Users, Heart } from "lucide-react";

export default function GroupAccountInvitePage() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const [status, setStatus] = useState("loading"); // loading, success, error
  const [groupAccountInfo, setGroupAccountInfo] = useState(null);
  const [errorMessage, setErrorMessage] = useState("");

  useEffect(() => {
    const token = searchParams.get("token");

    if (!token) {
      setStatus("error");
      setErrorMessage("초대 링크가 유효하지 않습니다.");
      return;
    }

    try {
      // Base64 디코딩
      const decodedData = JSON.parse(atob(token));
      const { groupAccountId, invitedMembers } = decodedData;

      if (!groupAccountId) {
        setStatus("error");
        setErrorMessage("모임통장 정보를 찾을 수 없습니다.");
        return;
      }

      // 모임통장 정보 설정
      setGroupAccountInfo({
        groupAccountId,
        invitedMembersCount: invitedMembers?.length || 0,
      });
      setStatus("success");
    } catch (error) {
      console.error("토큰 파싱 오류:", error);
      setStatus("error");
      setErrorMessage("초대 링크가 올바르지 않습니다.");
    }
  }, [searchParams]);

  const handleAcceptInvite = () => {
    // 로그인이 필요한 경우 홈 페이지로 이동
    const currentUser = localStorage.getItem("user");

    if (!currentUser) {
      // 초대 정보를 localStorage에 저장
      const token = searchParams.get("token");
      localStorage.setItem("pendingGroupInvite", token);

      // 홈 페이지로 리다이렉트 (로그인 화면이 표시됨)
      alert("모임통장 초대를 수락하려면 먼저 로그인해주세요.");
      router.push("/");
      return;
    }

    // 이미 로그인된 경우 초대 수락 처리
    acceptInviteAPI();
  };

  const acceptInviteAPI = async () => {
    try {
      const user = JSON.parse(localStorage.getItem("user"));
      const token = searchParams.get("token");

      const response = await fetch(
        `http://localhost:8080/api/group-accounts/${groupAccountInfo.groupAccountId}/accept-invite`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${user.token}`,
          },
          body: JSON.stringify({
            userId: user.id, // userId 추가
            token: token, // inviteToken이 아닌 token으로 변경
          }),
        }
      );

      if (response.ok) {
        alert("모임통장 초대를 수락했습니다!");
        router.push("/mypage"); // 마이페이지로 이동
      } else {
        const error = await response.json();
        alert(`초대 수락 실패: ${error.message || "알 수 없는 오류"}`);
      }
    } catch (error) {
      console.error("초대 수락 오류:", error);
      alert("초대 수락 중 오류가 발생했습니다.");
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-emerald-50 via-teal-50 to-cyan-50 flex items-center justify-center p-4">
      <div className="max-w-md w-full bg-white rounded-2xl shadow-2xl p-8">
        {status === "loading" && (
          <div className="text-center">
            <Loader2 className="w-16 h-16 text-emerald-500 animate-spin mx-auto mb-4" />
            <h2 className="text-2xl font-bold text-gray-900 mb-2">
              초대 정보를 확인하는 중...
            </h2>
            <p className="text-gray-600">잠시만 기다려주세요.</p>
          </div>
        )}

        {status === "error" && (
          <div className="text-center">
            <div className="w-16 h-16 bg-red-100 rounded-full flex items-center justify-center mx-auto mb-4">
              <XCircle className="w-10 h-10 text-red-500" />
            </div>
            <h2 className="text-2xl font-bold text-gray-900 mb-2">
              초대 링크 오류
            </h2>
            <p className="text-gray-600 mb-6">{errorMessage}</p>
            <button
              onClick={() => router.push("/")}
              className="w-full py-3 bg-gray-500 hover:bg-gray-600 text-white rounded-xl font-semibold transition-colors"
            >
              홈으로 돌아가기
            </button>
          </div>
        )}

        {status === "success" && (
          <div className="text-center">
            <div className="w-20 h-20 bg-gradient-to-br from-emerald-400 to-teal-500 rounded-full flex items-center justify-center mx-auto mb-6 shadow-lg">
              <Users className="w-10 h-10 text-white" />
            </div>
            <h2 className="text-3xl font-bold text-gray-900 mb-2">
              모임통장 초대
            </h2>
            <p className="text-gray-600 mb-8">
              모임통장에 초대되셨습니다!
              <br />
              함께 목표를 달성해보세요 🎉
            </p>

            <div className="bg-emerald-50 border border-emerald-200 rounded-xl p-6 mb-8">
              <div className="flex items-center justify-center gap-2 mb-4">
                <Heart className="w-5 h-5 text-emerald-600" />
                <span className="font-semibold text-gray-900">초대 정보</span>
              </div>
              <div className="space-y-2 text-sm text-gray-700">
                <div className="flex justify-between">
                  <span>모임통장 ID:</span>
                  <span className="font-semibold">
                    #{groupAccountInfo?.groupAccountId}
                  </span>
                </div>
                {groupAccountInfo?.invitedMembersCount > 0 && (
                  <div className="flex justify-between">
                    <span>초대된 멤버:</span>
                    <span className="font-semibold">
                      {groupAccountInfo.invitedMembersCount}명
                    </span>
                  </div>
                )}
              </div>
            </div>

            <div className="space-y-3">
              <button
                onClick={handleAcceptInvite}
                className="w-full py-4 bg-gradient-to-r from-emerald-500 to-teal-500 hover:from-emerald-600 hover:to-teal-600 text-white rounded-xl font-bold text-lg transition-all shadow-lg hover:shadow-xl flex items-center justify-center gap-2"
              >
                <CheckCircle className="w-6 h-6" />
                초대 수락하기
              </button>

              <button
                onClick={() => router.push("/")}
                className="w-full py-3 bg-gray-100 hover:bg-gray-200 text-gray-700 rounded-xl font-semibold transition-colors"
              >
                나중에 하기
              </button>
            </div>

            <div className="mt-6 p-4 bg-blue-50 border border-blue-200 rounded-xl">
              <p className="text-xs text-blue-800">
                💡 초대를 수락하려면 하나퓨처 회원가입 또는 로그인이 필요합니다.
              </p>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
