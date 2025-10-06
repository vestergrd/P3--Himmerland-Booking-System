import CaretakerNavBar from "../components/BothView/NavBar/CaretakerNavBar.tsx";
import Footer from "../components/BothView/Footer/Footer.tsx";
import CaretakerBookingOverview from "../components/CaretakerView/BookingOverview/CaretakerBookingOverview.tsx";

const CToverviewPage = () => {
  return (
    <>
    <CaretakerNavBar />
    <div>
      <CaretakerBookingOverview />
      <Footer />
    </div>
    </>
  );
};

export default CToverviewPage;
