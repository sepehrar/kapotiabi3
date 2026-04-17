import { notFound } from "next/navigation";
import { getTranslations } from "next-intl/server";
import ServiceDetailsView from "@/components/categories/ServiceDetailsView";

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

  const categories = [
    {
      slug: "roadside-assistance",
      title: t("items.roadside-assistance.title"),
      description: t("items.roadside-assistance.description"),
      imageUrl: "/img/category/01cat.jpg",
      services: [
        "Towing",
        "Fuel delivery",
        "Battery jump start",
        "Wheel replacement",
      ],
    },
    {
      slug: "maintenance-diagnostics",
      title: t("items.maintenance-diagnostics.title"),
      description: t("items.maintenance-diagnostics.description"),
      imageUrl: "/img/category/02cat.jpg",
      services: [
        "Oil change",
        "Filter replacement",
        "Car diagnostics",
        "Fluid replacement",
      ],
    },
    {
      slug: "auto-electrics",
      title: t("items.auto-electrics.title"),
      description: t("items.auto-electrics.description"),
      imageUrl: "/img/category/03cat.jpg",
      services: [
        "Electrical diagnostics",
        "Lighting repair",
        "Alternator repair",
        "Starter repair",
      ],
    },
    {
      slug: "bodywork",
      title: t("items.bodywork.title"),
      description: t("items.bodywork.description"),
      imageUrl: "/img/category/04cat.jpg",
      services: ["Body repair", "Car painting", "Dent repair", "Car polishing"],
    },
    {
      slug: "engine",
      title: t("items.engine.title"),
      description: t("items.engine.description"),
      imageUrl: "/img/category/05cat.jpg",
      services: [
        "Engine diagnostics",
        "Engine repair",
        "Cooling system repair",
        "Fuel system repair",
      ],
    },
    {
      slug: "transmission",
      title: t("items.transmission.title"),
      description: t("items.transmission.description"),
      imageUrl: "/img/category/06cat.jpg",
      services: [
        "Transmission diagnostics",
        "Gearbox repair",
        "Clutch repair",
        "Transmission fluid replacement",
      ],
    },
    {
      slug: "interior-care",
      title: t("items.interior-care.title"),
      description: t("items.interior-care.description"),
      imageUrl: "/img/category/07cat.jpg",
      services: [
        "Interior cleaning",
        "Upholstery repair",
        "Seat cover tailoring",
      ],
    },
    {
      slug: "auto-glass",
      title: t("items.auto-glass.title"),
      description: t("items.auto-glass.description"),
      imageUrl: "/img/category/08cat.jpg",
      services: ["Windshield replacement", "Chip repair", "Window tinting"],
    },
    {
      slug: "suspension",
      title: t("items.suspension.title"),
      description: t("items.suspension.description"),
      imageUrl: "/img/category/09cat.jpg",
      services: [
        "Suspension diagnostics",
        "Suspension repair",
        "Shock absorber replacement",
      ],
    },
    {
      slug: "steering",
      title: t("items.steering.title"),
      description: t("items.steering.description"),
      imageUrl: "/img/category/011cat.jpg",
      services: [
        "Steering diagnostics",
        "Steering rack repair",
        "Wheel alignment",
      ],
    },
    {
      slug: "brake-system",
      title: t("items.brake-system.title"),
      description: t("items.brake-system.description"),
      imageUrl: "/img/category/010cat.jpg",
      services: [
        "Brake diagnostics",
        "Brake pad replacement",
        "Brake disc replacement",
      ],
    },
    {
      slug: "exhaust-system",
      title: t("items.exhaust-system.title"),
      description: t("items.exhaust-system.description"),
      imageUrl: "/img/category/012cat.jpg",
      services: ["Exhaust diagnostics", "Muffler repair", "Exhaust welding"],
    },
    {
      slug: "tire-service",
      title: t("items.tire-service.title"),
      description: t("items.tire-service.description"),
      imageUrl: "/img/category/013cat.jpg",
      services: [
        "Tire replacement",
        "Wheel balancing",
        "Tire repair",
        "Seasonal tire change",
      ],
    },
    {
      slug: "air-conditioning",
      title: t("items.air-conditioning.title"),
      description: t("items.air-conditioning.description"),
      imageUrl: "/img/category/014cat.jpg",
      services: ["A/C diagnostics", "A/C refill", "Compressor repair"],
    },
    {
      slug: "lpg-systems",
      title: t("items.lpg-systems.title"),
      description: t("items.lpg-systems.description"),
      imageUrl: "/img/category/015cat.jpg",
      services: ["LPG installation", "LPG diagnostics", "LPG repair"],
    },
  ];

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
