"use client";

import { AuthProvider } from "../../contexts/AuthContext";
import { useChunkErrorHandler } from "../../hooks/use-chunk-error-handler";
import Footer from "../layout/footer";

export function ClientProviders({ children }) {
  // ChunkLoadError 자동 처리
  useChunkErrorHandler();

  return (
    <AuthProvider>
      <div className="flex flex-col min-h-screen">
        <main className="flex-grow">{children}</main>
        <Footer />
      </div>
    </AuthProvider>
  );
}
