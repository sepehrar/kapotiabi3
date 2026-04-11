import type { ReactNode } from "react";
import AccountSidebar from "@/components/account/AccountSidebar";

export default function AccountLayout({ children }: { children: ReactNode }) {
  return (
    <div className=" bg-black min-h-screen text-white flex maw-w-7xl mx-auto">
      <AccountSidebar />

      <main className="lg:pl-72">
        <div className="p-4 ">
          <div className="mx-auto  ">{children}</div>
        </div>
      </main>
    </div>
  );
}
