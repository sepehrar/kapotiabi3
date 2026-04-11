import type { Locale } from "@/lib/locale";

export const MAX_SEARCH_CHAT_QUESTIONS = 5;

export const SEARCH_FILTER_FIELDS = [
  "serviceQuery",
  "city",
  "vehicleMake",
  "vehicleModel",
  "vehicleYear",
  "partsQuality",
  "radiusKm",
] as const;

export const REQUIRED_SEARCH_FIELDS = [
  "serviceQuery",
  "city",
  "vehicleMake",
  "vehicleModel",
] as const;

export type SearchFilterField = (typeof SEARCH_FILTER_FIELDS)[number];
export type PartsQuality = "budget" | "oem_equivalent" | "oem" | "not_sure";
export type ChatRole = "assistant" | "user";
export type ChatStatus = "needs_more_info" | "ready" | "limit_reached";

export type SearchFilters = {
  serviceQuery: string | null;
  city: string | null;
  vehicleMake: string | null;
  vehicleModel: string | null;
  vehicleYear: number | null;
  partsQuality: PartsQuality | null;
  radiusKm: number | null;
  missingFields: SearchFilterField[];
};

export type ChatMessage = {
  id: number;
  role: ChatRole;
  text: string;
};

export type ChatMessagePayload = Pick<ChatMessage, "role" | "text">;

export type ChatRequestBody = {
  locale: Locale;
  messages: ChatMessagePayload[];
  questionsUsed: number;
  maxQuestions: number;
  filters: SearchFilters;
};

export type ChatResponseBody = {
  assistantMessage: string;
  status: ChatStatus;
  filters: SearchFilters;
  missingFields: SearchFilterField[];
};

export function createEmptySearchFilters(): SearchFilters {
  return {
    serviceQuery: null,
    city: null,
    vehicleMake: null,
    vehicleModel: null,
    vehicleYear: null,
    partsQuality: null,
    radiusKm: null,
    missingFields: [...SEARCH_FILTER_FIELDS],
  };
}

export function createInitialAssistantMessage(locale: Locale): ChatMessage {
  return {
    id: 1,
    role: "assistant",
    text:
      locale === "et"
        ? "Mida sinu auto praegu vajab?"
        : "What does your car need right now?",
  };
}

export function getQuestionsUsed(messages: ChatMessage[]): number {
  return messages.filter((message) => message.role === "user").length;
}

export function getFallbackAssistantMessage(locale: Locale): string {
  return locale === "et"
    ? "Mul ei õnnestunud praegu vastust saada. Proovi palun uuesti mõne hetke pärast."
    : "I could not get a response right now. Please try again in a moment.";
}

export function getLimitReachedAssistantMessage(locale: Locale): string {
  return locale === "et"
    ? "Küsimuste limiit on täis. Saadud andmete põhjal saad otsingut juba jätkata."
    : "You have reached the question limit. You can continue with the search using the details collected so far.";
}

export function getReadyAssistantMessage(locale: Locale): string {
  return locale === "et"
    ? "Mul on otsingu alustamiseks piisavalt infot."
    : "I have enough information to start the search.";
}

export function sanitizeText(value: unknown): string | null {
  if (typeof value !== "string") {
    return null;
  }

  const trimmed = value.trim().replace(/\s+/g, " ");
  return trimmed ? trimmed : null;
}

export function clampVehicleYear(value: unknown): number | null {
  if (typeof value !== "number" || !Number.isFinite(value)) {
    return null;
  }

  const normalizedYear = Math.trunc(value);
  const currentYear = new Date().getFullYear() + 1;

  if (normalizedYear < 1950 || normalizedYear > currentYear) {
    return null;
  }

  return normalizedYear;
}

export function clampRadiusKm(value: unknown): number | null {
  if (typeof value !== "number" || !Number.isFinite(value)) {
    return null;
  }

  if (value <= 0 || value > 250) {
    return null;
  }

  return Math.round(value);
}

export function normalizeMissingFields(
  fields: unknown,
): SearchFilterField[] | null {
  if (!Array.isArray(fields)) {
    return null;
  }

  const normalized = fields.filter((field): field is SearchFilterField =>
    typeof field === "string" &&
    (SEARCH_FILTER_FIELDS as readonly string[]).includes(field),
  );

  return Array.from(new Set(normalized));
}

