type SectionTitleProps = {
  title: string;
  description?: string;
  centered?: boolean;
  light?: boolean;
};

export default function SectionTitle({
  title,
  description,
  centered = false,
  light = true,
}: SectionTitleProps) {
  return (
    <div className={`max-w-2xl ${centered ? "mx-auto text-center" : ""}`}>
      <h1
        className={`text-3xl font-semibold tracking-tight sm:text-4xl ${
          light ? "text-white" : "text-gray-900"
        }`}
      >
        {title}
      </h1>

      {description && (
        <p
          className={`mt-3 text-base leading-7 sm:text-lg font-light text-balance ${
            light ? "text-white" : "text-gray-600"
          }`}
        >
          {description}
        </p>
      )}
    </div>
  );
}
