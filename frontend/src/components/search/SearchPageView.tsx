"use client";

import { useEffect, useState } from "react";
import { useLocale, useTranslations } from "next-intl";
import SearchHeader from "./SearchHeader";
import SearchChatPanel from "./SearchChatPanel";
import SearchSummary from "./SearchSummary";
import SearchResultsToolbar from "./SearchResultsToolbar";
import SearchResultsPanel from "./SearchResultsPanel";
import {
  MAX_SEARCH_CHAT_QUESTIONS,
  createEmptySearchFilters,
  createInitialAssistantMessage,
  getFallbackAssistantMessage,
  getQuestionsUsed,
  type ChatMessage,
  type ChatResponseBody,
  type ChatStatus,
  type PartsQuality,
  type SearchFilters,
} from "@/lib/search-chat";
import type { Locale } from "@/lib/locale";

const mockResults = [
  {
    id: 1,
    name: "AutoExpert Tallinn",
    price: "€95",
    distance: "3.2 km",
    rating: "4.7",
    reviews: 214,
    updatedAt: "Updated 2 days ago",
  },
  {
    id: 2,
    name: "Nord Service",
    price: "From €80",
    distance: "5.6 km",
    rating: "4.5",
    reviews: 98,
    updatedAt: "Updated today",
  },
  {
    id: 3,
    name: "FixCar Center",
    price: "€110",
    distance: "6.8 km",
    rating: "4.8",
    reviews: 156,
    updatedAt: "Updated 5 days ago",
  },
];

function partsQualityLabel(
  partsQuality: PartsQuality,
  t: ReturnType<typeof useTranslations>,
) {
  return t(`partsQuality.${partsQuality}`);
}

function buildFilterPills(
  filters: SearchFilters,
  locale: Locale,
  t: ReturnType<typeof useTranslations>,
) {
  const pills: string[] = [];

  if (filters.serviceQuery) {
    pills.push(filters.serviceQuery);
  }

  if (filters.city) {
    pills.push(filters.city);
  }

  if (filters.vehicleMake) {
    pills.push(filters.vehicleMake);
  }

  if (filters.vehicleModel) {
    pills.push(filters.vehicleModel);
  }

  if (filters.vehicleYear) {
    pills.push(String(filters.vehicleYear));
  }

  if (filters.partsQuality) {
    pills.push(partsQualityLabel(filters.partsQuality, t));
  }

  if (filters.radiusKm) {
    pills.push(
      locale === "et" ? `${filters.radiusKm} km raadius` : `${filters.radiusKm} km radius`,
    );
  }

  return pills;
}

export default function SearchPageView() {
  const locale = useLocale() as Locale;
  const t = useTranslations("searchPage");
  const searchChatT = useTranslations("searchChat");
  const [messages, setMessages] = useState<ChatMessage[]>(() => [
    createInitialAssistantMessage(locale),
  ]);
  const [filters, setFilters] = useState<SearchFilters>(() =>
    createEmptySearchFilters(),
  );
  const [status, setStatus] = useState<ChatStatus>("needs_more_info");
  const [isPending, setIsPending] = useState(false);
  const questionsUsed = getQuestionsUsed(messages);
  const filterPills = buildFilterPills(filters, locale, searchChatT);

  useEffect(() => {
    setMessages([createInitialAssistantMessage(locale)]);
    setFilters(createEmptySearchFilters());
    setStatus("needs_more_info");
    setIsPending(false);
  }, [locale]);

  async function handleSendMessage(text: string) {
    if (isPending || status === "limit_reached") {
      return;
    }

    const userMessage: ChatMessage = {
      id: Date.now(),
      role: "user",
      text,
    };
    const nextMessages = [...messages, userMessage];
    const nextQuestionsUsed = questionsUsed + 1;

    setMessages(nextMessages);
    setIsPending(true);

    try {
      const response = await fetch("/api/chat", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          locale,
          messages: nextMessages.map(({ role, text: messageText }) => ({
            role,
            text: messageText,
          })),
          questionsUsed: nextQuestionsUsed,
          maxQuestions: MAX_SEARCH_CHAT_QUESTIONS,
          filters,
        }),
      });

      const payload = (await response.json()) as ChatResponseBody;

      if (!response.ok || !payload.assistantMessage) {
        throw new Error("Chat request failed.");
      }

      setFilters(payload.filters);
      setStatus(payload.status);
      setMessages((currentMessages) => [
        ...currentMessages,
        {
          id: Date.now() + 1,
          role: "assistant",
          text: payload.assistantMessage,
        },
      ]);
    } catch {
      setMessages((currentMessages) => [
        ...currentMessages,
        {
          id: Date.now() + 1,
          role: "assistant",
          text: getFallbackAssistantMessage(locale),
        },
      ]);
    } finally {
      setIsPending(false);
    }
  }

  return (
    <section className="bg-black">
      <div className="mx-auto w-full max-w-7xl px-6 py-8 sm:px-12 lg:px-8 lg:py-10">
        <div className="flex flex-col gap-10 lg:flex-row">
          <div className="flex max-w-xl flex-col gap-12">
            <SearchHeader />
            <SearchSummary
              title={t("summaryTitle")}
              filters={filterPills}
              emptyState={t("summaryEmpty")}
            />
          </div>

          <div className="w-full">
            <SearchChatPanel
              messages={messages}
              questionsUsed={questionsUsed}
              maxQuestions={MAX_SEARCH_CHAT_QUESTIONS}
              status={status}
              isPending={isPending}
              onSendMessage={handleSendMessage}
            />
          </div>
        </div>

        <div className="mt-10 space-y-6">
          <SearchResultsToolbar count={mockResults.length} />
          <SearchResultsPanel results={mockResults} />
        </div>
      </div>
    </section>
  );
}
