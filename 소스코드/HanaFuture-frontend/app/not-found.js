"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { Home, ArrowLeft, Search } from "lucide-react";

export default function NotFound() {
  const router = useRouter();

  return (
    <div className="min-h-screen bg-gradient-to-br from-emerald-50 via-white to-teal-50 flex items-center justify-center px-4">
      <div className="max-w-2xl w-full text-center">
        {/* 404 일러스트 */}
        <div className="mb-8 relative">
          <h1 className="text-9xl font-bold text-emerald-100 select-none">
            404
          </h1>
          <div className="absolute inset-0 flex items-center justify-center">
            <Search className="w-24 h-24 text-emerald-600 opacity-20" />
          </div>
        </div>

        {/* 메시지 */}
        <div className="space-y-4 mb-8">
          <h2 className="text-3xl font-bold text-gray-900">
            페이지를 찾을 수 없습니다
          </h2>
          <p className="text-gray-600 text-lg">
            요청하신 페이지가 존재하지 않거나 이동되었습니다.
            <br />
            주소를 다시 확인해주세요.
          </p>
        </div>

        {/* 액션 버튼 */}
        <div className="flex flex-col sm:flex-row gap-4 justify-center items-center">
          <button
            onClick={() => router.back()}
            className="flex items-center gap-2 px-6 py-3 bg-white border-2 border-emerald-600 text-emerald-600 rounded-xl font-semibold hover:bg-emerald-50 transition-all shadow-sm hover:shadow-md"
          >
            <ArrowLeft className="w-5 h-5" />
            이전 페이지
          </button>

          <Link href="/dashboard">
            <button className="flex items-center gap-2 px-6 py-3 bg-gradient-to-r from-emerald-600 to-teal-600 text-white rounded-xl font-semibold hover:from-emerald-700 hover:to-teal-700 transition-all shadow-lg hover:shadow-xl">
              <Home className="w-5 h-5" />
              홈으로 가기
            </button>
          </Link>
        </div>

        {/* 도움말 링크 */}
        <div className="mt-12 pt-8 border-t border-gray-200">
          <p className="text-gray-500 text-sm mb-4">자주 찾는 페이지</p>
          <div className="flex flex-wrap gap-3 justify-center">
            <Link href="/government-benefits">
              <span className="px-4 py-2 bg-gray-100 hover:bg-gray-200 text-gray-700 rounded-lg text-sm font-medium transition-colors">
                정부 혜택
              </span>
            </Link>
            <Link href="/savings">
              <span className="px-4 py-2 bg-gray-100 hover:bg-gray-200 text-gray-700 rounded-lg text-sm font-medium transition-colors">
                적금 관리
              </span>
            </Link>
            <Link href="/group-account">
              <span className="px-4 py-2 bg-gray-100 hover:bg-gray-200 text-gray-700 rounded-lg text-sm font-medium transition-colors">
                모임통장
              </span>
            </Link>
            <Link href="/my-page">
              <span className="px-4 py-2 bg-gray-100 hover:bg-gray-200 text-gray-700 rounded-lg text-sm font-medium transition-colors">
                마이페이지
              </span>
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
}

