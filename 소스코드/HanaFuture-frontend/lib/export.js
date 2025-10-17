// 데이터 내보내기 유틸리티

import { StorageManager } from "./storage";

class ExportManager {
  // CSV 형태로 데이터 내보내기
  static exportToCSV(data, filename = "hanafuture_data.csv") {
    if (!data || data.length === 0) {
      throw new Error("내보낼 데이터가 없습니다.");
    }

    // CSV 헤더 생성
    const headers = Object.keys(data[0]);
    const csvContent = [
      headers.join(","),
      ...data.map((row) =>
        headers
          .map((header) => {
            const value = row[header];
            // 쉼표나 따옴표가 포함된 경우 처리
            if (
              typeof value === "string" &&
              (value.includes(",") || value.includes('"'))
            ) {
              return `"${value.replace(/"/g, '""')}"`;
            }
            return value;
          })
          .join(",")
      ),
    ].join("\n");

    // BOM 추가 (한글 깨짐 방지)
    const BOM = "\uFEFF";
    const blob = new Blob([BOM + csvContent], {
      type: "text/csv;charset=utf-8;",
    });

    this.downloadFile(blob, filename);
  }

  // JSON 형태로 데이터 내보내기
  static exportToJSON(data, filename = "hanafuture_data.json") {
    const jsonContent = JSON.stringify(data, null, 2);
    const blob = new Blob([jsonContent], { type: "application/json" });

    this.downloadFile(blob, filename);
  }

  // 파일 다운로드 실행
  static downloadFile(blob, filename) {
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = url;
    link.download = filename;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
  }

  // 혜택 데이터 내보내기
  static exportBenefitsData(benefits) {
    const exportData = benefits.map((benefit) => ({
      혜택명: benefit.serviceName || "",
      서비스유형: benefit.serviceType === "CENTRAL" ? "중앙정부" : "지자체",
      지역: benefit.sidoName || "",
      지원금액: benefit.supportAmount
        ? `${benefit.supportAmount.toLocaleString()}원`
        : "",
      생애주기: benefit.lifeCycle || "",
      서비스내용: benefit.serviceContent || "",
      신청방법: benefit.applicationMethod || "",
      문의처: benefit.inquiryUrl || "",
      내보낸날짜: new Date().toLocaleDateString("ko-KR"),
    }));

    this.exportToCSV(
      exportData,
      `혜택목록_${new Date().toISOString().split("T")[0]}.csv`
    );
  }

  // 가계부 데이터 내보내기
  static exportBudgetData() {
    const budgetData = StorageManager.getBudgetData();
    const savingsGoals = StorageManager.getSavingsGoals();

    const exportData = {
      예산정보: {
        월예산: budgetData.monthlyBudget,
        카테고리별예산: budgetData.categories,
        마지막업데이트: budgetData.lastUpdated,
      },
      저축목표: savingsGoals.map((goal) => ({
        목표명: goal.name,
        목표금액: goal.target,
        현재금액: goal.current,
        달성률: `${goal.percentage}%`,
        마감일: goal.deadline,
      })),
      지출내역: budgetData.expenses.map((expense) => ({
        날짜: expense.date,
        카테고리: expense.category,
        금액: expense.amount,
        내용: expense.description,
      })),
      내보낸날짜: new Date().toISOString(),
    };

    this.exportToJSON(
      exportData,
      `가계부_${new Date().toISOString().split("T")[0]}.json`
    );
  }

  // 사용자 데이터 전체 내보내기
  static exportAllUserData() {
    const userData = StorageManager.getUserData();
    const budgetData = StorageManager.getBudgetData();
    const savingsGoals = StorageManager.getSavingsGoals();
    const notificationSettings = StorageManager.getNotificationSettings();
    const dashboardPrefs = StorageManager.getDashboardPreferences();

    const exportData = {
      사용자정보: userData,
      예산데이터: budgetData,
      저축목표: savingsGoals,
      알림설정: notificationSettings,
      대시보드설정: dashboardPrefs,
      내보낸날짜: new Date().toISOString(),
      버전: "1.0",
    };

    this.exportToJSON(
      exportData,
      `HanaFuture_백업_${new Date().toISOString().split("T")[0]}.json`
    );
  }

