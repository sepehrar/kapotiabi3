import assert from "node:assert/strict";
import { readFile } from "node:fs/promises";
import test from "node:test";

async function readMessages(locale) {
  const raw = await readFile(new URL(`../messages/${locale}.json`, import.meta.url), "utf8");
  return JSON.parse(raw);
}

function flattenKeys(value, prefix = "") {
  if (Array.isArray(value)) {
    return [prefix];
  }

  if (value && typeof value === "object") {
    return Object.entries(value).flatMap(([key, nested]) =>
      flattenKeys(nested, prefix ? `${prefix}.${key}` : key),
    );
  }

  return [prefix];
}

test("English and Estonian message files expose the same translation keys", async () => {
  const [en, et] = await Promise.all([readMessages("en"), readMessages("et")]);
  const enKeys = flattenKeys(en).sort();
  const etKeys = flattenKeys(et).sort();

  assert.deepEqual(etKeys, enKeys);
});

test("core demo and business namespaces are documented in frontend messages", async () => {
  const en = await readMessages("en");
  const requiredNamespaces = [
    "searchCompare",
    "accountCompare",
    "accountBookings",
    "booking",
    "businessDashboard",
    "businessLeads",
    "businessListing",
    "businessPricing",
    "businessSubscription",
    "businessProfile",
  ];

  for (const namespace of requiredNamespaces) {
    assert.ok(en[namespace], `Missing ${namespace} messages`);
  }
});
