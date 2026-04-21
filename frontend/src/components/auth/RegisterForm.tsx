"use client";

import { EyeIcon, EyeSlashIcon } from "@heroicons/react/24/outline";
import Image from "next/image";
import Link from "next/link";
import { FormEvent, useState } from "react";
import { useTranslations } from "next-intl";
import SectionTitle from "../ui/SectionTitle";
import {
  validateRegisterForm,
  type RegisterFormData,
} from "@/lib/validations/auth";

type RegisterApiResponse = {
  token: string;
  expiresAt: string;
  role: string;
  userId: number;
  shopStatus: string | null;
};

export default function RegisterForm() {
  const t = useTranslations("register");

  const [form, setForm] = useState<RegisterFormData>({
    firstName: "",
    lastName: "",
    email: "",
    password: "",
    confirmPassword: "",
    phone: "",
  });

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

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
    setSuccess("");

    const validationErrorKey = validateRegisterForm(form);
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

      const response = await fetch(`${apiUrl}/api/v1/auth/register`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          firstName: form.firstName.trim(),
          lastName: form.lastName.trim(),
          email: form.email.trim(),
          password: form.password,
          phone: form.phone.trim(),
          role: "CAR_OWNER",
        }),
      });

      const data = (await response.json().catch(() => null)) as
        | RegisterApiResponse
        | { message?: string; error?: string }
        | null;

      if (!response.ok) {
        const message =
          (data && "message" in data && data.message) ||
          (data && "error" in data && data.error) ||
          t("errors.generic");

        throw new Error(message);
      }

      console.log("Register response:", data);

      // Temporary solution for now. You can decide later where auth should be stored.
      // I do not recommend localStorage as a final solution.
      // For the demo stage, sessionStorage is safer than localStorage.
      if (data && "token" in data && data.token) {
        sessionStorage.setItem("authToken", data.token);
      }

      setSuccess(t("messages.success"));
      setForm({
        firstName: "",
        lastName: "",
        email: "",
        password: "",
        confirmPassword: "",
        phone: "",
      });
      setShowPassword(false);
      setShowConfirmPassword(false);
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
        <div className="flex items-start px-6 sm:pt-20 pb-20 lg:pb-52 lg:px-8 lg:justify-start justify-center">
          <div className="w-full max-w-xl lg:max-w-lg">
            <SectionTitle title={t("title")} description={t("description")} />

            <form onSubmit={handleSubmit} className="mt-12" noValidate>
              <div className="grid grid-cols-1 gap-y-8">
                <div className="grid grid-cols-1 gap-6 sm:grid-cols-2">
                  <div>
                    <label htmlFor="firstName" className="sr-only">
                      {t("fields.firstName.label")}
                    </label>
                    <input
                      id="firstName"
                      name="firstName"
                      type="text"
                      autoComplete="given-name"
                      placeholder={t("fields.firstName.placeholder")}
                      value={form.firstName}
                      onChange={handleChange}
                      className={inputClassName}
                    />
                  </div>

                  <div>
                    <label htmlFor="lastName" className="sr-only">
                      {t("fields.lastName.label")}
                    </label>
                    <input
                      id="lastName"
                      name="lastName"
                      type="text"
                      autoComplete="family-name"
                      placeholder={t("fields.lastName.placeholder")}
                      value={form.lastName}
                      onChange={handleChange}
                      className={inputClassName}
                    />
                  </div>
                </div>

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
                  <label htmlFor="phone" className="sr-only">
                    {t("fields.phone.label")}
                  </label>
                  <input
                    id="phone"
                    name="phone"
                    type="tel"
                    autoComplete="tel"
                    placeholder={t("fields.phone.placeholder")}
                    value={form.phone}
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
                      autoComplete="new-password"
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

                <div>
                  <label htmlFor="confirmPassword" className="sr-only">
                    {t("fields.confirmPassword.label")}
                  </label>

                  <div className="relative">
                    <input
                      id="confirmPassword"
                      name="confirmPassword"
                      type={showConfirmPassword ? "text" : "password"}
                      autoComplete="new-password"
                      placeholder={t("fields.confirmPassword.placeholder")}
                      value={form.confirmPassword}
                      onChange={handleChange}
                      className={`${inputClassName} pr-10`}
                    />

                    <button
                      type="button"
                      onClick={() => setShowConfirmPassword((prev) => !prev)}
                      aria-label={
                        showConfirmPassword
                          ? t("fields.confirmPassword.hide")
                          : t("fields.confirmPassword.show")
                      }
                      className="absolute right-2 top-1/2 -translate-y-1/2 text-gray-400 transition hover:text-white"
                    >
                      {showConfirmPassword ? (
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

              {success && (
                <p
                  className="mt-4 text-sm font-medium text-green-400"
                  role="status"
                >
                  {success}
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
              {t("links.loginPrompt")}{" "}
              <Link
                href="/login"
                className="font-semibold text-white hover:text-(--accent)"
              >
                {t("links.login")}
              </Link>
            </p>
          </div>
        </div>

        <div className="relative min-h-40 lg:block hidden">
          <Image
            alt="Car workshop"
            src="/img/vidhi00.jpg"
            fill
            priority
            sizes="(min-width: 1024px) 50vw, 100vw"
            className="object-contain"
          />
        </div>
      </div>
    </section>
  );
}
