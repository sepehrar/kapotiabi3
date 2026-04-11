import { getRequestConfig } from "next-intl/server";
import en from "../../messages/en.json";
import et from "../../messages/et.json";
import { routing } from "./routing";

type Locale = (typeof routing.locales)[number];

const messagesByLocale: Record<Locale, typeof en> = {
  en,
  et,
};

export default getRequestConfig(async ({ requestLocale }) => {
  let locale = await requestLocale;

  if (!locale || !routing.locales.includes(locale as Locale)) {
    locale = routing.defaultLocale;
  }

  return {
    locale,
    messages: messagesByLocale[locale as Locale],
  };
});
