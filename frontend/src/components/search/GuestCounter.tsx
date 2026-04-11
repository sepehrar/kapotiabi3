type GuestCounterProps = {
  used: number;
  max: number;
  label: string;
};

export default function GuestCounter({ used, max, label }: GuestCounterProps) {
  const remaining = max - used;

  return (
    <p className="text-xs text-black/60 sm:text-sm">
      {label}{" "}
      <span className="font-semibold text-black">{remaining}</span> / {max}
    </p>
  );
}
