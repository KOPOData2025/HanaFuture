"use client";

import React, { useState, useEffect } from "react";
import {
  Bell,
  X,
  Check,
  AlertCircle,
  Gift,
  TrendingUp,
  Calendar,
} from "lucide-react";
import { StorageManager } from "../../lib/storage";

// 알림 타입별 아이콘과 색상
const NOTIFICATION_TYPES = {
  benefit: { icon: Gift, color: "text-green-600", bgColor: "bg-green-100" },
  budget: { icon: TrendingUp, color: "text-blue-600", bgColor: "bg-blue-100" },
  goal: { icon: Calendar, color: "text-purple-600", bgColor: "bg-purple-100" },
  alert: { icon: AlertCircle, color: "text-red-600", bgColor: "bg-red-100" },
};

export function NotificationCenter() {
  const [notifications, setNotifications] = useState([]);
  const [isOpen, setIsOpen] = useState(false);
  const [settings, setSettings] = useState({});

  useEffect(() => {
    loadNotifications();
    loadSettings();

    // 주기적으로 새 알림 확인 (실제 환경에서는 WebSocket 등 사용)
    const interval = setInterval(checkForNewNotifications, 30000); // 30초마다

    return () => clearInterval(interval);
  }, []);

  const loadNotifications = () => {
    // 실제로는 API에서 가져오지만, 여기서는 로컬 스토리지 사용
    const savedNotifications = StorageManager.getItem("notifications", []);
    setNotifications(savedNotifications);
  };

  const loadSettings = () => {
    const savedSettings = StorageManager.getNotificationSettings();
    setSettings(savedSettings);
  };

  const checkForNewNotifications = () => {
    // 새로운 알림 생성 로직 (예시)
    const newNotifications = generateSampleNotifications();
    if (newNotifications.length > 0) {
      const updatedNotifications = [...notifications, ...newNotifications];
      setNotifications(updatedNotifications);
      StorageManager.setItem("notifications", updatedNotifications);
    }
  };

  const generateSampleNotifications = () => {
    const now = new Date();
    const sampleNotifications = [];

    // 랜덤하게 알림 생성 (실제로는 서버에서 받아옴)
    if (Math.random() > 0.8) {
      sampleNotifications.push({
        id: `notif_${Date.now()}`,
        type: "benefit",
        title: "새로운 혜택 발견!",
        message: "서울시 출산지원금 신청이 가능합니다.",
        timestamp: now.toISOString(),
        read: false,
        actionUrl: "/benefits",
      });
    }

    if (Math.random() > 0.9) {
      sampleNotifications.push({
        id: `notif_${Date.now() + 1}`,
        type: "budget",
        title: "예산 알림",
        message: "이번 달 육아비 예산의 80%를 사용했습니다.",
        timestamp: now.toISOString(),
        read: false,
      });
    }

    return sampleNotifications;
  };

  const markAsRead = (notificationId) => {
    const updatedNotifications = notifications.map((notif) =>
      notif.id === notificationId ? { ...notif, read: true } : notif
    );
    setNotifications(updatedNotifications);
    StorageManager.setItem("notifications", updatedNotifications);
  };

  const markAllAsRead = () => {
    const updatedNotifications = notifications.map((notif) => ({
      ...notif,
      read: true,
    }));
    setNotifications(updatedNotifications);
    StorageManager.setItem("notifications", updatedNotifications);
  };

  const deleteNotification = (notificationId) => {
    const updatedNotifications = notifications.filter(
      (notif) => notif.id !== notificationId
    );
    setNotifications(updatedNotifications);
    StorageManager.setItem("notifications", updatedNotifications);
  };

  const unreadCount = notifications.filter((notif) => !notif.read).length;

  return (
    <div className="relative">
      {/* 알림 버튼 */}
      <button
        onClick={() => setIsOpen(!isOpen)}
        className="relative hover:bg-primary/10 transition-colors rounded-2xl p-4 focus:outline-none focus:ring-2 focus:ring-primary/50"
        aria-label="알림 센터"
      >
        <Bell className="h-6 w-6" />
        {unreadCount > 0 && (
          <span className="absolute -top-1 -right-1 h-5 w-5 bg-red-500 text-white text-xs font-bold rounded-full flex items-center justify-center animate-pulse">
            {unreadCount > 9 ? "9+" : unreadCount}
          </span>
        )}
      </button>

      {/* 알림 패널 */}
      {isOpen && (
        <>
          {/* 배경 오버레이 */}
          <div
            className="fixed inset-0 z-40"
            onClick={() => setIsOpen(false)}
          />

          {/* 알림 드롭다운 */}
          <div className="absolute right-0 top-full mt-2 w-96 max-h-96 bg-white dark:bg-gray-800 rounded-xl border border-border shadow-2xl z-50 overflow-hidden">
            {/* 헤더 */}
            <div className="p-4 border-b border-border bg-muted/30">
              <div className="flex items-center justify-between">
                <h3 className="font-bold text-lg">알림</h3>
                <div className="flex items-center gap-2">
                  {unreadCount > 0 && (
                    <button
                      onClick={markAllAsRead}
                      className="text-sm text-primary hover:text-primary/80 font-medium"
                    >
                      모두 읽음
                    </button>
                  )}
                  <button
                    onClick={() => setIsOpen(false)}
                    className="p-1 hover:bg-muted rounded-lg"
                  >
                    <X className="h-4 w-4" />
                  </button>
                </div>
              </div>
            </div>

            {/* 알림 목록 */}
            <div className="max-h-80 overflow-y-auto">
              {notifications.length === 0 ? (
                <div className="p-8 text-center text-muted-foreground">
                  <Bell className="h-12 w-12 mx-auto mb-3 opacity-50" />
                  <p>새로운 알림이 없습니다</p>
                </div>
              ) : (
                notifications.map((notification) => {
                  const {
                    icon: Icon,
                    color,
                    bgColor,
                  } = NOTIFICATION_TYPES[notification.type] ||
                  NOTIFICATION_TYPES.alert;

                  return (
                    <div
                      key={notification.id}
                      className={`p-4 border-b border-border hover:bg-muted/30 transition-colors ${
                        !notification.read ? "bg-primary/5" : ""
                      }`}
                    >
                      <div className="flex items-start gap-3">
                        {/* 아이콘 */}
                        <div
                          className={`p-2 rounded-lg ${bgColor} flex-shrink-0`}
                        >
                          <Icon className={`h-4 w-4 ${color}`} />
                        </div>

                        {/* 내용 */}
                        <div className="flex-1 min-w-0">
                          <div className="flex items-start justify-between">
                            <div>
                              <h4
                                className={`font-medium text-sm ${
                                  !notification.read ? "font-bold" : ""
                                }`}
                              >
                                {notification.title}
                              </h4>
                              <p className="text-sm text-muted-foreground mt-1">
                                {notification.message}
                              </p>
                              <p className="text-xs text-muted-foreground mt-2">
                                {new Date(
                                  notification.timestamp
                                ).toLocaleString("ko-KR")}
                              </p>
                            </div>

                            {/* 액션 버튼들 */}
                            <div className="flex items-center gap-1 ml-2">
                              {!notification.read && (
                                <button
                                  onClick={() => markAsRead(notification.id)}
                                  className="p-1 hover:bg-muted rounded text-primary"
                                  title="읽음으로 표시"
                                >
                                  <Check className="h-3 w-3" />
                                </button>
                              )}
                              <button
                                onClick={() =>
                                  deleteNotification(notification.id)
                                }
                                className="p-1 hover:bg-muted rounded text-muted-foreground hover:text-red-500"
                                title="삭제"
                              >
                                <X className="h-3 w-3" />
                              </button>
                            </div>
                          </div>

                          {/* 액션 버튼 */}
                          {notification.actionUrl && (
                            <button
                              onClick={() => {
                                markAsRead(notification.id);
                                // 실제로는 라우터를 사용해 페이지 이동
                                console.log(
                                  "Navigate to:",
                                  notification.actionUrl
                                );
                              }}
                              className="mt-2 text-xs bg-primary text-primary-foreground px-2 py-1 rounded hover:bg-primary/90 transition-colors"
                            >
                              자세히 보기
                            </button>
                          )}
                        </div>
                      </div>
                    </div>
                  );
                })
              )}
            </div>

            {/* 푸터 */}
            {notifications.length > 0 && (
              <div className="p-3 border-t border-border bg-muted/30">
                <button
                  onClick={() => {
                    // 알림 설정 페이지로 이동
                  }}
                  className="w-full text-sm text-center text-muted-foreground hover:text-foreground transition-colors"
                >
                  알림 설정
                </button>
              </div>
            )}
          </div>
        </>
      )}
    </div>
  );
}

