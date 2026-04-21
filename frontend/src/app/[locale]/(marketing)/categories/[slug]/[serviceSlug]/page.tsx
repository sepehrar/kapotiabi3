import { notFound } from "next/navigation";
import { getTranslations } from "next-intl/server";
import ServiceDetailsView from "@/components/categories/ServiceDetailsView";
import { categoriesConfig } from "@/lib/categories";

type ServicePageProps = {
  params: Promise<{
    locale: string;
    slug: string;
    serviceSlug: string;
  }>;
};

function slugify(str: string) {
  return str
    .toLowerCase()
    .replace(/[^a-z0-9]+/g, "-")
    .replace(/(^-|-$)/g, "");
}

export default async function ServicePage({ params }: ServicePageProps) {
  const { slug, serviceSlug } = await params;
  const t = await getTranslations("categoriesPage");
  const ts = await getTranslations("serviceDetailsPage");

  const categories = categoriesConfig.map((category) => ({
    slug: category.slug,
    title: t(`items.${category.slug}.title`),
    description: t(`items.${category.slug}.description`),
    imageUrl: category.imageUrl,
    services: category.services,
  }));

  const category = categories.find((item) => item.slug === slug);

  if (!category) {
    notFound();
  }

  const service = category.services.find(
    (item) => slugify(item) === serviceSlug,
  );

  if (!service) {
    notFound();
  }

  return (
    <ServiceDetailsView
      categoryTitle={category.title}
      categorySlug={category.slug}
      serviceTitle={service}
      description={ts("description", { service: service.toLowerCase() })}
      imageUrl={category.imageUrl}
      searchButton={ts("searchButton")}
      businessButton={ts("businessButton")}
      relatedTitle={ts("relatedTitle")}
      notashop={ts("notashop")}
    />
  );
}
