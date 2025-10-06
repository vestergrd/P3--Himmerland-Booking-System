import { useNavigate, useLocation } from "react-router-dom";
import { useState, useEffect } from "react";
import { getBookingCount } from "../../../utils/sessionStorageSupport";
import { useDarkMode } from "../../DarkModeContext";
import { FaHome, FaEnvelope, FaList, FaUser, FaSun, FaMoon } from "react-icons/fa";

function CaretakerNavbar() {
  const { toggleDarkMode } = useDarkMode();
  const navigate = useNavigate();
  const location = useLocation(); // Track current location
  const [showPopup, setShowPopup] = useState<boolean>(false);
  const [bookingCount, setBookingCount] = useState<number>(0);
  const [bookingAdded, setBookingAdded] = useState<boolean>(false);
  const [bookingCanceled, setBookingCanceled] = useState<boolean>(false);
  const [resourceAdded, setResourceAdded] = useState<boolean>(false);
  const [resourceRemoved, setResourceRemoved] = useState<boolean>(false);
  const [resourceEdited, setResourceEdited] = useState<boolean>(false);

  useEffect(() => {
    setBookingCount(getBookingCount());

    const handleBookingAdded = () => {
      setBookingCount(getBookingCount());
      showPopupFunc(setBookingAdded);
    };

    const handleBookingCanceled = () => {
      showPopupFunc(setBookingCanceled);
    }

    const handleResourceAdded = () => {
      showPopupFunc(setResourceAdded);
    }

    const handleResourceRemoved = () => {
      showPopupFunc(setResourceRemoved);
    }

    const handleResourceEdited = () => {
      showPopupFunc(setResourceEdited);
    }


    window.addEventListener("bookingsUpdated", handleBookingAdded);
    window.addEventListener("bookingCanceled", handleBookingCanceled);
    window.addEventListener("resourceAdded", handleResourceAdded);
    window.addEventListener("resourceRemoved", handleResourceRemoved);
    window.addEventListener("resourceEdited", handleResourceEdited);

    return () => {
      window.removeEventListener("bookingsUpdated", handleBookingAdded);
      window.removeEventListener("bookingCanceled", handleBookingCanceled);
      window.removeEventListener("resourceAdded", handleResourceAdded);
      window.removeEventListener("resourceRemoved", handleResourceRemoved);
      window.removeEventListener("resourceEdited", handleResourceEdited);
    };
  }, []);

  // Function to determine if the current route matches
  const isActive = (path: string) => (location.pathname === path ? 'active' : '');

  const showPopupFunc = (setState: React.Dispatch<React.SetStateAction<boolean>>) => {
    setShowPopup(true);
    setState(true);
    setTimeout(() => {
      setShowPopup(false);
      setState(false);
    }, 4000);
  }

  const getPopupText = () => {
    if (bookingAdded) { return "Reservation oprettet" }
    else if (bookingCanceled) { return "Reservation anulleret" }
    else if (resourceAdded) { return "Ressource tilføjet" }
    else if (resourceRemoved) { return "Ressource fjernet" }
    else if (resourceEdited) { return "Ressource redigeret" }
  }

  return (
    <>
      <nav className="navbar navbar-expand-md navbar-dark" style={{ backgroundColor: '#28a745' }}>
        <div className="container-fluid">
          <a href="#intro" className="navbar-title">
            <span style={{ color: "#fff", fontWeight: "bold" }}>
              Himmerland <br /> Boligforening
            </span>
          </a>

          <button
            className="navbar-toggler"
            type="button"
            data-bs-toggle="collapse"
            data-bs-target="#main-nav"
            aria-controls="main-nav"
            aria-expanded="false"
            aria-label="Toggle navigation"
          >
            <span className="navbar-toggler-icon"></span>
          </button>

          <div className="collapse navbar-collapse justify-content-end" id="main-nav">
            <ul className="navbar-nav">
              <li className={`nav-item ${isActive('/admin-overblik')}`}>
                <button
                  className="nav-link btn btn-link text-white"
                  onClick={() => navigate("/admin-overblik")}
                >
                  <FaHome /> Admin-overblik
                </button>
              </li>
              <li className={`nav-item ${isActive('/ressource-overblik')}`}>
                <button
                  className="nav-link btn btn-link text-white"
                  onClick={() => navigate("/ressource-overblik")}
                >
                  <FaList /> Ressource-overblik
                </button>
              </li>
              <li className={`nav-item ${isActive('/beboer-overblik')}`}>
                <button
                  className="nav-link btn btn-link text-white"
                  onClick={() => navigate("/beboer-overblik")}
                >
                  <FaUser /> Beboer-overblik
                </button>
              </li>
              <li className={`nav-item ${isActive('/konto-admin')}`}>
                <button
                  className="nav-link btn btn-link text-white"
                  onClick={() => navigate("/konto-admin")}
                >
                  <FaUser /> Konto
                </button>
              </li>
              <li className="nav-item">
                <button className="nav-link btn btn-link text-white" onClick={toggleDarkMode}>
                  {location.pathname === "/konto-admin" ? <FaSun /> : <FaMoon />} Toggle Dark Mode
                </button>
              </li>
            </ul>
          </div>
        </div>
      </nav>
      {showPopup && (
        <div
          style={{
            position: "fixed",
            top: "50px",
            left: "50%",
            transform: "translateX(-50%)",
            backgroundColor: "#ffffff",
            color: "#000000",
            padding: "15px 25px",
            borderRadius: "8px",
            zIndex: 1050,
            fontWeight: "bold",
            fontSize: "1.2rem",
            boxShadow: "0 4px 8px rgba(0, 0, 0, 0.2)",
            textAlign: "center",
            maxWidth: "300px",
            animation: "fade-in-out 4s ease-in-out",
          }}
        >
          {getPopupText()}
        </div>
      )}

      <style>
        {`
        @keyframes fade-in-out {
          0% { opacity: 0; transform: translateY(-20px); }
          10% { opacity: 1; transform: translateY(0); }
          90% { opacity: 1; transform: translateY(0); }
          100% { opacity: 0; transform: translateY(-20px); }
        }
      `}
      </style>
    </>
  );
}

export default CaretakerNavbar;