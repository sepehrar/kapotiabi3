"use client";

import { useState } from "react";
import Link from "next/link";
import { useLocale, useTranslations } from "next-intl";
import { BookmarkIcon } from "@heroicons/react/24/outline";
import {
  buildDemoService,
  getCompareDraft,
  type DemoCompareService,
  updateCompareServices,
} from "@/lib/demoFlowSession";

const fallbackServices: DemoCompareService[] = [
  buildDemoService({
    id: 1,
    name: "AutoExpert Tallinn",
    price: "€95",
    distance: "3.2 km",
    rating: "4.7",
    reviews: 214,
    updatedAt: "2 days ago",
  }),
  buildDemoService({
    id: 2,
    name: "Nord Service",
    price: "From €80",
    distance: "5.6 km",
    rating: "4.5",
    reviews: 98,
    updatedAt: "Today",
  }),
  buildDemoService({
    id: 3,
    name: "FixCar Center",
    price: "€110",
    distance: "6.8 km",
    rating: "4.8",
    reviews: 156,
    updatedAt: "5 days ago",
  }),
];

const rows: {
  key: string;
  label: string;
  getValue: (s: DemoCompareService) => string;
}[] = [
  { key: "price", label: "rows.price", getValue: (s) => s.price },
  { key: "distance", label: "rows.distance", getValue: (s) => s.distance },
  {
    key: "rating",
    label: "rows.rating",
    getValue: (s) => `${s.rating} (${s.reviews})`,
  },
  {
    key: "partsQuality",
    label: "rows.partsQuality",
    getValue: (s) => s.partsQuality,
  },
  {
    key: "priceStatus",
    label: "rows.priceStatus",
    getValue: (s) => s.updatedAt,
  },
];

