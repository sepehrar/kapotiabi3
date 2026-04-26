// import { getTranslations } from "next-intl/server";
import AccountBookingsView from "@/components/account/AccountBookingsView";

export default async function AccountBookingsPage() {
  //   const t = await getTranslations("accountBookings");

  return <AccountBookingsView />;
}
