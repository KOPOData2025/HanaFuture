"use client";

import React from "react";

export function CheckboxGroup({
  options = [],
  selectedValues = [],
  onChange,
  title,
  description,
  columns = 2,
}) {
  const handleToggle = (value) => {
    const newValues = selectedValues.includes(value)
      ? selectedValues.filter((v) => v !== value)
      : [...selectedValues, value];
    onChange(newValues);
  };

  return (
    <div className="space-y-4">
      {title && (
        <div>
          <h4 className="text-lg font-semibold text-gray-900">{title}</h4>
          {description && (
            <p className="text-sm text-gray-600 mt-1">{description}</p>
          )}
        </div>
      )}

      <div className={`grid grid-cols-${columns} gap-3`}>
        {options.map((option) => (
          <label
            key={option.value}
            className="flex items-center gap-3 p-4 border border-gray-200 rounded-xl hover:border-primary/50 hover:bg-primary/5 cursor-pointer transition-all duration-200 group"
          >
            <div className="relative">
              <input
                type="checkbox"
                checked={selectedValues.includes(option.value)}
                onChange={() => handleToggle(option.value)}
                className="sr-only"
              />
              <div
                className={`
                w-5 h-5 rounded border-2 transition-all duration-200
                ${
                  selectedValues.includes(option.value)
                    ? "bg-primary border-primary"
                    : "border-gray-300 group-hover:border-primary/50"
                }
              `}
              >
                {selectedValues.includes(option.value) && (
                  <svg
                    className="w-3 h-3 text-white absolute top-0.5 left-0.5"
                    fill="currentColor"
                    viewBox="0 0 20 20"
                  >
                    <path
                      fillRule="evenodd"
                      d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z"
                      clipRule="evenodd"
                    />
                  </svg>
                )}
              </div>
            </div>
            <div className="flex-1">
              <span className="text-sm font-medium text-gray-900 group-hover:text-primary transition-colors">
                {option.label}
              </span>
              {option.description && (
                <p className="text-xs text-gray-500 mt-1">
                  {option.description}
                </p>
              )}
            </div>
          </label>
        ))}
      </div>
    </div>
  );
}
