"use client";

import { useTranslations } from "next-intl";

const leads = [
  {
    id: 1,
    service: "Oil change",
    car: "BMW 320d 2018",
    location: "Tallinn",
    status: "new",
    date: "Today, 10:24",
    note: "Customer is looking for a fast appointment this week.",
  },
  {
    id: 2,
    service: "Brake service",
    car: "Audi A4 2017",
    location: "Tartu",
    status: "viewed",
    date: "Today, 09:10",
    note: "Front brake pads and discs may need replacement.",
  },
  {
    id: 3,
    service: "A/C diagnostics",
    car: "Volkswagen Passat 2019",
    location: "Tallinn",
    status: "responded",
    date: "Yesterday",
    note: "Customer reported weak cooling and unusual noise.",
  },
  {
    id: 4,
    service: "Tire service",
    car: "Toyota RAV4 2020",
    location: "Pärnu",
    status: "new",
    date: "Yesterday",
    note: "Seasonal tire change, customer prefers morning slot.",
  },
];

function getStatusClasses(status: string) {
  if (status === "new") {
    return "border-(--accent)/30 bg-(--accent)/10 text-(--accent)";
  }

  if (status === "viewed") {
    return "border-white/10 bg-white/5 text-white/70";
  }

  return "border-emerald-500/20 bg-emerald-500/10 text-emerald-400";
}

export default function BusinessLeadsView() {
  const t = useTranslations("businessLeads");

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

      <section className="rounded-3xl border border-white/10 bg-(--pills-background) p-4 sm:p-5">
        <div className="flex flex-col gap-3 lg:flex-row">
          <div className="rounded-2xl border border-white/10 bg-black/40 px-4 py-3 text-sm text-white/70">
            {t("filters.allServices")}
          </div>
          <div className="rounded-2xl border border-white/10 bg-black/40 px-4 py-3 text-sm text-white/70">
            {t("filters.allStatuses")}
          </div>
          <div className="rounded-2xl border border-white/10 bg-black/40 px-4 py-3 text-sm text-white/70">
            {t("filters.allLocations")}
          </div>
        </div>
      </section>

      <section className="space-y-4">
        {leads.map((lead) => (
          <article
            key={lead.id}
            className="rounded-3xl border border-white/10 bg-(--pills-background) p-5 sm:p-6"
          >
            <div className="flex flex-col gap-4 lg:flex-row lg:items-start lg:justify-between">
              <div className="min-w-0">
                <div className="flex flex-wrap items-center gap-3">
                  <h2 className="text-lg font-semibold text-white">
                    {lead.service}
                  </h2>

                  <span
                    className={`inline-flex rounded-full border px-3 py-1 text-xs font-medium ${getStatusClasses(
                      lead.status,
                    )}`}
                  >
                    {t(`statuses.${lead.status}`)}
                  </span>
                </div>

                <p className="mt-2 text-sm text-white/60">
                  {lead.car} · {lead.location}
                </p>

                <p className="mt-4 max-w-2xl text-sm leading-6 text-white/70">
                  {lead.note}
                </p>
              </div>

              <div className="flex flex-col items-start gap-3 lg:items-end">
                <span className="text-xs text-white/50">{lead.date}</span>

                <button
                  type="button"
                  className="inline-flex items-center justify-center rounded-full bg-(--accent) px-4 py-2 text-sm font-semibold text-(--accent-foreground) transition hover:bg-(--accent-hover) ring-1 ring-inset ring-(--accent)"
                >
                  {t("openDetails")}
                </button>
              </div>
            </div>
          </article>
        ))}
      </section>
    </div>
  );
}
