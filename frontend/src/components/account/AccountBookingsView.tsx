"use client";

import Link from "next/link";
import { useEffect, useState } from "react";
import { useLocale, useTranslations } from "next-intl";
import { getPublicApiBaseUrl } from "@/lib/publicApiBaseUrl";
import {
  getDemoBookings,
  removeDemoBooking,
  type DemoBooking,
} from "@/lib/demoFlowSession";

type BookingStatus = "upcoming" | "completed" | "cancelled";

type UiBooking = {
  id: string;
  source: "session" | "backend";
  shop: string;
  service: string;
  car: string;
  date: string;
  time: string;
  price: string;
  status: BookingStatus;
  backendBookingId?: number;
  serviceId?: string;
};

type BackendBooking = {
  id: number;
  offerId: number;
  jobRequestId: number;
  shopId: number;
  scheduledAt: string | null;
  status: "AWAITING_SHOP" | "CONFIRMED" | "COMPLETED" | "CANCELLED" | string;
};

type BackendPage = {
  content?: BackendBooking[];
};

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

function mapBackendStatus(status: BackendBooking["status"]): BookingStatus {
  if (status === "COMPLETED") {
    return "completed";
  }

  if (status === "CANCELLED") {
    return "cancelled";
  }

  return "upcoming";
}

function splitDateTime(value: string | null) {
  if (!value) {
    return {
      date: "Time pending",
      time: "",
    };
  }

  const date = new Date(value);
  return {
    date: date.toLocaleDateString("en-GB", {
      weekday: "long",
      day: "numeric",
      month: "long",
      year: "numeric",
    }),
    time: date.toLocaleTimeString("en-GB", {
      hour: "2-digit",
      minute: "2-digit",
    }),
  };
}

function mapSessionBooking(booking: DemoBooking): UiBooking {
  return {
    id: booking.id,
    source: booking.source,
    shop: booking.shop,
    service: booking.service,
    car: booking.car,
    date: booking.date,
    time: booking.time,
    price: booking.price,
    status: booking.status,
    backendBookingId: booking.backendBookingId,
    serviceId: booking.serviceId,
  };
}

function mapBackendBooking(booking: BackendBooking): UiBooking {
  const { date, time } = splitDateTime(booking.scheduledAt);

  return {
    id: `backend-${booking.id}`,
    source: "backend",
    shop: `Shop #${booking.shopId}`,
    service: `Offer #${booking.offerId}`,
    car: `Request #${booking.jobRequestId}`,
    date,
    time,
    price: "Confirmed offer",
    status: mapBackendStatus(booking.status),
    backendBookingId: booking.id,
  };
}

