"use client";

import { useState, useEffect } from "react";
import {
  User,
  Phone,
  MapPin,
  Calendar,
  Users,
  Heart,
  Baby,
  Briefcase,
  DollarSign,
  Save,
  RefreshCw,
  AlertCircle,
  CheckCircle,
  ArrowRight,
  Home,
  Gift,
} from "lucide-react";
import { apiClient } from "../../../lib/api-client";
import { useAuth } from "../../../contexts/AuthContext";
import { toast } from "sonner";

export function UserInfoUpdate() {
  const { user } = useAuth();
  const [formData, setFormData] = useState({
    name: "",
    phoneNumber: "",
    residenceSido: "서울특별시",
    residenceSigungu: "",
    birthDate: "",
    gender: "MALE",
    maritalStatus: "SINGLE",
    income: "",
    employmentStatus: "EMPLOYED",
    hasChildren: false,
    numberOfChildren: 0,
    childrenAges: [],
    interests: [],
  });

  const [loading, setLoading] = useState(false);
  const [initialLoading, setInitialLoading] = useState(true);
  const [showSuccessModal, setShowSuccessModal] = useState(false);

  // 사용자 정보 불러오기
  useEffect(() => {
    const loadUserInfo = async () => {
      if (user) {
        try {
          const response = await apiClient.users.getInfo();
          if (response.success && response.data) {
            const userData = response.data;
            setFormData({
              name: userData.name || "",
              phoneNumber: userData.phoneNumber || "",
              residenceSido: userData.residenceSido || "서울특별시",
              residenceSigungu: userData.residenceSigungu || "",
              birthDate: userData.birthDate
                ? userData.birthDate.split("T")[0]
                : "",
              gender: userData.gender || "MALE",
              maritalStatus: userData.maritalStatus || "SINGLE",
              income: userData.income || "",
              employmentStatus: userData.employmentStatus || "EMPLOYED",
              hasChildren: userData.hasChildren || false,
              numberOfChildren: userData.numberOfChildren || 0,
              childrenAges: userData.childrenAges || [],
              interests: userData.interests || [],
            });
          }
        } catch (error) {
          console.error("사용자 정보 로드 실패:", error);
          toast.error("사용자 정보를 불러오는데 실패했습니다.");
        } finally {
          setInitialLoading(false);
        }
      }
    };

    loadUserInfo();
  }, [user]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      // 필수 필드 검증
      if (!formData.name.trim()) {
        toast.error("이름을 입력해주세요.");
        return;
      }

      // birthDate를 LocalDateTime 형식으로 변환
      const submitData = {
        ...formData,
        birthDate: formData.birthDate ? `${formData.birthDate}T00:00:00` : null,
        income: formData.income ? parseInt(formData.income) : null,
      };

      const response = await apiClient.users.updateInfo(submitData);
      if (response.success) {
        setShowSuccessModal(true);
      } else {
        toast.error("업데이트 실패: " + response.message);
      }
    } catch (error) {
      console.error("사용자 정보 업데이트 실패:", error);
      toast.error("업데이트 중 오류가 발생했습니다.");
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: type === "checkbox" ? checked : value,
    }));
  };

  if (initialLoading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-emerald-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">사용자 정보를 불러오는 중...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-4xl mx-auto px-4">
        {/* 헤더 */}
        <div className="mb-8">
          <div className="flex items-center gap-3 mb-4">
            <div className="w-12 h-12 bg-gradient-to-br from-emerald-500 to-teal-600 rounded-2xl flex items-center justify-center">
              <User className="h-6 w-6 text-white" />
            </div>
            <div>
              <h1 className="text-3xl font-bold text-gray-900">내 정보 수정</h1>
              <p className="text-gray-600">
                개인정보를 업데이트하여 더 정확한 맞춤 서비스를 받으세요
              </p>
            </div>
          </div>
        </div>

        <form onSubmit={handleSubmit} className="space-y-8">
          {/* 기본 정보 섹션 */}
          <div className="bg-white rounded-2xl p-6 border border-gray-100">
            <div className="flex items-center gap-3 mb-6">
              <div className="w-10 h-10 bg-blue-100 rounded-xl flex items-center justify-center">
                <User className="h-5 w-5 text-blue-600" />
              </div>
              <h2 className="text-xl font-bold text-gray-900">기본 정보</h2>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  이름 *
                </label>
                <input
                  type="text"
                  name="name"
                  value={formData.name}
                  onChange={handleChange}
                  required
                  className="w-full px-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-emerald-500 transition-colors"
                  placeholder="이름을 입력하세요"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  휴대폰 번호
                </label>
                <div className="relative">
                  <Phone className="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
                  <input
                    type="tel"
                    name="phoneNumber"
                    value={formData.phoneNumber}
                    onChange={handleChange}
                    className="w-full pl-10 pr-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-emerald-500 transition-colors"
                    placeholder="010-1234-5678"
                  />
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  생년월일
                </label>
                <div className="relative">
                  <Calendar className="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
                  <input
                    type="date"
                    name="birthDate"
                    value={formData.birthDate}
                    onChange={handleChange}
                    className="w-full pl-10 pr-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-emerald-500 transition-colors"
                  />
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  성별
                </label>
                <select
                  name="gender"
                  value={formData.gender}
                  onChange={handleChange}
                  className="w-full px-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-emerald-500 transition-colors"
                >
                  <option value="MALE">남성</option>
                  <option value="FEMALE">여성</option>
                </select>
              </div>
            </div>
          </div>

          {/* 거주지 정보 섹션 */}
          <div className="bg-white rounded-2xl p-6 border border-gray-100">
            <div className="flex items-center gap-3 mb-6">
              <div className="w-10 h-10 bg-green-100 rounded-xl flex items-center justify-center">
                <MapPin className="h-5 w-5 text-green-600" />
              </div>
              <h2 className="text-xl font-bold text-gray-900">거주지 정보</h2>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  거주지 시도
                </label>
                <select
                  name="residenceSido"
                  value={formData.residenceSido}
                  onChange={handleChange}
                  className="w-full px-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-emerald-500 transition-colors"
                >
                  <option value="서울특별시">서울특별시</option>
                  <option value="부산광역시">부산광역시</option>
                  <option value="대구광역시">대구광역시</option>
                  <option value="인천광역시">인천광역시</option>
                  <option value="광주광역시">광주광역시</option>
                  <option value="대전광역시">대전광역시</option>
                  <option value="울산광역시">울산광역시</option>
                  <option value="세종특별자치시">세종특별자치시</option>
                  <option value="경기도">경기도</option>
                  <option value="강원도">강원도</option>
                  <option value="충청북도">충청북도</option>
                  <option value="충청남도">충청남도</option>
                  <option value="전라북도">전라북도</option>
                  <option value="전라남도">전라남도</option>
                  <option value="경상북도">경상북도</option>
                  <option value="경상남도">경상남도</option>
                  <option value="제주특별자치도">제주특별자치도</option>
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  거주지 시군구
                </label>
                <input
                  type="text"
                  name="residenceSigungu"
                  value={formData.residenceSigungu}
                  onChange={handleChange}
                  className="w-full px-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-emerald-500 transition-colors"
                  placeholder="예: 구로구"
                />
              </div>
            </div>
          </div>

          {/* 경제 정보 섹션 */}
          <div className="bg-white rounded-2xl p-6 border border-gray-100">
            <div className="flex items-center gap-3 mb-6">
              <div className="w-10 h-10 bg-yellow-100 rounded-xl flex items-center justify-center">
                <DollarSign className="h-5 w-5 text-yellow-600" />
              </div>
              <h2 className="text-xl font-bold text-gray-900">경제 정보</h2>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  월 소득 (만원)
                </label>
                <div className="relative">
                  <DollarSign className="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
                  <input
                    type="number"
                    name="income"
                    value={formData.income}
                    onChange={handleChange}
                    className="w-full pl-10 pr-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-emerald-500 transition-colors"
                    placeholder="예: 300"
                  />
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  직업 상태
                </label>
                <div className="relative">
                  <Briefcase className="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
                  <select
                    name="employmentStatus"
                    value={formData.employmentStatus}
                    onChange={handleChange}
                    className="w-full pl-10 pr-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-emerald-500 transition-colors"
                  >
                    <option value="EMPLOYED">재직중</option>
                    <option value="UNEMPLOYED">구직중</option>
                    <option value="SELF_EMPLOYED">자영업</option>
                    <option value="STUDENT">학생</option>
                    <option value="HOUSEWIFE">주부</option>
                    <option value="RETIRED">은퇴</option>
                    <option value="OTHER">기타</option>
                  </select>
                </div>
              </div>
            </div>
          </div>

          {/* 가족 정보 섹션 */}
          <div className="bg-white rounded-2xl p-6 border border-gray-100">
            <div className="flex items-center gap-3 mb-6">
              <div className="w-10 h-10 bg-purple-100 rounded-xl flex items-center justify-center">
                <Users className="h-5 w-5 text-purple-600" />
              </div>
              <h2 className="text-xl font-bold text-gray-900">가족 정보</h2>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  결혼 상태
                </label>
                <select
                  name="maritalStatus"
                  value={formData.maritalStatus}
                  onChange={handleChange}
                  className="w-full px-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-emerald-500 transition-colors"
                >
                  <option value="SINGLE">미혼</option>
                  <option value="MARRIED">기혼</option>
                  <option value="DIVORCED">이혼</option>
                  <option value="WIDOWED">사별</option>
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  자녀 수
                </label>
                <div className="relative">
                  <Baby className="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
                  <input
                    type="number"
                    name="numberOfChildren"
                    value={formData.numberOfChildren}
                    onChange={(e) => {
                      const value = parseInt(e.target.value) || 0;
                      setFormData((prev) => ({
                        ...prev,
                        numberOfChildren: value,
                        hasChildren: value > 0,
                      }));
                    }}
                    min="0"
                    max="10"
                    className="w-full pl-10 pr-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-emerald-500 transition-colors"
                    placeholder="0"
                  />
                </div>
              </div>
            </div>
          </div>

          {/* 관심사 섹션 */}
          <div className="bg-white rounded-2xl p-6 border border-gray-100">
            <div className="flex items-center gap-3 mb-6">
              <div className="w-10 h-10 bg-pink-100 rounded-xl flex items-center justify-center">
                <Heart className="h-5 w-5 text-pink-600" />
              </div>
              <div>
                <h2 className="text-xl font-bold text-gray-900">관심 분야</h2>
                <p className="text-sm text-gray-500">
                  맞춤 혜택 추천을 위해 선택해주세요
                </p>
              </div>
            </div>

            <div className="grid grid-cols-2 md:grid-cols-4 gap-3">
              {[
                "육아",
                "교육",
                "의료",
                "주거",
                "취업",
                "창업",
                "노후",
                "문화",
                "여행",
                "스포츠",
                "기술",
                "환경",
              ].map((interest) => (
                <label
                  key={interest}
                  className="flex items-center gap-2 p-3 border border-gray-200 rounded-xl hover:bg-gray-50 cursor-pointer transition-colors"
                >
                  <input
                    type="checkbox"
                    checked={formData.interests.includes(interest)}
                    onChange={(e) => {
                      if (e.target.checked) {
                        setFormData((prev) => ({
                          ...prev,
                          interests: [...prev.interests, interest],
                        }));
                      } else {
                        setFormData((prev) => ({
                          ...prev,
                          interests: prev.interests.filter(
                            (i) => i !== interest
                          ),
                        }));
                      }
                    }}
                    className="rounded border-gray-300 text-emerald-600 focus:ring-emerald-500"
                  />
                  <span className="text-sm font-medium text-gray-700">
                    {interest}
                  </span>
                </label>
              ))}
            </div>
          </div>

          {/* 제출 버튼 */}
          <div className="bg-white rounded-2xl p-6 border border-gray-100">
            <div className="flex flex-col sm:flex-row gap-4">
              <button
                type="submit"
                disabled={loading}
                className="flex-1 flex items-center justify-center gap-3 px-6 py-4 bg-gradient-to-r from-emerald-600 to-teal-600 text-white rounded-xl hover:from-emerald-700 hover:to-teal-700 focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-300 transform hover:scale-105 shadow-lg font-semibold"
              >
                {loading ? (
                  <>
                    <RefreshCw className="h-5 w-5 animate-spin" />
                    업데이트 중...
                  </>
                ) : (
                  <>
                    <Save className="h-5 w-5" />
                    정보 업데이트
                  </>
                )}
              </button>

              <button
                type="button"
                onClick={() => {
                  if (
                    confirm(
                      "변경사항이 저장되지 않을 수 있습니다. 대시보드로 이동하시겠습니까?"
                    )
                  ) {
                    window.location.hash = "#dashboard";
                    window.location.reload();
                  }
                }}
                className="px-6 py-4 bg-gray-100 text-gray-700 rounded-xl hover:bg-gray-200 transition-colors font-semibold"
              >
                취소
              </button>
            </div>

            <div className="mt-4 p-4 bg-blue-50 rounded-xl border border-blue-200">
              <div className="flex items-start gap-3">
                <AlertCircle className="h-5 w-5 text-blue-600 mt-0.5" />
                <div>
                  <h4 className="font-medium text-blue-900 mb-1">
                    정보 활용 안내
                  </h4>
                  <p className="text-sm text-blue-700 leading-relaxed">
                    입력하신 정보는 맞춤형 복지혜택 및 금융상품 추천에만
                    사용되며, 개인정보보호법에 따라 안전하게 보호됩니다.
                  </p>
                </div>
              </div>
            </div>
          </div>
        </form>

        {/* 성공 모달 */}
        {showSuccessModal && (
          <SuccessModal
            onGoToDashboard={() => {
              window.location.hash = "#dashboard";
              window.location.reload();
            }}
            onViewBenefits={() => {
              window.location.hash = "#benefits";
              window.location.reload();
            }}
            onStayHere={() => {
              setShowSuccessModal(false);
              toast.success("정보가 성공적으로 업데이트되었습니다!");
            }}
          />
        )}
      </div>
    </div>
  );
}

