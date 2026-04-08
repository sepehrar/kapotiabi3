import GuestCounter from "./GuestCounter";

export type Message = {
  id: number;
  role: "assistant" | "user";
  text: string;
};

type SearchChatPanelProps = {
  messages: Message[];
  questionsUsed: number;
  maxQuestions: number;
};

export default function SearchChatPanel({
  messages,
  questionsUsed,
  maxQuestions,
}: SearchChatPanelProps) {
  return (
    <aside className="rounded-3xl border border-black/10 bg-[#f8f8f8] p-5 sm:p-6">
      <div>
        <h2 className="text-xl font-semibold text-black">Search chat</h2>
        <p className="mt-2 text-sm leading-6 text-black/70">
          Answer one short question at a time.
        </p>
      </div>

      <div className="mt-6 space-y-4">
        {messages.map((message) => (
          <div
            key={message.id}
            className={`max-w-[90%] rounded-2xl px-4 py-3 text-sm leading-6 ${
              message.role === "assistant"
                ? "bg-white text-black ring-1 ring-black/5"
                : "ml-auto bg-[#f44b05] text-white"
            }`}
          >
            {message.text}
          </div>
        ))}
      </div>

      <div className="mt-6">
        <label htmlFor="search-message" className="sr-only">
          Type your answer
        </label>

        <div className="rounded-2xl bg-white p-3 ring-1 ring-black/5">
          <textarea
            id="search-message"
            rows={3}
            placeholder="Type your answer..."
            className="w-full resize-none border-0 bg-transparent text-sm text-black placeholder:text-black/40 focus:outline-none"
          />

          <div className="mt-3 flex items-center justify-between gap-3">
            <GuestCounter used={questionsUsed} max={maxQuestions} />

            <button
              type="button"
              className="rounded-full bg-[#f44b05] px-5 py-2 text-sm font-medium text-white transition hover:bg-[#902d03]"
            >
              Send
            </button>
          </div>
        </div>
      </div>
    </aside>
  );
}
