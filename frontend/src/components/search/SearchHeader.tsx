"use client";

import SectionTitle from "../ui/SectionTitle";
import { useTranslations } from "next-intl";

export default function SearchHeader() {
  const t = useTranslations("searchHeader");

  return (
    <SectionTitle
      title={t("title")}
      description={t("description")}
    />
  );
}
