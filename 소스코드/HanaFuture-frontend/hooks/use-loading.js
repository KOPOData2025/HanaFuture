"use client";

import { useState, useCallback } from "react";
import { toast } from "sonner";

export function useLoading() {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);

  const withLoading = useCallback(async (asyncFunction, options = {}) => {
    const {
      successMessage,
      errorMessage = "작업 중 오류가 발생했습니다.",
      showSuccessToast = false,
      onSuccess,
      onError,
    } = options;

    try {
      setIsLoading(true);
      setError(null);

      const result = await asyncFunction();

      if (successMessage && showSuccessToast) {
        toast.success(successMessage);
      }

      if (onSuccess) {
        onSuccess(result);
      }

      return result;
    } catch (err) {
      const errorMsg = err.message || errorMessage;
      setError(errorMsg);
      toast.error(errorMsg);

      if (onError) {
        onError(err);
      }

      throw err;
    } finally {
      setIsLoading(false);
    }
  }, []);

  const reset = useCallback(() => {
    setIsLoading(false);
    setError(null);
  }, []);

  return {
    isLoading,
    error,
    withLoading,
    reset,
  };
}


























