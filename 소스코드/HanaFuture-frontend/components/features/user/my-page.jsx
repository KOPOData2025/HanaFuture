"use client";

import React, { useState, useEffect } from "react";
import {
  ArrowLeft,
  Users,
  CreditCard,
  PiggyBank,
  Gift,
  Settings,
  ChevronRight,
  Plus,
  TrendingUp,
  Wallet,
  Bell,
  User,
  Eye,
  EyeOff,
  UserPlus,
  Heart,
  Phone,
  Calendar,
  CheckCircle2,
  XCircle,
  Clock,
  MessageCircle,
  Trash2,
} from "lucide-react";
import { BookmarksPage } from "../welfare/bookmarks-page";
import { useAuth } from "../../../contexts/AuthContext";

// API Base URL - 환경변수 사용
const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080/api";

export function MyPage({ onBack }) {
  const [activeTab, setActiveTab] = useState("overview");
  const [showBalance, setShowBalance] = useState(true);
  const { user } = useAuth();

  // 실제 백엔드 데이터
  const [myData, setMyData] = useState({
    groupAccounts: [],
    savings: [],
    childCards: [],
    bookmarks: 0,
    totalAssets: 0,
  });
  const [loading, setLoading] = useState(true);

  // 컴포넌트 마운트 시 데이터 로드
  useEffect(() => {
    if (user?.id) {
      loadMyData();
    }
  }, [user]);

  // 데이터 새로고침 함수 (외부에서 호출 가능)
  const refreshData = () => {
    loadMyData();
  };

  // 컴포넌트가 포커스될 때마다 데이터 새로고침
  useEffect(() => {
    const handleFocus = () => {
      loadMyData();
    };

    const handleSavingsCreated = () => {
      console.log("함께 적금 생성 완료 - 마이페이지 데이터 새로고침");
      loadMyData();
    };

    window.addEventListener("focus", handleFocus);
    window.addEventListener("savingsCreated", handleSavingsCreated);

    return () => {
      window.removeEventListener("focus", handleFocus);
      window.removeEventListener("savingsCreated", handleSavingsCreated);
    };
  }, [user]);

  const loadMyData = async () => {
    try {
      setLoading(true);

      console.log("마이페이지 데이터 로드 시작 - 사용자 ID:", user.id);

      // 병렬로 모든 데이터 조회
      // 토큰 다중 소스 확인 및 형식 검증
      const rawToken =
        user?.token ||
        localStorage.getItem("auth_token") ||
        localStorage.getItem("accessToken") ||
        localStorage.getItem("token");

      // JWT 형식 검증
      const isValidJwtFormat =
        rawToken &&
        typeof rawToken === "string" &&
        rawToken.split(".").length === 3;
      const validToken = isValidJwtFormat ? rawToken : null;

      console.log("🔑 마이페이지 토큰 확인:");
      console.log("- user.token:", user?.token?.substring(0, 50) + "...");
      console.log("- localStorage token:", rawToken?.substring(0, 50) + "...");
      console.log("- 형식 검증:", isValidJwtFormat);
      console.log("- 최종 토큰:", validToken?.substring(0, 50) + "...");

      // 인증 헤더 설정
      const authHeaders = {
        "Content-Type": "application/json",
        ...(validToken && { Authorization: `Bearer ${validToken}` }),
      };

      const [
        groupAccountsRes,
        savingsRes,
        childCardsRes,
        bookmarksRes,
        connectedAccountsRes,
      ] = await Promise.allSettled([
        fetch(`${API_BASE_URL}/group-accounts/user/${user.id}`, {
          headers: authHeaders,
        }),
        fetch(`${API_BASE_URL}/savings/user/${user.id}`, {
          headers: authHeaders,
        }),
        fetch(`${API_BASE_URL}/child-cards/user/${user.id}`, {
          headers: authHeaders,
        }),
        fetch(`${API_BASE_URL}/bookmarks/user/${user.id}/count`, {
          headers: authHeaders,
        }),
        fetch(`${API_BASE_URL}/user/bank-accounts/user/${user.id}`, {
          headers: authHeaders,
        }),
      ]);

      const newData = { ...myData };
      let totalAssets = 0;

      // 모임통장 데이터 처리
      if (
        groupAccountsRes.status === "fulfilled" &&
        groupAccountsRes.value.ok
      ) {
        const groupData = await groupAccountsRes.value.json();
        console.log("모임통장 API 응답:", groupData);
        newData.groupAccounts = groupData.data || [];
        totalAssets += newData.groupAccounts.reduce(
          (sum, account) => sum + (account.balance || 0),
          0
        );
      } else {
        console.error("모임통장 API 실패:", groupAccountsRes);
      }

      // 적금 데이터 처리
      if (savingsRes.status === "fulfilled" && savingsRes.value.ok) {
        const savingsData = await savingsRes.value.json();
        newData.savings = savingsData.data || [];
        totalAssets += newData.savings.reduce(
          (sum, saving) => sum + (saving.currentAmount || 0),
          0
        );
      }

      // 아이카드 데이터 처리
      if (childCardsRes.status === "fulfilled" && childCardsRes.value.ok) {
        const childCardsData = await childCardsRes.value.json();
        newData.childCards = childCardsData.data || [];
        totalAssets += newData.childCards.reduce(
          (sum, card) => sum + (card.balance || 0),
          0
        );
      }

      // 즐겨찾기 개수 처리
      if (bookmarksRes.status === "fulfilled" && bookmarksRes.value.ok) {
        const bookmarksData = await bookmarksRes.value.json();
        newData.bookmarks = bookmarksData.data || 0;
      }

      // 연결된 계좌 데이터 처리 (오픈뱅킹 연동 계좌만)
      if (
        connectedAccountsRes.status === "fulfilled" &&
        connectedAccountsRes.value.ok
      ) {
        const accountsData = await connectedAccountsRes.value.json();
        newData.connectedAccounts = accountsData.data || [];
        console.log("오픈뱅킹 연동 계좌 조회 결과:", newData.connectedAccounts);

        // 연결된 계좌 잔액도 총 자산에 포함
        totalAssets += newData.connectedAccounts.reduce(
          (sum, account) => sum + (account.balance || 0),
          0
        );
      } else {
        console.error("오픈뱅킹 연동 계좌 API 실패:", connectedAccountsRes);
        newData.connectedAccounts = [];
      }

      newData.totalAssets = totalAssets;
      setMyData(newData);

      console.log("마이페이지 데이터 로드 완료:", newData);
    } catch (error) {
      console.error("마이페이지 데이터 로드 실패:", error);
    } finally {
      setLoading(false);
    }
  };

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat("ko-KR").format(amount);
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString("ko-KR");
  };

  const tabs = [
    { id: "overview", label: "전체", icon: TrendingUp },
    { id: "group-accounts", label: "모임통장", icon: Users },
    { id: "savings", label: "함께 적금", icon: PiggyBank },
    { id: "child-cards", label: "아이카드", icon: CreditCard },
    { id: "family", label: "내 가족", icon: Heart },
    { id: "bookmarks", label: "즐겨찾기", icon: Gift },
    { id: "connected-accounts", label: "연결된 계좌", icon: Wallet },
  ];

  if (loading) {
    return (
      <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="flex items-center justify-center min-h-[400px]">
          <div className="animate-spin rounded-full h-12 w-12 border-2 border-gray-200 border-t-emerald-500"></div>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* 뒤로가기 버튼 */}
      <div className="mb-6">
        <button
          onClick={onBack}
          className="flex items-center gap-2 text-gray-600 hover:text-gray-800 transition-colors"
        >
          <ArrowLeft className="h-5 w-5" />
          뒤로
        </button>
      </div>

      {/* 헤더 */}
      <div className="mb-8">
        <div className="flex items-center space-x-4 mb-6">
          <div className="w-16 h-16 bg-gradient-to-br from-emerald-400 to-teal-600 rounded-full flex items-center justify-center">
            <User className="w-8 h-8 text-white" />
          </div>
          <div>
            <h1 className="text-2xl font-bold text-gray-900">
              {user?.name || "사용자"}님의 마이페이지
            </h1>
            <p className="text-gray-600">하나Future 서비스 이용 현황</p>
          </div>
        </div>

        {/* 자산 요약 */}
        <div className="bg-gradient-to-br from-emerald-50 to-teal-50 rounded-2xl p-6 border border-emerald-200">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-lg font-semibold text-gray-900">총 자산</h2>
            <button
              onClick={() => setShowBalance(!showBalance)}
              className="p-2 text-gray-500 hover:text-gray-700 transition-colors"
            >
              {showBalance ? (
                <EyeOff className="w-5 h-5" />
              ) : (
                <Eye className="w-5 h-5" />
              )}
            </button>
          </div>
          <div className="text-3xl font-bold text-emerald-600 mb-2">
            {showBalance
              ? `${formatCurrency(myData.totalAssets)}원`
              : "••••••••원"}
          </div>
          <p className="text-sm text-gray-600">
            모임통장, 함께 적금, 아이카드 잔액 포함
          </p>
        </div>
      </div>

      {/* 탭 네비게이션 */}
      <div className="mb-8">
        <div className="flex space-x-1 bg-gray-100 rounded-xl p-1">
          {tabs.map((tab) => {
            const Icon = tab.icon;
            return (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id)}
                className={`flex items-center space-x-2 px-4 py-2 rounded-lg font-medium transition-all ${
                  activeTab === tab.id
                    ? "bg-white text-emerald-600 shadow-sm"
                    : "text-gray-600 hover:text-gray-900"
                }`}
              >
                <Icon className="w-4 h-4" />
                <span>{tab.label}</span>
              </button>
            );
          })}
        </div>
      </div>

      {/* 탭 콘텐츠 */}
      <div className="space-y-6">
        {/* 전체 탭 */}
        {activeTab === "overview" && (
          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
            {/* 모임통장 카드 */}
            <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
              <div className="flex items-center justify-between mb-4">
                <div className="flex items-center space-x-3">
                  <div className="w-10 h-10 bg-emerald-100 rounded-lg flex items-center justify-center">
                    <Users className="w-5 h-5 text-emerald-600" />
                  </div>
                  <div>
                    <h3 className="font-semibold text-gray-900">모임통장</h3>
                    <p className="text-sm text-gray-600">
                      {myData.groupAccounts.length}개
                    </p>
                  </div>
                </div>
                <ChevronRight className="w-5 h-5 text-gray-400" />
              </div>
              <div className="text-2xl font-bold text-gray-900 mb-1">
                {formatCurrency(
                  myData.groupAccounts.reduce(
                    (sum, acc) => sum + acc.balance,
                    0
                  )
                )}
                원
              </div>
              <p className="text-sm text-gray-600">총 잔액</p>
            </div>

            {/* 함께 적금 카드 */}
            <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
              <div className="flex items-center justify-between mb-4">
                <div className="flex items-center space-x-3">
                  <div className="w-10 h-10 bg-blue-100 rounded-lg flex items-center justify-center">
                    <PiggyBank className="w-5 h-5 text-blue-600" />
                  </div>
                  <div>
                    <h3 className="font-semibold text-gray-900">함께 적금</h3>
                    <p className="text-sm text-gray-600">
                      {myData.savings.length}개
                    </p>
                  </div>
                </div>
                <ChevronRight className="w-5 h-5 text-gray-400" />
              </div>
              <div className="text-2xl font-bold text-gray-900 mb-1">
                {formatCurrency(
                  myData.savings.reduce(
                    (sum, sav) => sum + sav.currentAmount,
                    0
                  )
                )}
                원
              </div>
              <p className="text-sm text-gray-600">현재 적립액</p>
            </div>

            {/* 아이카드 카드 */}
            <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
              <div className="flex items-center justify-between mb-4">
                <div className="flex items-center space-x-3">
                  <div className="w-10 h-10 bg-purple-100 rounded-lg flex items-center justify-center">
                    <CreditCard className="w-5 h-5 text-purple-600" />
                  </div>
                  <div>
                    <h3 className="font-semibold text-gray-900">아이카드</h3>
                    <p className="text-sm text-gray-600">
                      {myData.childCards.length}개
                    </p>
                  </div>
                </div>
                <ChevronRight className="w-5 h-5 text-gray-400" />
              </div>
              <div className="text-2xl font-bold text-gray-900 mb-1">
                {formatCurrency(
                  myData.childCards.reduce((sum, card) => sum + card.balance, 0)
                )}
                원
              </div>
              <p className="text-sm text-gray-600">총 잔액</p>
            </div>
          </div>
        )}

        {/* 모임통장 탭 */}
        {activeTab === "group-accounts" && (
          <div className="space-y-4">
            {myData.groupAccounts.map((account) => (
              <div
                key={account.id}
                className="bg-white rounded-xl shadow-sm border border-gray-200 p-6"
              >
                <div className="flex items-center justify-between mb-4">
                  <div>
                    <h3 className="text-lg font-semibold text-gray-900">
                      {account.name}
                    </h3>
                    <p className="text-sm text-gray-600">
                      {account.accountNumber}
                    </p>
                  </div>
                  <div className="text-right">
                    <div className="text-xl font-bold text-gray-900">
                      {formatCurrency(account.balance)}원
                    </div>
                    <p className="text-sm text-gray-600">{account.purpose}</p>
                  </div>
                </div>
                <div className="flex items-center justify-between text-sm text-gray-600">
                  <span>모임원 {account.memberCount}명</span>
                  <span>개설일 {formatDate(account.createdAt)}</span>
                </div>
              </div>
            ))}
          </div>
        )}

        {/* 함께 적금 탭 */}
        {activeTab === "savings" && (
          <div className="space-y-4">
            {myData.savings.map((saving) => (
              <div
                key={saving.id}
                className="bg-white rounded-xl shadow-sm border border-gray-200 p-6"
              >
                <div className="flex items-center justify-between mb-4">
                  <div>
                    <h3 className="text-lg font-semibold text-gray-900">
                      {saving.name}
                    </h3>
                    <p className="text-sm text-gray-600">
                      연 {saving.interestRate}%
                    </p>
                  </div>
                  <div className="text-right">
                    <div className="text-xl font-bold text-gray-900">
                      {formatCurrency(saving.currentAmount)}원
                    </div>
                    <p className="text-sm text-gray-600">
                      목표 {formatCurrency(saving.targetAmount)}원
                    </p>
                  </div>
                </div>
                <div className="mb-4">
                  <div className="flex justify-between text-sm text-gray-600 mb-2">
                    <span>진행률</span>
                    <span>
                      {Math.round(
                        (saving.currentAmount / saving.targetAmount) * 100
                      )}
                      %
                    </span>
                  </div>
                  <div className="w-full bg-gray-200 rounded-full h-2">
                    <div
                      className="bg-blue-600 h-2 rounded-full"
                      style={{
                        width: `${Math.min(
                          (saving.currentAmount / saving.targetAmount) * 100,
                          100
                        )}%`,
                      }}
                    ></div>
                  </div>
                </div>
                <div className="flex items-center justify-between text-sm text-gray-600">
                  <span>
                    월 적립액 {formatCurrency(saving.monthlyAmount)}원
                  </span>
                  <span>만기일 {formatDate(saving.maturityDate)}</span>
                </div>
              </div>
            ))}
          </div>
        )}

        {/* 아이카드 탭 */}
        {activeTab === "child-cards" && (
          <div className="space-y-4">
            {myData.childCards.map((card) => (
              <div
                key={card.id}
                className="bg-white rounded-xl shadow-sm border border-gray-200 p-6"
              >
                <div className="flex items-center justify-between mb-4">
                  <div>
                    <h3 className="text-lg font-semibold text-gray-900">
                      {card.childName} 카드
                    </h3>
                    <p className="text-sm text-gray-600">{card.cardNumber}</p>
                  </div>
                  <div className="text-right">
                    <div className="text-xl font-bold text-gray-900">
                      {formatCurrency(card.balance)}원
                    </div>
                    <p className="text-sm text-gray-600">잔액</p>
                  </div>
                </div>
                <div className="flex items-center justify-between text-sm text-gray-600">
                  <span>월 용돈 {formatCurrency(card.monthlyAllowance)}원</span>
                  <span>최근 사용 {formatDate(card.lastTransaction)}</span>
                </div>
              </div>
            ))}
          </div>
        )}

        {/* 내 가족 탭 */}
        {activeTab === "family" && <FamilyManagementTab user={user} />}

        {/* 즐겨찾기 탭 */}
        {activeTab === "bookmarks" && (
          <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
            <BookmarksPage />
          </div>
        )}

        {/* 연결된 계좌 탭 */}
        {activeTab === "connected-accounts" && (
          <div className="space-y-4">
            {myData.connectedAccounts && myData.connectedAccounts.length > 0 ? (
              myData.connectedAccounts.map((account, index) => (
                <div
                  key={account.id || index}
                  className="bg-white rounded-xl shadow-sm border border-gray-200 p-6"
                >
                  <div className="flex items-center justify-between mb-4">
                    <div className="flex items-center space-x-3">
                      <div className="w-12 h-12 bg-green-100 rounded-full flex items-center justify-center p-2">
                        <img
                          src="/bank-logos/HanaLogo.png"
                          alt="하나은행"
                          className="w-full h-full object-contain"
                        />
                      </div>
                      <div>
                        <h3 className="text-lg font-semibold text-gray-900">
                          {account.accountName}
                        </h3>
                        <p className="text-sm text-gray-600">
                          {account.accountNumber}
                        </p>
                        <p className="text-xs text-gray-500">
                          {account.bankName} •{" "}
                          {account.accountType === "1"
                            ? "입출금"
                            : account.accountType}
                        </p>
                      </div>
                    </div>
                    <div className="text-right">
                      <div className="text-xl font-bold text-gray-900">
                        {(account.balance || 0).toLocaleString()}원
                      </div>
                      <p className="text-sm text-gray-600">오픈뱅킹 연동</p>
                    </div>
                  </div>
                  {account.accountType === "1" && (
                    <div className="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-emerald-100 text-emerald-800">
                      출금 가능
                    </div>
                  )}
                </div>
              ))
            ) : (
              <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-8 text-center">
                <Wallet className="w-12 h-12 text-gray-400 mx-auto mb-4" />
                <h3 className="text-lg font-semibold text-gray-900 mb-2">
                  연결된 계좌가 없습니다
                </h3>
                <p className="text-gray-600 mb-4">
                  오픈뱅킹을 통해 계좌를 연결해보세요
                </p>
                <button className="bg-emerald-600 text-white px-6 py-3 rounded-xl hover:bg-emerald-700 transition-colors">
                  계좌 연결하기
                </button>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
}

// 가족 관리 탭 컴포넌트
function FamilyManagementTab({ user }) {
  const [familyMembers, setFamilyMembers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showAddModal, setShowAddModal] = useState(false);
  const [newMember, setNewMember] = useState({
    name: "",
    phoneNumber: "",
  });

  useEffect(() => {
    loadFamilyMembers();
  }, []);

  const loadFamilyMembers = async () => {
    try {
      setLoading(true);
      const response = await fetch(
        `${API_BASE_URL}/family/members?userId=${user.id}`,
        {
          headers: {
            Authorization: `Bearer ${user.token}`,
          },
        }
      );

      if (response.ok) {
        const result = await response.json();
        setFamilyMembers(result.data || []);
      }
    } catch (error) {
      console.error("가족 목록 조회 실패:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleAddFamily = async () => {
    try {
      const requestData = {
        name: newMember.name,
        phoneNumber: newMember.phoneNumber,
      };

      console.log("📤 가족 추가 요청:", requestData);

      const response = await fetch(
        `${API_BASE_URL}/family/members?userId=${user.id}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${user.token}`,
          },
          body: JSON.stringify(requestData),
        }
      );

      if (response.ok) {
        setShowAddModal(false);
        setNewMember({
          name: "",
          phoneNumber: "",
        });
        loadFamilyMembers();
        console.log("가족 추가 완료!");
      } else {
        const errorData = await response.json();
        console.error("가족 추가 실패:", errorData);
        alert(`가족 추가 실패: ${errorData.message || "알 수 없는 오류"}`);
      }
    } catch (error) {
      console.error("가족 추가 실패:", error);
      alert("가족 추가 중 오류가 발생했습니다.");
    }
  };

  const getRelationTypeLabel = (type) => {
    const labels = {
      PARENT: "부모",
      CHILD: "자녀",
      SPOUSE: "배우자",
      SIBLING: "형제자매",
      GRANDPARENT: "조부모",
      GRANDCHILD: "손자녀",
      OTHER: "기타",
    };
    return labels[type] || type;
  };

  const getStatusBadge = (status) => {
    const badges = {
      ACCEPTED: {
        icon: CheckCircle2,
        text: "연결됨",
        className: "bg-green-100 text-green-700",
      },
      PENDING: {
        icon: Clock,
        text: "대기중",
        className: "bg-yellow-100 text-yellow-700",
      },
      NOT_INVITED: {
        icon: XCircle,
        text: "미연결",
        className: "bg-gray-100 text-gray-700",
      },
    };
    return badges[status] || badges.NOT_INVITED;
  };

  const sendKakaoInvite = (member) => {
    // 알림 확인 페이지로 이동하는 링크 (로그인 후 알림 탭으로)
    const inviteUrl = `${
      window.location.origin
    }/?tab=notifications&from=${encodeURIComponent(user.name)}`;

    if (window.Kakao && window.Kakao.Share) {
      try {
        window.Kakao.Share.sendDefault({
          objectType: "feed",
          content: {
            title: `💚 ${user.name}님이 가족으로 초대합니다!`,
            description: `${user.name}님이 ${member.name}님을 가족으로 등록했습니다.\n\n하나퓨처에 로그인하여 초대를 확인해주세요! 🏦`,
            imageUrl:
              "https://via.placeholder.com/300x200/4F46E5/FFFFFF?text=HanaFuture",
            link: { mobileWebUrl: inviteUrl, webUrl: inviteUrl },
          },
          buttons: [
            {
              title: "초대 확인하기",
              link: { mobileWebUrl: inviteUrl, webUrl: inviteUrl },
            },
          ],
        });
        console.log(`${member.name}님에게 카카오톡 초대 전송 완료 📤`);
      } catch (kakaoError) {
        console.error("카카오톡 공유 실패:", kakaoError);
        navigator.clipboard.writeText(inviteUrl);
        console.log("📋 초대 링크가 클립보드에 복사되었습니다!");
      }
    } else {
      navigator.clipboard.writeText(inviteUrl);
      console.log("📋 초대 링크가 클립보드에 복사되었습니다!");
    }
  };

  const handleDeleteFamily = async (familyMemberId, memberName) => {
    if (!window.confirm(`${memberName}님을 가족 목록에서 삭제하시겠습니까?`)) {
      return;
    }

    try {
      const response = await fetch(
        `http://localhost:8080/api/family/members/${familyMemberId}?userId=${user.id}`,
        {
          method: "DELETE",
          headers: {
            Authorization: `Bearer ${user.token}`,
          },
        }
      );

      if (response.ok) {
        console.log(`${memberName}님 삭제 완료`);
        loadFamilyMembers();
      } else {
        console.error("가족 삭제 실패");
      }
    } catch (error) {
      console.error("가족 삭제 실패:", error);
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <div className="animate-spin rounded-full h-12 w-12 border-2 border-gray-200 border-t-emerald-500"></div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* 헤더 */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold text-gray-900 flex items-center gap-2">
            <Heart className="w-7 h-7 text-red-500" />내 가족
          </h2>
          <p className="text-gray-600 mt-1">
            가족을 추가하고 모임통장이나 함께 적금에 초대하세요
          </p>
        </div>
        <button
          onClick={() => setShowAddModal(true)}
          className="flex items-center gap-2 px-4 py-2 bg-emerald-600 text-white rounded-lg hover:bg-emerald-700 transition-colors"
        >
          <Plus className="w-5 h-5" />
          가족 추가
        </button>
      </div>

      {/* 가족 목록 */}
      {familyMembers.length > 0 ? (
        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-4">
          {familyMembers.map((member) => {
            const statusBadge = getStatusBadge(member.inviteStatus);
            const StatusIcon = statusBadge.icon;

            return (
              <div
                key={member.id}
                className="bg-white rounded-xl shadow-sm border border-gray-200 p-6 hover:shadow-md transition-shadow"
              >
                <div className="flex items-start justify-between mb-4">
                  <div className="flex items-center gap-3">
                    <div className="w-12 h-12 bg-gradient-to-br from-pink-400 to-red-500 rounded-full flex items-center justify-center">
                      <User className="w-6 h-6 text-white" />
                    </div>
                    <div>
                      <h3 className="font-semibold text-gray-900">
                        {member.name}
                      </h3>
                      <p className="text-sm text-gray-600">가족</p>
                    </div>
                  </div>
                  <div className="flex items-center gap-2">
                    <span
                      className={`px-2 py-1 rounded-full text-xs font-medium flex items-center gap-1 ${statusBadge.className}`}
                    >
                      <StatusIcon className="w-3 h-3" />
                      {statusBadge.text}
                    </span>
                    <button
                      onClick={() => handleDeleteFamily(member.id, member.name)}
                      className="p-2 text-red-500 hover:bg-red-50 rounded-lg transition-colors"
                      title="삭제"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                  </div>
                </div>

                <div className="space-y-2 text-sm">
                  {member.phoneNumber && (
                    <div className="flex items-center gap-2 text-gray-600">
                      <Phone className="w-4 h-4" />
                      {member.phoneNumber}
                    </div>
                  )}
                  {member.birthDate && (
                    <div className="flex items-center gap-2 text-gray-600">
                      <Calendar className="w-4 h-4" />
                      {new Date(member.birthDate).toLocaleDateString("ko-KR")}
                    </div>
                  )}
                  {member.memo && (
                    <p className="text-gray-600 mt-2 text-xs">
                      메모: {member.memo}
                    </p>
                  )}
                </div>

                {/* 카카오톡 초대 버튼 (미연결 또는 대기중인 경우) */}
                {(member.inviteStatus === "NOT_INVITED" ||
                  member.inviteStatus === "PENDING") && (
                  <button
                    onClick={() => sendKakaoInvite(member)}
                    className="mt-4 w-full px-4 py-2 bg-yellow-400 hover:bg-yellow-500 text-gray-900 rounded-lg transition-colors flex items-center justify-center gap-2 font-medium"
                  >
                    <MessageCircle className="w-4 h-4" />
                    카카오톡으로 초대하기
                  </button>
                )}
              </div>
            );
          })}
        </div>
      ) : (
        <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-12 text-center">
          <Heart className="w-16 h-16 text-gray-300 mx-auto mb-4" />
          <h3 className="text-lg font-semibold text-gray-900 mb-2">
            등록된 가족이 없습니다
          </h3>
          <p className="text-gray-600 mb-6">
            가족을 추가하고 금융 서비스를 함께 이용해보세요
          </p>
          <button
            onClick={() => setShowAddModal(true)}
            className="inline-flex items-center gap-2 px-6 py-3 bg-emerald-600 text-white rounded-lg hover:bg-emerald-700 transition-colors"
          >
            <Plus className="w-5 h-5" />첫 가족 추가하기
          </button>
        </div>
      )}

      {/* 가족 추가 모달 */}
      {showAddModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl max-w-md w-full p-6">
            <h3 className="text-xl font-bold text-gray-900 mb-6">가족 추가</h3>

            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  이름 *
                </label>
                <input
                  type="text"
                  value={newMember.name}
                  onChange={(e) =>
                    setNewMember({ ...newMember, name: e.target.value })
                  }
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500"
                  placeholder="이름을 입력하세요"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  전화번호 *
                </label>
                <input
                  type="tel"
                  value={newMember.phoneNumber}
                  onChange={(e) =>
                    setNewMember({ ...newMember, phoneNumber: e.target.value })
                  }
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500"
                  placeholder="010-1234-5678"
                />
              </div>
            </div>

            <div className="flex gap-3 mt-6">
              <button
                onClick={() => setShowAddModal(false)}
                className="flex-1 px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors"
              >
                취소
              </button>
              <button
                onClick={handleAddFamily}
                className="flex-1 px-4 py-2 bg-emerald-600 text-white rounded-lg hover:bg-emerald-700 transition-colors"
                disabled={!newMember.name || !newMember.phoneNumber}
              >
                추가
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
