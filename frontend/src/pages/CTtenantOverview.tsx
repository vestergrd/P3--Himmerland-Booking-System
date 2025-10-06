import { Link } from "react-router-dom";
import CaretakerNavBar from "../components/BothView/NavBar/CaretakerNavBar.tsx";
import Footer from "../components/BothView/Footer/Footer.tsx";
import TenantDetailsList from "../components/CaretakerView/TenantDetails/TenantDetailsList.tsx";
const CTtenantOverview = () => {
  return (
    <>
    <CaretakerNavBar />
    <div>
      <TenantDetailsList />
      <Footer />
    </div>
    </>
  );
};

export default CTtenantOverview;
