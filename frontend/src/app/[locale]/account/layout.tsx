import type { ReactNode } from "react";
import AccountSidebar from "@/components/account/AccountSidebar";
import AuthRouteGuard from "@/components/auth/AuthRouteGuard";

export default function AccountLayout({ children }: { children: ReactNode }) {
  return (
    <AuthRouteGuard requiredRole="CAR_OWNER">
      <div className="bg-black min-h-screen text-white">
        <div className="max-w-7xl mx-auto">
          <AccountSidebar />

          <main className="lg:pl-72">
            <div className="p-3">
              <div className="mx-auto max-w-4xl">{children}</div>
            </div>
          </main>
        </div>
      </div>
    </AuthRouteGuard>
  );
}
