export type CategoryConfig = {
  id: number;
  slug: string;
  imageUrl: string;
  services: string[];
};

export const categoriesConfig: CategoryConfig[] = [
  {
    id: 1,
    slug: "roadside-assistance",
    imageUrl: "/img/category/01cat.jpg",
    services: [
      "Towing",
      "Fuel delivery",
      "Battery jump start",
      "Wheel replacement",
    ],
  },
  {
    id: 2,
    slug: "maintenance-diagnostics",
    imageUrl: "/img/category/02cat.jpg",
    services: [
      "Oil change",
      "Filter replacement",
      "Car diagnostics",
      "Fluid replacement",
    ],
  },
  {
    id: 3,
    slug: "auto-electrics",
    imageUrl: "/img/category/03cat.jpg",
    services: [
      "Electrical diagnostics",
      "Lighting repair",
      "Alternator repair",
      "Starter repair",
    ],
  },
  {
    id: 4,
    slug: "bodywork",
    imageUrl: "/img/category/04cat.jpg",
    services: ["Body repair", "Car painting", "Dent repair", "Car polishing"],
  },
  {
    id: 5,
    slug: "engine",
    imageUrl: "/img/category/05cat.jpg",
    services: [
      "Engine diagnostics",
      "Engine repair",
      "Cooling system repair",
      "Fuel system repair",
    ],
  },
  {
    id: 6,
    slug: "transmission",
    imageUrl: "/img/category/06cat.jpg",
    services: [
      "Transmission diagnostics",
      "Gearbox repair",
      "Clutch repair",
      "Transmission fluid replacement",
    ],
  },
  {
    id: 7,
    slug: "interior-care",
    imageUrl: "/img/category/07cat.jpg",
    services: [
      "Interior cleaning",
      "Upholstery repair",
      "Seat cover tailoring",
    ],
  },
  {
    id: 8,
    slug: "auto-glass",
    imageUrl: "/img/category/08cat.jpg",
    services: ["Windshield replacement", "Chip repair", "Window tinting"],
  },
  {
    id: 9,
    slug: "suspension",
    imageUrl: "/img/category/09cat.jpg",
    services: [
      "Suspension diagnostics",
      "Suspension repair",
      "Shock absorber replacement",
    ],
  },
  {
    id: 10,
    slug: "steering",
    imageUrl: "/img/category/011cat.jpg",
    services: [
      "Steering diagnostics",
      "Steering rack repair",
      "Wheel alignment",
    ],
  },
  {
    id: 11,
    slug: "brake-system",
    imageUrl: "/img/category/010cat.jpg",
    services: [
      "Brake diagnostics",
      "Brake pad replacement",
      "Brake disc replacement",
    ],
  },
  {
    id: 12,
    slug: "exhaust-system",
    imageUrl: "/img/category/012cat.jpg",
    services: ["Exhaust diagnostics", "Muffler repair", "Exhaust welding"],
  },
  {
    id: 13,
    slug: "tire-service",
    imageUrl: "/img/category/013cat.jpg",
    services: [
      "Tire replacement",
      "Wheel balancing",
      "Tire repair",
      "Seasonal tire change",
    ],
  },
  {
    id: 14,
    slug: "air-conditioning",
    imageUrl: "/img/category/014cat.jpg",
    services: ["A/C diagnostics", "A/C refill", "Compressor repair"],
  },
  {
    id: 15,
    slug: "lpg-systems",
    imageUrl: "/img/category/015cat.jpg",
    services: ["LPG installation", "LPG diagnostics", "LPG repair"],
  },
  {
    id: 16,
    slug: "motorcycle-repair",
    imageUrl: "/img/category/016cat.jpg",
    services: [],
  },
  {
    id: 17,
    slug: "car-insurance",
    imageUrl: "/img/category/017cat.jpg",
    services: [],
  },
  {
    id: 18,
    slug: "other-car-repairs",
    imageUrl: "/img/category/018cat.jpg",
    services: [],
  },
];
