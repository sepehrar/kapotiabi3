import { useTranslations } from "next-intl";

const comparedServices = [
  {
    id: 1,
    name: "AutoExpert Tallinn",
    price: "€95",
    distance: "3.2 km",
    rating: "4.7",
    reviews: 214,
    updatedAt: "Updated 2 days ago",
    partsQuality: "OEM-equivalent",
  },
  {
    id: 2,
    name: "Nord Service",
    price: "From €80",
    distance: "5.6 km",
    rating: "4.5",
    reviews: 98,
    updatedAt: "Updated today",
    partsQuality: "Budget",
  },
  {
    id: 3,
    name: "FixCar Center",
    price: "€110",
    distance: "6.8 km",
    rating: "4.8",
    reviews: 156,
    updatedAt: "Updated 5 days ago",
    partsQuality: "OEM",
  },
];

export default function AccountCompareView() {
  const t = useTranslations("accountCompare");

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

      <section className="rounded-3xl border border-black/10 bg-white p-6 shadow-sm sm:p-8">
        <div className="overflow-x-auto">
          <div className="w-full">
            <div className="grid grid-cols-4 gap-4">
              <div className="rounded-2xl bg-black/3 p-4">
                <p className="text-sm font-semibold text-black">
                  {t("rows.service")}
                </p>
              </div>

              {comparedServices.map((service) => (
                <div
                  key={service.id}
                  className="rounded-2xl border border-black/10 p-4"
                >
                  <h2 className="text-base font-semibold text-black">
                    {service.name}
                  </h2>
                </div>
              ))}

              <div className="rounded-2xl bg-black/3 p-4 text-sm font-medium text-black/70">
                {t("rows.price")}
              </div>
              {comparedServices.map((service) => (
                <div
                  key={`${service.id}-price`}
                  className="rounded-2xl border border-black/10 p-4 text-sm font-semibold text-black"
                >
                  {service.price}
                </div>
              ))}

              <div className="rounded-2xl bg-black/3 p-4 text-sm font-medium text-black/70">
                {t("rows.distance")}
              </div>
              {comparedServices.map((service) => (
                <div
                  key={`${service.id}-distance`}
                  className="rounded-2xl border border-black/10 p-4 text-sm font-semibold text-black"
                >
                  {service.distance}
                </div>
              ))}

              <div className="rounded-2xl bg-black/3 p-4 text-sm font-medium text-black/70">
                {t("rows.rating")}
              </div>
              {comparedServices.map((service) => (
                <div
                  key={`${service.id}-rating`}
                  className="rounded-2xl border border-black/10 p-4 text-sm font-semibold text-black"
                >
                  {service.rating} ({service.reviews} {t("rows.reviews")})
                </div>
              ))}

              <div className="rounded-2xl bg-black/3 p-4 text-sm font-medium text-black/70">
                {t("rows.partsQuality")}
              </div>
              {comparedServices.map((service) => (
                <div
                  key={`${service.id}-parts`}
                  className="rounded-2xl border border-black/10 p-4 text-sm font-semibold text-black"
                >
                  {service.partsQuality}
                </div>
              ))}

              <div className="rounded-2xl bg-black/3 p-4 text-sm font-medium text-black/70">
                {t("rows.priceStatus")}
              </div>
              {comparedServices.map((service) => (
                <div
                  key={`${service.id}-updated`}
                  className="rounded-2xl border border-black/10 p-4 text-sm font-semibold text-black"
                >
                  {service.updatedAt}
                </div>
              ))}

              <div className="rounded-2xl bg-black/3 p-4 text-sm font-medium text-black/70">
                {t("rows.actions")}
              </div>
              {comparedServices.map((service) => (
                <div
                  key={`${service.id}-actions`}
                  className="flex flex-col gap-3 rounded-2xl border border-black/10 p-4"
                >
                  <button
                    type="button"
                    className="inline-flex items-center justify-center rounded-full bg-(--accent) px-4 py-2 text-sm font-semibold text-(--accent-foreground) transition hover:bg-(--accent-hover)"
                  >
                    {t("actions.view")}
                  </button>

                  <button
                    type="button"
                    className="inline-flex items-center justify-center rounded-full border border-black/10 px-4 py-2 text-sm font-semibold text-black transition hover:bg-black hover:text-white"
                  >
                    {t("actions.remove")}
                  </button>
                </div>
              ))}
            </div>
          </div>
        </div>
      </section>
    </div>
  );
}