export default function AccountCompareView() {
  const t = useTranslations("accountCompare");
  const locale = useLocale();
  const [comparedServices, setComparedServices] = useState<
    DemoCompareService[]
  >(() => {
    const draft = getCompareDraft();
    return draft?.services.length ? draft.services : fallbackServices;
  });
  const [activeIndex, setActiveIndex] = useState(0);

  const cols = comparedServices.length;
  const activeService =
    comparedServices[Math.min(activeIndex, comparedServices.length - 1)];

  function persistServices(nextServices: DemoCompareService[]) {
    const visibleServices = nextServices.length ? nextServices : fallbackServices;

    setComparedServices(visibleServices);
    updateCompareServices(nextServices);
    setActiveIndex((currentIndex) =>
      Math.min(currentIndex, Math.max(visibleServices.length - 1, 0)),
    );
  }

  function toggleSaved(serviceId: string) {
    persistServices(
      comparedServices.map((service) =>
        service.id === serviceId
          ? { ...service, saved: !service.saved }
          : service,
      ),
    );
  }

  function removeService(serviceId: string) {
    persistServices(
      comparedServices.filter((service) => service.id !== serviceId),
    );
  }

  return (
    <div className="mx-auto w-full max-w-6xl">
      <div className="pt-4">
        <h1 className="text-2xl font-light tracking-tight text-white text-balance sm:text-3xl">
          {t("title")}
        </h1>
        <p className="mt-6 max-w-xl text-lg leading-8 text-white/50 text-balance">
          {t("description")}
        </p>
      </div>

      <div className="mt-12 hidden sm:block rounded-2xl border border-white/10 bg-(--pills-background) overflow-hidden">
        <div
          className="grid border-b border-white/10"
          style={{ gridTemplateColumns: `180px repeat(${cols}, 1fr)` }}
        >
          <div className="p-5 text-sm font-medium text-white/30">
            {t("rows.service")}
          </div>

          {comparedServices.map((service) => (
            <div
              key={service.id}
              className="p-5 border-l border-white/10 flex items-center justify-between gap-3"
            >
              <h2 className="text-base font-semibold text-white">
                {service.name}
              </h2>

              <button
                type="button"
                aria-label={t("actions.save")}
                onClick={() => toggleSaved(service.id)}
                className={`shrink-0 rounded-full bg-(--pills-background) p-2 shadow ring-1 ring-inset transition-colors duration-300 hover:bg-(--accent)/40 hover:ring-(--accent) hover:text-white ${
                  service.saved
                    ? "text-(--accent) ring-(--accent)"
                    : "text-white/60 ring-white/10"
                }`}
              >
                <BookmarkIcon className="size-4" />
              </button>
            </div>
          ))}
        </div>

        {rows.map((row) => (
          <div
            key={row.key}
            className="grid border-b border-white/10"
            style={{ gridTemplateColumns: `180px repeat(${cols}, 1fr)` }}
          >
            <div className="p-5 text-sm text-white/40 flex items-center">
              {t(row.label)}
            </div>

            {comparedServices.map((service) => (
              <div
                key={`${service.id}-${row.key}`}
                className="p-5 border-l border-white/10 flex items-center"
              >
                <span className="text-sm font-semibold text-white">
                  {row.getValue(service)}
                </span>
              </div>
            ))}
          </div>
        ))}

        <div
          className="grid"
          style={{ gridTemplateColumns: `180px repeat(${cols}, 1fr)` }}
        >
          <div className="p-5 text-sm text-white/40 flex items-center">
            {t("rows.actions")}
          </div>

          {comparedServices.map((service) => (
            <div
              key={`${service.id}-actions`}
              className="p-5 border-l border-white/10 flex flex-col gap-2"
            >
              <Link
                href={`/${locale}/booking/${service.id}`}
                className="inline-flex items-center justify-center rounded-full bg-(--accent) px-4 py-2 text-sm font-semibold text-(--accent-foreground) ring-1 ring-inset ring-(--accent) transition hover:bg-(--accent-hover)"
              >
                {t("actions.book")}
              </Link>

              <button
                type="button"
                onClick={() => removeService(service.id)}
                className="inline-flex items-center justify-center rounded-full bg-(--pills-background) px-4 py-2 text-sm font-semibold text-white/60 shadow ring-1 ring-inset ring-white/10 transition-colors duration-300 hover:bg-(--accent)/40 hover:ring-(--accent) hover:text-white"
              >
                {t("actions.remove")}
              </button>
            </div>
          ))}
        </div>
      </div>

      <div className="mt-12 sm:hidden">
        <div className="flex gap-2 mb-4">
          {comparedServices.map((service, i) => (
            <button
              key={service.id}
              type="button"
              onClick={() => setActiveIndex(i)}
              className={`flex-1 rounded-full px-3 py-2 text-sm font-medium transition-colors duration-200 ring-1 ring-inset ${
                activeIndex === i
                  ? "bg-(--accent) text-(--accent-foreground) ring-(--accent)"
                  : "bg-(--pills-background) text-white/50 ring-white/10"
              }`}
            >
              {service.name.split(" ")[0]}
            </button>
          ))}
        </div>

        <div className="rounded-2xl border border-white/10 bg-(--pills-background) overflow-hidden">
          <div className="p-5 border-b border-white/10 flex items-center justify-between gap-3">
            <h2 className="text-xl font-semibold text-white">
              {activeService.name}
            </h2>

            <button
              type="button"
              aria-label={t("actions.save")}
              onClick={() => toggleSaved(activeService.id)}
              className={`shrink-0 rounded-full bg-(--pills-background) p-2 shadow ring-1 ring-inset transition-colors duration-300 hover:bg-(--accent)/40 hover:ring-(--accent) hover:text-white ${
                activeService.saved
                  ? "text-(--accent) ring-(--accent)"
                  : "text-white/60 ring-white/10"
              }`}
            >
              <BookmarkIcon className="size-5" />
            </button>
          </div>

          {rows.map((row) => (
            <div
              key={row.key}
              className="flex items-center justify-between p-5 border-b border-white/10"
            >
              <span className="text-sm text-white/40">{t(row.label)}</span>
              <span className="text-sm font-semibold text-white">
                {row.getValue(activeService)}
              </span>
            </div>
          ))}

          <div className="flex flex-col gap-2 p-5">
            <Link
              href={`/${locale}/booking/${activeService.id}`}
              className="inline-flex items-center justify-center rounded-full bg-(--accent) px-4 py-3 text-sm font-semibold text-(--accent-foreground) ring-1 ring-inset ring-(--accent) transition hover:bg-(--accent-hover)"
            >
              {t("actions.book")}
            </Link>

            <button
              type="button"
              onClick={() => removeService(activeService.id)}
              className="inline-flex items-center justify-center rounded-full bg-(--pills-background) px-4 py-3 text-sm font-semibold text-white/60 shadow ring-1 ring-inset ring-white/10 transition-colors duration-300 hover:bg-(--accent)/40 hover:ring-(--accent) hover:text-white"
            >
              {t("actions.remove")}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
