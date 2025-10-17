/**
 * API 재시도 유틸리티
 */

export async function retryWithBackoff(
  apiCall,
  maxRetries = 3,
  baseDelay = 1000,
  backoffMultiplier = 2
) {
  let lastError;

  for (let attempt = 0; attempt <= maxRetries; attempt++) {
    try {
      return await apiCall();
    } catch (error) {
      lastError = error;

      // 마지막 시도라면 에러 던지기
      if (attempt === maxRetries) {
        throw error;
      }

      // 재시도하지 않을 에러들
      if (
        error.status === 400 || // Bad Request
        error.status === 401 || // Unauthorized
        error.status === 403 || // Forbidden
        error.status === 404 // Not Found
      ) {
        throw error;
      }

      // 백오프 지연
      const delay = baseDelay * Math.pow(backoffMultiplier, attempt);
      await new Promise((resolve) => setTimeout(resolve, delay));

      console.warn(`API 재시도 ${attempt + 1}/${maxRetries}:`, error.message);
    }
  }

  throw lastError;
}

export function createRetryableApiCall(apiFunction, retryOptions = {}) {
  return (...args) =>
    retryWithBackoff(
      () => apiFunction(...args),
      retryOptions.maxRetries,
      retryOptions.baseDelay,
      retryOptions.backoffMultiplier
    );
}


























