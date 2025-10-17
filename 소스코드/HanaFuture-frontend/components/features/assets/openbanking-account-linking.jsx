"use client";

import { useState, useEffect } from "react";
import {
  Plus,
  CreditCard,
  Shield,
  CheckCircle,
  ArrowRight,
  Banknote,
  TrendingUp,
  AlertCircle,
} from "lucide-react";

export function OpenBankingAccountLinking({ onAccountLinkRequest }) {
  const [connectedBanks, setConnectedBanks] = useState([]);
  const [loading, setLoading] = useState(false);

  // 연결된 은행 목록 (실제로는 API에서 가져옴)
  useEffect(() => {
    fetchConnectedAccounts();
  }, []);

  const fetchConnectedAccounts = async () => {
    try {
      setLoading(true);
      // const response = await accountAPI.getConnectedAccounts();

      setConnectedBanks([]);
    } catch (error) {
      console.error("계좌 목록 조회 실패:", error);
      setConnectedBanks([]);
    } finally {
      setLoading(false);
    }
  };

  const handleBankConnect = (selectedBanks) => {
    setLoading(true);
    // 실제로는 API 호출
    setTimeout(() => {
      // 선택된 은행들을 연결된 목록에 추가
      const newBanks = selectedBanks.map((bank) => ({
        bankCode: bank.code,
        bankName: bank.name,
        logo: bank.logo,
        accounts: [], // 계좌 조회 후 채워짐
        connectedAt: new Date().toISOString().split("T")[0],
      }));

      setConnectedBanks((prev) => [...prev, ...newBanks]);
      setShowBankSelection(false);
      setLoading(false);
    }, 2000);
  };

  const handleDisconnectBank = (bankCode) => {
    setConnectedBanks((prev) =>
      prev.filter((bank) => bank.bankCode !== bankCode)
    );
  };

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat("ko-KR").format(amount);
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* 페이지 헤더 */}
      <div className="text-center mb-8">
        <div className="inline-flex items-center gap-3 mb-4">
          <img
            src="/hanacharacter/hanacharacter1.png"
            alt="계좌 연결 캐릭터"
            className="w-16 h-16 object-contain"
          />
          <h1 className="text-3xl font-black text-gray-900">내 계좌 연결</h1>
        </div>
        <p className="text-lg text-gray-600 mb-6">
          오픈뱅킹을 통해 안전하게 계좌를 연결하고 관리하세요
        </p>
        <button
          onClick={() => {
            if (onAccountLinkRequest) {
              onAccountLinkRequest();
            }
          }}
          className="inline-flex items-center gap-2 bg-gradient-to-r from-emerald-600 to-teal-600 text-white px-6 py-3 rounded-2xl hover:from-emerald-700 hover:to-teal-700 transition-all duration-300 font-semibold shadow-lg"
        >
          <Plus className="w-5 h-5" />
          계좌 추가하기
        </button>
      </div>

      {/* 연결된 은행 목록 */}
      {connectedBanks.length === 0 ? (
        <div className="bg-white rounded-3xl shadow-lg border border-gray-100 p-12 text-center">
          <div className="w-24 h-24 bg-gradient-to-br from-emerald-50 to-teal-50 rounded-full flex items-center justify-center mx-auto mb-6">
            <CreditCard className="w-12 h-12 text-emerald-600" />
          </div>
          <h3 className="text-2xl font-bold text-gray-900 mb-4">
            연결된 계좌가 없습니다
          </h3>
          <p className="text-gray-600 mb-8 max-w-md mx-auto">
            먼저 오픈뱅킹을 통해 계좌를 연결해주세요
          </p>
          <button
            onClick={() => {
              if (onAccountLinkRequest) {
                onAccountLinkRequest();
              }
            }}
            className="bg-gradient-to-r from-emerald-600 to-teal-600 text-white px-8 py-4 rounded-2xl hover:from-emerald-700 hover:to-teal-700 transition-all duration-300 font-bold text-lg shadow-lg"
          >
            계좌 연결하기
          </button>
        </div>
      ) : (
        <div className="grid gap-6">
          {connectedBanks.map((bank) => (
            <div
              key={bank.bankCode}
              className="bg-white rounded-3xl shadow-lg border border-gray-100 p-6 hover:shadow-xl transition-all duration-300"
            >
              <div className="flex items-center justify-between mb-4">
                <div className="flex items-center gap-4">
                  <div className="w-12 h-12 bg-gray-50 rounded-lg flex items-center justify-center">
                    <img
                      src={bank.logo}
                      alt={bank.bankName}
                      className="w-8 h-8 object-contain"
                    />
                  </div>
                  <div>
                    <h3 className="font-semibold text-gray-900">
                      {bank.bankName}
                    </h3>
                    <p className="text-sm text-gray-500">
                      연결일: {bank.connectedAt}
                    </p>
                  </div>
                </div>
                <div className="flex items-center gap-2">
                  <div className="flex items-center gap-1 text-emerald-600">
                    <CheckCircle className="w-4 h-4" />
                    <span className="text-sm font-medium">연결됨</span>
                  </div>
                  <button
                    onClick={() => handleDisconnectBank(bank.bankCode)}
                    className="text-gray-400 hover:text-red-500 transition-colors"
                  >
                    연결 해제
                  </button>
                </div>
              </div>

              {/* 계좌 목록 */}
              {bank.accounts.length > 0 ? (
                <div className="space-y-3">
                  {bank.accounts.map((account) => (
                    <div
                      key={account.accountNum}
                      className="flex items-center justify-between p-3 bg-gray-50 rounded-lg"
                    >
                      <div>
                        <p className="font-medium text-gray-900">
                          {account.accountName}
                        </p>
                        <p className="text-sm text-gray-500">
                          {account.accountNum.replace(
                            /(\d{4})\d{4}(\d{4})/,
                            "$1****$2"
                          )}
                        </p>
                      </div>
                      <div className="text-right">
                        <p className="font-semibold text-gray-900">
                          {formatCurrency(account.balance)}원
                        </p>
                        <div className="flex items-center gap-1">
                          <div
                            className={`w-2 h-2 rounded-full ${
                              account.isActive
                                ? "bg-emerald-500"
                                : "bg-gray-400"
                            }`}
                          />
                          <span className="text-xs text-gray-500">
                            {account.isActive ? "활성" : "비활성"}
                          </span>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              ) : (
                <div className="text-center py-4 text-gray-500">
                  <AlertCircle className="w-6 h-6 mx-auto mb-2" />
                  <p className="text-sm">계좌 정보를 불러오는 중...</p>
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
