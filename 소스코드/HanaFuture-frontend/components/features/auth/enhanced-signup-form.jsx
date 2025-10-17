"use client";

import React, { useState, useEffect } from "react";
import { useAuth } from "../../../contexts/AuthContext";
import { DatePicker } from "../../ui/date-picker";
import { EnhancedSelect } from "../../ui/enhanced-select";
import { CheckboxGroup } from "../../ui/checkbox-group";
import {
  User,
  Mail,
  Phone,
  Lock,
  MapPin,
  Heart,
  Baby,
  DollarSign,
  Users,
  Calendar,
  ChevronRight,
  ChevronLeft,
  CheckCircle,
  AlertCircle,
  ArrowRight,
} from "lucide-react";

// API Base URL
const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080/api";

export function ExtendedSignupForm({
  onComplete,
  onCancel,
  onSuccess,
  onSkip,
  initialEmail = "",
  initialPassword = "",
  initialData = null,
  isOAuth2User = false,
}) {
  const [currentStep, setCurrentStep] = useState(isOAuth2User ? 2 : 1);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  // 전화번호 인증 상태
  const [phoneVerification, setPhoneVerification] = useState({
    verificationCode: "",
    isCodeSent: false,
    timeLeft: 300, // 5분
    isVerified: false,
    verificationId: null,
  });

  const [formData, setFormData] = useState({
    // 기본 정보
    email: initialData?.email || initialEmail,
    password: isOAuth2User ? "" : initialPassword,
    confirmPassword: isOAuth2User ? "" : initialPassword,
    name: initialData?.name || "",
    phoneNumber: initialData?.phoneNumber || "",

    // 개인 정보
    birthDate: "",
    gender: "",
    residenceSido: "",
    residenceSigungu: "",

    // 가족 정보
    maritalStatus: "",
    numberOfChildren: 0,
    isPregnant: false,
    expectedDueDate: "",

    // 경제 정보
    incomeLevel: "",
    hasDisability: false,
    isSingleParent: false,
    isMulticultural: false,

    // 관심 분야
    interestCategories: [],

    // 동의
    agreeToPersonalInfo: false,
    agreeToWelfareInfo: false,
  });

  const { extendedSignUp } = useAuth();

  const totalSteps = 5;

  // 전화번호 인증 타이머
  useEffect(() => {
    let timer;
    if (phoneVerification.isCodeSent && phoneVerification.timeLeft > 0) {
      timer = setInterval(() => {
        setPhoneVerification((prev) => ({
          ...prev,
          timeLeft: prev.timeLeft - 1,
        }));
      }, 1000);
    }
    return () => clearInterval(timer);
  }, [phoneVerification.isCodeSent, phoneVerification.timeLeft]);

  const formatTime = (seconds) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, "0")}`;
  };

  const stepInfo = [
    {
      title: "기본 정보",
      icon: User,
      description: "계정 생성을 위한 기본 정보",
    },
    {
      title: "개인 정보",
      icon: Calendar,
      description: "생년월일과 거주지 정보",
    },
    { title: "가족 정보", icon: Heart, description: "가족 구성과 현재 상황" },
    {
      title: "경제 정보",
      icon: DollarSign,
      description: "복지 혜택 자격 확인",
    },
    {
      title: "관심 분야",
      icon: CheckCircle,
      description: "관심 있는 복지 분야",
    },
  ];

  const genderOptions = [
    { value: "MALE", label: "남성", icon: "👨" },
    { value: "FEMALE", label: "여성", icon: "👩" },
    { value: "OTHER", label: "기타", icon: "🏳️‍⚧️" },
  ];

  const sidoOptions = [
    { value: "서울특별시", label: "서울특별시", icon: "🏙️" },
    { value: "부산광역시", label: "부산광역시", icon: "🌊" },
    { value: "대구광역시", label: "대구광역시", icon: "🏔️" },
    { value: "인천광역시", label: "인천광역시", icon: "✈️" },
    { value: "광주광역시", label: "광주광역시", icon: "🌸" },
    { value: "대전광역시", label: "대전광역시", icon: "🚄" },
    { value: "울산광역시", label: "울산광역시", icon: "🏭" },
    { value: "세종특별자치시", label: "세종특별자치시", icon: "🏛️" },
    { value: "경기도", label: "경기도", icon: "🌳" },
    { value: "강원도", label: "강원도", icon: "⛰️" },
    { value: "충청북도", label: "충청북도", icon: "🌾" },
    { value: "충청남도", label: "충청남도", icon: "🌾" },
    { value: "전라북도", label: "전라북도", icon: "🌾" },
    { value: "전라남도", label: "전라남도", icon: "🌾" },
    { value: "경상북도", label: "경상북도", icon: "🏔️" },
    { value: "경상남도", label: "경상남도", icon: "🏔️" },
    { value: "제주특별자치도", label: "제주특별자치도", icon: "🌺" },
  ];

  // 시/군/구 데이터 (시/도별로 구성)
  const sigunguOptions = {
    서울특별시: [
      { value: "강남구", label: "강남구", icon: "🏢" },
      { value: "강동구", label: "강동구", icon: "🏘️" },
      { value: "강북구", label: "강북구", icon: "🏘️" },
      { value: "강서구", label: "강서구", icon: "✈️" },
      { value: "관악구", label: "관악구", icon: "🏔️" },
      { value: "광진구", label: "광진구", icon: "🏘️" },
      { value: "구로구", label: "구로구", icon: "🏭" },
      { value: "금천구", label: "금천구", icon: "🏘️" },
      { value: "노원구", label: "노원구", icon: "🏘️" },
      { value: "도봉구", label: "도봉구", icon: "🏘️" },
      { value: "동대문구", label: "동대문구", icon: "🏘️" },
      { value: "동작구", label: "동작구", icon: "🏘️" },
      { value: "마포구", label: "마포구", icon: "🌉" },
      { value: "서대문구", label: "서대문구", icon: "🏘️" },
      { value: "서초구", label: "서초구", icon: "🏢" },
      { value: "성동구", label: "성동구", icon: "🏘️" },
      { value: "성북구", label: "성북구", icon: "🏘️" },
      { value: "송파구", label: "송파구", icon: "🏘️" },
      { value: "양천구", label: "양천구", icon: "🏘️" },
      { value: "영등포구", label: "영등포구", icon: "🏢" },
      { value: "용산구", label: "용산구", icon: "🏘️" },
      { value: "은평구", label: "은평구", icon: "🏘️" },
      { value: "종로구", label: "종로구", icon: "🏛️" },
      { value: "중구", label: "중구", icon: "🏛️" },
      { value: "중랑구", label: "중랑구", icon: "🏘️" },
    ],
    부산광역시: [
      { value: "강서구", label: "강서구", icon: "🏘️" },
      { value: "금정구", label: "금정구", icon: "⛰️" },
      { value: "기장군", label: "기장군", icon: "🌊" },
      { value: "남구", label: "남구", icon: "🏘️" },
      { value: "동구", label: "동구", icon: "🏘️" },
      { value: "동래구", label: "동래구", icon: "♨️" },
      { value: "부산진구", label: "부산진구", icon: "🏘️" },
      { value: "북구", label: "북구", icon: "🏘️" },
      { value: "사상구", label: "사상구", icon: "🏭" },
      { value: "사하구", label: "사하구", icon: "🏘️" },
      { value: "서구", label: "서구", icon: "🏘️" },
      { value: "수영구", label: "수영구", icon: "🏖️" },
      { value: "연제구", label: "연제구", icon: "🏘️" },
      { value: "영도구", label: "영도구", icon: "🏝️" },
      { value: "중구", label: "중구", icon: "🏛️" },
      { value: "해운대구", label: "해운대구", icon: "🏖️" },
    ],
    경기도: [
      { value: "가평군", label: "가평군", icon: "🏞️" },
      { value: "고양시", label: "고양시", icon: "🏘️" },
      { value: "과천시", label: "과천시", icon: "🏘️" },
      { value: "광명시", label: "광명시", icon: "🏘️" },
      { value: "광주시", label: "광주시", icon: "🏘️" },
      { value: "구리시", label: "구리시", icon: "🏘️" },
      { value: "군포시", label: "군포시", icon: "🏘️" },
      { value: "김포시", label: "김포시", icon: "✈️" },
      { value: "남양주시", label: "남양주시", icon: "🏘️" },
      { value: "동두천시", label: "동두천시", icon: "🏘️" },
      { value: "부천시", label: "부천시", icon: "🏘️" },
      { value: "성남시", label: "성남시", icon: "🏢" },
      { value: "수원시", label: "수원시", icon: "🏰" },
      { value: "시흥시", label: "시흥시", icon: "🏘️" },
      { value: "안산시", label: "안산시", icon: "🏭" },
      { value: "안성시", label: "안성시", icon: "🏘️" },
      { value: "안양시", label: "안양시", icon: "🏘️" },
      { value: "양주시", label: "양주시", icon: "🏘️" },
      { value: "양평군", label: "양평군", icon: "🏞️" },
      { value: "여주시", label: "여주시", icon: "🏘️" },
      { value: "연천군", label: "연천군", icon: "🏞️" },
      { value: "오산시", label: "오산시", icon: "🏘️" },
      { value: "용인시", label: "용인시", icon: "🏘️" },
      { value: "의왕시", label: "의왕시", icon: "🏘️" },
      { value: "의정부시", label: "의정부시", icon: "🏘️" },
      { value: "이천시", label: "이천시", icon: "🏺" },
      { value: "파주시", label: "파주시", icon: "🏘️" },
      { value: "평택시", label: "평택시", icon: "🏘️" },
      { value: "포천시", label: "포천시", icon: "🏞️" },
      { value: "하남시", label: "하남시", icon: "🏘️" },
      { value: "화성시", label: "화성시", icon: "🏘️" },
    ],
    인천광역시: [
      { value: "계양구", label: "계양구", icon: "🏘️" },
      { value: "미추홀구", label: "미추홀구", icon: "🏘️" },
      { value: "남동구", label: "남동구", icon: "🏘️" },
      { value: "동구", label: "동구", icon: "🏘️" },
      { value: "부평구", label: "부평구", icon: "🏘️" },
      { value: "서구", label: "서구", icon: "🏘️" },
      { value: "연수구", label: "연수구", icon: "🏘️" },
      { value: "중구", label: "중구", icon: "🏛️" },
      { value: "강화군", label: "강화군", icon: "🏝️" },
      { value: "옹진군", label: "옹진군", icon: "🏝️" },
    ],
    대구광역시: [
      { value: "중구", label: "중구", icon: "🏛️" },
      { value: "동구", label: "동구", icon: "🏘️" },
      { value: "서구", label: "서구", icon: "🏘️" },
      { value: "남구", label: "남구", icon: "🏘️" },
      { value: "북구", label: "북구", icon: "🏘️" },
      { value: "수성구", label: "수성구", icon: "🏘️" },
      { value: "달서구", label: "달서구", icon: "🏘️" },
      { value: "달성군", label: "달성군", icon: "🏞️" },
    ],
    광주광역시: [
      { value: "동구", label: "동구", icon: "🏘️" },
      { value: "서구", label: "서구", icon: "🏘️" },
      { value: "남구", label: "남구", icon: "🏘️" },
      { value: "북구", label: "북구", icon: "🏘️" },
      { value: "광산구", label: "광산구", icon: "🏘️" },
    ],
    대전광역시: [
      { value: "중구", label: "중구", icon: "🏛️" },
      { value: "동구", label: "동구", icon: "🏘️" },
      { value: "서구", label: "서구", icon: "🏘️" },
      { value: "유성구", label: "유성구", icon: "♨️" },
      { value: "대덕구", label: "대덕구", icon: "🏘️" },
    ],
    울산광역시: [
      { value: "중구", label: "중구", icon: "🏛️" },
      { value: "남구", label: "남구", icon: "🏘️" },
      { value: "동구", label: "동구", icon: "🏘️" },
      { value: "북구", label: "북구", icon: "🏘️" },
      { value: "울주군", label: "울주군", icon: "🏞️" },
    ],
    세종특별자치시: [{ value: "세종시", label: "세종시", icon: "🏛️" }],
    제주특별자치도: [
      { value: "제주시", label: "제주시", icon: "🏝️" },
      { value: "서귀포시", label: "서귀포시", icon: "🏝️" },
    ],
    강원도: [
      { value: "춘천시", label: "춘천시", icon: "🏞️" },
      { value: "원주시", label: "원주시", icon: "🏘️" },
      { value: "강릉시", label: "강릉시", icon: "🏖️" },
      { value: "동해시", label: "동해시", icon: "🌊" },
      { value: "태백시", label: "태백시", icon: "⛰️" },
      { value: "속초시", label: "속초시", icon: "🏖️" },
      { value: "삼척시", label: "삼척시", icon: "🏞️" },
    ],
    충청북도: [
      { value: "청주시", label: "청주시", icon: "🏘️" },
      { value: "충주시", label: "충주시", icon: "🏘️" },
      { value: "제천시", label: "제천시", icon: "🏞️" },
      { value: "보은군", label: "보은군", icon: "🏞️" },
      { value: "옥천군", label: "옥천군", icon: "🏞️" },
      { value: "영동군", label: "영동군", icon: "🏞️" },
    ],
    충청남도: [
      { value: "천안시", label: "천안시", icon: "🏘️" },
      { value: "공주시", label: "공주시", icon: "🏛️" },
      { value: "보령시", label: "보령시", icon: "🏖️" },
      { value: "아산시", label: "아산시", icon: "🏘️" },
      { value: "서산시", label: "서산시", icon: "🏘️" },
      { value: "논산시", label: "논산시", icon: "🏘️" },
      { value: "계룡시", label: "계룡시", icon: "🏘️" },
      { value: "당진시", label: "당진시", icon: "🏘️" },
    ],
    전라북도: [
      { value: "전주시", label: "전주시", icon: "🏛️" },
      { value: "군산시", label: "군산시", icon: "🏘️" },
      { value: "익산시", label: "익산시", icon: "🏘️" },
      { value: "정읍시", label: "정읍시", icon: "🏘️" },
      { value: "남원시", label: "남원시", icon: "🏞️" },
      { value: "김제시", label: "김제시", icon: "🏘️" },
    ],
    전라남도: [
      { value: "목포시", label: "목포시", icon: "🌊" },
      { value: "여수시", label: "여수시", icon: "🏖️" },
      { value: "순천시", label: "순천시", icon: "🌿" },
      { value: "나주시", label: "나주시", icon: "🏘️" },
      { value: "광양시", label: "광양시", icon: "🏭" },
    ],
    경상북도: [
      { value: "포항시", label: "포항시", icon: "🏭" },
      { value: "경주시", label: "경주시", icon: "🏛️" },
      { value: "김천시", label: "김천시", icon: "🏘️" },
      { value: "안동시", label: "안동시", icon: "🏛️" },
      { value: "구미시", label: "구미시", icon: "🏭" },
      { value: "영주시", label: "영주시", icon: "🏘️" },
      { value: "영천시", label: "영천시", icon: "🏘️" },
      { value: "상주시", label: "상주시", icon: "🏘️" },
      { value: "문경시", label: "문경시", icon: "🏞️" },
      { value: "경산시", label: "경산시", icon: "🏘️" },
    ],
    경상남도: [
      { value: "창원시", label: "창원시", icon: "🏭" },
      { value: "진주시", label: "진주시", icon: "🏘️" },
      { value: "통영시", label: "통영시", icon: "🏖️" },
      { value: "사천시", label: "사천시", icon: "🏘️" },
      { value: "김해시", label: "김해시", icon: "🏘️" },
      { value: "밀양시", label: "밀양시", icon: "🏘️" },
      { value: "거제시", label: "거제시", icon: "🏝️" },
      { value: "양산시", label: "양산시", icon: "🏘️" },
    ],
  };

  // 선택된 시/도에 따른 시/군/구 옵션 필터링
  const getAvailableSigunguOptions = () => {
    if (!formData.residenceSido) return [];
    return sigunguOptions[formData.residenceSido] || [];
  };

  const maritalStatusOptions = [
    { value: "SINGLE", label: "미혼", icon: "💙" },
    { value: "MARRIED", label: "기혼", icon: "💍" },
    { value: "DIVORCED", label: "이혼", icon: "💔" },
    { value: "WIDOWED", label: "사별", icon: "🖤" },
    { value: "SEPARATED", label: "별거", icon: "💛" },
  ];

  const incomeLevelOptions = [
    {
      value: "BASIC_LIVELIHOOD",
      label: "기초생활수급자",
      description: "기준 중위소득 30% 이하",
      icon: "🆘",
    },
    {
      value: "LOW_INCOME",
      label: "차상위계층",
      description: "기준 중위소득 50% 이하",
      icon: "📊",
    },
    {
      value: "MIDDLE_LOW",
      label: "중위소득 80% 이하",
      description: "일반 복지서비스 대상",
      icon: "📈",
    },
    {
      value: "MIDDLE",
      label: "중위소득 100% 이하",
      description: "일부 복지서비스 대상",
      icon: "💼",
    },
    {
      value: "MIDDLE_HIGH",
      label: "중위소득 150% 이하",
      description: "제한적 복지서비스 대상",
      icon: "💰",
    },
    {
      value: "HIGH",
      label: "고소득",
      description: "일반적으로 복지서비스 대상 아님",
      icon: "💎",
    },
  ];

  const interestOptions = [
    {
      value: "임신출산",
      label: "🤱 임신·출산",
      description: "산모 지원, 출산 준비",
    },
    {
      value: "보육",
      label: "👶 보육·돌봄",
      description: "어린이집, 돌봄 서비스",
    },
    { value: "교육", label: "📚 교육", description: "학비, 교육비 지원" },
    { value: "주거", label: "🏠 주거", description: "전세, 월세, 주택 지원" },
    { value: "일자리", label: "💼 일자리", description: "취업, 창업 지원" },
    {
      value: "생활지원",
      label: "🎁 생활지원",
      description: "생계, 생활비 지원",
    },
    { value: "의료", label: "🏥 의료", description: "의료비, 건강검진" },
    { value: "문화", label: "🎭 문화·여가", description: "문화생활, 여가활동" },
  ];

  const handleNext = () => {
    if (validateCurrentStep()) {
      setCurrentStep((prev) => Math.min(prev + 1, totalSteps));
      setError("");
    }
  };

  const handlePrev = () => {
    setCurrentStep((prev) => Math.max(prev - 1, 1));
    setError("");
  };

  const handleInputChange = (field, value) => {
    setFormData((prev) => {
      const newData = { ...prev, [field]: value };

      // 시/도가 변경되면 시/군/구 초기화
      if (field === "residenceSido") {
        newData.residenceSigungu = "";
      }

      return newData;
    });
    setError("");
  };

  // 전화번호 포맷팅
  const formatPhoneNumber = (value) => {
    const numbers = value.replace(/[^\d]/g, "").trim();
    if (numbers.length <= 3) {
      return numbers;
    } else if (numbers.length <= 7) {
      return `${numbers.slice(0, 3)}-${numbers.slice(3)}`;
    }
    return `${numbers.slice(0, 3)}-${numbers.slice(3, 7)}-${numbers.slice(
      7,
      11
    )}`;
  };

  // 인증번호 발송
  const handleSendVerificationCode = async () => {
    if (!formData.phoneNumber) {
      setError("전화번호를 입력해주세요.");
      return;
    }

    const phoneNumber = formData.phoneNumber.trim();
    console.log("입력된 전화번호:", phoneNumber);
    console.log("전화번호 길이:", phoneNumber.length);
    console.log("전화번호 타입:", typeof phoneNumber);

    const phoneRegex = /^010-\d{4}-\d{4}$/;
    if (!phoneRegex.test(phoneNumber)) {
      setError(
        `올바른 전화번호 형식을 입력해주세요. (예: 010-1234-5678) [입력값: "${phoneNumber}"]`
      );
      return;
    }

    try {
      setLoading(true);
      console.log("인증번호 발송 요청:", formData.phoneNumber);

      const response = await fetch(`${API_BASE_URL}/auth/send-sms`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          name: formData.name,
          phoneNumber: phoneNumber, // 하이픈 포함된 형식 유지
          residentNumber: "9999991234567", // 임시값 (실제로는 입력받아야 함)
          carrier: "SKT", // 임시값 (실제로는 선택받아야 함)
        }),
      });

      const data = await response.json();

      if (response.ok) {
        setPhoneVerification({
          ...phoneVerification,
          isCodeSent: true,
          timeLeft: 300,
          isVerified: false,
          verificationId: data.verificationId || null,
        });
        setError("");
        // alert 제거 - 인증번호 입력 UI가 표시되므로 별도 알림 불필요
      } else {
        setError(data.message || "인증번호 발송에 실패했습니다.");
      }
    } catch (err) {
      console.error("인증번호 발송 오류:", err);
      setError("인증번호 발송에 실패했습니다.");
    } finally {
      setLoading(false);
    }
  };

  // 인증번호 확인
  const handleVerifyCode = async () => {
    if (!phoneVerification.verificationCode) {
      setError("인증번호를 입력해주세요.");
      return;
    }

    try {
      setLoading(true);
      console.log("인증번호 확인 요청:", {
        phoneNumber: formData.phoneNumber,
        code: phoneVerification.verificationCode,
      });

      const phoneNumber = formData.phoneNumber.trim();
      const code = phoneVerification.verificationCode;

      const response = await fetch(
        `${API_BASE_URL}/auth/verify-sms?phoneNumber=${encodeURIComponent(
          phoneNumber
        )}&code=${encodeURIComponent(code)}`,
        {
          method: "POST",
        }
      );

      const data = await response.json();

      if (response.ok && data.data === true) {
        setPhoneVerification({
          ...phoneVerification,
          isVerified: true,
        });
        setError("");
        // alert 대신 에러 상태 초기화로 성공 표시
      } else {
        setError(data.message || "인증번호가 일치하지 않습니다.");
      }
    } catch (err) {
      console.error("인증 확인 오류:", err);
      setError("인증 확인에 실패했습니다.");
    } finally {
      setLoading(false);
    }
  };

  const validateCurrentStep = () => {
    switch (currentStep) {
      case 1: // 기본 정보
        if (isOAuth2User) {
          // OAuth2 사용자는 1단계를 건너뛰므로 항상 true 반환
          return true;
        }
        if (!formData.email || !formData.password || !formData.name) {
          setError("모든 필수 정보를 입력해주세요.");
          return false;
        }
        // 이메일 형식 검증
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(formData.email)) {
          setError("올바른 이메일 형식을 입력해주세요. (예: example@hana.com)");
          return false;
        }
        if (formData.password !== formData.confirmPassword) {
          setError("비밀번호가 일치하지 않습니다.");
          return false;
        }
        if (formData.password.length < 8) {
          setError("비밀번호는 8자 이상이어야 합니다.");
          return false;
        }
        if (!formData.phoneNumber) {
          setError("전화번호를 입력해주세요.");
          return false;
        }
        if (!phoneVerification.isVerified) {
          setError("전화번호 인증을 완료해주세요.");
          return false;
        }
        return true;

      case 2: // 개인 정보
        if (
          !formData.birthDate ||
          !formData.gender ||
          !formData.residenceSido
        ) {
          setError("모든 필수 정보를 입력해주세요.");
          return false;
        }
        return true;

      case 3: // 가족 정보
        if (!formData.maritalStatus) {
          setError("결혼 상태를 선택해주세요.");
          return false;
        }
        return true;

      case 4: // 경제 정보
        if (!formData.incomeLevel) {
          setError("소득 구간을 선택해주세요.");
          return false;
        }
        return true;

      case 5: // 동의
        if (!formData.agreeToPersonalInfo) {
          setError("개인정보 수집에 동의해야 합니다.");
          return false;
        }
        return true;

      default:
        return true;
    }
  };

  const handleSubmit = async () => {
    if (!validateCurrentStep()) return;

    setLoading(true);
    setError("");

    try {
      const signupData = {
        ...formData,
        // OAuth2 사용자는 전화번호가 없을 수 있음
        phoneNumber: formData.phoneNumber || "",
        birthDate: new Date(formData.birthDate).toISOString(),
        expectedDueDate: formData.expectedDueDate
          ? new Date(formData.expectedDueDate).toISOString()
          : null,
        interestCategories: formData.interestCategories.join(","),
      };

      let result;

      if (isOAuth2User) {
        // OAuth2 사용자 처리 - 정보를 임시 저장하고 오픈뱅킹으로 이동
        // 실제 회원가입은 오픈뱅킹 완료 후 처리
        if (typeof window !== "undefined") {
          const tempToken = sessionStorage.getItem("tempOAuth2Token");

          // OAuth2 회원가입 데이터 저장
          sessionStorage.setItem(
            "pendingOAuth2SignupData",
            JSON.stringify({
              signupData,
              tempToken,
              isNewOAuth2User: initialData?.isNewOAuth2User,
            })
          );
        }

        // 오픈뱅킹 플로우로 이동
        if (onSuccess) {
          onSuccess(); // 오픈뱅킹 플로우 시작
        } else {
          onComplete && onComplete();
        }
      } else {
        // 일반 회원가입 - 정보를 임시 저장하고 오픈뱅킹으로 이동
        // 실제 회원가입은 오픈뱅킹 완료 후 처리
        if (typeof window !== "undefined") {
          sessionStorage.setItem(
            "pendingSignupData",
            JSON.stringify(signupData)
          );
        }

        // 오픈뱅킹 플로우로 이동
        if (onSuccess) {
          onSuccess(); // 오픈뱅킹 플로우 시작
        } else {
          onComplete();
        }
      }
    } catch (err) {
      setError(err.message || "오류가 발생했습니다.");
    } finally {
      setLoading(false);
    }
  };

  const renderStepContent = () => {
    switch (currentStep) {
      case 1:
        return (
          <div className="space-y-6">
            <div className="text-center mb-8">
              <div className="w-16 h-16 bg-gradient-to-br from-primary to-primary/70 rounded-full flex items-center justify-center mx-auto mb-4">
                <User className="h-8 w-8 text-white" />
              </div>
              <h3 className="text-2xl font-bold text-gray-900 mb-2">
                기본 정보
              </h3>
              <p className="text-gray-600">
                서비스 이용을 위한 기본 정보를 입력해주세요
              </p>
            </div>

            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  <User className="inline h-4 w-4 mr-1" />
                  이름 *
                </label>
                <input
                  type="text"
                  placeholder="홍길동"
                  value={formData.name}
                  onChange={(e) => handleInputChange("name", e.target.value)}
                  className="w-full h-12 rounded-lg border border-gray-300 px-4 focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/20 transition-all"
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  <Mail className="inline h-4 w-4 mr-1" />
                  이메일 *
                </label>
                <input
                  type="email"
                  placeholder="example@hana.com"
                  value={formData.email}
                  onChange={(e) => handleInputChange("email", e.target.value)}
                  className="w-full h-12 rounded-lg border border-gray-300 px-4 focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/20 transition-all"
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  <Phone className="inline h-4 w-4 mr-1" />
                  전화번호 *
                </label>
                <div className="flex gap-2">
                  <input
                    type="tel"
                    placeholder="010-1234-5678"
                    value={formData.phoneNumber}
                    onChange={(e) =>
                      handleInputChange(
                        "phoneNumber",
                        formatPhoneNumber(e.target.value)
                      )
                    }
                    disabled={phoneVerification.isVerified}
                    className="flex-1 h-12 rounded-lg border border-gray-300 px-4 focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/20 transition-all disabled:bg-gray-100"
                  />
                  <button
                    type="button"
                    onClick={handleSendVerificationCode}
                    disabled={
                      phoneVerification.isCodeSent ||
                      phoneVerification.isVerified
                    }
                    className="px-4 py-2 bg-emerald-600 text-white rounded-lg hover:bg-emerald-700 transition-colors text-sm font-medium disabled:bg-gray-400 disabled:cursor-not-allowed whitespace-nowrap"
                  >
                    {phoneVerification.isVerified
                      ? "인증완료"
                      : phoneVerification.isCodeSent
                      ? "재발송"
                      : "인증번호 발송"}
                  </button>
                </div>
              </div>

              {phoneVerification.isCodeSent &&
                !phoneVerification.isVerified && (
                  <div className="bg-white border border-gray-200 rounded-lg p-4">
                    <div className="flex items-center justify-between mb-3">
                      <span className="text-sm font-medium text-gray-700">
                        인증번호 입력
                      </span>
                      <span className="text-sm font-semibold text-emerald-600">
                        {formatTime(phoneVerification.timeLeft)}
                      </span>
                    </div>
                    <div className="flex gap-2">
                      <input
                        type="text"
                        placeholder="인증번호 6자리"
                        value={phoneVerification.verificationCode}
                        onChange={(e) =>
                          setPhoneVerification({
                            ...phoneVerification,
                            verificationCode: e.target.value
                              .replace(/[^\d]/g, "")
                              .slice(0, 6),
                          })
                        }
                        className="flex-1 h-12 rounded-lg border border-gray-300 px-4 focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/20 transition-all"
                        maxLength={6}
                      />
                      <button
                        type="button"
                        onClick={handleVerifyCode}
                        className="px-4 py-2 bg-emerald-600 text-white rounded-lg hover:bg-emerald-700 transition-colors text-sm font-medium whitespace-nowrap"
                      >
                        확인
                      </button>
                    </div>
                  </div>
                )}

              {phoneVerification.isVerified && (
                <div className="flex items-center gap-2 text-emerald-600 text-sm">
                  <CheckCircle className="h-4 w-4" />
                  <span>전화번호 인증이 완료되었습니다.</span>
                </div>
              )}

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  <Lock className="inline h-4 w-4 mr-1" />
                  비밀번호 *
                </label>
                <input
                  type="password"
                  placeholder="8자 이상 입력하세요"
                  value={formData.password}
                  onChange={(e) =>
                    handleInputChange("password", e.target.value)
                  }
                  className="w-full h-12 rounded-lg border border-gray-300 px-4 focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/20 transition-all"
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  <Lock className="inline h-4 w-4 mr-1" />
                  비밀번호 확인 *
                </label>
                <input
                  type="password"
                  placeholder="비밀번호를 다시 입력하세요"
                  value={formData.confirmPassword}
                  onChange={(e) =>
                    handleInputChange("confirmPassword", e.target.value)
                  }
                  className="w-full h-12 rounded-lg border border-gray-300 px-4 focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/20 transition-all"
                  required
                />
              </div>
            </div>
          </div>
        );

      case 2:
        return (
          <div className="space-y-6">
            <div className="text-center mb-8">
              <div className="w-16 h-16 bg-gradient-to-br from-blue-500 to-blue-600 rounded-full flex items-center justify-center mx-auto mb-4">
                <Calendar className="h-8 w-8 text-white" />
              </div>
              <h3 className="text-2xl font-bold text-gray-900 mb-2">
                개인 정보
              </h3>
              <p className="text-gray-600">
                맞춤 서비스 제공을 위한 개인 정보입니다
              </p>
            </div>

            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  <Calendar className="inline h-4 w-4 mr-1" />
                  생년월일 *
                </label>
                <DatePicker
                  value={formData.birthDate}
                  onChange={(value) => handleInputChange("birthDate", value)}
                  placeholder="생년월일을 선택하세요"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  성별 *
                </label>
                <EnhancedSelect
                  options={genderOptions}
                  value={formData.gender}
                  onChange={(value) => handleInputChange("gender", value)}
                  placeholder="성별을 선택하세요"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  <MapPin className="inline h-4 w-4 mr-1" />
                  거주지 (시도) *
                </label>
                <EnhancedSelect
                  options={sidoOptions}
                  value={formData.residenceSido}
                  onChange={(value) =>
                    handleInputChange("residenceSido", value)
                  }
                  placeholder="거주지를 선택하세요"
                  description="지자체 복지 혜택 추천을 위해 필요합니다"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  거주지 (시군구)
                </label>
                <EnhancedSelect
                  options={getAvailableSigunguOptions()}
                  value={formData.residenceSigungu}
                  onChange={(value) =>
                    handleInputChange("residenceSigungu", value)
                  }
                  placeholder={
                    formData.residenceSido
                      ? "시군구를 선택하세요"
                      : "먼저 시도를 선택하세요"
                  }
                  disabled={!formData.residenceSido}
                  description="더 정확한 지역 혜택 추천을 위해 선택하세요"
                />
              </div>
            </div>
          </div>
        );

      case 3:
        return (
          <div className="space-y-6">
            <div className="text-center mb-8">
              <div className="w-16 h-16 bg-gradient-to-br from-pink-500 to-pink-600 rounded-full flex items-center justify-center mx-auto mb-4">
                <Heart className="h-8 w-8 text-white" />
              </div>
              <h3 className="text-2xl font-bold text-gray-900 mb-2">
                가족 정보
              </h3>
              <p className="text-gray-600">
                가족 구성에 맞는 복지 혜택을 추천해드립니다
              </p>
            </div>

            <div className="space-y-6">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  결혼 상태 *
                </label>
                <EnhancedSelect
                  options={maritalStatusOptions}
                  value={formData.maritalStatus}
                  onChange={(value) =>
                    handleInputChange("maritalStatus", value)
                  }
                  placeholder="결혼 상태를 선택하세요"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  <Users className="inline h-4 w-4 mr-1" />
                  자녀 수
                </label>
                <div className="grid grid-cols-6 gap-2">
                  {[0, 1, 2, 3, 4, 5].map((num) => (
                    <button
                      key={num}
                      type="button"
                      onClick={() => handleInputChange("numberOfChildren", num)}
                      className={`h-12 rounded-lg border-2 transition-all duration-200 font-medium ${
                        formData.numberOfChildren === num
                          ? "border-primary bg-primary text-white"
                          : "border-gray-300 hover:border-primary/50"
                      }`}
                    >
                      {num === 5 ? "5+" : num}명
                    </button>
                  ))}
                </div>
              </div>

              <div className="space-y-4">
                <label className="block text-sm font-medium text-gray-700">
                  현재 상황 (해당사항 선택)
                </label>

                <div className="space-y-3">
                  <label className="flex items-center gap-3 p-4 border border-gray-200 rounded-xl hover:border-primary/50 hover:bg-primary/5 cursor-pointer transition-all">
                    <input
                      type="checkbox"
                      checked={formData.isPregnant}
                      onChange={(e) =>
                        handleInputChange("isPregnant", e.target.checked)
                      }
                      className="w-5 h-5 text-primary rounded focus:ring-primary"
                    />
                    <div>
                      <span className="font-medium">🤱 임신 중입니다</span>
                      <p className="text-sm text-gray-500">
                        임신·출산 관련 혜택을 우선 추천해드립니다
                      </p>
                    </div>
                  </label>

                  {formData.isPregnant && (
                    <div className="ml-8 space-y-2 animate-in slide-in-from-top-2">
                      <label className="block text-sm font-medium text-gray-700">
                        <Baby className="inline h-4 w-4 mr-1" />
                        예상 출산일
                      </label>
                      <DatePicker
                        value={formData.expectedDueDate}
                        onChange={(value) =>
                          handleInputChange("expectedDueDate", value)
                        }
                        placeholder="예상 출산일을 선택하세요"
                      />
                    </div>
                  )}

                  <label className="flex items-center gap-3 p-4 border border-gray-200 rounded-xl hover:border-primary/50 hover:bg-primary/5 cursor-pointer transition-all">
                    <input
                      type="checkbox"
                      checked={formData.isSingleParent}
                      onChange={(e) =>
                        handleInputChange("isSingleParent", e.target.checked)
                      }
                      className="w-5 h-5 text-primary rounded focus:ring-primary"
                    />
                    <div>
                      <span className="font-medium">👨‍👩‍👧 한부모 가정입니다</span>
                      <p className="text-sm text-gray-500">
                        한부모 가족 지원 혜택을 추천해드립니다
                      </p>
                    </div>
                  </label>

                  <label className="flex items-center gap-3 p-4 border border-gray-200 rounded-xl hover:border-primary/50 hover:bg-primary/5 cursor-pointer transition-all">
                    <input
                      type="checkbox"
                      checked={formData.isMulticultural}
                      onChange={(e) =>
                        handleInputChange("isMulticultural", e.target.checked)
                      }
                      className="w-5 h-5 text-primary rounded focus:ring-primary"
                    />
                    <div>
                      <span className="font-medium">🌍 다문화 가정입니다</span>
                      <p className="text-sm text-gray-500">
                        다문화 가족 지원 혜택을 추천해드립니다
                      </p>
                    </div>
                  </label>

                  <label className="flex items-center gap-3 p-4 border border-gray-200 rounded-xl hover:border-primary/50 hover:bg-primary/5 cursor-pointer transition-all">
                    <input
                      type="checkbox"
                      checked={formData.hasDisability}
                      onChange={(e) =>
                        handleInputChange("hasDisability", e.target.checked)
                      }
                      className="w-5 h-5 text-primary rounded focus:ring-primary"
                    />
                    <div>
                      <span className="font-medium">♿ 장애인입니다</span>
                      <p className="text-sm text-gray-500">
                        장애인 지원 혜택을 추천해드립니다
                      </p>
                    </div>
                  </label>
                </div>
              </div>
            </div>
          </div>
        );

      case 4:
        return (
          <div className="space-y-6">
            <div className="text-center mb-8">
              <div className="w-16 h-16 bg-gradient-to-br from-green-500 to-green-600 rounded-full flex items-center justify-center mx-auto mb-4">
                <DollarSign className="h-8 w-8 text-white" />
              </div>
              <h3 className="text-2xl font-bold text-gray-900 mb-2">
                경제 정보
              </h3>
              <p className="text-gray-600">
                복지 혜택 자격 확인을 위해 필요합니다
              </p>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                가구 소득 수준 *
              </label>
              <EnhancedSelect
                options={incomeLevelOptions}
                value={formData.incomeLevel}
                onChange={(value) => handleInputChange("incomeLevel", value)}
                placeholder="소득 구간을 선택하세요"
                description="정확한 복지 혜택 추천을 위해 솔직하게 선택해주세요"
              />
            </div>

            <div className="bg-blue-50 border border-blue-200 rounded-xl p-4">
              <h4 className="font-medium text-blue-800 mb-2 flex items-center gap-2">
                <AlertCircle className="h-4 w-4" />
                💡 소득 구간 가이드
              </h4>
              <ul className="text-sm text-blue-700 space-y-1">
                <li>• 기초생활수급자: 생계급여, 의료급여 등 기본 지원</li>
                <li>• 차상위계층: 다양한 복지서비스 대상</li>
                <li>• 중위소득 80% 이하: 일반 복지서비스 대상</li>
                <li>• 중위소득 100% 이하: 일부 복지서비스 대상</li>
              </ul>
            </div>
          </div>
        );

      case 5:
        return (
          <div className="space-y-6">
            <div className="text-center mb-8">
              <div className="w-16 h-16 bg-gradient-to-br from-purple-500 to-purple-600 rounded-full flex items-center justify-center mx-auto mb-4">
                <CheckCircle className="h-8 w-8 text-white" />
              </div>
              <h3 className="text-2xl font-bold text-gray-900 mb-2">
                관심 분야 및 동의
              </h3>
              <p className="text-gray-600">
                마지막 단계입니다! 관심 있는 복지 분야를 선택해주세요
              </p>
            </div>

            <CheckboxGroup
              title="관심 있는 복지 분야 (선택사항)"
              description="선택하신 분야의 복지 혜택을 우선적으로 추천해드립니다"
              options={interestOptions}
              selectedValues={formData.interestCategories}
              onChange={(values) =>
                handleInputChange("interestCategories", values)
              }
              columns={2}
            />

            <div className="space-y-4 pt-6 border-t border-gray-200">
              <label className="flex items-start gap-3 p-4 border border-gray-200 rounded-xl hover:border-primary/50 cursor-pointer transition-all">
                <input
                  type="checkbox"
                  checked={formData.agreeToPersonalInfo}
                  onChange={(e) =>
                    handleInputChange("agreeToPersonalInfo", e.target.checked)
                  }
                  className="w-5 h-5 text-primary rounded focus:ring-primary mt-0.5"
                  required
                />
                <div>
                  <span className="font-medium text-gray-900">
                    개인정보 수집 및 이용 동의 *
                  </span>
                  <p className="text-sm text-gray-600 mt-1">
                    맞춤형 복지 혜택 추천을 위해 개인정보를 수집합니다.
                  </p>
                </div>
              </label>

              <label className="flex items-start gap-3 p-4 border border-gray-200 rounded-xl hover:border-primary/50 cursor-pointer transition-all">
                <input
                  type="checkbox"
                  checked={formData.agreeToWelfareInfo}
                  onChange={(e) =>
                    handleInputChange("agreeToWelfareInfo", e.target.checked)
                  }
                  className="w-5 h-5 text-primary rounded focus:ring-primary mt-0.5"
                />
                <div>
                  <span className="font-medium text-gray-900">
                    복지 혜택 정보 수신 동의
                  </span>
                  <p className="text-sm text-gray-600 mt-1">
                    새로운 복지 혜택 정보를 알림으로 받아보실 수 있습니다.
                  </p>
                </div>
              </label>
            </div>
          </div>
        );

      default:
        return null;
    }
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-2xl max-w-2xl w-full max-h-[90vh] overflow-y-auto shadow-2xl">
        <div className="p-8">
          {/* 헤더 */}
          <div className="flex justify-between items-start mb-8">
            <div className="flex items-center gap-4">
              <div className="h-12 w-12 rounded-2xl bg-white flex items-center justify-center shadow-lg p-2">
                <img
                  src="/bank-logos/HanaLogo.png"
                  alt="하나은행 로고"
                  className="w-full h-full object-contain"
                />
              </div>
              <div>
                <h2 className="text-3xl font-bold text-gray-900">회원가입</h2>
                <p className="text-gray-600 mt-1">
                  {stepInfo[currentStep - 1]?.description}
                </p>
              </div>
            </div>
            <button
              onClick={onCancel}
              className="text-gray-400 hover:text-gray-600 text-2xl transition-colors"
            >
              ✕
            </button>
          </div>

          {/* 단계 표시기 */}
          <div className="mb-8">
            <div className="flex items-center justify-between mb-4">
              {stepInfo.map((step, index) => {
                const stepNumber = index + 1;
                const isActive = stepNumber === currentStep;
                const isCompleted = stepNumber < currentStep;
                const Icon = step.icon;

                return (
                  <div key={stepNumber} className="flex flex-col items-center">
                    <div
                      className={`
                      w-12 h-12 rounded-full flex items-center justify-center transition-all duration-300 mb-2
                      ${
                        isActive
                          ? "bg-primary text-white scale-110 shadow-lg"
                          : isCompleted
                          ? "bg-green-500 text-white"
                          : "bg-gray-200 text-gray-500"
                      }
                    `}
                    >
                      {isCompleted ? (
                        <CheckCircle className="h-6 w-6" />
                      ) : (
                        <Icon className="h-6 w-6" />
                      )}
                    </div>
                    <span
                      className={`text-xs font-medium ${
                        isActive
                          ? "text-primary"
                          : isCompleted
                          ? "text-green-600"
                          : "text-gray-500"
                      }`}
                    >
                      {step.title}
                    </span>
                  </div>
                );
              })}
            </div>

            {/* 진행 바 */}
            <div className="w-full bg-gray-200 rounded-full h-3 shadow-inner">
              <div
                className="bg-gradient-to-r from-primary to-primary/80 h-3 rounded-full transition-all duration-500 shadow-sm"
                style={{ width: `${(currentStep / totalSteps) * 100}%` }}
              />
            </div>
          </div>

          {/* 에러 메시지 */}
          {error && (
            <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-xl mb-6 flex items-center gap-2 animate-in slide-in-from-top-2">
              <AlertCircle className="h-5 w-5" />
              {error}
            </div>
          )}

          {/* 단계별 콘텐츠 */}
          <div className="mb-8">{renderStepContent()}</div>

          {/* 버튼들 */}
          <div className="flex gap-4 pt-6 border-t border-gray-200">
            {currentStep > 1 && (
              <button
                onClick={handlePrev}
                className="flex items-center gap-2 px-6 py-3 border border-gray-300 rounded-xl hover:bg-gray-50 transition-all duration-200"
              >
                <ChevronLeft className="h-4 w-4" />
                이전
              </button>
            )}

            <div className="flex-1"></div>

            {currentStep < totalSteps ? (
              <button
                onClick={handleNext}
                className="flex items-center gap-2 px-8 py-3 bg-primary text-white rounded-xl hover:bg-primary/90 transition-all duration-200 shadow-lg hover:shadow-xl"
              >
                다음
                <ChevronRight className="h-4 w-4" />
              </button>
            ) : (
              <button
                onClick={handleSubmit}
                disabled={loading}
                className={`flex items-center gap-2 px-8 py-3 rounded-xl transition-all duration-200 shadow-lg ${
                  loading
                    ? "bg-gray-400 cursor-not-allowed"
                    : "bg-gradient-to-r from-primary to-primary/80 text-white hover:shadow-xl hover:scale-105"
                }`}
              >
                {loading ? (
                  <>
                    <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
                    처리 중...
                  </>
                ) : (
                  <>
                    <ArrowRight className="h-4 w-4" />
                    다음 단계 (계좌 연결)
                  </>
                )}
              </button>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
