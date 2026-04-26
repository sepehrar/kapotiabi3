"use client";

import Link from "next/link";

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
    <div className="bg-black py-24">
      <div className="mx-auto flex max-w-7xl flex-col gap-12 px-6 lg:flex-row lg:items-start lg:justify-between lg:px-8">
        <div className="w-full max-w-xs mt-5">
          <div className="text-left">
            <h1 className="text-2xl font-light tracking-tight text-white text-balance sm:text-3xl">
              {title}
            </h1>

            <div className="relative mt-3">
              <p className="text-lg text-white/70 transition-opacity duration-300">
                {description}
              </p>
            </div>
          </div>
        </div>

        <div className="w-full">
          <ul>
            {categories.map((category) => {
              return (
                <li key={category.id}>
                  <Link
                    href={`/categories/${category.slug}`}
                    className="group relative flex w-full flex-row items-start justify-between gap-6 border-b-2 border-(--accent)/40 py-3 "
                  >
                    <div>
                      <div className="p-1 text-lg font-semibold text-(--accent)">
                        {String(category.id).padStart(2, "0")}
                      </div>

                      <h2 className="mt-4 text-xl font-semibold text-white transition-colors duration-200 group-hover:text-(--accent) group-focus-within:text-(--accent)">
                        {category.title}
                      </h2>
                    </div>

                    <div className="max-w-xs">

                     {/* <div className=" w-42 shrink-0 overflow-hidden ">
                        <Image
                          width={400}
                          height={300}
                          alt={category.title}
                          src={category.imageUrl}
                          className="aspect-4/3 object-cover "
                        />
                      </div>  */}

                      <p className="mt-3 text-base leading-7 text-white/60 hover:text-white transition-colors duration-200 group-hover:text-white group-focus-within:text-white">
                      {category.description}
                    </p>
                    </div>

                 
                  </Link>
                </li>
              );
            })}
          </ul>
        </div>
      </div>
    </div>
  );
}
