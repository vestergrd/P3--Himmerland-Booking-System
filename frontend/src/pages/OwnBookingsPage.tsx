import TenantNavbar from "../components/BothView/NavBar/TenantNavBar.tsx";
import Footer from "../components/BothView/Footer/Footer.tsx";
import TenantBookingOverview from "../components/TenantView/BookingOverview/TenantBookingOverview.tsx";

const OwnBookingsPage = () => {
  return (
    <div>
      <TenantNavbar />
      <TenantBookingOverview />
      <Footer />
    </div>
  );
};

export default OwnBookingsPage;
