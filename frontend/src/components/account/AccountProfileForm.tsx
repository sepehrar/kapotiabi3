// AccountProfileForm.tsx — клиентский, здесь живёт useState
"use client";

import CustomSelect from "@/components/ui/CustomSelect";
import { useState } from "react";

type Labels = {
  personalInfo: string;
  preferences: string;
  security: string;
  fullName: string;
  email: string;
  phone: string;
  city: string;
  radius: string;
  partsQuality: string;
  password: string;
  passwordDescription: string;
  changePassword: string;
  save: string;
  saveDescription: string;
  placeholderFullName: string;
  placeholderEmail: string;
  placeholderPhone: string;
  placeholderCity: string;
};

const inputClass =
  "w-full border-0 border-b border-white/15 bg-transparent pb-2.5 pt-1 text-sm text-white outline-none transition placeholder:text-white/30 focus:border-white/50";

const labelClass =
  "mb-2 block text-xs font-medium uppercase tracking-widest text-white/35";

const radiusOptions = [
  { value: "5", label: "5 km" },
  { value: "10", label: "10 km" },
  { value: "20", label: "20 km" },
  { value: "30", label: "30 km" },
];

const qualityOptions = [
  { value: "oem", label: "OEM" },
  { value: "oem-eq", label: "OEM-equivalent" },
  { value: "budget", label: "Budget" },
];

export default function AccountProfileForm({ labels: l }: { labels: Labels }) {
  const [radius, setRadius] = useState("5");
  const [quality, setQuality] = useState("oem");

  return (
    <div className="space-y-16">
      {/* Личные данные */}
      <div>
        <p className="mb-8 text-xs font-medium uppercase tracking-widest text-white/30">
          {l.personalInfo}
        </p>
        <div className="grid gap-x-12 gap-y-8 sm:grid-cols-2">
          <div className="sm:col-span-2">
            <label className={labelClass}>{l.fullName}</label>
            <input
              type="text"
              defaultValue="Tom Cook"
              placeholder={l.placeholderFullName}
              className={inputClass}
            />
          </div>
          <div>
            <label className={labelClass}>{l.email}</label>
            <input
              type="email"
              defaultValue="tom@example.com"
              placeholder={l.placeholderEmail}
              className={inputClass}
            />
          </div>
          <div>
            <label className={labelClass}>{l.phone}</label>
            <input
              type="tel"
              defaultValue="+372 5555 1234"
              placeholder={l.placeholderPhone}
              className={inputClass}
            />
          </div>
        </div>
      </div>

      {/* Предпочтения */}
      <div>
        <p className="mb-8 text-xs font-medium uppercase tracking-widest text-white/30">
          {l.preferences}
        </p>
        <div className="grid gap-x-12 gap-y-8 sm:grid-cols-3">
          <div>
            <label className={labelClass}>{l.city}</label>
            <input
              type="text"
              defaultValue="Tallinn"
              placeholder={l.placeholderCity}
              className={inputClass}
            />
          </div>
          <div>
            <label className={labelClass}>{l.radius}</label>
            <CustomSelect
              options={radiusOptions}
              value={radius}
              onChange={setRadius}
            />
          </div>
          <div>
            <label className={labelClass}>{l.partsQuality}</label>
            <CustomSelect
              options={qualityOptions}
              value={quality}
              onChange={setQuality}
            />
          </div>
        </div>
      </div>

      {/* Безопасность */}
      <div>
        <p className="mb-8 text-xs font-medium uppercase tracking-widest text-white/30">
          {l.security}
        </p>
        <div className="flex items-center justify-between border-b border-white/10 pb-6">
          <div>
            <p className="text-sm font-medium text-white">{l.password}</p>
            <p className="mt-1 text-xs text-white/40">
              {l.passwordDescription}
            </p>
          </div>
          <button
            type="button"
            className="shrink-0 rounded-full border border-white/15 px-5 py-2.5 text-sm text-white/70 transition hover:border-white/35 hover:text-white"
          >
            {l.changePassword}
          </button>
        </div>
      </div>

      {/* Сохранить */}
      <div className="flex items-center justify-between">
        <p className="text-sm text-white/40">{l.saveDescription}</p>
        <button
          type="button"
          className="shrink-0 rounded-full bg-(--accent) px-8 py-3 text-sm font-semibold text-(--accent-foreground) transition hover:bg-(--accent-hover)"
        >
          {l.save}
        </button>
      </div>
    </div>
  );
}
