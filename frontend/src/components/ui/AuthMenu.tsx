"use client";

import { Popover, PopoverButton, PopoverPanel } from "@headlessui/react";
import { ChevronDownIcon } from "@heroicons/react/20/solid";
import Link from "next/link";
import { useTranslations } from "next-intl";
import { usePathname } from "next/navigation";

type AuthMenuProps = {
  role: "CAR_OWNER" | "SHOP_OWNER";
  onLogout: () => void;
};

export default function AuthMenu({ role, onLogout }: AuthMenuProps) {
  const t = useTranslations("authMenu");
  const pathname = usePathname();
  const isCarOwner = role === "CAR_OWNER";

  const localeFromPath = pathname.split("/")[1];
  const currentLocale = localeFromPath === "et" ? "et" : "en";

  return (
    <Popover className="relative">
      <PopoverButton className="inline-flex items-center gap-x-1 rounded-full px-3 py-2 text-sm text-white transition hover:text-(--accent)">
        <span>{isCarOwner ? t("account") : t("business")}</span>
        <ChevronDownIcon aria-hidden="true" className="size-4" />
      </PopoverButton>

      <PopoverPanel
        anchor="bottom end"
        transition
        className="z-50 mt-2 w-48 rounded-2xl bg-gray-800 p-2 text-sm shadow transition data-closed:translate-y-1 data-closed:opacity-0 data-enter:duration-200 data-leave:duration-150"
      >
        <div className="space-y-1">
          <Link
            href={
              isCarOwner
                ? `/${currentLocale}/account`
                : `/${currentLocale}/business`
            }
            className="block rounded-full px-3 py-2 text-sm font-medium text-(--accent-foreground) transition-all duration-300 ease-out hover:bg-(--accent) hover:text-(--accent-foreground)"
          >
            {isCarOwner ? t("account") : t("businessDashboard")}
          </Link>

          <button
            type="button"
            onClick={onLogout}
            className="block w-full rounded-full px-3 py-2 text-left text-sm font-medium text-(--accent-foreground) transition-all duration-300 ease-out hover:bg-(--accent) hover:text-(--accent-foreground)"
          >
            {t("logout")}
          </button>
        </div>
      </PopoverPanel>
    </Popover>
  );
}
