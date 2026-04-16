"use client";

import { useTranslations } from "next-intl";

export default function BusinessProfileView() {
  const t = useTranslations("businessProfile");

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
        <div className="xl:col-span-2 space-y-6">
          <div className="rounded-3xl border border-white/10 bg-(--pills-background) p-6">
            <h2 className="text-lg font-semibold text-white">
              {t("businessInfo.title")}
            </h2>
            <p className="mt-1 text-sm text-white/60">
              {t("businessInfo.description")}
            </p>

            <div className="mt-6 grid grid-cols-1 gap-4 sm:grid-cols-2">
              <div className="rounded-2xl border border-white/10 bg-black/40 p-4">
                <p className="text-sm text-white/60">
                  {t("businessInfo.businessName")}
                </p>
                <p className="mt-2 text-sm font-medium text-white">
                  Tallinn AutoCare
                </p>
              </div>

              <div className="rounded-2xl border border-white/10 bg-black/40 p-4">
                <p className="text-sm text-white/60">
                  {t("businessInfo.phone")}
                </p>
                <p className="mt-2 text-sm font-medium text-white">
                  +372 5555 1234
                </p>
              </div>

              <div className="rounded-2xl border border-white/10 bg-black/40 p-4 sm:col-span-2">
                <p className="text-sm text-white/60">
                  {t("businessInfo.address")}
                </p>
                <p className="mt-2 text-sm font-medium text-white">
                  Peterburi tee 12, Tallinn
                </p>
              </div>

              <div className="rounded-2xl border border-white/10 bg-black/40 p-4">
                <p className="text-sm text-white/60">
                  {t("businessInfo.email")}
                </p>
                <p className="mt-2 text-sm font-medium text-white">
                  hello@tallinnautocare.ee
                </p>
              </div>

              <div className="rounded-2xl border border-white/10 bg-black/40 p-4">
                <p className="text-sm text-white/60">
                  {t("businessInfo.website")}
                </p>
                <p className="mt-2 text-sm font-medium text-white">
                  tallinnautocare.ee
                </p>
              </div>
            </div>
          </div>

          <div className="rounded-3xl border border-white/10 bg-(--pills-background) p-6">
            <h2 className="text-lg font-semibold text-white">
              {t("hours.title")}
            </h2>
            <p className="mt-1 text-sm text-white/60">
              {t("hours.description")}
            </p>

            <div className="mt-6 space-y-3">
              {[
                { day: t("days.monFri"), value: "09:00–18:00" },
                { day: t("days.sat"), value: "10:00–15:00" },
                { day: t("days.sun"), value: t("hours.closed") },
              ].map((item) => (
                <div
                  key={item.day}
                  className="flex items-center justify-between rounded-2xl border border-white/10 bg-black/40 p-4"
                >
                  <span className="text-sm text-white/70">{item.day}</span>
                  <span className="text-sm font-medium text-white">
                    {item.value}
                  </span>
                </div>
              ))}
            </div>
          </div>
        </div>

        <div className="space-y-6">
          <div className="rounded-3xl border border-white/10 bg-(--pills-background) p-6">
            <h2 className="text-lg font-semibold text-white">
              {t("owner.title")}
            </h2>
            <p className="mt-1 text-sm text-white/60">
              {t("owner.description")}
            </p>

            <div className="mt-5 rounded-2xl border border-white/10 bg-black/40 p-4">
              <p className="text-sm text-white/60">{t("owner.name")}</p>
              <p className="mt-2 text-sm font-medium text-white">Olga T.</p>
            </div>

            <div className="mt-4 rounded-2xl border border-white/10 bg-black/40 p-4">
              <p className="text-sm text-white/60">{t("owner.role")}</p>
              <p className="mt-2 text-sm font-medium text-white">
                {t("owner.shopOwner")}
              </p>
            </div>
          </div>

          <div className="rounded-3xl border border-white/10 bg-(--pills-background) p-6">
            <h2 className="text-lg font-semibold text-white">
              {t("visibility.title")}
            </h2>
            <p className="mt-1 text-sm text-white/60">
              {t("visibility.description")}
            </p>

            <div className="mt-5 space-y-4">
              <div className="flex items-center justify-between">
                <span className="text-sm text-white/70">
                  {t("visibility.status")}
                </span>
                <span className="rounded-full bg-(--accent) px-3 py-1 text-xs font-medium text-(--accent-foreground)">
                  {t("visibility.active")}
                </span>
              </div>

              <div className="flex items-center justify-between">
                <span className="text-sm text-white/70">
                  {t("visibility.lastUpdated")}
                </span>
                <span className="text-sm text-white">2 days ago</span>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
}
