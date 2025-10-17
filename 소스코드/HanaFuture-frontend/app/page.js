"use client";

import { useState, useEffect } from "react";
import { UnifiedDashboard } from "../components/features/dashboard/unified-dashboard";
import { LoadingScreen } from "../components/common/loading-screen";
import { useAuth } from "../contexts/AuthContext";

export default function Home() {
  const [mounted, setMounted] = useState(false);
  const { loading } = useAuth();

  // SSR 문제 방지를 위한 mounted 상태 관리
  useEffect(() => {
    setMounted(true);
  }, []);

  // SSR 문제 방지: mounted 되기 전까지는 로딩 화면 표시
  if (!mounted || loading) {
    return <LoadingScreen message="로딩 중입니다. 잠시만 기다려주세요..." />;
  }

  return <UnifiedDashboard />;
}
