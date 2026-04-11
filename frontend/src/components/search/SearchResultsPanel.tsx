"use client";

import SearchResultCard from "./SearchResultCard";
import SearchEmptyState from "./SearchEmptyState";
import type { SearchResult } from "./SearchResultCard";

type SearchResultsPanelProps = {
  results: SearchResult[];
  expandedResultId: number | null;
  comparedResultIds: number[];
  onToggleDetails: (id: number) => void;
  onToggleCompare: (id: number) => void;
};

export default function SearchResultsPanel({
  results,
  expandedResultId,
  comparedResultIds,
  onToggleDetails,
  onToggleCompare,
}: SearchResultsPanelProps) {
  if (!results.length) {
    return (
      <SearchEmptyState
        title="No results yet"
        description="Describe what your car needs to see matching repair shops."
      />
    );
  }

  return (
    <section className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
      {results.map((result) => {
        const isCompared = comparedResultIds.includes(result.id);

        return (
          <SearchResultCard
            key={result.id}
            result={result}
            isExpanded={expandedResultId === result.id}
            isCompared={isCompared}
            compareDisabled={comparedResultIds.length >= 3 && !isCompared}
            onToggleDetails={onToggleDetails}
            onToggleCompare={onToggleCompare}
          />
        );
      })}
    </section>
  );
}
