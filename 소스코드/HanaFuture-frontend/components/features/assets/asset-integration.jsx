"use client";

import React, { useState, useEffect } from "react";
import {
  Wallet,
  Plus,
  RefreshCw,
  Eye,
  EyeOff,
  Settings,
  Trash2,
  AlertCircle,
  CheckCircle,
  Clock,
  TrendingUp,
  TrendingDown,
  CreditCard,
  PiggyBank,
  Building,
  Smartphone,
} from "lucide-react";
import apiClient from "../../../lib/api-client";

export function AssetIntegration() {
  const [assets, setAssets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showConnectModal, setShowConnectModal] = useState(false);
  const [syncing, setSyncing] = useState(false);
  const [selectedAsset, setSelectedAsset] = useState(null);

  useEffect(() => {
    loadAssets();
  }, []);

  const loadAssets = async () => {
    try {
      setLoading(true);
      const response = await apiClient.assets.list();

      if (response.success && response.data) {
        setAssets(response.data);
      } else {
        console.error("자산 목록 로딩 실패:", response.message);
      }
    } catch (error) {
      console.error("자산 로딩 실패:", error);
      // 개발 환경에서는 목 데이터 사용
      if (process.env.NODE_ENV === "development") {
        const mockAssets = [
          {
            _id: "1",
            accountName: "하나은행 주거래 통장",
            nickname: "주거래",
            bankName: "하나은행",
            bankCode: "HANA",
            accountType: "checking",
            balance: {
              current: 2500000,
              available: 2500000,
              currency: "KRW",
              lastUpdated: new Date(),
            },
            connection: {
              status: "connected",
              lastSyncAt: new Date(),
              syncFrequency: "daily",
            },
            settings: {
              isVisible: true,
              includeInTotalAssets: true,
              notifications: {
                balanceAlerts: true,
                lowBalanceThreshold: 100000,
              },
            },
            maskedAccountNumber: "****-****-****-1234",
          },
          {
            _id: "2",
            accountName: "KB국민은행 적금",
            nickname: "내 적금",
            bankName: "KB국민은행",
            bankCode: "KB",
            accountType: "savings",
            balance: {
              current: 15000000,
              available: 15000000,
              currency: "KRW",
              lastUpdated: new Date(Date.now() - 2 * 60 * 60 * 1000), // 2시간 전
            },
            connection: {
              status: "connected",
              lastSyncAt: new Date(Date.now() - 2 * 60 * 60 * 1000),
              syncFrequency: "daily",
            },
            settings: {
              isVisible: true,
              includeInTotalAssets: true,
            },
            maskedAccountNumber: "****-****-****-5678",
          },
          {
            _id: "3",
            accountName: "신한카드",
            nickname: "주카드",
            bankName: "신한카드",
            bankCode: "SHINHAN",
            accountType: "card",
            balance: {
              current: 3500000, // 사용 가능 한도
              available: 3500000,
              currency: "KRW",
              lastUpdated: new Date(),
            },
            card: {
              cardType: "credit",
              creditLimit: 5000000,
              availableCredit: 3500000,
              currentBalance: 1500000,
              paymentDueDate: new Date(Date.now() + 15 * 24 * 60 * 60 * 1000),
            },
            connection: {
              status: "connected",
              lastSyncAt: new Date(),
              syncFrequency: "daily",
            },
            settings: {
              isVisible: true,
              includeInTotalAssets: false, // 카드는 자산에서 제외
            },
            maskedAccountNumber: "****-****-****-9012",
          },
          {
            _id: "4",
            accountName: "우리은행 주택담보대출",
            nickname: "주택대출",
            bankName: "우리은행",
            bankCode: "WOORI",
            accountType: "loan",
            balance: {
              current: -150000000, // 대출은 음수로 표시
              available: 0,
              currency: "KRW",
              lastUpdated: new Date(),
            },
            loan: {
              principalBalance: 150000000,
              interestRate: 3.2,
              monthlyPayment: 800000,
              nextPaymentDate: new Date(Date.now() + 10 * 24 * 60 * 60 * 1000),
              maturityDate: new Date(
                Date.now() + 20 * 365 * 24 * 60 * 60 * 1000
              ),
              loanType: "mortgage",
            },
            connection: {
              status: "connected",
              lastSyncAt: new Date(),
              syncFrequency: "daily",
            },
            settings: {
              isVisible: true,
              includeInTotalAssets: true,
            },
            maskedAccountNumber: "****-****-****-3456",
          },
        ];
        setAssets(mockAssets);
      }
    } finally {
      setLoading(false);
    }
  };

  const syncAllAssets = async () => {
    try {
      setSyncing(true);
      await apiClient.assets.syncAll();
      await loadAssets(); // 동기화 후 목록 새로고침
      await new Promise((resolve) => setTimeout(resolve, 2000));

      // 잔액 업데이트 시뮬레이션
      setAssets((prevAssets) =>
        prevAssets.map((asset) => ({
          ...asset,
          balance: {
            ...asset.balance,
            lastUpdated: new Date(),
          },
          connection: {
            ...asset.connection,
            lastSyncAt: new Date(),
          },
        }))
      );

      alert("모든 자산이 동기화되었습니다!");
    } catch (error) {
      console.error("동기화 실패:", error);
    } finally {
      setSyncing(false);
    }
  };

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat("ko-KR").format(Math.abs(amount));
  };

  const getAccountTypeIcon = (type) => {
    switch (type) {
      case "checking":
        return Wallet;
      case "savings":
        return PiggyBank;
      case "investment":
        return TrendingUp;
      case "loan":
        return Building;
      case "card":
        return CreditCard;
      default:
        return Wallet;
    }
  };

  const getAccountTypeLabel = (type) => {
    const labels = {
      checking: "입출금",
      savings: "적금",
      investment: "투자",
      loan: "대출",
      card: "카드",
      insurance: "보험",
    };
    return labels[type] || type;
  };

  const getConnectionStatusColor = (status) => {
    switch (status) {
      case "connected":
        return "text-green-600 bg-green-100";
      case "error":
        return "text-red-600 bg-red-100";
      case "pending":
        return "text-yellow-600 bg-yellow-100";
      default:
        return "text-gray-600 bg-gray-100";
    }
  };

  const getConnectionStatusIcon = (status) => {
    switch (status) {
      case "connected":
        return CheckCircle;
      case "error":
        return AlertCircle;
      case "pending":
        return Clock;
      default:
        return AlertCircle;
    }
  };

  const totalAssets = assets
    .filter(
      (asset) =>
        asset.settings.includeInTotalAssets && asset.accountType !== "loan"
    )
    .reduce((sum, asset) => sum + asset.balance.current, 0);

  const totalDebt = assets
    .filter((asset) => asset.accountType === "loan")
    .reduce((sum, asset) => sum + Math.abs(asset.balance.current), 0);

  const netWorth = totalAssets - totalDebt;

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* 헤더 */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold text-foreground">자산 연동</h2>
          <p className="text-muted-foreground">
            은행 계좌를 연결하여 실시간으로 자산을 관리하세요
          </p>
        </div>
        <div className="flex items-center gap-3">
          <button
            onClick={syncAllAssets}
            disabled={syncing}
            className="flex items-center gap-2 px-4 py-2 border border-border rounded-xl hover:bg-muted/50 transition-colors disabled:opacity-50"
          >
            <RefreshCw className={`h-4 w-4 ${syncing ? "animate-spin" : ""}`} />
            {syncing ? "동기화 중..." : "전체 동기화"}
          </button>
          <button
            onClick={() => setShowConnectModal(true)}
            className="flex items-center gap-2 px-4 py-2 bg-primary text-primary-foreground rounded-xl hover:bg-primary/90 transition-colors"
          >
            <Plus className="h-4 w-4" />
            계좌 연결
          </button>
        </div>
      </div>

      {/* 자산 요약 */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div className="bg-white dark:bg-gray-800 rounded-xl p-6 border">
          <div className="flex items-center gap-3">
            <div className="p-2 bg-green-100 rounded-lg">
              <TrendingUp className="h-5 w-5 text-green-600" />
            </div>
            <div>
              <p className="text-sm text-muted-foreground">총 자산</p>
              <p className="text-2xl font-bold text-green-600">
                ₩{formatCurrency(totalAssets)}
              </p>
            </div>
          </div>
        </div>

        <div className="bg-white dark:bg-gray-800 rounded-xl p-6 border">
          <div className="flex items-center gap-3">
            <div className="p-2 bg-red-100 rounded-lg">
              <TrendingDown className="h-5 w-5 text-red-600" />
            </div>
            <div>
              <p className="text-sm text-muted-foreground">총 부채</p>
              <p className="text-2xl font-bold text-red-600">
                ₩{formatCurrency(totalDebt)}
              </p>
            </div>
          </div>
        </div>

        <div className="bg-white dark:bg-gray-800 rounded-xl p-6 border">
          <div className="flex items-center gap-3">
            <div className="p-2 bg-blue-100 rounded-lg">
              <Wallet className="h-5 w-5 text-blue-600" />
            </div>
            <div>
              <p className="text-sm text-muted-foreground">순자산</p>
              <p
                className={`text-2xl font-bold ${
                  netWorth >= 0 ? "text-blue-600" : "text-red-600"
                }`}
              >
                ₩{formatCurrency(netWorth)}
              </p>
            </div>
          </div>
        </div>
      </div>

      {/* 자산 목록 */}
      <div className="space-y-4">
        <h3 className="text-lg font-bold">연결된 계좌</h3>
        {assets.map((asset) => (
          <AssetCard
            key={asset._id}
            asset={asset}
            onSelect={setSelectedAsset}
            formatCurrency={formatCurrency}
            getAccountTypeIcon={getAccountTypeIcon}
            getAccountTypeLabel={getAccountTypeLabel}
            getConnectionStatusColor={getConnectionStatusColor}
            getConnectionStatusIcon={getConnectionStatusIcon}
          />
        ))}
      </div>

      {/* 계좌 연결 모달 */}
      {showConnectModal && (
        <ConnectAccountModal
          onClose={() => setShowConnectModal(false)}
          onSuccess={(newAsset) => {
            setAssets([...assets, newAsset]);
            setShowConnectModal(false);
          }}
        />
      )}

      {/* 자산 상세 모달 */}
      {selectedAsset && (
        <AssetDetailModal
          asset={selectedAsset}
          onClose={() => setSelectedAsset(null)}
          onUpdate={(updatedAsset) => {
            setAssets(
              assets.map((a) => (a._id === updatedAsset._id ? updatedAsset : a))
            );
          }}
          formatCurrency={formatCurrency}
        />
      )}
    </div>
  );
}

