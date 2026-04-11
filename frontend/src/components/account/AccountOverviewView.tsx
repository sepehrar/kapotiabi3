import Link from "next/link";
import { useTranslations } from "next-intl";

const stats = {
  saved: 12,
  compare: 3,
  searches: 8,
  alerts: 4,
};

const recentSearches = [
  {
    id: 1,
    title: "Oil change in Tallinn",
    subtitle: "BMW 320d 2018 · OEM-equivalent · 10 km",
    results: 3,
  },
  {
    id: 2,
    title: "Tire service in Tartu",
    subtitle: "Audi A4 2019 · Budget · 15 km",
    results: 5,
  },
];

const savedServices = [
  {
    id: 1,
    name: "AutoExpert Tallinn",
    price: "€95",
    distance: "3.2 km",
    rating: "4.7",
  },
  {
    id: 2,
    name: "Nord Service",
    price: "From €80",
    distance: "5.6 km",
    rating: "4.5",
  },
];

export default function AccountOverviewView() {
  const t = useTranslations("accountOverview");

  return (
    <div className="mx-auto w-full max-w-7xl">
      <div className="mb-8">
        <h1 className="text-3xl font-bold tracking-tight text-white">
          {t("title")}
        </h1>
        <p className="mt-3 max-w-2xl text-sm leading-6 text-white/60 sm:text-base">
          {t("description")}
        </p>
      </div>

      <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
        <section className="rounded-3xl border border-black/10 bg-white p-6 shadow-sm">
          <p className="text-sm font-medium text-black/60">
            {t("stats.saved")}
          </p>
          <p className="mt-3 text-3xl font-bold tracking-tight text-black">
            {stats.saved}
          </p>
        </section>

        <section className="rounded-3xl border border-black/10 bg-white p-6 shadow-sm">
          <p className="text-sm font-medium text-black/60">
            {t("stats.compare")}
          </p>
          <p className="mt-3 text-3xl font-bold tracking-tight text-black">
            {stats.compare}
          </p>
        </section>

        <section className="rounded-3xl border border-black/10 bg-white p-6 shadow-sm">
          <p className="text-sm font-medium text-black/60">
            {t("stats.searches")}
          </p>
          <p className="mt-3 text-3xl font-bold tracking-tight text-black">
            {stats.searches}
          </p>
        </section>

        <section className="rounded-3xl border border-black/10 bg-white p-6 shadow-sm">
          <p className="text-sm font-medium text-black/60">
            {t("stats.alerts")}
          </p>
          <p className="mt-3 text-3xl font-bold tracking-tight text-black">
            {stats.alerts}
          </p>
        </section>
      </div>

      <div className="mt-6 grid gap-6 xl:grid-cols-12">
        <section className="rounded-3xl border border-black/10 bg-white p-6 shadow-sm sm:p-8 xl:col-span-7">
          <div className="flex items-center justify-between gap-4">
            <div>
              <h2 className="text-xl font-semibold text-black">
                {t("recentSearches.title")}
              </h2>
              <p className="mt-2 text-sm leading-6 text-black/60">
                {t("recentSearches.description")}
              </p>
            </div>

            <Link
              href="/account/searches"
              className="text-sm font-semibold text-black transition hover:text-(--accent)"
            >
              {t("actions.viewAll")}
            </Link>
          </div>

          <div className="mt-6 space-y-4">
            {recentSearches.map((search) => (
              <div
                key={search.id}
                className="rounded-2xl border border-black/10 p-4"
              >
                <div className="flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
                  <div>
                    <h3 className="text-base font-semibold text-black">
                      {search.title}
                    </h3>
                    <p className="mt-1 text-sm leading-6 text-black/60">
                      {search.subtitle}
                    </p>
                    <p className="mt-3 text-sm text-black/50">
                      {search.results} {t("recentSearches.results")}
                    </p>
                  </div>

                  <Link
                    href="/search"
                    className="inline-flex items-center justify-center rounded-full bg-(--accent) px-4 py-2 text-sm font-semibold text-(--accent-foreground) transition hover:bg-(--accent-hover)"
                  >
                    {t("recentSearches.repeat")}
                  </Link>
                </div>
              </div>
            ))}
          </div>
        </section>

        <section className="rounded-3xl border border-black/10 bg-white p-6 shadow-sm sm:p-8 xl:col-span-5">
          <div className="flex items-center justify-between gap-4">
            <div>
              <h2 className="text-xl font-semibold text-black">
                {t("savedServices.title")}
              </h2>
              <p className="mt-2 text-sm leading-6 text-black/60">
                {t("savedServices.description")}
              </p>
            </div>

            <Link
              href="/account/saved"
              className="text-sm font-semibold text-black transition hover:text-(--accent)"
            >
              {t("actions.viewAll")}
            </Link>
          </div>

          <div className="mt-6 space-y-4">
            {savedServices.map((service) => (
              <div
                key={service.id}
                className="rounded-2xl border border-black/10 p-4"
              >
                <h3 className="text-base font-semibold text-black">
                  {service.name}
                </h3>

                <div className="mt-3 flex flex-wrap gap-3 text-sm text-black/60">
                  <span>
                    {t("savedServices.price")}: {service.price}
                  </span>
                  <span>•</span>
                  <span>
                    {t("savedServices.distance")}: {service.distance}
                  </span>
                  <span>•</span>
                  <span>
                    {t("savedServices.rating")}: {service.rating}
                  </span>
                </div>
              </div>
            ))}
          </div>
        </section>

        <section className="rounded-3xl border border-black/10 bg-white p-6 shadow-sm sm:p-8 xl:col-span-12">
          <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
            <div>
              <h2 className="text-xl font-semibold text-black">
                {t("quickActions.title")}
              </h2>
              <p className="mt-2 text-sm leading-6 text-black/60">
                {t("quickActions.description")}
              </p>
            </div>

            <div className="flex flex-wrap gap-3">
              <Link
                href="/search"
                className="inline-flex items-center justify-center rounded-full bg-(--accent) px-5 py-2.5 text-sm font-semibold text-(--accent-foreground) transition hover:bg-(--accent-hover)"
              >
                {t("quickActions.newSearch")}
              </Link>

              <Link
                href="/account/compare"
                className="inline-flex items-center justify-center rounded-full border border-black/10 px-5 py-2.5 text-sm font-semibold text-black transition hover:bg-black hover:text-white"
              >
                {t("quickActions.openCompare")}
              </Link>
            </div>
          </div>
        </section>
      </div>
    </div>
  );
}
