"use client";

import { useEffect, useRef, useState } from "react";
import { useLocale, useTranslations } from "next-intl";
import SearchHeader from "./SearchHeader";
import SearchChatPanel from "./SearchChatPanel";
import SearchSummary from "./SearchSummary";
import SearchResultsToolbar from "./SearchResultsToolbar";
import SearchResultsPanel from "./SearchResultsPanel";
import SearchEmptyState from "./SearchEmptyState";
import SearchLoadingState from "./SearchLoadingState";
import SearchCompareBar from "./SearchCompareBar";
import SearchCompareDialog from "./SearchCompareDialog";
import {
  MAX_SEARCH_CHAT_QUESTIONS,
  createEmptySearchFilters,
  createInitialAssistantMessage,
  getFallbackAssistantMessage,
  getQuestionsUsed,
  hasRequiredFilters,
  type ChatMessage,
  type ChatResponseBody,
  type ChatStatus,
  type PartsQuality,
  type SearchFilters,
} from "@/lib/search-chat";
import type { Locale } from "@/lib/locale";
import type { SortOption } from "./SearchResultsToolbar";
import type { SearchResult } from "./SearchResultCard";

type SearchPhase = "collecting" | "searching" | "results" | "locked";

type SearchResultSeed = {
  id: number;
  name: string;
  price: string;
  distance: string;
  rating: string;
  reviews: number;
  updatedAt: string;
  city: string;
  priceValue: number;
  distanceValue: number;
  ratingValue: number;
  serviceKeywords: string[];
  makeKeywords: string[];
  modelKeywords: string[];
  areaKeywords: string[];
  partsQualities: PartsQuality[];
};

const SEARCH_DELAY_MS = 900;
const STATUS_PATTERNS = {
  en: /\b(did you find anything|what did you find|show me the results|any results)\b/i,
  et: /\b(kas sa leidsid midagi|mida sa leidsid|näita tulemusi|kas tulemusi on)\b/i,
};

const SEARCH_RESULT_CATALOG: SearchResultSeed[] = [
  {
    id: 1,
    name: "AutoExpert Tallinn",
    price: "€95",
    distance: "3.2 km",
    rating: "4.7",
    reviews: 214,
    updatedAt: "Updated 2 days ago",
    city: "Tallinn",
    priceValue: 95,
    distanceValue: 3.2,
    ratingValue: 4.7,
    serviceKeywords: ["oil change", "maintenance", "inspection", "service"],
    makeKeywords: ["bmw", "audi", "volkswagen"],
    modelKeywords: ["3 series", "f30", "f31", "320d"],
    areaKeywords: ["kristiine", "kristiine keskus", "lillekula", "mustamae"],
    partsQualities: ["budget", "oem_equivalent", "oem"],
  },
  {
    id: 2,
    name: "Nord Service",
    price: "From €80",
    distance: "5.6 km",
    rating: "4.5",
    reviews: 98,
    updatedAt: "Updated today",
    city: "Tallinn",
    priceValue: 80,
    distanceValue: 5.6,
    ratingValue: 4.5,
    serviceKeywords: ["oil change", "maintenance", "diagnostics", "service"],
    makeKeywords: ["bmw", "toyota", "volvo", "skoda"],
    modelKeywords: ["3 series", "f30", "corolla", "xc60"],
    areaKeywords: ["mustamae", "kristiine", "haabersti"],
    partsQualities: ["budget", "oem_equivalent"],
  },
  {
    id: 3,
    name: "FixCar Center",
    price: "€110",
    distance: "6.8 km",
    rating: "4.8",
    reviews: 156,
    updatedAt: "Updated 5 days ago",
    city: "Tallinn",
    priceValue: 110,
    distanceValue: 6.8,
    ratingValue: 4.8,
    serviceKeywords: ["oil change", "brake repair", "a/c service", "maintenance"],
    makeKeywords: ["bmw", "mercedes", "audi"],
    modelKeywords: ["3 series", "f31", "c-class"],
    areaKeywords: ["kesklinn", "stockmann", "lillekula"],
    partsQualities: ["oem_equivalent", "oem"],
  },
  {
    id: 4,
    name: "Kristiine Auto Hub",
    price: "€105",
    distance: "2.1 km",
    rating: "4.6",
    reviews: 121,
    updatedAt: "Updated yesterday",
    city: "Tallinn",
    priceValue: 105,
    distanceValue: 2.1,
    ratingValue: 4.6,
    serviceKeywords: ["oil change", "maintenance", "engine service"],
    makeKeywords: ["bmw", "audi", "mercedes"],
    modelKeywords: ["3 series", "f30", "a4"],
    areaKeywords: ["kristiine", "kristiine keskus", "tihase"],
    partsQualities: ["oem_equivalent", "oem"],
  },
  {
    id: 5,
    name: "Tartu Garage",
    price: "€85",
    distance: "4.4 km",
    rating: "4.4",
    reviews: 77,
    updatedAt: "Updated today",
    city: "Tartu",
    priceValue: 85,
    distanceValue: 4.4,
    ratingValue: 4.4,
    serviceKeywords: ["oil change", "maintenance", "service"],
    makeKeywords: ["bmw", "toyota", "ford"],
    modelKeywords: ["3 series", "focus"],
    areaKeywords: ["kesklinn", "raudteejaam"],
    partsQualities: ["budget", "oem_equivalent"],
  },
];

