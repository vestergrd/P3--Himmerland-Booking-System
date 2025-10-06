import { FaFacebook, FaYoutube, FaLinkedin } from "react-icons/fa"; // Importing social media icons

function Footer({ style = {} }) {
  return (
    <footer
      className="text-white py-5"
      style={{ backgroundColor: '#28a745', ...style }} // Same green as btn-success
    >
      <div className="container">
        <div className="row text-center text-md-left">
          {/* Address Section */}
          <div className="col-md-3 mb-4">
            <h5 className="font-weight-bold mb-3">Adresse:</h5>
            <p>Damstræde</p>
            <p>Afdeling 41</p>
            <p>Himmerland Boligforening</p>
            <p>9220 Aalborg Ø</p>
          </div>

          {/* Service Section */}
          <div className="col-md-3 mb-4">
            <h5 className="font-weight-bold mb-3">Himmerland Service - din varmemester:</h5>
            <p>Anders Klagenberg</p>
            <p>Fredrik Bajers Vej 154 B</p>
          </div>

          {/* Contact Section */}
          <div className="col-md-3 mb-4">
            <h5 className="font-weight-bold mb-3">Kontakt:</h5>
            <p>Email: kanalkvarteret@abhim.dk</p>
            <p>Phone: +45 98 15 87 22</p>
          </div>

          {/* Office Hours Section */}
          <div className="col-md-3 mb-4">
            <h5 className="font-weight-bold mb-3">Kontortid:</h5>
            <p>Mandag - fredag kl. 07:00 - 07:30 samt 11:00 - 12:00</p>
          </div>

          {/* Social Media Section */}
          <div className="col-md-12 mt-4">
            <h5 className="font-weight-bold mb-3">Følg Os:</h5>
            <div className="d-flex justify-content-center">
              <a
                href="https://www.facebook.com/HimmerlandBoligforening/"
                target="_blank"
                rel="noopener noreferrer"
                className="text-light mx-4 hover-icon"
                aria-label="Besøg vores Facebook"
              >
                <FaFacebook size={35} />
              </a>
              <a
                href="https://www.youtube.com/user/abhimdk"
                target="_blank"
                rel="noopener noreferrer"
                className="text-light mx-4 hover-icon"
                aria-label="Besøg vores YouTube"
              >
                <FaYoutube size={35} />
              </a>
              <a
                href="https://dk.linkedin.com/company/himmerland-boligforening"
                target="_blank"
                rel="noopener noreferrer"
                className="text-light mx-4 hover-icon"
                aria-label="Besøg vores LinkedIn"
              >
                <FaLinkedin size={35} />
              </a>
            </div>
          </div>
        </div>
      </div>
    </footer>
  );
}

export default Footer;
