import SearchResultCard from "./SearchResultCard";
import SearchEmptyState from "./SearchEmptyState";

type SearchResult = {
  id: number;
  name: string;
  price: string;
  distance: string;
  rating: string;
  reviews: number;
  updatedAt: string;
};

type SearchResultsPanelProps = {
  results: SearchResult[];
};

export default function SearchResultsPanel({
  results,
}: SearchResultsPanelProps) {
  if (!results.length) {
    return <SearchEmptyState />;
  }

  return (
    <section className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
      {results.map((result) => (
        <SearchResultCard key={result.id} result={result} />
      ))}
    </section>
  );
}
