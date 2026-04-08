export const LOCALES = ["en", "et"] as const;
export type Locale = (typeof LOCALES)[number];

export const DEFAULT_LOCALE: Locale = "et";

export const LOCALE_LABEL: Record<Locale, string> = {
  en: "english",
  et: "eesti",
};
