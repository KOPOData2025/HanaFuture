"use client";

import React, { useState, useEffect } from "react";
import { createPortal } from "react-dom";
import {
  Home,
  Users,
  PiggyBank,
  Gift,
  Baby,
  ArrowRight,
  Menu,
  X,
  LogOut,
  User,
  Wallet,
  TrendingUp,
  Target,
  Heart,
  Star,
  Lock,
  ChevronLeft,
  ChevronRight,
  ChevronDown,
  Play,
  Pause,
  CreditCard,
  Plus,
  Bell,
} from "lucide-react";
import { useAuth } from "../../../contexts/AuthContext";

// API Base URL - 환경변수 사용
const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080/api";
import { AuthScreen } from "../auth/auth-screen";
import { ModernGroupAccountFlow } from "../group-account/modern-group-account-flow";
import { GroupAccountManagement } from "../group-account/group-account-management";
import GovernmentBenefits from "../welfare/government-benefits";
import { BookmarksPage } from "../welfare/bookmarks-page";
import { SavingsProducts } from "../savings/savings-products";
import { EnhancedSavingsFlow } from "../savings/enhanced-savings-flow";
import { GroupAccountIntro } from "../group-account/group-account-intro";
import { ChildCardManagement } from "../family/child-card-management";
import { GroupAccountHero } from "../group-account/group-account-hero";
import { MyPage } from "../user/my-page";
import TogetherSavingsIntro from "../savings/together-savings-intro";
import TogetherSavingsCreationFlow from "../savings/together-savings-creation-flow";
import { BankSelection } from "../assets/bank-selection";
import { OpenBankingConsent } from "../assets/openbanking-consent";
import { IdentityVerification } from "../auth/identity-verification";
import { AccountSelection } from "../assets/account-selection";
import { NotificationCenter } from "../../common/notification-center";

