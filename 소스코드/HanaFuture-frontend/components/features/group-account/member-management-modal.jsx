"use client";

import { useState, useEffect } from "react";
import {
  X,
  Users,
  Crown,
  UserPlus,
  Mail,
  Phone,
  Calendar,
  MoreVertical,
  Check,
  Clock,
  XCircle,
} from "lucide-react";
import { useAuth } from "../../../contexts/AuthContext";

export function MemberManagementModal({ isOpen, onClose, groupAccount }) {
  const { user } = useAuth();
  const [members, setMembers] = useState([]);
  const [invites, setInvites] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [activeTab, setActiveTab] = useState("members"); // "members" or "invites"

  useEffect(() => {
    if (isOpen && groupAccount) {
      fetchMembers();
    }
  }, [isOpen, groupAccount]);

  const fetchMembers = async () => {
    try {
      setIsLoading(true);

      // 실제 API 호출
      const response = await fetch(
        `http://localhost:8080/api/group-accounts/${groupAccount.id}/members`,
        {
          headers: {
            Authorization: `Bearer ${user.token}`,
          },
        }
      );

      if (response.ok) {
        const result = await response.json();
        setMembers(result.data.members || []);
        setInvites(result.data.invites || []);

        // 통계 데이터도 설정
        const stats = result.data.stats || {};
        console.log("멤버 데이터 로드 성공:", result.data);
      } else {
        console.error("멤버 데이터 로드 실패:", response.status);
        // 실패시 빈 배열로 설정
        setMembers([]);
        setInvites([]);
      }
    } catch (error) {
      console.error("멤버 목록 조회 중 오류:", error);
      setMembers([]);
    } finally {
      setIsLoading(false);
    }
  };

  // fetchInvites 함수 제거 - fetchMembers에서 초대 목록도 함께 처리

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString("ko-KR", {
      year: "numeric",
      month: "short",
      day: "numeric",
    });
  };

  const formatAmount = (amount) => {
    if (amount == null || isNaN(amount)) {
      return "0";
    }
    return new Intl.NumberFormat("ko-KR").format(amount);
  };

  const getRoleIcon = (role) => {
    if (role === "ADMIN") {
      return <Crown className="w-4 h-4 text-yellow-500" />;
    }
    return <Users className="w-4 h-4 text-gray-400" />;
  };

  const getRoleText = (role) => {
    return role === "ADMIN" ? "관리자" : "멤버";
  };

  const getStatusIcon = (status) => {
    switch (status) {
      case "PENDING":
        return <Clock className="w-4 h-4 text-yellow-500" />;
      case "ACCEPTED":
        return <Check className="w-4 h-4 text-emerald-500" />;
      case "REJECTED":
        return <XCircle className="w-4 h-4 text-red-500" />;
      default:
        return <Clock className="w-4 h-4 text-gray-400" />;
    }
  };

  const getStatusText = (status) => {
    switch (status) {
      case "PENDING":
        return "대기중";
      case "ACCEPTED":
        return "수락됨";
      case "REJECTED":
        return "거절됨";
      default:
        return "알 수 없음";
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case "PENDING":
        return "text-yellow-600 bg-yellow-50";
      case "ACCEPTED":
        return "text-emerald-600 bg-emerald-50";
      case "REJECTED":
        return "text-red-600 bg-red-50";
      default:
        return "text-gray-600 bg-gray-50";
    }
  };

  const handleInviteMember = () => {
    // 초대 링크 생성 후 카카오톡 공유
    shareGroupAccountInvite();
  };

  const handleKakaoInvite = () => {
    // 초대 링크 생성 후 카카오톡 공유
    shareGroupAccountInvite();
  };

  const shareGroupAccountInvite = () => {
    // 초대 링크 생성 (Base64 인코딩된 토큰)
    const inviteToken = btoa(
      JSON.stringify({
        groupAccountId: groupAccount.id,
        timestamp: Date.now(),
      })
    );

    const inviteUrl = `${window.location.origin}/group-account/invite?token=${inviteToken}`;

    // 카카오톡 공유
    if (window.Kakao && window.Kakao.Share) {
      try {
        window.Kakao.Share.sendDefault({
          objectType: "feed",
          content: {
            title: `🏦 하나퓨처 모임통장 초대`,
            description: `"${
              groupAccount?.accountName || groupAccount?.name
            }" 모임통장에 초대합니다!\n\n함께 목표를 달성해보세요! 💪`,
            imageUrl:
              "https://via.placeholder.com/300x200/4F46E5/FFFFFF?text=HanaFuture",
            link: {
              mobileWebUrl: inviteUrl,
              webUrl: inviteUrl,
            },
          },
          buttons: [
            {
              title: "모임통장 참여하기",
              link: {
                mobileWebUrl: inviteUrl,
                webUrl: inviteUrl,
              },
            },
          ],
        });

        console.log("카카오톡 초대 전송 완료");
      } catch (kakaoError) {
        console.error("카카오톡 공유 실패:", kakaoError);
        // 카카오톡 공유 실패 시 링크 복사로 대체
        navigator.clipboard.writeText(inviteUrl);
        alert("초대 링크가 클립보드에 복사되었습니다!");
      }
    } else {
      // 카카오톡 SDK가 없으면 링크 복사
      navigator.clipboard.writeText(inviteUrl);
      alert("초대 링크가 클립보드에 복사되었습니다!");
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-4xl max-h-[90vh] overflow-hidden">
        {/* 헤더 */}
        <div className="flex items-center justify-between p-6 border-b border-gray-200">
          <div>
            <h2 className="text-xl font-bold text-gray-900">멤버 관리</h2>
            <p className="text-gray-600 mt-1">
              {groupAccount?.accountName || groupAccount?.name} • 총{" "}
              {members.length}명
            </p>
          </div>
          <div className="flex items-center gap-3">
            <button
              onClick={handleKakaoInvite}
              className="px-4 py-2 bg-yellow-400 text-gray-800 rounded-lg hover:bg-yellow-500 font-medium transition-colors flex items-center gap-2"
            >
              <UserPlus className="w-4 h-4" />
              카카오톡 초대
            </button>
            <button
              onClick={onClose}
              className="p-2 hover:bg-gray-100 rounded-lg transition-colors"
            >
              <X className="w-5 h-5 text-gray-500" />
            </button>
          </div>
        </div>

        {/* 탭 */}
        <div className="border-b border-gray-200">
          <div className="flex">
            <button
              onClick={() => setActiveTab("members")}
              className={`px-6 py-3 font-medium transition-colors ${
                activeTab === "members"
                  ? "text-emerald-600 border-b-2 border-emerald-600"
                  : "text-gray-600 hover:text-gray-900"
              }`}
            >
              멤버 ({members.length})
            </button>
            <button
              onClick={() => setActiveTab("invites")}
              className={`px-6 py-3 font-medium transition-colors ${
                activeTab === "invites"
                  ? "text-emerald-600 border-b-2 border-emerald-600"
                  : "text-gray-600 hover:text-gray-900"
              }`}
            >
              초대 현황 ({invites.length})
            </button>
          </div>
        </div>

        {/* 콘텐츠 */}
        <div className="flex-1 overflow-y-auto" style={{ maxHeight: "500px" }}>
          {activeTab === "members" && (
            <div className="divide-y divide-gray-200">
              {members.length === 0 ? (
                <div className="text-center py-12">
                  <Users className="w-12 h-12 text-gray-300 mx-auto mb-4" />
                  <h3 className="text-lg font-medium text-gray-900 mb-2">
                    멤버가 없습니다
                  </h3>
                  <p className="text-gray-600">첫 번째 멤버를 초대해보세요!</p>
                </div>
              ) : (
                members.map((member) => (
                  <div
                    key={member.id}
                    className="p-6 hover:bg-gray-50 transition-colors"
                  >
                    <div className="flex items-center justify-between">
                      <div className="flex items-center gap-4">
                        <div className="w-12 h-12 bg-gradient-to-br from-emerald-400 to-emerald-600 rounded-full flex items-center justify-center text-white font-bold">
                          {member.userName?.charAt(0) ||
                            member.name?.charAt(0) ||
                            "?"}
                        </div>
                        <div>
                          <div className="flex items-center gap-2">
                            <p className="font-medium text-gray-900">
                              {member.userName || member.name || "알 수 없음"}
                            </p>
                            {getRoleIcon(member.role)}
                            <span className="text-sm text-gray-600">
                              {getRoleText(member.role)}
                            </span>
                            {member.isCreator && (
                              <span className="px-2 py-1 bg-blue-100 text-blue-700 text-xs rounded-full">
                                생성자
                              </span>
                            )}
                          </div>
                          <div className="flex items-center gap-4 text-sm text-gray-600 mt-1">
                            {(member.userEmail || member.email) && (
                              <div className="flex items-center gap-1">
                                <Mail className="w-3 h-3" />
                                {member.userEmail || member.email}
                              </div>
                            )}
                            {member.phoneNumber && (
                              <div className="flex items-center gap-1">
                                <Phone className="w-3 h-3" />
                                {member.phoneNumber}
                              </div>
                            )}
                            <div className="flex items-center gap-1">
                              <Calendar className="w-3 h-3" />
                              {formatDate(member.joinedAt)} 가입
                            </div>
                          </div>
                        </div>
                      </div>
                      <div className="text-right">
                        <p className="text-lg font-bold text-emerald-600">
                          {formatAmount(member.contributionAmount)}원
                        </p>
                        <p className="text-sm text-gray-600">기여금</p>
                      </div>
                    </div>
                  </div>
                ))
              )}
            </div>
          )}

          {activeTab === "invites" && (
            <div className="divide-y divide-gray-200">
              {invites.length === 0 ? (
                <div className="text-center py-12">
                  <UserPlus className="w-12 h-12 text-gray-300 mx-auto mb-4" />
                  <h3 className="text-lg font-medium text-gray-900 mb-2">
                    초대 내역이 없습니다
                  </h3>
                  <p className="text-gray-600">새로운 멤버를 초대해보세요!</p>
                </div>
              ) : (
                invites.map((invite) => (
                  <div
                    key={invite.id}
                    className="p-6 hover:bg-gray-50 transition-colors"
                  >
                    <div className="flex items-center justify-between">
                      <div className="flex items-center gap-4">
                        <div className="w-12 h-12 bg-gray-200 rounded-full flex items-center justify-center text-gray-600 font-bold">
                          {invite.name?.charAt(0) || "?"}
                        </div>
                        <div>
                          <div className="flex items-center gap-2">
                            <p className="font-medium text-gray-900">
                              {invite.name || "알 수 없음"}
                            </p>
                            <div
                              className={`flex items-center gap-1 px-2 py-1 rounded-full text-xs ${getStatusColor(
                                invite.status
                              )}`}
                            >
                              {getStatusIcon(invite.status)}
                              {getStatusText(invite.status)}
                            </div>
                          </div>
                          <div className="flex items-center gap-4 text-sm text-gray-600 mt-1">
                            <div className="flex items-center gap-1">
                              <Phone className="w-3 h-3" />
                              {invite.phoneNumber || "전화번호 없음"}
                            </div>
                            <div className="flex items-center gap-1">
                              <Calendar className="w-3 h-3" />
                              {formatDate(
                                invite.invitedAt || invite.createdAt
                              )}{" "}
                              초대
                            </div>
                            {invite.status === "PENDING" &&
                              invite.expiresAt && (
                                <span className="text-red-500">
                                  {formatDate(invite.expiresAt)} 만료
                                </span>
                              )}
                            {invite.joinedAt && (
                              <span className="text-emerald-600">
                                {formatDate(invite.joinedAt)} 수락
                              </span>
                            )}
                          </div>
                        </div>
                      </div>
                      <div className="flex items-center gap-2">
                        {invite.status === "PENDING" && (
                          <button
                            onClick={handleKakaoInvite}
                            className="px-3 py-1 text-sm bg-blue-100 text-blue-700 rounded-lg hover:bg-blue-200 transition-colors"
                          >
                            재초대
                          </button>
                        )}
                        <button className="p-2 hover:bg-gray-100 rounded-lg transition-colors">
                          <MoreVertical className="w-4 h-4 text-gray-400" />
                        </button>
                      </div>
                    </div>
                  </div>
                ))
              )}
            </div>
          )}
        </div>

        {/* 통계 요약 */}
        <div className="p-6 border-t border-gray-200 bg-gray-50">
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            <div className="text-center">
              <p className="text-sm text-gray-600">총 멤버</p>
              <p className="text-lg font-bold text-gray-900">
                {members.length}명
              </p>
            </div>
            <div className="text-center">
              <p className="text-sm text-gray-600">대기중 초대</p>
              <p className="text-lg font-bold text-yellow-600">
                {invites.filter((i) => i.status === "PENDING").length}건
              </p>
            </div>
            <div className="text-center">
              <p className="text-sm text-gray-600">총 기여금</p>
              <p className="text-lg font-bold text-emerald-600">
                {formatAmount(
                  members.reduce((sum, m) => sum + m.contributionAmount, 0)
                )}
                원
              </p>
            </div>
            <div className="text-center">
              <p className="text-sm text-gray-600">평균 기여금</p>
              <p className="text-lg font-bold text-blue-600">
                {formatAmount(
                  members.length > 0
                    ? members.reduce(
                        (sum, m) => sum + m.contributionAmount,
                        0
                      ) / members.length
                    : 0
                )}
                원
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
