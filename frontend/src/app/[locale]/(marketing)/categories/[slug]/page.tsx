import { notFound } from "next/navigation";
import { getTranslations } from "next-intl/server";
import CategoryDetailsView from "@/components/categories/CategoryDetailsView";
import { categoriesConfig } from "@/lib/categories";

type CategoryPageProps = {
  params: Promise<{
    locale: string;
    slug: string;
  }>;
};

export default async function CategoryPage({ params }: CategoryPageProps) {
  const { slug } = await params;
  const t = await getTranslations("categoriesPage");
  const tc = await getTranslations("categoryDetailsPage");

  const categories = categoriesConfig.map((category) => ({
    id: category.id,
    slug: category.slug,
    title: t(`items.${category.slug}.title`),
    description: t(`items.${category.slug}.description`),
    imageUrl: category.imageUrl,
    subcategories: category.services,
  }));

  const category = categories.find((item) => item.slug === slug);

  if (!category) {
    notFound();
  }

  return (
    <CategoryDetailsView
      title={category.title}
      description={category.description}
      imageUrl={category.imageUrl}
      slug={category.slug}
      subcategories={category.subcategories}
      ctaTitle={tc("ctaTitle")}
      ctaDescription={tc("ctaDescription")}
      searchButton={tc("searchButton")}
      businessButton={tc("businessButton")}
      relatedTitle={tc("relatedTitle")}
      subcategoriesTitle={tc("subcategoriesTitle")}
      notashop={tc("notashop")}
    />
  );
}
