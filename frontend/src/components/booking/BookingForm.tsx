"use client";

import { useMemo, useState } from "react";
import { useRouter } from "next/navigation";
import { ChevronLeftIcon, ChevronRightIcon } from "@heroicons/react/20/solid";

type Slot = { id: string; date: string; time: string };

type Labels = {
  selectDate: string;
  availableTimes: string;
  messageToShop: string;
  optional: string;
  messagePlaceholder: string;
  selectDateAndTime: string;
  confirmBooking: string;
  bookingConfirmed: string;
  redirecting: string;
};

type Props = { slots: Slot[]; redirectTo: string; labels: Labels };

type CalendarDay = {
  date: string;
  dayNumber: number;
  isCurrentMonth: boolean;
  isToday: boolean;
  hasSlots: boolean;
};

function toDateKey(date: Date) {
  const y = date.getFullYear();
  const m = `${date.getMonth() + 1}`.padStart(2, "0");
  const d = `${date.getDate()}`.padStart(2, "0");
  return `${y}-${m}-${d}`;
}

function parseDateKey(key: string) {
  const [y, m, d] = key.split("-").map(Number);
  return new Date(y, m - 1, d);
}

function formatDate(str: string) {
  return parseDateKey(str).toLocaleDateString("en-GB", {
    weekday: "long",
    day: "numeric",
    month: "long",
  });
}

function formatMonthTitle(date: Date) {
  return date.toLocaleDateString("en-GB", { month: "long", year: "numeric" });
}

function groupByDate(slots: Slot[]): Record<string, Slot[]> {
  return slots.reduce(
    (acc, slot) => {
      if (!acc[slot.date]) acc[slot.date] = [];
      acc[slot.date].push(slot);
      return acc;
    },
    {} as Record<string, Slot[]>,
  );
}

function getCalendarDays(
  monthDate: Date,
  slotDates: Set<string>,
): CalendarDay[] {
  const year = monthDate.getFullYear();
  const month = monthDate.getMonth();
  const firstWeekday = new Date(year, month, 1).getDay();
  const mondayOffset = firstWeekday === 0 ? 6 : firstWeekday - 1;
  const start = new Date(year, month, 1 - mondayOffset);
  const todayKey = toDateKey(new Date());

  return Array.from({ length: 42 }, (_, i): CalendarDay => {
    const d = new Date(start);
    d.setDate(start.getDate() + i);
    const key = toDateKey(d);
    return {
      date: key,
      dayNumber: d.getDate(),
      isCurrentMonth: d.getMonth() === month,
      isToday: key === todayKey,
      hasSlots: slotDates.has(key),
    };
  });
}

function cn(...classes: (string | false | undefined)[]) {
  return classes.filter(Boolean).join(" ");
}