export function getMissingFields(filters: Omit<SearchFilters, "missingFields">) {
  return SEARCH_FILTER_FIELDS.filter((field) => filters[field] === null);
}

export function normalizeSearchFilters(input: unknown): SearchFilters | null {
  if (!input || typeof input !== "object") {
    return null;
  }

  const value = input as Record<string, unknown>;
  const partsQuality =
    typeof value.partsQuality === "string" &&
    ["budget", "oem_equivalent", "oem", "not_sure"].includes(value.partsQuality)
      ? (value.partsQuality as PartsQuality)
      : null;

  const normalized = {
    serviceQuery: sanitizeText(value.serviceQuery),
    city: sanitizeText(value.city),
    vehicleMake: sanitizeText(value.vehicleMake),
    vehicleModel: sanitizeText(value.vehicleModel),
    vehicleYear: clampVehicleYear(value.vehicleYear),
    partsQuality,
    radiusKm: clampRadiusKm(value.radiusKm),
  };

  const missingFields = getMissingFields(normalized);

  return {
    ...normalized,
    missingFields,
  };
}

export function ensureSearchFilters(input: unknown): SearchFilters {
  return normalizeSearchFilters(input) ?? createEmptySearchFilters();
}

export function hasRequiredFilters(filters: SearchFilters): boolean {
  return REQUIRED_SEARCH_FIELDS.every((field) => filters[field] !== null);
}

export function coerceChatStatus(
  status: unknown,
  filters: SearchFilters,
  questionsUsed: number,
  maxQuestions: number,
): ChatStatus {
  if (questionsUsed > maxQuestions) {
    return "limit_reached";
  }

  if (hasRequiredFilters(filters)) {
    return "ready";
  }

  return status === "needs_more_info" ? status : "needs_more_info";
}

export function isLocale(value: unknown): value is Locale {
  return value === "en" || value === "et";
}

export function isChatMessagePayloadArray(
  messages: unknown,
): messages is ChatMessagePayload[] {
  return (
    Array.isArray(messages) &&
    messages.every(
      (message) =>
        message &&
        typeof message === "object" &&
        (message.role === "assistant" || message.role === "user") &&
        typeof message.text === "string",
    )
  );
}

export function toPositiveInt(value: unknown, fallback: number): number {
  if (typeof value !== "number" || !Number.isFinite(value)) {
    return fallback;
  }

  return Math.max(0, Math.trunc(value));
}

export function normalizeChatResponse(
  payload: unknown,
  locale: Locale,
  questionsUsed: number,
  maxQuestions: number,
): ChatResponseBody | null {
  if (!payload || typeof payload !== "object") {
    return null;
  }

  const value = payload as Record<string, unknown>;
  const assistantMessage = sanitizeText(value.assistantMessage);
  const filters = normalizeSearchFilters(value.filters);

  if (!assistantMessage || !filters) {
    return null;
  }

  const missingFields =
    normalizeMissingFields(value.missingFields) ?? filters.missingFields;

  const normalizedFilters: SearchFilters = {
    ...filters,
    missingFields,
  };

  const status = coerceChatStatus(
    value.status,
    normalizedFilters,
    questionsUsed,
    maxQuestions,
  );
  const responseMessage =
    status === "limit_reached"
      ? getLimitReachedAssistantMessage(locale)
      : assistantMessage;

  return {
    assistantMessage: responseMessage,
    status,
    filters: {
      ...normalizedFilters,
      missingFields: getMissingFields(normalizedFilters),
    },
    missingFields: getMissingFields(normalizedFilters),
  };
}

export function buildFallbackChatResponse(
  locale: Locale,
  filters: SearchFilters,
  questionsUsed: number,
  maxQuestions: number,
): ChatResponseBody {
  const missingFields = getMissingFields(filters);
  const status = coerceChatStatus(
    "needs_more_info",
    filters,
    questionsUsed,
    maxQuestions,
  );

  return {
    assistantMessage:
      status === "limit_reached"
        ? getLimitReachedAssistantMessage(locale)
        : status === "ready"
          ? getReadyAssistantMessage(locale)
          : getFallbackAssistantMessage(locale),
    status,
    filters: {
      ...filters,
      missingFields,
    },
    missingFields,
  };
}
