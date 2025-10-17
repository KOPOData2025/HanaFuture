"use client";

export function BudgetManagement() {
  return (
    <div className="space-y-6">
      <div className="flex items-center space-x-4">
        <img
          src="/hanacharacter/hanacharacter12.png"
          alt="예산 관리 하나 캐릭터"
          className="w-28 h-28 object-contain"
        />
        <div>
          <h2 className="font-heading font-bold text-2xl text-foreground mb-2">
            예산 관리
          </h2>
          <p className="text-muted-foreground">
            가족의 지출을 체계적으로 관리하세요
          </p>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div className="p-6 bg-card rounded-xl border shadow-sm">
          <h3 className="font-semibold mb-4">이번 달 예산</h3>
          <div className="space-y-3">
            <div className="flex justify-between">
              <span>육아비</span>
              <span className="font-semibold">₩800,000 / ₩1,000,000</span>
            </div>
            <div className="w-full bg-gray-200 rounded-full h-2">
              <div
                className="bg-primary h-2 rounded-full"
                style={{ width: "80%" }}
              ></div>
            </div>

            <div className="flex justify-between">
              <span>생활비</span>
              <span className="font-semibold">₩1,200,000 / ₩1,500,000</span>
            </div>
            <div className="w-full bg-gray-200 rounded-full h-2">
              <div
                className="bg-primary h-2 rounded-full"
                style={{ width: "80%" }}
              ></div>
            </div>
          </div>
        </div>

        <div className="p-6 bg-card rounded-xl border shadow-sm">
          <h3 className="font-semibold mb-4">카테고리별 지출</h3>
          <div className="space-y-2">
            <div className="flex justify-between">
              <span>육아용품</span>
              <span className="font-semibold">₩450,000</span>
            </div>
            <div className="flex justify-between">
              <span>식비</span>
              <span className="font-semibold">₩380,000</span>
            </div>
            <div className="flex justify-between">
              <span>교통비</span>
              <span className="font-semibold">₩120,000</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
