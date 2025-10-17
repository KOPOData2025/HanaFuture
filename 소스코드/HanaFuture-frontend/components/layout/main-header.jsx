"use client";

import React, { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { Bell, User, LogOut, Menu, X } from "lucide-react";

export default function MainHeader({ currentPage = "home" }) {
  const router = useRouter();
  const [user, setUser] = useState(null);
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [showNotifications, setShowNotifications] = useState(false);
  const [showMobileMenu, setShowMobileMenu] = useState(false);
  const [notifications, setNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [hoveredMenu, setHoveredMenu] = useState(null);
  const [hoverTimeout, setHoverTimeout] = useState(null);
  const [mounted, setMounted] = useState(false);

  useEffect(() => {
    setMounted(true);
  }, []);

  useEffect(() => {
    if (!mounted) return;

    // 로컬 스토리지에서 사용자 정보 가져오기
    const checkUser = () => {
      try {
        const userData = localStorage.getItem("user");
        console.log("🔍 MainHeader - localStorage user:", userData);
        if (userData) {
          const parsedUser = JSON.parse(userData);
          console.log("MainHeader - 파싱된 사용자:", parsedUser);
          setUser(parsedUser);
          setIsLoggedIn(true);
          loadNotifications(parsedUser);
        } else {
          console.log("MainHeader - localStorage에 user 없음");
          setUser(null);
          setIsLoggedIn(false);
        }
      } catch (error) {
        console.error("MainHeader - 사용자 정보 파싱 실패:", error);
      }
    };

    checkUser();

    // 스토리지 변경 감지
    window.addEventListener("storage", checkUser);
    return () => window.removeEventListener("storage", checkUser);
  }, [mounted]);

  const loadNotifications = async (userData) => {
    if (!userData) return;

    try {
      const response = await fetch(
        `http://localhost:8080/api/notifications/user/${userData.id}`,
        {
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${userData.token}`,
          },
        }
      );

      if (response.ok) {
        const result = await response.json();
        if (result.success && result.data) {
          setNotifications(result.data);
          const unread = result.data.filter(
            (n) => n.status === "UNREAD"
          ).length;
          setUnreadCount(unread);
        }
      }
    } catch (error) {
      console.error("알림 조회 실패:", error);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem("user");
    setUser(null);
    setIsLoggedIn(false);
    router.push("/");
  };

  const handleFeatureClick = (featureId) => {
    if (!isLoggedIn && featureId !== "home") {
      router.push("/");
      return;
    }

    if (featureId === "group-account") {
      router.push("/");
    } else if (featureId === "savings") {
      router.push("/together-savings-intro");
    } else if (featureId === "benefits") {
      router.push("/");
    } else if (featureId === "home") {
      router.push("/");
    } else {
      router.push("/");
    }
  };

  const navigationItems = [
    {
      id: "group-account",
      label: "모임통장",
      submenu: [
        {
          id: "create-group-account",
          label: "모임통장 개설",
          description: "가족 모임통장 만들기",
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
      submenu: [
        {
          id: "create-allowance-card",
          label: "아이카드 발급",
          description: "자녀용 카드 발급",
        },
        {
          id: "manage-allowance",
          label: "용돈 관리",
          description: "용돈 설정 및 관리",
        },
      ],
    },
    {
      id: "savings",
      label: "함께 적금",
      submenu: [
        {
          id: "create-savings",
          label: "적금 상품 신청",
          description: "목표별 적금 상품 가입",
        },
        {
          id: "manage-savings",
          label: "적금 관리",
          description: "기존 적금 상품 관리",
        },
      ],
    },
    {
      id: "benefits",
      label: "정부혜택",
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

  return (
    <>
      <header className="sticky top-0 z-50 bg-white border-b border-gray-200 shadow-sm">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            {/* Logo */}
            <div className="flex items-center space-x-4">
              <button
                onClick={() => handleFeatureClick("home")}
                className="flex items-center space-x-3 hover:opacity-80 transition-opacity"
              >
                <img
                  src="/bank-logos/HanaLogo.png"
                  alt="HanaBank"
                  className="h-8 w-8 object-contain"
                />
                <h1 className="text-xl font-bold text-gray-900">하나Future</h1>
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
                    onClick={() => handleFeatureClick(item.id)}
                    className={`text-base font-medium transition-colors ${
                      currentPage === item.id
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
                    onClick={() => router.push("/")}
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
                  onClick={() => router.push("/")}
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
                  <div
                    key={notification.id}
                    className="p-4 hover:bg-gray-50 cursor-pointer transition-colors"
                  >
                    <p className="text-sm text-gray-900 font-medium mb-1">
                      {notification.title}
                    </p>
                    <p className="text-xs text-gray-600">
                      {notification.message}
                    </p>
                  </div>
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
                handleFeatureClick("home");
                setShowMobileMenu(false);
              }}
              className={`flex items-center px-4 py-3 rounded-lg text-lg font-medium transition-colors w-full ${
                currentPage === "home"
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
                    currentPage === item.id
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
    </>
  );
}
