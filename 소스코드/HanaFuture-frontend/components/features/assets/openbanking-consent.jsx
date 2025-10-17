"use client";

import React, { useState, useEffect } from "react";
import {
  Shield,
  Check,
  ArrowRight,
  Building2,
  Lock,
  Eye,
  FileText,
  Clock,
} from "lucide-react";

export function OpenBankingConsent({ onConsent, onCancel }) {
  const [agreements, setAgreements] = useState({
    openBankingTerms: false,
    personalInfo: false,
    thirdPartyInfo: false,
  });

  const [allAgreed, setAllAgreed] = useState(false);
  const [expandedTerms, setExpandedTerms] = useState(null);

  // 컴포넌트 마운트 시 스크롤 최상단으로 이동
  useEffect(() => {
    window.scrollTo({ top: 0, behavior: "smooth" });
  }, []);

  const handleAgreementChange = (key, value) => {
    const newAgreements = { ...agreements, [key]: value };
    setAgreements(newAgreements);

    // 모든 필수 약관에 동의했는지 확인
    const requiredAgreements = ["openBankingTerms", "personalInfo"];
    const allRequired = requiredAgreements.every((key) => newAgreements[key]);
    setAllAgreed(allRequired);
  };

  const handleAllAgree = (checked) => {
    const newAgreements = {
      openBankingTerms: checked,
      personalInfo: checked,
      thirdPartyInfo: checked,
    };
    setAgreements(newAgreements);
    setAllAgreed(checked);
  };

  const toggleTerms = (termsKey) => {
    setExpandedTerms(expandedTerms === termsKey ? null : termsKey);
  };

  const termsContent = {
    openBankingTerms: {
      title: "오픈뱅킹공동업무 금융정보조회 약관",
      content: `제1조 (약관의 적용)

오픈뱅킹공동업무를 기반으로 이용기관이 개발한 핀테크 서비스를 통해 본인의 금융정보를 확인하고자 하는 개인(이하 "사용자"라 한다)과 하나모아(이하 "회사")에 대하여 본 약관을 적용합니다.

제2조 (용어의 정의)

① "오픈뱅킹공동업무"란 이용기관이 핀테크 서비스를 용이하게 개발하고, 제공할 수 있도록 금융권에서 공동으로 구축하여 운영하는 표준화된 오픈API 플랫폼에 대한 제반 업무를 말합니다.
② "이용기관"이란 금융결제원과 오픈뱅킹공동업무 이용계약을 체결하고 이용승인을 받은 기업을 말합니다.
③ "금융정보조회"란 사용자가 핀테크 서비스를 통해 제5조에 명시하는 본인의 금융정보조회 요청 시 회사가 금융정보를 제공하는 업무를 말합니다.
④ "오픈뱅킹중계센터"란 오픈뱅킹공동업무 시스템을 구축하고 운영하며, 회사와 이용기관을 서로 중계하는 곳으로 금융결제원이 운영합니다.

제3조 (금융정보조회 신청)

① 사용자는 본인의 금융정보를 조회하기 위해서는 이용기관 또는 오픈뱅킹중계센터가 제공하는 웹페이지 또는 앱에서 금융정보조회 신청을 하며, 관련 정보는 해당 금융회사로 전달됩니다.
② 사용자가 금융정보조회 신청 시 이용기관으로 금융정보가 제공될 수 있도록 금융실명거래 및 비밀보장에 관한 법률 제4조 제1항, 신용정보의 이용 및 보호에 관한 법률 제32조 제1항에 따라 회원 단위로 서면(전자서명이 있는 전자문서 포함), ARS, 녹취 등에 의하여 동의를 하여야 합니다.
③ 사용자는 자신의 정보를 보호받고 관리하기 위해 금융정보조회 신청을 1년 단위로 재신청하여야 합니다.
④ 회사는 개인정보보호법 및 신용정보의 이용 및 보호에 관한 법률 등에서 정한 바에 따라 사용자의 정보를 관리하여야 하며, 이에 대한 세부적인 내용은 회사의 개인정보처리(취급) 방침에 정한 바에 의합니다. 회사는 개인정보처리(취급) 방침에 대한 세부내용을 홈페이지에 게시하여 사용자가 확인할 수 있도록 합니다.

제4조 (금융정보조회 이용시간)

금융정보조회 이용시간은 연중무휴로 00시10분부터 23시50분까지로 합니다.

제5조 (금융정보 종류)

회사에서 제공하는 금융정보의 종류는 다음 각 호와 같습니다.
1. 카드정보조회 : 사용자 본인(본인의 가족카드 포함)의 카드목록, 보유한 카드별 기본정보 및 연계된 결제계좌정보 등 실시간 조회
2. 카드청구정보조회 : 사용자 본인(본인의 가족카드 포함)의 청구금액 및 결제일, 월별 청구금액에 대한 카드이용내역(사용일, 이용금액, 가맹점명(절반 마스킹), 할부·해외 등 이용 수수료 등)(본인의 가족카드 제외) 조회

제6조 (금융정보제공 및 금융정보 제공사실 통보)

① 사용자가 핀테크 서비스를 통해 회사로 금융정보조회를 요청하는 경우에는 회사는 별도의 통지 없이 사용자의 금융정보 내역을 오픈뱅킹중계센터로 통지하며, 오픈뱅킹중계센터는 사용자일련번호를 기반으로 이용기관에 제공합니다.
② 금융실명거래 및 비밀보장에 관한 법률 제4조의2 제1항에 따라 사용자가 오픈뱅킹중계센터에 등록한 연락처로 회사는 사용자에게 금융정보 제공사실을 서면(전자문서 포함)으로 통보하여야 합니다.
③ 사용자는 연락처가 변경되는 경우에는 오픈뱅킹중계센터에 등록한 연락처를 변경하여야 합니다.
④ 회사는 사용자의 편익을 위해 금융정보 제공사실을 1년 이내 단위로 통보할 수 있습니다.

제7조 (금융정보조회 중단)

① 회사는 오픈뱅킹공동업무 관련 시스템의 장애 및 유지보수 등 부득이한 사유가 발생한 경우 금융정보조회를 중단할 수 있습니다.
② 제1항에 따라 금융정보조회를 중단하는 경우 회사는 사전에 공지합니다. 다만, 불가피하게 사전 공지를 할 수 없는 경우 회사는 이를 사후 공지할 수 있습니다.

제8조 (금융정보조회 해지)

① 사용자는 금융정보조회 해지를 이용기관 또는 오픈뱅킹중계센터에서 제공하는 웹페이지 또는 앱에서 신청하여야 합니다.
② 회사는 사용자가 다음 각 호에 해당할 때에는 상당한 기간을 정하여 사용자에게 최고하고 그 기간 내에 해당 사유가 해소되지 않는 경우 금융정보조회를 해지할 수 있습니다. 다만, 해당 사유가 해소되는 것이 불가능한 상황이거나 관련 법규에서 별도로 정하는 경우에는 최고하지 아니할 수 있습니다.
  1. 사용자가 타인의 정보를 도용하거나 허위내용을 등록한 경우
  2. 1년 이상 이용기관의 조회요청이 없는 경우
③ 제2항에 따라 금융정보조회를 해지하는 경우 회사는 사전에 통지하며, 사용자는 이의를 신청할 수 있습니다.

제9조 (면책 사항)

본 약관의 면책 사항에 대해서는 전자금융거래 이용에 관한 기본약관을 준용합니다.

제10조 (관할 법원)

본 약관에 따른 금융정보조회와 관련하여 회사와 사용자 사이에 소송의 필요가 있는 경우 관할법원은 민사소송법에서 정하는 바에 따릅니다.

제11조 (약관의 변경)

회사가 본 약관을 변경하고자 하는 경우에는 전자금융거래 이용에 관한 기본약관의 약관의 변경 조항을 준용합니다.

제12조 (다른 약관과의 관계)

① 금융정보조회에는 본 약관 외에도 전자금융거래 이용에 관한 기본약관이 적용되며, 규정된 내용이 서로 다를 경우 본 약관의 규정이 우선합니다.
② 본 약관과 전자금융거래 이용에 관한 기본약관에 정하지 않은 사항에 대하여는 다른 약정이 없으면 전자금융거래법, 금융실명거래 및 비밀보장에 관한 법률 등 관계법령을 적용합니다.

부칙 <제정>
본 약관은 2021년 5월 31일부터 시행합니다.`,
    },
    serviceTerms: {
      title: "하나Future 서비스 이용약관",
      content: `제1조 (목적)
본 약관은 하나Future(이하 "회사")가 제공하는 서비스의 이용과 관련하여 회사와 회원 간의 권리, 의무 및 책임사항을 규정함을 목적으로 합니다.

제2조 (정의)
① "서비스"란 회사가 제공하는 모든 금융 관련 서비스를 의미합니다.
② "회원"이란 회사와 서비스 이용계약을 체결하고 회사가 제공하는 서비스를 이용하는 자를 말합니다.

제3조 (약관의 효력 및 변경)
① 본 약관은 서비스를 이용하고자 하는 모든 회원에 대하여 그 효력을 발생합니다.
② 회사는 필요한 경우 관련 법령을 위배하지 않는 범위 내에서 본 약관을 변경할 수 있습니다.

제4조 (서비스의 제공 및 변경)
① 회사는 다음과 같은 업무를 수행합니다.
  1. 금융 관련 정보 제공
  2. 모임통장, 함께 적금 등 금융 서비스 제공
  3. 기타 회사가 정하는 업무
② 회사는 필요한 경우 서비스의 내용을 변경할 수 있습니다.`,
    },
    personalInfo: {
      title: "개인정보 수집·이용 동의",
      content: `제1조 (개인정보의 수집 및 이용 목적)
회사는 다음의 목적을 위하여 개인정보를 수집 및 이용합니다.
1. 본인 확인 및 인증
2. 금융 서비스 제공
3. 고객 상담 및 불만 처리
4. 법령상 의무 이행

제2조 (수집하는 개인정보의 항목)
회사는 다음의 개인정보를 수집합니다.
1. 필수항목: 이름, 생년월일, 연락처, 이메일 주소
2. 선택항목: 주소, 직업 정보

제3조 (개인정보의 보유 및 이용 기간)
회사는 원칙적으로 개인정보 수집 및 이용목적이 달성된 후에는 해당 정보를 지체 없이 파기합니다. 단, 관련 법령에 따라 일정 기간 보관해야 하는 경우에는 해당 기간 동안 보관합니다.

제4조 (동의를 거부할 권리 및 불이익)
이용자는 개인정보 수집 및 이용에 대한 동의를 거부할 수 있습니다. 다만, 필수항목에 대한 동의를 거부하실 경우 서비스 이용이 제한될 수 있습니다.`,
    },
    thirdPartyInfo: {
      title: "개인정보 제3자 제공 동의",
      content: `제1조 (개인정보를 제공받는 자)
금융상품 제공 파트너사

제2조 (개인정보를 제공받는 자의 이용 목적)
맞춤형 금융상품 추천 및 마케팅

제3조 (제공하는 개인정보의 항목)
이름, 연령대, 금융 이용 패턴

제4조 (개인정보를 제공받는 자의 보유 및 이용 기간)
제공 목적 달성 시까지

제5조 (동의를 거부할 권리 및 불이익)
이용자는 개인정보 제3자 제공에 대한 동의를 거부할 수 있으며, 거부 시에도 기본 서비스 이용에는 제한이 없습니다. 다만, 맞춤형 추천 서비스는 제공되지 않습니다.`,
    },
  };

  const agreementItems = [
    {
      key: "openBankingTerms",
      title: "오픈뱅킹공동업무 금융정보조회 약관",
      required: true,
      icon: Building2,
      description:
        "계좌 정보 조회 및 금융거래를 위한 오픈뱅킹 서비스 이용에 동의합니다.",
    },
    {
      key: "personalInfo",
      title: "개인정보 수집·이용 동의",
      required: true,
      icon: Lock,
      description: "본인 인증 및 계좌 연동을 위한 개인정보 처리에 동의합니다.",
    },
    {
      key: "thirdPartyInfo",
      title: "개인정보 제3자 제공 동의",
      required: false,
      icon: Eye,
      description: "맞춤형 금융상품 추천을 위한 정보 제공에 동의합니다. (선택)",
    },
  ];

  return (
    <div className="bg-gray-50 py-8 px-4">
      <div className="w-full max-w-2xl mx-auto">
        {/* 헤더 */}
        <div className="bg-white rounded-t-2xl p-8 border-b border-gray-100">
          <div className="text-center">
            <h1 className="text-2xl font-bold text-gray-900 mb-2">
              오픈뱅킹 서비스 동의
            </h1>
            <p className="text-gray-600 leading-relaxed">
              모임통장 개설을 위해 오픈뱅킹 계좌 등록이 필요합니다.
              <br />
              아래 약관을 확인하고 동의해주세요.
            </p>
          </div>
        </div>

        {/* 약관 동의 */}
        <div className="bg-white p-8">
          {/* 전체 동의 */}
          <div className="mb-6">
            <label className="flex items-center p-4 border-2 border-gray-300 rounded-xl bg-white cursor-pointer hover:bg-gray-50 transition-colors">
              <input
                type="checkbox"
                checked={allAgreed}
                onChange={(e) => handleAllAgree(e.target.checked)}
                className="sr-only"
              />
              <div
                className={`w-6 h-6 rounded-full border-2 flex items-center justify-center mr-4 transition-colors ${
                  allAgreed
                    ? "bg-emerald-600 border-emerald-600"
                    : "border-gray-300 bg-white"
                }`}
              >
                {allAgreed && <Check className="w-4 h-4 text-white" />}
              </div>
              <div className="flex-1">
                <span className="font-semibold text-gray-900">
                  전체 동의하기
                </span>
                <p className="text-sm text-gray-600 mt-1">
                  필수 및 선택 항목에 모두 동의합니다.
                </p>
              </div>
            </label>
          </div>

          {/* 개별 약관 */}
          <div className="space-y-4">
            {agreementItems.map((item) => {
              const IconComponent = item.icon;
              const isChecked = agreements[item.key];

              const isExpanded = expandedTerms === item.key;

              return (
                <div
                  key={item.key}
                  className="border border-gray-200 rounded-xl overflow-hidden"
                >
                  <div className="flex items-start p-4 hover:bg-gray-50 transition-colors">
                    <label className="flex items-start flex-1 cursor-pointer">
                      <input
                        type="checkbox"
                        checked={isChecked}
                        onChange={(e) =>
                          handleAgreementChange(item.key, e.target.checked)
                        }
                        className="sr-only"
                      />
                      <div
                        className={`w-5 h-5 rounded border-2 flex items-center justify-center mr-4 mt-0.5 flex-shrink-0 transition-colors ${
                          isChecked
                            ? "bg-emerald-600 border-emerald-600"
                            : "border-gray-300 bg-white"
                        }`}
                      >
                        {isChecked && <Check className="w-3 h-3 text-white" />}
                      </div>

                      <div className="flex-1">
                        <div className="flex items-center mb-1">
                          <IconComponent className="w-4 h-4 text-gray-500 mr-2" />
                          <span className="font-medium text-gray-900">
                            {item.title}
                          </span>
                          {item.required && (
                            <span className="ml-2 px-2 py-0.5 bg-red-100 text-red-600 text-xs rounded-full">
                              필수
                            </span>
                          )}
                        </div>
                        <p className="text-sm text-gray-600">
                          {item.description}
                        </p>
                      </div>
                    </label>

                    <button
                      onClick={(e) => {
                        e.preventDefault();
                        toggleTerms(item.key);
                      }}
                      className="text-gray-400 hover:text-gray-600 ml-2 p-1 transition-transform"
                      style={{
                        transform: isExpanded
                          ? "rotate(90deg)"
                          : "rotate(0deg)",
                      }}
                    >
                      <ArrowRight className="w-4 h-4" />
                    </button>
                  </div>

                  {/* 약관 내용 펼치기 */}
                  {isExpanded && (
                    <div className="border-t border-gray-200 bg-gray-50 p-6 max-h-96 overflow-y-auto">
                      <div className="text-sm text-gray-700 whitespace-pre-wrap leading-relaxed">
                        {termsContent[item.key]?.content}
                      </div>
                    </div>
                  )}
                </div>
              );
            })}
          </div>

          {/* 유효기간 안내 */}
          <div className="mt-6 p-4 bg-white rounded-xl border border-gray-300">
            <div className="flex items-start">
              <Clock className="w-5 h-5 text-gray-600 mr-3 mt-0.5" />
              <div className="text-sm">
                <p className="font-medium text-gray-900 mb-1">동의 유효기간</p>
                <p className="text-gray-600">
                  오픈뱅킹 서비스 동의는 1년간 유효하며, 기간 만료 전 재동의가
                  필요합니다.
                </p>
              </div>
            </div>
          </div>
        </div>

        {/* 버튼 */}
        <div className="bg-white rounded-b-2xl p-8 border-t border-gray-100">
          <div className="flex space-x-4">
            <button
              onClick={onCancel}
              className="flex-1 px-6 py-4 border border-gray-300 text-gray-700 rounded-xl hover:bg-gray-50 transition-colors font-medium"
            >
              취소
            </button>
            <button
              onClick={() => onConsent(agreements)}
              disabled={!allAgreed}
              className={`flex-1 px-6 py-4 rounded-xl font-medium transition-colors ${
                allAgreed
                  ? "bg-emerald-600 text-white hover:bg-emerald-700"
                  : "bg-gray-300 text-gray-500 cursor-not-allowed"
              }`}
            >
              동의하고 계속하기
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
