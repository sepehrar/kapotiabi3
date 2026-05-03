import Image from "next/image";
import { useTranslations } from "next-intl";

const items = [
  "shopPage",
  "requests",
  "bookings",
  "reviews",
  "analytics",
] as const;

export default function WhatYouGetSection() {
  const t = useTranslations("businessLanding.whatYouGet");

  return (
    <section className="bg-black">
      <div className="mx-auto max-w-7xl px-8 pt-24">
        <div className="flex items-start gap-12">
          <div>
            <Image
              src="/img/bg1.jpg"
              alt=""
              width={350}
              height={500}
              className="aspect-2/3 max-w-75 object-contain"
            />
          </div>

          <div>
            <h2 className="max-w-xl text-balance text-4xl font-light tracking-tight text-white sm:text-6xl">
              {t("title")}
            </h2>

            <p className="mt-6 max-w-xl text-balance text-lg font-medium leading-8 text-white/50 sm:text-xl">
              {t("description")}
            </p>

            <div className="mt-10 space-y-5">
              {items.map((key) => (
                <div
                  key={key}
                  className="group border-b-2 border-(--accent)/40 py-4 text-left transition-all duration-300 hover:-translate-y-0.5 hover:border-(--accent)"
                >
                  <h3 className="text-lg font-semibold text-white transition group-hover:text-(--accent)">
                    {t(`items.${key}.title`)}
                  </h3>

                  <p className="mt-2 text-sm leading-6 text-white/60 transition group-hover:text-white">
                    {t(`items.${key}.desc`)}
                  </p>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}
