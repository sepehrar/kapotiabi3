import {
  ChatBubbleOvalLeftEllipsisIcon,
  HeartIcon,
  PencilSquareIcon,
  TrashIcon,
} from "@heroicons/react/24/outline";
import { useTranslations } from "next-intl";

const icons = [
  TrashIcon,
  PencilSquareIcon,
  ChatBubbleOvalLeftEllipsisIcon,
  HeartIcon,
];
import Image from "next/image";

export default function WhatCompare() {
  const t = useTranslations("whatYouCanCompare");

  const items = [
    { title: t("item1Title"), description: t("item1Description") },
    { title: t("item2Title"), description: t("item2Description") },
    { title: t("item3Title"), description: t("item3Description") },
    { title: t("item4Title"), description: t("item4Description") },
  ];

  return (
    <section className="bg-black py-14 ">
      <div className="mx-auto max-w-7xl px-6 lg:px-8">
        <div className="flex flex-row items-start gap-x-16 gap-y-20 justify-between">
          {/* Left: heading — 2 columns */}
          <div className="maw-w-80 relative">
            <Image
              src="/img/compare.jpg"
              alt=""
              width={500}
              height={700}
              className="max-w-80 "
            />{" "}
            <h2 className="text-2xl tracking-tight text-white sm:text-3xl text-balance font-light absolute top-52 -left-15 z-10 max-w-80">
              {t("title")}
            </h2>
          </div>

          {/* Right: feature grid — 3 columns */}
          <dl className="col-span-1 grid grid-cols-1 gap-x-8 gap-y-12 sm:grid-cols-2 ">
            {items.map((item, i) => {
              const Icon = icons[i];
              return (
                <div key={item.title} className="group max-w-80 ">
                  <dt className="text-xl font-semibold text-white group-hover:text-(--accent) transition-colors duration-300">
                    <div className="mb-5 flex size-10 items-center justify-center rounded-lg bg-(--accent-hover)/20 border border-transparent group-hover:border-(--accent) transition-colors duration-300">
                      <Icon
                        className="size-5 text-(--accent)"
                        aria-hidden="true"
                      />
                    </div>
                    {item.title}
                  </dt>
                  <dd className="mt-3 text-base leading-7 text-white/70">
                    {item.description}
                  </dd>
                </div>
              );
            })}
          </dl>
        </div>
      </div>
    </section>
  );
}