function normalizeValue(value: string | null | undefined) {
  return value?.trim().toLowerCase() ?? "";
}

function tokenizeServiceQuery(serviceQuery: string | null) {
  return normalizeValue(serviceQuery)
    .split(/[\s,/.-]+/)
    .filter(Boolean);
}

function countKeywordMatches(tokens: string[], keywords: string[]) {
  return tokens.reduce((count, token) => {
    const matched = keywords.some((keyword) => {
      const normalizedKeyword = normalizeValue(keyword);

      return (
        normalizedKeyword.includes(token) || token.includes(normalizedKeyword)
      );
    });

    return count + (matched ? 1 : 0);
  }, 0);
}

function extractLocationHint(text: string, locale: Locale): string | null {
  const normalizedText = text.trim();
  const patterns =
    locale === "et"
      ? [/lähedal\s+(.+)$/i, /juures\s+(.+)$/i]
      : [/near\s+(.+)$/i, /around\s+(.+)$/i, /close to\s+(.+)$/i];

  for (const pattern of patterns) {
    const match = normalizedText.match(pattern);

    if (match?.[1]) {
      return match[1].trim();
    }
  }

  return null;
}

function isResultsStatusQuestion(text: string, locale: Locale) {
  return STATUS_PATTERNS[locale].test(text.trim());
}

function buildSearchQueryLabel(
  filters: SearchFilters,
  locationHint: string | null,
  locale: Locale,
) {
  const parts: string[] = [];
  const vehicleLabel = [filters.vehicleMake, filters.vehicleModel]
    .filter(Boolean)
    .join(" ");

  if (filters.serviceQuery) {
    parts.push(filters.serviceQuery);
  }

  if (filters.city) {
    parts.push(locale === "et" ? filters.city : `in ${filters.city}`);
  }

  if (vehicleLabel) {
    parts.push(locale === "et" ? vehicleLabel : `for ${vehicleLabel}`);
  }

  if (locationHint) {
    parts.push(
      locale === "et" ? `${locationHint} lähedal` : `near ${locationHint}`,
    );
  }

  return parts.join(locale === "et" ? ", " : " ");
}

function sortResults(results: SearchResult[], sortBy: SortOption) {
  return [...results].sort((left, right) => {
    if (sortBy === "price") {
      return left.priceValue - right.priceValue;
    }

    if (sortBy === "distance") {
      return left.distanceValue - right.distanceValue;
    }

    return right.ratingValue - left.ratingValue;
  });
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
    pills.push(t(`partsQuality.${filters.partsQuality}`));
  }

  if (filters.radiusKm) {
    pills.push(
      locale === "et"
        ? `${filters.radiusKm} km raadius`
        : `${filters.radiusKm} km radius`,
    );
  }

  return pills;
}

