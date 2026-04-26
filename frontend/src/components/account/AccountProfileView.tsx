// AccountProfileView.tsx — остаётся серверным, без "use client"
import AccountProfileForm from "./AccountProfileForm";

type AccountProfileViewProps = {
  t: (key: string) => string;
};

export default function AccountProfileView({ t }: AccountProfileViewProps) {
  return (
    <div className="mx-auto w-full max-w-6xl">
      <div className="mb-10">
        <h1 className="text-3xl font-bold text-white">{t("title")}</h1>
        <p className="mt-2 text-sm text-white/40">{t("description")}</p>
      </div>

      <AccountProfileForm
        labels={{
          personalInfo: t("sections.personalInfo"),
          preferences: t("sections.preferences"),
          security: t("sections.security"),
          fullName: t("fields.fullName"),
          email: t("fields.email"),
          phone: t("fields.phone"),
          city: t("fields.city"),
          radius: t("fields.radius"),
          partsQuality: t("fields.partsQuality"),
          password: t("security.password"),
          passwordDescription: t("security.passwordDescription"),
          changePassword: t("actions.changePassword"),
          save: t("actions.save"),
          saveDescription: t("saveDescription"),
          placeholderFullName: t("placeholders.fullName"),
          placeholderEmail: t("placeholders.email"),
          placeholderPhone: t("placeholders.phone"),
          placeholderCity: t("placeholders.city"),
        }}
      />
    </div>
  );
}
