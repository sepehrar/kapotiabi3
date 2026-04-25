"use client";

import { useEffect, useState } from "react";
import { Dialog, DialogPanel } from "@headlessui/react";
import { Bars3Icon, XMarkIcon } from "@heroicons/react/24/outline";
import { useTranslations } from "next-intl";
import { usePathname, useRouter } from "next/navigation";
import LocaleSwitcher from "@/components/ui/LocaleSwitcher";
import AuthMenu from "@/components/ui/AuthMenu";
import Link from "next/link";
import AccentButton from "@/components/ui/AccentButton";

type UserRole = "CAR_OWNER" | "SHOP_OWNER";

type AuthState = {
  token: string | null;
  role: UserRole | null;
};

function normalizeRole(role: string | null): UserRole | null {
  if (role === "CAR_OWNER" || role === "car_owner") {
    return "CAR_OWNER";
  }

  if (role === "SHOP_OWNER" || role === "shop_owner") {
    return "SHOP_OWNER";
  }

  return null;
}

function readAuthState(): AuthState {
  if (typeof window === "undefined") {
    return {
      token: null,
      role: null,
    };
  }

  return {
    token: sessionStorage.getItem("authToken"),
    role: normalizeRole(sessionStorage.getItem("authRole")),
  };
}

export default function Header() {
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const [authState, setAuthState] = useState<AuthState>(() => readAuthState());

  const t = useTranslations("header");
  const router = useRouter();
  const pathname = usePathname();

  const localeFromPath = pathname.split("/")[1];
  const currentLocale = localeFromPath === "et" ? "et" : "en";

  useEffect(() => {
    const syncAuthState = () => {
      setAuthState(readAuthState());
    };

    syncAuthState();

    window.addEventListener("auth-changed", syncAuthState);
    window.addEventListener("storage", syncAuthState);

    return () => {
      window.removeEventListener("auth-changed", syncAuthState);
      window.removeEventListener("storage", syncAuthState);
    };
  }, [pathname]);

  const authToken = authState.token;
  const authRole = authState.role;
  const isAuthenticated = Boolean(authToken && authRole);

  const handleLogout = () => {
    sessionStorage.removeItem("authToken");
    sessionStorage.removeItem("authRole");
    window.dispatchEvent(new Event("auth-changed"));
    setMobileMenuOpen(false);
    router.push(`/${currentLocale}`);
  };

  const navigation = [
    { name: t("nav.findService"), href: "#" },
    { name: t("nav.categories"), href: `/${currentLocale}/categories` },
    { name: t("nav.howItWorks"), href: "#" },
    { name: t("nav.forBusinesses"), href: "#" },
  ];

  return (
    <div className="bg-black">
      <header>
        <nav
          aria-label="Global"
          className="mx-auto flex max-w-7xl items-center justify-between p-6 lg:px-8"
        >
          <div className="flex lg:flex-1">
            <Link
              href={`/${currentLocale}`}
              className="-m-1.5 flex items-center gap-2 p-1.5"
            >
              <span className="sr-only">{t("brand")}</span>
              <span className="text-xl font-bold text-white">{t("brand")}</span>
            </Link>
          </div>

          <div className="flex lg:hidden">
            <button
              type="button"
              onClick={() => setMobileMenuOpen(true)}
              className="-m-2.5 inline-flex items-center justify-center rounded-md p-2.5 text-white"
            >
              <span className="sr-only">{t("openMenu")}</span>
              <Bars3Icon aria-hidden="true" className="size-6" />
            </button>
          </div>

          <div className="hidden lg:flex lg:gap-x-6">
            {navigation.map((item) => (
              <a
                key={item.name}
                href={item.href}
                className="text-sm text-white hover:text-(--accent)"
              >
                {item.name}
              </a>
            ))}
          </div>

          <div className="hidden lg:flex lg:flex-1 lg:items-center lg:justify-end lg:gap-x-6">
            {isAuthenticated && authRole ? (
              <AuthMenu role={authRole} onLogout={handleLogout} />
            ) : (
              <Link
                href={`/${currentLocale}/login`}
                className="text-sm text-white hover:text-(--accent)"
              >
                {t("login")}
              </Link>
            )}

            <AccentButton href={`/${currentLocale}/search`}>
              {t("startSearch")}
            </AccentButton>
            <LocaleSwitcher />
          </div>
        </nav>

        <Dialog
          open={mobileMenuOpen}
          onClose={setMobileMenuOpen}
          className="lg:hidden"
        >
          <div className="fixed inset-0 z-50 bg-black" />
          <DialogPanel className="fixed inset-y-0 right-0 z-50 w-full overflow-y-auto bg-black p-6 sm:max-w-sm">
            <div className="flex items-center justify-between">
              <Link
                href={`/${currentLocale}`}
                className="-m-1.5 flex items-center gap-2 p-1.5"
              >
                <span className="sr-only">{t("brand")}</span>
                <span className="text-2xl font-bold text-white">
                  {t("brand")}
                </span>
              </Link>

              <button
                type="button"
                onClick={() => setMobileMenuOpen(false)}
                className="-m-2.5 rounded-md p-2.5 text-white"
              >
                <span className="sr-only">{t("closeMenu")}</span>
                <XMarkIcon aria-hidden="true" className="size-6" />
              </button>
            </div>

            <div className="mt-6 flow-root">
              <div className="-my-6 divide-y divide-gray-200">
                <div className="space-y-1 py-6">
                  {navigation.map((item) => (
                    <a
                      key={item.name}
                      href={item.href}
                      className="-mx-3 block rounded-lg px-2 py-2 text-base font-semibold text-white hover:text-(--accent)"
                    >
                      {item.name}
                    </a>
                  ))}
                </div>

                <div className="space-y-2 py-6">
                  <LocaleSwitcher />

                  {isAuthenticated && authRole ? (
                    <>
                      <Link
                        href={
                          authRole === "CAR_OWNER"
                            ? `/${currentLocale}/account`
                            : `/${currentLocale}/business`
                        }
                        className="-mx-3 block rounded-lg px-3 py-2.5 text-base font-semibold text-white hover:text-(--accent)"
                        onClick={() => setMobileMenuOpen(false)}
                      >
                        {authRole === "CAR_OWNER" ? "My account" : "Business"}
                      </Link>

                      <button
                        type="button"
                        onClick={handleLogout}
                        className="-mx-3 block w-full rounded-lg px-3 py-2.5 text-left text-base font-semibold text-white hover:text-(--accent)"
                      >
                        Log out
                      </button>
                    </>
                  ) : (
                    <Link
                      href={`/${currentLocale}/login`}
                      className="-mx-3 block rounded-lg px-3 py-2.5 text-base font-semibold text-white hover:text-(--accent)"
                      onClick={() => setMobileMenuOpen(false)}
                    >
                      {t("login")}
                    </Link>
                  )}

                  <AccentButton href={`/${currentLocale}/search`}>
                    {t("startSearch")}
                  </AccentButton>
                </div>
              </div>
            </div>
          </DialogPanel>
        </Dialog>
      </header>
    </div>
  );
}