export default function BookingForm({ slots, redirectTo, labels: l }: Props) {
  const router = useRouter();

  const grouped = useMemo(() => groupByDate(slots), [slots]);
  const dates = useMemo(() => Object.keys(grouped).sort(), [grouped]);
  const firstDate = dates[0] ?? toDateKey(new Date());

  const [selectedDate, setSelectedDate] = useState(firstDate);
  const [selectedSlot, setSelectedSlot] = useState<string | null>(null);
  const [visibleMonth, setVisibleMonth] = useState(parseDateKey(firstDate));
  const [message, setMessage] = useState("");
  const [confirmed, setConfirmed] = useState(false);

  const slotDates = useMemo(() => new Set(dates), [dates]);
  const calendarDays = useMemo(
    () => getCalendarDays(visibleMonth, slotDates),
    [visibleMonth, slotDates],
  );
  const selectedSlots = grouped[selectedDate] ?? [];
  const selectedTime = selectedSlots.find((s) => s.id === selectedSlot)?.time;

  if (confirmed) {
    return (
      <div className="flex min-h-64 flex-col items-center justify-center gap-4 rounded-2xl border border-green-500/20 bg-green-500/5 p-10 text-center">
        <div className="flex size-12 items-center justify-center rounded-full bg-green-500/20">
          <svg
            className="size-6 text-green-400"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
            strokeWidth={2}
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              d="m4.5 12.75 6 6 9-13.5"
            />
          </svg>
        </div>
        <h2 className="text-xl font-semibold text-white">
          {l.bookingConfirmed}
        </h2>
        <p className="text-base text-white/50">
          {formatDate(selectedDate)} · {selectedTime}
        </p>
        <p className="text-sm text-white/30">{l.redirecting}</p>
      </div>
    );
  }

  return (
    <div className="space-y-4">
      <section className="rounded-2xl border border-white/10 bg-(--pills-background) p-6 sm:p-8">
        <div className="grid gap-10 lg:grid-cols-[minmax(0,1fr)_minmax(200px,260px)]">
          {/* ── Calendar ── */}
          <div>
            {/* Month nav */}
            <div className="flex items-center text-white">
              <button
                type="button"
                onClick={() =>
                  setVisibleMonth(
                    (m) => new Date(m.getFullYear(), m.getMonth() - 1, 1),
                  )
                }
                className="-m-1.5 flex flex-none items-center justify-center p-1.5 text-white/40 transition hover:text-white"
              >
                <span className="sr-only">Previous month</span>
                <ChevronLeftIcon className="size-5" />
              </button>
              <div className="flex-auto text-center text-sm font-semibold">
                {formatMonthTitle(visibleMonth)}
              </div>
              <button
                type="button"
                onClick={() =>
                  setVisibleMonth(
                    (m) => new Date(m.getFullYear(), m.getMonth() + 1, 1),
                  )
                }
                className="-m-1.5 flex flex-none items-center justify-center p-1.5 text-white/40 transition hover:text-white"
              >
                <span className="sr-only">Next month</span>
                <ChevronRightIcon className="size-5" />
              </button>
            </div>

            {/* Weekday headers */}
            <div className="mt-6 grid grid-cols-7 text-center text-xs/6 text-white/40">
              {["M", "T", "W", "T", "F", "S", "S"].map((d, i) => (
                <div key={i}>{d}</div>
              ))}
            </div>

            {/* Days grid — bg-white/10 на родителе + непрозрачный bg на ячейках = gap-px эффект */}
            <div
              className="isolate mt-2 grid grid-cols-7 gap-px overflow-hidden rounded-lg text-sm"
              style={{ background: "rgba(255,255,255,0.10)" }}
            >
              {calendarDays.map((day, i) => {
                const isSelected = selectedDate === day.date;
                return (
                  <button
                    key={day.date}
                    type="button"
                    disabled={!day.hasSlots}
                    onClick={() => {
                      setSelectedDate(day.date);
                      setSelectedSlot(null);
                    }}
                    style={{
                      backgroundColor: day.isCurrentMonth
                        ? "var(--pills-background)"
                        : "color-mix(in srgb, var(--pills-background) 60%, transparent)",
                    }}
                    className={cn(
                      "relative py-1.5 focus:z-10 transition-colors",
                      day.hasSlots
                        ? "cursor-pointer hover:brightness-125"
                        : "cursor-default",
                      i === 0 && "rounded-tl-lg",
                      i === 6 && "rounded-tr-lg",
                      i === 35 && "rounded-bl-lg",
                      i === 41 && "rounded-br-lg",
                    )}
                  >
                    <time
                      dateTime={day.date}
                      className={cn(
                        "mx-auto flex size-7 items-center justify-center rounded-full text-sm",
                        isSelected && "bg-white font-semibold text-gray-950",
                        !isSelected &&
                          day.hasSlots &&
                          "font-semibold text-(--accent)",
                        !isSelected &&
                          day.isToday &&
                          !day.hasSlots &&
                          "text-(--accent)",
                        !isSelected &&
                          !day.hasSlots &&
                          day.isCurrentMonth &&
                          "text-white/50",
                        !isSelected &&
                          !day.hasSlots &&
                          !day.isCurrentMonth &&
                          "text-white/20",
                      )}
                    >
                      {day.dayNumber}
                    </time>
                    {day.hasSlots && !isSelected && (
                      <span className="absolute bottom-1 left-1/2 size-1 -translate-x-1/2 rounded-full bg-(--accent)" />
                    )}
                  </button>
                );
              })}
            </div>
          </div>

          {/* ── Time slots ── */}
          <div className="lg:border-l lg:border-white/10 lg:pl-8">
            <h2 className="text-base font-semibold text-white">
              {l.availableTimes}
            </h2>
            <p className="mt-1 text-sm text-white/40">
              {formatDate(selectedDate)}
            </p>

            <div className="mt-6 space-y-3">
              {selectedSlots.length > 0 ? (
                selectedSlots.map((slot) => (
                  <button
                    key={slot.id}
                    type="button"
                    onClick={() => setSelectedSlot(slot.id)}
                    className="flex items-center gap-3 text-sm text-white/70 transition hover:text-white"
                  >
                    <span
                      className={cn(
                        "flex size-4 items-center justify-center rounded-full ring-1 ring-inset transition",
                        selectedSlot === slot.id
                          ? "bg-white ring-white"
                          : "ring-white/30",
                      )}
                    >
                      {selectedSlot === slot.id && (
                        <span className="size-1.5 rounded-full bg-gray-950" />
                      )}
                    </span>
                    <span>{slot.time}</span>
                  </button>
                ))
              ) : (
                <p className="text-sm text-white/30">{l.selectDateAndTime}</p>
              )}
            </div>
          </div>
        </div>
      </section>

      {/* Message */}
      <section className="rounded-2xl border border-white/10 bg-(--pills-background) p-6">
        <h2 className="mb-4 text-base font-semibold text-white/40">
          {l.messageToShop}{" "}
          <span className="font-normal text-white/20">({l.optional})</span>
        </h2>
        <textarea
          value={message}
          onChange={(e) => setMessage(e.target.value)}
          rows={4}
          placeholder={l.messagePlaceholder}
          className="w-full resize-none border-0 border-b border-white/15 bg-transparent pb-2 text-sm text-white outline-none transition placeholder:text-white/25 focus:border-white/40"
        />
      </section>

      {/* Confirm */}
      <div className="flex items-center justify-between gap-4 pt-2">
        <p className="text-sm text-white/30">
          {selectedSlot
            ? `${formatDate(selectedDate)} · ${selectedTime}`
            : l.selectDateAndTime}
        </p>
        <button
          type="button"
          disabled={!selectedSlot}
          onClick={() => {
            setConfirmed(true);
            setTimeout(() => router.push(redirectTo), 1500);
          }}
          className="inline-flex shrink-0 items-center justify-center rounded-full bg-(--accent) px-8 py-3 text-sm font-semibold text-(--accent-foreground) ring-1 ring-inset ring-(--accent) transition hover:bg-(--accent-hover) disabled:cursor-not-allowed disabled:opacity-30"
        >
          {l.confirmBooking}
        </button>
      </div>
    </div>
  );
}
