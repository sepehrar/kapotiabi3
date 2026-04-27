import assert from "node:assert/strict";
import test, { beforeEach } from "node:test";

import {
  buildDemoService,
  findCompareService,
  getCompareDraft,
  getDemoBookings,
  removeDemoBooking,
  saveCompareDraft,
  saveDemoBooking,
  updateCompareServices,
  type DemoBooking,
} from "../src/lib/demoFlowSession.js";

class MemorySessionStorage {
  private readonly values = new Map<string, string>();

  getItem(key: string) {
    return this.values.get(key) ?? null;
  }

  setItem(key: string, value: string) {
    this.values.set(key, value);
  }

  removeItem(key: string) {
    this.values.delete(key);
  }

  clear() {
    this.values.clear();
  }
}

beforeEach(() => {
  Object.defineProperty(globalThis, "window", {
    configurable: true,
    value: {
      sessionStorage: new MemorySessionStorage(),
    },
  });
});

test("compare draft can be saved, updated, and queried by service id", () => {
  const first = buildDemoService({
    id: 1,
    name: "Fast Garage",
    price: "120 EUR",
    distance: "2.4 km",
    rating: "4.8",
    reviews: 42,
    updatedAt: "Today",
    serviceFit: "Brake repair",
  });
  const second = buildDemoService({
    id: 2,
    name: "City Motors",
    price: "135 EUR",
    distance: "3.1 km",
    rating: "4.7",
    reviews: 30,
    updatedAt: "Yesterday",
  });

  saveCompareDraft([first, second], "brake repair");
  assert.equal(getCompareDraft()?.queryLabel, "brake repair");
  assert.deepEqual(
    getCompareDraft()?.services.map((service) => service.id),
    ["1", "2"],
  );

  updateCompareServices([second]);
  assert.equal(getCompareDraft()?.queryLabel, "brake repair");
  assert.equal(findCompareService("1"), null);
  assert.equal(findCompareService("2")?.name, "City Motors");
});

test("session bookings can be added and removed for demo continuity", () => {
  const booking: DemoBooking = {
    id: "demo-1",
    source: "session",
    serviceId: "2",
    shop: "City Motors",
    service: "Oil change",
    car: "BMW 320d 2018",
    date: "May 6, 2026",
    time: "10:00",
    price: "135 EUR",
    status: "upcoming",
    messageToShop: "Please check the filters too.",
    createdAt: "2026-04-27T10:00:00.000Z",
  };

  saveDemoBooking(booking);
  assert.deepEqual(getDemoBookings(), [booking]);

  removeDemoBooking("demo-1");
  assert.deepEqual(getDemoBookings(), []);
});