export function UnifiedDashboard() {
  const { user, logout, isLoggedIn } = useAuth();
  const [activeTab, setActiveTab] = useState("home");
  const [showMobileMenu, setShowMobileMenu] = useState(false);
  const [showUserMenu, setShowUserMenu] = useState(false);
  const [showAuth, setShowAuth] = useState(false);
  const [showLoginModal, setShowLoginModal] = useState(false);
  const [showGroupAccountFlow, setShowGroupAccountFlow] = useState(false);
  const [showGroupAccountIntro, setShowGroupAccountIntro] = useState(false);
  const [showSavingsCreationFlow, setShowSavingsCreationFlow] = useState(false);
  const [showTogetherSavingsIntro, setShowTogetherSavingsIntro] =
    useState(false);
  const [currentSlide, setCurrentSlide] = useState(0);
  const [isAutoPlaying, setIsAutoPlaying] = useState(true);
  const [hoveredMenu, setHoveredMenu] = useState(null);
  const [hoverTimeout, setHoverTimeout] = useState(null);

  // 알림 상태 관리
  const [showNotifications, setShowNotifications] = useState(false);
  const [notifications, setNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);

  // 오픈뱅킹 플로우 상태 관리
  const [showOpenBankingConsent, setShowOpenBankingConsent] = useState(false);
  const [showUserVerification, setShowUserVerification] = useState(false);
  const [showBankSelection, setShowBankSelection] = useState(false);
  const [showAccountSelection, setShowAccountSelection] = useState(false);
  const [availableAccounts, setAvailableAccounts] = useState([]);
  const [bankSelectionError, setBankSelectionError] = useState("");

  // 연결된 계좌 상태 관리
  const [connectedAccounts, setConnectedAccounts] = useState(() => {
    if (typeof window !== "undefined") {
      const saved = localStorage.getItem("connectedAccounts");
      return saved ? JSON.parse(saved) : [];
    }
    return [];
  });
  useEffect(() => {
    const loadConnectedAccounts = async () => {
      if (isLoggedIn && user?.id) {
        try {
          const response = await fetch(
            `${API_BASE_URL}/user/bank-accounts/user/${user.id}`,
            {
              headers: {
                "Content-Type": "application/json",
                ...(user?.token && { Authorization: `Bearer ${user.token}` }),
              },
            }
          );

          if (response.ok) {
            const accountsData = await response.json();
            const accounts = accountsData.data || [];

            console.log("로그인 시 연결된 계좌 자동 로드:", accounts);

            setConnectedAccounts(accounts);

            // localStorage에도 저장
            if (typeof window !== "undefined") {
              localStorage.setItem(
                "connectedAccounts",
                JSON.stringify(accounts)
              );
            }
          } else {
            console.error("계좌 조회 실패:", response.status);
          }
        } catch (error) {
          console.error("계좌 조회 중 오류:", error);
        }
      }
    };

    loadConnectedAccounts();
  }, [isLoggedIn, user?.id]);

  // 사용자 인증 데이터 저장
  const [userVerificationData, setUserVerificationData] = useState(null);

  // 가족 추가 모달 상태
  const [showFamilyAddModal, setShowFamilyAddModal] = useState(false);
  const [showFamilySuccessModal, setShowFamilySuccessModal] = useState(false);
  const [showFamilyAcceptModal, setShowFamilyAcceptModal] = useState(false);
  const [showLoginRequiredModal, setShowLoginRequiredModal] = useState(false);
  const [showAccountLinkSuccessModal, setShowAccountLinkSuccessModal] =
    useState(false);
  const [newFamilyMember, setNewFamilyMember] = useState({
    name: "",
    phoneNumber: "",
  });

  // 초대 링크로 접속한 경우 로그인 페이지 표시
  useEffect(() => {
    if (typeof window !== "undefined") {
      const urlParams = new URLSearchParams(window.location.search);
      const isInvite = urlParams.get("invite");
      const authParam = urlParams.get("auth");

      // 초대 링크로 접속했고 로그인하지 않은 경우
      if (isInvite === "family" && !isLoggedIn) {
        setShowAuth(true);
      }

      // auth=true 파라미터가 있고 로그인하지 않은 경우 로그인 화면 표시
      if (authParam === "true" && !isLoggedIn) {
        setShowAuth(true);
      }
    }
  }, [isLoggedIn]);

  // connectedAccounts가 변경될 때마다 localStorage에 저장
  useEffect(() => {
    if (typeof window !== "undefined") {
      localStorage.setItem(
        "connectedAccounts",
        JSON.stringify(connectedAccounts)
      );
    }
  }, [connectedAccounts]);

  // 로그인 후 초대 링크로 다시 돌아가기
  useEffect(() => {
    if (user) {
      // 기존 초대 링크 처리
      const pendingInvite = localStorage.getItem("pendingInvite");
      if (pendingInvite) {
        localStorage.removeItem("pendingInvite");
        setTimeout(() => {
          window.location.href = pendingInvite;
        }, 1000); // 1초 후 이동 (로그인 완료 메시지 확인 후)
        return;
      }

      // 모임통장 초대 토큰 처리
      const pendingGroupInvite = localStorage.getItem("pendingGroupInvite");
      if (pendingGroupInvite) {
        localStorage.removeItem("pendingGroupInvite");
        // 초대 수락 API 호출
        acceptGroupInvite(pendingGroupInvite);
      }
    }
  }, [user]);

  // URL 파라미터 확인하여 알림 탭으로 이동
  useEffect(() => {
    if (user && typeof window !== "undefined") {
      const urlParams = new URLSearchParams(window.location.search);
      const tab = urlParams.get("tab");
      const from = urlParams.get("from");
      const invite = urlParams.get("invite");

      // 가족 초대 링크로 접속한 경우 URL 파라미터만 제거
      if (invite === "family") {
        console.log("가족 초대 링크로 접속 - 로그인 완료");
        // URL 파라미터 제거
        window.history.replaceState(
          {},
          document.title,
          window.location.pathname
        );
      } else if (tab === "notifications") {
        console.log(`${from || "누군가"}님의 초대로 알림 탭으로 이동합니다`);
        setActiveTab("mypage");
        // URL 파라미터 제거
        window.history.replaceState(
          {},
          document.title,
          window.location.pathname
        );
      }
    }
  }, [user]);

  // 알림 데이터 로드
  const loadNotifications = async () => {
    if (!user?.id) return;

    try {
      const response = await fetch(
        `${API_BASE_URL}/notifications/user/${user.id}`,
        {
          headers: {
            Authorization: `Bearer ${user.token}`,
          },
        }
      );

      if (response.ok) {
        const result = await response.json();
        const notificationData = result.data || [];
        setNotifications(notificationData);

        console.log("알림 데이터:", notificationData);

        // 읽지 않은 알림 개수 계산
        const unread = notificationData.filter(
          (n) => n.status === "UNREAD"
        ).length;
        setUnreadCount(unread);

        console.log(`읽지 않은 알림: ${unread}개`);
      }
    } catch (error) {
      console.error("알림 조회 실패:", error);
    }
  };

  // 로그인 시 알림 로드
  useEffect(() => {
    if (user) {
      loadNotifications();
      // 30초마다 알림 새로고침
      const interval = setInterval(loadNotifications, 30000);
      return () => clearInterval(interval);
    }
  }, [user]);

  // 모임통장 초대 수락 함수
  const acceptGroupInvite = async (token) => {
    try {
      // 토큰 디코딩하여 groupAccountId 추출
      const decodedData = JSON.parse(atob(token));
      const { groupAccountId } = decodedData;

      const response = await fetch(
        `${API_BASE_URL}/group-accounts/${groupAccountId}/accept-invite`,
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
        alert("모임통장 초대를 수락했습니다! 🎉");
        // 마이페이지로 이동
        setActiveTab("mypage");
      } else {
        const error = await response.json();
        alert(`초대 수락 실패: ${error.message || "알 수 없는 오류"}`);
      }
    } catch (error) {
      console.error("초대 수락 중 오류:", error);
      alert("초대 수락 중 오류가 발생했습니다.");
    }
  };

  const handleLogout = async () => {
    await logout();
    // 로그아웃 시 연결된 계좌 정보 초기화
    if (typeof window !== "undefined") {
      localStorage.removeItem("connectedAccounts");
    }
    setConnectedAccounts([]);
    setActiveTab("home");
  };

  // 오픈뱅킹 플로우 핸들러들
  const handleOpenBankingStart = () => {
    // 모든 상태 초기화
    setShowOpenBankingConsent(false);
    setShowUserVerification(false);
    setShowBankSelection(false);
    // 오픈뱅킹 동의부터 시작
    setTimeout(() => setShowOpenBankingConsent(true), 100);
  };

  const handleConsentComplete = () => {
    console.log(
      "상태 변경: showOpenBankingConsent -> false, showUserVerification -> true"
    );
    setShowOpenBankingConsent(false);
    setShowUserVerification(true);
  };

  const handleVerificationComplete = (verificationData) => {
    console.log("🔍 userCi:", verificationData?.userCi);
    console.log("🔍 userInfo:", verificationData?.userInfo);
    setUserVerificationData(verificationData); // 인증 데이터 저장
    setShowUserVerification(false);
    setShowBankSelection(true);
  };

  const handleAccountSelectionComplete = async (selectedAccounts) => {
    setShowAccountSelection(false);

    try {
      // 다중 계좌를 순차적으로 백엔드에 저장
      const selectedArray = Array.isArray(selectedAccounts)
        ? selectedAccounts
        : [selectedAccounts];

      console.log(`💾 ${selectedArray.length}개 계좌 저장 시작`);

      for (const selectedAccount of selectedArray) {
        const saveResponse = await fetch(
          `${API_BASE_URL}/user/bank-accounts/link`,
          {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
              ...(user?.token && { Authorization: `Bearer ${user.token}` }),
            },
            body: JSON.stringify({
              userId: user?.id || 1,
              accountNumber:
                selectedAccount.accountNum || selectedAccount.accountNumber,
              accountName:
                selectedAccount.productName || selectedAccount.accountName,
              accountType: selectedAccount.accountType || "1",
              balance:
                parseInt(
                  String(
                    selectedAccount.balanceAmt || selectedAccount.balance || 0
                  ).replace(/,/g, "")
                ) || 0,
              bankCode: selectedAccount.bankCode || "081",
              bankName: selectedAccount.bankName || "하나은행",
              accountAlias:
                selectedAccount.productName || selectedAccount.accountName,
              setAsPrimary: false,
              autoSyncEnabled: true,
            }),
          }
        );

        if (saveResponse.ok) {
          const savedAccount = await saveResponse.json();
          console.log("계좌 저장 성공:", savedAccount);
          setConnectedAccounts((prev) => [...prev, selectedAccount]);
        } else {
          const errorData = await saveResponse.json();
          console.error("계좌 저장 실패:", selectedAccount.accountNum);
          console.error("에러:", errorData.message || errorData);

          // 이미 등록된 계좌인 경우 사용자에게 알림
          if (
            errorData.message &&
            errorData.message.includes("이미 등록된 계좌")
          ) {
            console.warn(
              `계좌 ${selectedAccount.accountNum}는 이미 등록되어 있습니다.`
            );
          }
        }
      }

      // 모든 계좌 저장 완료 후 성공 모달 표시
      console.log(
        `${selectedArray.length}개 계좌 저장 완료! 성공 모달을 표시합니다.`
      );
      setShowAccountLinkSuccessModal(true);
    } catch (error) {
      console.error("계좌 저장 중 오류:", error);
      alert("계좌 저장 중 오류가 발생했습니다: " + error.message);
    }
  };

  const handleBankSelectionComplete = async (selectedBanks) => {
    setShowBankSelection(false);

    try {
      // 실제 오픈뱅킹 API를 통해 계좌 정보 가져오기
      const accountPromises = selectedBanks.map(async (bank) => {
        // 회원가입 중인 경우 sessionStorage에서 이메일 가져오기
        let userEmail = user?.email;
        if (!userEmail && typeof window !== "undefined") {
          const pendingSignupData = sessionStorage.getItem("pendingSignupData");
          const pendingOAuth2Data = sessionStorage.getItem(
            "pendingOAuth2SignupData"
          );

          if (pendingSignupData) {
            const signupData = JSON.parse(pendingSignupData);
            userEmail = signupData.email;
          } else if (pendingOAuth2Data) {
            const oauth2Data = JSON.parse(pendingOAuth2Data);
            userEmail = oauth2Data.signupData?.email;
          }
        }

        console.log(
          "🔍 계좌 조회 요청 - userEmail:",
          userEmail,
          "userName:",
          userVerificationData?.userInfo?.name
        );

        const requestData = {
          userCi: userVerificationData?.userCi,
          userName: userVerificationData?.userInfo?.name,
          userNum: userVerificationData?.userInfo?.residentNumber?.replace(
            "-",
            ""
          ),
          bankCode: bank.code || bank.id,
          bankName: bank.name || bank,
          userId: user?.id || null,
          userEmail: userEmail || null, // fallback 제거
        };

        const response = await fetch(`${API_BASE_URL}/openbanking/accounts`, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            ...(user?.token && { Authorization: `Bearer ${user.token}` }),
          },
          body: JSON.stringify(requestData),
        });

        if (response.ok) {
          const accountData = await response.json();

          // 백엔드에서 success: false를 반환하는 경우 (예: 사용자 없음)
          if (accountData.success === false) {
            console.error(`❌ 계좌 조회 실패: ${accountData.message}`);
            return [];
          }

          const result =
            accountData.data?.data?.resList ||
            accountData.data?.resList ||
            accountData.data?.accounts ||
            accountData.data ||
            [];
          return result;
        } else {
          const bankName =
            typeof bank === "string" ? bank : bank?.name || "Unknown Bank";
          console.error(`${bankName} 계좌 조회 실패 (HTTP ${response.status})`);
          return [];
        }
      });

      const accountResults = await Promise.all(accountPromises);
      const allAccounts = accountResults.flat();

      if (allAccounts.length > 0) {
        // 사용자에게 계좌 선택 화면 표시
        console.log(
          `📋 조회된 계좌 ${allAccounts.length}개 - 계좌 선택 화면 표시`
        );
        setBankSelectionError(""); // 에러 메시지 초기화
        setAvailableAccounts(allAccounts);
        setShowAccountSelection(true);
      } else {
        // 계좌 조회 실패 - 에러 메시지 설정하고 은행 선택 화면 유지
        console.error("❌ 계좌 조회 실패: 조회된 계좌가 없습니다.");
        setBankSelectionError(
          "선택하신 은행의 계좌 정보를 불러올 수 없습니다."
        );
        setShowBankSelection(true);
      }
    } catch (error) {
      console.error("계좌 연결 중 오류 발생:", error);
      // 오류 발생 시 에러 메시지 설정
      setBankSelectionError(
        `계좌 정보를 불러오는 중 오류가 발생했습니다. (${error.message})`
      );
      setShowBankSelection(true);
    }
  };

  const handleFlowCancel = () => {
    setShowOpenBankingConsent(false);
    setShowUserVerification(false);
    setShowBankSelection(false);
    setBankSelectionError(""); // 에러 메시지 초기화
    setShowAccountSelection(false);
    setAvailableAccounts([]);
    setUserVerificationData(null); // 인증 데이터도 초기화
  };

  const handleFeatureClick = async (featureId) => {
    if (!isLoggedIn) {
      setShowLoginModal(true);
      return;
    }

    // 로그인된 경우 실제 기능으로 이동
    if (featureId === "group-account") {
      console.log("🏠 모임통장 서비스 소개 페이지로 이동!");
      setShowGroupAccountIntro(true);
      // 페이지 최상단으로 스크롤
      window.scrollTo({ top: 0, behavior: "smooth" });
    } else if (featureId === "create-group-account") {
      console.log(" 모임통장 개설 플로우 시작!");

      //  연결된 계좌 확인
      try {
        const response = await fetch(
          `${API_BASE_URL}/user/bank-accounts/user/${user?.id || 1}`,
          {
            headers: {
              "Content-Type": "application/json",
              ...(user?.token && { Authorization: `Bearer ${user.token}` }),
            },
          }
        );

        if (response.ok) {
          const accountsData = await response.json();
          const connectedAccounts = accountsData.data || [];

          console.log("🏦 연결된 계좌 확인:", connectedAccounts);

          if (connectedAccounts.length === 0) {
            // 연결된 계좌가 없으면 오픈뱅킹 플로우 시작
            console.log("연결된 계좌가 없습니다. 오픈뱅킹 연동을 시작합니다.");
            setShowOpenBankingConsent(true);
            return;
          } else {
            // 연결된 계좌가 있으면 바로 모임통장 개설 플로우 시작
            console.log("연결된 계좌가 있습니다. 모임통장 개설을 진행합니다.");
            setShowGroupAccountFlow(true);
          }
        } else {
          console.error("계좌 조회 실패:", response.status);
          // API 실패 시에도 오픈뱅킹 플로우로 안내
          setShowOpenBankingConsent(true);
        }
      } catch (error) {
        console.error("계좌 조회 중 오류:", error);
        // 오류 시에도 오픈뱅킹 플로우로 안내
        setShowOpenBankingConsent(true);
      }
    } else if (featureId === "manage-group-account") {
      console.log("📊 모임통장 관리 페이지로 이동!");
      setActiveTab("manage-group-account");
    } else if (featureId === "benefits-search") {
      console.log("🔍 정부 혜택 조회 페이지로 이동!");
      setActiveTab("benefits");
    } else if (featureId === "bookmarks-list") {
      setActiveTab("bookmarks");
    } else if (featureId === "my-page") {
      setActiveTab("my-page");
    } else if (featureId === "savings") {
      console.log("💰 함께 적금 소개 페이지 표시!");
      setShowTogetherSavingsIntro(true);
      // 페이지 최상단으로 스크롤
      window.scrollTo({ top: 0, behavior: "smooth" });
    } else if (featureId === "allowance-card") {
      console.log("💳 아이카드 관리 페이지로 이동!");
      setActiveTab("allowance-card");
      // 페이지 최상단으로 스크롤
      window.scrollTo({ top: 0, behavior: "smooth" });
    } else if (featureId === "government-benefits") {
      console.log("🏛️ 정부 혜택 페이지로 이동!");
      setActiveTab("benefits");
      // 페이지 최상단으로 스크롤
      window.scrollTo({ top: 0, behavior: "smooth" });
    } else {
      setActiveTab(featureId);
    }
  };

  const navigationItems = [
    {
      id: "group-account",
      label: "모임통장",
      icon: Users,
      requiresAuth: true,
      submenu: [
        {
          id: "my-accounts",
          label: "내 계좌",
          description: "오픈뱅킹으로 계좌 연결 및 관리",
        },
        {
          id: "create-group-account",
          label: "모임통장 개설",
          description: "새로운 모임통장 만들기",
        },
        {
          id: "manage-group-account",
          label: "모임통장 관리",
          description: "기존 모임통장 관리",
        },
      ],
    },
    {
      id: "allowance-card",
      label: "아이카드",
      icon: Baby,
      requiresAuth: true,
    },
    {
      id: "savings",
      label: "함께 적금",
      icon: PiggyBank,
      requiresAuth: true,
    },
    {
      id: "benefits",
      label: "정부혜택",
      icon: Gift,
      requiresAuth: true,
      submenu: [
        {
          id: "benefits-search",
          label: "혜택 조회",
          description: "중앙정부와 지자체 혜택 통합 조회",
        },
        {
          id: "bookmarks-list",
          label: "내 즐겨찾기",
          description: "저장한 혜택들을 관리하세요",
        },
      ],
    },
  ];

  const dashboardData = {
    totalAssets: "₩45,231,000",
    totalAssetsChange: "+2.1%",
    monthlySavings: "₩1,200,000",
    monthlySavingsProgress: "80%",
    savingGoals: 2,
    bookmarks: 5,
  };

  const heroSlides = [
    {
      id: 1,
      title: "쉽고 빠른 가족 금융 서비스",
      subtitle: "하나Future에서 우리 가족만의 특별한 금융 여정을 시작하세요",
      description: "모임통장, 아이카드, 함께 적금까지 한 번에",
      character: "/hanacharacter/hanacharacter1.png",
      characterAlt: "하나 캐릭터 - 가족 금융",
    },
    {
      id: 2,
      title: "자녀 용돈 관리",
      subtitle: "아이카드로 시작하는 편리한 용돈 관리",
      description: "언제 어디서나 간편하게, 안전한 용돈카드 서비스",
      character: "/hanacharacter/hanacharacter9.png",
      characterAlt: "하나 캐릭터 - 용돈카드",
    },
    {
      id: 3,
      title: "함께 만드는 든든한 미래",
      subtitle: "부부가 함께하는 목표별 저축과 투자",
      description: "모임통장과 함께 적금으로 미래를 준비하세요",
      character: "/hanacharacter/hanacharacter5.png",
      characterAlt: "하나 캐릭터 - 미래 저축",
    },
  ];

  // 로그인/회원가입 화면으로 전환 시 스크롤 최상단 이동
  useEffect(() => {
    if (showAuth) {
      window.scrollTo({ top: 0, behavior: "smooth" });
    }
  }, [showAuth]);

  // 자동 슬라이드 재생
  useEffect(() => {
    if (!isAutoPlaying) return;

    const interval = setInterval(() => {
      setCurrentSlide((prev) => (prev + 1) % heroSlides.length);
    }, 2500);

    return () => clearInterval(interval);
  }, [isAutoPlaying, heroSlides.length]);

  const nextSlide = () => {
    setCurrentSlide((prev) => (prev + 1) % heroSlides.length);
  };

  const prevSlide = () => {
    setCurrentSlide(
      (prev) => (prev - 1 + heroSlides.length) % heroSlides.length
    );
  };

  const toggleAutoPlay = () => {
    setIsAutoPlaying(!isAutoPlaying);
  };

  // 로그인 화면 - 메인 페이지 헤더 사용
  if (showAuth) {
    return (
      <div className="min-h-screen bg-gray-50 flex flex-col">
        {/* 전체 헤더 - 메인 페이지와 동일 */}
        <header className="bg-white shadow-sm border-b border-gray-200 sticky top-0 z-50">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
            <div className="flex items-center justify-between">
              {/* 로고 */}
              <div className="flex-shrink-0">
                <button
                  onClick={() => {
                    setShowAuth(false);
                    setActiveTab("home");
                  }}
                  className="flex items-center space-x-3 hover:opacity-80 transition-opacity select-none"
                >
                  <div className="w-10 h-10">
                    <img
                      src="/bank-logos/HanaLogo.png"
                      alt="하나은행"
                      className="w-full h-full object-contain"
                    />
                  </div>
                  <span className="font-heading font-bold text-xl text-gray-900 select-none">
                    하나Future
                  </span>
                </button>
              </div>

              {/* Desktop Navigation */}
              <nav className="hidden md:flex items-center space-x-8">
                {navigationItems.map((item) => (
                  <div
                    key={item.id}
                    className="relative"
                    onMouseEnter={() => {
                      if (hoverTimeout) {
                        clearTimeout(hoverTimeout);
                        setHoverTimeout(null);
                      }
                      setHoveredMenu(item.id);
                    }}
                    onMouseLeave={() => {
                      const timeout = setTimeout(() => {
                        setHoveredMenu(null);
                      }, 150);
                      setHoverTimeout(timeout);
                    }}
                  >
                    <button
                      onClick={() => {
                        setShowAuth(false);
                        handleFeatureClick(item.id);
                        setShowGroupAccountIntro(false);
                      }}
                      className="text-base font-medium text-gray-700 hover:text-gray-900 transition-colors"
                    >
                      {item.label}
                    </button>

                    {/* 드롭다운 메뉴 */}
                    {item.submenu && hoveredMenu === item.id && (
                      <div
                        className="dropdown-menu fixed top-16 left-0 right-0 w-full bg-white shadow-xl border-t border-gray-200 z-50 transform transition-all duration-300 ease-out"
                        onMouseEnter={() => {
                          if (hoverTimeout) {
                            clearTimeout(hoverTimeout);
                            setHoverTimeout(null);
                          }
                          setHoveredMenu(item.id);
                        }}
                        onMouseLeave={() => {
                          const timeout = setTimeout(() => {
                            setHoveredMenu(null);
                          }, 150);
                          setHoverTimeout(timeout);
                        }}
                      >
                        <div className="max-w-7xl mx-auto px-8 py-8">
                          <div className="grid grid-cols-4 gap-8">
                            {item.submenu.map((subItem) => (
                              <button
                                key={subItem.id}
                                onClick={() => {
                                  setShowAuth(false);
                                  handleFeatureClick(subItem.id);
                                  setHoveredMenu(null);
                                  setShowGroupAccountIntro(false);
                                }}
                                className="text-left p-4 rounded-lg hover:bg-gray-50 transition-colors group"
                              >
                                <div className="font-semibold text-gray-900 text-lg mb-2 group-hover:text-emerald-600 transition-colors">
                                  {subItem.label}
                                </div>
                                <div className="text-sm text-gray-600 leading-relaxed">
                                  {subItem.description}
                                </div>
                              </button>
                            ))}
                          </div>
                        </div>
                      </div>
                    )}
                  </div>
                ))}
              </nav>

              {/* Right Section - 뒤로가기 버튼 */}
              <div className="flex items-center space-x-6">
                <button
                  onClick={() => setShowAuth(false)}
                  className="px-4 py-2 bg-emerald-600 text-white rounded-lg hover:bg-emerald-700 transition-colors text-sm font-medium"
                >
                  뒤로가기
                </button>
              </div>
            </div>
          </div>
        </header>

        {/* Main Content - Auth Screen */}
        <main className="flex-1">
          <AuthScreen
            onBack={() => setShowAuth(false)}
            onSignupComplete={() => {
              // 회원가입 완료 후 오픈뱅킹 동의 화면으로 이동
              setShowAuth(false);
              setShowOpenBankingConsent(true);
            }}
          />
        </main>
      </div>
    );
  }

  // 함께 적금 소개 페이지
  if (showTogetherSavingsIntro) {
    return (
      <div className="min-h-screen bg-gray-50 flex flex-col">
        {/* Header - 메인 페이지와 동일 */}
        <header className="bg-white shadow-sm sticky top-0 z-50">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
            <div className="flex justify-between items-center h-16">
              <div className="flex items-center space-x-4">
                <button
                  onClick={() => {
                    setShowTogetherSavingsIntro(false);
                    setActiveTab("home");
                  }}
                  className="flex items-center space-x-3 hover:opacity-80 transition-opacity select-none"
                >
                  <img
                    src="/bank-logos/HanaLogo.png"
                    alt="HanaBank"
                    className="h-8 w-8 object-contain"
                  />
                  <h1 className="text-xl font-bold text-gray-900 select-none">
                    하나Future
                  </h1>
                </button>
              </div>

              {/* Desktop Navigation */}
              <nav className="hidden md:flex items-center space-x-8">
                {navigationItems.map((item) => (
                  <div
                    key={item.id}
                    className="relative"
                    onMouseEnter={() => {
                      if (hoverTimeout) {
                        clearTimeout(hoverTimeout);
                        setHoverTimeout(null);
                      }
                      setHoveredMenu(item.id);
                    }}
                    onMouseLeave={() => {
                      const timeout = setTimeout(() => {
                        setHoveredMenu(null);
                      }, 150);
                      setHoverTimeout(timeout);
                    }}
                  >
                    <button
                      onClick={() => {
                        handleFeatureClick(item.id);
                        setShowTogetherSavingsIntro(false);
                      }}
                      className={`text-base font-medium transition-colors ${
                        item.id === "savings"
                          ? "text-teal-600"
                          : "text-gray-700 hover:text-gray-900"
                      }`}
                    >
                      {item.label}
                    </button>

                    {/* 드롭다운 메뉴 */}
                    {item.submenu && hoveredMenu === item.id && (
                      <div
                        className="dropdown-menu fixed top-16 left-0 right-0 w-full bg-white shadow-xl border-t border-gray-200 z-50 transform transition-all duration-300 ease-out"
                        onMouseEnter={() => {
                          if (hoverTimeout) {
                            clearTimeout(hoverTimeout);
                            setHoverTimeout(null);
                          }
                          setHoveredMenu(item.id);
                        }}
                        onMouseLeave={() => {
                          const timeout = setTimeout(() => {
                            setHoveredMenu(null);
                          }, 150);
                          setHoverTimeout(timeout);
                        }}
                      >
                        <div className="max-w-7xl mx-auto px-8 py-8">
                          <div className="grid grid-cols-4 gap-8">
                            {item.submenu.map((subItem) => (
                              <button
                                key={subItem.id}
                                onClick={() => {
                                  handleFeatureClick(subItem.id);
                                  setHoveredMenu(null);
                                  setShowTogetherSavingsIntro(false);
                                }}
                                className="text-left p-4 rounded-lg hover:bg-gray-50 transition-colors group"
                              >
                                <div className="font-semibold text-gray-900 text-lg mb-2 group-hover:text-teal-600 transition-colors">
                                  {subItem.label}
                                </div>
                                <div className="text-sm text-gray-600 leading-relaxed">
                                  {subItem.description}
                                </div>
                              </button>
                            ))}
                          </div>
                        </div>
                      </div>
                    )}
                  </div>
                ))}
              </nav>

              {/* User Menu */}
              <div className="flex items-center space-x-4">
                {isLoggedIn ? (
                  <div className="flex items-center space-x-3">
                    {/* 알림 벨 아이콘 */}
                    <button
                      onClick={() => setShowNotifications(!showNotifications)}
                      className="relative p-2 text-gray-600 hover:text-emerald-600 hover:bg-emerald-50 rounded-lg transition-colors"
                    >
                      <Bell className="w-5 h-5" />
                      {unreadCount > 0 && (
                        <span className="absolute -top-1 -right-1 w-5 h-5 bg-red-500 text-white text-xs font-bold rounded-full flex items-center justify-center">
                          {unreadCount > 9 ? "9+" : unreadCount}
                        </span>
                      )}
                    </button>

                    <button
                      onClick={() => {
                        setShowTogetherSavingsIntro(false);
                        setActiveTab("my-page");
                      }}
                      className="flex items-center space-x-2 text-gray-600 hover:text-gray-900 px-3 py-2 rounded-lg hover:bg-gray-100 transition-colors"
                    >
                      <User className="h-4 w-4" />
                      <span className="text-sm font-medium">
                        {user?.name || "사용자"}님
                      </span>
                    </button>

                    {/* Mobile Menu Button */}
                    <button
                      onClick={() => setShowMobileMenu(!showMobileMenu)}
                      className="md:hidden p-2 rounded-lg text-gray-600 hover:bg-gray-100"
                    >
                      {showMobileMenu ? (
                        <X className="h-6 w-6" />
                      ) : (
                        <Menu className="h-6 w-6" />
                      )}
                    </button>
                  </div>
                ) : (
                  <div className="flex items-center space-x-2">
                    <button
                      onClick={() => (window.location.href = "/login")}
                      className="px-4 py-2 text-gray-700 hover:text-gray-900 font-medium"
                    >
                      로그인
                    </button>
                    <button
                      onClick={() => (window.location.href = "/register")}
                      className="px-4 py-2 bg-teal-600 text-white rounded-lg hover:bg-teal-700 transition-colors"
                    >
                      회원가입
                    </button>
                  </div>
                )}
              </div>
            </div>
          </div>

          {/* Mobile Menu */}
          {showMobileMenu && (
            <div className="md:hidden border-t border-gray-200 bg-white">
              <div className="px-4 py-2 space-y-1">
                {navigationItems.map((item) => (
                  <div key={item.id}>
                    <button
                      onClick={() => {
                        handleFeatureClick(item.id);
                        setShowTogetherSavingsIntro(false);
                        setShowMobileMenu(false);
                      }}
                      className="w-full text-left px-3 py-2 rounded-lg text-gray-700 hover:bg-gray-100 font-medium"
                    >
                      {item.label}
                    </button>
                    {item.submenu && (
                      <div className="ml-4 space-y-1">
                        {item.submenu.map((subItem) => (
                          <button
                            key={subItem.id}
                            onClick={() => {
                              handleFeatureClick(subItem.id);
                              setShowTogetherSavingsIntro(false);
                              setShowMobileMenu(false);
                            }}
                            className="w-full text-left px-3 py-2 text-sm text-gray-600 hover:bg-gray-50 rounded-lg"
                          >
                            {subItem.label}
                          </button>
                        ))}
                      </div>
                    )}
                  </div>
                ))}
              </div>
            </div>
          )}
        </header>

        {/* 알림 패널 */}
        {showNotifications && (
          <div className="fixed top-16 right-4 w-96 max-h-[32rem] bg-white rounded-lg shadow-2xl border border-gray-200 z-50 overflow-hidden">
            <NotificationCenter
              userId={user?.id}
              onClose={() => setShowNotifications(false)}
            />
          </div>
        )}

        {/* Main Content */}
        <main className="flex-1">
          <TogetherSavingsIntro
            onStartCreation={() => {
              console.log("함께 적금 개설 시작!");
              setShowTogetherSavingsIntro(false);
              setShowSavingsCreationFlow(true);
            }}
          />
        </main>
      </div>
    );
  }

  // 함께 적금 개설 플로우
  if (showSavingsCreationFlow) {
    return (
      <div className="min-h-screen bg-gray-50 flex flex-col">
        {/* Header - 메인 페이지와 동일 */}
        <header className="bg-white shadow-sm sticky top-0 z-50">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
            <div className="flex justify-between items-center h-16">
              <div className="flex items-center space-x-4">
                <button
                  onClick={() => {
                    setShowSavingsCreationFlow(false);
                    setActiveTab("home");
                  }}
                  className="flex items-center space-x-3 hover:opacity-80 transition-opacity"
                >
                  <img
                    src="/bank-logos/HanaLogo.png"
                    alt="HanaBank"
                    className="h-8 w-8 object-contain"
                  />
                  <h1 className="text-xl font-bold text-gray-900">
                    하나Future
                  </h1>
                </button>
              </div>

              {/* Desktop Navigation */}
              <nav className="hidden md:flex items-center space-x-8">
                {navigationItems.map((item) => (
                  <div
                    key={item.id}
                    className="relative"
                    onMouseEnter={() => {
                      if (hoverTimeout) {
                        clearTimeout(hoverTimeout);
                        setHoverTimeout(null);
                      }
                      setHoveredMenu(item.id);
                    }}
                    onMouseLeave={() => {
                      const timeout = setTimeout(() => {
                        setHoveredMenu(null);
                      }, 150);
                      setHoverTimeout(timeout);
                    }}
                  >
                    <button
                      onClick={() => {
                        handleFeatureClick(item.id);
                        setShowSavingsCreationFlow(false);
                      }}
                      className={`text-base font-medium transition-colors ${
                        item.id === "savings"
                          ? "text-teal-600"
                          : "text-gray-700 hover:text-gray-900"
                      }`}
                    >
                      {item.label}
                    </button>

                    {/* 드롭다운 메뉴 */}
                    {item.submenu && hoveredMenu === item.id && (
                      <div
                        className="dropdown-menu fixed top-16 left-0 right-0 w-full bg-white shadow-xl border-t border-gray-200 z-50 transform transition-all duration-300 ease-out"
                        onMouseEnter={() => {
                          if (hoverTimeout) {
                            clearTimeout(hoverTimeout);
                            setHoverTimeout(null);
                          }
                          setHoveredMenu(item.id);
                        }}
                        onMouseLeave={() => {
                          const timeout = setTimeout(() => {
                            setHoveredMenu(null);
                          }, 150);
                          setHoverTimeout(timeout);
                        }}
                      >
                        <div className="max-w-7xl mx-auto px-8 py-8">
                          <div className="grid grid-cols-4 gap-8">
                            {item.submenu.map((subItem) => (
                              <button
                                key={subItem.id}
                                onClick={() => {
                                  handleFeatureClick(subItem.id);
                                  setHoveredMenu(null);
                                  setShowSavingsCreationFlow(false);
                                }}
                                className="text-left p-4 rounded-lg hover:bg-gray-50 transition-colors group"
                              >
                                <div className="font-semibold text-gray-900 text-lg mb-2 group-hover:text-teal-600 transition-colors">
                                  {subItem.label}
                                </div>
                                <div className="text-sm text-gray-600 leading-relaxed">
                                  {subItem.description}
                                </div>
                              </button>
                            ))}
                          </div>
                        </div>
                      </div>
                    )}
                  </div>
                ))}
              </nav>

              {/* User Menu */}
              <div className="flex items-center space-x-4">
                {isLoggedIn ? (
                  <div className="flex items-center space-x-3">
                    {/* 알림 벨 아이콘 */}
                    <button
                      onClick={() => setShowNotifications(!showNotifications)}
                      className="relative p-2 text-gray-600 hover:text-emerald-600 hover:bg-emerald-50 rounded-lg transition-colors"
                    >
                      <Bell className="w-5 h-5" />
                      {unreadCount > 0 && (
                        <span className="absolute -top-1 -right-1 w-5 h-5 bg-red-500 text-white text-xs font-bold rounded-full flex items-center justify-center">
                          {unreadCount > 9 ? "9+" : unreadCount}
                        </span>
                      )}
                    </button>

                    <button
                      onClick={() => {
                        setShowSavingsCreationFlow(false);
                        setActiveTab("my-page");
                      }}
                      className="flex items-center space-x-2 text-gray-600 hover:text-gray-900 px-3 py-2 rounded-lg hover:bg-gray-100 transition-colors"
                    >
                      <User className="h-4 w-4" />
                      <span className="text-sm font-medium">
                        {user?.name || "사용자"}님
                      </span>
                    </button>

                    {/* Mobile Menu Button */}
                    <button
                      onClick={() => setShowMobileMenu(!showMobileMenu)}
                      className="md:hidden p-2 rounded-lg text-gray-600 hover:bg-gray-100"
                    >
                      {showMobileMenu ? (
                        <X className="h-6 w-6" />
                      ) : (
                        <Menu className="h-6 w-6" />
                      )}
                    </button>
                  </div>
                ) : (
                  <div className="flex items-center space-x-2">
                    <button
                      onClick={() => (window.location.href = "/login")}
                      className="px-4 py-2 text-gray-700 hover:text-gray-900 font-medium"
                    >
                      로그인
                    </button>
                    <button
                      onClick={() => (window.location.href = "/register")}
                      className="px-4 py-2 bg-teal-600 text-white rounded-lg hover:bg-teal-700 transition-colors"
                    >
                      회원가입
                    </button>
                  </div>
                )}
              </div>
            </div>
          </div>

          {/* Mobile Menu */}
          {showMobileMenu && (
            <div className="md:hidden border-t border-gray-200 bg-white">
              <div className="px-4 py-2 space-y-1">
                {navigationItems.map((item) => (
                  <div key={item.id}>
                    <button
                      onClick={() => {
                        handleFeatureClick(item.id);
                        setShowSavingsCreationFlow(false);
                        setShowMobileMenu(false);
                      }}
                      className="w-full text-left px-3 py-2 rounded-lg text-gray-700 hover:bg-gray-100 font-medium"
                    >
                      {item.label}
                    </button>
                    {item.submenu && (
                      <div className="ml-4 space-y-1">
                        {item.submenu.map((subItem) => (
                          <button
                            key={subItem.id}
                            onClick={() => {
                              handleFeatureClick(subItem.id);
                              setShowSavingsCreationFlow(false);
                              setShowMobileMenu(false);
                            }}
                            className="w-full text-left px-3 py-2 text-sm text-gray-600 hover:bg-gray-50 rounded-lg"
                          >
                            {subItem.label}
                          </button>
                        ))}
                      </div>
                    )}
                  </div>
                ))}
              </div>
            </div>
          )}
        </header>

        {/* 알림 패널 */}
        {showNotifications && (
          <div className="fixed top-16 right-4 w-96 max-h-[32rem] bg-white rounded-lg shadow-2xl border border-gray-200 z-50 overflow-hidden">
            <NotificationCenter
              userId={user?.id}
              onClose={() => setShowNotifications(false)}
            />
          </div>
        )}

        {/* Main Content */}
        <main className="flex-1">
          <TogetherSavingsCreationFlow
            onComplete={(data) => {
              console.log("함께 적금 개설 완료:", data);
              setShowSavingsCreationFlow(false);
              setActiveTab("savings");
              // TODO: 백엔드 API 호출하여 계좌 개설
            }}
            onCancel={() => {
              console.log("함께 적금 개설 취소");
              setShowSavingsCreationFlow(false);
              setShowTogetherSavingsIntro(true);
            }}
          />
        </main>
      </div>
    );
  }

  if (showGroupAccountIntro) {
    return (
      <div className="min-h-screen bg-gray-50 flex flex-col">
        {/* 전체 헤더 - 함께 적금과 동일 */}
        <header className="bg-white shadow-sm border-b border-gray-200 sticky top-0 z-50">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
            <div className="flex items-center justify-between">
              {/* 로고 */}
              <div className="flex-shrink-0">
                <button
                  onClick={() => {
                    setShowGroupAccountIntro(false);
                    setActiveTab("home");
                  }}
                  className="flex items-center space-x-3 hover:opacity-80 transition-opacity select-none"
                >
                  <div className="w-10 h-10">
                    <img
                      src="/bank-logos/HanaLogo.png"
                      alt="하나은행"
                      className="w-full h-full object-contain"
                    />
                  </div>
                  <span className="font-heading font-bold text-xl text-gray-900 select-none">
                    하나Future
                  </span>
                </button>
              </div>

              {/* Desktop Navigation */}
              <nav className="hidden md:flex items-center space-x-8">
                {navigationItems.map((item) => (
                  <div
                    key={item.id}
                    className="relative"
                    onMouseEnter={() => {
                      if (hoverTimeout) {
                        clearTimeout(hoverTimeout);
                        setHoverTimeout(null);
                      }
                      setHoveredMenu(item.id);
                    }}
                    onMouseLeave={() => {
                      const timeout = setTimeout(() => {
                        setHoveredMenu(null);
                      }, 150);
                      setHoverTimeout(timeout);
                    }}
                  >
                    <button
                      onClick={() => {
                        handleFeatureClick(item.id);
                        setShowGroupAccountIntro(false);
                      }}
                      className={`text-base font-medium transition-colors ${
                        item.id === "group-account"
                          ? "text-teal-600"
                          : "text-gray-700 hover:text-gray-900"
                      }`}
                    >
                      {item.label}
                    </button>

                    {/* 드롭다운 메뉴 */}
                    {item.submenu && hoveredMenu === item.id && (
                      <div
                        className="dropdown-menu fixed top-16 left-0 right-0 w-full bg-white shadow-xl border-t border-gray-200 z-50 transform transition-all duration-300 ease-out"
                        onMouseEnter={() => {
                          if (hoverTimeout) {
                            clearTimeout(hoverTimeout);
                            setHoverTimeout(null);
                          }
                          setHoveredMenu(item.id);
                        }}
                        onMouseLeave={() => {
                          const timeout = setTimeout(() => {
                            setHoveredMenu(null);
                          }, 150);
                          setHoverTimeout(timeout);
                        }}
                      >
                        <div className="max-w-7xl mx-auto px-8 py-8">
                          <div className="grid grid-cols-4 gap-8">
                            {item.submenu.map((subItem) => (
                              <button
                                key={subItem.id}
                                onClick={() => {
                                  handleFeatureClick(subItem.id);
                                  setHoveredMenu(null);
                                  setShowGroupAccountIntro(false);
                                }}
                                className="text-left p-4 rounded-lg hover:bg-gray-50 transition-colors group"
                              >
                                <div className="font-semibold text-gray-900 text-lg mb-2 group-hover:text-emerald-600 transition-colors">
                                  {subItem.label}
                                </div>
                                <div className="text-sm text-gray-600 leading-relaxed">
                                  {subItem.description}
                                </div>
                              </button>
                            ))}
                          </div>
                        </div>
                      </div>
                    )}
                  </div>
                ))}
              </nav>

              {/* Right Section - 알림 + 사용자 */}
              <div className="flex items-center space-x-6">
                {/* 알림 아이콘 */}
                <div className="relative">
                  <button
                    onClick={() => setShowNotifications(!showNotifications)}
                    className="p-2 hover:bg-gray-100 rounded-full transition-colors relative"
                  >
                    <Bell className="h-6 w-6 text-gray-700" />
                    {unreadCount > 0 && (
                      <span className="absolute top-1 right-1 h-4 w-4 bg-red-500 text-white text-xs rounded-full flex items-center justify-center">
                        {unreadCount}
                      </span>
                    )}
                  </button>

                  {/* 알림 드롭다운 */}
                  {showNotifications && (
                    <div className="absolute right-0 mt-2 w-96 bg-white rounded-lg shadow-xl border border-gray-200 z-50 max-h-[500px] overflow-y-auto">
                      <div className="p-4 border-b border-gray-200">
                        <div className="flex items-center justify-between">
                          <h3 className="font-semibold text-gray-900">알림</h3>
                          {unreadCount > 0 && (
                            <span className="text-sm text-emerald-600">
                              {unreadCount}개의 새 알림
                            </span>
                          )}
                        </div>
                      </div>

                      <div className="divide-y divide-gray-100">
                        {notifications.length === 0 ? (
                          <div className="p-8 text-center text-gray-500">
                            알림이 없습니다
                          </div>
                        ) : (
                          notifications.map((notification) => (
                            <NotificationItem
                              key={notification.id}
                              notification={notification}
                              user={user}
                              setActiveTab={setActiveTab}
                              onAction={() => {
                                loadNotifications();
                                setShowNotifications(false);
                              }}
                            />
                          ))
                        )}
                      </div>
                    </div>
                  )}
                </div>

                {/* 사용자 메뉴 */}
                {isLoggedIn ? (
                  <div className="relative">
                    <button
                      onClick={() => setShowUserMenu(!showUserMenu)}
                      className="flex items-center space-x-2 hover:opacity-80 transition-opacity"
                    >
                      <User className="h-6 w-6 text-gray-700" />
                      <span className="text-sm font-medium text-gray-900 hidden md:inline">
                        {user?.name || "사용자"}님
                      </span>
                      <ChevronDown className="h-4 w-4 text-gray-500" />
                    </button>

                    {/* 사용자 드롭다운 메뉴 */}
                    {showUserMenu && (
                      <div className="absolute right-0 mt-2 w-48 bg-white rounded-lg shadow-xl border border-gray-200 z-50 py-2">
                        <button
                          onClick={() => {
                            setShowGroupAccountIntro(false);
                            setActiveTab("my-page");
                            setShowUserMenu(false);
                          }}
                          className="w-full text-left px-4 py-2 hover:bg-gray-50 transition-colors text-gray-700"
                        >
                          마이페이지
                        </button>
                        <button
                          onClick={() => {
                            handleLogout();
                            setShowUserMenu(false);
                          }}
                          className="w-full text-left px-4 py-2 hover:bg-gray-50 transition-colors text-red-600"
                        >
                          로그아웃
                        </button>
                      </div>
                    )}
                  </div>
                ) : (
                  <button
                    onClick={() => setShowAuth(true)}
                    className="px-4 py-2 bg-emerald-600 text-white rounded-lg hover:bg-emerald-700 transition-colors text-sm font-medium"
                  >
                    로그인
                  </button>
                )}

                {/* Mobile menu button */}
                <button
                  onClick={() => setShowMobileMenu(!showMobileMenu)}
                  className="md:hidden p-2 hover:bg-gray-100 rounded-lg transition-colors"
                >
                  {showMobileMenu ? (
                    <X className="h-6 w-6 text-gray-700" />
                  ) : (
                    <Menu className="h-6 w-6 text-gray-700" />
                  )}
                </button>
              </div>
            </div>
          </div>

          {/* Mobile Navigation */}
          {showMobileMenu && (
            <div className="md:hidden border-t border-gray-200 bg-white">
              <nav className="px-4 py-4 space-y-2">
                {navigationItems.map((item) => (
                  <button
                    key={item.id}
                    onClick={() => {
                      handleFeatureClick(item.id);
                      setShowMobileMenu(false);
                      setShowGroupAccountIntro(false);
                    }}
                    className={`w-full text-left px-4 py-3 rounded-lg transition-colors ${
                      item.id === "group-account"
                        ? "bg-emerald-50 text-emerald-600 font-medium"
                        : "text-gray-700 hover:bg-gray-50"
                    }`}
                  >
                    {item.label}
                  </button>
                ))}
              </nav>
            </div>
          )}
        </header>
        <main className="flex-1">
          <GroupAccountIntro
            onStartCreation={() => {
              console.log("모임통장 개설 시작 - 연결된 계좌 확인");
              // 🔥 연결된 계좌 확인 로직 재사용
              handleFeatureClick("create-group-account");
              setShowGroupAccountIntro(false);
            }}
            onCancel={() => {
              console.log("모임통장 서비스 소개 취소");
              setShowGroupAccountIntro(false);
              setActiveTab("home");
            }}
          />
        </main>
      </div>
    );
  }

  if (showGroupAccountFlow) {
    return (
      <div className="min-h-screen bg-gray-50 flex flex-col">
        {/* Header - 메인 페이지와 동일 */}
        <header className="bg-white shadow-sm sticky top-0 z-[100]">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
            <div className="flex justify-between items-center h-16">
              {/* Logo and Brand */}
              <button
                onClick={() => {
                  setShowGroupAccountFlow(false);
                  setActiveTab("home");
                }}
                className="flex items-center hover:opacity-80 transition-opacity cursor-pointer select-none"
              >
                <img
                  src="/bank-logos/HanaLogo.png"
                  alt="하나Future"
                  className="h-8 w-auto mr-3"
                />
                <span className="text-xl font-bold text-gray-900 select-none">
                  하나Future
                </span>
              </button>

              {/* Desktop Navigation */}
              <nav className="hidden md:flex items-center space-x-8">
                {navigationItems.map((item) => (
                  <button
                    key={item.id}
                    onClick={() => {
                      handleFeatureClick(item.id);
                      setShowGroupAccountFlow(false);
                    }}
                    className={`text-sm font-medium transition-colors ${
                      item.id === "group-account"
                        ? "text-teal-600"
                        : "text-gray-700 hover:text-gray-900"
                    }`}
                  >
                    {item.label}
                  </button>
                ))}
              </nav>

              {/* Right side - Notifications & User */}
              <div className="flex items-center space-x-4">
                {/* Notification Bell */}
                {isLoggedIn && (
                  <button
                    onClick={() => setShowNotificationModal(true)}
                    className="relative p-2 hover:bg-gray-100 rounded-lg transition-colors"
                  >
                    <Bell className="h-6 w-6 text-gray-700" />
                    {unreadCount > 0 && (
                      <span className="absolute top-1 right-1 w-2 h-2 bg-red-500 rounded-full"></span>
                    )}
                  </button>
                )}

                {/* User Menu */}
                {isLoggedIn ? (
                  <div className="relative">
                    <button
                      onClick={() => setShowUserMenu(!showUserMenu)}
                      className="flex items-center space-x-2 hover:bg-gray-100 px-3 py-2 rounded-lg transition-colors"
                    >
                      <span className="text-sm font-medium text-gray-900">
                        {user?.name || "사용자"}님
                      </span>
                      <ChevronDown className="h-4 w-4 text-gray-500" />
                    </button>

                    {showUserMenu && (
                      <div className="absolute right-0 mt-2 w-48 bg-white rounded-lg shadow-lg border border-gray-200 py-2 z-50">
                        <button
                          onClick={() => {
                            handleFeatureClick("my-page");
                            setShowUserMenu(false);
                            setShowGroupAccountFlow(false);
                          }}
                          className="w-full text-left px-4 py-2 hover:bg-gray-50 transition-colors text-gray-700"
                        >
                          마이페이지
                        </button>
                        <button
                          onClick={() => {
                            handleLogout();
                            setShowUserMenu(false);
                          }}
                          className="w-full text-left px-4 py-2 hover:bg-gray-50 transition-colors text-red-600"
                        >
                          로그아웃
                        </button>
                      </div>
                    )}
                  </div>
                ) : (
                  <button
                    onClick={() => setShowAuth(true)}
                    className="px-4 py-2 bg-emerald-600 text-white rounded-lg hover:bg-emerald-700 transition-colors text-sm font-medium"
                  >
                    로그인
                  </button>
                )}

                {/* Mobile menu button */}
                <button
                  onClick={() => setShowMobileMenu(!showMobileMenu)}
                  className="md:hidden p-2 hover:bg-gray-100 rounded-lg transition-colors"
                >
                  {showMobileMenu ? (
                    <X className="h-6 w-6 text-gray-700" />
                  ) : (
                    <Menu className="h-6 w-6 text-gray-700" />
                  )}
                </button>
              </div>
            </div>
          </div>

          {/* Mobile Navigation */}
          {showMobileMenu && (
            <div className="md:hidden border-t border-gray-200 bg-white">
              <nav className="px-4 py-4 space-y-2">
                {navigationItems.map((item) => (
                  <button
                    key={item.id}
                    onClick={() => {
                      handleFeatureClick(item.id);
                      setShowMobileMenu(false);
                      setShowGroupAccountFlow(false);
                    }}
                    className={`w-full text-left px-4 py-3 rounded-lg transition-colors ${
                      item.id === "group-account"
                        ? "bg-emerald-50 text-emerald-600 font-medium"
                        : "text-gray-700 hover:bg-gray-50"
                    }`}
                  >
                    {item.label}
                  </button>
                ))}
              </nav>
            </div>
          )}
        </header>

        {/* Hero Section - 컴포넌트 재사용 */}
        <GroupAccountHero />

        {/* Main Content */}
        <main className="flex-1">
          <ModernGroupAccountFlow
            onComplete={(data) => {
              console.log("모임통장 개설 완료:", data);
              setShowGroupAccountFlow(false);

              // nextAction에 따라 다른 페이지로 이동
              if (data.nextAction === "savings") {
                setActiveTab("savings");
              } else if (data.nextAction === "allowance-card") {
                setActiveTab("allowance-card");
              } else if (data.nextAction === "my-page") {
                setActiveTab("my-page");
              } else {
                // 기본값: 모임통장 관리 페이지
                setActiveTab("manage-group-account");
              }
            }}
            onCancel={() => {
              console.log("모임통장 개설 취소");
              setShowGroupAccountFlow(false);
              setActiveTab("home");
            }}
          />
        </main>
      </div>
    );
  }

  // 가족 추가 함수
  const handleAddFamilyMember = async () => {
    if (!newFamilyMember.name || !newFamilyMember.phoneNumber) {
      alert("이름과 전화번호를 모두 입력해주세요.");
      return;
    }

    try {
      const requestData = {
        name: newFamilyMember.name,
        phoneNumber: newFamilyMember.phoneNumber,
      };

      console.log("📤 가족 추가 요청:", requestData);

      const response = await fetch(
        `${API_BASE_URL}/family/members?userId=${user.id}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            ...(user?.token && { Authorization: `Bearer ${user.token}` }),
          },
          body: JSON.stringify(requestData),
        }
      );

      if (response.ok) {
        const result = await response.json();

        // 카카오톡 공유하기로 초대 메시지 발송
        sendFamilyInviteKakao(newFamilyMember.name);

        // 성공 모달 표시
        setShowFamilyAddModal(false);
        setShowFamilySuccessModal(true);
        setNewFamilyMember({
          name: "",
          phoneNumber: "",
        });
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

  // 카카오톡으로 가족 초대 메시지 발송
  const sendFamilyInviteKakao = (familyName) => {
    console.log("카카오톡 가족 초대 메시지 발송");

    // 가족 초대 링크 (invite=family 파라미터 추가)
    const inviteLink = `${window.location.origin}?invite=family`;

    // 카카오 SDK 확인
    if (window.Kakao && window.Kakao.isInitialized()) {
      console.log("카카오 SDK 사용 가능");

      try {
        window.Kakao.Share.sendDefault({
          objectType: "feed",
          content: {
            title: `${
              user?.name || "사용자"
            }님이 하나Future 가족으로 초대했습니다!`,
            description: `로그인 후 가족 초대를 수락하세요!`,
            imageUrl: `https://www.hanafn.com/assets/img/ko/info/img-hana-symbol.png`,
            link: {
              webUrl: inviteLink,
              mobileWebUrl: inviteLink,
            },
          },
          buttons: [
            {
              title: "초대 수락하기",
              link: {
                webUrl: inviteLink,
                mobileWebUrl: inviteLink,
              },
            },
          ],
        });

        console.log("카카오톡 공유 창 열림");
      } catch (error) {
        console.error("카카오톡 공유 오류:", error);
        // 카카오톡 공유 실패해도 가족 추가는 완료되었으므로 에러 무시
      }
    } else {
      console.log("카카오 SDK 없음 - 클립보드 복사");

      // 카카오 SDK가 없으면 클립보드에 복사
      const message = `${
        user?.name || "사용자"
      }님이 하나Future 가족으로 초대했습니다!\n\n로그인 후 가족 초대를 수락하세요!\n\n하나Future: ${inviteLink}`;

      navigator.clipboard
        .writeText(message)
        .then(() => {
          console.log("클립보드 복사 완료");
        })
        .catch((err) => {
          console.error("클립보드 복사 실패:", err);
        });
    }
  };

  // 적금 생성 플로우는 메인 대시보드 내에서 렌더링

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col">
      {/* Header */}
      <header className="bg-white shadow-sm sticky top-0 z-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <div className="flex items-center space-x-4">
              <button
                onClick={() => setActiveTab("home")}
                className="flex items-center space-x-3 hover:opacity-80 transition-opacity select-none"
              >
                <img
                  src="/bank-logos/HanaLogo.png"
                  alt="HanaBank"
                  className="h-8 w-8 object-contain"
                />
                <h1 className="text-xl font-bold text-gray-900 select-none">
                  하나Future
                </h1>
              </button>
            </div>

            {/* Desktop Navigation */}
            <nav className="hidden md:flex items-center space-x-8">
              {navigationItems.map((item) => (
                <div
                  key={item.id}
                  className="relative"
                  onMouseEnter={() => {
                    if (hoverTimeout) {
                      clearTimeout(hoverTimeout);
                      setHoverTimeout(null);
                    }
                    setHoveredMenu(item.id);
                  }}
                  onMouseLeave={() => {
                    const timeout = setTimeout(() => {
                      setHoveredMenu(null);
                    }, 150); // 150ms 지연
                    setHoverTimeout(timeout);
                  }}
                >
                  <button
                    onClick={() => handleFeatureClick(item.id)}
                    className={`text-base font-medium transition-colors ${
                      activeTab === item.id
                        ? "text-teal-600"
                        : "text-gray-700 hover:text-gray-900"
                    }`}
                  >
                    {item.label}
                  </button>

                  {/* 토스뱅크 스타일 슬라이드 드롭다운 메뉴 */}
                  {item.submenu && hoveredMenu === item.id && (
                    <div
                      className="dropdown-menu fixed top-16 left-0 right-0 w-full bg-white shadow-xl border-t border-gray-200 z-50 transform transition-all duration-300 ease-out"
                      onMouseEnter={() => {
                        if (hoverTimeout) {
                          clearTimeout(hoverTimeout);
                          setHoverTimeout(null);
                        }
                        setHoveredMenu(item.id);
                      }}
                      onMouseLeave={() => {
                        const timeout = setTimeout(() => {
                          setHoveredMenu(null);
                        }, 150);
                        setHoverTimeout(timeout);
                      }}
                    >
                      <div className="max-w-7xl mx-auto px-8 py-8">
                        <div className="grid grid-cols-4 gap-8">
                          {item.submenu.map((subItem) => (
                            <button
                              key={subItem.id}
                              onClick={() => {
                                handleFeatureClick(subItem.id);
                                setHoveredMenu(null);
                              }}
                              className="text-left p-4 rounded-lg hover:bg-gray-50 transition-colors group"
                            >
                              <div className="font-semibold text-gray-900 text-lg mb-2 group-hover:text-teal-600 transition-colors">
                                {subItem.label}
                              </div>
                              <div className="text-sm text-gray-600 leading-relaxed">
                                {subItem.description}
                              </div>
                            </button>
                          ))}
                        </div>
                      </div>
                    </div>
                  )}
                </div>
              ))}
            </nav>

            {/* User Menu */}
            <div className="flex items-center space-x-4">
              {isLoggedIn ? (
                <div className="flex items-center space-x-3">
                  {/* 알림 벨 아이콘 */}
                  <button
                    onClick={() => setShowNotifications(!showNotifications)}
                    className="relative p-2 text-gray-600 hover:text-emerald-600 hover:bg-emerald-50 rounded-lg transition-colors"
                  >
                    <Bell className="w-5 h-5" />
                    {unreadCount > 0 && (
                      <span className="absolute -top-1 -right-1 w-5 h-5 bg-red-500 text-white text-xs font-bold rounded-full flex items-center justify-center">
                        {unreadCount > 9 ? "9+" : unreadCount}
                      </span>
                    )}
                  </button>

                  <button
                    onClick={() => setActiveTab("my-page")}
                    className="flex items-center space-x-2 text-gray-600 hover:text-gray-900 px-3 py-2 rounded-lg hover:bg-gray-100 transition-colors"
                  >
                    <User className="h-4 w-4" />
                    <span className="text-sm font-medium">
                      {user?.name || "사용자"}님
                    </span>
                  </button>
                  <button
                    onClick={handleLogout}
                    className="flex items-center space-x-2 text-gray-600 hover:text-gray-900 px-3 py-2 rounded-lg hover:bg-gray-100 transition-colors"
                  >
                    <LogOut className="h-4 w-4" />
                    <span className="hidden sm:inline">로그아웃</span>
                  </button>
                </div>
              ) : (
                <button
                  onClick={() => setShowAuth(true)}
                  className="bg-teal-600 text-white px-4 py-2 rounded-lg hover:bg-teal-700 transition-colors font-medium"
                >
                  로그인
                </button>
              )}

              <button
                onClick={() => setShowMobileMenu(!showMobileMenu)}
                className="md:hidden text-gray-600 hover:text-gray-900"
              >
                <Menu className="h-6 w-6" />
              </button>
            </div>
          </div>
        </div>
      </header>

      {/* 알림 드롭다운 */}
      {showNotifications && isLoggedIn && (
        <div className="fixed top-20 right-4 w-96 max-h-[600px] bg-white rounded-2xl shadow-2xl border border-gray-200 z-50 overflow-hidden">
          <div className="bg-gradient-to-r from-emerald-500 to-teal-500 p-4">
            <div className="flex items-center justify-between">
              <h3 className="text-lg font-bold text-white">알림</h3>
              <button
                onClick={() => setShowNotifications(false)}
                className="text-white hover:bg-white/20 rounded-lg p-1 transition-colors"
              >
                <X className="w-5 h-5" />
              </button>
            </div>
          </div>

          <div className="overflow-y-auto max-h-[500px]">
            {notifications.length === 0 ? (
              <div className="p-12 text-center">
                <Bell className="w-16 h-16 text-gray-300 mx-auto mb-4" />
                <p className="text-gray-500">새로운 알림이 없습니다</p>
              </div>
            ) : (
              <div className="divide-y divide-gray-100">
                {notifications.map((notification) => (
                  <NotificationItem
                    key={notification.id}
                    notification={notification}
                    user={user}
                    setActiveTab={setActiveTab}
                    onAction={() => {
                      loadNotifications();
                      setShowNotifications(false);
                    }}
                  />
                ))}
              </div>
            )}
          </div>
        </div>
      )}

      {/* Mobile Menu */}
      {showMobileMenu && (
        <div className="md:hidden fixed inset-0 bg-white z-40 flex flex-col">
          <div className="flex justify-between items-center p-4 border-b">
            <h2 className="text-xl font-bold">메뉴</h2>
            <button onClick={() => setShowMobileMenu(false)}>
              <X className="h-6 w-6" />
            </button>
          </div>
          <nav className="flex flex-col p-4 space-y-2">
            <button
              onClick={() => {
                setActiveTab("home");
                setShowMobileMenu(false);
              }}
              className={`flex items-center px-4 py-3 rounded-lg text-lg font-medium transition-colors w-full ${
                activeTab === "home"
                  ? "bg-teal-100 text-teal-700"
                  : "text-gray-700 hover:text-gray-900 hover:bg-gray-100"
              }`}
            >
              홈
            </button>
            {navigationItems.map((item) => (
              <div key={item.id}>
                <button
                  onClick={() => {
                    handleFeatureClick(item.id);
                    setShowMobileMenu(false);
                  }}
                  className={`flex items-center px-4 py-3 rounded-lg text-lg font-medium transition-colors w-full ${
                    activeTab === item.id
                      ? "bg-teal-100 text-teal-700"
                      : "text-gray-700 hover:text-gray-900 hover:bg-gray-100"
                  }`}
                >
                  {item.label}
                </button>
                {item.submenu && (
                  <div className="ml-4 mt-2 space-y-1">
                    {item.submenu.map((subItem) => (
                      <button
                        key={subItem.id}
                        onClick={() => {
                          handleFeatureClick(subItem.id);
                          setShowMobileMenu(false);
                        }}
                        className="block px-4 py-2 text-sm text-gray-600 hover:text-gray-900 hover:bg-gray-50 rounded-lg w-full text-left"
                      >
                        {subItem.label}
                      </button>
                    ))}
                  </div>
                )}
              </div>
            ))}
          </nav>
        </div>
      )}

      {/* Main Content */}
      <main className="flex-1">
        {activeTab === "home" &&
          !showOpenBankingConsent &&
          !showUserVerification &&
          !showBankSelection &&
          !showAccountSelection && (
            <section>
              {/* Integrated Hero Section - Full Width */}
              <div className="relative h-[500px] overflow-hidden bg-gradient-to-br from-emerald-400 via-emerald-500 to-teal-600">
                <div className="absolute inset-0">
                  {/* Subtle overlay for better text readability */}
                  <div className="absolute inset-0 bg-black/5"></div>

                  <div className="relative h-full flex items-center">
                    <div className="max-w-6xl mx-auto px-8 w-full">
                      <div className="flex flex-col lg:flex-row items-center justify-between h-full">
                        {/* Main Content - Text focused */}
                        <div className="flex-1 text-white space-y-6 max-w-2xl">
                          <div className="space-y-4">
                            <h1 className="text-3xl lg:text-4xl font-bold leading-tight">
                              {heroSlides[currentSlide].title}
                            </h1>
                            <p className="text-lg lg:text-xl text-white/90 leading-relaxed">
                              {heroSlides[currentSlide].subtitle}
                            </p>
                            <p className="text-base text-white/70 leading-relaxed">
                              {heroSlides[currentSlide].description}
                            </p>
                          </div>

                          {/* Simple CTA Button */}
                          {!isLoggedIn && (
                            <button
                              onClick={() => setShowAuth(true)}
                              className="bg-white text-emerald-600 px-8 py-4 rounded-xl hover:bg-white/95 transition-all duration-200 font-semibold flex items-center space-x-2 shadow-lg mt-8"
                            >
                              <span>지금 시작하기</span>
                              <ArrowRight className="w-5 h-5" />
                            </button>
                          )}
                        </div>

                        {/* Right side - Hana Character */}
                        <div className="hidden lg:block flex-shrink-0 ml-16">
                          <div className="relative w-96 h-96">
                            {/* Character Image with fallback */}
                            <div className="w-full h-full flex items-center justify-center">
                              <img
                                src={heroSlides[currentSlide].character}
                                alt={heroSlides[currentSlide].characterAlt}
                                className="w-full h-full object-contain drop-shadow-2xl transition-all duration-500 transform hover:scale-105"
                                style={{
                                  filter:
                                    "drop-shadow(0 10px 20px rgba(0,0,0,0.1))",
                                }}
                                onError={(e) => {
                                  console.log(
                                    "Image failed to load:",
                                    heroSlides[currentSlide].character
                                  );
                                  e.target.style.display = "none";
                                  // Show fallback emoji
                                  const fallback =
                                    document.createElement("div");
                                  fallback.className = "text-6xl";
                                  fallback.textContent = "🌱";
                                  e.target.parentNode.appendChild(fallback);
                                }}
                                onLoad={() => {
                                  console.log(
                                    "Image loaded successfully:",
                                    heroSlides[currentSlide].character
                                  );
                                }}
                              />
                            </div>

                            {/* Subtle background decoration */}
                            <div className="absolute inset-0 bg-white/5 rounded-full transform -translate-x-4 translate-y-4 -z-10"></div>
                            <div className="absolute inset-0 bg-white/3 rounded-full transform translate-x-2 -translate-y-2 -z-10"></div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>

                  {/* Simple Slide Controls */}
                  <div className="absolute bottom-6 left-1/2 transform -translate-x-1/2 flex items-center space-x-2">
                    <button
                      onClick={prevSlide}
                      className="w-8 h-8 bg-white/80 rounded-full flex items-center justify-center hover:bg-white transition-all duration-200"
                    >
                      <ChevronLeft className="w-4 h-4 text-gray-600" />
                    </button>

                    <div className="flex space-x-1">
                      {heroSlides.map((_, index) => (
                        <button
                          key={index}
                          onClick={() => setCurrentSlide(index)}
                          className={`w-2 h-2 rounded-full transition-all duration-300 ${
                            index === currentSlide
                              ? "bg-white w-4"
                              : "bg-white/60 hover:bg-white/80"
                          }`}
                        />
                      ))}
                    </div>

                    <button
                      onClick={nextSlide}
                      className="w-8 h-8 bg-white/80 rounded-full flex items-center justify-center hover:bg-white transition-all duration-200"
                    >
                      <ChevronRight className="w-4 h-4 text-gray-600" />
                    </button>
                  </div>
                </div>
              </div>

              {/* 서비스 카드 - 가로 일렬 */}
              <div className="bg-white py-12">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                  <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
                    {/* 모임통장 */}
                    <div
                      onClick={() => handleFeatureClick("group-account")}
                      className="group bg-gradient-to-br from-emerald-50 to-teal-50 p-6 rounded-2xl cursor-pointer transform hover:scale-105 transition-all duration-300 shadow-lg hover:shadow-xl border border-emerald-100"
                    >
                      <div className="flex flex-col items-center text-center space-y-4">
                        <img
                          src="/hana3dIcon/hanaIcon3d_3_47.png"
                          alt="모임통장"
                          className="w-16 h-16"
                        />
                        <h3 className="text-xl font-bold text-gray-900">
                          모임통장
                        </h3>
                        <p className="text-gray-600 text-sm leading-relaxed">
                          부부가 함께 관리하는 모임통장
                        </p>
                        <div className="flex items-center text-emerald-600 group-hover:text-emerald-700 transition-colors">
                          <span className="text-sm font-medium">시작하기</span>
                          <ArrowRight className="w-4 h-4 ml-1 group-hover:translate-x-1 transition-transform" />
                        </div>
                      </div>
                    </div>

                    {/* 함께 적금 */}
                    <div
                      onClick={() => handleFeatureClick("savings")}
                      className="group bg-gradient-to-br from-blue-50 to-indigo-50 p-6 rounded-2xl cursor-pointer transform hover:scale-105 transition-all duration-300 shadow-lg hover:shadow-xl border border-blue-100"
                    >
                      <div className="flex flex-col items-center text-center space-y-4">
                        <img
                          src="/hana3dIcon/hanaIcon3d_2_51.png"
                          alt="함께 적금"
                          className="w-16 h-16"
                        />
                        <h3 className="text-xl font-bold text-gray-900">
                          함께 적금
                        </h3>
                        <p className="text-gray-600 text-sm leading-relaxed">
                          부부가 함께하는 목표 달성 저축
                        </p>
                        <div className="flex items-center text-blue-600 group-hover:text-blue-700 transition-colors">
                          <span className="text-sm font-medium">시작하기</span>
                          <ArrowRight className="w-4 h-4 ml-1 group-hover:translate-x-1 transition-transform" />
                        </div>
                      </div>
                    </div>
                    {/* 아이카드 */}
                    <div
                      onClick={() => handleFeatureClick("allowance-card")}
                      className="group bg-gradient-to-br from-purple-50 to-pink-50 p-6 rounded-2xl cursor-pointer transform hover:scale-105 transition-all duration-300 shadow-lg hover:shadow-xl border border-purple-100"
                    >
                      <div className="flex flex-col items-center text-center space-y-4">
                        <img
                          src="/hana3dIcon/hanaIcon3d_2_13.png"
                          alt="아이카드"
                          className="w-16 h-16"
                        />
                        <h3 className="text-xl font-bold text-gray-900">
                          아이카드
                        </h3>
                        <p className="text-gray-600 text-sm leading-relaxed">
                          자녀 용돈 관리
                        </p>
                        <div className="flex items-center text-purple-600 group-hover:text-purple-700 transition-colors">
                          <span className="text-sm font-medium">시작하기</span>
                          <ArrowRight className="w-4 h-4 ml-1 group-hover:translate-x-1 transition-transform" />
                        </div>
                      </div>
                    </div>

                    {/* 정부 혜택 */}
                    <div
                      onClick={() => handleFeatureClick("government-benefits")}
                      className="group bg-gradient-to-br from-orange-50 to-red-50 p-6 rounded-2xl cursor-pointer transform hover:scale-105 transition-all duration-300 shadow-lg hover:shadow-xl border border-orange-100"
                    >
                      <div className="flex flex-col items-center text-center space-y-4">
                        <img
                          src="/hana3dIcon/hanaIcon3d_87.png"
                          alt="정부 혜택"
                          className="w-16 h-16"
                        />
                        <h3 className="text-xl font-bold text-gray-900">
                          정부 혜택
                        </h3>
                        <p className="text-gray-600 text-sm leading-relaxed">
                          가족 맞춤 정부 지원 혜택
                        </p>
                        <div className="flex items-center text-orange-600 group-hover:text-orange-700 transition-colors">
                          <span className="text-sm font-medium">시작하기</span>
                          <ArrowRight className="w-4 h-4 ml-1 group-hover:translate-x-1 transition-transform" />
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              {/* 가족 추가 CTA 배너 */}
              <div className="bg-gray-50 py-12">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                  <div className="relative bg-gradient-to-br from-emerald-50 to-teal-50 rounded-3xl p-8 lg:p-12 border border-emerald-200 hover:shadow-lg transition-all duration-300 overflow-hidden">
                    {/* 배경 이미지 (워터마크 스타일) */}
                    <div className="absolute inset-0 flex items-center justify-center opacity-[0.12] pointer-events-none">
                      <img
                        src="/hanacharacter/hanafamily.png"
                        alt="가족 배경"
                        className="w-full h-full object-contain"
                      />
                    </div>

                    {/* 콘텐츠 */}
                    <div className="relative z-10 flex flex-col lg:flex-row items-center justify-between gap-6">
                      {/* 왼쪽: 텍스트 */}
                      <div className="text-center lg:text-left space-y-3">
                        <h3 className="text-3xl lg:text-4xl font-bold text-gray-900">
                          가족과 함께 시작하세요!
                        </h3>
                        <p className="text-lg text-gray-600">
                          가족을 추가하면 모임통장, 아이카드, 함께 적금 등
                          <br className="hidden sm:block" />
                          모든 서비스를 이용할 수 있어요
                        </p>
                      </div>

                      {/* 오른쪽: 버튼 */}
                      <button
                        onClick={() => {
                          if (!user) {
                            setShowLoginRequiredModal(true);
                          } else {
                            setShowFamilyAddModal(true);
                          }
                        }}
                        className="flex-shrink-0 inline-flex items-center gap-2 bg-gradient-to-r from-emerald-600 to-teal-600 text-white px-6 py-3 rounded-xl font-bold hover:from-emerald-700 hover:to-teal-700 transition-all duration-300 shadow-lg hover:shadow-xl hover:scale-105 transform"
                      >
                        <Users className="w-5 h-5" />
                        <span>가족 추가하기</span>
                        <ArrowRight className="w-5 h-5" />
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </section>
          )}

        {/* Other Tabs Content */}
        {activeTab === "my-accounts" &&
          !showOpenBankingConsent &&
          !showUserVerification &&
          !showBankSelection && (
            <div className="space-y-6">
              <div className="text-center py-12">
                <h2 className="text-3xl font-bold text-gray-900 mb-3">
                  내 계좌
                </h2>
                <p className="text-lg text-gray-600 mb-8">
                  오픈뱅킹을 통해 안전하게 계좌를 연결하고 관리하세요
                </p>

                {/* 연결된 계좌 표시 또는 계좌 추가 안내 */}
                {connectedAccounts.length > 0 ? (
                  // 연결된 계좌가 있을 때
                  <div className="max-w-4xl mx-auto">
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
                      {connectedAccounts.map((account, index) => (
                        <div
                          key={
                            account.id ||
                            account.account_num ||
                            account.accountNumber ||
                            `account-${index}`
                          }
                          className="bg-white rounded-2xl p-6 border border-gray-200 shadow-sm hover:shadow-md transition-shadow"
                        >
                          <div className="flex items-center justify-between mb-4">
                            <h3 className="font-semibold text-lg text-gray-900">
                              {account.bankName || "하나은행"}
                            </h3>
                            <span className="text-sm text-gray-500">
                              {account.productSubName ||
                                account.accountType ||
                                "보통예금"}
                            </span>
                          </div>
                          <p className="text-gray-600 mb-2">
                            {account.accountNum ||
                              account.accountNumber ||
                              "계좌번호"}
                          </p>
                          <p className="text-sm text-gray-500 mb-2">
                            {account.productName ||
                              account.accountAlias ||
                              "상품명"}
                          </p>
                          <p className="text-2xl font-bold text-gray-900">
                            {(() => {
                              const balance =
                                account.balanceAmt || account.balance;
                              if (!balance) return "0";
                              const numBalance =
                                typeof balance === "string"
                                  ? parseFloat(balance)
                                  : balance;
                              return isNaN(numBalance)
                                ? "0"
                                : numBalance.toLocaleString();
                            })()}
                            원
                          </p>
                        </div>
                      ))}
                    </div>

                    {/* 계좌 추가 버튼 */}
                    <button
                      onClick={() => {
                        handleOpenBankingStart();
                      }}
                      className="bg-emerald-600 hover:bg-emerald-700 text-white px-6 py-3 rounded-xl transition-colors font-medium"
                    >
                      + 계좌 추가하기
                    </button>
                  </div>
                ) : (
                  // 연결된 계좌가 없을 때
                  <div className="max-w-lg mx-auto">
                    <div className="bg-gradient-to-br from-gray-50 to-gray-100 rounded-2xl p-8 text-center relative overflow-hidden">
                      {/* 배경 장식 */}
                      <div className="absolute top-0 right-0 w-32 h-32 bg-gradient-to-br from-emerald-200/30 to-teal-200/30 rounded-full -translate-y-16 translate-x-16"></div>
                      <div className="absolute bottom-0 left-0 w-24 h-24 bg-gradient-to-tr from-blue-200/30 to-purple-200/30 rounded-full translate-y-12 -translate-x-12"></div>

                      <div className="relative z-10">
                        <div className="w-16 h-16 mx-auto mb-6 relative">
                          <img
                            src="/hanacharacter/hanacharacter6.png"
                            alt="하나 캐릭터"
                            className="w-full h-full object-contain"
                            onError={(e) => {
                              e.target.style.display = "none";
                              e.target.nextSibling.style.display = "flex";
                            }}
                          />
                          <div
                            className="w-full h-full bg-gray-200 rounded-full flex items-center justify-center"
                            style={{ display: "none" }}
                          >
                            <CreditCard className="w-8 h-8 text-gray-400" />
                          </div>
                        </div>

                        <h3 className="text-xl font-semibold text-gray-900 mb-3">
                          연결된 계좌가 없습니다
                        </h3>
                        <p className="text-gray-600 mb-8 leading-relaxed">
                          은행을 연결하여 계좌 정보를 안전하게 가져오세요
                          <br />
                          <span className="text-sm text-gray-500">
                            하나은행, 국민은행, 신한은행 등 다양한 은행 지원
                          </span>
                        </p>

                        <button
                          onClick={() => {
                            // 오픈뱅킹 플로우 시작
                            handleOpenBankingStart();
                          }}
                          className="bg-gradient-to-r from-emerald-600 to-teal-600 text-white px-8 py-4 rounded-xl hover:from-emerald-700 hover:to-teal-700 transition-all duration-300 font-semibold text-lg shadow-lg hover:shadow-xl transform hover:-translate-y-1"
                        >
                          <div className="flex items-center gap-3">
                            <Plus className="w-5 h-5" />
                            계좌 추가하기
                          </div>
                        </button>
                      </div>
                    </div>
                  </div>
                )}
              </div>
            </div>
          )}

        {activeTab === "group-account" && (
          <>
            {/* 전체 헤더 - 함께 적금과 동일 */}
            <header className="bg-white shadow-sm border-b border-gray-200 sticky top-0 z-[100]">
              <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
                <div className="flex items-center justify-between">
                  {/* 로고 */}
                  <div className="flex-shrink-0">
                    <button
                      onClick={() => setActiveTab("home")}
                      className="flex items-center space-x-3 hover:opacity-80 transition-opacity"
                    >
                      <img
                        src="/bank-logos/HanaLogo.png"
                        alt="HanaBank"
                        className="h-8 w-8 object-contain"
                      />
                      <h1 className="text-xl font-bold text-gray-900">
                        하나Future
                      </h1>
                    </button>
                  </div>

                  {/* Desktop Navigation */}
                  <nav className="hidden md:flex items-center space-x-8">
                    {navigationItems.map((item) => (
                      <div
                        key={item.id}
                        className="relative"
                        onMouseEnter={() => {
                          if (hoverTimeout) {
                            clearTimeout(hoverTimeout);
                            setHoverTimeout(null);
                          }
                          setHoveredMenu(item.id);
                        }}
                        onMouseLeave={() => {
                          const timeout = setTimeout(() => {
                            setHoveredMenu(null);
                          }, 150);
                          setHoverTimeout(timeout);
                        }}
                      >
                        <button
                          onClick={() => {
                            handleFeatureClick(item.id);
                          }}
                          className={`text-base font-medium transition-colors ${
                            item.id === "group-account"
                              ? "text-emerald-600"
                              : "text-gray-700 hover:text-gray-900"
                          }`}
                        >
                          {item.label}
                        </button>

                        {/* 드롭다운 메뉴 */}
                        {item.submenu && hoveredMenu === item.id && (
                          <div
                            className="dropdown-menu fixed top-16 left-0 right-0 w-full bg-white shadow-xl border-t border-gray-200 z-50 transform transition-all duration-300 ease-out"
                            onMouseEnter={() => {
                              if (hoverTimeout) {
                                clearTimeout(hoverTimeout);
                                setHoverTimeout(null);
                              }
                              setHoveredMenu(item.id);
                            }}
                            onMouseLeave={() => {
                              const timeout = setTimeout(() => {
                                setHoveredMenu(null);
                              }, 150);
                              setHoverTimeout(timeout);
                            }}
                          >
                            <div className="max-w-7xl mx-auto px-8 py-8">
                              <div className="grid grid-cols-4 gap-8">
                                {item.submenu.map((subItem) => (
                                  <button
                                    key={subItem.id}
                                    onClick={() => {
                                      handleFeatureClick(subItem.id);
                                      setHoveredMenu(null);
                                    }}
                                    className="text-left p-4 rounded-lg hover:bg-gray-50 transition-colors group"
                                  >
                                    <div className="font-semibold text-gray-900 text-lg mb-2 group-hover:text-emerald-600 transition-colors">
                                      {subItem.label}
                                    </div>
                                    <div className="text-sm text-gray-600 leading-relaxed">
                                      {subItem.description}
                                    </div>
                                  </button>
                                ))}
                              </div>
                            </div>
                          </div>
                        )}
                      </div>
                    ))}
                  </nav>

                  {/* Right Section - 알림 + 사용자 */}
                  <div className="flex items-center space-x-6">
                    {/* 알림 아이콘 */}
                    <div className="relative">
                      <button
                        onClick={() => setShowNotifications(!showNotifications)}
                        className="p-2 hover:bg-gray-100 rounded-full transition-colors relative"
                      >
                        <Bell className="h-6 w-6 text-gray-700" />
                        {unreadCount > 0 && (
                          <span className="absolute top-1 right-1 h-4 w-4 bg-red-500 text-white text-xs rounded-full flex items-center justify-center">
                            {unreadCount}
                          </span>
                        )}
                      </button>

                      {/* 알림 드롭다운 */}
                      {showNotifications && (
                        <div className="absolute right-0 mt-2 w-96 bg-white rounded-lg shadow-xl border border-gray-200 z-50 max-h-[500px] overflow-y-auto">
                          <div className="p-4 border-b border-gray-200">
                            <div className="flex items-center justify-between">
                              <h3 className="font-semibold text-gray-900">
                                알림
                              </h3>
                              {unreadCount > 0 && (
                                <span className="text-sm text-emerald-600">
                                  {unreadCount}개의 새 알림
                                </span>
                              )}
                            </div>
                          </div>

                          <div className="divide-y divide-gray-100">
                            {notifications.length === 0 ? (
                              <div className="p-8 text-center text-gray-500">
                                알림이 없습니다
                              </div>
                            ) : (
                              notifications.map((notification) => (
                                <NotificationItem
                                  key={notification.id}
                                  notification={notification}
                                  user={user}
                                  setActiveTab={setActiveTab}
                                  onAction={() => {
                                    loadNotifications();
                                    setShowNotifications(false);
                                  }}
                                />
                              ))
                            )}
                          </div>
                        </div>
                      )}
                    </div>

                    {/* 사용자 메뉴 */}
                    {isLoggedIn ? (
                      <div className="relative">
                        <button
                          onClick={() => setShowUserMenu(!showUserMenu)}
                          className="flex items-center space-x-2 hover:opacity-80 transition-opacity"
                        >
                          <User className="h-6 w-6 text-gray-700" />
                          <span className="text-sm font-medium text-gray-900 hidden md:inline">
                            {user?.name || "사용자"}님
                          </span>
                          <ChevronDown className="h-4 w-4 text-gray-500" />
                        </button>

                        {/* 사용자 드롭다운 메뉴 */}
                        {showUserMenu && (
                          <div className="absolute right-0 mt-2 w-48 bg-white rounded-lg shadow-xl border border-gray-200 z-50 py-2">
                            <button
                              onClick={() => {
                                setActiveTab("my-page");
                                setShowUserMenu(false);
                              }}
                              className="w-full text-left px-4 py-2 hover:bg-gray-50 transition-colors text-gray-700"
                            >
                              마이페이지
                            </button>
                            <button
                              onClick={() => {
                                handleLogout();
                                setShowUserMenu(false);
                              }}
                              className="w-full text-left px-4 py-2 hover:bg-gray-50 transition-colors text-red-600"
                            >
                              로그아웃
                            </button>
                          </div>
                        )}
                      </div>
                    ) : (
                      <button
                        onClick={() => setShowAuth(true)}
                        className="px-4 py-2 bg-emerald-600 text-white rounded-lg hover:bg-emerald-700 transition-colors text-sm font-medium"
                      >
                        로그인
                      </button>
                    )}

                    {/* Mobile menu button */}
                    <button
                      onClick={() => setShowMobileMenu(!showMobileMenu)}
                      className="md:hidden p-2 hover:bg-gray-100 rounded-lg transition-colors"
                    >
                      {showMobileMenu ? (
                        <X className="h-6 w-6 text-gray-700" />
                      ) : (
                        <Menu className="h-6 w-6 text-gray-700" />
                      )}
                    </button>
                  </div>
                </div>
              </div>

              {/* Mobile Navigation */}
              {showMobileMenu && (
                <div className="md:hidden border-t border-gray-200 bg-white">
                  <nav className="px-4 py-4 space-y-2">
                    {navigationItems.map((item) => (
                      <button
                        key={item.id}
                        onClick={() => {
                          handleFeatureClick(item.id);
                          setShowMobileMenu(false);
                        }}
                        className={`w-full text-left px-4 py-3 rounded-lg transition-colors ${
                          item.id === "group-account"
                            ? "bg-emerald-50 text-emerald-600 font-medium"
                            : "text-gray-700 hover:bg-gray-50"
                        }`}
                      >
                        {item.label}
                      </button>
                    ))}
                  </nav>
                </div>
              )}
            </header>

            {/* 모임통장 관리 컴포넌트 */}
            <GroupAccountManagement />
          </>
        )}

        {activeTab === "bank-selection" && (
          <BankSelection
            onBankSelect={(selectedBanks) => {
              // 은행 선택 완료 후 내 계좌 페이지로 돌아가기
              setActiveTab("my-accounts");
            }}
            onBack={() => setActiveTab("my-accounts")}
            userInfo={user}
          />
        )}

        {activeTab === "allowance-card" && (
          <ChildCardManagement onBack={() => setActiveTab("home")} />
        )}

        {activeTab === "savings" && !showSavingsCreationFlow && (
          <SavingsProducts
            onStartTogetherFlow={() => setShowSavingsCreationFlow(true)}
          />
        )}

        {activeTab === "savings" && showSavingsCreationFlow && (
          <EnhancedSavingsFlow
            onComplete={(data) => {
              console.log("적금 가입 완료:", data);
              setShowSavingsCreationFlow(false);
              setActiveTab("savings");
            }}
            onCancel={() => {
              console.log("적금 가입 취소");
              setShowSavingsCreationFlow(false);
              setActiveTab("savings");
            }}
            userInfo={user}
          />
        )}

        {activeTab === "benefits" && <GovernmentBenefits />}

        {activeTab === "bookmarks" && <BookmarksPage />}

        {activeTab === "manage-group-account" && <GroupAccountManagement />}

        {activeTab === "my-page" && (
          <MyPage onBack={() => setActiveTab("home")} />
        )}
      </main>

      {/* Login Required Modal */}
      {showLoginModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl p-8 max-w-md w-full">
            <div className="text-center">
              <div className="w-16 h-16 bg-teal-100 rounded-full flex items-center justify-center mx-auto mb-4">
                <Lock className="h-8 w-8 text-teal-600" />
              </div>
              <h3 className="text-xl font-bold text-gray-900 mb-2">
                로그인이 필요합니다
              </h3>
              <p className="text-gray-600 mb-6">
                이 기능을 사용하려면 먼저 로그인해주세요.
              </p>
              <div className="flex space-x-3">
                <button
                  onClick={() => setShowLoginModal(false)}
                  className="flex-1 px-4 py-2 text-gray-600 border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors"
                >
                  취소
                </button>
                <button
                  onClick={() => {
                    setShowLoginModal(false);
                    setShowAuth(true);
                  }}
                  className="flex-1 px-4 py-2 bg-teal-600 text-white rounded-lg hover:bg-teal-700 transition-colors"
                >
                  로그인
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* 오픈뱅킹 플로우 - 헤더 포함 */}
      {(showOpenBankingConsent ||
        showUserVerification ||
        showBankSelection ||
        showAccountSelection) && (
        <div className="absolute inset-0 z-50 bg-white min-h-screen overflow-y-auto">
          {/* 전체 헤더 - 모임통장과 동일 */}
          <header className="bg-white shadow-sm sticky top-0 z-50">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
              <div className="flex items-center justify-between h-16">
                {/* Left: Logo */}
                <div className="flex items-center space-x-3 select-none">
                  <div className="w-10 h-10">
                    <img
                      src="/bank-logos/HanaLogo.png"
                      alt="하나은행"
                      className="w-full h-full object-contain"
                    />
                  </div>
                  <span className="font-heading font-bold text-xl text-gray-900 select-none">
                    하나Future
                  </span>
                </div>

                {/* Center: Navigation */}
                <nav className="hidden md:flex items-center space-x-8">
                  {navigationItems.map((item) => (
                    <button
                      key={item.id}
                      onClick={() => {
                        handleFlowCancel(); // 오픈뱅킹 플로우 취소
                        handleFeatureClick(item.id);
                      }}
                      className={`text-base font-medium transition-colors ${
                        item.id === "group-account"
                          ? "text-teal-600"
                          : "text-gray-700 hover:text-gray-900"
                      }`}
                    >
                      {item.label}
                    </button>
                  ))}
                </nav>

                {/* Right: User Menu */}
                {user ? (
                  <div className="flex items-center space-x-4">
                    {/* Notification Bell */}
                    <button
                      onClick={() => {
                        handleFlowCancel(); // 오픈뱅킹 플로우 취소
                        setShowNotificationPanel(!showNotificationPanel);
                      }}
                      className="relative p-2 text-gray-600 hover:bg-gray-100 rounded-lg transition-colors"
                    >
                      <Bell className="h-5 w-5" />
                      {unreadCount > 0 && (
                        <span className="absolute -top-1 -right-1 bg-red-500 text-white text-xs rounded-full h-5 w-5 flex items-center justify-center font-bold">
                          {unreadCount > 9 ? "9+" : unreadCount}
                        </span>
                      )}
                    </button>

                    {/* User Dropdown */}
                    <div className="relative">
                      <button
                        onClick={() => setShowUserMenu(!showUserMenu)}
                        className="flex items-center space-x-2 p-2 hover:bg-gray-100 rounded-lg transition-colors"
                      >
                        <User className="h-5 w-5 text-gray-600" />
                        <span className="text-sm font-medium text-gray-700">
                          {user.name}
                        </span>
                        <ChevronDown className="h-4 w-4 text-gray-600" />
                      </button>
                    </div>
                  </div>
                ) : (
                  <div className="flex items-center space-x-2">
                    <button
                      onClick={() => setShowAuth(true)}
                      className="px-4 py-2 text-gray-700 hover:text-gray-900 font-medium"
                    >
                      로그인
                    </button>
                  </div>
                )}
              </div>
            </div>
          </header>

          {/* Hero Section - 컴포넌트 재사용 */}
          <GroupAccountHero />

          {/* 오픈뱅킹 플로우 컨텐츠 */}
          <main className="flex-1">
            {showOpenBankingConsent &&
              !showUserVerification &&
              !showBankSelection &&
              !showAccountSelection && (
                <div>
                  {console.log("🔒 OpenBankingConsent")}
                  <OpenBankingConsent
                    onConsent={handleConsentComplete}
                    onCancel={handleFlowCancel}
                  />
                </div>
              )}

            {!showOpenBankingConsent &&
              showUserVerification &&
              !showBankSelection &&
              !showAccountSelection && (
                <div>
                  {console.log("👤 IdentityVerification")}
                  <IdentityVerification
                    onVerificationComplete={handleVerificationComplete}
                    onBack={handleFlowCancel}
                  />
                </div>
              )}

            {!showOpenBankingConsent &&
              !showUserVerification &&
              showBankSelection &&
              !showAccountSelection && (
                <div>
                  {console.log("🏦 BankSelection")}
                  <BankSelection
                    onBankSelect={handleBankSelectionComplete}
                    onBack={handleFlowCancel}
                    userInfo={user}
                    errorMessage={bankSelectionError}
                  />
                </div>
              )}

            {!showOpenBankingConsent &&
              !showUserVerification &&
              !showBankSelection &&
              showAccountSelection && (
                <div>
                  {console.log("💳 AccountSelection", availableAccounts)}
                  <AccountSelection
                    onAccountSelect={handleAccountSelectionComplete}
                    onBack={() => {
                      setShowAccountSelection(false);
                      setShowBankSelection(true);
                    }}
                    userInfo={userVerificationData}
                    selectedBanks={[{ name: "하나은행", code: "081" }]}
                    availableAccounts={availableAccounts}
                  />
                </div>
              )}
          </main>

          {/* 계좌 연결 성공 모달 - 오픈뱅킹 플로우 내부에서만 표시 */}
          {showAccountLinkSuccessModal && (
            <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
              <div className="bg-white rounded-3xl max-w-md w-full p-8 text-center">
                {/* 성공 아이콘 */}
                <div className="w-20 h-20 mx-auto mb-6 relative">
                  <img
                    src="/hanacharacter/hanacharacter8.png"
                    alt="계좌 연결 성공"
                    className="w-full h-full object-contain"
                  />
                </div>

                {/* 메시지 */}
                <h3 className="text-2xl font-bold text-gray-900 mb-3">
                  계좌 연결 완료!
                </h3>
                <p className="text-gray-600 mb-6 leading-relaxed">
                  오픈뱅킹 계좌 연결이 완료되었습니다.
                  <br />
                  이제 회원가입을 완료하고 모든 서비스를 이용하실 수 있습니다.
                </p>

                {/* 버튼 */}
                <div className="flex gap-3">
                  <button
                    onClick={async () => {
                      const API_BASE_URL =
                        process.env.NEXT_PUBLIC_API_BASE_URL ||
                        "http://localhost:8080/api";

                      // 일반 회원가입 대기 데이터 확인
                      const pendingSignupData =
                        sessionStorage.getItem("pendingSignupData");

                      if (pendingSignupData) {
                        try {
                          const signupData = JSON.parse(pendingSignupData);
                          const response = await fetch(
                            `${API_BASE_URL}/auth/signup`,
                            {
                              method: "POST",
                              headers: {
                                "Content-Type": "application/json",
                              },
                              body: JSON.stringify(signupData),
                            }
                          );

                          const result = await response.json();

                          if (response.ok) {
                            sessionStorage.removeItem("pendingSignupData");
                            console.log("일반 회원가입 완료!");
                            // 로그인 처리
                            if (result.accessToken) {
                              localStorage.setItem(
                                "accessToken",
                                result.accessToken
                              );
                              localStorage.setItem(
                                "auth_token",
                                result.accessToken
                              );
                              if (result.refreshToken) {
                                localStorage.setItem(
                                  "refreshToken",
                                  result.refreshToken
                                );
                              }
                            }
                          }
                        } catch (err) {
                          console.error("회원가입 실패:", err);
                        }
                      }

                      // OAuth2 회원가입 대기 데이터 확인
                      const pendingOAuth2Data = sessionStorage.getItem(
                        "pendingOAuth2SignupData"
                      );

                      if (pendingOAuth2Data) {
                        try {
                          const { signupData, tempToken, isNewOAuth2User } =
                            JSON.parse(pendingOAuth2Data);

                          if (isNewOAuth2User) {
                            // 신규 OAuth2 사용자 등록
                            const response = await fetch(
                              `${API_BASE_URL}/auth/oauth2/register`,
                              {
                                method: "POST",
                                headers: {
                                  "Content-Type": "application/json",
                                  Authorization: `Bearer ${tempToken}`,
                                },
                                body: JSON.stringify(signupData),
                              }
                            );

                            const result = await response.json();

                            if (response.ok) {
                              sessionStorage.removeItem(
                                "pendingOAuth2SignupData"
                              );
                              sessionStorage.removeItem("tempOAuth2Token");
                              console.log("OAuth2 회원가입 완료!");
                              // 로그인 처리
                              if (result.data?.accessToken) {
                                localStorage.setItem(
                                  "accessToken",
                                  result.data.accessToken
                                );
                                localStorage.setItem(
                                  "auth_token",
                                  result.data.accessToken
                                );
                                if (result.data.refreshToken) {
                                  localStorage.setItem(
                                    "refreshToken",
                                    result.data.refreshToken
                                  );
                                }
                              }
                            }
                          } else {
                            // 기존 OAuth2 사용자 정보 업데이트
                            const response = await fetch(
                              `${API_BASE_URL}/user/update-info`,
                              {
                                method: "PUT",
                                headers: {
                                  "Content-Type": "application/json",
                                  Authorization: `Bearer ${localStorage.getItem(
                                    "accessToken"
                                  )}`,
                                },
                                body: JSON.stringify(signupData),
                              }
                            );

                            if (response.ok) {
                              sessionStorage.removeItem(
                                "pendingOAuth2SignupData"
                              );
                              console.log("사용자 정보 업데이트 완료!");
                            }
                          }
                        } catch (err) {
                          console.error("OAuth2 처리 실패:", err);
                        }
                      }

                      setShowAccountLinkSuccessModal(false);
                      handleFlowCancel();
                    }}
                    className="flex-1 px-6 py-3 bg-gradient-to-r from-emerald-600 to-teal-600 text-white rounded-xl font-bold hover:from-emerald-700 hover:to-teal-700 transition-all duration-300 shadow-lg hover:shadow-xl"
                  >
                    확인
                  </button>
                </div>
              </div>
            </div>
          )}
        </div>
      )}

      {/* 가족 추가 모달 */}
      {showFamilyAddModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl max-w-md w-full p-6">
            <h3 className="text-2xl font-bold text-gray-900 mb-6">
              가족 추가하기
            </h3>

            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  이름 *
                </label>
                <input
                  type="text"
                  value={newFamilyMember.name}
                  onChange={(e) =>
                    setNewFamilyMember({
                      ...newFamilyMember,
                      name: e.target.value,
                    })
                  }
                  placeholder="가족 구성원의 이름을 입력하세요"
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-emerald-500 focus:border-transparent"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  전화번호 *
                </label>
                <input
                  type="tel"
                  value={newFamilyMember.phoneNumber}
                  onChange={(e) =>
                    setNewFamilyMember({
                      ...newFamilyMember,
                      phoneNumber: e.target.value,
                    })
                  }
                  placeholder="010-1234-5678"
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-emerald-500 focus:border-transparent"
                />
              </div>
            </div>

            <div className="flex gap-3 mt-6">
              <button
                onClick={() => {
                  setShowFamilyAddModal(false);
                  setNewFamilyMember({ name: "", phoneNumber: "" });
                }}
                className="flex-1 px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors"
              >
                취소
              </button>
              <button
                onClick={handleAddFamilyMember}
                className="flex-1 px-4 py-2 bg-emerald-600 text-white rounded-lg hover:bg-emerald-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                disabled={!newFamilyMember.name || !newFamilyMember.phoneNumber}
              >
                추가하기
              </button>
            </div>
          </div>
        </div>
      )}

      {/* 가족 추가 성공 모달 */}
      {showFamilySuccessModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-3xl max-w-md w-full p-8 text-center transform animate-bounce-in">
            {/* 성공 아이콘 - 하나 캐릭터 */}
            <div className="w-32 h-32 mx-auto mb-6 flex items-center justify-center">
              <img
                src="/hanacharacter/hanacharacter8.png"
                alt="축하"
                className="w-full h-full object-contain"
              />
            </div>

            {/* 메시지 */}
            <h3 className="text-2xl font-bold text-gray-900 mb-3">
              가족 초대 완료! 🎉
            </h3>
            <p className="text-gray-600 mb-6 leading-relaxed">
              카카오톡으로 초대 메시지가 전송되었습니다.
              <br />
              초대받은 분이 가족 초대를 수락하면
              <br />
              가족으로 등록됩니다!
            </p>

            {/* 확인 버튼 */}
            <button
              onClick={() => setShowFamilySuccessModal(false)}
              className="w-full px-6 py-3 bg-gradient-to-r from-emerald-600 to-teal-600 text-white rounded-xl font-bold hover:from-emerald-700 hover:to-teal-700 transition-all duration-300 shadow-lg hover:shadow-xl"
            >
              확인
            </button>
          </div>
        </div>
      )}

      {/* 로그인 필요 모달 */}
      {showLoginRequiredModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-3xl max-w-md w-full p-8 text-center">
            {/* 잠금 아이콘 */}
            <div className="w-20 h-20 mx-auto mb-6 bg-teal-100 rounded-full flex items-center justify-center">
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
                  d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"
                />
              </svg>
            </div>

            {/* 메시지 */}
            <h3 className="text-2xl font-bold text-gray-900 mb-3">
              로그인이 필요합니다
            </h3>
            <p className="text-gray-600 mb-6 leading-relaxed">
              이 기능을 사용하려면 먼저 로그인해주세요.
            </p>

            {/* 버튼 */}
            <div className="flex gap-3">
              <button
                onClick={() => setShowLoginRequiredModal(false)}
                className="flex-1 px-6 py-3 border border-gray-300 text-gray-700 rounded-xl font-medium hover:bg-gray-50 transition-colors"
              >
                취소
              </button>
              <button
                onClick={() => {
                  setShowLoginRequiredModal(false);
                  setShowAuth(true);
                }}
                className="flex-1 px-6 py-3 bg-gradient-to-r from-teal-600 to-emerald-600 text-white rounded-xl font-bold hover:from-teal-700 hover:to-emerald-700 transition-all duration-300 shadow-lg hover:shadow-xl"
              >
                로그인
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

// 알림 아이템 컴포넌트
function NotificationItem({ notification, user, onAction, setActiveTab }) {
  const [loading, setLoading] = useState(false);
  const [showAcceptSuccessModal, setShowAcceptSuccessModal] = useState(false);

  const handleAccept = async () => {
    setLoading(true);
    try {
      console.log("🔍 알림 데이터:", notification);

      // 알림 타입에 따라 다른 API 호출
      if (notification.relatedEntityType === "FamilyMember") {
        // 가족 초대 수락
        console.log("📤 가족 초대 수락 요청:", {
          familyMemberId: notification.relatedEntityId,
          userId: user.id,
        });

        const response = await fetch(
          `${API_BASE_URL}/family/members/${notification.relatedEntityId}/accept?userId=${user.id}`,
          {
            method: "POST",
            headers: {
              Authorization: `Bearer ${user.token}`,
            },
          }
        );

        console.log("📥 응답 상태:", response.status, response.ok);

        if (response.ok) {
          console.log("가족 초대 수락 완료");
          console.log("🎯 모달 상태 변경 시도...");
          setShowAcceptSuccessModal(true);
          console.log("🎯 모달 상태 변경 완료");
          // onAction()은 모달 닫을 때 호출하도록 변경
        } else {
          const errorData = await response.json();
          console.error("가족 초대 수락 실패:", errorData);
          alert(
            `가족 초대 수락 실패: ${errorData.message || "알 수 없는 오류"}`
          );
        }
      } else if (notification.relatedEntityType === "GroupAccountMember") {
        // 모임통장 초대 수락
        console.log("📤 모임통장 초대 수락 요청:", {
          memberId: notification.relatedEntityId,
          userId: user.id,
        });

        const response = await fetch(
          `${API_BASE_URL}/group-accounts/members/${notification.relatedEntityId}/accept?userId=${user.id}`,
          {
            method: "POST",
            headers: {
              Authorization: `Bearer ${user.token}`,
            },
          }
        );

        console.log("📥 응답 상태:", response.status, response.ok);

        if (response.ok) {
          console.log("모임통장 초대 수락 완료");
          console.log("🎯 모달 상태 변경 시도...");
          setShowAcceptSuccessModal(true);
          console.log("🎯 모달 상태 변경 완료");
          // onAction()은 모달 닫을 때 호출하도록 변경
        } else {
          const errorData = await response.json();
          console.error("모임통장 초대 수락 실패:", errorData);
          alert(
            `모임통장 초대 수락 실패: ${errorData.message || "알 수 없는 오류"}`
          );
        }
      }
    } catch (error) {
      console.error("초대 수락 실패:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleReject = async () => {
    setLoading(true);
    try {
      // 알림 상태를 거절로 변경
      const response = await fetch(
        `${API_BASE_URL}/notifications/${notification.id}/status?userId=${user.id}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${user.token}`,
          },
          body: JSON.stringify({ status: "REJECTED" }),
        }
      );

      if (response.ok) {
        console.log("초대 거절 완료");
        onAction();
      }
    } catch (error) {
      console.error("초대 거절 실패:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async () => {
    if (!window.confirm("이 알림을 삭제하시겠습니까?")) {
      return;
    }

    setLoading(true);
    try {
      const response = await fetch(
        `${API_BASE_URL}/notifications/${notification.id}?userId=${user.id}`,
        {
          method: "DELETE",
          headers: {
            Authorization: `Bearer ${user.token}`,
          },
        }
      );

      if (response.ok) {
        console.log("알림 삭제 완료");
        onAction();
      } else {
        console.error("알림 삭제 실패");
      }
    } catch (error) {
      console.error("알림 삭제 실패:", error);
    } finally {
      setLoading(false);
    }
  };

  const markAsRead = async () => {
    if (notification.status === "UNREAD") {
      try {
        await fetch(
          `${API_BASE_URL}/notifications/${notification.id}/read?userId=${user.id}`,
          {
            method: "PUT",
            headers: {
              Authorization: `Bearer ${user.token}`,
            },
          }
        );
      } catch (error) {
        console.error("알림 읽음 처리 실패:", error);
      }
    }
  };

  return (
    <div
      className={`p-4 hover:bg-gray-50 transition-colors ${
        notification.status === "UNREAD" ? "bg-blue-50" : ""
      }`}
      onClick={markAsRead}
    >
      <div className="flex items-start gap-3">
        <div className="flex-shrink-0">
          {notification.type === "SYSTEM_NOTICE" ? (
            <div className="w-10 h-10 bg-gradient-to-br from-purple-400 to-pink-500 rounded-full flex items-center justify-center">
              <Heart className="w-5 h-5 text-white" />
            </div>
          ) : notification.type === "GROUP_ACCOUNT_INVITE" ||
            notification.relatedEntityType === "GroupAccountMember" ? (
            <div className="w-10 h-10 bg-gradient-to-br from-purple-400 to-pink-500 rounded-full flex items-center justify-center">
              <Heart className="w-5 h-5 text-white" />
            </div>
          ) : (
            <div className="w-10 h-10 bg-gradient-to-br from-blue-400 to-indigo-500 rounded-full flex items-center justify-center">
              <Bell className="w-5 h-5 text-white" />
            </div>
          )}
        </div>

        <div className="flex-1 min-w-0">
          <h4 className="text-sm font-semibold text-gray-900 mb-1">
            {notification.title}
          </h4>
          <p className="text-xs text-gray-600 mb-2">{notification.content}</p>
          <p className="text-xs text-gray-400">
            {new Date(notification.createdAt).toLocaleString("ko-KR")}
          </p>

          {/* 수락/거절/삭제 버튼 */}
          {(notification.relatedEntityType === "FamilyMember" ||
            notification.relatedEntityType === "GroupAccountMember") && (
            <div className="flex gap-2 mt-3">
              <button
                onClick={(e) => {
                  e.stopPropagation();
                  handleAccept();
                }}
                disabled={loading}
                className="flex-1 px-3 py-2 bg-emerald-600 text-white rounded-lg hover:bg-emerald-700 transition-colors text-xs font-medium disabled:opacity-50"
              >
                {loading ? "처리중..." : "수락"}
              </button>
              <button
                onClick={(e) => {
                  e.stopPropagation();
                  handleReject();
                }}
                disabled={loading}
                className="flex-1 px-3 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition-colors text-xs font-medium disabled:opacity-50"
              >
                거절
              </button>
            </div>
          )}

          {/* 알림 삭제 버튼 */}
          <button
            onClick={(e) => {
              e.stopPropagation();
              handleDelete();
            }}
            disabled={loading}
            className="mt-2 w-full px-3 py-2 text-red-500 hover:bg-red-50 rounded-lg transition-colors text-xs font-medium flex items-center justify-center gap-1"
          >
            <X className="w-3 h-3" />
            삭제
          </button>
        </div>

        {notification.status === "UNREAD" && (
          <div className="flex-shrink-0">
            <div className="w-2 h-2 bg-emerald-500 rounded-full"></div>
          </div>
        )}
      </div>

      {/* 초대 수락 성공 모달 (가족/모임통장 공통) */}
      {showAcceptSuccessModal &&
        console.log("🎨 모달 렌더링 시작:", showAcceptSuccessModal)}
      {showAcceptSuccessModal &&
        typeof window !== "undefined" &&
        createPortal(
          <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-[9999] p-4">
            <div className="bg-white rounded-3xl max-w-md w-full p-8 text-center transform animate-bounce-in">
              {/* 성공 아이콘 - 하나 캐릭터 */}
              <div className="w-32 h-32 mx-auto mb-6 flex items-center justify-center">
                <img
                  src="/hanacharacter/hanacharacter8.png"
                  alt="축하"
                  className="w-full h-full object-contain"
                />
              </div>

              {/* 메시지 - 타입에 따라 다르게 표시 */}
              {notification.relatedEntityType === "FamilyMember" ? (
                <>
                  <h3 className="text-2xl font-bold text-gray-900 mb-3">
                    가족 등록 완료! 🎉
                  </h3>
                  <p className="text-gray-600 mb-6 leading-relaxed">
                    가족 등록이 완료되었습니다!
                    <br />
                    이제 함께 하나Future의 다양한 서비스를
                    <br />
                    이용하실 수 있습니다.
                  </p>
                </>
              ) : (
                <>
                  <h3 className="text-2xl font-bold text-gray-900 mb-3">
                    모임통장 참여 완료! 🎉
                  </h3>
                  <p className="text-gray-600 mb-6 leading-relaxed">
                    모임통장 참여가 완료되었습니다!
                    <br />
                    이제 함께 모임통장을
                    <br />
                    이용하실 수 있습니다.
                  </p>
                </>
              )}

              {/* 확인 버튼 */}
              <button
                onClick={() => {
                  setShowAcceptSuccessModal(false);
                  onAction(); // 모달 닫은 후 알림 목록 새로고침
                  // 모임통장인 경우 모임통장 관리 페이지로 이동
                  if (notification.relatedEntityType === "GroupAccountMember") {
                    setActiveTab("manage-group-account");
                  }
                }}
                className="w-full px-6 py-3 bg-gradient-to-r from-emerald-600 to-teal-600 text-white rounded-xl font-bold hover:from-emerald-700 hover:to-teal-700 transition-all duration-300 shadow-lg hover:shadow-xl"
              >
                확인
              </button>
            </div>
          </div>,
          document.body
        )}
    </div>
  );
}
