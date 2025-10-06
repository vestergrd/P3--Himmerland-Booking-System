import ResourceGrid from "../components/TenantView/ResourceGrid/ResourceGrid.tsx";
import TenantNavBar from "../components/BothView/NavBar/TenantNavBar.tsx";
import Footer from "../components/BothView/Footer/Footer.tsx";

const HomePage = () => {

  return (
    <div className="homePage">
      <TenantNavBar />
      <ResourceGrid />
      <Footer />
    </div>
  );
};

export default HomePage;
