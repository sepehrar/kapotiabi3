import Link from "next/link";
import Image from "next/image";

type CategoryItem = {
  id: number;
  slug: string;
  title: string;
  description: string;
  imageUrl: string;
};

type CategoriesViewProps = {
  title: string;
  description: string;
  categories: CategoryItem[];
};

export default function CategoriesView({
  title,
  description,
  categories,
}: CategoriesViewProps) {
  return (
    <div className="bg-black py-24 sm:py-32">
      <div className="mx-auto max-w-7xl px-6 lg:px-8">
        <div className="mx-auto max-w-2xl text-center">
          <h1 className="text-balance text-4xl font-semibold tracking-tight text-white sm:text-5xl">
            {title}
          </h1>
          <p className="mt-3 text-lg text-white/70">{description}</p>
        </div>

        <div className="mx-auto mt-16 grid max-w-2xl grid-cols-1 gap-8 sm:grid-cols-2 lg:mx-0 lg:max-w-none lg:grid-cols-3 xl:grid-cols-4">
          {categories.map((category) => (
            <Link
              key={category.id}
              href={`/categories/${category.slug}`}
              className="group block overflow-hidden rounded-3xl border border-white/10 bg-(--pills-background) transition hover:-translate-y-1 hover:border-white/20"
            >
              <div className="relative">
                <Image
                  width={400}
                  height={300}
                  alt={category.title}
                  src={category.imageUrl}
                  className="aspect-4/3 w-full object-cover"
                />
                <div className="absolute inset-0 bg-black/20 transition group-hover:bg-black/10" />
              </div>

              <div className="p-5">
                <h2 className="text-lg font-semibold text-white">
                  {category.title}
                </h2>
                <p className="mt-2 text-sm text-white/65">
                  {category.description}
                </p>
              </div>
            </Link>
          ))}
        </div>
      </div>
    </div>
  );
}
