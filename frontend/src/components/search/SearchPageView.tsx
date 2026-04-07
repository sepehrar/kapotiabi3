import SearchHeader from "./SearchHeader";
import SearchChatPanel from "./SearchChatPanel";
import SearchSummary from "./SearchSummary";
import SearchResultsToolbar from "./SearchResultsToolbar";
import SearchResultsPanel from "./SearchResultsPanel";

const mockMessages = [
  {
    id: 1,
    role: "assistant",
    text: "What does your car need?",
  },
  {
    id: 2,
    role: "user",
    text: "Oil change in Tallinn",
  },
  {
    id: 3,
    role: "assistant",
    text: "What car do you have?",
  },
  {
    id: 4,
    role: "user",
    text: "BMW 320d 2018",
  },
  {
    id: 5,
    role: "assistant",
    text: "Which parts quality do you prefer?",
  },
];

const mockFilters = [
  "Oil change",
  "Tallinn",
  "BMW",
  "320d",
  "2018",
  "OEM-equivalent",
  "10 km",
];

const mockResults = [
  {
    id: 1,
    name: "AutoExpert Tallinn",
    price: "€95",
    distance: "3.2 km",
    rating: "4.7",
    reviews: 214,
    updatedAt: "Updated 2 days ago",
  },
  {
    id: 2,
    name: "Nord Service",
    price: "From €80",
    distance: "5.6 km",
    rating: "4.5",
    reviews: 98,
    updatedAt: "Updated today",
  },
  {
    id: 3,
    name: "FixCar Center",
    price: "€110",
    distance: "6.8 km",
    rating: "4.8",
    reviews: 156,
    updatedAt: "Updated 5 days ago",
  },
];

export default function SearchPageView() {
  return (
    <section className="bg-black">
      <div className="mx-auto w-full max-w-7xl px-6 py-8 sm:px-12 lg:px-8 lg:py-10">
        <div className="flex flex-col lg:flex-row gap-10">
          <div className="max-w-xl gap-12 flex flex-col">
            <SearchHeader />
            <SearchSummary filters={mockFilters} />
          </div>
          <div className="w-full">
            <SearchChatPanel
              messages={mockMessages}
              questionsUsed={2}
              maxQuestions={5}
            />
          </div>
        </div>

        <div className="mt-10 space-y-6">
          <SearchResultsToolbar count={mockResults.length} />
          <SearchResultsPanel results={mockResults} />
        </div>
      </div>
    </section>
  );
}
