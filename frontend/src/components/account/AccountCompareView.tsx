"use client";

import { useState } from "react";
import Link from "next/link";
import { useTranslations } from "next-intl";
import { BookmarkIcon } from "@heroicons/react/24/outline";

const comparedServices = [
  {
    id: 1,
    name: "AutoExpert Tallinn",
    price: "€95",
    distance: "3.2 km",
    rating: "4.7",
    reviews: 214,
    updatedAt: "2 days ago",
    partsQuality: "OEM-equivalent",
  },
  {
    id: 2,
    name: "Nord Service",
    price: "From €80",
    distance: "5.6 km",
    rating: "4.5",
    reviews: 98,
    updatedAt: "Today",
    partsQuality: "Budget",
  },
  {
    id: 3,
    name: "FixCar Center",
    price: "€110",
    distance: "6.8 km",
    rating: "4.8",
    reviews: 156,
    updatedAt: "5 days ago",
    partsQuality: "OEM",
  },
];

type Service = (typeof comparedServices)[0];

const rows: { key: string; label: string; getValue: (s: Service) => string }[] =
  [
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
  const [activeIndex, setActiveIndex] = useState(0);

  const cols = comparedServices.length;

  return (
    <div className="mx-auto w-full max-w-6xl">
      {/* title */}
      <div className="pt-4">
        <h1 className="text-2xl font-light tracking-tight text-white text-balance sm:text-3xl">
          {t("title")}
        </h1>
        <p className="mt-6 max-w-xl text-lg leading-8 text-white/50 text-balance">
          {t("description")}
        </p>
      </div>

      {/* ── Desktop table ── */}
      <div className="mt-12 hidden sm:block rounded-2xl border border-white/10 bg-(--pills-background) overflow-hidden">
        {/* Header — service names */}
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
                className="shrink-0 rounded-full bg-(--pills-background) p-2 text-white/60 shadow ring-1 ring-inset ring-white/10 transition-colors duration-300 hover:bg-(--accent)/40 hover:ring-(--accent) hover:text-white"
              >
                <BookmarkIcon className="size-4" />
              </button>
            </div>
          ))}
        </div>

        {/* Data rows */}
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

        {/* Actions row */}
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
                href={`/booking/${service.id}`}
                className="inline-flex items-center justify-center rounded-full bg-(--accent) px-4 py-2 text-sm font-semibold text-(--accent-foreground) ring-1 ring-inset ring-(--accent) transition hover:bg-(--accent-hover)"
              >
                {t("actions.book")}
              </Link>

              <button
                type="button"
                className="inline-flex items-center justify-center rounded-full bg-(--pills-background) px-4 py-2 text-sm font-semibold text-white/60 shadow ring-1 ring-inset ring-white/10 transition-colors duration-300 hover:bg-(--accent)/40 hover:ring-(--accent) hover:text-white"
              >
                {t("actions.remove")}
              </button>
            </div>
          ))}
        </div>
      </div>

      {/* ── Mobile: tab switcher + card ── */}
      <div className="mt-12 sm:hidden">
        {/* Tabs */}
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

        {/* Card */}
        <div className="rounded-2xl border border-white/10 bg-(--pills-background) overflow-hidden">
          <div className="p-5 border-b border-white/10 flex items-center justify-between gap-3">
            <h2 className="text-xl font-semibold text-white">
              {comparedServices[activeIndex].name}
            </h2>

            <button
              type="button"
              aria-label={t("actions.save")}
              className="shrink-0 rounded-full bg-(--pills-background) p-2 text-white/60 shadow ring-1 ring-inset ring-white/10 transition-colors duration-300 hover:bg-(--accent)/40 hover:ring-(--accent) hover:text-white"
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
                {row.getValue(comparedServices[activeIndex])}
              </span>
            </div>
          ))}

          <div className="flex flex-col gap-2 p-5">
            <Link
              href={`/booking/${comparedServices[activeIndex].id}`}
              className="inline-flex items-center justify-center rounded-full bg-(--accent) px-4 py-3 text-sm font-semibold text-(--accent-foreground) ring-1 ring-inset ring-(--accent) transition hover:bg-(--accent-hover)"
            >
              {t("actions.book")}
            </Link>

            <button
              type="button"
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
