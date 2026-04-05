import { useTranslations } from "next-intl";

export default function HowItWorks() {
  const t = useTranslations("howItWorks");

  const steps = [
    {
      number: "01",
      title: t("step1Title"),
      description: t("step1Description"),
    },
    {
      number: "02",
      title: t("step2Title"),
      description: t("step2Description"),
    },
    {
      number: "03",
      title: t("step3Title"),
      description: t("step3Description"),
    },
  ];

  return (
    <section aria-labelledby="how-it-works-heading" className="bg-black">
      <div className="mx-auto w-full max-w-screen-2xl px-6 pb-16 sm:px-12 lg:px-16 ">
        <div className=" mx-auto  max-w-7xl ">
          <h2
            id="how-it-works-heading"
            className="text-2xl max-w-xl tracking-tight text-white sm:text-4xl text-balance font-light"
          >
            {t("title")}
          </h2>
        </div>

        <div className="mx-auto mt-12 grid max-w-7xl gap-6 md:grid-cols-3">
          {steps.map((step) => (
            <div
              key={step.number}
              className="rounded-3xl border border-black/10 bg-[#f8f8f8] p-6 text-left shadow-sm"
            >
              <div className="text-base font-semibold text-[#f44b05]">
                {step.number}
              </div>

              <h3 className="mt-4 text-xl font-semibold text-black">
                {step.title}
              </h3>

              <p className="mt-3 text-base leading-7 text-black/70">
                {step.description}
              </p>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}
