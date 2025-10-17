import BenefitDetailClient from "./BenefitDetailClient";

// 실제 DB에 있는 모든 혜택 ID를 기반으로 정적 페이지 생성
export async function generateStaticParams() {
  try {
    // 빌드 시 백엔드 API URL (환경에 따라 다름)
    const API_BASE_URL =
      process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080/api";

    console.log("🔍 [generateStaticParams] 사용 중인 API URL:", API_BASE_URL);

    // 백엔드 API에서 모든 혜택 ID 가져오기 (인증 불필요한 공개 API)
    const response = await fetch(
      `${API_BASE_URL}/hana-future-welfare/all?page=0&size=1000`,
      {
        cache: "no-store",
      }
    );

    console.log(
      "🔍 [generateStaticParams] API 응답 상태:",
      response.status,
      response.statusText
    );

    if (!response.ok) {
      console.error("혜택 목록 조회 실패, 더미 데이터 사용");
      return Array.from({ length: 200 }, (_, i) => ({ id: String(i + 1) }));
    }

    const result = await response.json();
    // HanaFutureWelfareController는 Page 형식으로 반환
    const benefits = result?.data?.content || [];

    console.log("🔍 [generateStaticParams] 혜택 개수:", benefits.length);
    console.log(
      "🔍 [generateStaticParams] 첫 3개 ID:",
      benefits.slice(0, 3).map((b) => b.id)
    );

    // 모든 혜택 ID를 정적 경로로 생성
    return benefits.map((benefit) => ({
      id: String(benefit.id),
    }));
  } catch (error) {
    console.error("generateStaticParams 오류:", error);
    return Array.from({ length: 200 }, (_, i) => ({ id: String(i + 1) }));
  }
}

export default function BenefitDetailPage() {
  return <BenefitDetailClient />;
}
