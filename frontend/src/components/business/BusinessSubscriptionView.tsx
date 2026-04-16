"use client";

import { useTranslations } from "next-intl";

const plans = [
  {
    id: 1,
    name: "Basic",
    price: "€29/mo",
    description: "Manage your listing and keep core shop details visible.",
    current: false,
  },
  {
    id: 2,
    name: "Pro",
    price: "€79/mo",
    description:
      "Get more visibility, pricing tools, and lead management features.",
    current: true,
  },
  {
    id: 3,
    name: "Premium",
    price: "€149/mo",
    description:
      "Unlock featured placement, advanced analytics, and priority support.",
    current: false,
  },
];

export default function BusinessSubscriptionView() {
  const t = useTranslations("businessSubscription");

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
            {t("manageBilling")}
          </button>
        </div>
      </section>

      <section className="grid grid-cols-1 gap-4 sm:grid-cols-3">
        <div className="rounded-3xl border border-white/10 bg-(--pills-background) p-5">
          <p className="text-sm text-white/60">{t("summary.currentPlan")}</p>
          <p className="mt-3 text-3xl font-semibold text-white">Pro</p>
        </div>

        <div className="rounded-3xl border border-white/10 bg-(--pills-background) p-5">
          <p className="text-sm text-white/60">{t("summary.billingCycle")}</p>
          <p className="mt-3 text-3xl font-semibold text-white">
            {t("summary.monthly")}
          </p>
        </div>

        <div className="rounded-3xl border border-white/10 bg-(--pills-background) p-5">
          <p className="text-sm text-white/60">{t("summary.renewalDate")}</p>
          <p className="mt-3 text-3xl font-semibold text-white">24 Sep 2025</p>
        </div>
      </section>

      <section className="grid grid-cols-1 gap-6 xl:grid-cols-3">
        {plans.map((plan) => (
          <article
            key={plan.id}
            className="rounded-3xl border border-white/10 bg-(--pills-background) p-6"
          >
            <div className="flex items-start justify-between gap-3">
              <div>
                <h2 className="text-xl font-semibold text-white">
                  {plan.name}
                </h2>
                <p className="mt-2 text-sm text-white/60">{plan.price}</p>
              </div>

              {plan.current ? (
                <span className="rounded-full bg-(--accent) px-3 py-1 text-xs font-medium text-(--accent-foreground)">
                  {t("current")}
                </span>
              ) : null}
            </div>

            <p className="mt-4 text-sm leading-6 text-white/70">
              {plan.description}
            </p>

            <button
              type="button"
              className={`mt-6 inline-flex w-full items-center justify-center rounded-full px-4 py-2 text-sm font-semibold transition ring-1 ring-inset ${
                plan.current
                  ? "bg-white/5 text-white/50 ring-white/10"
                  : "bg-(--accent) text-(--accent-foreground) ring-(--accent) hover:bg-(--accent-hover)"
              }`}
            >
              {plan.current ? t("currentPlan") : t("choosePlan")}
            </button>
          </article>
        ))}
      </section>
    </div>
  );
}
