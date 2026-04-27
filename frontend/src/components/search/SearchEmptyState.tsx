type SearchEmptyStateProps = {
  title: string;
  description: string;
  examples?: string[];
};

export default function SearchEmptyState({
  title,
  description,
  examples = [],
}: SearchEmptyStateProps) {
  return (
    <div className="rounded-3xl border border-dashed border-black/15  p-8 text-center">
      <h3 className="text-xl font-semibold text-white">{title}</h3>
      <p className="mt-3 text-sm leading-6 text-white/60 sm:text-base">
        {description}
      </p>

      {examples.length ? (
        <div className="mt-6 flex flex-wrap justify-center gap-3">
          {examples.map((example) => (
            <span
              key={example}
              className="rounded-full px-4 py-2 text-sm bg-(--pills-background)  shadow ring-1 ring-inset ring-white/10 "
            >
              {example}
            </span>
          ))}
        </div>
      ) : null}
    </div>
  );
}
