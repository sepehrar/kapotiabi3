"use client";

import { useState } from "react";
import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import { useTranslations } from "next-intl";
import {
  Dialog,
  DialogBackdrop,
  DialogPanel,
  TransitionChild,
} from "@headlessui/react";
import {
  Bars3Icon,
  BookmarkIcon,
  Cog6ToothIcon,
  HomeIcon,
  MagnifyingGlassIcon,
  ScaleIcon,
  BellIcon,
  XMarkIcon,
  ArrowRightOnRectangleIcon,
} from "@heroicons/react/24/outline";
import LocaleSwitcher from "@/components/ui/LocaleSwitcher";

const navigation = [
  { key: "dashboard", href: "/account", icon: HomeIcon },
  { key: "saved", href: "/account/saved", icon: BookmarkIcon },
  { key: "compare", href: "/account/compare", icon: ScaleIcon },
  { key: "searches", href: "/account/searches", icon: MagnifyingGlassIcon },
  { key: "notifications", href: "/account/notifications", icon: BellIcon },
  { key: "profile", href: "/account/profile", icon: Cog6ToothIcon },
];

function classNames(...classes: string[]) {
  return classes.filter(Boolean).join(" ");
}

function isActivePath(pathname: string, href: string) {
  const normalizedPath = pathname.replace(/^\/[a-z]{2}(?=\/|$)/, "");

  if (href === "/account") {
    return normalizedPath === "/account";
  }

  return normalizedPath === href || normalizedPath.startsWith(`${href}/`);
}

type SidebarContentProps = {
  pathname: string;
  currentLocale: "en" | "et";
  t: ReturnType<typeof useTranslations<"accountSidebar">>;
  logoutT: ReturnType<typeof useTranslations<"authMenu">>;
  onLogout: () => void;
  onNavigate?: () => void;
};

function SidebarContent({
  pathname,
  currentLocale,
  t,
  logoutT,
  onLogout,
  onNavigate,
}: SidebarContentProps) {
  return (
    <>
      <div className="relative flex h-16 shrink-0 items-center justify-between">
        <div className="flex items-center gap-3">
          <Link
            href={`/${currentLocale}`}
            className="-m-1.5 flex items-center gap-2 p-1.5"
          >
            <span className="sr-only">{t("brand")}</span>
            <span className="text-xl font-bold text-white">KapotiAbi</span>
          </Link>
          <div className="h-2 w-2 rounded-full bg-(--accent)"></div>
          <div>
            <p className="text-base font-light text-white">{t("title")}</p>
          </div>
        </div>
      </div>

      <div className="ml-4">
        <LocaleSwitcher />
      </div>

      <nav className="relative flex flex-1 flex-col">
        <ul role="list" className="flex flex-1 flex-col gap-y-2">
          <li>
            <ul role="list" className="-mx-2 space-y-0.5">
              {navigation.map((item) => {
                const active = isActivePath(pathname, item.href);

                return (
                  <li key={item.key}>
                    <Link
                      href={`/${currentLocale}${item.href}`}
                      onClick={onNavigate}
                      aria-current={active ? "page" : undefined}
                      className={classNames(
                        active
                          ? "bg-(--accent) text-(--accent-foreground)"
                          : "text-white/70 hover:bg-(--pills-background) hover:text-white",
                        "group flex items-center gap-x-3 rounded-2xl px-3 py-3 text-sm transition",
                      )}
                    >
                      <item.icon
                        aria-hidden="true"
                        className={classNames(
                          active
                            ? "text-(--accent-foreground)"
                            : "text-white/40 group-hover:text-white",
                          "size-4 shrink-0 transition",
                        )}
                      />
                      {t(`items.${item.key}`)}
                    </Link>
                  </li>
                );
              })}

              <li>
                <button
                  type="button"
                  onClick={onLogout}
                  className="group flex w-full items-center gap-x-3 rounded-2xl px-3 py-3 text-sm text-white/70 transition hover:bg-(--pills-background) hover:text-white"
                >
                  <ArrowRightOnRectangleIcon
                    aria-hidden="true"
                    className="size-4 shrink-0 text-white/40 transition group-hover:text-white"
                  />
                  {logoutT("logout")}
                </button>
              </li>
            </ul>
          </li>

          <li className="mt-auto mb-4">
            <div className="text-center">
              <p className="mb-4 text-base font-semibold text-white">
                {t("card.title")}
              </p>
              <p className="mb-4 w-50 text-balance text-sm leading-6 text-white/60">
                {t("card.description")}
              </p>

              <Link
                href={`/${currentLocale}/search`}
                className="shrink-0 rounded-full bg-(--pills-background) px-5 py-3 text-sm shadow ring-1 ring-inset ring-white/10 transition-colors duration-300 hover:bg-(--accent)/40 hover:ring-(--accent)"
              >
                {t("card.button")}
              </Link>
            </div>
          </li>
        </ul>
      </nav>
    </>
  );
}

