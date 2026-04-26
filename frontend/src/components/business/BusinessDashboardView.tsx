"use client";

import Link from "next/link";
import { useLocale, useTranslations } from "next-intl";

export default function BusinessDashboardView() {
  const t = useTranslations("businessDashboard");
  const locale = useLocale();

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
      <section className="border-b border-white/10 pb-6">
        <div className="flex flex-col gap-5 lg:flex-row lg:items-end lg:justify-between">
          <div>
            <p className="text-sm text-white/50">{t("eyebrow")}</p>
            <h1 className="mt-3 text-2xl font-light tracking-tight text-white text-balance sm:text-3xl">
              {t("title")}
            </h1>
            <p className="mt-4 max-w-2xl text-sm leading-6 text-white/55">
              {t("description")}
            </p>
          </div>

          <div className="flex flex-wrap gap-3">
            <Link
              href={`/${locale}/business/leads`}
              className="inline-flex items-center justify-center rounded-full bg-(--accent) px-4 py-2 text-sm font-semibold text-(--accent-foreground) transition hover:bg-(--accent-hover)"
            >
              {t("recentLeads.title")}
            </Link>
            <span className="inline-flex items-center rounded-full border border-white/10 px-4 py-2 text-sm text-white/65">
              {t("period")}
            </span>
          </div>
        </div>
      </section>

      <section className="grid grid-cols-2 gap-px overflow-hidden rounded-lg border border-white/10 bg-white/10 xl:grid-cols-4">
        {stats.map((stat) => (
          <div key={stat.label} className="bg-black p-5">
            <p className="text-xs uppercase text-white/40">{stat.label}</p>
            <div className="mt-3 flex items-end justify-between gap-3">
              <p className="text-2xl font-semibold text-white">{stat.value}</p>
              <p className="text-sm text-(--accent)">{stat.change}</p>
            </div>
          </div>
        ))}
      </section>

      <section className="grid grid-cols-1 gap-6 xl:grid-cols-[minmax(0,1fr)_20rem]">
        <div className="rounded-lg border border-white/10">
          <div className="flex items-start justify-between gap-4 border-b border-white/10 p-5">
            <div>
              <h2 className="text-base font-semibold text-white">
                {t("recentLeads.title")}
              </h2>
              <p className="mt-1 text-sm text-white/50">
                {t("recentLeads.description")}
              </p>
            </div>
          </div>

          <div className="divide-y divide-white/10">
            {recentLeads.map((lead) => (
              <div
                key={lead.id}
                className="grid gap-3 p-5 sm:grid-cols-[minmax(0,1fr)_8rem_6rem]"
              >
                <div>
                  <p className="text-sm font-semibold text-white">
                    {lead.service}
                  </p>
                  <p className="mt-1 text-sm text-white/55">
                    {lead.car} · {lead.location}
                  </p>
                </div>
                <span className="h-fit w-fit rounded-full border border-white/10 px-3 py-1 text-xs text-white/70">
                  {lead.status}
                </span>
                <span className="text-sm text-white/45 sm:text-right">
                  {lead.date}
                </span>
              </div>
            ))}
          </div>
        </div>

        <aside className="space-y-6">
          <section className="rounded-lg border border-white/10 p-5">
            <h2 className="text-base font-semibold text-white">
              {t("listingStatus.title")}
            </h2>
            <p className="mt-1 text-sm text-white/50">
              {t("listingStatus.description")}
            </p>

            <dl className="mt-5 space-y-4">
              <div className="flex items-center justify-between gap-4">
                <dt className="text-sm text-white/60">
                  {t("listingStatus.status")}
                </dt>
                <dd className="rounded-full bg-(--accent) px-3 py-1 text-xs font-medium text-(--accent-foreground)">
                  {t("listingStatus.active")}
                </dd>
              </div>
              <div className="flex items-center justify-between gap-4">
                <dt className="text-sm text-white/60">
                  {t("listingStatus.services")}
                </dt>
                <dd className="text-sm text-white">
                  {t("listingStatus.listed", { count: 24 })}
                </dd>
              </div>
              <div className="flex items-center justify-between gap-4">
                <dt className="text-sm text-white/60">
                  {t("listingStatus.lastUpdated")}
                </dt>
                <dd className="text-sm text-white">
                  {t("listingStatus.daysAgo", { count: 2 })}
                </dd>
              </div>
            </dl>
          </section>

          <section className="rounded-lg border border-white/10 p-5">
            <h2 className="text-base font-semibold text-white">
              {t("subscription.title")}
            </h2>
            <p className="mt-1 text-sm text-white/50">
              {t("subscription.description")}
            </p>
            <dl className="mt-5 space-y-2">
              <div>
                <dt className="text-sm text-white/45">
                  {t("subscription.currentPlan")}
                </dt>
                <dd className="mt-1 text-xl font-semibold text-white">Pro</dd>
              </div>
              <div className="text-sm text-white/60">
                {t("subscription.renewsOn", { date: "24 Sep 2026" })}
              </div>
            </dl>
          </section>
        </aside>
      </section>
    </div>
  );
}
