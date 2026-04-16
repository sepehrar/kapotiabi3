"use client";

import { useTranslations } from "next-intl";

const services = [
  { id: 1, name: "Oil change", price: "from €79" },
  { id: 2, name: "Brake service", price: "from €140" },
  { id: 3, name: "A/C diagnostics", price: "from €65" },
  { id: 4, name: "Tire service", price: "from €45" },
];

export default function BusinessListingView() {
  const t = useTranslations("businessListing");

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
            {t("editButton")}
          </button>
        </div>
      </section>

      <section className="grid grid-cols-1 gap-6 xl:grid-cols-3">
        <div className="xl:col-span-2 rounded-3xl border border-white/10 bg-(--pills-background) p-6">
          <div className="flex items-start justify-between gap-4">
            <div>
              <h2 className="text-lg font-semibold text-white">
                {t("services.title")}
              </h2>
              <p className="mt-1 text-sm text-white/60">
                {t("services.description")}
              </p>
            </div>
          </div>

          <div className="mt-6 space-y-3">
            {services.map((service) => (
              <div
                key={service.id}
                className="flex flex-col gap-3 rounded-2xl border border-white/10 bg-black/40 p-4 sm:flex-row sm:items-center sm:justify-between"
              >
                <div>
                  <p className="text-sm font-semibold text-white">
                    {service.name}
                  </p>
                  <p className="mt-1 text-sm text-white/60">{service.price}</p>
                </div>

                <button
                  type="button"
                  className="inline-flex w-fit items-center justify-center rounded-full border border-white/10 bg-white/5 px-4 py-2 text-sm font-medium text-white/80 transition hover:bg-white/10 hover:text-white"
                >
                  {t("services.edit")}
                </button>
              </div>
            ))}
          </div>
        </div>

        <div className="space-y-6">
          <div className="rounded-3xl border border-white/10 bg-(--pills-background) p-6">
            <h2 className="text-lg font-semibold text-white">
              {t("status.title")}
            </h2>
            <p className="mt-1 text-sm text-white/60">
              {t("status.description")}
            </p>

            <div className="mt-5 space-y-4">
              <div className="flex items-center justify-between">
                <span className="text-sm text-white/70">
                  {t("status.visibility")}
                </span>
                <span className="rounded-full bg-(--accent) px-3 py-1 text-xs font-medium text-(--accent-foreground)">
                  {t("status.active")}
                </span>
              </div>

              <div className="flex items-center justify-between">
                <span className="text-sm text-white/70">
                  {t("status.services")}
                </span>
                <span className="text-sm text-white">24</span>
              </div>

              <div className="flex items-center justify-between">
                <span className="text-sm text-white/70">
                  {t("status.lastUpdated")}
                </span>
                <span className="text-sm text-white">2 days ago</span>
              </div>
            </div>
          </div>

          <div className="rounded-3xl border border-white/10 bg-(--pills-background) p-6">
            <h2 className="text-lg font-semibold text-white">
              {t("profile.title")}
            </h2>
            <p className="mt-1 text-sm text-white/60">
              {t("profile.description")}
            </p>

            <div className="mt-5 space-y-4">
              <div className="flex items-center justify-between">
                <span className="text-sm text-white/70">
                  {t("profile.address")}
                </span>
                <span className="text-sm text-white">Tallinn</span>
              </div>

              <div className="flex items-center justify-between">
                <span className="text-sm text-white/70">
                  {t("profile.phone")}
                </span>
                <span className="text-sm text-white">+372 5555 1234</span>
              </div>

              <div className="flex items-center justify-between">
                <span className="text-sm text-white/70">
                  {t("profile.hours")}
                </span>
                <span className="text-sm text-white">Mon–Fri, 09:00–18:00</span>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
}
