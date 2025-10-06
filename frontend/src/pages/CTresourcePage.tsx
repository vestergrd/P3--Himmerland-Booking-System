import CaretakerNavBar from "../components/BothView/NavBar/CaretakerNavBar.tsx";
import Footer from "../components/BothView/Footer/Footer.tsx";
import CaretakerResourceOverview from "../components/CaretakerView/RessourceOverview/CaretakerResourceOverview.tsx";

const CTresourcePage = () => {
  return (
    <>
    <CaretakerNavBar />
    <div>
      <CaretakerResourceOverview />
      <Footer />
    </div>
    </>
  );
};

export default CTresourcePage;
