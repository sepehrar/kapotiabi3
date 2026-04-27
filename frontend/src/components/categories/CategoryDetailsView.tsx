import Link from "next/link";
import Image from "next/image";

type CategoryDetailsViewProps = {
  title: string;
  description: string;
  imageUrl: string;
  slug: string;
  subcategories: string[];
  ctaTitle: string;
  ctaDescription: string;
  searchButton: string;
  businessButton: string;
  relatedTitle: string;
  subcategoriesTitle: string;
  notashop: string;
};

function slugify(str: string) {
  return str
    .toLowerCase()
    .replace(/[^a-z0-9]+/g, "-")
    .replace(/(^-|-$)/g, "");
}

export default function CategoryDetailsView({
  title,
  description,
  imageUrl,
  slug,
  subcategories,
  searchButton,
  businessButton,
  subcategoriesTitle,
  relatedTitle,
  notashop,
}: CategoryDetailsViewProps) {
  const hasSubcategories = subcategories.length > 0;

  return (
    <div className="bg-black py-24 min-h-screen">
      <div className="mx-auto max-w-7xl px-6 lg:px-8">
        <div className="flex flex-col items-start gap-12 lg:flex-row lg:justify-start">
          <div className="w-75 h-auto overflow-hidden shrink-0 lg:w-112.5">
            <Image
              src={imageUrl}
              alt={title}
              width={450}
              height={300}
              className="h-full w-full object-cover"
              sizes="(max-width: 1024px) 100vw, 450px"
            />
          </div>

          <div className="w-full">
            <div>
              <h1 className="text-2xl font-light tracking-tight text-white text-balance sm:text-3xl">
                {title}
              </h1>

              <p className="mt-6 max-w-xl text-lg leading-8 text-white/50 text-balance">
                {description}
              </p>

              <div className="mt-12 flex flex-col gap-2 sm:flex-row">
                <Link
                  href={`/search?category=${slug}`}
                  className="inline-flex shrink-0 items-center justify-center rounded-full bg-(--accent) px-5 py-3 text-sm font-semibold text-(--accent-foreground) ring-1 ring-inset ring-(--accent) transition hover:bg-(--accent-hover)"
                >
                  {searchButton}
                </Link>

                <Link
                  href={`/register?role=SHOP_OWNER`}
                  className="shrink-0 rounded-full bg-(--pills-background) px-5 py-3 text-sm shadow ring-1 ring-inset ring-white/10 transition-colors duration-300 hover:bg-(--accent)/40 hover:ring-(--accent)"
                >
                  {businessButton}
                </Link>
              </div>
            </div>

            {hasSubcategories ? (
              <div className="pt-24">
                <h2 className="text-2xl font-light tracking-tight text-white text-balance sm:text-3xl">
                  {subcategoriesTitle}
                </h2>

                <ul className="mt-6">
                  {subcategories.map((subcategory, index) => {
                    const subSlug = slugify(subcategory);

                    return (
                      <li key={subSlug}>
                        <Link
                          href={`/categories/${slug}/${subSlug}`}
                          className="group flex items-start justify-between gap-6 border-b-2 border-(--accent)/40 py-5"
                        >
                          <div>
                            <div className="text-sm font-medium text-(--accent)">
                              {String(index + 1).padStart(2, "0")}
                            </div>

                            <h3 className="mt-3 text-xl font-semibold text-white transition-colors duration-200 group-hover:text-(--accent)">
                              {subcategory}
                            </h3>
                          </div>

                          <span className="mt-3 text-sm text-white/50 transition-colors duration-200 group-hover:text-white">
                            Open
                          </span>
                        </Link>
                      </li>
                    );
                  })}
                </ul>
              </div>
            ) : null}
          </div>
        </div>{" "}
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
