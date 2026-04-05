import Hero from "@/components/ui/landing/Hero";
import CTA from "@/components/ui/CTA";
import Testimonials from "@/components/ui/Testimonials";
import HowItWorks from "@/components/ui/landing/HowItworks";
import WhatCompare from "@/components/ui/landing/WhatСompare";
import PopularCategories from "@/components/ui/landing/PopularCategories";
import WhyUseCarFix from "@/components/ui/landing/WhyUseCarFix";
import ForBusinesses from "@/components/ui/landing/ForBusinesses";
import FAQ from "@/components/ui/landing/FAQ";
// import { useTranslations } from "next-intl";

export default function Home() {
  // const t = useTranslations("home");

  return (
    <>
      {" "}
      <Hero />
      <HowItWorks />
      <WhatCompare />
      <PopularCategories />
      <WhyUseCarFix />
      <ForBusinesses />
      <FAQ />
      <div className="bg-white">
        {/* <div className="flex flex-col flex-1 items-center justify-center bg-zinc-500 font-sans dark:bg-black">
          <h1>{t("title")}</h1>
          <p>{t("description")}</p>
        </div>{" "} */}
        <Testimonials />
        <CTA />
      </div>
    </>
  );
}
