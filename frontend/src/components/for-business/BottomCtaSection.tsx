import Link from "next/link";
import { useTranslations } from "next-intl";

export default function BottomCtaSection() {
  const t = useTranslations("businessLanding.cta");

  return (
    <section className="py-24 px-6 ">
      <div className="mx-auto max-w-3xl text-center">
        <h2 className="text-3xl font-light tracking-tight text-white text-balance sm:text-4xl">
          {t("title")}
        </h2>

        <p className="mt-6 text-lg text-white/50 text-balance">
          {t("description")}
        </p>

        <div className="mt-10 flex flex-col sm:flex-row gap-4 justify-center ">
          <Link
            href="/business/register"
            className="inline-flex items-center justify-center rounded-full bg-(--accent) px-8 py-4 text-sm font-semibold text-(--accent-foreground) ring-1 ring-inset ring-(--accent) transition hover:bg-(--accent-hover)"
          >
            {t("button")}
          </Link>
        </div>
      </div>
    </section>
  );
}
