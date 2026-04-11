import { NextResponse } from "next/server";
import {
  MAX_SEARCH_CHAT_QUESTIONS,
  buildFallbackChatResponse,
  coerceChatStatus,
  ensureSearchFilters,
  getLimitReachedAssistantMessage,
  isChatMessagePayloadArray,
  isLocale,
  normalizeChatResponse,
  toPositiveInt,
  type ChatRequestBody,
  type ChatResponseBody,
  type SearchFilters,
} from "@/lib/search-chat";

const GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions";
const DEFAULT_MODEL = "llama-3.3-70b-versatile";

function extractJson(text: string): unknown {
  const fencedMatch = text.match(/```json\s*([\s\S]*?)```/i);
  const candidate = fencedMatch?.[1] ?? text;
  const startIndex = candidate.indexOf("{");
  const endIndex = candidate.lastIndexOf("}");

  if (startIndex === -1 || endIndex === -1 || endIndex <= startIndex) {
    throw new Error("No JSON object found in model response.");
  }

  return JSON.parse(candidate.slice(startIndex, endIndex + 1));
}

function buildSystemPrompt(
  locale: ChatRequestBody["locale"],
  filters: SearchFilters,
  questionsUsed: number,
  maxQuestions: number,
) {
  const languageName = locale === "et" ? "Estonian" : "English";

  return `
You are CarFix search chat. Your only job is to collect search criteria for car-service discovery.

Rules:
- Reply only in ${languageName}.
- Ask one short follow-up question at a time.
- Stay focused on collecting service search criteria.
- If the user goes off-topic, briefly steer them back to the search.
- Extract as many fields as the user already provided and never ask for information you already know.
- Required fields for status "ready": serviceQuery, city, vehicleMake, vehicleModel.
- Optional fields: vehicleYear, partsQuality, radiusKm.
- Prioritize missing fields in this order: serviceQuery, city, vehicleMake, vehicleModel, vehicleYear, partsQuality, radiusKm.
- If the user has reached the question cap, do not ask another question. Return status "limit_reached".
- Return only valid JSON.

Current normalized filters:
${JSON.stringify(filters, null, 2)}

Question counter:
${JSON.stringify({ questionsUsed, maxQuestions }, null, 2)}

Return this exact JSON shape:
{
  "assistantMessage": "short localized assistant reply",
  "status": "needs_more_info" | "ready" | "limit_reached",
  "filters": {
    "serviceQuery": string | null,
    "city": string | null,
    "vehicleMake": string | null,
    "vehicleModel": string | null,
    "vehicleYear": number | null,
    "partsQuality": "budget" | "oem_equivalent" | "oem" | "not_sure" | null,
    "radiusKm": number | null,
    "missingFields": ["serviceQuery", "city", "vehicleMake", "vehicleModel", "vehicleYear", "partsQuality", "radiusKm"]
  },
  "missingFields": ["serviceQuery", "city", "vehicleMake", "vehicleModel", "vehicleYear", "partsQuality", "radiusKm"]
}
`.trim();
}

function buildApiMessages(
  locale: ChatRequestBody["locale"],
  messages: ChatRequestBody["messages"],
  filters: SearchFilters,
  questionsUsed: number,
  maxQuestions: number,
) {
  return [
    {
      role: "system",
      content: buildSystemPrompt(locale, filters, questionsUsed, maxQuestions),
    },
    ...messages.map((message) => ({
      role: message.role,
      content: message.text,
    })),
  ];
}

async function requestChatCompletion(
  body: ChatRequestBody,
  filters: SearchFilters,
  reminder?: string,
): Promise<ChatResponseBody | null> {
  const apiKey = process.env.GROQ_API_KEY;

  if (!apiKey) {
    return null;
  }

  const messages = buildApiMessages(
    body.locale,
    body.messages,
    filters,
    body.questionsUsed,
    body.maxQuestions,
  );

  if (reminder) {
    messages.push({
      role: "system",
      content: reminder,
    });
  }

  const response = await fetch(GROQ_API_URL, {
    method: "POST",
    headers: {
      Authorization: `Bearer ${apiKey}`,
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      model: process.env.GROQ_CHAT_MODEL ?? DEFAULT_MODEL,
      temperature: 0.2,
      messages,
      response_format: { type: "json_object" },
    }),
  });

  if (!response.ok) {
    throw new Error(`Groq request failed with ${response.status}`);
  }

  const payload = (await response.json()) as {
    choices?: Array<{ message?: { content?: string | null } }>;
  };
  const content = payload.choices?.[0]?.message?.content;

  if (!content) {
    return null;
  }

  return normalizeChatResponse(
    extractJson(content),
    body.locale,
    body.questionsUsed,
    body.maxQuestions,
  );
}

function parseRequestBody(input: unknown): ChatRequestBody | null {
  if (!input || typeof input !== "object") {
    return null;
  }

  const value = input as Record<string, unknown>;

  if (!isLocale(value.locale) || !isChatMessagePayloadArray(value.messages)) {
    return null;
  }

  return {
    locale: value.locale,
    messages: value.messages,
    questionsUsed: toPositiveInt(value.questionsUsed, 0),
    maxQuestions: Math.max(
      1,
      toPositiveInt(value.maxQuestions, MAX_SEARCH_CHAT_QUESTIONS),
    ),
    filters: ensureSearchFilters(value.filters),
  };
}

export async function POST(request: Request) {
  const input = await request.json().catch(() => null);
  const body = parseRequestBody(input);

  if (!body) {
    return NextResponse.json(
      { error: "Invalid chat request payload." },
      { status: 400 },
    );
  }

  const filters = ensureSearchFilters(body.filters);

  if (body.questionsUsed > body.maxQuestions) {
    const status = coerceChatStatus(
      "limit_reached",
      filters,
      body.questionsUsed,
      body.maxQuestions,
    );

    return NextResponse.json({
      assistantMessage: getLimitReachedAssistantMessage(body.locale),
      status,
      filters: {
        ...filters,
        missingFields: filters.missingFields,
      },
      missingFields: filters.missingFields,
    } satisfies ChatResponseBody);
  }

  const reminders = [
    undefined,
    "Return only a single JSON object that follows the requested schema exactly. Do not include markdown fences or extra prose.",
  ];

  for (const reminder of reminders) {
    try {
      const result = await requestChatCompletion(body, filters, reminder);

      if (result) {
        return NextResponse.json(result);
      }
    } catch {
      continue;
    }
  }

  return NextResponse.json(
    buildFallbackChatResponse(
      body.locale,
      filters,
      body.questionsUsed,
      body.maxQuestions,
    ),
  );
}
