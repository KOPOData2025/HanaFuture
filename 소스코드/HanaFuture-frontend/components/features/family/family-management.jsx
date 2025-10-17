"use client";

export function FamilyManagement() {
  return (
    <div className="space-y-6">
      <div className="flex items-center space-x-4">
        <img
          src="/hanacharacter/hanacharacter14.png"
          alt="가족 정보 하나 캐릭터"
          className="w-28 h-28 object-contain"
        />
        <div>
          <h2 className="font-heading font-bold text-2xl text-foreground mb-2">
            가족 정보
          </h2>
          <p className="text-muted-foreground">가족 구성원 정보를 관리하세요</p>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div className="p-6 bg-card rounded-xl border shadow-sm">
          <h3 className="font-semibold mb-4">가족 구성원</h3>
          <div className="space-y-3">
            <div className="flex items-center gap-3 p-3 bg-gray-50 rounded-lg">
              <div className="w-10 h-10 bg-primary rounded-full flex items-center justify-center text-white font-bold">
                김
              </div>
              <div>
                <p className="font-semibold">김하나</p>
                <p className="text-sm text-muted-foreground">
                  본인 (주계좌 소유자)
                </p>
              </div>
            </div>

            <div className="flex items-center gap-3 p-3 bg-gray-50 rounded-lg">
              <div className="w-10 h-10 bg-secondary rounded-full flex items-center justify-center text-white font-bold">
                이
              </div>
              <div>
                <p className="font-semibold">이민수</p>
                <p className="text-sm text-muted-foreground">배우자</p>
              </div>
            </div>

            <div className="flex items-center gap-3 p-3 bg-gray-50 rounded-lg">
              <div className="w-10 h-10 bg-accent rounded-full flex items-center justify-center text-white font-bold">
                김
              </div>
              <div>
                <p className="font-semibold">김아이</p>
                <p className="text-sm text-muted-foreground">자녀 (3세)</p>
              </div>
            </div>
          </div>
        </div>

        <div className="p-6 bg-card rounded-xl border shadow-sm">
          <h3 className="font-semibold mb-4">가족 목표</h3>
          <div className="space-y-3">
            <div className="p-3 bg-blue-50 rounded-lg">
              <p className="font-semibold text-blue-800">자녀 교육비 마련</p>
              <p className="text-sm text-blue-600">목표: ₩30,000,000</p>
              <p className="text-sm text-blue-600">현재: ₩15,000,000 (50%)</p>
            </div>

            <div className="p-3 bg-green-50 rounded-lg">
              <p className="font-semibold text-green-800">내집마련 자금</p>
              <p className="text-sm text-green-600">목표: ₩100,000,000</p>
              <p className="text-sm text-green-600">현재: ₩25,000,000 (25%)</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
