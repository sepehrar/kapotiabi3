import { useTranslations } from "next-intl";

export default function AccountNotificationsView() {
  const t = useTranslations("accountNotifications");

  return (
    <div className="mx-auto w-full max-w-5xl">
      <div className="mb-8">
        <h1 className="text-3xl font-bold tracking-tight text-white">
          {t("title")}
        </h1>
        <p className="mt-3 max-w-2xl text-sm leading-6 text-white/60 sm:text-base">
          {t("description")}
        </p>
      </div>

      <div className="space-y-6">
        <section className="rounded-3xl border border-black/10 bg-white p-6 shadow-sm sm:p-8">
          <div className="space-y-6">
            <div className="flex items-start justify-between gap-6">
              <div>
                <h2 className="text-base font-semibold text-black">
                  {t("items.priceUpdates.title")}
                </h2>
                <p className="mt-1 text-sm leading-6 text-black/60">
                  {t("items.priceUpdates.description")}
                </p>
              </div>
              <input type="checkbox" defaultChecked className="mt-1 size-5" />
            </div>

            <div className="flex items-start justify-between gap-6">
              <div>
                <h2 className="text-base font-semibold text-black">
                  {t("items.savedServices.title")}
                </h2>
                <p className="mt-1 text-sm leading-6 text-black/60">
                  {t("items.savedServices.description")}
                </p>
              </div>
              <input type="checkbox" defaultChecked className="mt-1 size-5" />
            </div>

            <div className="flex items-start justify-between gap-6">
              <div>
                <h2 className="text-base font-semibold text-black">
                  {t("items.searchAlerts.title")}
                </h2>
                <p className="mt-1 text-sm leading-6 text-black/60">
                  {t("items.searchAlerts.description")}
                </p>
              </div>
              <input type="checkbox" className="mt-1 size-5" />
            </div>

            <div className="flex items-start justify-between gap-6">
              <div>
                <h2 className="text-base font-semibold text-black">
                  {t("items.accountUpdates.title")}
                </h2>
                <p className="mt-1 text-sm leading-6 text-black/60">
                  {t("items.accountUpdates.description")}
                </p>
              </div>
              <input type="checkbox" defaultChecked className="mt-1 size-5" />
            </div>
          </div>
        </section>

        <section className="rounded-3xl border border-black/10 bg-white p-6 shadow-sm sm:p-8">
          <div className="mb-4">
            <h2 className="text-xl font-semibold text-black">
              {t("delivery.title")}
            </h2>
            <p className="mt-2 text-sm leading-6 text-black/60">
              {t("delivery.description")}
            </p>
          </div>

          <div className="space-y-4">
            <label className="flex items-center justify-between gap-4 rounded-2xl border border-black/10 px-4 py-4">
              <span>
                <span className="block text-sm font-medium text-black">
                  {t("delivery.email")}
                </span>
                <span className="mt-1 block text-sm text-black/60">
                  tom@example.com
                </span>
              </span>
              <input type="checkbox" defaultChecked className="size-5" />
            </label>

            <label className="flex items-center justify-between gap-4 rounded-2xl border border-black/10 px-4 py-4">
              <span>
                <span className="block text-sm font-medium text-black">
                  {t("delivery.browser")}
                </span>
                <span className="mt-1 block text-sm text-black/60">
                  {t("delivery.browserDescription")}
                </span>
              </span>
              <input type="checkbox" className="size-5" />
            </label>
          </div>
        </section>

        <div className="flex justify-end">
          <button
            type="button"
            className="inline-flex items-center justify-center rounded-full bg-(--accent) px-6 py-3 text-sm font-semibold text-(--accent-foreground) transition hover:bg-(--accent-hover)"
          >
            {t("actions.save")}
          </button>
        </div>
      </div>
    </div>
  );
}
