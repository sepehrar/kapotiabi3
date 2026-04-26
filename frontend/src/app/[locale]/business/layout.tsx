import type { ReactNode } from "react";
import BusinessSidebar from "@/components/business/BusinessSidebar";
import AuthRouteGuard from "@/components/auth/AuthRouteGuard";

export default function BusinessLayout({ children }: { children: ReactNode }) {
  return (
    <AuthRouteGuard requiredRole="SHOP_OWNER">
      <div className="min-h-screen  text-white bg-black">
        <div className="max-w-7xl mx-auto">
          <BusinessSidebar />

          <main className="lg:pl-72">
            <div className="mx-auto max-w-6xl p-3">{children}</div>
          </main>
        </div>
      </div>
    </AuthRouteGuard>
  );
}
