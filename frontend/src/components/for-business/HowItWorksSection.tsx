import { useTranslations } from "next-intl";

const steps = [
  {
    num: "01",
    titleKey: "steps.register.title",
    descKey: "steps.register.desc",
  },
  {
    num: "02",
    titleKey: "steps.fillServices.title",
    descKey: "steps.fillServices.desc",
  },
  {
    num: "03",
    titleKey: "steps.getPage.title",
    descKey: "steps.getPage.desc",
  },
  {
    num: "04",
    titleKey: "steps.receiveRequests.title",
    descKey: "steps.receiveRequests.desc",
  },
  {
    num: "05",
    titleKey: "steps.proposeAndBook.title",
    descKey: "steps.proposeAndBook.desc",
  },
];

export default function HowItWorksSection() {
  const t = useTranslations("businessLanding.howItWorks");

  return (
    <section
      id="how-it-works"
      aria-labelledby="how-it-works-heading"
      className="bg-black"
    >
      <div className="mx-auto w-full max-w-screen-2xl  py-24 ">
        <div className="mx-auto max-w-7xl px-8">
          <h2
            id="how-it-works-heading"
            className="text-3xl font-light tracking-tight text-white text-balance sm:text-4xl"
          >
            {t("title")}
          </h2>

          <p className="mt-4 max-w-xl text-lg text-white/50">
            {t("description")}
          </p>
        </div>

        <div className="mx-auto mt-12 grid max-w-7xl gap-x-6 gap-y-12 sm:grid-cols-2 lg:grid-cols-3 px-8">
          {steps.map((step) => (
            <div
              key={step.num}
              className="rounded-r-3xl border-l-2 border-(--accent)/40 p-4 text-left transition-all duration-300 hover:-translate-y-0.5 hover:bg-linear-60 from-(--pills-background) to-(--pills-background-hover)"
            >
              <div className="-mt-3 p-1 text-lg font-semibold text-(--accent)">
                {step.num}
              </div>

              <div className="p-6">
                <h3 className="mt-4 text-xl font-semibold text-white">
                  {t(step.titleKey)}
                </h3>

                <p className="mt-3 text-base leading-7 text-white/70">
                  {t(step.descKey)}
                </p>
              </div>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}
