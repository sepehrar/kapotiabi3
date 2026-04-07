// frontend/src/app/[locale]/layout.tsx
import type { Metadata } from "next";
import { Montserrat } from "next/font/google";
import { NextIntlClientProvider } from "next-intl";
import { getMessages } from "next-intl/server";
import { routing } from "@/i18n/routing";
import { notFound } from "next/navigation";
import "../globals.css";
import Header from "@/components/ui/Header";
import Footer from "@/components/ui/Footer";

const montserrat = Montserrat({
  variable: "--font-montserrat",
  subsets: ["latin"],
});

export const metadata: Metadata = {
  title: "CarFix - Car Repair Service",
  description:
    "CarFix is your trusted partner for all your car repair needs. We offer a wide range of services, from routine maintenance to complex repairs, ensuring your vehicle runs smoothly and safely. Our team of experienced mechanics is dedicated to providing high-quality service and customer satisfaction. Whether you need an oil change, brake repair, or engine diagnostics, CarFix has you covered. Visit us today and experience the difference in car care!",
};

export function generateStaticParams() {
  return routing.locales.map((locale) => ({ locale }));
}

export default async function RootLayout({
  children,
  params,
}: Readonly<{
  children: React.ReactNode;
  params: Promise<{ locale: string }>;
}>) {
  const { locale } = await params;

  if (!routing.locales.includes(locale as (typeof routing.locales)[number])) {
    notFound();
  }

  const messages = await getMessages();

  return (
    <html lang={locale} className={`${montserrat.variable} h-full antialiased`}>
      <body className="min-h-full flex flex-col">
        <NextIntlClientProvider messages={messages}>
          <Header />
          {children}
          <Footer />
        </NextIntlClientProvider>
      </body>
    </html>
  );
}
