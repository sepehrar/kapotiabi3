import type { ReactNode } from "react";
import Header from "@/components/landing/Header";
import Footer from "@/components/ui/Footer";

export default function MarketingLayout({ children }: { children: ReactNode }) {
  return (
    <>
      <Header />
      {children}
      <Footer/>
    </>
  );
}