function buildResultCardData(
  result: SearchResultSeed,
  filters: SearchFilters,
  locationHint: string | null,
  locale: Locale,
  cardT: ReturnType<typeof useTranslations>,
  chatT: ReturnType<typeof useTranslations>,
): SearchResult {
  const vehicleLabel = [filters.vehicleMake, filters.vehicleModel]
    .filter(Boolean)
    .join(" ");
  const serviceLabel =
    filters.serviceQuery ?? cardT("serviceFitFallbackService");
  const normalizedLocationHint = normalizeValue(locationHint);
  const areaMatch = normalizedLocationHint
    ? locationHint &&
      result.areaKeywords.some((keyword) => {
        const normalizedKeyword = normalizeValue(keyword);

        return (
          normalizedLocationHint.includes(normalizedKeyword) ||
          normalizedKeyword.includes(normalizedLocationHint)
        );
      })
      ? locationHint
      : null
    : null;
  const partsLabel =
    filters.partsQuality != null
      ? chatT(`partsQuality.${filters.partsQuality}`)
      : null;

  const matchReasons = [
    filters.serviceQuery
      ? cardT("matchReasonService", {
          service: filters.serviceQuery,
        })
      : null,
    filters.city
      ? cardT("matchReasonCity", {
          city: filters.city,
        })
      : null,
    vehicleLabel
      ? cardT("matchReasonVehicle", {
          vehicle: vehicleLabel,
        })
      : null,
    areaMatch
      ? cardT("matchReasonArea", {
          area: areaMatch,
        })
      : null,
    partsLabel &&
    filters.partsQuality &&
    result.partsQualities.includes(filters.partsQuality)
      ? cardT("matchReasonParts", {
          parts: partsLabel,
        })
      : null,
    result.distanceValue <= 3
      ? cardT("matchReasonDistance", {
          distance: result.distance,
        })
      : null,
  ].filter((value): value is string => Boolean(value));

  const bestFor = areaMatch
    ? cardT("bestForArea", {
        area: areaMatch,
      })
    : result.distanceValue <= 3
      ? cardT("bestForClosest")
      : result.priceValue <= 85
        ? cardT("bestForBudget")
        : result.ratingValue >= 4.7
          ? cardT("bestForTrust")
          : cardT("bestForBalanced");

  const tradeoff =
    result.priceValue >= 105
      ? cardT("tradeoffPrice")
      : result.distanceValue >= 6
        ? cardT("tradeoffDistance")
        : result.reviews < 100
          ? cardT("tradeoffReviews")
          : cardT("tradeoffFreshness");

  const trustNote =
    result.ratingValue >= 4.7
      ? cardT("trustNoteHigh", {
          rating: result.rating,
          reviews: result.reviews,
          updatedAt: result.updatedAt,
        })
      : cardT("trustNoteSolid", {
          rating: result.rating,
          reviews: result.reviews,
          updatedAt: result.updatedAt,
        });

  const nextStep = areaMatch
    ? cardT("nextStepArea", {
        area: areaMatch,
      })
    : filters.radiusKm
      ? cardT("nextStepRadius", {
          radius: filters.radiusKm,
        })
      : cardT("nextStepCompare");

  return {
    id: result.id,
    name: result.name,
    price: result.price,
    distance: result.distance,
    rating: result.rating,
    reviews: result.reviews,
    updatedAt: result.updatedAt,
    priceValue: result.priceValue,
    distanceValue: result.distanceValue,
    ratingValue: result.ratingValue,
    serviceFit: vehicleLabel
      ? cardT("serviceFitVehicle", {
          service: serviceLabel,
          vehicle: vehicleLabel,
        })
      : cardT("serviceFit", {
          service: serviceLabel,
        }),
    areaMatch,
    matchReasons: matchReasons.slice(0, 3),
    bestFor,
    tradeoff,
    trustNote,
    nextStep,
  };
}