export default function AccountBookingsView() {
  const t = useTranslations("accountBookings");
  const locale = useLocale();
  const [backendBookings, setBackendBookings] = useState<UiBooking[]>([]);
  const [sessionBookings, setSessionBookings] = useState<UiBooking[]>(() =>
    getDemoBookings().map(mapSessionBooking),
  );

  const bookings = [...sessionBookings, ...backendBookings].filter(
    (booking, index, allBookings) =>
      allBookings.findIndex((item) => item.id === booking.id) === index,
  );
  const upcoming = bookings.filter((b) => b.status === "upcoming");
  const past = bookings.filter((b) => b.status !== "upcoming");

  useEffect(() => {
    const token = sessionStorage.getItem("authToken");
    if (!token) {
      return;
    }

    let cancelled = false;

    async function loadBackendBookings() {
      try {
        const response = await fetch(`${getPublicApiBaseUrl()}/api/v1/bookings`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (!response.ok) {
          return;
        }

        const data = (await response.json()) as BackendPage | BackendBooking[];
        const content = Array.isArray(data) ? data : data.content ?? [];

        if (!cancelled) {
          setBackendBookings(content.map(mapBackendBooking));
        }
      } catch {
        if (!cancelled) {
          setBackendBookings([]);
        }
      }
    }

    void loadBackendBookings();

    return () => {
      cancelled = true;
    };
  }, []);

  async function cancelBooking(booking: UiBooking) {
    if (booking.source === "session" || !booking.backendBookingId) {
      removeDemoBooking(booking.id);
      setSessionBookings((current) =>
        current.filter((item) => item.id !== booking.id),
      );
      return;
    }

    const token = sessionStorage.getItem("authToken");
    if (!token) {
      return;
    }

    const response = await fetch(
      `${getPublicApiBaseUrl()}/api/v1/bookings/${booking.backendBookingId}/cancel`,
      {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
        },
      },
    );

    if (response.ok) {
      setBackendBookings((current) =>
        current.map((item) =>
          item.id === booking.id ? { ...item, status: "cancelled" } : item,
        ),
      );
    }
  }

  function renderBooking(booking: UiBooking, faded = false) {
    return (
      <section
        key={booking.id}
        className={`w-full rounded-2xl border border-white/10 bg-(--pills-background) p-6 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-5 ${
          faded ? "opacity-70" : ""
        }`}
      >
        <div>
          <div className="flex flex-wrap items-center gap-3">
            <h3 className="text-xl font-semibold text-white">{booking.shop}</h3>
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
              {booking.date}
              {booking.time ? ` · ${booking.time}` : ""}
            </span>
            <span className="text-white/20">•</span>
            <span className={faded ? "" : "font-semibold text-(--accent)"}>
              {booking.price}
            </span>
          </div>
        </div>

        {booking.status === "upcoming" ? (
          <div className="flex flex-row sm:flex-col gap-2 shrink-0">
            <Link
              href={`/${locale}/booking/${booking.serviceId ?? booking.id}`}
              className="inline-flex items-center justify-center rounded-full bg-(--accent) px-5 py-3 text-sm font-semibold text-(--accent-foreground) ring-1 ring-inset ring-(--accent) transition hover:bg-(--accent-hover)"
            >
              {t("actions.reschedule")}
            </Link>
            <button
              type="button"
              onClick={() => void cancelBooking(booking)}
              className="shrink-0 rounded-full bg-(--pills-background) px-5 py-3 text-sm text-white/60 shadow ring-1 ring-inset ring-white/10 transition-colors duration-300 hover:bg-red-500/20 hover:ring-red-500/40 hover:text-red-400"
            >
              {t("actions.cancel")}
            </button>
          </div>
        ) : (
          <div className="shrink-0">
            <Link
              href={`/${locale}/search`}
              className="shrink-0 rounded-full bg-(--pills-background) px-5 py-3 text-sm text-white/60 shadow ring-1 ring-inset ring-white/10 transition-colors duration-300 hover:bg-(--accent)/40 hover:ring-(--accent) hover:text-white"
            >
              {t("actions.bookAgain")}
            </Link>
          </div>
        )}
      </section>
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

      {!bookings.length ? (
        <div className="mt-12 rounded-2xl border border-white/10 bg-(--pills-background) p-8">
          <h2 className="text-xl font-semibold text-white">
            {t("empty.title")}
          </h2>
          <p className="mt-3 text-base text-white/50">
            {t("empty.description")}
          </p>
          <Link
            href={`/${locale}/search`}
            className="mt-6 inline-flex rounded-full bg-(--accent) px-5 py-3 text-sm font-semibold text-(--accent-foreground) transition hover:bg-(--accent-hover)"
          >
            {t("empty.action")}
          </Link>
        </div>
      ) : null}

      {upcoming.length > 0 && (
        <div className="mt-12">
          <h2 className="text-xl font-semibold text-white mb-4">
            {t("sections.upcoming")}
          </h2>
          <div className="space-y-4">{upcoming.map((b) => renderBooking(b))}</div>
        </div>
      )}

      {past.length > 0 && (
        <div className="mt-10">
          <h2 className="text-xl font-semibold text-white mb-4">
            {t("sections.past")}
          </h2>
          <div className="space-y-4">
            {past.map((b) => renderBooking(b, true))}
          </div>
        </div>
      )}
    </div>
  );
}