// 알림 설정 컴포넌트
export function NotificationSettings() {
  const [settings, setSettings] = useState({});

  useEffect(() => {
    const savedSettings = StorageManager.getNotificationSettings();
    setSettings(savedSettings);
  }, []);

  const updateSetting = (key, value) => {
    const updatedSettings = { ...settings, [key]: value };
    setSettings(updatedSettings);
    StorageManager.saveNotificationSettings(updatedSettings);
  };

  return (
    <div className="space-y-6">
      <h2 className="text-2xl font-bold">알림 설정</h2>

      <div className="space-y-4">
        <div className="flex items-center justify-between p-4 bg-card rounded-lg border">
          <div>
            <h3 className="font-medium">혜택 알림</h3>
            <p className="text-sm text-muted-foreground">
              새로운 정부 혜택 발견 시 알림
            </p>
          </div>
          <label className="relative inline-flex items-center cursor-pointer">
            <input
              type="checkbox"
              checked={settings.benefitAlerts || false}
              onChange={(e) => updateSetting("benefitAlerts", e.target.checked)}
              className="sr-only peer"
            />
            <div className="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-primary/20 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-primary"></div>
          </label>
        </div>

        <div className="flex items-center justify-between p-4 bg-card rounded-lg border">
          <div>
            <h3 className="font-medium">예산 알림</h3>
            <p className="text-sm text-muted-foreground">
              예산 초과 위험 시 알림
            </p>
          </div>
          <label className="relative inline-flex items-center cursor-pointer">
            <input
              type="checkbox"
              checked={settings.budgetAlerts || false}
              onChange={(e) => updateSetting("budgetAlerts", e.target.checked)}
              className="sr-only peer"
            />
            <div className="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-primary/20 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-primary"></div>
          </label>
        </div>

        <div className="flex items-center justify-between p-4 bg-card rounded-lg border">
          <div>
            <h3 className="font-medium">목표 리마인더</h3>
            <p className="text-sm text-muted-foreground">
              저축 목표 달성 현황 알림
            </p>
          </div>
          <label className="relative inline-flex items-center cursor-pointer">
            <input
              type="checkbox"
              checked={settings.goalReminders || false}
              onChange={(e) => updateSetting("goalReminders", e.target.checked)}
              className="sr-only peer"
            />
            <div className="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-primary/20 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-primary"></div>
          </label>
        </div>
      </div>
    </div>
  );
}