function deriveVisibleResults(
  filters: SearchFilters,
  locationHint: string | null,
  sortBy: SortOption,
  locale: Locale,
  cardT: ReturnType<typeof useTranslations>,
  chatT: ReturnType<typeof useTranslations>,
) {
  const city = normalizeValue(filters.city);
  const make = normalizeValue(filters.vehicleMake);
  const model = normalizeValue(filters.vehicleModel);
  const serviceTokens = tokenizeServiceQuery(filters.serviceQuery);
  const normalizedLocationHint = normalizeValue(locationHint);

  const matchedResults = SEARCH_RESULT_CATALOG.map((result) => {
    let score = 0;

    if (city && normalizeValue(result.city) === city) {
      score += 5;
    }

    const serviceMatches = countKeywordMatches(
      serviceTokens,
      result.serviceKeywords,
    );
    score += serviceMatches * 2;

    if (serviceTokens.length && serviceMatches === 0) {
      score -= 4;
    }

    if (
      make &&
      result.makeKeywords.some((keyword) => normalizeValue(keyword) === make)
    ) {
      score += 2;
    }

    if (
      model &&
      result.modelKeywords.some((keyword) => {
        const normalizedKeyword = normalizeValue(keyword);

        return (
          normalizeValue(model).includes(normalizedKeyword) ||
          normalizedKeyword.includes(model)
        );
      })
    ) {
      score += 2;
    }

    if (
      normalizedLocationHint &&
      result.areaKeywords.some((keyword) => {
        const normalizedKeyword = normalizeValue(keyword);

        return (
          normalizedLocationHint.includes(normalizedKeyword) ||
          normalizedKeyword.includes(normalizedLocationHint)
        );
      })
    ) {
      score += 3;
    }

    if (
      filters.partsQuality &&
      result.partsQualities.includes(filters.partsQuality)
    ) {
      score += 1;
    }

    return { result, score };
  })
    .filter(({ score, result }) => {
      if (city && normalizeValue(result.city) !== city) {
        return false;
      }

      return score >= 5;
    })
    .map(({ result }) =>
      buildResultCardData(result, filters, locationHint, locale, cardT, chatT),
    );

  return sortResults(matchedResults, sortBy).slice(0, 3);
}

