"use client";

import { type ReactNode, useEffect } from "react";
import { usePathname, useRouter } from "next/navigation";

type UserRole = "CAR_OWNER" | "SHOP_OWNER";

type AuthRouteGuardProps = {
  children: ReactNode;
  requiredRole: UserRole;
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

function getLocaleFromPath(pathname: string) {
  const locale = pathname.split("/")[1];
  return locale === "et" ? "et" : "en";
}

function getRoleHome(locale: string, role: UserRole) {
  return role === "CAR_OWNER" ? `/${locale}/account` : `/${locale}/business`;
}

export default function AuthRouteGuard({
  children,
  requiredRole,
}: AuthRouteGuardProps) {
  const pathname = usePathname();
  const router = useRouter();
  const locale = getLocaleFromPath(pathname);
  const token =
    typeof window === "undefined" ? null : sessionStorage.getItem("authToken");
  const role =
    typeof window === "undefined"
      ? null
      : normalizeRole(sessionStorage.getItem("authRole"));

  useEffect(() => {
    if (!token || !role) {
      router.replace(`/${locale}/login`);
      return;
    }

    if (role !== requiredRole) {
      router.replace(getRoleHome(locale, role));
    }
  }, [locale, requiredRole, role, router, token]);

  if (!token || !role || role !== requiredRole) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-black text-sm text-white/50">
        Loading...
      </div>
    );
  }

  return children;
}
