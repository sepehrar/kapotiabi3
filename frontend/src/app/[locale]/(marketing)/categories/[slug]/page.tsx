import { notFound } from "next/navigation";
import { getTranslations } from "next-intl/server";
import CategoryDetailsView from "@/components/categories/CategoryDetailsView";

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

  const categories = [
    {
      id: 1,
      slug: "roadside-assistance",
      title: t("items.roadside-assistance.title"),
      description: t("items.roadside-assistance.description"),
      imageUrl: "/img/category/01cat.jpg",
      subcategories: [
        "Towing",
        "Fuel delivery",
        "Battery jump start",
        "Wheel replacement",
      ],
    },
    {
      id: 2,
      slug: "maintenance-diagnostics",
      title: t("items.maintenance-diagnostics.title"),
      description: t("items.maintenance-diagnostics.description"),
      imageUrl: "/img/category/02cat.jpg",
      subcategories: [
        "Oil change",
        "Filter replacement",
        "Car diagnostics",
        "Fluid replacement",
      ],
    },
    {
      id: 3,
      slug: "auto-electrics",
      title: t("items.auto-electrics.title"),
      description: t("items.auto-electrics.description"),
      imageUrl: "/img/category/03cat.jpg",
      subcategories: [
        "Electrical diagnostics",
        "Lighting repair",
        "Alternator repair",
        "Starter repair",
      ],
    },
    {
      id: 4,
      slug: "bodywork",
      title: t("items.bodywork.title"),
      description: t("items.bodywork.description"),
      imageUrl: "/img/category/04cat.jpg",
      subcategories: [
        "Body repair",
        "Car painting",
        "Dent repair",
        "Car polishing",
      ],
    },
    {
      id: 5,
      slug: "engine",
      title: t("items.engine.title"),
      description: t("items.engine.description"),
      imageUrl: "/img/category/05cat.jpg",
      subcategories: [
        "Engine diagnostics",
        "Engine repair",
        "Cooling system repair",
        "Fuel system repair",
      ],
    },
    {
      id: 6,
      slug: "transmission",
      title: t("items.transmission.title"),
      description: t("items.transmission.description"),
      imageUrl: "/img/category/06cat.jpg",
      subcategories: [
        "Transmission diagnostics",
        "Gearbox repair",
        "Clutch repair",
        "Transmission fluid replacement",
      ],
    },
    {
      id: 7,
      slug: "interior-care",
      title: t("items.interior-care.title"),
      description: t("items.interior-care.description"),
      imageUrl: "/img/category/07cat.jpg",
      subcategories: [
        "Interior cleaning",
        "Upholstery repair",
        "Seat cover tailoring",
      ],
    },
    {
      id: 8,
      slug: "auto-glass",
      title: t("items.auto-glass.title"),
      description: t("items.auto-glass.description"),
      imageUrl: "/img/category/08cat.jpg",
      subcategories: [
        "Windshield replacement",
        "Chip repair",
        "Window tinting",
      ],
    },
    {
      id: 9,
      slug: "suspension",
      title: t("items.suspension.title"),
      description: t("items.suspension.description"),
      imageUrl: "/img/category/09cat.jpg",
      subcategories: [
        "Suspension diagnostics",
        "Suspension repair",
        "Shock absorber replacement",
      ],
    },
    {
      id: 10,
      slug: "steering",
      title: t("items.steering.title"),
      description: t("items.steering.description"),
      imageUrl: "/img/category/011cat.jpg",
      subcategories: [
        "Steering diagnostics",
        "Steering rack repair",
        "Wheel alignment",
      ],
    },
    {
      id: 11,
      slug: "brake-system",
      title: t("items.brake-system.title"),
      description: t("items.brake-system.description"),
      imageUrl: "/img/category/010cat.jpg",
      subcategories: [
        "Brake diagnostics",
        "Brake pad replacement",
        "Brake disc replacement",
      ],
    },
    {
      id: 12,
      slug: "exhaust-system",
      title: t("items.exhaust-system.title"),
      description: t("items.exhaust-system.description"),
      imageUrl: "/img/category/012cat.jpg",
      subcategories: [
        "Exhaust diagnostics",
        "Muffler repair",
        "Exhaust welding",
      ],
    },
    {
      id: 13,
      slug: "tire-service",
      title: t("items.tire-service.title"),
      description: t("items.tire-service.description"),
      imageUrl: "/img/category/013cat.jpg",
      subcategories: [
        "Tire replacement",
        "Wheel balancing",
        "Tire repair",
        "Seasonal tire change",
      ],
    },
    {
      id: 14,
      slug: "air-conditioning",
      title: t("items.air-conditioning.title"),
      description: t("items.air-conditioning.description"),
      imageUrl: "/img/category/014cat.jpg",
      subcategories: ["A/C diagnostics", "A/C refill", "Compressor repair"],
    },
    {
      id: 15,
      slug: "lpg-systems",
      title: t("items.lpg-systems.title"),
      description: t("items.lpg-systems.description"),
      imageUrl: "/img/category/015cat.jpg",
      subcategories: ["LPG installation", "LPG diagnostics", "LPG repair"],
    },
    {
      id: 16,
      slug: "motorcycle-repair",
      title: t("items.motorcycle-repair.title"),
      description: t("items.motorcycle-repair.description"),
      imageUrl: "/img/category/016cat.jpg",
      subcategories: [],
    },
    {
      id: 17,
      slug: "car-insurance",
      title: t("items.car-insurance.title"),
      description: t("items.car-insurance.description"),
      imageUrl: "/img/category/017cat.jpg",
      subcategories: [],
    },
    {
      id: 18,
      slug: "other-car-repairs",
      title: t("items.other-car-repairs.title"),
      description: t("items.other-car-repairs.description"),
      imageUrl: "/img/category/018cat.jpg",
      subcategories: [],
    },
  ];

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
