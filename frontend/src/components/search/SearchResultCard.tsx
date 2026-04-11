export type SearchResult = {
  id: number;
  name: string;
  price: string;
  distance: string;
  rating: string;
  reviews: number;
  updatedAt: string;
};

type SearchResultCardProps = {
  result: SearchResult;
};

export default function SearchResultCard({ result }: SearchResultCardProps) {
  return (
    <article className="rounded-3xl border border-black/10 bg-white p-5 shadow-sm sm:p-6">
      <div className="flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
        <div>
          <h3 className="text-xl font-semibold text-black">{result.name}</h3>

          <div className="mt-4 space-y-2 text-sm text-black/70">
            <p>
              <span className="font-medium text-black">Price:</span>{" "}
              {result.price}
            </p>
            <p>
              <span className="font-medium text-black">Distance:</span>{" "}
              {result.distance}
            </p>
            <p>
              <span className="font-medium text-black">Google rating:</span>{" "}
              {result.rating} ({result.reviews} reviews)
            </p>
            <p>
              <span className="font-medium text-black">Price status:</span>{" "}
              {result.updatedAt}
            </p>
          </div>
        </div>

        <div className="flex flex-wrap gap-3 sm:justify-end">
          <button
            type="button"
            className="rounded-full border border-black/10 px-4 py-2 text-sm font-medium text-black transition hover:bg-black hover:text-white"
          >
            View details
          </button>

          <button
            type="button"
            className="rounded-full border border-black/10 px-4 py-2 text-sm font-medium text-black transition hover:bg-black hover:text-white"
          >
            Save
          </button>

          <button
            type="button"
            className="rounded-full bg-[#f44b05] px-4 py-2 text-sm font-medium text-white transition hover:bg-[#902d03]"
          >
            Compare
          </button>
        </div>
      </div>
    </article>
  );
}
