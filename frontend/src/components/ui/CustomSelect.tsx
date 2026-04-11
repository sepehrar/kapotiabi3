"use client";

import { Popover, PopoverButton, PopoverPanel } from "@headlessui/react";
import { ChevronDownIcon } from "@heroicons/react/20/solid";

type SelectOption = { value: string; label: string };

type CustomSelectProps = {
  options: SelectOption[];
  value: string;
  onChange: (value: string) => void;
  className?: string;
};

export default function CustomSelect({
  options,
  value,
  onChange,
  className,
}: CustomSelectProps) {
  const selected = options.find((o) => o.value === value) ?? options[0];

  return (
    <Popover className={`relative ${className ?? ""}`}>
      <PopoverButton className="inline-flex w-full items-center justify-between border-b border-white/15 pb-2.5 pt-1 text-sm text-white outline-none transition hover:border-white/50 focus:border-white/50 data-open:border-white/50">
        <span>{selected.label}</span>
        <ChevronDownIcon className="size-4 text-white/40 transition ui-open:rotate-180" />
      </PopoverButton>

      <PopoverPanel
        anchor="bottom start"
        transition
        className="z-50 mt-2 min-w-(--button-width) rounded-2xl bg-gray-900 p-2 text-sm shadow transition data-closed:translate-y-1 data-closed:opacity-0 data-enter:duration-200 data-leave:duration-150"
      >
        {({ close }) => (
          <div className="space-y-1">
            {options.map((opt) => (
              <button
                key={opt.value}
                type="button"
                onClick={() => {
                  onChange(opt.value);
                  close();
                }}
                className={`block w-full rounded-full px-3 py-2 text-left text-sm font-medium transition-all duration-200 ${
                  opt.value === selected.value
                    ? "bg-(--accent) text-(--accent-foreground)"
                    : "text-white hover:bg-(--accent) hover:text-(--accent-foreground)"
                }`}
              >
                {opt.label}
              </button>
            ))}
          </div>
        )}
      </PopoverPanel>
    </Popover>
  );
}
