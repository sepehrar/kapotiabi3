type SearchSummaryProps = {
  title: string;
  filters: string[];
  emptyState: string;
};

export default function SearchSummary({
  title,
  filters,
  emptyState,
}: SearchSummaryProps) {
  return (
    <section className="border-t border-white/40 pt-6">
      <h2 className="text-lg leading-7 text-white sm:text-lg font-light">
        {title}
      </h2>
      <div className="mt-6 flex flex-wrap gap-3">
        {filters.length ? (
          filters.map((filter) => (
            <span
              key={filter}
              className="rounded-full bg-(--pills-background) px-4 py-2 text-sm shadow ring-1 ring-inset ring-white/10 transition-colors duration-300 hover:bg-(--accent)/40 hover:ring-(--accent)"
            >
              {filter}
            </span>
          ))
        ) : (
          <p className="text-sm text-white/70">{emptyState}</p>
        )}
      </div>
    </section>
  );
}