export default function AccountSidebar() {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const pathname = usePathname();
  const router = useRouter();
  const t = useTranslations("accountSidebar");
  const logoutT = useTranslations("authMenu");

  const localeFromPath = pathname.split("/")[1];
  const currentLocale: "en" | "et" = localeFromPath === "et" ? "et" : "en";

  const handleLogout = () => {
    sessionStorage.removeItem("authToken");
    sessionStorage.removeItem("authRole");
    window.dispatchEvent(new Event("auth-changed"));
    setSidebarOpen(false);
    router.push(`/${currentLocale}`);
  };

  return (
    <>
      <Dialog
        open={sidebarOpen}
        onClose={setSidebarOpen}
        className="relative z-50 lg:hidden"
      >
        <DialogBackdrop
          transition
          className="fixed inset-0 bg-black transition-opacity duration-300 ease-linear data-closed:opacity-0"
        />

        <div className="fixed inset-0 flex">
          <DialogPanel
            transition
            className="relative mr-16 flex w-full max-w-xs flex-1 transform transition duration-300 ease-in-out data-closed:-translate-x-full"
          >
            <TransitionChild>
              <div className="absolute left-full top-0 flex w-16 justify-center pt-5 duration-300 ease-in-out data-closed:opacity-0">
                <button
                  type="button"
                  onClick={() => setSidebarOpen(false)}
                  className="-m-2.5 p-2.5"
                >
                  <span className="sr-only">{t("closeSidebar")}</span>
                  <XMarkIcon aria-hidden="true" className="size-6 text-white" />
                </button>
              </div>
            </TransitionChild>

            <div className="relative flex grow flex-col gap-y-2 overflow-y-auto border-r border-white/10 px-6 pb-6 before:pointer-events-none before:absolute before:inset-0 before:bg-black/20">
              <SidebarContent
                pathname={pathname}
                currentLocale={currentLocale}
                t={t}
                logoutT={logoutT}
                onLogout={handleLogout}
                onNavigate={() => setSidebarOpen(false)}
              />
            </div>
          </DialogPanel>
        </div>
      </Dialog>

      <div className="sticky top-0 z-40 flex items-center gap-x-4 border-b border-black/10 bg-white px-4 py-4 sm:px-6 lg:hidden">
        <button
          type="button"
          onClick={() => setSidebarOpen(true)}
          className="-m-2.5 p-2.5 text-black/70"
        >
          <span className="sr-only">{t("openSidebar")}</span>
          <Bars3Icon aria-hidden="true" className="size-6" />
        </button>

        <div className="flex-1 text-sm font-semibold text-white">
          {t("title")}
        </div>

        <LocaleSwitcher />
      </div>

      <aside className="hidden lg:fixed lg:inset-y-0 lg:z-40 lg:flex lg:w-72 lg:flex-col">
        <div className="relative flex grow flex-col gap-y-5 overflow-y-auto border-r border-white/10 bg-black px-6 pb-6 before:pointer-events-none before:absolute before:inset-0 before:bg-black/20">
          <SidebarContent
            pathname={pathname}
            currentLocale={currentLocale}
            t={t}
            logoutT={logoutT}
            onLogout={handleLogout}
          />
        </div>
      </aside>
    </>
  );
}
