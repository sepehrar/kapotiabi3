import AccountProfileView from "@/components/account/AccountProfileView";
import { getTranslations } from "next-intl/server";

export default async function AccountProfilePage() {
  const t = await getTranslations("accountProfile");

  return <AccountProfileView t={t} />;
}
