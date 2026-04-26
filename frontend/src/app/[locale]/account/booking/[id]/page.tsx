import { getTranslations } from "next-intl/server";
import BookingView from "@/components/booking/BookingView";

type BookingPageProps = {
  params: Promise<{
    locale: string;
    id: string;
  }>;
};

export default async function BookingPage({ params }: BookingPageProps) {
  const { id } = await params;
  const t = await getTranslations("booking");

  return (
    <BookingView
      bookingId={id}
      labels={{
        title: t("title"),
        description: t("description"),
        actions: {
          backToCompare: t("actions.backToCompare"),
        },
        sections: {
          request: t("sections.request"),
          shopMessage: t("sections.shopMessage"),
        },
        fields: {
          service: t("fields.service"),
          car: t("fields.car"),
          parts: t("fields.parts"),
          price: t("fields.price"),
        },
        form: {
          selectDate: t("form.selectDate"),
          availableTimes: t("form.availableTimes"),
          messageToShop: t("form.messageToShop"),
          optional: t("form.optional"),
          messagePlaceholder: t("form.messagePlaceholder"),
          selectDateAndTime: t("form.selectDateAndTime"),
          confirmBooking: t("form.confirmBooking"),
          reviewTitle: t("form.reviewTitle"),
          reviewDescription: t("form.reviewDescription"),
          cancel: t("form.cancel"),
          confirmFinal: t("form.confirmFinal"),
          bookingConfirmed: t("form.bookingConfirmed"),
          redirecting: t("form.redirecting"),
          bookingFailed: t("form.bookingFailed"),
        },
      }}
    />
  );
}
