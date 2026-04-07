type GuestCounterProps = {
  used: number;
  max: number;
};

export default function GuestCounter({ used, max }: GuestCounterProps) {
  const remaining = max - used;

  return (
    <p className="text-xs text-black/60 sm:text-sm">
      Questions left:{" "}
      <span className="font-semibold text-black">{remaining}</span> / {max}
    </p>
  );
}
