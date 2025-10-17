import "./globals.css";
import { ClientProviders } from "../components/providers/client-providers";

export const metadata = {
  title: "HanaFuture | 하나Future - 가족의 미래를 함께 설계하세요",
  description:
    "하나금융그룹의 육아 금융 서비스. 자녀 교육비부터 복지 혜택까지, 체계적인 가족 금융 계획을 세워보세요.",
  keywords:
    "하나Future, 하나금융그룹, 육아금융, 교육비, 복지혜택, 적금, 보험, 가족금융",
  authors: [{ name: "하나금융그룹" }],
  creator: "하나금융그룹",
  publisher: "하나금융그룹",
  formatDetection: {
    email: false,
    address: false,
    telephone: false,
  },
  icons: {
    icon: "/bank-logos/HanaLogo.png",
    shortcut: "/bank-logos/HanaLogo.png",
    apple: "/bank-logos/HanaLogo.png",
  },
  openGraph: {
    title: "HanaFuture | 하나Future",
    description: "가족의 미래를 함께 설계하는 육아 금융 서비스",
    url: "http://localhost:3000",
    siteName: "HanaFuture",
    images: [
      {
        url: "/bank-logos/HanaLogo.png",
        width: 1200,
        height: 630,
        alt: "HanaFuture 로고",
      },
    ],
    locale: "ko_KR",
    type: "website",
  },
  twitter: {
    card: "summary_large_image",
    title: "HanaFuture | 하나Future",
    description: "가족의 미래를 함께 설계하는 육아 금융 서비스",
    images: ["/bank-logos/HanaLogo.png"],
  },
  metadataBase: new URL("http://localhost:3000"),
};

export default function RootLayout({ children }) {
  return (
    <html lang="ko" suppressHydrationWarning>
      <head>
        <link rel="preconnect" href="https://fonts.googleapis.com" />
        <link
          rel="preconnect"
          href="https://fonts.gstatic.com"
          crossOrigin="anonymous"
        />
        <link
          href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@300;400;500;600;700;800;900&family=Inter:wght@300;400;500;600;700;800;900&display=swap"
          rel="stylesheet"
        />
        {/* 카카오 SDK */}
        <script src="https://t1.kakaocdn.net/kakao_js_sdk/2.7.2/kakao.min.js"></script>
        <script
          dangerouslySetInnerHTML={{
            __html: `
              window.addEventListener('load', function() {
                if (window.Kakao && !window.Kakao.isInitialized()) {
                  window.Kakao.init('23fee0ae0e034a8553b1e0138973002f');
                  console.log('카카오 SDK 초기화 완료:', window.Kakao.isInitialized());
                }
              });
            `,
          }}
        />
      </head>
      <body
        suppressHydrationWarning
        style={{
          fontFamily:
            "'Noto Sans KR', 'Inter', -apple-system, BlinkMacSystemFont, system-ui, sans-serif",
          WebkitFontSmoothing: "antialiased",
          MozOsxFontSmoothing: "grayscale",
        }}
      >
        <ClientProviders>{children}</ClientProviders>
      </body>
    </html>
  );
}
