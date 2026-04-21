// frontend\src\app\[locale]\(marketing) \categories\page.tsx
import { getTranslations } from "next-intl/server";
import CategoriesView from "@/components/categories/CategoriesView";
import { categoriesConfig } from "@/lib/categories";

export default async function CategoriesPage() {
  const t = await getTranslations("categoriesPage");

  const categories = categoriesConfig.map((category) => ({
    id: category.id,
    slug: category.slug,
    title: t(`items.${category.slug}.title`),
    description: t(`items.${category.slug}.description`),
    imageUrl: category.imageUrl,
  }));

  return (
    <CategoriesView
      title={t("title")}
      description={t("description")}
      categories={categories}
    />
  );
}
