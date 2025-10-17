"use client";

import { useParams } from "next/navigation";
import { WelfareBenefitDetail } from "../../../components/features/welfare/welfare-benefit-detail";

export default function WelfareDetailClient() {
  const params = useParams();
  const benefitId = params.id;

  return <WelfareBenefitDetail benefitId={benefitId} />;
}
