import Link from "next/link";
import Image from "next/image";

type ServiceDetailsViewProps = {
  categoryTitle: string;
  categorySlug: string;
  serviceTitle: string;
  description: string;
  imageUrl: string;
  searchButton: string;
  businessButton: string;
  relatedTitle: string;
  notashop: string;
};

export default function ServiceDetailsView({
  categoryTitle,
  categorySlug,
  serviceTitle,
  description,
  imageUrl,
  searchButton,
  businessButton,
  relatedTitle,
  notashop,
}: ServiceDetailsViewProps) {
  return (
    <div className="bg-black py-24">
      <div className="mx-auto max-w-7xl px-6 lg:px-8">
        <div className="flex flex-col items-start gap-12 lg:flex-row lg:justify-start">
          <div className="w-70 shrink-0 overflow-hidden ">
            <Image
              src={imageUrl}
              alt={serviceTitle}
              width={420}
              height={280}
              className="h-65 w-full object-cover lg:h-80 xl:h-90"
              sizes="(max-width: 1024px) 100vw, 420px"
            />
          </div>
          <div className="w-full max-w-2xl">
            <div>
              <p className="text-sm uppercase tracking-[0.2em] text-white/35">
                <Link
                  href={`/categories/${categorySlug}`}
                  className="transition-colors duration-200 hover:text-(--accent)"
                >
                  {categoryTitle}
                </Link>
              </p>

              <h1 className="mt-4 text-2xl font-light tracking-tight text-white text-balance sm:text-3xl">
                {serviceTitle}
              </h1>

              <p className="mt-6 max-w-xl text-lg leading-8 text-white/50 text-balance">
                {description}
              </p>

              <div className="mt-12 flex flex-col gap-2 sm:flex-row">
                <Link
                  href={`/search?category=${categorySlug}&service=${encodeURIComponent(
                    serviceTitle,
                  )}`}
                  className="inline-flex shrink-0 items-center justify-center rounded-full bg-(--accent) px-5 py-3 text-sm font-semibold text-(--accent-foreground) ring-1 ring-inset ring-(--accent) transition hover:bg-(--accent-hover)"
                >
                  {searchButton}
                </Link>

                <Link
                  href="/business"
                  className="shrink-0 rounded-full bg-(--pills-background) px-5 py-3 text-sm shadow ring-1 ring-inset ring-white/10 transition-colors duration-300 hover:bg-(--accent)/40 hover:ring-(--accent)"
                >
                  {businessButton}
                </Link>
              </div>
            </div>
          </div>
        </div>
        <div className="pt-24">
          <h2 className="text-2xl font-light tracking-tight text-white text-balance sm:text-3xl">
            {relatedTitle}
          </h2>

          <p className="mt-6 max-w-xl text-lg leading-8 text-white/50 text-balance">
            {notashop}
          </p>
        </div>
      </div>
    </div>
  );
}
