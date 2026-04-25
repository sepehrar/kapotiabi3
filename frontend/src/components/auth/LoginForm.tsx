"use client";

import { EyeIcon, EyeSlashIcon } from "@heroicons/react/24/outline";
import Image from "next/image";
import Link from "next/link";
import { FormEvent, useState } from "react";
import { useTranslations } from "next-intl";
import { usePathname, useRouter } from "next/navigation";
import SectionTitle from "../ui/SectionTitle";
import { validateLoginForm, type LoginFormData } from "@/lib/validations/auth";

type LoginApiResponse = {
  token: string;
  expiresAt: string;
  role: string;
  userId: number;
  shopStatus: string | null;
};

function normalizeRole(role: string | null): "CAR_OWNER" | "SHOP_OWNER" | null {
  if (role === "CAR_OWNER" || role === "car_owner") {
    return "CAR_OWNER";
  }

  if (role === "SHOP_OWNER" || role === "shop_owner") {
    return "SHOP_OWNER";
  }

  return null;
}

export default function LoginForm() {
  const t = useTranslations("login");
  const router = useRouter();
  const pathname = usePathname();

  const localeFromPath = pathname.split("/")[1];
  const currentLocale = localeFromPath === "et" ? "et" : "en";

  const [form, setForm] = useState<LoginFormData>({
    email: "",
    password: "",
  });

  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;

    setForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setError("");

    const validationErrorKey = validateLoginForm(form);
    if (validationErrorKey) {
      setError(t(`errors.${validationErrorKey}`));
      return;
    }

    try {
      setLoading(true);

      const apiUrl = process.env.NEXT_PUBLIC_API_URL;
      if (!apiUrl) {
        throw new Error("NEXT_PUBLIC_API_URL is not configured.");
      }

      const response = await fetch(`${apiUrl}/api/v1/auth/login`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          email: form.email.trim(),
          password: form.password,
        }),
      });

      const data = (await response.json().catch(() => null)) as
        | LoginApiResponse
        | { message?: string; error?: string }
        | null;

      if (!response.ok) {
        const message =
          (data && "message" in data && data.message) ||
          (data && "error" in data && data.error) ||
          t("errors.generic");

        throw new Error(message);
      }

      if (data && "token" in data && data.token) {
        const normalizedRole = normalizeRole(data.role);

        sessionStorage.setItem("authToken", data.token);
        sessionStorage.setItem("authRole", data.role);
        window.dispatchEvent(new Event("auth-changed"));

        if (normalizedRole === "CAR_OWNER") {
          router.push(`/${currentLocale}/account`);
          return;
        }

        if (normalizedRole === "SHOP_OWNER") {
          router.push(`/${currentLocale}/business`);
          return;
        }
      }

      router.push(`/${currentLocale}`);
    } catch (err) {
      setError(err instanceof Error ? err.message : t("errors.generic"));
    } finally {
      setLoading(false);
    }
  };

  const inputClassName =
    "block w-full border-0 border-b border-white/30 bg-transparent px-2 pb-3 pt-2 text-base text-white placeholder:text-gray-500 caret-[var(--accent)] focus:border-[var(--accent)] focus:outline-none focus:ring-0";

  return (
    <section className="bg-black">
      <div className="mx-auto grid max-w-7xl lg:grid-cols-2">
        <div className="flex items-start justify-center px-6 pb-20 sm:pt-20 lg:justify-start lg:px-8 lg:pb-52">
          <div className="w-full max-w-xl lg:max-w-lg">
            <SectionTitle title={t("title")} description={t("description")} />

            <form onSubmit={handleSubmit} className="mt-12" noValidate>
              <div className="grid grid-cols-1 gap-y-8">
                <div>
                  <label htmlFor="email" className="sr-only">
                    {t("fields.email.label")}
                  </label>
                  <input
                    id="email"
                    name="email"
                    type="email"
                    autoComplete="email"
                    placeholder={t("fields.email.placeholder")}
                    value={form.email}
                    onChange={handleChange}
                    className={inputClassName}
                  />
                </div>

                <div>
                  <label htmlFor="password" className="sr-only">
                    {t("fields.password.label")}
                  </label>

                  <div className="relative">
                    <input
                      id="password"
                      name="password"
                      type={showPassword ? "text" : "password"}
                      autoComplete="current-password"
                      placeholder={t("fields.password.placeholder")}
                      value={form.password}
                      onChange={handleChange}
                      className={`${inputClassName} pr-10`}
                    />

                    <button
                      type="button"
                      onClick={() => setShowPassword((prev) => !prev)}
                      aria-label={
                        showPassword
                          ? t("fields.password.hide")
                          : t("fields.password.show")
                      }
                      className="absolute right-2 top-1/2 -translate-y-1/2 text-gray-400 transition hover:text-white"
                    >
                      {showPassword ? (
                        <EyeSlashIcon className="size-5" />
                      ) : (
                        <EyeIcon className="size-5" />
                      )}
                    </button>
                  </div>
                </div>
              </div>

              {error && (
                <p
                  className="mt-4 text-sm font-medium text-red-400"
                  role="alert"
                >
                  {error}
                </p>
              )}

              <div className="pt-8">
                <button
                  type="submit"
                  disabled={loading}
                  className="inline-flex w-full items-center justify-center rounded-full bg-(--accent) px-4 py-3 text-sm font-semibold text-(--accent-foreground) transition hover:bg-(--accent-hover) disabled:cursor-not-allowed disabled:opacity-50"
                >
                  {loading ? t("actions.submitting") : t("actions.submit")}
                </button>
              </div>
            </form>

            <p className="mt-6 text-sm text-gray-400">
              {t("links.registerPrompt")}{" "}
              <Link
                href={`/${currentLocale}/register`}
                className="font-semibold text-white hover:text-(--accent)"
              >
                {t("links.register")}
              </Link>
            </p>
          </div>
        </div>

        <div className="relative min-h-40 hidden lg:block">
          <Image
            alt="Car workshop"
            src="/img/vidhi00.jpg"
            fill
            priority
            sizes="(min-width: 1024px) 50vw, 100vw"
            className="object-cover"
          />
        </div>
      </div>
    </section>
  );
}
