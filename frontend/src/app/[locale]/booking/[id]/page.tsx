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

  return <BookingView bookingId={id} t={t} />;
}