// 성공 모달 컴포넌트
function SuccessModal({ onGoToDashboard, onViewBenefits, onStayHere }) {
  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-3xl p-8 max-w-md w-full shadow-2xl">
        {/* 성공 아이콘 */}
        <div className="text-center mb-6">
          <div className="w-20 h-20 bg-gradient-to-br from-emerald-500 to-teal-600 rounded-full flex items-center justify-center mx-auto mb-4">
            <CheckCircle className="h-10 w-10 text-white" />
          </div>
          <h2 className="text-2xl font-bold text-gray-900 mb-2">
            정보 업데이트 완료!
          </h2>
          <p className="text-gray-600">
            개인정보가 성공적으로 업데이트되었습니다.
            <br />
            이제 더 정확한 맞춤 서비스를 받으실 수 있어요.
          </p>
        </div>

        {/* 액션 버튼들 */}
        <div className="space-y-3">
          <button
            onClick={onGoToDashboard}
            className="w-full flex items-center justify-center gap-3 px-6 py-4 bg-gradient-to-r from-emerald-600 to-teal-600 text-white rounded-2xl hover:from-emerald-700 hover:to-teal-700 transition-all duration-300 transform hover:scale-105 shadow-lg font-semibold"
          >
            <Home className="h-5 w-5" />
            대시보드로 이동
            <ArrowRight className="h-4 w-4" />
          </button>

          <button
            onClick={onViewBenefits}
            className="w-full flex items-center justify-center gap-3 px-6 py-4 bg-gradient-to-r from-purple-600 to-pink-600 text-white rounded-2xl hover:from-purple-700 hover:to-pink-700 transition-all duration-300 transform hover:scale-105 shadow-lg font-semibold"
          >
            <Gift className="h-5 w-5" />
            맞춤 혜택 확인하기
            <ArrowRight className="h-4 w-4" />
          </button>

          <button
            onClick={onStayHere}
            className="w-full px-6 py-3 bg-gray-100 text-gray-700 rounded-2xl hover:bg-gray-200 transition-colors font-medium"
          >
            계속 수정하기
          </button>
        </div>

        {/* 추가 안내 */}
        <div className="mt-6 p-4 bg-emerald-50 rounded-xl border border-emerald-200">
          <div className="flex items-start gap-3">
            <CheckCircle className="h-5 w-5 text-emerald-600 mt-0.5" />
            <div>
              <h4 className="font-medium text-emerald-900 mb-1">
                맞춤 서비스 준비 완료
              </h4>
              <p className="text-sm text-emerald-700 leading-relaxed">
                업데이트된 정보를 바탕으로 새로운 복지혜택과 금융상품을
                추천해드릴 준비가 되었습니다.
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
