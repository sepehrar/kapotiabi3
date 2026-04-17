import { useTranslations } from "next-intl";
import Link from "next/link";

const searches = [
  {
    id: 1,
    title: "Oil change in Tallinn",
    subtitle: "BMW 320d 2018 · OEM-equivalent · 10 km",
    updatedAt: "2 hours ago",
    results: 3,
  },
  {
    id: 2,
    title: "Tire service in Tartu",
    subtitle: "Audi A4 2019 · Budget · 15 km",
    updatedAt: "Yesterday",
    results: 5,
  },
  {
    id: 3,
    title: "A/C service in Pärnu",
    subtitle: "Volkswagen Passat 2017 · OEM · 20 km",
    updatedAt: "3 days ago",
    results: 4,
  },
];

export default function AccountSearchesView() {
  const t = useTranslations("accountSearches");

  return (
    <div className="mx-auto w-full max-w-6xl">
      {/* title */}
      <div className="pt-4">
        <h1 className="text-2xl font-light tracking-tight text-white text-balance sm:text-3xl">
          {t("title")}
        </h1>
        <p className="mt-6 max-w-xl text-lg leading-8 text-white/50 text-balance">
          {t("description")}
        </p>
      </div>

      <div className="mt-12 space-y-4">
        {searches.map((search) => (
          <section
            key={search.id}
            className="w-full rounded-2xl border border-white/10 bg-(--pills-background) p-6 flex flex-row gap-3 items-start justify-between"
          >
            <div className="flex  gap-5 flex-row items-center justify-between w-full">
              <div className="">
                <h2 className="text-xl font-semibold text-white">
                  {search.title}
                </h2>
                <p className="mt-2 text-lg leading-6 text-white/60">
                  {search.subtitle}
                </p>

                <div className="mt-6 flex flex-wrap items-center gap-3 text-base text-white/50">
                  <span>
                    {search.results} {t("meta.results")}
                  </span>
                  <span className="text-white/20">•</span>
                  <span>
                    {t("meta.updated")} {search.updatedAt}
                  </span>
                </div>
              </div>

              <div className="flex flex-row gap-3">
                <Link
                  href="/search"
                  className="inline-flex shrink-0 items-center justify-center rounded-full bg-(--accent) px-5 py-3 text-sm font-semibold text-(--accent-foreground) ring-1 ring-inset ring-(--accent) transition hover:bg-(--accent-hover)"
                >
                  {t("actions.repeat")}
                </Link>

                <button
                  type="button"
                  className="shrink-0 rounded-full bg-(--pills-background) px-5 py-3 text-sm shadow ring-1 ring-inset ring-white/10 transition-colors duration-300 hover:bg-(--accent)/40 hover:ring-(--accent)"
                >
                  {t("actions.remove")}
                </button>
              </div>
            </div>
          </section>
        ))}
      </div>
    </div>
  );
}
