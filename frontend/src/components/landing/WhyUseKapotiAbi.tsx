import { useTranslations } from "next-intl";

export default function WhyUseKapotiAbi() {
  const t = useTranslations("whyUseKapotiAbi");

  const items = [
    { title: t("item1Title"), description: t("item1Description") },
    { title: t("item2Title"), description: t("item2Description") },
    { title: t("item3Title"), description: t("item3Description") },
    { title: t("item4Title"), description: t("item4Description") },
  ];

  return (
    <section
      aria-labelledby="why-use-kapotiabi-heading"
      className="bg-[#f8f8f8]"
    >
      <div className="mx-auto w-full max-w-5xl px-6 py-16 sm:px-12 lg:px-16 lg:py-24">
        {/* Header */}
        <div className="mx-auto max-w-2xl text-center">
          <h2
            id="why-use-kapotiabi-heading"
            className="text-3xl font-bold tracking-tight text-black sm:text-4xl text-balance"
          >
            {t("title")}
          </h2>
          <p className="mt-4 text-base leading-7 text-black/70 sm:text-lg text-balance">
            {t("description")}
          </p>
        </div>

        {/* Grid */}
        <div className="mt-12 grid gap-4 sm:grid-cols-2">
          {items.map((item, i) => (
            <div
              key={item.title}
              className="group  bg-white p-8  border-white transition-all border-b-3 duration-300 hover:-translate-y-0.5 hover:border-b-(--accent) hover:shadow-md"
            >
              <span className="mb-4 block text-[10px] font-bold uppercase tracking-[0.15em] text-(--accent)">
                {String(i + 1).padStart(2, "0")}
              </span>
              <h3 className="text-xl font-semibold text-black group-hover:text-(--accent) transition-colors duration-300">
                {item.title}
              </h3>
              <p className="mt-3 text-base leading-7  text-black/70">
                {item.description}
              </p>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}
