"use client";

import { useTranslations } from "next-intl";

export type SearchResult = {
  id: number;
  name: string;
  price: string;
  distance: string;
  rating: string;
  reviews: number;
  updatedAt: string;
  priceValue: number;
  distanceValue: number;
  ratingValue: number;
  serviceFit: string;
  areaMatch: string | null;
  matchReasons: string[];
  bestFor: string;
  tradeoff: string;
  trustNote: string;
  nextStep: string;
};

type SearchResultCardProps = {
  result: SearchResult;
  isExpanded: boolean;
  isCompared: boolean;
  compareDisabled: boolean;
  onToggleDetails: (id: number) => void;
  onToggleCompare: (id: number) => void;
};

export default function SearchResultCard({
  result,
  isExpanded,
  isCompared,
  compareDisabled,
  onToggleDetails,
  onToggleCompare,
}: SearchResultCardProps) {
  const t = useTranslations("searchResultCard");

  return (
    <article
      className={`rounded-3xl border bg-white p-5 shadow-sm transition-all sm:p-6 ${
        isCompared
          ? "border-(--accent) ring-2 ring-(--accent)/30"
          : "border-black/10"
      }`}
    >
      <div className="flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
        <div className="min-w-0">
          <div className="flex flex-wrap items-center gap-2">
            <h3 className="text-xl font-semibold text-black">{result.name}</h3>
            {result.areaMatch ? (
              <span className="rounded-full bg-(--accent)/10 px-3 py-1 text-xs font-medium text-(--accent)">
                {t("nearArea", { area: result.areaMatch })}
              </span>
            ) : null}
            {isCompared ? (
              <span className="rounded-full bg-black px-3 py-1 text-xs font-medium text-white">
                {t("inCompare")}
              </span>
            ) : null}
          </div>

          <div className="mt-4 space-y-2 text-sm text-black/70">
            <p>
              <span className="font-medium text-black">{t("price")}:</span>{" "}
              {result.price}
            </p>
            <p>
              <span className="font-medium text-black">{t("distance")}:</span>{" "}
              {result.distance}
            </p>
            <p>
              <span className="font-medium text-black">{t("rating")}:</span>{" "}
              {result.rating} ({result.reviews} {t("reviews")})
            </p>
            <p>
              <span className="font-medium text-black">
                {t("priceStatus")}:
              </span>{" "}
              {result.updatedAt}
            </p>
          </div>
        </div>

        <div className="flex flex-wrap gap-3 sm:justify-end">
          <button
            type="button"
            onClick={() => onToggleDetails(result.id)}
            className="rounded-full border border-black/10 px-4 py-2 text-sm font-medium text-black transition hover:bg-black hover:text-white"
          >
            {isExpanded ? t("hideDetails") : t("showDetails")}
          </button>

          <button
            type="button"
            onClick={() => onToggleCompare(result.id)}
            disabled={compareDisabled}
            className={`rounded-full px-4 py-2 text-sm font-medium transition ${
              isCompared
                ? "border border-black/10 text-black hover:bg-black hover:text-white"
                : "bg-[#f44b05] text-white hover:bg-[#902d03]"
            } disabled:cursor-not-allowed disabled:opacity-50`}
          >
            {isCompared
              ? t("removeFromCompare")
              : compareDisabled
                ? t("compareFull")
                : t("addToCompare")}
          </button>
        </div>
      </div>

      {isExpanded ? (
        <div className="mt-6 border-t border-black/10 pt-5">
          <div className="grid gap-4 md:grid-cols-2">
            <div className="space-y-2">
              <p className="text-sm font-semibold text-black">
                {t("whyItMatches")}
              </p>
              <p className="text-sm text-black/70">{result.serviceFit}</p>
              <div className="flex flex-wrap gap-2">
                {result.matchReasons.map((reason) => (
                  <span
                    key={reason}
                    className="rounded-full bg-black/5 px-3 py-1 text-xs text-black/70"
                  >
                    {reason}
                  </span>
                ))}
              </div>
            </div>

            <div className="space-y-4">
              <div>
                <p className="text-sm font-semibold text-black">
                  {t("bestFor")}
                </p>
                <p className="mt-1 text-sm text-black/70">{result.bestFor}</p>
              </div>

              <div>
                <p className="text-sm font-semibold text-black">
                  {t("tradeoff")}
                </p>
                <p className="mt-1 text-sm text-black/70">{result.tradeoff}</p>
              </div>

              <div>
                <p className="text-sm font-semibold text-black">
                  {t("trustAndFreshness")}
                </p>
                <p className="mt-1 text-sm text-black/70">{result.trustNote}</p>
              </div>

              <div>
                <p className="text-sm font-semibold text-black">
                  {t("nextStep")}
                </p>
                <p className="mt-1 text-sm text-black/70">{result.nextStep}</p>
              </div>
            </div>
          </div>
        </div>
      ) : null}
    </article>
  );
}
