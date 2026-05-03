import Image from "next/image";
import Link from "next/link";
import { useTranslations } from "next-intl";

export default function HeroBusiness() {
  const t = useTranslations("businessLanding.hero");

  return (
    <section aria-labelledby="business-hero-heading" className="bg-black">
      <div className="relative mx-auto h-162.5 w-full max-w-screen-2xl overflow-hidden">
        <Image
          src="/img/hero2.jpg"
          alt=""
          fill
          priority
          className="object-contain object-top"
        />

        {/* <div className="absolute inset-0 bg-black/50" /> */}

        <div className="relative  ">
          <div className="mx-auto max-w-7xl  px-8 pt-32">
            <p className="mb-5 text-sm font-medium uppercase tracking-widest text-(--accent) mt-32">
              {t("eyebrow")}
            </p>

            <h1
              id="business-hero-heading"
              className="text-4xl font-light tracking-tight text-white text-balance sm:text-6xl max-w-2xl"
            >
              {t("title")}
            </h1>

            <p className="mt-16 max-w-xl text-lg leading-8 text-white/70 text-balance">
              {t("description")}
            </p>

            <div className="mt-8 flex w-full max-w-xl flex-col gap-4 sm:flex-row sm:justify-center">
              <Link
                href={`/register?role=SHOP_OWNER`}
                className="inline-flex justify-center rounded-full border border-transparent bg-(--accent) px-8 py-3 text-base font-medium text-white ring-1 ring-inset ring-(--accent) transition hover:bg-(--accent-hover)"
              >
                {t("cta")}
              </Link>

              <Link
                href="#how-it-works"
                className="inline-flex justify-center rounded-full bg-white/5 px-8 py-3 text-base font-medium text-white ring-1 ring-inset ring-white/10 transition hover:bg-white/10"
              >
                {t("ctaSecondary")}
              </Link>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}
