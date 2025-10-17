"use client";

import { Mail, Phone } from "lucide-react";

export default function Footer() {
  return (
    <footer className="bg-gradient-to-br from-emerald-600 to-teal-700 text-white">
      <div className="max-w-7xl mx-auto px-4 py-12">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
          {/* 로고 & 소개 */}
          <div className="md:col-span-1">
            <h3 className="text-xl font-bold mb-4">하나Future</h3>
            <p className="text-emerald-100 text-sm leading-relaxed">
              가족의 미래를 함께 설계하는
              <br />
              육아 금융 플랫폼
            </p>
          </div>

          {/* 서비스 */}
          <div>
            <h4 className="font-bold text-lg mb-4">서비스</h4>
            <ul className="space-y-2">
              <li className="text-emerald-100 text-sm">모임통장</li>
              <li className="text-emerald-100 text-sm">함께 적금</li>
              <li className="text-emerald-100 text-sm">정부 혜택</li>
              <li className="text-emerald-100 text-sm">아이카드</li>
            </ul>
          </div>

          {/* 정책 */}
          <div>
            <h4 className="font-bold text-lg mb-4">정책</h4>
            <ul className="space-y-2">
              <li className="text-emerald-100 text-sm">서비스 이용 약관</li>
              <li className="text-emerald-100 text-sm">개인정보 처리방침</li>
              <li className="text-emerald-100 text-sm">자주 묻는 질문</li>
            </ul>
          </div>

          {/* Follow Us */}
          <div>
            <h4 className="font-bold text-lg mb-4">Follow Us</h4>
            <div className="space-y-3">
              <div className="flex items-center gap-2 text-emerald-100 text-sm">
                <Mail className="w-4 h-4" />
                support@hanafuture.com
              </div>
              <div className="flex items-center gap-2 text-emerald-100 text-sm">
                <Phone className="w-4 h-4" />
                고객센터: 1234-5678
              </div>
            </div>
          </div>
        </div>

        {/* 하단 구분선 & 저작권 */}
        <div className="border-t border-emerald-500 mt-8 pt-6">
          <div className="flex flex-col md:flex-row justify-between items-center gap-4 text-sm text-emerald-100">
            <div className="text-center md:text-left">
              <p className="mb-1">
                인천 서구 에코로 167 하나금융그룹 통합데이터센터 비전센터 5층
              </p>
              <p>(주)하나Future | 사업자등록번호: 123-45-67890</p>
            </div>
            <p className="text-center md:text-right">
              © 2025 하나Future. All rights reserved.
            </p>
          </div>
        </div>
      </div>
    </footer>
  );
}
