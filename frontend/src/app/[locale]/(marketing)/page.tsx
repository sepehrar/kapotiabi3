import Hero from "@/components/landing/Hero";
import CTA from "@/components/landing/CTA";
import Testimonials from "@/components/landing/Testimonials";
import HowItWorks from "@/components/landing/HowItworks";
import WhatCompare from "@/components/landing/WhatCompare";
import PopularCategories from "@/components/landing/PopularCategories";
import WhyUseKapotiAbi from "@/components/landing/WhyUseKapotiAbi";
import ForBusinesses from "@/components/landing/ForBusinesses";
import FAQ from "@/components/landing/FAQ";
// import { useTranslations } from "next-intl";

export default function Home() {
  // const t = useTranslations("home");

  return (
    <>
      {" "}
      <Hero />
      <HowItWorks />
      <PopularCategories />
      <WhatCompare />
      <WhyUseKapotiAbi />
      <ForBusinesses />
      <FAQ />
      <Testimonials />
      <CTA />
    </>
  );
}
