// frontend/next.config.ts
import { dirname } from "node:path";
import { fileURLToPath } from "node:url";
import type { NextConfig } from "next";
import createNextIntlPlugin from "next-intl/plugin";

const withNextIntl = createNextIntlPlugin("./src/i18n/request.ts");
const configDir = dirname(fileURLToPath(import.meta.url));

const nextConfig: NextConfig = {
  turbopack: {
    root: configDir,
  },
  images: {
    remotePatterns: [
      {
        protocol: "https",
        hostname: "tailwindcss.com",
      },
    ],
  },
};

export default withNextIntl(nextConfig);
