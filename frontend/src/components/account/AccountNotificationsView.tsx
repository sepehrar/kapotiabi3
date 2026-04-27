"use client";

import { useState } from "react";
import Link from "next/link";
import { useTranslations } from "next-intl";

type RequestStatus = "new" | "accepted" | "booked";

type Reply = {
  id: number;
  shop: string;
  avatar: string;
  time: string;
  price: string;
  message: string;
  booked: boolean;
};

type Request = {
  id: number;
  title: string;
  subtitle: string;
  sentAt: string;
  status: RequestStatus;
  replies: Reply[];
};

const statusStyles: Record<RequestStatus, string> = {
  new: "bg-white/10 text-white/40",
  accepted: "bg-(--accent)/20 text-(--accent)",
  booked: "bg-green-500/20 text-green-400",
};

const requests: Request[] = [
  {
    id: 1,
    title: "Oil change in Tallinn",
    subtitle: "BMW 320d 2018 · OEM-equivalent · 10 km",
    sentAt: "Today, 14:32",
    status: "booked",
    replies: [
      {
        id: 1,
        shop: "AutoExpert Tallinn",
        avatar: "AE",
        time: "14:45",
        price: "€95",
        message:
          "We can take your car tomorrow at 10:00 or 14:00. OEM-equivalent parts, work takes about 1.5 hours.",
        booked: true,
      },
      {
        id: 2,
        shop: "Nord Service",
        avatar: "NS",
        time: "15:10",
        price: "From €80",
        message:
          "Available this week. We use budget parts but quality is guaranteed.",
        booked: false,
      },
    ],
  },
  {
    id: 2,
    title: "Tire service in Tartu",
    subtitle: "Audi A4 2019 · Budget · 15 km",
    sentAt: "Yesterday, 10:15",
    status: "accepted",
    replies: [
      {
        id: 3,
        shop: "FixCar Center",
        avatar: "FC",
        time: "11:02",
        price: "€110",
        message:
          "We can do it Friday or Saturday morning. Please confirm which time suits you.",
        booked: false,
      },
    ],
  },
  {
    id: 3,
    title: "A/C service in Pärnu",
    subtitle: "Volkswagen Passat 2017 · OEM · 20 km",
    sentAt: "3 days ago",
    status: "new",
    replies: [],
  },
];

