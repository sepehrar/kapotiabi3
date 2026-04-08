import Link from "next/link";
import { ReactNode } from "react";

type AccentButtonProps = {
  href: string;
  children: ReactNode;
  className?: string;
};

export default function AccentButton({
  href,
  children,
  className = "",
}: AccentButtonProps) {
  return (
    <Link
      href={href}
      className={`inline-flex items-center justify-center rounded-full bg-(--accent) px-4 py-2 text-sm font-semibold text-(--accent-foreground) transition hover:bg-(--accent-hover) ${className} text-nowrap`}
    >
      {children}
    </Link>
  );
}
