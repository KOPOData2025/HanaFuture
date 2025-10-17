"use client";

import React, { useState } from "react";
import { ChevronLeft, ChevronRight, Calendar } from "lucide-react";

export function DatePicker({
  value,
  onChange,
  placeholder = "날짜를 선택하세요",
}) {
  const [isOpen, setIsOpen] = useState(false);
  const [currentDate, setCurrentDate] = useState(new Date());

  const months = [
    "1월",
    "2월",
    "3월",
    "4월",
    "5월",
    "6월",
    "7월",
    "8월",
    "9월",
    "10월",
    "11월",
    "12월",
  ];

  const getDaysInMonth = (date) => {
    const year = date.getFullYear();
    const month = date.getMonth();
    const firstDay = new Date(year, month, 1);
    const lastDay = new Date(year, month + 1, 0);
    const daysInMonth = lastDay.getDate();
    const startingDayOfWeek = firstDay.getDay();

    const days = [];

    // 이전 달의 빈 칸들
    for (let i = 0; i < startingDayOfWeek; i++) {
      days.push(null);
    }

    // 현재 달의 날짜들
    for (let day = 1; day <= daysInMonth; day++) {
      days.push(new Date(year, month, day));
    }

    return days;
  };

  const formatDate = (date) => {
    if (!date) return "";
    return date.toISOString().split("T")[0];
  };

  const parseDate = (dateString) => {
    if (!dateString) return null;
    return new Date(dateString);
  };

  const handleDateSelect = (date) => {
    onChange(formatDate(date));
    setIsOpen(false);
  };

  const navigateMonth = (direction) => {
    setCurrentDate((prev) => {
      const newDate = new Date(prev);
      newDate.setMonth(prev.getMonth() + direction);
      return newDate;
    });
  };

  const navigateYear = (direction) => {
    setCurrentDate((prev) => {
      const newDate = new Date(prev);
      newDate.setFullYear(prev.getFullYear() + direction);
      return newDate;
    });
  };

  const selectedDate = parseDate(value);
  const days = getDaysInMonth(currentDate);

  return (
    <div className="relative">
      {/* 입력 필드 */}
      <div
        onClick={() => setIsOpen(!isOpen)}
        className="w-full h-12 rounded-lg border border-gray-300 px-4 flex items-center justify-between cursor-pointer hover:border-primary transition-colors bg-white"
      >
        <span className={selectedDate ? "text-gray-900" : "text-gray-500"}>
          {selectedDate
            ? `${selectedDate.getFullYear()}년 ${
                selectedDate.getMonth() + 1
              }월 ${selectedDate.getDate()}일`
            : placeholder}
        </span>
        <Calendar className="h-5 w-5 text-gray-400" />
      </div>

      {/* 캘린더 드롭다운 */}
      {isOpen && (
        <div className="absolute top-full left-0 right-0 mt-2 bg-white border border-gray-200 rounded-xl shadow-xl z-50 p-4 animate-in slide-in-from-top-2">
          {/* 헤더 */}
          <div className="flex items-center justify-between mb-4">
            <div className="flex items-center gap-2">
              <button
                onClick={() => navigateYear(-1)}
                className="p-1 hover:bg-gray-100 rounded"
              >
                <ChevronLeft className="h-4 w-4" />
              </button>
              <span className="font-semibold text-lg min-w-[80px] text-center">
                {currentDate.getFullYear()}년
              </span>
              <button
                onClick={() => navigateYear(1)}
                className="p-1 hover:bg-gray-100 rounded"
              >
                <ChevronRight className="h-4 w-4" />
              </button>
            </div>

            <div className="flex items-center gap-2">
              <button
                onClick={() => navigateMonth(-1)}
                className="p-1 hover:bg-gray-100 rounded"
              >
                <ChevronLeft className="h-4 w-4" />
              </button>
              <span className="font-semibold text-lg min-w-[60px] text-center">
                {months[currentDate.getMonth()]}
              </span>
              <button
                onClick={() => navigateMonth(1)}
                className="p-1 hover:bg-gray-100 rounded"
              >
                <ChevronRight className="h-4 w-4" />
              </button>
            </div>
          </div>

          {/* 요일 헤더 */}
          <div className="grid grid-cols-7 gap-1 mb-2">
            {["일", "월", "화", "수", "목", "금", "토"].map((day) => (
              <div
                key={day}
                className="text-center text-sm font-medium text-gray-500 py-2"
              >
                {day}
              </div>
            ))}
          </div>

          {/* 날짜 그리드 */}
          <div className="grid grid-cols-7 gap-1">
            {days.map((day, index) => (
              <button
                key={index}
                onClick={() => day && handleDateSelect(day)}
                disabled={!day}
                className={`
                  h-10 text-sm rounded-lg transition-all duration-200
                  ${!day ? "invisible" : ""}
                  ${
                    day &&
                    selectedDate &&
                    day.getDate() === selectedDate.getDate() &&
                    day.getMonth() === selectedDate.getMonth() &&
                    day.getFullYear() === selectedDate.getFullYear()
                      ? "bg-primary text-white font-bold shadow-lg scale-105"
                      : "hover:bg-primary/10 hover:text-primary"
                  }
                `}
              >
                {day?.getDate()}
              </button>
            ))}
          </div>

          {/* 빠른 선택 버튼들 */}
          <div className="flex gap-2 mt-4 pt-4 border-t">
            <button
              onClick={() => handleDateSelect(new Date())}
              className="text-sm px-3 py-1 bg-gray-100 hover:bg-gray-200 rounded-lg transition-colors"
            >
              오늘
            </button>
            <button
              onClick={() => {
                const date = new Date();
                date.setFullYear(date.getFullYear() - 30);
                handleDateSelect(date);
              }}
              className="text-sm px-3 py-1 bg-gray-100 hover:bg-gray-200 rounded-lg transition-colors"
            >
              30년 전
            </button>
            <button
              onClick={() => setIsOpen(false)}
              className="text-sm px-3 py-1 bg-red-100 hover:bg-red-200 text-red-600 rounded-lg transition-colors ml-auto"
            >
              닫기
            </button>
          </div>
        </div>
      )}

      {/* 오버레이 */}
      {isOpen && (
        <div className="fixed inset-0 z-40" onClick={() => setIsOpen(false)} />
      )}
    </div>
  );
}
