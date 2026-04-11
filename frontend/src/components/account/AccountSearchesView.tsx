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
      <div className="mb-8">
        <h1 className="text-3xl font-bold tracking-tight text-white">
          {t("title")}
        </h1>
        <p className="mt-3 max-w-2xl text-sm leading-6 text-white/60 sm:text-base">
          {t("description")}
        </p>
      </div>

      <div className="space-y-4">
        {searches.map((search) => (
          <section
            key={search.id}
            className="rounded-3xl border border-black/10 bg-white p-6 shadow-sm sm:p-8"
          >
            <div className="flex flex-col gap-5 lg:flex-row lg:items-start lg:justify-between">
              <div className="min-w-0">
                <h2 className="text-xl font-semibold text-black">
                  {search.title}
                </h2>
                <p className="mt-2 text-sm leading-6 text-black/60">
                  {search.subtitle}
                </p>

                <div className="mt-4 flex flex-wrap items-center gap-3 text-sm text-black/50">
                  <span>
                    {search.results} {t("meta.results")}
                  </span>
                  <span className="text-black/20">•</span>
                  <span>
                    {t("meta.updated")} {search.updatedAt}
                  </span>
                </div>
              </div>

              <div className="flex flex-wrap gap-3">
                <Link
                  href="/search"
                  className="inline-flex items-center justify-center rounded-full bg-(--accent) px-5 py-2.5 text-sm font-semibold text-(--accent-foreground) transition hover:bg-(--accent-hover)"
                >
                  {t("actions.repeat")}
                </Link>

                <button
                  type="button"
                  className="inline-flex items-center justify-center rounded-full border border-black/10 px-5 py-2.5 text-sm font-semibold text-black transition hover:bg-black hover:text-white"
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
