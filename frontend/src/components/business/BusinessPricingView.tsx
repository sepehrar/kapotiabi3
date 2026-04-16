"use client";

import { useTranslations } from "next-intl";

const pricingItems = [
  {
    id: 1,
    service: "Oil change",
    price: "from €79",
    tier: "Standard",
    updatedAt: "2 days ago",
  },
  {
    id: 2,
    service: "Brake service",
    price: "from €140",
    tier: "OEM-equivalent",
    updatedAt: "3 days ago",
  },
  {
    id: 3,
    service: "A/C diagnostics",
    price: "from €65",
    tier: "Inspection",
    updatedAt: "1 week ago",
  },
  {
    id: 4,
    service: "Tire service",
    price: "from €45",
    tier: "Seasonal",
    updatedAt: "Today",
  },
];

export default function BusinessPricingView() {
  const t = useTranslations("businessPricing");

  return (
    <div className="space-y-6">
      <section className="rounded-3xl border border-white/10 bg-(--pills-background) p-6">
        <div className="flex flex-col gap-4 lg:flex-row lg:items-end lg:justify-between">
          <div>
            <p className="text-sm text-white/60">{t("eyebrow")}</p>
            <h1 className="mt-2 text-3xl font-semibold tracking-tight text-white">
              {t("title")}
            </h1>
            <p className="mt-3 max-w-2xl text-sm text-white/70">
              {t("description")}
            </p>
          </div>

          <button
            type="button"
            className="inline-flex w-fit items-center justify-center rounded-full bg-(--accent) px-4 py-2 text-sm font-semibold text-(--accent-foreground) transition hover:bg-(--accent-hover) ring-1 ring-inset ring-(--accent)"
          >
            {t("updateButton")}
          </button>
        </div>
      </section>

      <section className="grid grid-cols-1 gap-4 sm:grid-cols-3">
        <div className="rounded-3xl border border-white/10 bg-(--pills-background) p-5">
          <p className="text-sm text-white/60">{t("stats.totalServices")}</p>
          <p className="mt-3 text-3xl font-semibold text-white">24</p>
        </div>

        <div className="rounded-3xl border border-white/10 bg-(--pills-background) p-5">
          <p className="text-sm text-white/60">{t("stats.lastUpdate")}</p>
          <p className="mt-3 text-3xl font-semibold text-white">Today</p>
        </div>

        <div className="rounded-3xl border border-white/10 bg-(--pills-background) p-5">
          <p className="text-sm text-white/60">{t("stats.visibility")}</p>
          <p className="mt-3 text-3xl font-semibold text-white">
            {t("status.active")}
          </p>
        </div>
      </section>

      <section className="rounded-3xl border border-white/10 bg-(--pills-background) p-6">
        <div className="flex items-start justify-between gap-4">
          <div>
            <h2 className="text-lg font-semibold text-white">
              {t("table.title")}
            </h2>
            <p className="mt-1 text-sm text-white/60">
              {t("table.description")}
            </p>
          </div>
        </div>

        <div className="mt-6 space-y-3">
          {pricingItems.map((item) => (
            <div
              key={item.id}
              className="flex flex-col gap-4 rounded-2xl border border-white/10 bg-black/40 p-4 lg:flex-row lg:items-center lg:justify-between"
            >
              <div className="min-w-0">
                <p className="text-sm font-semibold text-white">
                  {item.service}
                </p>
                <div className="mt-2 flex flex-wrap items-center gap-2 text-sm text-white/60">
                  <span>{item.price}</span>
                  <span>•</span>
                  <span>{item.tier}</span>
                  <span>•</span>
                  <span>
                    {t("table.updated")} {item.updatedAt}
                  </span>
                </div>
              </div>

              <button
                type="button"
                className="inline-flex w-fit items-center justify-center rounded-full border border-white/10 bg-white/5 px-4 py-2 text-sm font-medium text-white/80 transition hover:bg-white/10 hover:text-white"
              >
                {t("table.edit")}
              </button>
            </div>
          ))}
        </div>
      </section>
    </div>
  );
}
