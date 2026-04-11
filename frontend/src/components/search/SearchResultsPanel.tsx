import SearchResultCard from "./SearchResultCard";
import SearchEmptyState from "./SearchEmptyState";
import type { SearchResult } from "./SearchResultCard";

type SearchResultsPanelProps = {
  results: SearchResult[];
};

export default function SearchResultsPanel({
  results,
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
      {results.map((result) => (
        <SearchResultCard key={result.id} result={result} />
      ))}
    </section>
  );
}