export default function SearchPageView() {
  const locale = useLocale() as Locale;
  const t = useTranslations("searchPage");
  const searchChatT = useTranslations("searchChat");
  const searchResultT = useTranslations("searchResultCard");
  const [searchPhase, setSearchPhase] = useState<SearchPhase>("collecting");
  const [searchQueryLabel, setSearchQueryLabel] = useState<string | null>(null);
  const [visibleResults, setVisibleResults] = useState<SearchResult[]>([]);
  const [guestLimitReached, setGuestLimitReached] = useState(false);
  const [sortBy, setSortBy] = useState<SortOption>("price");
  const [locationHint, setLocationHint] = useState<string | null>(null);
  const [expandedResultId, setExpandedResultId] = useState<number | null>(null);
  const [comparedResultIds, setComparedResultIds] = useState<number[]>([]);
  const [isCompareOpen, setIsCompareOpen] = useState(false);
  const [messages, setMessages] = useState<ChatMessage[]>(() => [
    createInitialAssistantMessage(locale),
  ]);
  const [filters, setFilters] = useState<SearchFilters>(() =>
    createEmptySearchFilters(),
  );
  const [status, setStatus] = useState<ChatStatus>("needs_more_info");
  const [isPending, setIsPending] = useState(false);
  const searchTimeoutRef = useRef<ReturnType<typeof setTimeout> | null>(null);
  const lastSearchKeyRef = useRef<string>("");
  const questionsUsed = getQuestionsUsed(messages);
  const filterPills = buildFilterPills(filters, locale, searchChatT);
  const comparedResults = visibleResults.filter((result) =>
    comparedResultIds.includes(result.id),
  );
  const examples = [
    t("examples.exampleOne"),
    t("examples.exampleTwo"),
    t("examples.exampleThree"),
  ];

  function appendAssistantMessage(text: string) {
    setMessages((currentMessages) => [
      ...currentMessages,
      {
        id: Date.now() + Math.random(),
        role: "assistant",
        text,
      },
    ]);
  }

  function setLockedPhaseIfNeeded(shouldLock: boolean) {
    if (shouldLock) {
      setSearchPhase("locked");
    }
  }

  function reconcileResultSelections(results: SearchResult[]) {
    const visibleIds = new Set(results.map((result) => result.id));

    setComparedResultIds((currentIds) =>
      currentIds.filter((id) => visibleIds.has(id)),
    );
    setExpandedResultId((currentId) =>
      currentId && visibleIds.has(currentId) ? currentId : null,
    );
  }

  function scheduleSearch(
    nextFilters: SearchFilters,
    nextLocationHint: string | null,
    shouldLockAfterSearch: boolean,
  ) {
    const hadExistingSearch = Boolean(searchQueryLabel);
    const nextSearchLabel =
      buildSearchQueryLabel(nextFilters, nextLocationHint, locale) ||
      t("queryFallback");
    const searchKey = JSON.stringify({
      nextFilters,
      nextLocationHint,
      sortBy,
    });

    if (lastSearchKeyRef.current === searchKey) {
      if (shouldLockAfterSearch) {
        setSearchPhase("locked");
      }
      return;
    }

    lastSearchKeyRef.current = searchKey;

    if (searchTimeoutRef.current) {
      clearTimeout(searchTimeoutRef.current);
    }

    setSearchQueryLabel(nextSearchLabel);
    setSearchPhase("searching");
    appendAssistantMessage(
      hadExistingSearch
        ? t("searchUpdatedMessage", {
            query: nextSearchLabel,
          })
        : t("searchStartedMessage", {
            query: nextSearchLabel,
          }),
    );

    searchTimeoutRef.current = setTimeout(() => {
      const nextResults = deriveVisibleResults(
        nextFilters,
        nextLocationHint,
        sortBy,
        locale,
        searchResultT,
        searchChatT,
      );
      const nextPhase = shouldLockAfterSearch ? "locked" : "results";

      setVisibleResults(nextResults);
      reconcileResultSelections(nextResults);
      setSearchPhase(nextPhase);
      appendAssistantMessage(
        nextResults.length
          ? t("resultsFoundMessage", {
              count: nextResults.length,
            })
          : t("resultsEmptyMessage"),
      );
    }, SEARCH_DELAY_MS);
  }

  function buildResultsStatusReply() {
    const query = searchQueryLabel ?? t("queryFallback");

    if (searchPhase === "searching") {
      return searchChatT("stillSearching", { query });
    }

    if (!visibleResults.length) {
      return searchChatT("resultsStatusNone", { query });
    }

    return searchChatT("resultsStatus", {
      count: visibleResults.length,
      query,
    });
  }

  function getResultsSummary() {
    if (!searchQueryLabel) {
      return null;
    }

    if (searchPhase === "searching") {
      return t("searchingDescription", { query: searchQueryLabel });
    }

    if (searchPhase === "results" || searchPhase === "locked") {
      return t("resultsSummary", { query: searchQueryLabel });
    }

    return null;
  }

  function getResultsCountLabel() {
    if (searchPhase === "results" || searchPhase === "locked") {
      return t("resultsCount", { count: visibleResults.length });
    }

    return null;
  }

  function handleToggleDetails(id: number) {
    setExpandedResultId((currentId) => (currentId === id ? null : id));
  }

  function handleToggleCompare(id: number) {
    setComparedResultIds((currentIds) => {
      if (currentIds.includes(id)) {
        return currentIds.filter((currentId) => currentId !== id);
      }

      if (currentIds.length >= 3) {
        return currentIds;
      }

      return [...currentIds, id];
    });
  }

  useEffect(() => {
    if (searchTimeoutRef.current) {
      clearTimeout(searchTimeoutRef.current);
    }

    setMessages([createInitialAssistantMessage(locale)]);
    setFilters(createEmptySearchFilters());
    setStatus("needs_more_info");
    setIsPending(false);
    setSearchPhase("collecting");
    setSearchQueryLabel(null);
    setVisibleResults([]);
    setGuestLimitReached(false);
    setSortBy("price");
    setLocationHint(null);
    setExpandedResultId(null);
    setComparedResultIds([]);
    setIsCompareOpen(false);
    lastSearchKeyRef.current = "";
  }, [locale]);

  useEffect(() => {
    if (searchPhase === "results" || searchPhase === "locked") {
      const nextResults = deriveVisibleResults(
        filters,
        locationHint,
        sortBy,
        locale,
        searchResultT,
        searchChatT,
      );

      setVisibleResults(nextResults);
      reconcileResultSelections(nextResults);
    }
  }, [filters, locationHint, locale, searchPhase, searchChatT, searchResultT, sortBy]);

  useEffect(() => {
    if (comparedResultIds.length < 2 && isCompareOpen) {
      setIsCompareOpen(false);
    }
  }, [comparedResultIds.length, isCompareOpen]);

  useEffect(() => {
    return () => {
      if (searchTimeoutRef.current) {
        clearTimeout(searchTimeoutRef.current);
      }
    };
  }, []);

  async function handleSendMessage(text: string) {
    if (isPending || guestLimitReached || status === "limit_reached") {
      return;
    }

    const userMessage: ChatMessage = {
      id: Date.now(),
      role: "user",
      text,
    };
    const nextMessages = [...messages, userMessage];
    const nextQuestionsUsed = questionsUsed + 1;
    const nextGuestLimitReached = nextQuestionsUsed >= MAX_SEARCH_CHAT_QUESTIONS;
    const nextLocationHint = extractLocationHint(text, locale) ?? locationHint;

    setMessages(nextMessages);
    setIsPending(true);
    setLocationHint(nextLocationHint);

    if (isResultsStatusQuestion(text, locale) && searchPhase !== "collecting") {
      appendAssistantMessage(buildResultsStatusReply());
      setGuestLimitReached(nextGuestLimitReached);
      setIsPending(false);

      if (nextGuestLimitReached) {
        setSearchPhase((currentPhase) =>
          currentPhase === "searching" ? "searching" : "locked",
        );
      }

      return;
    }

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
      appendAssistantMessage(payload.assistantMessage);

      if (hasRequiredFilters(payload.filters)) {
        scheduleSearch(payload.filters, nextLocationHint, nextGuestLimitReached);
      } else if (nextGuestLimitReached) {
        setLockedPhaseIfNeeded(true);
      }

      setGuestLimitReached(nextGuestLimitReached);
    } catch {
      appendAssistantMessage(getFallbackAssistantMessage(locale));
      setGuestLimitReached(nextGuestLimitReached);

      if (nextGuestLimitReached) {
        setLockedPhaseIfNeeded(true);
      }
    } finally {
      setIsPending(false);
    }
  }

  return (
    <>
      <section
        className={`bg-black ${comparedResults.length >= 2 ? "pb-28" : ""}`}
      >
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
                guestLimitReached={guestLimitReached}
                locale={locale}
                onSendMessage={handleSendMessage}
              />
            </div>
          </div>

          <div className="mt-10 space-y-6">
            <SearchResultsToolbar
              countLabel={getResultsCountLabel()}
              summary={getResultsSummary()}
              note={searchPhase === "locked" ? t("lockedDescription") : null}
              showSort={searchPhase === "results" || searchPhase === "locked"}
              sortBy={sortBy}
              onSortChange={setSortBy}
            />

            {searchPhase === "collecting" ? (
              <SearchEmptyState
                title={t("resultsEmptyTitle")}
                description={t("resultsEmptyDescription")}
                examples={examples}
              />
            ) : searchPhase === "searching" ? (
              <SearchLoadingState
                title={t("searchingTitle")}
                description={t("searchingDescription", {
                  query: searchQueryLabel ?? t("queryFallback"),
                })}
              />
            ) : visibleResults.length ? (
              <SearchResultsPanel
                results={visibleResults}
                expandedResultId={expandedResultId}
                comparedResultIds={comparedResultIds}
                onToggleDetails={handleToggleDetails}
                onToggleCompare={handleToggleCompare}
              />
            ) : (
              <SearchEmptyState
                title={t("noMatchesTitle")}
                description={t("noMatchesDescription")}
              />
            )}
          </div>
        </div>
      </section>

      <SearchCompareBar
        results={comparedResults}
        onRemove={handleToggleCompare}
        onClear={() => setComparedResultIds([])}
        onOpen={() => setIsCompareOpen(true)}
      />

      <SearchCompareDialog
        open={isCompareOpen}
        onClose={() => setIsCompareOpen(false)}
        results={comparedResults}
        queryLabel={searchQueryLabel}
      />
    </>
  );
}
