import HeroBusiness from "./HeroBusiness";
import HowItWorksSection from "./HowItWorksSection";
import WhatYouGetSection from "./WhatYouGetSection";
import RequestFlowSection from "./RequestFlowSection";
import BottomCtaSection from "./BottomCtaSection";

export default function BusinessLandingView() {
  return (
    <div className="bg-black text-white">
      <HeroBusiness />
      <HowItWorksSection />
      <WhatYouGetSection />
      <RequestFlowSection />
      <BottomCtaSection />
    </div>
  );
}
