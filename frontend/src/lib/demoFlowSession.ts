export type DemoSlot = {
  id: string;
  date: string;
  time: string;
  slotId?: number;
};

export type DemoCompareService = {
  id: string;
  name: string;
  price: string;
  distance: string;
  rating: string;
  reviews: number;
  updatedAt: string;
  partsQuality: string;
  service: string;
  car: string;
  address: string;
  phone: string;
  message: string;
  saved?: boolean;
  offerId?: number;
  slots: DemoSlot[];
};

export type DemoCompareDraft = {
  queryLabel: string | null;
  services: DemoCompareService[];
  updatedAt: string;
};

export type DemoBooking = {
  id: string;
  source: "session" | "backend";
  serviceId: string;
  shop: string;
  service: string;
  car: string;
  date: string;
  time: string;
  price: string;
  status: "upcoming" | "completed" | "cancelled";
  messageToShop?: string;
  createdAt: string;
  backendBookingId?: number;
};

const COMPARE_DRAFT_KEY = "kapotiabi.compareDraft";
const BOOKINGS_KEY = "kapotiabi.demoBookings";

const defaultSlots: DemoSlot[] = [
  { id: "s1", date: "2026-05-06", time: "10:00" },
  { id: "s2", date: "2026-05-06", time: "14:00" },
  { id: "s3", date: "2026-05-07", time: "09:00" },
  { id: "s4", date: "2026-05-07", time: "11:00" },
  { id: "s5", date: "2026-05-08", time: "13:00" },
  { id: "s6", date: "2026-05-08", time: "15:00" },
];

function canUseSessionStorage() {
  return typeof window !== "undefined" && Boolean(window.sessionStorage);
}

function readJson<T>(key: string, fallback: T): T {
  if (!canUseSessionStorage()) {
    return fallback;
  }

  try {
    const raw = window.sessionStorage.getItem(key);
    return raw ? (JSON.parse(raw) as T) : fallback;
  } catch {
    return fallback;
  }
}

function writeJson<T>(key: string, value: T) {
  if (!canUseSessionStorage()) {
    return;
  }

  window.sessionStorage.setItem(key, JSON.stringify(value));
}

export function buildDemoService(input: {
  id: number | string;
  name: string;
  price: string;
  distance: string;
  rating: string;
  reviews: number;
  updatedAt: string;
  serviceFit?: string;
}): DemoCompareService {
  return {
    id: String(input.id),
    name: input.name,
    price: input.price,
    distance: input.distance,
    rating: input.rating,
    reviews: input.reviews,
    updatedAt: input.updatedAt,
    partsQuality: "OEM-equivalent",
    service: input.serviceFit ?? "Oil change",
    car: "BMW 320d 2018",
    address: "Parnu mnt 12, Tallinn",
    phone: "+372 5123 4567",
    message:
      "We can take your car at one of the available times. The work takes about 1.5 hours.",
    slots: defaultSlots,
  };
}

export function getCompareDraft(): DemoCompareDraft | null {
  return readJson<DemoCompareDraft | null>(COMPARE_DRAFT_KEY, null);
}

export function saveCompareDraft(
  services: DemoCompareService[],
  queryLabel: string | null,
) {
  writeJson<DemoCompareDraft>(COMPARE_DRAFT_KEY, {
    queryLabel,
    services,
    updatedAt: new Date().toISOString(),
  });
}

export function updateCompareServices(services: DemoCompareService[]) {
  const current = getCompareDraft();
  saveCompareDraft(services, current?.queryLabel ?? null);
}

export function findCompareService(id: string) {
  return getCompareDraft()?.services.find((service) => service.id === id) ?? null;
}

export function getDemoBookings() {
  return readJson<DemoBooking[]>(BOOKINGS_KEY, []);
}

export function saveDemoBooking(booking: DemoBooking) {
  const bookings = getDemoBookings();
  writeJson<DemoBooking[]>(BOOKINGS_KEY, [booking, ...bookings]);
}

export function removeDemoBooking(id: string) {
  writeJson<DemoBooking[]>(
    BOOKINGS_KEY,
    getDemoBookings().filter((booking) => booking.id !== id),
  );
}
