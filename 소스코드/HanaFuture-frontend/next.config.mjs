/** @type {import('next').NextConfig} */
const nextConfig = {
  // ===========================
  // 배포 설정 (S3 + CloudFront)
  // ===========================

  // 정적 사이트 생성 (Static Site Generation)
  // 개발 환경에서는 동적 라우팅을 위해 비활성화
  output: process.env.NODE_ENV === "production" ? "export" : undefined,

  // 빌드 출력 디렉토리
  distDir: "out",

  // URL 끝에 슬래시 추가 (S3 호환성)
  trailingSlash: true,

  // 빌드 ID 생성 (캐시 무효화를 위해 타임스탬프 사용)
  generateBuildId: async () => {
    return `build-${Date.now()}`;
  },

  // ===========================
  // 빌드 최적화
  // ===========================

  // 빌드 중 ESLint 에러 무시
  eslint: {
    ignoreDuringBuilds: true,
  },

  // TypeScript 에러 무시 (필요시)
  typescript: {
    ignoreBuildErrors: false,
  },

  // 이미지 최적화 비활성화 (정적 export 필수)
  images: {
    unoptimized: true,
  },

  // 압축 활성화
  compress: true,

  // React Strict Mode
  reactStrictMode: true,

  // ===========================
  // 실험적 기능
  // ===========================

  experimental: {
    // SWC 트랜스폼 강제 사용
    forceSwcTransforms: true,
  },

  // ===========================
  // Webpack 설정
  // ===========================

  webpack: (config, { isServer, dev }) => {
    // 클라이언트 사이드 번들 최적화
    if (!isServer) {
      config.optimization = {
        ...config.optimization,
        splitChunks: {
          chunks: "all",
          cacheGroups: {
            // Vendor 라이브러리 분리
            vendor: {
              test: /[\\/]node_modules[\\/]/,
              name: "vendors",
              priority: 10,
              reuseExistingChunk: true,
            },
            // 공통 컴포넌트 분리
            common: {
              minChunks: 2,
              priority: 5,
              reuseExistingChunk: true,
            },
          },
        },
      };
    }

    // 프로덕션 빌드에서 콘솔 로그 제거 (선택사항)
    if (!dev && !isServer) {
      config.optimization.minimizer = config.optimization.minimizer || [];
      // TerserPlugin 설정 추가 가능
    }

    return config;
  },

  // ===========================
  // 환경 변수
  // ===========================

  env: {
    CUSTOM_KEY: process.env.CUSTOM_KEY,
    // 필요한 환경 변수 추가
  },

  // ===========================
  // 헤더 설정 (output: "export" 모드에서는 적용 안됨)
  // CloudFront에서 설정 필요
  // ===========================

  // async headers() {
  //   return [
  //     {
  //       source: "/:path*",
  //       headers: [
  //         {
  //           key: "X-Frame-Options",
  //           value: "DENY",
  //         },
  //         {
  //           key: "X-Content-Type-Options",
  //           value: "nosniff",
  //         },
  //       ],
  //     },
  //   ];
  // },
};

export default nextConfig;
