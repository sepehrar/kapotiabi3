import type { ReactNode } from "react";
import AccountSidebar from "@/components/account/AccountSidebar";

export default function AccountLayout({ children }: { children: ReactNode }) {
  return (
    <div className="bg-black min-h-screen text-white">
      <AccountSidebar />

      <main className="lg:pl-72">
        <div className="p-4 sm:p-6 lg:p-8">
          <div className="mx-auto max-w-4xl">{children}</div>
        </div>
      </main>
    </div>
  );
}
