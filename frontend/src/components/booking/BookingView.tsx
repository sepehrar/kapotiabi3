import Link from "next/link";
import BookingForm from "./BookingForm";

type BookingViewProps = {
  bookingId: string;
  t: (key: string) => string;
};

// В реальности данные придут из БД по bookingId / replyId
const mockReply = {
  id: 1,
  shop: {
    name: "AutoExpert Tallinn",
    address: "Pärnu mnt 12, Tallinn",
    phone: "+372 5123 4567",
  },
  request: {
    service: "Oil change",
    car: "BMW 320d 2018",
    partsQuality: "OEM-equivalent",
  },
  price: "€95",
  message:
    "We can take your car tomorrow at 10:00 or 14:00. OEM-equivalent parts, work takes about 1.5 hours. Please bring the car freshly washed.",
  slots: [
    { id: "s1", date: "2026-05-06", time: "10:00" },
    { id: "s2", date: "2026-05-06", time: "14:00" },
    { id: "s3", date: "2026-05-07", time: "09:00" },
    { id: "s4", date: "2026-05-07", time: "11:00" },
    { id: "s5", date: "2026-05-08", time: "13:00" },
    { id: "s6", date: "2026-05-08", time: "15:00" },
  ],
};

export default function BookingView({ bookingId, t }: BookingViewProps) {
  const reply = {
    ...mockReply,
    id: Number(bookingId) || mockReply.id,
  };

  return (
    <div className="mx-auto w-full max-w-7xl">
      {/* Back link */}
      <div className="pt-4">
        <Link
          href="/account/notifications"
          className="text-sm text-white/40 transition hover:text-white"
        >
          ← {t("actions.backToNotifications")}
        </Link>
      </div>

      {/* Title */}
      <div className="pt-6">
        <h1 className="text-2xl font-light tracking-tight text-white text-balance sm:text-3xl">
          {t("title")}
        </h1>
        <p className="mt-6 max-w-xl text-lg leading-8 text-white/50 text-balance">
        {t("description")} {reply.shop.name}.
        </p>
      </div>

      <div className="mt-12 flex flex-col gap-10 xl:flex-row xl:justify-between">
        {/* Left block */}
        <div className="w-full xl:w-[30%]">
          <div className="space-y-8 min-w-0">
            {/* Shop info */}
            <section>
              <h2 className="text-xl font-semibold text-white">
                {reply.shop.name}
              </h2>

              <div className="mt-4 space-y-2 text-base text-white/50">
                <p>{reply.shop.address}</p>
                <p>{reply.shop.phone}</p>
              </div>
            </section>

            {/* Message from shop */}
            <section>
              <h2 className="mb-3 border-t border-white/10 pt-6 text-base text-white/50">
                {t("sections.shopMessage")}:
              </h2>

              <div className="mt-6 rounded-2xl border border-white/10 bg-(--pills-background) p-6">
                <p className="text-base leading-relaxed text-white/70">
                  {reply.message}
                </p>
              </div>
            </section>

            {/* Request details */}
            <section>
              <h2 className="mb-3 border-t border-white/10 pt-6 text-base text-white/50">
                {t("sections.request")}
              </h2>

              <div className="mt-6 rounded-2xl border border-white/10 bg-(--pills-background) p-6">
                <div className="space-y-3">
                  <div className="flex items-center justify-between gap-4">
                    <span className="text-sm text-white/40">
                      {t("fields.service")}
                    </span>
                    <span className="text-right text-sm font-semibold text-white">
                      {reply.request.service}
                    </span>
                  </div>

                  <div className="flex items-center justify-between gap-4">
                    <span className="text-sm text-white/40">
                      {t("fields.car")}
                    </span>
                    <span className="text-right text-sm font-semibold text-white">
                      {reply.request.car}
                    </span>
                  </div>

                  <div className="flex items-center justify-between gap-4">
                    <span className="text-sm text-white/40">
                      {t("fields.parts")}
                    </span>
                    <span className="text-right text-sm font-semibold text-white">
                      {reply.request.partsQuality}
                    </span>
                  </div>

                  <div className="mt-4 flex items-center justify-between gap-4 border-t border-white/10 pt-3">
                    <span className="text-sm text-white/40">
                      {t("fields.price")}
                    </span>
                    <span className="text-right text-base font-semibold text-(--accent)">
                      {reply.price}
                    </span>
                  </div>
                </div>
              </div>
            </section>
          </div>
        </div>

        {/* Right block */}
        <div className="w-full min-w-0 xl:w-[66%]">
          <BookingForm
            slots={reply.slots}
            redirectTo="/account/notifications"
            labels={{
              selectDate: t("form.selectDate"),
              availableTimes: t("form.availableTimes"),
              messageToShop: t("form.messageToShop"),
              optional: t("form.optional"),
              messagePlaceholder: t("form.messagePlaceholder"),
              selectDateAndTime: t("form.selectDateAndTime"),
              confirmBooking: t("form.confirmBooking"),
              bookingConfirmed: t("form.bookingConfirmed"),
              redirecting: t("form.redirecting"),
            }}
          />
        </div>
      </div>
    </div>
  );
}
