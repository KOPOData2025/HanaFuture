"use client";

import React, { useState } from "react";
import { ChevronDown, Check } from "lucide-react";

export function EnhancedSelect({
  options = [],
  value,
  onChange,
  placeholder = "선택하세요",
  icon = null,
  description = null,
}) {
  const [isOpen, setIsOpen] = useState(false);

  const selectedOption = options.find((option) => option.value === value);

  const handleSelect = (optionValue) => {
    onChange(optionValue);
    setIsOpen(false);
  };

  return (
    <div className="relative">
      {/* 선택 버튼 */}
      <button
        type="button"
        onClick={() => setIsOpen(!isOpen)}
        className="w-full h-12 rounded-lg border border-gray-300 px-4 flex items-center justify-between cursor-pointer hover:border-primary transition-all duration-200 bg-white focus:outline-none focus:ring-2 focus:ring-primary/20"
      >
        <div className="flex items-center gap-3">
          {icon && <span className="text-gray-400">{icon}</span>}
          <span className={selectedOption ? "text-gray-900" : "text-gray-500"}>
            {selectedOption ? selectedOption.label : placeholder}
          </span>
        </div>
        <ChevronDown
          className={`h-5 w-5 text-gray-400 transition-transform duration-200 ${
            isOpen ? "rotate-180" : ""
          }`}
        />
      </button>

      {/* 드롭다운 메뉴 */}
      {isOpen && (
        <div className="absolute top-full left-0 right-0 mt-2 bg-white border border-gray-200 rounded-xl shadow-xl z-50 max-h-60 overflow-y-auto animate-in slide-in-from-top-2">
          {description && (
            <div className="px-4 py-3 border-b border-gray-100">
              <p className="text-sm text-gray-600">{description}</p>
            </div>
          )}

          {options.map((option) => (
            <button
              key={option.value}
              onClick={() => handleSelect(option.value)}
              className="w-full px-4 py-3 text-left hover:bg-primary/5 transition-colors duration-150 flex items-center justify-between group"
            >
              <div>
                <span className="text-gray-900 group-hover:text-primary transition-colors">
                  {option.label}
                </span>
                {option.description && (
                  <p className="text-sm text-gray-500 mt-1">
                    {option.description}
                  </p>
                )}
              </div>
              {value === option.value && (
                <Check className="h-4 w-4 text-primary" />
              )}
            </button>
          ))}
        </div>
      )}

      {/* 오버레이 */}
      {isOpen && (
        <div className="fixed inset-0 z-40" onClick={() => setIsOpen(false)} />
      )}
    </div>
  );
}
