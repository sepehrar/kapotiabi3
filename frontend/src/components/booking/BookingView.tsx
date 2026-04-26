"use client";

import Link from "next/link";
import { useLocale } from "next-intl";
import BookingForm, { type BookingSlot } from "./BookingForm";
import { getPublicApiBaseUrl } from "@/lib/publicApiBaseUrl";
import {
  buildDemoService,
  findCompareService,
  saveDemoBooking,
  type DemoCompareService,
} from "@/lib/demoFlowSession";

type BookingLabels = {
  title: string;
  description: string;
  actions: {
    backToCompare: string;
  };
  sections: {
    request: string;
    shopMessage: string;
  };
  fields: {
    service: string;
    car: string;
    parts: string;
    price: string;
  };
  form: {
    selectDate: string;
    availableTimes: string;
    messageToShop: string;
    optional: string;
    messagePlaceholder: string;
    selectDateAndTime: string;
    confirmBooking: string;
    reviewTitle: string;
    reviewDescription: string;
    cancel: string;
    confirmFinal: string;
    bookingConfirmed: string;
    redirecting: string;
    bookingFailed: string;
  };
};

type BookingViewProps = {
  bookingId: string;
  labels: BookingLabels;
};

type BackendBookingResponse = {
  id: number;
  scheduledAt: string;
  status: string;
};

function getFallbackService(bookingId: string): DemoCompareService {
  return buildDemoService({
    id: bookingId,
    name: "AutoExpert Tallinn",
    price: "€95",
    distance: "3.2 km",
    rating: "4.7",
    reviews: 214,
    updatedAt: "2 days ago",
  });
}

function formatDateForBooking(date: string) {
  return new Date(`${date}T00:00:00`).toLocaleDateString("en-GB", {
    weekday: "long",
    day: "numeric",
    month: "long",
    year: "numeric",
  });
}

export default function BookingView({ bookingId, labels }: BookingViewProps) {
  const locale = useLocale();
  const service = findCompareService(bookingId) ?? getFallbackService(bookingId);

  async function handleConfirm({
    slot,
    messageToShop,
  }: {
    slot: BookingSlot;
    messageToShop: string;
  }) {
    const token = sessionStorage.getItem("authToken");

    if (service.offerId && slot.slotId && token) {
      const response = await fetch(
        `${getPublicApiBaseUrl()}/api/v1/offers/${service.offerId}/accept`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify({ slotId: slot.slotId }),
        },
      );

      if (!response.ok) {
        throw new Error("Backend booking failed.");
      }

      const booking = (await response.json()) as BackendBookingResponse;
      saveDemoBooking({
        id: `backend-${booking.id}`,
        source: "backend",
        serviceId: service.id,
        shop: service.name,
        service: service.service,
        car: service.car,
        date: formatDateForBooking(slot.date),
        time: slot.time,
        price: service.price,
        status: "upcoming",
        messageToShop,
        createdAt: new Date().toISOString(),
        backendBookingId: booking.id,
      });
      return;
    }

    saveDemoBooking({
      id: `session-${Date.now()}`,
      source: "session",
      serviceId: service.id,
      shop: service.name,
      service: service.service,
      car: service.car,
      date: formatDateForBooking(slot.date),
      time: slot.time,
      price: service.price,
      status: "upcoming",
      messageToShop,
      createdAt: new Date().toISOString(),
    });
  }

  return (
    <div className="mx-auto w-full max-w-7xl">
      <div className="pt-4">
        <Link
          href={`/${locale}/account/compare`}
          className="text-sm text-white/40 transition hover:text-white"
        >
          ← {labels.actions.backToCompare}
        </Link>
      </div>

      <div className="pt-6">
        <h1 className="text-2xl font-light tracking-tight text-white text-balance sm:text-3xl">
          {labels.title}
        </h1>
        <p className="mt-6 max-w-xl text-lg leading-8 text-white/50 text-balance">
          {labels.description} {service.name}.
        </p>
      </div>

      <div className="mt-12 flex flex-col gap-10 xl:flex-row xl:justify-between">
        <div className="w-full xl:w-[30%]">
          <div className="space-y-8 min-w-0">
            <section>
              <h2 className="text-xl font-semibold text-white">
                {service.name}
              </h2>

              <div className="mt-4 space-y-2 text-base text-white/50">
                <p>{service.address}</p>
                <p>{service.phone}</p>
              </div>
            </section>

            <section>
              <h2 className="mb-3 border-t border-white/10 pt-6 text-base text-white/50">
                {labels.sections.shopMessage}:
              </h2>

              <div className="mt-6 rounded-2xl border border-white/10 bg-(--pills-background) p-6">
                <p className="text-base leading-relaxed text-white/70">
                  {service.message}
                </p>
              </div>
            </section>

            <section>
              <h2 className="mb-3 border-t border-white/10 pt-6 text-base text-white/50">
                {labels.sections.request}
              </h2>

              <div className="mt-6 rounded-2xl border border-white/10 bg-(--pills-background) p-6">
                <div className="space-y-3">
                  <div className="flex items-center justify-between gap-4">
                    <span className="text-sm text-white/40">
                      {labels.fields.service}
                    </span>
                    <span className="text-right text-sm font-semibold text-white">
                      {service.service}
                    </span>
                  </div>

                  <div className="flex items-center justify-between gap-4">
                    <span className="text-sm text-white/40">
                      {labels.fields.car}
                    </span>
                    <span className="text-right text-sm font-semibold text-white">
                      {service.car}
                    </span>
                  </div>

                  <div className="flex items-center justify-between gap-4">
                    <span className="text-sm text-white/40">
                      {labels.fields.parts}
                    </span>
                    <span className="text-right text-sm font-semibold text-white">
                      {service.partsQuality}
                    </span>
                  </div>

                  <div className="mt-4 flex items-center justify-between gap-4 border-t border-white/10 pt-3">
                    <span className="text-sm text-white/40">
                      {labels.fields.price}
                    </span>
                    <span className="text-right text-base font-semibold text-(--accent)">
                      {service.price}
                    </span>
                  </div>
                </div>
              </div>
            </section>
          </div>
        </div>

        <div className="w-full min-w-0 xl:w-[66%]">
          <BookingForm
            slots={service.slots}
            redirectTo={`/${locale}/account/bookings`}
            labels={labels.form}
            onConfirm={handleConfirm}
          />
        </div>
      </div>
    </div>
  );
}
