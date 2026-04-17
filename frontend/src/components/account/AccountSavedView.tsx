import Link from "next/link";
import { useTranslations } from "next-intl";

const savedServices = [
  {
    id: 1,
    name: "AutoExpert Tallinn",
    service: "Oil change",
    car: "BMW 320d 2018",
    price: "€95",
    distance: "3.2 km",
    rating: "4.7",
    reviews: 214,
    updatedAt: "Updated 2 days ago",
  },
  {
    id: 2,
    name: "Nord Service",
    service: "Tire service",
    car: "Audi A4 2019",
    price: "From €80",
    distance: "5.6 km",
    rating: "4.5",
    reviews: 98,
    updatedAt: "Updated today",
  },
  {
    id: 3,
    name: "FixCar Center",
    service: "A/C service",
    car: "Volkswagen Passat 2017",
    price: "€110",
    distance: "6.8 km",
    rating: "4.8",
    reviews: 156,
    updatedAt: "Updated 5 days ago",
  },
];

export default function AccountSavedView() {
  const t = useTranslations("accountSaved");

  return (
    <div className="mx-auto w-full max-w-6xl">
      <div className="mb-8">
        <h1 className="text-2xl font-light tracking-tight text-white text-balance sm:text-3xl">
          {t("title")}
        </h1>
        <p className="mt-3 max-w-2xl text-sm leading-6 text-white/60 sm:text-base">
          {t("description")}
        </p>
      </div>

      <div className="space-y-4">
        {savedServices.map((item) => (
          <section
            key={item.id}
            className="rounded-3xl border border-black/10 bg-white p-6 shadow-sm sm:p-8"
          >
            <div className="flex flex-col gap-6 xl:flex-row xl:items-start xl:justify-between">
              <div className="min-w-0">
                <div className="flex flex-wrap items-center gap-3">
                  <h2 className="text-xl font-semibold text-black">
                    {item.name}
                  </h2>
                  <span className="rounded-full bg-black/5 px-3 py-1 text-xs font-medium text-black/60">
                    {item.service}
                  </span>
                </div>

                <p className="mt-2 text-sm leading-6 text-black/60">
                  {item.car}
                </p>

                <div className="mt-5 grid gap-3 sm:grid-cols-2 xl:grid-cols-4">
                  <div className="rounded-2xl bg-black/3 px-4 py-3">
                    <p className="text-xs font-medium uppercase tracking-wide text-black/40">
                      {t("meta.price")}
                    </p>
                    <p className="mt-1 text-sm font-semibold text-black">
                      {item.price}
                    </p>
                  </div>

                  <div className="rounded-2xl bg-black/3 px-4 py-3">
                    <p className="text-xs font-medium uppercase tracking-wide text-black/40">
                      {t("meta.distance")}
                    </p>
                    <p className="mt-1 text-sm font-semibold text-black">
                      {item.distance}
                    </p>
                  </div>

                  <div className="rounded-2xl bg-black/3 px-4 py-3">
                    <p className="text-xs font-medium uppercase tracking-wide text-black/40">
                      {t("meta.rating")}
                    </p>
                    <p className="mt-1 text-sm font-semibold text-black">
                      {item.rating} ({item.reviews} {t("meta.reviews")})
                    </p>
                  </div>

                  <div className="rounded-2xl bg-black/3 px-4 py-3">
                    <p className="text-xs font-medium uppercase tracking-wide text-black/40">
                      {t("meta.priceStatus")}
                    </p>
                    <p className="mt-1 text-sm font-semibold text-black">
                      {item.updatedAt}
                    </p>
                  </div>
                </div>
              </div>

              <div className="flex flex-wrap gap-3 xl:w-auto xl:flex-col">
                <Link
                  href="/search"
                  className="inline-flex items-center justify-center rounded-full bg-(--accent) px-5 py-2.5 text-sm font-semibold text-(--accent-foreground) transition hover:bg-(--accent-hover)"
                >
                  {t("actions.view")}
                </Link>

                <button
                  type="button"
                  className="inline-flex items-center justify-center rounded-full border border-black/10 px-5 py-2.5 text-sm font-semibold text-black transition hover:bg-black hover:text-white"
                >
                  {t("actions.compare")}
                </button>

                <button
                  type="button"
                  className="inline-flex items-center justify-center rounded-full border border-black/10 px-5 py-2.5 text-sm font-semibold text-black transition hover:bg-black hover:text-white"
                >
                  {t("actions.remove")}
                </button>
              </div>
            </div>
          </section>
        ))}
      </div>
    </div>
  );
}
