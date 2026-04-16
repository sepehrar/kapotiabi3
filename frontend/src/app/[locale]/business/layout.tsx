import type { ReactNode } from "react";
import BusinessSidebar from "@/components/business/BusinessSidebar";

export default function BusinessLayout({ children }: { children: ReactNode }) {
  return (
    <div className="min-h-screen bg-black text-white">
      <BusinessSidebar />

      <main className="lg:pl-72">
        <div className="p-4 sm:p-6 lg:p-8">
          <div className="mx-auto max-w-6xl">{children}</div>
        </div>
      </main>
    </div>
  );
}