export default function AccountNotificationsView() {
  const t = useTranslations("accountNotifications");
  const [openId, setOpenId] = useState<number | null>(1);

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

      <div className="mt-12 space-y-4">
        {requests.map((req) => {
          const isOpen = openId === req.id;
          const hasBooked = req.replies.some((reply) => reply.booked);

          return (
            <section
              key={req.id}
              className="overflow-hidden rounded-2xl border border-white/10 bg-(--pills-background)"
            >
              <div className="flex flex-col gap-4 p-6 sm:flex-row sm:items-center sm:justify-between">
                <button
                  type="button"
                  onClick={() => setOpenId(isOpen ? null : req.id)}
                  className="flex min-w-0 flex-1 items-start gap-4 text-left"
                >
                  <svg
                    className={`mt-1 size-4 shrink-0 text-white/30 transition-transform duration-200 ${
                      isOpen ? "rotate-180" : ""
                    }`}
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                    strokeWidth={1.5}
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      d="m19.5 8.25-7.5 7.5-7.5-7.5"
                    />
                  </svg>

                  <div>
                    <h2 className="text-xl font-semibold text-white">
                      {req.title}
                    </h2>

                    <p className="mt-1 text-base text-white/50">
                      {req.subtitle}
                    </p>

                    <p className="mt-1 text-sm text-white/30">{req.sentAt}</p>
                  </div>
                </button>

                <div className="flex shrink-0 flex-row items-center gap-3 sm:flex-col sm:items-end">
                  <span
                    className={`rounded-full px-3 py-1 text-xs font-medium ${
                      statusStyles[req.status]
                    }`}
                  >
                    {t(`statuses.${req.status}`)}
                  </span>

                  {req.replies.length > 0 && (
                    <span className="text-sm text-white/30">
                      {req.replies.length}{" "}
                      {t(
                        req.replies.length === 1
                          ? "replies.one"
                          : "replies.other",
                      )}
                    </span>
                  )}

                  {req.status === "accepted" && (
                    <button
                      type="button"
                      onClick={() => setOpenId(req.id)}
                      className="inline-flex items-center justify-center rounded-full bg-(--accent) px-5 py-2 text-sm font-semibold text-(--accent-foreground) ring-1 ring-inset ring-(--accent) transition hover:bg-(--accent-hover)"
                    >
                      {t("actions.bookTime")}
                    </button>
                  )}

                  {req.status === "booked" && (
                    <span className="text-sm font-medium text-green-400">
                      ✓ {t("actions.appointmentConfirmed")}
                    </span>
                  )}
                </div>
              </div>

              {isOpen && (
                <div className="space-y-3 border-t border-white/10 p-6">
                  {req.replies.length === 0 ? (
                    <p className="py-6 text-center text-base text-white/30">
                      {t("replies.empty")}
                    </p>
                  ) : (
                    req.replies.map((reply) => (
                      <div
                        key={reply.id}
                        className={`flex items-start gap-4 rounded-2xl p-4 transition-all ${
                          reply.booked
                            ? "bg-green-500/5 ring-1 ring-inset ring-green-500/30"
                            : "bg-white/2 opacity-50 ring-1 ring-inset ring-white/5"
                        }`}
                      >
                        <div
                          className={`flex size-10 shrink-0 items-center justify-center rounded-full border text-xs font-bold ${
                            reply.booked
                              ? "border-green-500/20 bg-green-500/10 text-green-400"
                              : "border-white/10 bg-gray-800 text-white/30"
                          }`}
                        >
                          {reply.avatar}
                        </div>

                        <div className="flex min-w-0 flex-1 flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
                          <div className="min-w-0 flex-1">
                            <div className="mb-2 flex items-center gap-2">
                              <span
                                className={`text-sm font-semibold ${
                                  reply.booked ? "text-white" : "text-white/50"
                                }`}
                              >
                                {reply.shop}
                              </span>

                              <span className="text-xs text-white/30">
                                {reply.time}
                              </span>

                              {reply.booked && (
                                <span className="rounded-full bg-green-500/20 px-2.5 py-0.5 text-xs font-medium text-green-400">
                                  {t("statuses.booked")}
                                </span>
                              )}
                            </div>

                            <p
                              className={`text-sm leading-relaxed ${
                                reply.booked ? "text-white/70" : "text-white/40"
                              }`}
                            >
                              {reply.message}
                            </p>
                          </div>

                          <div className="flex shrink-0 flex-row items-center gap-2 sm:flex-col sm:items-end">
                            <p
                              className={`text-base font-semibold ${
                                reply.booked
                                  ? "text-green-400"
                                  : "text-(--accent)"
                              }`}
                            >
                              {reply.price}
                            </p>

                            {!reply.booked && (
                              <>
                                {hasBooked ? (
                                  <button
                                    type="button"
                                    disabled
                                    className="inline-flex cursor-not-allowed items-center justify-center rounded-full px-5 py-2 text-sm font-semibold text-white/20 ring-1 ring-inset ring-white/10"
                                  >
                                    {t("actions.bookTime")}
                                  </button>
                                ) : (
                                  <Link
                                    href={`/booking/${req.id}`}
                                    className="inline-flex items-center justify-center rounded-full bg-(--accent) px-5 py-2 text-sm font-semibold text-(--accent-foreground) ring-1 ring-inset ring-(--accent) transition hover:bg-(--accent-hover)"
                                  >
                                    {t("actions.bookTime")}
                                  </Link>
                                )}

                                <button
                                  type="button"
                                  disabled={hasBooked}
                                  className="shrink-0 rounded-full bg-(--pills-background) px-5 py-2 text-sm text-white/60 shadow ring-1 ring-inset ring-white/10 transition-colors duration-300 hover:bg-(--accent)/40 hover:text-white hover:ring-(--accent) disabled:cursor-not-allowed disabled:opacity-30"
                                >
                                  {t("actions.decline")}
                                </button>
                              </>
                            )}
                          </div>
                        </div>
                      </div>
                    ))
                  )}
                </div>
              )}
            </section>
          );
        })}
      </div>
    </div>
  );
}
