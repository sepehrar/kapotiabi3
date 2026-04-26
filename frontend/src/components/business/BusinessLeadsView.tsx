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
    budget: "€95",
  },
  {
    id: 2,
    service: "Brake service",
    car: "Audi A4 2017",
    location: "Tartu",
    status: "viewed",
    date: "Today, 09:10",
    note: "Front brake pads and discs may need replacement.",
    budget: "From €140",
  },
  {
    id: 3,
    service: "A/C diagnostics",
    car: "Volkswagen Passat 2019",
    location: "Tallinn",
    status: "responded",
    date: "Yesterday",
    note: "Customer reported weak cooling and unusual noise.",
    budget: "€65",
  },
  {
    id: 4,
    service: "Tire service",
    car: "Toyota RAV4 2020",
    location: "Pärnu",
    status: "new",
    date: "Yesterday",
    note: "Seasonal tire change, customer prefers morning slot.",
    budget: "From €45",
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

          <span className="inline-flex w-fit items-center rounded-full border border-white/10 px-4 py-2 text-sm text-white/65">
            {t("period")}
          </span>
        </div>
      </section>

      <section className="flex flex-wrap gap-2">
        {[
          t("filters.allServices"),
          t("filters.allStatuses"),
          t("filters.allLocations"),
        ].map((filter) => (
          <button
            key={filter}
            type="button"
            className="rounded-full border border-white/10 px-4 py-2 text-sm text-white/70 transition hover:border-white/25 hover:text-white"
          >
            {filter}
          </button>
        ))}
      </section>

      <section className="overflow-hidden rounded-lg border border-white/10">
        <div className="hidden grid-cols-[minmax(0,1fr)_8rem_8rem_7rem] border-b border-white/10 px-5 py-3 text-xs uppercase text-white/35 md:grid">
          <span>{t("table.request")}</span>
          <span>{t("table.status")}</span>
          <span>{t("table.budget")}</span>
          <span className="text-right">{t("table.received")}</span>
        </div>

        <div className="divide-y divide-white/10">
          {leads.map((lead) => (
            <article
              key={lead.id}
              className="grid gap-4 p-5 md:grid-cols-[minmax(0,1fr)_8rem_8rem_7rem]"
            >
              <div className="min-w-0">
                <div className="flex flex-wrap items-center gap-3">
                  <h2 className="text-base font-semibold text-white">
                    {lead.service}
                  </h2>
                  <span className="text-sm text-white/45">{lead.location}</span>
                </div>
                <p className="mt-1 text-sm text-white/55">{lead.car}</p>
                <p className="mt-3 max-w-2xl text-sm leading-6 text-white/65">
                  {lead.note}
                </p>
              </div>

              <span
                className={`h-fit w-fit rounded-full border px-3 py-1 text-xs font-medium ${getStatusClasses(
                  lead.status,
                )}`}
              >
                {t(`statuses.${lead.status}`)}
              </span>

              <span className="text-sm font-semibold text-white">
                {lead.budget}
              </span>

              <div className="flex items-center justify-between gap-3 md:block md:text-right">
                <span className="text-xs text-white/45">{lead.date}</span>
                <button
                  type="button"
                  className="mt-0 inline-flex items-center justify-center rounded-full bg-(--accent) px-4 py-2 text-sm font-semibold text-(--accent-foreground) transition hover:bg-(--accent-hover) md:mt-4"
                >
                  {t("openDetails")}
                </button>
              </div>
            </article>
          ))}
        </div>
      </section>
    </div>
  );
}
