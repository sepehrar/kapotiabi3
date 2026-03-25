import Image from "next/image";
import { useTranslations } from "next-intl";

export default function Hero() {
  const t = useTranslations("home");

  return (
    <section aria-labelledby="social-impact-heading" className="bg-black">
      <div className="relative w-full max-w-screen-2xl mx-auto h-162.5 overflow-hidden">
        <Image
          src="/img/hero.png"
          alt=""
          fill
          priority
          className="object-contain object-top"
        />

        <div className="absolute inset-0 " />

        <div className="relative z-10 px-6 pt-70 sm:px-12 lg:px-16">
          <div className="mx-auto flex max-w-3xl flex-col items-center text-center">
            <h2
              id="social-impact-heading"
              className="text-3xl font-bold tracking-tight text-white sm:text-4xl"
            >
              {t("title")}
            </h2>

            <p className="mt-3 text-xl text-white">{t("description")}</p>

            <div className="flex gap-12 items-center">
              <a
                href="#"
                className="mt-8 block w-full rounded-full border border-transparent bg-[#f44b05] px-8 py-3 text-base font-medium text-white hover:bg-[#902d03] sm:w-auto"
              >
                {t("button")}
              </a>

              <a href="#" className=" mt-8 text-sm  text-white">
                {t("secondaryButton")}
              </a>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}
