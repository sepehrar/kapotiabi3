"use client";

import {
  Dialog,
  DialogBackdrop,
  DialogPanel,
  DialogTitle,
} from "@headlessui/react";
import { XMarkIcon } from "@heroicons/react/24/outline";
import { useTranslations } from "next-intl";
import type { SearchResult } from "./SearchResultCard";

type SearchCompareDialogProps = {
  open: boolean;
  onClose: () => void;
  results: SearchResult[];
  queryLabel: string | null;
};

export default function SearchCompareDialog({
  open,
  onClose,
  results,
  queryLabel,
}: SearchCompareDialogProps) {
  const t = useTranslations("searchCompare");

  return (
    <Dialog open={open} onClose={onClose} className="relative z-50">
      <DialogBackdrop className="fixed inset-0 bg-black/70 backdrop-blur-sm" />

      <div className="fixed inset-0 overflow-y-auto p-4 sm:p-6">
        <div className="mx-auto flex min-h-full max-w-6xl items-center justify-center">
          <DialogPanel className="w-full rounded-[2rem] border border-black/10 bg-[#f8f8f8] p-6 shadow-2xl sm:p-8">
            <div className="flex items-start justify-between gap-4">
              <div className="space-y-2">
                <DialogTitle className="text-2xl font-semibold text-black">
                  {t("dialogTitle")}
                </DialogTitle>
                <p className="text-sm text-black/70">
                  {queryLabel
                    ? t("dialogDescription", { query: queryLabel })
                    : t("dialogDescriptionFallback")}
                </p>
              </div>

              <button
                type="button"
                onClick={onClose}
                className="rounded-full border border-black/10 p-2 text-black/70 transition hover:bg-black hover:text-white"
                aria-label={t("close")}
              >
                <XMarkIcon className="size-5" />
              </button>
            </div>

            <div className="mt-6 overflow-x-auto">
              <table className="min-w-full border-separate border-spacing-0 overflow-hidden rounded-3xl">
                <thead>
                  <tr>
                    <th className="bg-black px-4 py-4 text-left text-sm font-semibold text-white">
                      {t("metric")}
                    </th>
                    {results.map((result) => (
                      <th
                        key={result.id}
                        className="bg-black px-4 py-4 text-left text-sm font-semibold text-white"
                      >
                        {result.name}
                      </th>
                    ))}
                  </tr>
                </thead>
                <tbody>
                  {[
                    {
                      label: t("rows.price"),
                      render: (result: SearchResult) => result.price,
                    },
                    {
                      label: t("rows.distance"),
                      render: (result: SearchResult) => result.distance,
                    },
                    {
                      label: t("rows.rating"),
                      render: (result: SearchResult) =>
                        `${result.rating} (${result.reviews})`,
                    },
                    {
                      label: t("rows.priceStatus"),
                      render: (result: SearchResult) => result.updatedAt,
                    },
                    {
                      label: t("rows.bestFor"),
                      render: (result: SearchResult) => result.bestFor,
                    },
                    {
                      label: t("rows.tradeoff"),
                      render: (result: SearchResult) => result.tradeoff,
                    },
                  ].map((row, rowIndex) => (
                    <tr key={row.label}>
                      <th
                        className={`px-4 py-4 text-left text-sm font-semibold text-black ${
                          rowIndex % 2 === 0 ? "bg-white" : "bg-black/5"
                        }`}
                      >
                        {row.label}
                      </th>
                      {results.map((result) => (
                        <td
                          key={`${row.label}-${result.id}`}
                          className={`px-4 py-4 text-sm text-black/75 ${
                            rowIndex % 2 === 0 ? "bg-white" : "bg-black/5"
                          }`}
                        >
                          {row.render(result)}
                        </td>
                      ))}
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </DialogPanel>
        </div>
      </div>
    </Dialog>
  );
}
