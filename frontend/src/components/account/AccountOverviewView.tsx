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
      {/* title */}
      <div className="pt-4">
        <h1 className="text-2xl font-light tracking-tight text-white text-balance sm:text-3xl">
          {t("title")}
        </h1>
        <p className="mt-6 max-w-xl text-lg leading-8 text-white/50 text-balance">
          {t("description")}
        </p>
      </div>

      {/* stats */}
      <div className="mt-12 grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
        <div className="border-l-2 border-(--accent)/40 p-2 text-(--foreground)">
          <p className="text-3xl font-bold tracking-tight text-(--accent)">
            {stats.saved}
          </p>
          <p className="text-base font-medium">{t("stats.saved")}</p>
        </div>

        <div className="border-l-2 border-(--accent)/40 p-2 text-(--foreground)">
          <p className="text-3xl font-bold tracking-tight text-(--accent)">
            {stats.compare}
          </p>
          <p className="text-base font-medium">{t("stats.compare")}</p>
        </div>

        <div className="border-l-2 border-(--accent)/40 p-2 text-(--foreground)">
          <p className="text-3xl font-bold tracking-tight text-(--accent)">
            {stats.searches}
          </p>
          <p className="text-base font-medium">{t("stats.searches")}</p>
        </div>

        <div className="border-l-2 border-(--accent)/40 p-2 text-(--foreground)">
          <p className="text-3xl font-bold tracking-tight text-(--accent)">
            {stats.alerts}
          </p>
          <p className="text-base font-medium">{t("stats.alerts")}</p>
        </div>
      </div>

      <div className="mt-12 grid gap-12 xl:grid-cols-12">
        {/* recentSearches */}
        <div className="p-3  xl:col-span-7">
          <div className="flex items-center justify-between gap-4">
            <div>
              <h2 className="text-xl font-semibold text-white">
                {t("recentSearches.title")}
              </h2>
              <p className="mt-2 text-base leading-6 text-white/60 text-balance w-50">
                {t("recentSearches.description")}
              </p>
            </div>
          </div>

          <div className="space-y-2 mt-4 mb-4">
            {recentSearches.map((search) => (
              <div
                key={search.id}
                className="w-full rounded-2xl border border-white/10 bg-(--pills-background) px-4 py-6 flex flex-row gap-3 items-start justify-between"
              >
                <div className="flex  gap-4 flex-row items-start justify-between w-full">
                  <div>
                    <h3 className="text-base font-semibold text-white">
                      {search.title}
                    </h3>
                    <p className="mt-2 text-sm  text-white/50">
                      {search.subtitle}
                    </p>
                    <p className="mt-3 text-sm text-white">
                      {search.results} {t("recentSearches.results")}
                    </p>
                  </div>

                  <Link
                    href="/search"
                    className="shrink-0 rounded-full bg-(--pills-background) px-5 py-3 text-sm shadow ring-1 ring-inset ring-white/10 transition-colors duration-300 hover:bg-(--accent)/40 hover:ring-(--accent)"
                  >
                    {t("recentSearches.repeat")}
                  </Link>
                </div>
              </div>
            ))}
          </div>

          <Link
            href="/account/searches"
            className="text-sm font-semibold text-white transition hover:text-(--accent)"
          >
            {t("actions.viewAll")}
          </Link>
        </div>

        {/* savedServices */}
        <div className="p-3 xl:col-span-5">
          <div className="flex items-center justify-between gap-4">
            <div>
              <h2 className="text-xl font-semibold text-white">
                {t("savedServices.title")}
              </h2>
              <p className="mt-2 text-base leading-6 text-white/60 text-balance w-55">
                {t("savedServices.description")}
              </p>
            </div>
          </div>

          <div className="space-y-2 mt-4 mb-4">
            {savedServices.map((service) => (
              <div
                key={service.id}
                className="w-full rounded-2xl border border-white/10 bg-(--pills-background) px-4 py-6 flex flex-row gap-3 items-start justify-between"
              >
                <h3 className="text-base font-semibold text-white">
                  {service.name}
                </h3>

                <div className=" flex flex-col gap-3 text-sm text-white/60">
                  <span>
                    {t("savedServices.price")}: {service.price}
                  </span>
                  {/* <span>•</span> */}
                  <span>
                    {t("savedServices.distance")}: {service.distance}
                  </span>
                  {/* <span>•</span> */}
                  <span>
                    {t("savedServices.rating")}: {service.rating}
                  </span>
                </div>
              </div>
            ))}
          </div>

          <Link
            href="/account/saved"
            className="text-sm font-semibold text-white transition hover:text-(--accent) "
          >
            {t("actions.viewAll")}
          </Link>
        </div>
      </div>

      {/* bottons */}
      <div className="mt-12 w-full  p-6 border-t border-(--accent)/40">
        <div className="flex flex-col gap-12 lg:flex-row lg:items-center lg:justify-between">
          <div>
            <h2 className="text-2xl font-light tracking-tight text-white text-balance sm:text-3xl">
              {t("quickActions.title")}
            </h2>
            <p className="mt-6 w-55 text-lg leading-8 text-white/50 text-balance ">
              {t("quickActions.description")}
            </p>
          </div>

          <div className="flex flex-col gap-3 sm:flex-row sm:gap-5 shrink-0">
            <Link
              href="/search"
              className="inline-flex shrink-0 items-center justify-center rounded-full bg-(--accent) px-5 py-3 text-sm font-semibold text-(--accent-foreground) ring-1 ring-inset ring-(--accent) transition hover:bg-(--accent-hover)"
            >
              {t("quickActions.newSearch")}
            </Link>

            <Link
              href="/account/compare"
              className="shrink-0 rounded-full bg-(--pills-background) px-5 py-3 text-sm shadow ring-1 ring-inset ring-white/10 transition-colors duration-300 hover:bg-(--accent)/40 hover:ring-(--accent)"
            >
              {t("quickActions.openCompare")}
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
}
