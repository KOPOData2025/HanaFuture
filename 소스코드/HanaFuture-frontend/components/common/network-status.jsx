"use client";

import { useState, useEffect } from "react";
import { WifiOff, Wifi } from "lucide-react";
import { toast } from "sonner";

export function NetworkStatus() {
  const [isOnline, setIsOnline] = useState(true);

  useEffect(() => {
    const handleOnline = () => {
      setIsOnline(true);
      toast.success("인터넷 연결이 복구되었습니다.", {
        icon: <Wifi className="h-4 w-4 text-green-600" />,
      });
    };

    const handleOffline = () => {
      setIsOnline(false);
      toast.error("인터넷 연결이 끊어졌습니다.", {
        icon: <WifiOff className="h-4 w-4 text-red-600" />,
        duration: Infinity,
      });
    };

    window.addEventListener("online", handleOnline);
    window.addEventListener("offline", handleOffline);

    // 초기 상태 설정
    setIsOnline(navigator.onLine);

    return () => {
      window.removeEventListener("online", handleOnline);
      window.removeEventListener("offline", handleOffline);
    };
  }, []);

  if (isOnline) return null;

  return (
    <div className="fixed top-0 left-0 right-0 z-50 bg-red-600 text-white px-4 py-2">
      <div className="flex items-center justify-center gap-2 text-sm font-medium">
        <WifiOff className="h-4 w-4" />
        인터넷 연결이 끊어졌습니다. 일부 기능이 제한될 수 있습니다.
      </div>
    </div>
  );
}























