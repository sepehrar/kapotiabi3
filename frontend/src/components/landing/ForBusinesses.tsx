import { useTranslations } from "next-intl";
import Image from "next/image";
export default function ForBusinesses() {
  const t = useTranslations("forBusinesses");

  const items = [
    {
      title: t("item1Title"),
      description: t("item1Description"),
      img: "/img/bussines/h01.jpg",
      bg: "bg-gradient-to-br from-[#c0340a] via-[#7a1a05] to-[#1a0800]",
    },
    {
      title: t("item2Title"),
      description: t("item2Description"),
      img: "/img/bussines/h02.jpg",
      bg: "bg-gradient-to-br from-[#E8520A] via-[#a33205] to-[#0d0d0d]",
    },
    {
      title: t("item3Title"),
      description: t("item3Description"),
      img: "/img/bussines/h03.jpg",
      bg: "bg-gradient-to-br from-[#3a1500] via-[#8b2e06] to-[#E8520A]",
    },
    {
      title: t("item4Title"),
      description: t("item4Description"),
      img: "/img/bussines/h04.jpg",
      bg: "bg-gradient-to-br from-[#1a0800] via-[#7a1a05] to-[#c0340a]",
    },
  ];

  return (
    <section
      aria-labelledby="for-businesses-heading"
      className="bg-black py-24 "
    >
      <div className="mx-auto w-full max-w-7xl px-6 lg:px-8">
        {/* Header */}
        <div className="mx-auto max-w-2xl text-center mb-20">
          <h2
            id="for-businesses-heading"
            className="text-2xl tracking-tight text-white sm:text-3xl  font-light text-balance"
          >
            {t("title")}
          </h2>
          <p className="mt-4 text-base leading-7 text-white/70 sm:text-lg max-w-md mx-auto text-balance">
            {t("description")}
          </p>
          <a
            href="#"
            className="mt-8 inline-block rounded-full bg-(--accent) px-8 py-3 font-syne text-base font-semibold  transition hover:bg-(--accent-hover)  text-(--accent-foreground)  ${className} text-nowrap ring-(--accent)  ring-1 ring-inset"
          >
            {t("button")}
          </a>
        </div>

        {/* Cards */}
        <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-4 ">
          {items.map((item, i) => (
            <div key={item.title}>
              {/* Square card */}
              <div
                className={`relative aspect-square overflow-hidden  transition-transform duration-300 hover:-translate-y-1 ${item.bg}`}
              >
                {/* Background image */}

                <Image
                  width={300}
                  height={300}
                  src={item.img}
                  alt={item.title}
                  className="absolute inset-0 h-full w-full object-cover mix-blend-overlay opacity-60"
                />

                {/* Logo */}
                <span className="absolute top-5 left-5 text-[10px] font-bold uppercase tracking-[0.15em] text-(--accent)">
                  {String(i + 1).padStart(2, "0")}
                </span>

                {/* Title bottom right */}
                <h3 className="absolute bottom-5 right-5 text-lg text-white text-right max-w-[70%] text-balance font-bold">
                  {item.title}
                </h3>
              </div>

              {/* Description below card */}
              <p className="mt-6 text-sm  text-white/70 px-1 text-balance ">
                {item.description}
              </p>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}
