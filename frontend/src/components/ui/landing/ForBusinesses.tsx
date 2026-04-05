import { useTranslations } from "next-intl";

export default function ForBusinesses() {
  const t = useTranslations("forBusinesses");

  const items = [
    {
      title: t("item1Title"),
      description: t("item1Description"),
    },
    {
      title: t("item2Title"),
      description: t("item2Description"),
    },
    {
      title: t("item3Title"),
      description: t("item3Description"),
    },
    {
      title: t("item4Title"),
      description: t("item4Description"),
    },
  ];

  return (
    <section aria-labelledby="for-businesses-heading" className="bg-white">
      <div className="mx-auto w-full max-w-screen-2xl px-6 py-16 sm:px-12 lg:px-16 lg:py-24">
        <div className="mx-auto max-w-3xl text-center">
          <h2
            id="for-businesses-heading"
            className="text-3xl font-bold tracking-tight text-black sm:text-4xl"
          >
            {t("title")}
          </h2>

          <p className="mt-4 text-base leading-7 text-black/70 sm:text-lg">
            {t("description")}
          </p>

          <a
            href="#"
            className="mt-8 inline-block rounded-full bg-[#f44b05] px-8 py-3 text-base font-medium text-white transition hover:bg-[#902d03]"
          >
            {t("button")}
          </a>
        </div>

        <div className="mx-auto mt-12 grid max-w-5xl gap-6 sm:grid-cols-2">
          {items.map((item) => (
            <div
              key={item.title}
              className="rounded-3xl bg-[#f8f8f8] p-6 shadow-sm ring-1 ring-black/5"
            >
              <h3 className="text-lg font-semibold text-black">{item.title}</h3>
              <p className="mt-3 text-base leading-7 text-black/70">
                {item.description}
              </p>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}
