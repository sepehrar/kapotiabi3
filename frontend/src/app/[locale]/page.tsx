import Hero from "@/components/ui/Hero";
import CTA from "@/components/ui/CTA";
import Testimonials from "@/components/ui/Testimonials";
// import { useTranslations } from "next-intl";

export default function Home() {
  // const t = useTranslations("home");

  return (
    <>
      {" "}
      <Hero />
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
