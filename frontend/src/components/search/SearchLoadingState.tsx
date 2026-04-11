type SearchLoadingStateProps = {
  title: string;
  description: string;
};

export default function SearchLoadingState({
  title,
  description,
}: SearchLoadingStateProps) {
  return (
    <div className="rounded-3xl border border-black/10 bg-white p-6 shadow-sm sm:p-8">
      <div className="flex items-center gap-3">
        <div className="size-3 animate-pulse rounded-full bg-(--accent)" />
        <h3 className="text-xl font-semibold text-black">{title}</h3>
      </div>

      <p className="mt-3 text-sm leading-6 text-black/70 sm:text-base">
        {description}
      </p>

      <div className="mt-8 space-y-4">
        {[0, 1, 2].map((item) => (
          <div
            key={item}
            className="rounded-2xl border border-black/8 bg-[#f8f8f8] p-5"
          >
            <div className="h-6 w-1/3 animate-pulse rounded-full bg-black/10" />
            <div className="mt-4 h-4 w-1/2 animate-pulse rounded-full bg-black/8" />
            <div className="mt-3 h-4 w-2/5 animate-pulse rounded-full bg-black/8" />
            <div className="mt-3 h-4 w-1/3 animate-pulse rounded-full bg-black/8" />
          </div>
        ))}
      </div>
    </div>
  );
}
