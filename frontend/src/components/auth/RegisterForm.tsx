"use client";

import Image from "next/image";
import Link from "next/link";
import { FormEvent, useState } from "react";
import { useTranslations } from "next-intl";
import SectionTitle from "../ui/SectionTitle";
import {
  validateRegisterForm,
  type RegisterFormData,
} from "@/lib/validations/auth";

export default function RegisterForm() {
  const t = useTranslations("register");

  const [form, setForm] = useState<RegisterFormData>({
    name: "",
    email: "",
    password: "",
    confirmPassword: "",
  });

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(false);

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

      console.log("Register payload:", {
        name: form.name,
        email: form.email,
        password: form.password,
      });

      setSuccess(t("messages.success"));
      setForm({
        name: "",
        email: "",
        password: "",
        confirmPassword: "",
      });
    } catch {
      setError(t("errors.generic"));
    } finally {
      setLoading(false);
    }
  };

  const inputClassName =
    "block w-full border-0 border-b border-white/30 bg-transparent px-2 pb-3 pt-2 text-base text-white placeholder:text-gray-500 caret-[var(--accent)] focus:border-[var(--accent)] focus:outline-none focus:ring-0";

  return (
    <section className="bg-black">
      <div className="mx-auto grid min-h-screen max-w-7xl lg:grid-cols-2">
        <div className="flex items-start px-6 py-8 sm:py-20 lg:px-8">
          <div className="w-full max-w-xl lg:max-w-lg">
            <SectionTitle title={t("title")} description={t("description")} />

            <form onSubmit={handleSubmit} className="mt-12" noValidate>
              <div className="grid grid-cols-1 gap-y-8">
                <div>
                  <label htmlFor="name" className="sr-only">
                    {t("fields.name.label")}
                  </label>
                  <input
                    id="name"
                    name="name"
                    type="text"
                    autoComplete="name"
                    placeholder={t("fields.name.placeholder")}
                    value={form.name}
                    onChange={handleChange}
                    className={inputClassName}
                  />
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
                  <label htmlFor="password" className="sr-only">
                    {t("fields.password.label")}
                  </label>
                  <input
                    id="password"
                    name="password"
                    type="password"
                    autoComplete="new-password"
                    placeholder={t("fields.password.placeholder")}
                    value={form.password}
                    onChange={handleChange}
                    className={inputClassName}
                  />
                </div>

                <div>
                  <label htmlFor="confirmPassword" className="sr-only">
                    {t("fields.confirmPassword.label")}
                  </label>
                  <input
                    id="confirmPassword"
                    name="confirmPassword"
                    type="password"
                    autoComplete="new-password"
                    placeholder={t("fields.confirmPassword.placeholder")}
                    value={form.confirmPassword}
                    onChange={handleChange}
                    className={inputClassName}
                  />
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

        <div className="relative min-h-60">
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
