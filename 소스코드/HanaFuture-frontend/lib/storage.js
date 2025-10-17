// 로컬 스토리지 관리 유틸리티

const STORAGE_KEYS = {
  USER_DATA: "hanafuture_user_data",
  SAVINGS_GOALS: "hanafuture_savings_goals",
  BUDGET_DATA: "hanafuture_budget_data",
  NOTIFICATION_SETTINGS: "hanafuture_notifications",
  DASHBOARD_PREFERENCES: "hanafuture_dashboard_prefs",
  RECENT_SEARCHES: "hanafuture_recent_searches",
};

class StorageManager {
  // 데이터 저장
  static setItem(key, value) {
    try {
      const serializedValue = JSON.stringify(value);
      localStorage.setItem(key, serializedValue);
      return true;
    } catch (error) {
      console.error("Storage save error:", error);
      return false;
    }
  }

  // 데이터 조회
  static getItem(key, defaultValue = null) {
    try {
      const item = localStorage.getItem(key);
      return item ? JSON.parse(item) : defaultValue;
    } catch (error) {
      console.error("Storage read error:", error);
      return defaultValue;
    }
  }

  // 데이터 삭제
  static removeItem(key) {
    try {
      localStorage.removeItem(key);
      return true;
    } catch (error) {
      console.error("Storage remove error:", error);
      return false;
    }
  }

  // 모든 데이터 삭제
  static clear() {
    try {
      Object.values(STORAGE_KEYS).forEach((key) => {
        localStorage.removeItem(key);
      });
      return true;
    } catch (error) {
      console.error("Storage clear error:", error);
      return false;
    }
  }

  // 사용자 데이터 관리
  static saveUserData(userData) {
    return this.setItem(STORAGE_KEYS.USER_DATA, {
      ...userData,
      lastUpdated: new Date().toISOString(),
    });
  }

  static getUserData() {
    return this.getItem(STORAGE_KEYS.USER_DATA, {
      name: "",
      email: "",
      preferences: {},
      lastUpdated: null,
    });
  }

  // 저축 목표 관리
  static saveSavingsGoals(goals) {
    return this.setItem(STORAGE_KEYS.SAVINGS_GOALS, {
      goals,
      lastUpdated: new Date().toISOString(),
    });
  }

  static getSavingsGoals() {
    const data = this.getItem(STORAGE_KEYS.SAVINGS_GOALS, {
      goals: [],
      lastUpdated: null,
    });
    return data.goals || [];
  }

  // 예산 데이터 관리
  static saveBudgetData(budgetData) {
    return this.setItem(STORAGE_KEYS.BUDGET_DATA, {
      ...budgetData,
      lastUpdated: new Date().toISOString(),
    });
  }

  static getBudgetData() {
    return this.getItem(STORAGE_KEYS.BUDGET_DATA, {
      monthlyBudget: 0,
      categories: {},
      expenses: [],
      lastUpdated: null,
    });
  }

  // 알림 설정 관리
  static saveNotificationSettings(settings) {
    return this.setItem(STORAGE_KEYS.NOTIFICATION_SETTINGS, settings);
  }

  static getNotificationSettings() {
    return this.getItem(STORAGE_KEYS.NOTIFICATION_SETTINGS, {
      benefitAlerts: true,
      budgetAlerts: true,
      goalReminders: true,
      emailNotifications: true,
      pushNotifications: false,
    });
  }

  // 대시보드 설정 관리
  static saveDashboardPreferences(preferences) {
    return this.setItem(STORAGE_KEYS.DASHBOARD_PREFERENCES, preferences);
  }

  static getDashboardPreferences() {
    return this.getItem(STORAGE_KEYS.DASHBOARD_PREFERENCES, {
      cardOrder: ["assets", "savings", "expenses", "benefits"],
      hiddenCards: [],
      compactMode: false,
    });
  }

  // 최근 검색어 관리
  static addRecentSearch(searchTerm) {
    const recentSearches = this.getItem(STORAGE_KEYS.RECENT_SEARCHES, []);
    const updatedSearches = [
      searchTerm,
      ...recentSearches.filter((term) => term !== searchTerm),
    ].slice(0, 10); // 최대 10개까지 저장

    return this.setItem(STORAGE_KEYS.RECENT_SEARCHES, updatedSearches);
  }

  static getRecentSearches() {
    return this.getItem(STORAGE_KEYS.RECENT_SEARCHES, []);
  }

  static clearRecentSearches() {
    return this.removeItem(STORAGE_KEYS.RECENT_SEARCHES);
  }
}

export { StorageManager, STORAGE_KEYS };
