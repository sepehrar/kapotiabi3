"use client";

import Link from "next/link";
import { useState } from "react";
import { useTranslations } from "next-intl";
import GuestCounter from "./GuestCounter";
import type { ChatMessage, ChatStatus } from "@/lib/search-chat";
import type { Locale } from "@/lib/locale";

type SearchChatPanelProps = {
  messages: ChatMessage[];
  questionsUsed: number;
  maxQuestions: number;
  status: ChatStatus;
  isPending: boolean;
  guestLimitReached: boolean;
  locale: Locale;
  onSendMessage: (message: string) => Promise<void>;
};

export default function SearchChatPanel({
  messages,
  questionsUsed,
  maxQuestions,
  status,
  isPending,
  guestLimitReached,
  locale,
  onSendMessage,
}: SearchChatPanelProps) {
  const t = useTranslations("searchChat");
  const [draft, setDraft] = useState("");
  const isDisabled = isPending || guestLimitReached || status === "limit_reached";

  async function handleSubmit() {
    const value = draft.trim();

    if (!value || isDisabled) {
      return;
    }

    setDraft("");
    await onSendMessage(value);
  }

  return (
    <aside className="rounded-3xl border border-black/10 bg-[#f8f8f8] p-5 sm:p-6">
      <div>
        <h2 className="text-xl font-semibold text-black">{t("title")}</h2>
        <p className="mt-2 text-sm leading-6 text-black/70">
          {t("description")}
        </p>
      </div>

      <div className="mt-6 space-y-4">
        {messages.map((message) => (
          <div
            key={message.id}
            className={`max-w-[90%] rounded-2xl px-4 py-3 text-sm leading-6 ${
              message.role === "assistant"
                ? "bg-white text-black ring-1 ring-black/5"
                : "ml-auto bg-[#f44b05] text-white"
            }`}
          >
            {message.text}
          </div>
        ))}
      </div>

      <div className="mt-6">
        <label htmlFor="search-message" className="sr-only">
          {t("inputLabel")}
        </label>

        {guestLimitReached ? (
          <div className="rounded-2xl bg-white p-5 ring-1 ring-black/5">
            <div className="flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
              <div className="space-y-2">
                <p className="text-sm font-semibold text-(--accent)">
                  {t("limitBadge")}
                </p>
                <h3 className="text-lg font-semibold text-black">
                  {t("lockedTitle")}
                </h3>
                <p className="text-sm leading-6 text-black/70">
                  {t("lockedDescription")}
                </p>
              </div>

              <GuestCounter
                used={questionsUsed}
                max={maxQuestions}
                label={t("questionsLeft")}
              />
            </div>

            <div className="mt-5 flex flex-wrap gap-3">
              <Link
                href={`/${locale}/register`}
                className="rounded-full bg-(--accent) px-5 py-2 text-sm font-medium text-white transition hover:bg-(--accent-hover)"
              >
                {t("createAccount")}
              </Link>

              <Link
                href={`/${locale}/login`}
                className="rounded-full border border-black/10 px-5 py-2 text-sm font-medium text-black transition hover:bg-black hover:text-white"
              >
                {t("login")}
              </Link>
            </div>
          </div>
        ) : (
          <div className="rounded-2xl bg-white p-3 ring-1 ring-black/5">
            <textarea
              id="search-message"
              rows={3}
              value={draft}
              onChange={(event) => setDraft(event.target.value)}
              onKeyDown={(event) => {
                if (event.key === "Enter" && !event.shiftKey) {
                  event.preventDefault();
                  void handleSubmit();
                }
              }}
              placeholder={t("inputPlaceholder")}
              disabled={isDisabled}
              className="w-full resize-none border-0 bg-transparent text-sm text-black placeholder:text-black/40 focus:outline-none"
            />

            <div className="mt-3 flex items-center justify-between gap-3">
              <GuestCounter
                used={questionsUsed}
                max={maxQuestions}
                label={t("questionsLeft")}
              />

              <button
                type="button"
                onClick={() => void handleSubmit()}
                disabled={isDisabled}
                className="rounded-full bg-(--accent) px-5 py-2 text-sm font-medium text-white transition hover:bg-(--accent-hover) ring-(--accent) ring-1 ring-inset disabled:cursor-not-allowed disabled:opacity-50"
              >
                {isPending ? t("sending") : t("send")}
              </button>
            </div>
          </div>
        )}
      </div>
    </aside>
  );
}
