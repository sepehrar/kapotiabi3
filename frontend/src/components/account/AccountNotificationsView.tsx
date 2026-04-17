"use client";

import { useState } from "react";
import { useTranslations } from "next-intl";

function AccentCheckbox({
  checked,
  onChange,
}: {
  checked: boolean;
  onChange: (value: boolean) => void;
}) {
  return (
    <div className="flex h-7 shrink-0 items-center">
      <div className="group grid size-6 grid-cols-1">
        <input
          type="checkbox"
          checked={checked}
          onChange={(event) => onChange(event.target.checked)}
          className="col-start-1 row-start-1 size-6 appearance-none rounded-md border border-white/15 bg-white/5 checked:border-[var(--accent)] checked:bg-[var(--accent)] hover:border-white/30 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-[var(--accent)] forced-colors:appearance-auto"
        />
        <svg
          fill="none"
          viewBox="0 0 14 14"
          className="pointer-events-none col-start-1 row-start-1 size-4 self-center justify-self-center stroke-[var(--accent-foreground)]"
        >
          <path
            d="M3 8L6 11L11 3.5"
            strokeWidth={2.5}
            strokeLinecap="round"
            strokeLinejoin="round"
            className={checked ? "opacity-100" : "opacity-0"}
          />
        </svg>
      </div>
    </div>
  );
}

function NotificationRow({
  title,
  description,
  checked,
  onChange,
}: {
  title: string;
  description: string;
  checked: boolean;
  onChange: (value: boolean) => void;
}) {
  return (
    <div
      className={`flex w-full flex-row items-center justify-between gap-3 rounded-2xl border p-6 transition-all duration-200 ${
        checked
          ? "border-white/10 bg-(--pills-background)"
          : "border-white/10 bg-(--pills-background)/50"
      }`}
    >
      <div className={checked ? "opacity-100" : "opacity-50"}>
        <h2 className="text-base font-semibold text-white">{title}</h2>
        <p className="mt-1 text-base leading-6 text-white/60">{description}</p>
      </div>
      <AccentCheckbox checked={checked} onChange={onChange} />
    </div>
  );
}

export default function AccountNotificationsView() {
  const t = useTranslations("accountNotifications");

  const [priceUpdates, setPriceUpdates] = useState(true);
  const [savedServices, setSavedServices] = useState(true);
  const [searchAlerts, setSearchAlerts] = useState(false);
  const [accountUpdates, setAccountUpdates] = useState(true);
  const [emailDelivery, setEmailDelivery] = useState(true);
  const [browserDelivery, setBrowserDelivery] = useState(false);

  return (
    <div className="mx-auto w-full max-w-5xl">
      <div className="mb-8 pt-4">
        <h1 className="text-2xl font-light tracking-tight text-white text-balance sm:text-3xl">
          {t("title")}
        </h1>
        <p className="mt-4 max-w-xl text-lg leading-8 text-white/50 text-balance">
          {t("description")}
        </p>
      </div>

      <div className="space-y-6">
        <section>
          <div className="space-y-2">
            <NotificationRow
              title={t("items.priceUpdates.title")}
              description={t("items.priceUpdates.description")}
              checked={priceUpdates}
              onChange={setPriceUpdates}
            />

            <NotificationRow
              title={t("items.savedServices.title")}
              description={t("items.savedServices.description")}
              checked={savedServices}
              onChange={setSavedServices}
            />

            <NotificationRow
              title={t("items.searchAlerts.title")}
              description={t("items.searchAlerts.description")}
              checked={searchAlerts}
              onChange={setSearchAlerts}
            />

            <NotificationRow
              title={t("items.accountUpdates.title")}
              description={t("items.accountUpdates.description")}
              checked={accountUpdates}
              onChange={setAccountUpdates}
            />
          </div>
        </section>

        <section>
          <div className="mb-4">
            <h2 className="text-2xl font-light tracking-tight text-white text-balance sm:text-3xl">
              {t("delivery.title")}
            </h2>
            <p className="mt-4 max-w-xl text-lg leading-8 text-white/50 text-balance">
              {t("delivery.description")}
            </p>
          </div>

          <div className="space-y-2">
            <label className="block">
              <NotificationRow
                title={t("delivery.email")}
                description="tom@example.com"
                checked={emailDelivery}
                onChange={setEmailDelivery}
              />
            </label>

            <label className="block">
              <NotificationRow
                title={t("delivery.browser")}
                description={t("delivery.browserDescription")}
                checked={browserDelivery}
                onChange={setBrowserDelivery}
              />
            </label>
          </div>
        </section>

        <div className="flex justify-end">
          <button
            type="button"
            className="inline-flex shrink-0 items-center justify-center rounded-full bg-(--accent) px-5 py-3 text-sm font-semibold text-(--accent-foreground) ring-1 ring-inset ring-(--accent) transition hover:bg-(--accent-hover)"
          >
            {t("actions.save")}
          </button>
        </div>
      </div>
    </div>
  );
}
