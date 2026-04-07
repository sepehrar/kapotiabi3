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
    <section aria-labelledby="faq-heading" className="bg-[#f8f8f8]">
      <div className="mx-auto w-full max-w-screen-2xl px-6 py-16 sm:px-12 lg:px-16 lg:py-24">
        <div className="mx-auto max-w-3xl text-center">
          <h2
            id="faq-heading"
            className="text-3xl font-bold tracking-tight text-black sm:text-4xl"
          >
            {t("title")}
          </h2>

          <p className="mt-4 text-base leading-7 text-black/70 sm:text-lg">
            {t("description")}
          </p>
        </div>

        <div className="mx-auto mt-12 max-w-4xl space-y-4">
          {items.map((item) => (
            <details
              key={item.question}
              className="rounded-3xl bg-white p-6 shadow-sm ring-1 ring-black/5"
            >
              <summary className="cursor-pointer list-none text-left text-lg font-semibold text-black">
                {item.question}
              </summary>
              <p className="mt-4 text-base leading-7 text-black/70">
                {item.answer}
              </p>
            </details>
          ))}
        </div>
      </div>
    </section>
  );
}