  // PDF 리포트 생성 (간단한 HTML to PDF)
  static generatePDFReport(data, title = "HanaFuture 리포트") {
    const printWindow = window.open("", "_blank");

    const htmlContent = `
      <!DOCTYPE html>
      <html>
      <head>
        <meta charset="utf-8">
        <title>${title}</title>
        <style>
          body {
            font-family: 'Malgun Gothic', sans-serif;
            margin: 20px;
            line-height: 1.6;
          }
          .header {
            text-align: center;
            border-bottom: 2px solid #10b981;
            padding-bottom: 20px;
            margin-bottom: 30px;
          }
          .header h1 {
            color: #10b981;
            margin: 0;
          }
          .section {
            margin-bottom: 30px;
          }
          .section h2 {
            color: #374151;
            border-left: 4px solid #10b981;
            padding-left: 10px;
          }
          table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 10px;
          }
          th, td {
            border: 1px solid #ddd;
            padding: 8px;
            text-align: left;
          }
          th {
            background-color: #f8f9fa;
            font-weight: bold;
          }
          .footer {
            margin-top: 50px;
            text-align: center;
            color: #6b7280;
            font-size: 12px;
          }
          @media print {
            body { margin: 0; }
            .no-print { display: none; }
          }
        </style>
      </head>
      <body>
        <div class="header">
          <h1>🏦 ${title}</h1>
          <p>생성일: ${new Date().toLocaleDateString("ko-KR")}</p>
        </div>
        
        <div class="section">
          <h2>📊 요약 정보</h2>
          ${this.generateSummaryHTML(data)}
        </div>
        
        <div class="section">
          <h2>💰 상세 내역</h2>
          ${this.generateDetailHTML(data)}
        </div>
        
        <div class="footer">
          <p>이 리포트는 HanaFuture에서 생성되었습니다.</p>
          <p>문의: support@hanafuture.com</p>
        </div>
        
        <div class="no-print" style="margin-top: 30px; text-align: center;">
          <button onclick="window.print()" style="padding: 10px 20px; background: #10b981; color: white; border: none; border-radius: 5px; cursor: pointer;">
            인쇄하기
          </button>
          <button onclick="window.close()" style="padding: 10px 20px; background: #6b7280; color: white; border: none; border-radius: 5px; cursor: pointer; margin-left: 10px;">
            닫기
          </button>
        </div>
      </body>
      </html>
    `;

    printWindow.document.write(htmlContent);
    printWindow.document.close();
  }

  // 요약 HTML 생성
  static generateSummaryHTML(data) {
    if (Array.isArray(data)) {
      return `<p>총 ${data.length}개의 항목이 있습니다.</p>`;
    }

    return `
      <table>
        <tr><th>항목</th><th>값</th></tr>
        ${Object.entries(data)
          .map(
            ([key, value]) =>
              `<tr><td>${key}</td><td>${
                typeof value === "object" ? JSON.stringify(value) : value
              }</td></tr>`
          )
          .join("")}
      </table>
    `;
  }

  // 상세 HTML 생성
  static generateDetailHTML(data) {
    if (!Array.isArray(data) || data.length === 0) {
      return "<p>상세 데이터가 없습니다.</p>";
    }

    const headers = Object.keys(data[0]);

    return `
      <table>
        <thead>
          <tr>
            ${headers.map((header) => `<th>${header}</th>`).join("")}
          </tr>
        </thead>
        <tbody>
          ${data
            .map(
              (row) =>
                `<tr>
              ${headers
                .map((header) => `<td>${row[header] || ""}</td>`)
                .join("")}
            </tr>`
            )
            .join("")}
        </tbody>
      </table>
    `;
  }

  // 데이터 가져오기 (백업 복원)
  static importUserData(file) {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();

      reader.onload = (e) => {
        try {
          const data = JSON.parse(e.target.result);

          // 데이터 유효성 검사
          if (!data.버전 || !data.내보낸날짜) {
            throw new Error("올바른 HanaFuture 백업 파일이 아닙니다.");
          }

          // 데이터 복원
          if (data.사용자정보) StorageManager.saveUserData(data.사용자정보);
          if (data.예산데이터) StorageManager.saveBudgetData(data.예산데이터);
          if (data.저축목표) StorageManager.saveSavingsGoals(data.저축목표);
          if (data.알림설정)
            StorageManager.saveNotificationSettings(data.알림설정);
          if (data.대시보드설정)
            StorageManager.saveDashboardPreferences(data.대시보드설정);

          resolve(data);
        } catch (error) {
          reject(
            new Error("파일을 읽는 중 오류가 발생했습니다: " + error.message)
          );
        }
      };

      reader.onerror = () => reject(new Error("파일을 읽을 수 없습니다."));
      reader.readAsText(file);
    });
  }
}

export { ExportManager };