// 자산 카드 컴포넌트
function AssetCard({
  asset,
  onSelect,
  formatCurrency,
  getAccountTypeIcon,
  getAccountTypeLabel,
  getConnectionStatusColor,
  getConnectionStatusIcon,
}) {
  const Icon = getAccountTypeIcon(asset.accountType);
  const StatusIcon = getConnectionStatusIcon(asset.connection.status);

  const lastSyncTime = new Date(asset.connection.lastSyncAt);
  const timeDiff = Date.now() - lastSyncTime.getTime();
  const hoursAgo = Math.floor(timeDiff / (1000 * 60 * 60));

  return (
    <div
      className="bg-white dark:bg-gray-800 rounded-xl p-6 border hover:shadow-lg transition-all cursor-pointer"
      onClick={() => onSelect(asset)}
    >
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-4">
          <div className="p-3 bg-primary/10 rounded-lg">
            <Icon className="h-6 w-6 text-primary" />
          </div>
          <div>
            <div className="flex items-center gap-2">
              <h3 className="font-bold text-lg">
                {asset.nickname || asset.accountName}
              </h3>
              <span className="px-2 py-1 rounded-full text-xs font-medium bg-gray-100 text-gray-700">
                {getAccountTypeLabel(asset.accountType)}
              </span>
            </div>
            <p className="text-sm text-muted-foreground">
              {asset.bankName} • {asset.maskedAccountNumber}
            </p>
            <div className="flex items-center gap-2 mt-1">
              <StatusIcon
                className={`h-4 w-4 ${
                  getConnectionStatusColor(asset.connection.status).split(
                    " "
                  )[0]
                }`}
              />
              <span className="text-xs text-muted-foreground">
                {hoursAgo === 0 ? "방금 전" : `${hoursAgo}시간 전`} 동기화
              </span>
            </div>
          </div>
        </div>

        <div className="text-right">
          {asset.accountType === "card" ? (
            <div>
              <p className="text-sm text-muted-foreground">사용 가능</p>
              <p className="text-xl font-bold text-primary">
                ₩{formatCurrency(asset.card.availableCredit)}
              </p>
              <p className="text-sm text-muted-foreground">
                한도: ₩{formatCurrency(asset.card.creditLimit)}
              </p>
            </div>
          ) : asset.accountType === "loan" ? (
            <div>
              <p className="text-sm text-muted-foreground">대출 잔액</p>
              <p className="text-xl font-bold text-red-600">
                ₩{formatCurrency(asset.loan.principalBalance)}
              </p>
              <p className="text-sm text-muted-foreground">
                월 상환: ₩{formatCurrency(asset.loan.monthlyPayment)}
              </p>
            </div>
          ) : (
            <div>
              <p className="text-sm text-muted-foreground">잔액</p>
              <p className="text-xl font-bold text-primary">
                ₩{formatCurrency(asset.balance.current)}
              </p>
              <p className="text-sm text-muted-foreground">
                {asset.balance.currency}
              </p>
            </div>
          )}

          {!asset.settings.isVisible && (
            <div className="flex items-center gap-1 mt-1">
              <EyeOff className="h-3 w-3 text-muted-foreground" />
              <span className="text-xs text-muted-foreground">숨김</span>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

// 계좌 연결 모달
function ConnectAccountModal({ onClose, onSuccess }) {
  const [step, setStep] = useState(1);
  const [selectedBank, setSelectedBank] = useState(null);
  const [formData, setFormData] = useState({
    accountNumber: "",
    accountPassword: "",
    nickname: "",
  });

  const supportedBanks = [
    { code: "HANA", name: "하나은행", logo: "🏦" },
    { code: "KB", name: "KB국민은행", logo: "🏦" },
    { code: "SHINHAN", name: "신한은행", logo: "🏦" },
    { code: "WOORI", name: "우리은행", logo: "🏦" },
    { code: "NH", name: "NH농협은행", logo: "🏦" },
    { code: "KAKAO", name: "카카오뱅크", logo: "💛" },
    { code: "TOSS", name: "토스뱅크", logo: "💙" },
  ];

  const handleConnect = async () => {
    try {
      // API 호출 시뮬레이션
      await new Promise((resolve) => setTimeout(resolve, 2000));

      const newAsset = {
        _id: Date.now().toString(),
        accountName: `${selectedBank.name} 계좌`,
        nickname: formData.nickname || `${selectedBank.name} 계좌`,
        bankName: selectedBank.name,
        bankCode: selectedBank.code,
        accountType: "checking",
        balance: {
          current: Math.floor(Math.random() * 5000000) + 100000,
          available: 0,
          currency: "KRW",
          lastUpdated: new Date(),
        },
        connection: {
          status: "connected",
          lastSyncAt: new Date(),
          syncFrequency: "daily",
        },
        settings: {
          isVisible: true,
          includeInTotalAssets: true,
        },
        maskedAccountNumber: `****-****-****-${formData.accountNumber.slice(
          -4
        )}`,
      };

      onSuccess(newAsset);
    } catch (error) {
      console.error("계좌 연결 실패:", error);
    }
  };

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
      <div className="bg-white dark:bg-gray-800 rounded-xl p-6 w-full max-w-md mx-4">
        <div className="flex items-center justify-between mb-6">
          <h3 className="text-xl font-bold">계좌 연결</h3>
          <button onClick={onClose} className="p-2 hover:bg-muted rounded-lg">
            <Plus className="h-4 w-4 rotate-45" />
          </button>
        </div>

        {step === 1 && (
          <div className="space-y-4">
            <p className="text-muted-foreground">연결할 은행을 선택해주세요</p>
            <div className="grid grid-cols-2 gap-3">
              {supportedBanks.map((bank) => (
                <button
                  key={bank.code}
                  onClick={() => {
                    setSelectedBank(bank);
                    setStep(2);
                  }}
                  className="p-4 border border-border rounded-lg hover:bg-muted/50 transition-colors text-left"
                >
                  <div className="flex items-center gap-3">
                    <span className="text-2xl">{bank.logo}</span>
                    <span className="font-medium">{bank.name}</span>
                  </div>
                </button>
              ))}
            </div>
          </div>
        )}

        {step === 2 && selectedBank && (
          <div className="space-y-4">
            <div className="flex items-center gap-3 p-4 bg-muted/30 rounded-lg">
              <span className="text-2xl">{selectedBank.logo}</span>
              <div>
                <p className="font-medium">{selectedBank.name}</p>
                <p className="text-sm text-muted-foreground">
                  계좌 정보를 입력해주세요
                </p>
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium mb-2">계좌번호</label>
              <input
                type="text"
                value={formData.accountNumber}
                onChange={(e) =>
                  setFormData({ ...formData, accountNumber: e.target.value })
                }
                className="w-full p-3 border border-border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50"
                placeholder="123-456-789012"
                required
              />
            </div>

            <div>
              <label className="block text-sm font-medium mb-2">
                계좌 비밀번호
              </label>
              <input
                type="password"
                value={formData.accountPassword}
                onChange={(e) =>
                  setFormData({ ...formData, accountPassword: e.target.value })
                }
                className="w-full p-3 border border-border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50"
                placeholder="••••"
                required
              />
            </div>

            <div>
              <label className="block text-sm font-medium mb-2">
                별칭 (선택사항)
              </label>
              <input
                type="text"
                value={formData.nickname}
                onChange={(e) =>
                  setFormData({ ...formData, nickname: e.target.value })
                }
                className="w-full p-3 border border-border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50"
                placeholder="예: 주거래 통장"
              />
            </div>

            <div className="flex gap-3 pt-4">
              <button
                onClick={() => setStep(1)}
                className="flex-1 px-4 py-3 border border-border rounded-lg hover:bg-muted/50 transition-colors"
              >
                이전
              </button>
              <button
                onClick={handleConnect}
                disabled={!formData.accountNumber || !formData.accountPassword}
                className="flex-1 px-4 py-3 bg-primary text-primary-foreground rounded-lg hover:bg-primary/90 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
              >
                연결
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

// 자산 상세 모달
function AssetDetailModal({ asset, onClose, onUpdate, formatCurrency }) {
  const [activeTab, setActiveTab] = useState("overview");
  const [settings, setSettings] = useState(asset.settings);

  const tabs = [
    { id: "overview", label: "개요" },
    { id: "transactions", label: "거래내역" },
    { id: "settings", label: "설정" },
  ];

  const handleSettingsUpdate = () => {
    const updatedAsset = { ...asset, settings };
    onUpdate(updatedAsset);
    alert("설정이 저장되었습니다!");
  };

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
      <div className="bg-white dark:bg-gray-800 rounded-xl w-full max-w-2xl mx-4 max-h-[90vh] overflow-hidden">
        <div className="p-6 border-b border-border">
          <div className="flex items-center justify-between">
            <div>
              <h3 className="text-xl font-bold">
                {asset.nickname || asset.accountName}
              </h3>
              <p className="text-muted-foreground">
                {asset.bankName} • {asset.maskedAccountNumber}
              </p>
            </div>
            <button onClick={onClose} className="p-2 hover:bg-muted rounded-lg">
              <Plus className="h-5 w-5 rotate-45" />
            </button>
          </div>

          <div className="flex gap-1 mt-4">
            {tabs.map((tab) => (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id)}
                className={`px-4 py-2 rounded-lg transition-colors ${
                  activeTab === tab.id
                    ? "bg-primary text-primary-foreground"
                    : "hover:bg-muted"
                }`}
              >
                {tab.label}
              </button>
            ))}
          </div>
        </div>

        <div className="p-6 overflow-y-auto max-h-[60vh]">
          {activeTab === "overview" && (
            <div className="space-y-6">
              <div className="bg-muted/30 rounded-lg p-6">
                <h4 className="font-bold text-lg mb-4">계좌 정보</h4>
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <p className="text-sm text-muted-foreground">현재 잔액</p>
                    <p className="text-2xl font-bold text-primary">
                      ₩{formatCurrency(asset.balance.current)}
                    </p>
                  </div>
                  <div>
                    <p className="text-sm text-muted-foreground">
                      마지막 업데이트
                    </p>
                    <p className="text-lg">
                      {new Date(asset.balance.lastUpdated).toLocaleString(
                        "ko-KR"
                      )}
                    </p>
                  </div>
                </div>
              </div>

              {asset.accountType === "card" && asset.card && (
                <div className="bg-muted/30 rounded-lg p-6">
                  <h4 className="font-bold text-lg mb-4">카드 정보</h4>
                  <div className="grid grid-cols-2 gap-4">
                    <div>
                      <p className="text-sm text-muted-foreground">신용한도</p>
                      <p className="text-xl font-bold">
                        ₩{formatCurrency(asset.card.creditLimit)}
                      </p>
                    </div>
                    <div>
                      <p className="text-sm text-muted-foreground">사용 금액</p>
                      <p className="text-xl font-bold text-red-600">
                        ₩{formatCurrency(asset.card.currentBalance)}
                      </p>
                    </div>
                    <div>
                      <p className="text-sm text-muted-foreground">결제일</p>
                      <p className="text-lg">
                        {new Date(asset.card.paymentDueDate).toLocaleDateString(
                          "ko-KR"
                        )}
                      </p>
                    </div>
                  </div>
                </div>
              )}

              {asset.accountType === "loan" && asset.loan && (
                <div className="bg-muted/30 rounded-lg p-6">
                  <h4 className="font-bold text-lg mb-4">대출 정보</h4>
                  <div className="grid grid-cols-2 gap-4">
                    <div>
                      <p className="text-sm text-muted-foreground">대출 잔액</p>
                      <p className="text-xl font-bold text-red-600">
                        ₩{formatCurrency(asset.loan.principalBalance)}
                      </p>
                    </div>
                    <div>
                      <p className="text-sm text-muted-foreground">금리</p>
                      <p className="text-xl font-bold">
                        {asset.loan.interestRate}%
                      </p>
                    </div>
                    <div>
                      <p className="text-sm text-muted-foreground">월 상환액</p>
                      <p className="text-lg">
                        ₩{formatCurrency(asset.loan.monthlyPayment)}
                      </p>
                    </div>
                    <div>
                      <p className="text-sm text-muted-foreground">
                        다음 상환일
                      </p>
                      <p className="text-lg">
                        {new Date(
                          asset.loan.nextPaymentDate
                        ).toLocaleDateString("ko-KR")}
                      </p>
                    </div>
                  </div>
                </div>
              )}
            </div>
          )}

          {activeTab === "transactions" && (
            <div className="space-y-4">
              <h4 className="font-bold text-lg">거래 내역</h4>
              <div className="text-center py-8 text-muted-foreground">
                <Wallet className="h-12 w-12 mx-auto mb-3 opacity-50" />
                <p>거래 내역을 불러오는 중...</p>
              </div>
            </div>
          )}

          {activeTab === "settings" && (
            <div className="space-y-6">
              <h4 className="font-bold text-lg">계좌 설정</h4>

              <div className="space-y-4">
                <div className="flex items-center justify-between p-4 border border-border rounded-lg">
                  <div>
                    <h5 className="font-medium">자산 목록에 표시</h5>
                    <p className="text-sm text-muted-foreground">
                      대시보드에서 이 계좌를 표시합니다
                    </p>
                  </div>
                  <label className="relative inline-flex items-center cursor-pointer">
                    <input
                      type="checkbox"
                      checked={settings.isVisible}
                      onChange={(e) =>
                        setSettings({
                          ...settings,
                          isVisible: e.target.checked,
                        })
                      }
                      className="sr-only peer"
                    />
                    <div className="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-primary/20 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-primary"></div>
                  </label>
                </div>

                <div className="flex items-center justify-between p-4 border border-border rounded-lg">
                  <div>
                    <h5 className="font-medium">총 자산에 포함</h5>
                    <p className="text-sm text-muted-foreground">
                      총 자산 계산에 이 계좌를 포함합니다
                    </p>
                  </div>
                  <label className="relative inline-flex items-center cursor-pointer">
                    <input
                      type="checkbox"
                      checked={settings.includeInTotalAssets}
                      onChange={(e) =>
                        setSettings({
                          ...settings,
                          includeInTotalAssets: e.target.checked,
                        })
                      }
                      className="sr-only peer"
                    />
                    <div className="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-primary/20 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-primary"></div>
                  </label>
                </div>

                {settings.notifications && (
                  <div className="flex items-center justify-between p-4 border border-border rounded-lg">
                    <div>
                      <h5 className="font-medium">잔액 알림</h5>
                      <p className="text-sm text-muted-foreground">
                        잔액이 부족할 때 알림을 받습니다
                      </p>
                    </div>
                    <label className="relative inline-flex items-center cursor-pointer">
                      <input
                        type="checkbox"
                        checked={settings.notifications.balanceAlerts}
                        onChange={(e) =>
                          setSettings({
                            ...settings,
                            notifications: {
                              ...settings.notifications,
                              balanceAlerts: e.target.checked,
                            },
                          })
                        }
                        className="sr-only peer"
                      />
                      <div className="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-primary/20 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-primary"></div>
                    </label>
                  </div>
                )}

                <button
                  onClick={handleSettingsUpdate}
                  className="w-full px-4 py-3 bg-primary text-primary-foreground rounded-lg hover:bg-primary/90 transition-colors"
                >
                  설정 저장
                </button>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
