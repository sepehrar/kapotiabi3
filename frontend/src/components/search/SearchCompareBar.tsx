"use client";

import { XMarkIcon } from "@heroicons/react/20/solid";
import { useTranslations } from "next-intl";
import type { SearchResult } from "./SearchResultCard";

type SearchCompareBarProps = {
  results: SearchResult[];
  onRemove: (id: number) => void;
  onClear: () => void;
  onOpen: () => void;
  onContinue: () => void;
};

export default function SearchCompareBar({
  results,
  onRemove,
  onClear,
  onOpen,
  onContinue,
}: SearchCompareBarProps) {
  const t = useTranslations("searchCompare");

  if (results.length < 2) {
    return null;
  }

  return (
    <div className="fixed bottom-4 left-1/2 z-40 w-[min(92vw,64rem)] -translate-x-1/2 rounded-3xl border border-white/10 bg-black/95 p-4 shadow-2xl backdrop-blur">
      <div className="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
        <div className="space-y-2">
          <p className="text-sm font-semibold text-(--accent)">
            {t("title", { count: results.length })}
          </p>
          <div className="flex flex-wrap gap-2">
            {results.map((result) => (
              <span
                key={result.id}
                className="inline-flex items-center gap-2 rounded-full bg-white/10 px-3 py-2 text-sm text-white"
              >
                <span>{result.name}</span>
                <button
                  type="button"
                  onClick={() => onRemove(result.id)}
                  className="rounded-full p-0.5 text-white/60 transition hover:bg-white/10 hover:text-white"
                  aria-label={t("removeShop", { name: result.name })}
                >
                  <XMarkIcon className="size-4" />
                </button>
              </span>
            ))}
          </div>
        </div>

        <div className="flex flex-wrap gap-3">
          <button
            type="button"
            onClick={onClear}
            className="rounded-full border border-white/15 px-4 py-2 text-sm font-medium text-white transition hover:bg-white/10"
          >
            {t("clear")}
          </button>

          <button
            type="button"
            onClick={onOpen}
            className="rounded-full bg-(--accent) px-5 py-2 text-sm font-medium text-white transition hover:bg-(--accent-hover)"
          >
            {t("open")}
          </button>

          <button
            type="button"
            onClick={onContinue}
            className="rounded-full bg-white px-5 py-2 text-sm font-medium text-black transition hover:bg-white/80"
          >
            {t("continueToAccount")}
          </button>
        </div>
      </div>
    </div>
  );
}
