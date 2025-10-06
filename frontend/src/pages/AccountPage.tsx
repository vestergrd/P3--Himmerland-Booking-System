import React from "react";
import SettingsForm from "../components/BothView/Settings/AccountSettings.tsx";
import TenantNavbar from "../components/BothView/NavBar/TenantNavBar.tsx";
import CaretakerNavBar from "../components/BothView/NavBar/CaretakerNavBar.tsx";
import Footer from "../components/BothView/Footer/Footer.tsx";
import { getUserRole } from "../utils/authConfig.ts";

const SettingsPage: React.FC = () => {

  const navbar = getUserRole() === "ROLE_TENANT" ? <TenantNavbar /> : <CaretakerNavBar />;

  return (
    <div>
      {navbar}
      <SettingsForm />
      <Footer />
    </div>
  );
};

export default SettingsPage;
