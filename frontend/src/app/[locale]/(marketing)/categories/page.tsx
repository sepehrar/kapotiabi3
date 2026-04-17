// frontend\src\app\[locale]\(marketing) \categories\page.tsx
import { getTranslations } from "next-intl/server";
import CategoriesView from "@/components/categories/CategoriesView";

export default async function CategoriesPage() {
  const t = await getTranslations("categoriesPage");

  const categories = [
    {
      id: 1,
      slug: "roadside-assistance",
      title: t("items.roadside-assistance.title"),
      description: t("items.roadside-assistance.description"),
      imageUrl: "/img/category/01cat.jpg",
    },
    {
      id: 2,
      slug: "maintenance-diagnostics",
      title: t("items.maintenance-diagnostics.title"),
      description: t("items.maintenance-diagnostics.description"),
      imageUrl: "/img/category/02cat.jpg",
    },
    {
      id: 3,
      slug: "auto-electrics",
      title: t("items.auto-electrics.title"),
      description: t("items.auto-electrics.description"),
      imageUrl: "/img/category/03cat.jpg",
    },
    {
      id: 4,
      slug: "bodywork",
      title: t("items.bodywork.title"),
      description: t("items.bodywork.description"),
      imageUrl: "/img/category/04cat.jpg",
    },
    {
      id: 5,
      slug: "engine",
      title: t("items.engine.title"),
      description: t("items.engine.description"),
      imageUrl: "/img/category/05cat.jpg",
    },
    {
      id: 6,
      slug: "transmission",
      title: t("items.transmission.title"),
      description: t("items.transmission.description"),
      imageUrl: "/img/category/06cat.jpg",
    },
    {
      id: 7,
      slug: "interior-care",
      title: t("items.interior-care.title"),
      description: t("items.interior-care.description"),
      imageUrl: "/img/category/07cat.jpg",
    },
    {
      id: 8,
      slug: "auto-glass",
      title: t("items.auto-glass.title"),
      description: t("items.auto-glass.description"),
      imageUrl: "/img/category/08cat.jpg",
    },
    {
      id: 9,
      slug: "suspension",
      title: t("items.suspension.title"),
      description: t("items.suspension.description"),
      imageUrl: "/img/category/09cat.jpg",
    },
    {
      id: 10,
      slug: "steering",
      title: t("items.steering.title"),
      description: t("items.steering.description"),
      imageUrl: "/img/category/011cat.jpg",
    },
    {
      id: 11,
      slug: "brake-system",
      title: t("items.brake-system.title"),
      description: t("items.brake-system.description"),
      imageUrl: "/img/category/010cat.jpg",
    },
    {
      id: 12,
      slug: "exhaust-system",
      title: t("items.exhaust-system.title"),
      description: t("items.exhaust-system.description"),
      imageUrl: "/img/category/012cat.jpg",
    },
    {
      id: 13,
      slug: "tire-service",
      title: t("items.tire-service.title"),
      description: t("items.tire-service.description"),
      imageUrl: "/img/category/013cat.jpg",
    },
    {
      id: 14,
      slug: "air-conditioning",
      title: t("items.air-conditioning.title"),
      description: t("items.air-conditioning.description"),
      imageUrl: "/img/category/014cat.jpg",
    },
    {
      id: 15,
      slug: "lpg-systems",
      title: t("items.lpg-systems.title"),
      description: t("items.lpg-systems.description"),
      imageUrl: "/img/category/015cat.jpg",
    },
    {
      id: 16,
      slug: "motorcycle-repair",
      title: t("items.motorcycle-repair.title"),
      description: t("items.motorcycle-repair.description"),
      imageUrl: "/img/category/016cat.jpg",
    },
    {
      id: 17,
      slug: "car-insurance",
      title: t("items.car-insurance.title"),
      description: t("items.car-insurance.description"),
      imageUrl: "/img/category/017cat.jpg",
    },
    {
      id: 18,
      slug: "other-car-repairs",
      title: t("items.other-car-repairs.title"),
      description: t("items.other-car-repairs.description"),
      imageUrl: "/img/category/018cat.jpg",
    },
  ];

  return (
    <CategoriesView
      title={t("title")}
      description={t("description")}
      categories={categories}
    />
  );
}
