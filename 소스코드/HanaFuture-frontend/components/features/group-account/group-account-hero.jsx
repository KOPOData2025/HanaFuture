"use client";

export function GroupAccountHero() {
  return (
    <section className="relative overflow-hidden bg-gradient-to-r from-emerald-500 to-teal-500 text-white py-20">
      <div className="absolute inset-0 flex items-center justify-center opacity-10">
        <img
          src="/hanacharacter/hanafamily.png"
          alt=""
          className="w-auto h-full max-w-none object-contain"
        />
      </div>
      <div className="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
        <h2 className="text-4xl md:text-5xl font-bold mb-4">
          우리 가족만의
          <br />
          똑똑한 돈 관리
        </h2>
        <p className="text-xl md:text-2xl text-emerald-50">
          투명하고 편리한 가족 공동 계좌 서비스
        </p>
      </div>
    </section>
  );
}
