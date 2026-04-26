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
    updatedAt: "2 days ago",
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
    updatedAt: "Today",
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
    updatedAt: "5 days ago",
  },
];

export default function AccountSavedView() {
  const t = useTranslations("accountSaved");

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

      <div className="mt-12 space-y-4">
        {savedServices.map((item) => (
          <section
            key={item.id}
            className="w-full rounded-2xl border border-white/10 bg-(--pills-background) p-6 flex flex-row gap-3 items-start justify-between"
          >
            <div className="flex gap-5 flex-col sm:flex-row items-start justify-between w-full">
              <div>
                <div className="flex flex-wrap items-center gap-3">
                  <h2 className="text-xl font-semibold text-white">
                    {item.name}
                  </h2>
                  <span className="rounded-full bg-white/10 px-3 py-1 text-xs font-medium text-white/60">
                    {item.service}
                  </span>
                </div>

                <p className="mt-2 text-lg leading-6 text-white/60">
                  {item.car}
                </p>

                <div className="mt-6 flex flex-wrap items-center gap-3 text-base text-white/50">
                  <span>{item.price}</span>
                  <span className="text-white/20">•</span>
                  <span>{item.distance}</span>
                  <span className="text-white/20">•</span>
                  <span>
                    {item.rating} ({item.reviews} {t("meta.reviews")})
                  </span>
                  <span className="text-white/20">•</span>
                  <span>
                    {t("meta.updated")} {item.updatedAt}
                  </span>
                </div>
              </div>

              <div className="flex flex-row sm:flex-col gap-2 shrink-0">
                <Link
                  href={`/booking/${item.id}`}
                  className="inline-flex items-center justify-center rounded-full bg-(--accent) px-5 py-3 text-sm font-semibold text-(--accent-foreground) ring-1 ring-inset ring-(--accent) transition hover:bg-(--accent-hover)"
                >
                  {t("actions.view")}
                </Link>

                {/* <button
                  type="button"
                  className="shrink-0 rounded-full bg-(--pills-background) px-5 py-3 text-sm shadow ring-1 ring-inset ring-white/10 transition-colors duration-300 hover:bg-(--accent)/40 hover:ring-(--accent) hover:text-white text-white/60"
                >
                  {t("actions.compare")}
                </button> */}

                <button
                  type="button"
                  className="shrink-0 rounded-full bg-(--pills-background) px-5 py-3 text-sm shadow ring-1 ring-inset ring-white/10 transition-colors duration-300 hover:bg-(--accent)/40 hover:ring-(--accent) hover:text-white text-white/60"
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
