"use client";

import { useState, useEffect } from "react";
import {
  Users,
  Plus,
  CreditCard,
  ArrowRight,
  Settings,
  Trash2,
} from "lucide-react";
import { ModernGroupAccountFlow } from "./modern-group-account-flow";
import { DepositWithdrawModal } from "./deposit-withdraw-modal";
import { TransactionHistoryModal } from "./transaction-history-modal";
import { MemberManagementModal } from "./member-management-modal";
import { GroupAccountHero } from "./group-account-hero";

// API Base URL - 환경변수 사용
const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080/api";
import { useAuth } from "../../../contexts/AuthContext";

export function GroupAccountManagement() {
  const [showOpeningFlow, setShowOpeningFlow] = useState(false);
  const [groupAccounts, setGroupAccounts] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [showDepositModal, setShowDepositModal] = useState(false);
  const [showWithdrawModal, setShowWithdrawModal] = useState(false);
  const [showTransactionModal, setShowTransactionModal] = useState(false);
  const [showMemberModal, setShowMemberModal] = useState(false);
  const [selectedAccount, setSelectedAccount] = useState(null);
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);
  const [accountToDelete, setAccountToDelete] = useState(null);
  const { user } = useAuth();

  // 백엔드에서 모임통장 목록 가져오기
  const fetchGroupAccounts = async () => {
    try {
      setIsLoading(true);

      const response = await fetch(
        `${API_BASE_URL}/group-accounts/user/${user?.id || 1}`,
        {
          headers: {
            "Content-Type": "application/json",
            ...(user?.token && { Authorization: `Bearer ${user.token}` }),
          },
        }
      );

      if (response.ok) {
        const result = await response.json();

        setGroupAccounts(result.data || []);
      } else {
        const errorText = await response.text();
        console.error("모임통장 목록 조회 실패:", response.status, errorText);
        setGroupAccounts([]);
      }
    } catch (error) {
      console.error("모임통장 목록 조회 중 오류:", error);
      setGroupAccounts([]);
    } finally {
      setIsLoading(false);
    }
  };

  // 컴포넌트 마운트 시 데이터 로드
  useEffect(() => {
    if (user?.id) {
      fetchGroupAccounts();
    }
  }, [user?.id]);

  const handleCreateGroupAccount = () => {
    setShowOpeningFlow(true);
  };

  const handleOpeningComplete = async (groupAccountData) => {
    setShowOpeningFlow(false);
    // 백엔드에서 최신 데이터 다시 가져오기
    await fetchGroupAccounts();
  };

  const handleCancel = () => {
    setShowOpeningFlow(false);
  };

  const handleDeposit = (account) => {
    setSelectedAccount(account);
    setShowDepositModal(true);
  };

  const handleWithdraw = (account) => {
    setSelectedAccount(account);
    setShowWithdrawModal(true);
  };

  const handleTransactionSuccess = async () => {
    // 거래 성공 후 목록 새로고침
    await fetchGroupAccounts();
  };

  const handleShowTransactions = (account) => {
    setSelectedAccount(account);
    setShowTransactionModal(true);
  };

  const handleShowMembers = (account) => {
    setSelectedAccount(account);
    setShowMemberModal(true);
  };

  const handleDeleteClick = (account) => {
    setAccountToDelete(account);
    setShowDeleteConfirm(true);
  };

  const handleDeleteConfirm = async () => {
    if (!accountToDelete) return;

    try {
      const response = await fetch(
        `${API_BASE_URL}/group-accounts/${accountToDelete.id}?userId=${user?.id}`,
        {
          method: "DELETE",
          headers: {
            "Content-Type": "application/json",
            ...(user?.token && { Authorization: `Bearer ${user.token}` }),
          },
        }
      );

      if (response.ok) {
        console.log("모임통장 삭제 성공");
        await fetchGroupAccounts(); // 목록 새로고침
        setShowDeleteConfirm(false);
        setAccountToDelete(null);
      } else {
        const errorData = await response.json();
        alert(errorData.message || "모임통장 삭제에 실패했습니다.");
      }
    } catch (error) {
      console.error("모임통장 삭제 중 오류:", error);
      alert("모임통장 삭제 중 오류가 발생했습니다.");
    }
  };

  const handleDeleteCancel = () => {
    setShowDeleteConfirm(false);
    setAccountToDelete(null);
  };

  if (showOpeningFlow) {
    return (
      <ModernGroupAccountFlow
        onComplete={handleOpeningComplete}
        onCancel={handleCancel}
      />
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* 히어로 섹션 */}
      <GroupAccountHero />

      <div className="max-w-6xl mx-auto px-4 py-8">
        {/* 모임통장 목록 */}
        {isLoading ? (
          <div className="bg-white rounded-2xl shadow-lg border border-gray-100 p-8 text-center">
            <div className="flex items-center justify-center space-x-2">
              <div className="w-4 h-4 bg-emerald-500 rounded-full animate-bounce"></div>
              <div
                className="w-4 h-4 bg-emerald-500 rounded-full animate-bounce"
                style={{ animationDelay: "0.1s" }}
              ></div>
              <div
                className="w-4 h-4 bg-emerald-500 rounded-full animate-bounce"
                style={{ animationDelay: "0.2s" }}
              ></div>
            </div>
            <p className="text-gray-600 mt-4">모임통장 목록을 불러오는 중...</p>
          </div>
        ) : groupAccounts.length === 0 ? (
          <div className="bg-white rounded-2xl shadow-lg border border-gray-100 overflow-hidden">
            {/* 3D 일러스트레이션 영역 */}
            <div className="bg-gradient-to-br from-emerald-50 to-blue-50 px-8 py-12">
              <div className="text-center">
                <div
                  className="relative mx-auto mb-8"
                  style={{ width: "200px", height: "150px" }}
                >
                  {/* 3D 스타일 모임통장 일러스트 */}
                  <div className="absolute inset-0 bg-gradient-to-br from-emerald-400 to-emerald-600 rounded-2xl transform rotate-3 shadow-2xl opacity-80"></div>
                  <div className="absolute inset-0 bg-gradient-to-br from-blue-400 to-blue-600 rounded-2xl transform -rotate-3 shadow-2xl opacity-60 translate-x-4"></div>
                  <div className="absolute inset-0 bg-gradient-to-br from-purple-400 to-purple-600 rounded-2xl transform rotate-1 shadow-2xl opacity-40 translate-x-8"></div>
                  <div className="relative bg-white rounded-2xl shadow-2xl p-6 flex items-center justify-center border-4 border-emerald-200">
                    <Users className="w-12 h-12 text-emerald-600" />
                  </div>

                  {/* 플로팅 아이콘들 */}
                  <div className="absolute -top-4 -right-4 w-8 h-8 bg-yellow-400 rounded-full flex items-center justify-center shadow-lg animate-bounce">
                    <span className="text-sm">💰</span>
                  </div>
                  <div className="absolute -bottom-2 -left-4 w-8 h-8 bg-pink-400 rounded-full flex items-center justify-center shadow-lg animate-pulse">
                    <span className="text-sm">🎯</span>
                  </div>
                  <div className="absolute top-1/2 -right-8 w-6 h-6 bg-green-400 rounded-full flex items-center justify-center shadow-lg animate-ping">
                    <span className="text-xs">✨</span>
                  </div>
                </div>

                <h3 className="text-2xl font-bold text-gray-900 mb-4">
                  함께 만드는 첫 번째 모임통장 🚀
                </h3>
                <p className="text-gray-600 mb-8 max-w-lg mx-auto text-lg leading-relaxed">
                  가족, 친구들과 함께 목표를 정하고 체계적으로 저축해보세요.
                  <br />
                  <span className="text-emerald-600 font-semibold">
                    자동이체
                  </span>
                  로 편리하게,
                  <span className="text-blue-600 font-semibold">
                    {" "}
                    실시간 알림
                  </span>
                  으로 투명하게!
                </p>

                {/* 특징 카드들 */}
                <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
                  <div className="bg-white rounded-xl p-4 shadow-md border border-gray-100">
                    <div className="w-12 h-12 bg-emerald-100 rounded-xl flex items-center justify-center mx-auto mb-3">
                      <span className="text-2xl">🏠</span>
                    </div>
                    <h4 className="font-semibold text-gray-900 mb-1">
                      내집마련
                    </h4>
                    <p className="text-sm text-gray-600">우리 집 마련의 꿈</p>
                  </div>
                  <div className="bg-white rounded-xl p-4 shadow-md border border-gray-100">
                    <div className="w-12 h-12 bg-blue-100 rounded-xl flex items-center justify-center mx-auto mb-3">
                      <span className="text-2xl">✈️</span>
                    </div>
                    <h4 className="font-semibold text-gray-900 mb-1">
                      여행자금
                    </h4>
                    <p className="text-sm text-gray-600">특별한 추억 만들기</p>
                  </div>
                  <div className="bg-white rounded-xl p-4 shadow-md border border-gray-100">
                    <div className="w-12 h-12 bg-purple-100 rounded-xl flex items-center justify-center mx-auto mb-3">
                      <span className="text-2xl">💒</span>
                    </div>
                    <h4 className="font-semibold text-gray-900 mb-1">
                      결혼자금
                    </h4>
                    <p className="text-sm text-gray-600">행복한 출발 준비</p>
                  </div>
                </div>
              </div>
            </div>

            {/* 액션 영역 */}
            <div className="px-8 py-6 bg-white">
              <button
                onClick={handleCreateGroupAccount}
                className="w-full bg-gradient-to-r from-emerald-500 to-emerald-600 text-white px-8 py-4 rounded-xl hover:from-emerald-600 hover:to-emerald-700 transition-all shadow-lg hover:shadow-xl font-semibold text-lg flex items-center justify-center gap-3"
              >
                <Plus className="w-5 h-5" />
                지금 바로 모임통장 만들기
                <span className="text-emerald-200">→</span>
              </button>
            </div>
          </div>
        ) : (
          <div className="grid gap-8">
            {groupAccounts.map((account) => (
              <div
                key={account.id}
                className="bg-white rounded-2xl shadow-lg border border-gray-100 overflow-hidden hover:shadow-xl transition-all duration-300 hover:-translate-y-1"
              >
                {/* 계좌 헤더 */}
                <div className="bg-gradient-to-r from-emerald-500 to-emerald-600 p-6 text-white">
                  <div className="flex items-center justify-between mb-4">
                    <div className="flex items-center gap-4">
                      <div className="flex items-center justify-center">
                        <img
                          src="/hana3dIcon/hanaIcon3d_3_47.png"
                          alt="통장 아이콘"
                          className="w-20 h-20 object-contain"
                        />
                      </div>
                      <div>
                        <h3 className="text-2xl font-bold mb-1">
                          {account.name}
                        </h3>
                        <p className="text-emerald-100 text-sm">
                          {account.accountName}
                        </p>
                        <p className="text-emerald-200 text-xs font-medium mt-1">
                          {account.accountNumber}
                        </p>
                        <p className="text-emerald-100 text-xs mt-2">
                          모임원:{" "}
                          {account.memberNames
                            ? account.memberNames.join(", ")
                            : `${account.activeMembersCount || 0}명`}
                        </p>
                      </div>
                    </div>
                    <div className="flex items-center gap-2">
                      <button
                        onClick={() => handleDeleteClick(account)}
                        className="p-2 text-white/80 hover:text-white hover:bg-red-500/30 rounded-lg transition-colors"
                        title="모임통장 삭제"
                      >
                        <Trash2 className="w-5 h-5" />
                      </button>
                      <button
                        onClick={() => handleShowMembers(account)}
                        className="p-2 text-white/80 hover:text-white hover:bg-white/20 rounded-lg transition-colors"
                        title="설정"
                      >
                        <Settings className="w-5 h-5" />
                      </button>
                      <button
                        onClick={() => handleShowTransactions(account)}
                        className="flex items-center gap-2 bg-white/20 hover:bg-white/30 text-white px-4 py-2 rounded-lg transition-colors backdrop-blur-sm"
                      >
                        <span className="font-medium">관리</span>
                        <ArrowRight className="w-4 h-4" />
                      </button>
                    </div>
                  </div>

                  {/* 잔액 정보 */}
                  <div className="mb-6 text-center">
                    <p className="text-emerald-100 text-sm mb-2">현재 잔액</p>
                    <p className="text-4xl font-bold text-white">
                      {new Intl.NumberFormat("ko-KR").format(
                        account.currentBalance || 0
                      )}
                      원
                    </p>
                  </div>
                </div>

                {/* 계좌 상세 정보 */}
                <div className="p-6">
                  {/* 액션 버튼들 */}
                  <div className="flex gap-3">
                    <button
                      onClick={() => handleDeposit(account)}
                      className="flex-1 bg-white hover:bg-gray-50 text-gray-700 py-3 px-4 rounded-xl font-semibold transition-colors border border-gray-200"
                    >
                      채우기
                    </button>
                    <button
                      onClick={() => handleShowTransactions(account)}
                      className="flex-1 bg-white hover:bg-gray-50 text-gray-700 py-3 px-4 rounded-xl font-semibold transition-colors border border-gray-200"
                    >
                      거래내역
                    </button>
                    <button
                      onClick={() => handleWithdraw(account)}
                      className="flex-1 bg-white hover:bg-gray-50 text-gray-700 py-3 px-4 rounded-xl font-semibold transition-colors border border-gray-200"
                    >
                      보내기
                    </button>
                  </div>

                  {/* 멤버 관리 버튼 */}
                  <div className="mt-3">
                    <button
                      onClick={() => handleShowMembers(account)}
                      className="w-full bg-white hover:bg-gray-50 text-gray-700 py-3 px-4 rounded-xl font-semibold transition-colors border border-gray-200 flex items-center justify-center gap-2"
                    >
                      <Users className="w-4 h-4" />
                      멤버 관리
                    </button>
                  </div>

                  {/* 연결된 계좌 정보 */}
                  {account.withdrawalAccount && (
                    <div className="mt-6 p-4 bg-gray-50 rounded-xl border border-gray-200">
                      <div className="flex items-center gap-3">
                        <CreditCard className="w-5 h-5 text-gray-500" />
                        <div>
                          <span className="text-sm text-gray-600">
                            자동이체 계좌
                          </span>
                          <p className="font-medium text-gray-900">
                            {account.withdrawalAccount.bankName}{" "}
                            {account.withdrawalAccount.accountNum?.replace(
                              /(\d{4})\d{4}(\d{4})/,
                              "$1****$2"
                            )}
                          </p>
                        </div>
                        <div className="ml-auto">
                          <span className="inline-flex items-center px-3 py-1 rounded-full text-xs font-medium bg-emerald-100 text-emerald-800">
                            매월 {new Date().getDate()}일 자동이체
                          </span>
                        </div>
                      </div>
                    </div>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}

        {/* 입금 모달 */}
        <DepositWithdrawModal
          isOpen={showDepositModal}
          onClose={() => setShowDepositModal(false)}
          groupAccount={selectedAccount}
          type="deposit"
          onSuccess={handleTransactionSuccess}
        />
        {/* 출금 모달 */}
        <DepositWithdrawModal
          isOpen={showWithdrawModal}
          onClose={() => setShowWithdrawModal(false)}
          groupAccount={selectedAccount}
          type="withdraw"
          onSuccess={handleTransactionSuccess}
        />
        {/* 거래내역 모달 */}
        <TransactionHistoryModal
          isOpen={showTransactionModal}
          onClose={() => setShowTransactionModal(false)}
          groupAccount={selectedAccount}
        />
        {/* 멤버 관리 모달 */}
        <MemberManagementModal
          isOpen={showMemberModal}
          onClose={() => setShowMemberModal(false)}
          groupAccount={selectedAccount}
        />
        {/* 삭제 확인 모달 */}
        {showDeleteConfirm && accountToDelete && (
          <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
            <div className="bg-white rounded-2xl shadow-2xl max-w-md w-full p-6 animate-scale-in">
              <div className="text-center mb-6">
                <div className="w-16 h-16 bg-red-100 rounded-full flex items-center justify-center mx-auto mb-4">
                  <Trash2 className="w-8 h-8 text-red-600" />
                </div>
                <h3 className="text-xl font-bold text-gray-900 mb-2">
                  모임통장을 삭제하시겠습니까?
                </h3>
                <p className="text-gray-600 mb-4">
                  <strong className="text-red-600">
                    {accountToDelete.accountName}
                  </strong>
                  <br />
                  계좌번호: {accountToDelete.accountNumber}
                </p>
                <div className="bg-yellow-50 border border-yellow-200 rounded-xl p-4 text-left">
                  <p className="text-sm text-yellow-800">
                    <strong>주의사항</strong>
                  </p>
                  <ul className="text-sm text-yellow-700 mt-2 space-y-1 list-disc list-inside">
                    <li>생성자만 삭제할 수 있습니다</li>
                    <li>삭제 후 복구할 수 없습니다</li>
                    <li>잔액이 남아있어도 삭제됩니다</li>
                  </ul>
                </div>
              </div>

              <div className="flex gap-3">
                <button
                  onClick={handleDeleteCancel}
                  className="flex-1 py-3 border-2 border-gray-300 text-gray-700 rounded-xl hover:bg-gray-50 font-medium transition-colors"
                >
                  취소
                </button>
                <button
                  onClick={handleDeleteConfirm}
                  className="flex-1 py-3 bg-red-600 text-white rounded-xl hover:bg-red-700 font-medium transition-colors flex items-center justify-center gap-2"
                >
                  <Trash2 className="w-5 h-5" />
                  삭제하기
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
