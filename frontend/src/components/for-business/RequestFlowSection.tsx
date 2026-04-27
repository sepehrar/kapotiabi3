import Image from "next/image";
import { useTranslations } from "next-intl";

const requestSteps = ["receive", "propose", "booked"] as const;

export default function RequestFlowSection() {
  const t = useTranslations("businessLanding.requestFlow");

  return (
    <section className=" bg-black px-6 pt-24">
      <div className="mx-auto max-w-7xl">
        <div className="relative overflow-hidden rounded-3xl">
          <Image
            alt=""
            src="/img/bg.jpg"
            width={1200}
            height={350}
            className="h-87.5 w-full object-cover object-center"
          />

          <div className="absolute inset-0 bg-black/60" />

          <div className="absolute inset-0 flex flex-col items-center  justify-end px-6 text-center">
            <h2 className="max-w-xl text-balance text-3xl font-light tracking-tight text-white sm:text-4xl mb-14">
              {t("title")}
            </h2>
          </div>
        </div>

        <div className="mt-12 grid gap-6 sm:grid-cols-3">
          {requestSteps.map((key, i) => (
            <div key={key} className="relative p-8">
              <span className="text-4xl font-light text-(--accent)">
                {String(i + 1).padStart(2, "0")}
              </span>

              <h3 className="mt-4 text-lg font-semibold text-white">
                {t(`steps.${key}.title`)}
              </h3>

              <p className="mt-2 text-sm leading-relaxed text-white/50">
                {t(`steps.${key}.desc`)}
              </p>

              {i < 2 && (
                <div className="absolute -right-3 top-1/2 hidden -translate-y-1/2 text-xl text-white/20 sm:block">
                  →
                </div>
              )}
            </div>
          ))}
        </div>
        <p className="text-center text-lg text-(--accent) text-balance mt-12  mx-auto ">
          {t("description")}
        </p>
      </div>
    </section>
  );
}
