"use client";

import { Popover, PopoverButton, PopoverPanel } from "@headlessui/react";
import { ChevronDownIcon } from "@heroicons/react/20/solid";
import { useTranslations } from "next-intl";

export type SortOption = "price" | "distance" | "rating";

type SearchResultsToolbarProps = {
  countLabel?: string | null;
  summary: string | null;
  note?: string | null;
  showSort?: boolean;
  sortBy?: SortOption;
  onSortChange?: (value: SortOption) => void;
};

export default function SearchResultsToolbar({
  countLabel,
  summary,
  note,
  showSort = false,
  sortBy = "price",
  onSortChange,
}: SearchResultsToolbarProps) {
  const t = useTranslations("searchResultsToolbar");

  const sortOptions: { value: SortOption; label: string }[] = [
    { value: "price", label: t("options.price") },
    { value: "distance", label: t("options.distance") },
    { value: "rating", label: t("options.rating") },
  ];

  const activeOption =
    sortOptions.find((option) => option.value === sortBy) ?? sortOptions[0];

  if (!summary && !countLabel && !note) {
    return null;
  }

  return (
    <div className="flex flex-col gap-4 border-t border-white/70 p-5 sm:flex-row sm:items-start sm:justify-between sm:p-6">
      <div className="space-y-2">
        {countLabel ? (
          <p className="text-sm font-semibold text-(--accent) sm:text-lg">
            {countLabel}
          </p>
        ) : null}

        {summary ? (
          <p className="text-sm text-white sm:text-base">{summary}</p>
        ) : null}

        {note ? <p className="text-sm text-white/70">{note}</p> : null}
      </div>

      {showSort ? (
        <div className="flex items-center gap-3">
          <span className="text-sm text-white/70">{t("sortBy")}</span>

          <Popover className="relative">
            <PopoverButton className="inline-flex items-center gap-x-1 rounded-full bg-gray-800 px-4 py-2 text-sm text-white shadow ring-1 ring-inset ring-white/10 transition hover:text-(--accent)">
              <span>{activeOption.label}</span>
              <ChevronDownIcon aria-hidden="true" className="size-4" />
            </PopoverButton>

            <PopoverPanel
              anchor="bottom end"
              transition
              className="z-50 mt-2 w-40 rounded-2xl bg-gray-800 p-2 text-sm shadow transition data-closed:translate-y-1 data-closed:opacity-0 data-enter:duration-200 data-leave:duration-150"
            >
              <div className="space-y-1">
                {sortOptions.map((option) => {
                  const isActive = option.value === sortBy;

                  return (
                    <button
                      key={option.value}
                      type="button"
                      onClick={() => onSortChange?.(option.value)}
                      className={`block w-full rounded-full px-3 py-2 text-left text-sm font-medium transition-all duration-300 ease-out ${
                        isActive
                          ? "bg-(--accent) text-(--accent-foreground)"
                          : "text-(--accent-foreground) hover:bg-(--accent) hover:text-(--accent-foreground)"
                      }`}
                    >
                      {option.label}
                    </button>
                  );
                })}
              </div>
            </PopoverPanel>
          </Popover>
        </div>
      ) : null}
    </div>
  );
}
