const DEFAULT_DEV_API_URL = "http://localhost:8080";

export function getPublicApiBaseUrl() {
  const raw = process.env.NEXT_PUBLIC_API_URL?.trim();
  if (raw) {
    return raw.replace(/\/$/, "");
  }
  if (process.env.NODE_ENV === "development") {
    return DEFAULT_DEV_API_URL;
  }
  throw new Error(
    "NEXT_PUBLIC_API_URL is not configured. Set it in your environment (e.g. NEXT_PUBLIC_API_URL=http://localhost:8080 in .env.local).",
  );
}
