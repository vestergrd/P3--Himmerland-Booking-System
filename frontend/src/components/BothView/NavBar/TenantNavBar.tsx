import { useNavigate, useLocation } from "react-router-dom";
import { useState, useEffect } from "react";
import { getBookingCount } from "../../../utils/sessionStorageSupport";
import { useDarkMode } from "../../DarkModeContext";
import { FaHome, FaEnvelope, FaList, FaUser, FaSun, FaMoon } from "react-icons/fa";

function TenantNavbar() {
  const { toggleDarkMode } = useDarkMode();
  const navigate = useNavigate();
  const location = useLocation();
  const [showPopup, setShowPopup] = useState<boolean>(false);
  const [bookingCount, setBookingCount] = useState<number>(0);
  const [bookingAdded, setBookingAdded] = useState<boolean>(false);
  const [bookingCanceled, setBookingCanceled] = useState<boolean>(false);
  const [bookingEdited, setBookingEdited] = useState<boolean>(false);

  useEffect(() => {
    setBookingCount(getBookingCount());

    const handleBookingAdded = () => {
      setBookingCount(getBookingCount());
      showPopupFunc(setBookingAdded);
    };

    const handleBookingCanceled = () => {
      showPopupFunc(setBookingCanceled);
    }

    const handleBookingEdited = () => {
      showPopupFunc(setBookingEdited);
    }

    window.addEventListener("bookingsUpdated", handleBookingAdded);
    window.addEventListener("bookingCanceled", handleBookingCanceled);
    window.addEventListener("bookingEdited", handleBookingEdited);

    return () => {
      window.removeEventListener("bookingsUpdated", handleBookingAdded);
      window.removeEventListener("bookingCanceled", handleBookingCanceled);
      window.removeEventListener("bookingEdited", handleBookingEdited);
    };
  }, []);

 // Function to determine if the current route matches
  const isActive = (path: string) => location.pathname === path ? 'active' : '';

  const showPopupFunc = (setState: React.Dispatch<React.SetStateAction<boolean>>) => {
    setShowPopup(true);
    setState(true);
      setTimeout(() => {
        setShowPopup(false);
        setState(false);
      }, 4000);
  }

  const getPopupText = () => {
    if (bookingAdded) {return "Reservation oprettet"}
    else if (bookingCanceled) {return "Reservation anulleret"}
    else if (bookingEdited) {return "Reservation redigeret"}
  }

  return (
    <>
      <nav className="navbar navbar-expand-md navbar-dark" style={{ backgroundColor: "#28a745" }}>
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

          {/* Navbar links */}
          <div 
          className="collapse navbar-collapse justify-content-end align-center" 
          id="main-nav"
          >
            <ul className="navbar-nav">
              <li className={`nav-item ${isActive('/hjem')}`}>
                <button 
                className="nav-link btn btn-link text-white" 
                onClick={() => navigate("/hjem")}
                >
                  <FaHome /> Hjem
                </button>
              </li>
              <li className={`nav-item ${isActive('/kontakt')}`}
              >
                <button 
                className="nav-link btn btn-link text-white" 
                onClick={() => navigate("/kontakt")}>
                  <FaEnvelope /> Kontakt
                </button>
              </li>
              <li className={`nav-item ${isActive('/mine-reservationer')}`}>
                <button
                  className="nav-link btn btn-link text-white"
                  onClick={() => navigate("/mine-reservationer")}
                >
                  <FaList /> Reservationer
                </button>
              </li>
              <li className={`nav-item ${isActive("/konto")}`}>
                <button className="nav-link btn btn-link text-white" onClick={() => navigate("/konto")}>
                  <FaUser /> Konto
                </button>
              </li>

                  {/* Dark Mode Toggle */}
              <li className="nav-item">
                <button className="nav-link btn btn-link text-white" onClick={toggleDarkMode}>
                  {location.pathname === "/konto" ? <FaSun /> : <FaMoon />} Toggle Dark Mode
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

export default TenantNavbar;
