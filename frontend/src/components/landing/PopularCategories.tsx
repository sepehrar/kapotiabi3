import { useTranslations } from "next-intl";

export default function PopularCategories() {
  const t = useTranslations("popularCategories");

  const categories = [
    t("item1"),
    t("item2"),
    t("item3"),
    t("item4"),
    t("item5"),
    t("item6"),
  ];

  return (
    <section aria-labelledby="popular-categories-heading" className="bg-white">
      <div className="mx-auto w-full max-w-screen-2xl px-6 py-16 sm:px-12 lg:px-16 lg:py-24">
        <div className="mx-auto max-w-2xl text-center">
          <h2
            id="popular-categories-heading"
            className="text-3xl font-bold tracking-tight text-black sm:text-4xl"
          >
            {t("title")}
          </h2>

          <p className="mt-4 text-base leading-7 text-black/70 sm:text-lg">
            {t("description")}
          </p>
        </div>

        <div className="mx-auto mt-12 grid max-w-5xl gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {categories.map((category) => (
            <a
              key={category}
              href="#"
              className="rounded-3xl bg-[#f8f8f8] px-6 py-5 text-left text-lg font-medium text-black shadow-sm ring-1 ring-black/5 transition hover:bg-black hover:text-white"
            >
              {category}
            </a>
          ))}
        </div>
      </div>
    </section>
  );
}
