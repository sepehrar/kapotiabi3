import { useTranslations } from "next-intl";

export default function FAQ() {
  const t = useTranslations("faq");

  const items = [
    {
      question: t("item1Question"),
      answer: t("item1Answer"),
    },
    {
      question: t("item2Question"),
      answer: t("item2Answer"),
    },
    {
      question: t("item3Question"),
      answer: t("item3Answer"),
    },
    {
      question: t("item4Question"),
      answer: t("item4Answer"),
    },
    {
      question: t("item5Question"),
      answer: t("item5Answer"),
    },
  ];

  return (
    <section aria-labelledby="faq-heading" className="bg-white">
      <div className="mx-auto max-w-7xl px-6 lg:px-8 flex justify-between gap-12 pt-20 pb-5">
        <div className="mx-auto text-left pt-6">
          <h2
            id="faq-heading"
            className="text-3xl font-bold tracking-tight text-black sm:text-4xl text-balance"
          >
            {t("title")}
          </h2>

          <p className="mt-4 text-base leading-7 text-black/70 sm:text-lg text-balance">
            {t("description")}
          </p>
        </div>

        <div className="mx-auto  w-full max-w-3xl space-y-4">
          {items.map((item) => (
            <details
              key={item.question}
              className=" p-6 border-b-2 border-gray-200"
            >
              <summary className="cursor-pointer list-none text-left text-lg font-semibold text-black hover:text-(--accent) transition-colors -mt-2 text-balance">
                {item.question}
              </summary>
              <p className="mt-4 text-base leading-7 text-black/70 text-balance">
                {item.answer}
              </p>
            </details>
          ))}
        </div>
      </div>
    </section>
  );
}
