export default function SearchEmptyState() {
  return (
    <div className="rounded-3xl border border-dashed border-black/15 bg-[#f8f8f8] p-8 text-center">
      <h3 className="text-xl font-semibold text-black">No results yet</h3>
      <p className="mt-3 text-sm leading-6 text-black/70 sm:text-base">
        Describe what your car needs to see matching repair shops.
      </p>

      <div className="mt-6 flex flex-wrap justify-center gap-3">
        {[
          "Oil change in Tallinn",
          "Brake repair in Tartu",
          "A/C service for Audi A4",
        ].map((example) => (
          <span
            key={example}
            className="rounded-full bg-white px-4 py-2 text-sm text-black ring-1 ring-black/5"
          >
            {example}
          </span>
        ))}
      </div>
    </div>
  );
}
