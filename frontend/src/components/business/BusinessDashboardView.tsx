"use client";

import { useTranslations } from "next-intl";

export default function BusinessDashboardView() {
  const t = useTranslations("businessDashboard");

  const stats = [
    { label: t("stats.views"), value: "1,248", change: "+12.4%" },
    { label: t("stats.leads"), value: "86", change: "+8.1%" },
    { label: t("stats.calls"), value: "34", change: "+5.2%" },
    { label: t("stats.websiteClicks"), value: "19", change: "+2.7%" },
  ];

  const recentLeads = [
    {
      id: 1,
      service: "Oil change",
      car: "BMW 320d 2018",
      location: "Tallinn",
      status: t("leadStatuses.new"),
      date: "Today, 10:24",
    },
    {
      id: 2,
      service: "Brake service",
      car: "Audi A4 2017",
      location: "Tartu",
      status: t("leadStatuses.viewed"),
      date: "Today, 09:10",
    },
    {
      id: 3,
      service: "A/C diagnostics",
      car: "Volkswagen Passat 2019",
      location: "Tallinn",
      status: t("leadStatuses.responded"),
      date: "Yesterday",
    },
  ];

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

          <div className="inline-flex w-fit items-center rounded-full border border-white/10 bg-black px-4 py-2 text-sm text-white/70">
            {t("period")}
          </div>
        </div>
      </section>

      <section className="grid grid-cols-1 gap-4 sm:grid-cols-2 xl:grid-cols-4">
        {stats.map((stat) => (
          <div
            key={stat.label}
            className="rounded-3xl border border-white/10 bg-(--pills-background) p-5"
          >
            <p className="text-sm text-white/60">{stat.label}</p>
            <p className="mt-3 text-3xl font-semibold text-white">
              {stat.value}
            </p>
            <p className="mt-2 text-sm text-(--accent)">{stat.change}</p>
          </div>
        ))}
      </section>

      <section className="grid grid-cols-1 gap-6 xl:grid-cols-3">
        <div className="xl:col-span-2 rounded-3xl border border-white/10 bg-(--pills-background) p-6">
          <div className="flex items-start justify-between gap-4">
            <div>
              <h2 className="text-lg font-semibold text-white">
                {t("recentLeads.title")}
              </h2>
              <p className="mt-1 text-sm text-white/60">
                {t("recentLeads.description")}
              </p>
            </div>
          </div>

          <div className="mt-6 space-y-3">
            {recentLeads.map((lead) => (
              <div
                key={lead.id}
                className="rounded-2xl border border-white/10 bg-black/40 p-4"
              >
                <div className="flex flex-col gap-3 lg:flex-row lg:items-center lg:justify-between">
                  <div>
                    <p className="text-sm font-semibold text-white">
                      {lead.service}
                    </p>
                    <p className="mt-1 text-sm text-white/60">
                      {lead.car} · {lead.location}
                    </p>
                  </div>

                  <div className="flex items-center gap-3">
                    <span className="rounded-full border border-white/10 bg-white/5 px-3 py-1 text-xs text-white/70">
                      {lead.status}
                    </span>
                    <span className="text-xs text-white/50">{lead.date}</span>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>

        <div className="space-y-6">
          <div className="rounded-3xl border border-white/10 bg-(--pills-background) p-6">
            <h2 className="text-lg font-semibold text-white">
              {t("listingStatus.title")}
            </h2>
            <p className="mt-1 text-sm text-white/60">
              {t("listingStatus.description")}
            </p>

            <div className="mt-5 space-y-4">
              <div className="flex items-center justify-between">
                <span className="text-sm text-white/70">
                  {t("listingStatus.status")}
                </span>
                <span className="rounded-full bg-(--accent) px-3 py-1 text-xs font-medium text-(--accent-foreground)">
                  {t("listingStatus.active")}
                </span>
              </div>

              <div className="flex items-center justify-between">
                <span className="text-sm text-white/70">
                  {t("listingStatus.services")}
                </span>
                <span className="text-sm text-white">
                  {t("listingStatus.listed", { count: 24 })}
                </span>
              </div>

              <div className="flex items-center justify-between">
                <span className="text-sm text-white/70">
                  {t("listingStatus.lastUpdated")}
                </span>
                <span className="text-sm text-white">
                  {t("listingStatus.daysAgo", { count: 2 })}
                </span>
              </div>
            </div>
          </div>

          <div className="rounded-3xl border border-white/10 bg-(--pills-background) p-6">
            <h2 className="text-lg font-semibold text-white">
              {t("subscription.title")}
            </h2>
            <p className="mt-1 text-sm text-white/60">
              {t("subscription.description")}
            </p>

            <div className="mt-5 rounded-2xl border border-white/10 bg-black/40 p-4">
              <p className="text-sm text-white/60">
                {t("subscription.currentPlan")}
              </p>
              <p className="mt-2 text-xl font-semibold text-white">Pro</p>
              <p className="mt-2 text-sm text-white/70">
                {t("subscription.renewsOn", { date: "24 Sep 2025" })}
              </p>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
}
