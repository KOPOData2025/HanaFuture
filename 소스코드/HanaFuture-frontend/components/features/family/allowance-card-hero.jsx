"use client";

export function AllowanceCardHero() {
  return (
    <div className="relative bg-gradient-to-r from-emerald-400 via-teal-400 to-cyan-400 overflow-hidden">
      {/* 배경 패턴 */}
      <div
        className="absolute inset-0 opacity-10"
        style={{
          backgroundImage: `url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='1'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E")`,
        }}
      />

      {/* 배경 아이콘들 */}
      <div className="absolute inset-0 overflow-hidden pointer-events-none">
        <div className="absolute top-10 left-10 text-white/10 text-9xl">💳</div>
        <div className="absolute top-20 right-20 text-white/10 text-7xl">
          🎁
        </div>
        <div className="absolute bottom-10 left-1/4 text-white/10 text-8xl">
          ⭐
        </div>
        <div className="absolute bottom-20 right-1/3 text-white/10 text-6xl">
          🏦
        </div>
      </div>

      {/* 컨텐츠 */}
      <div className="relative max-w-6xl mx-auto px-4 py-16 text-center">
        <h1 className="text-5xl md:text-6xl font-black text-white mb-4 drop-shadow-lg select-none">
          우리 아이만의
          <br />첫 금융 교육
        </h1>
        <p className="text-xl md:text-2xl text-white/90 font-medium drop-shadow select-none">
          안전하고 스마트한 용돈 관리 서비스
        </p>
      </div>
    </div>
  );
}
