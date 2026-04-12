import Image from "next/image";
import { useTranslations } from "next-intl";
import AccentButton from "../ui/AccentButton";

export default function CTA() {
  const t = useTranslations("cta");

  return (
    <div className="pt-24 sm:pt-32 bg-white">
      <section aria-labelledby="cta-heading">
        <div className="overflow-hidden pt-32 sm:pt-14">
          <div className="bg-black">
            <div className="mx-auto  max-w-7xl px-6 lg:px-8">
              <div className="relative pb-16 pt-30 sm:pb-24">
                <div className="max-w-xl">
                  <h2
                    id="cta-heading"
                    className="text-4xl font-light tracking-tight text-white text-balance"
                  >
                    {t("title")}
                  </h2>

                  <p className="mt-12 text-base text-white/70 max-w-lg text-balance">
                    {t("description")}
                  </p>

                  <AccentButton
                    href="/register"
                    className="mt-8 px-8 py-3 text-base"
                  >
                    {t("button")}
                  </AccentButton>
                </div>

                <div className="absolute -top-32 left-1/2 -translate-x-1/2 transform sm:top-6 sm:translate-x-0">
                  <div className="ml-24 flex min-w-max space-x-6 sm:ml-3 lg:space-x-8">
                    <div className="flex space-x-6 sm:flex-col sm:space-x-0 sm:space-y-6 lg:space-y-8">
                      <div className="shrink-0">
                        <Image
                          alt=""
                          width={288}
                          height={288}
                          src="/img/cta/01.jpg"
                          className="size-64  object-cover md:size-72"
                        />
                      </div>

                      <div className="mt-6 shrink-0 sm:mt-0">
                        <Image
                          alt=""
                          width={288}
                          height={288}
                          src="/img/cta/02.jpg"
                          className="size-64  object-cover md:size-72"
                        />
                      </div>
                    </div>

                    <div className="flex space-x-6 sm:-mt-20 sm:flex-col sm:space-x-0 sm:space-y-6 lg:space-y-8">
                      <div className="shrink-0">
                        <Image
                          alt=""
                          width={288}
                          height={288}
                          src="/img/cta/03.jpg"
                          className="size-64 object-cover md:size-72"
                        />
                      </div>

                      <div className="mt-6 shrink-0 sm:mt-0">
                        <Image
                          alt=""
                          width={288}
                          height={288}
                          src="/img/cta/04.jpg"
                          className="size-64  object-cover md:size-72"
                        />
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
}
