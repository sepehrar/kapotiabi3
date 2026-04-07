type SearchResultsToolbarProps = {
  count: number;
};

export default function SearchResultsToolbar({
  count,
}: SearchResultsToolbarProps) {
  return (
    <div className="flex flex-col gap-4 rounded-3xl border border-black/10 bg-white p-5 sm:flex-row sm:items-center sm:justify-between sm:p-6">
      <p className="text-sm text-black/70 sm:text-base">
        <span className="font-semibold text-black">{count}</span> matching
        services
      </p>

      <div className="flex items-center gap-3">
        <label htmlFor="sort-by" className="text-sm text-black/70">
          Sort by
        </label>
        <select
          id="sort-by"
          className="rounded-full border border-black/10 bg-white px-4 py-2 text-sm text-black focus:outline-none"
          defaultValue="price"
        >
          <option value="price">Price</option>
          <option value="distance">Distance</option>
          <option value="rating">Rating</option>
        </select>
      </div>
    </div>
  );
}
