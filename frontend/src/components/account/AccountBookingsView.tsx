import Link from "next/link";
import { useTranslations } from "next-intl";

type BookingStatus = "upcoming" | "completed" | "cancelled";

const bookings = [
  {
    id: 1,
    shop: "AutoExpert Tallinn",
    service: "Oil change",
    car: "BMW 320d 2018",
    date: "Wednesday, 6 May 2026",
    time: "10:00",
    price: "€95",
    status: "upcoming" as BookingStatus,
  },
  {
    id: 2,
    shop: "FixCar Center",
    service: "Tire service",
    car: "Audi A4 2019",
    date: "Friday, 8 May 2026",
    time: "13:00",
    price: "€110",
    status: "upcoming" as BookingStatus,
  },
  {
    id: 3,
    shop: "Nord Service",
    service: "A/C service",
    car: "Volkswagen Passat 2017",
    date: "Monday, 14 April 2026",
    time: "11:00",
    price: "From €80",
    status: "completed" as BookingStatus,
  },
  {
    id: 4,
    shop: "AutoExpert Tallinn",
    service: "Brake inspection",
    car: "BMW 320d 2018",
    date: "Thursday, 3 April 2026",
    time: "09:00",
    price: "€60",
    status: "cancelled" as BookingStatus,
  },
];

const statusStyles: Record<BookingStatus, string> = {
  upcoming: "bg-(--accent)/20 text-(--accent)",
  completed: "bg-green-500/20 text-green-400",
  cancelled: "bg-white/10 text-white/40",
};

const statusLabels: Record<BookingStatus, string> = {
  upcoming: "Upcoming",
  completed: "Completed",
  cancelled: "Cancelled",
};

export default function AccountBookingsView() {
  const t = useTranslations("accountBookings");

  const upcoming = bookings.filter((b) => b.status === "upcoming");
  const past = bookings.filter((b) => b.status !== "upcoming");

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

      {/* Upcoming */}
      {upcoming.length > 0 && (
        <div className="mt-12">
          <h2 className="text-xl font-semibold text-white mb-4">
            {t("sections.upcoming")}
          </h2>
          <div className="space-y-4">
            {upcoming.map((booking) => (
              <section
                key={booking.id}
                className="w-full rounded-2xl border border-white/10 bg-(--pills-background) p-6 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-5"
              >
                <div>
                  <div className="flex flex-wrap items-center gap-3">
                    <h3 className="text-xl font-semibold text-white">
                      {booking.shop}
                    </h3>
                    <span className="rounded-full bg-white/10 px-3 py-1 text-xs font-medium text-white/60">
                      {booking.service}
                    </span>
                    <span
                      className={`rounded-full px-3 py-1 text-xs font-medium ${statusStyles[booking.status]}`}
                    >
                      {statusLabels[booking.status]}
                    </span>
                  </div>

                  <p className="mt-2 text-base text-white/60">{booking.car}</p>

                  <div className="mt-4 flex flex-wrap items-center gap-3 text-base text-white/50">
                    <span>
                      {booking.date} · {booking.time}
                    </span>
                    <span className="text-white/20">•</span>
                    <span className="font-semibold text-(--accent)">
                      {booking.price}
                    </span>
                  </div>
                </div>

                <div className="flex flex-row sm:flex-col gap-2 shrink-0">
                  <Link
                    href={`/booking/${booking.id}`}
                    className="inline-flex items-center justify-center rounded-full bg-(--accent) px-5 py-3 text-sm font-semibold text-(--accent-foreground) ring-1 ring-inset ring-(--accent) transition hover:bg-(--accent-hover)"
                  >
                    {t("actions.reschedule")}
                  </Link>
                  <button
                    type="button"
                    className="shrink-0 rounded-full bg-(--pills-background) px-5 py-3 text-sm text-white/60 shadow ring-1 ring-inset ring-white/10 transition-colors duration-300 hover:bg-red-500/20 hover:ring-red-500/40 hover:text-red-400"
                  >
                    {t("actions.cancel")}
                  </button>
                </div>
              </section>
            ))}
          </div>
        </div>
      )}

      {/* Past */}
      {past.length > 0 && (
        <div className="mt-10">
          <h2 className="text-xl font-semibold text-white mb-4">
            {t("sections.past")}
          </h2>
          <div className="space-y-4">
            {past.map((booking) => (
              <section
                key={booking.id}
                className="w-full rounded-2xl border border-white/10 bg-(--pills-background) p-6 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-5 opacity-70"
              >
                <div>
                  <div className="flex flex-wrap items-center gap-3">
                    <h3 className="text-xl font-semibold text-white">
                      {booking.shop}
                    </h3>
                    <span className="rounded-full bg-white/10 px-3 py-1 text-xs font-medium text-white/60">
                      {booking.service}
                    </span>
                    <span
                      className={`rounded-full px-3 py-1 text-xs font-medium ${statusStyles[booking.status]}`}
                    >
                      {statusLabels[booking.status]}
                    </span>
                  </div>

                  <p className="mt-2 text-base text-white/60">{booking.car}</p>

                  <div className="mt-4 flex flex-wrap items-center gap-3 text-base text-white/50">
                    <span>
                      {booking.date} · {booking.time}
                    </span>
                    <span className="text-white/20">•</span>
                    <span>{booking.price}</span>
                  </div>
                </div>

                <div className="shrink-0">
                  <button
                    type="button"
                    className="shrink-0 rounded-full bg-(--pills-background) px-5 py-3 text-sm text-white/60 shadow ring-1 ring-inset ring-white/10 transition-colors duration-300 hover:bg-(--accent)/40 hover:ring-(--accent) hover:text-white"
                  >
                    {t("actions.bookAgain")}
                  </button>
                </div>
              </section>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
