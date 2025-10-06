import React, { useState, useEffect } from "react";
import {
  Form,
  Button,
  Modal,
  Row,
  Col,
  Container,
} from "react-bootstrap";
import LogoutButton from "../Logout/Logout";
import ApiService from "../../../utils/ApiService";
import showAlert from "../Alert/AlertFunction";
import { useNavigate } from "react-router-dom";
import DeleteUserButton from "../DeleteUser/DeleteUser";

interface UserInfo {
  id: number;
  username: string;
  password: string;
}

const SettingsForm: React.FC = () => {
  const [userInfo, setUserInfo] = useState<UserInfo>({
    id: 0,
    username: "",
    password: "",
  });
  const navigate = useNavigate();

  const [isEditing, setIsEditing] = useState(false);
  const [showSuccessModal, setShowSuccessModal] = useState(false);
  const [showWarningModal, setShowWarningModal] = useState(false);
  const [currentView, setCurrentView] = useState("settings");
  const [passwordVisible, setPasswordVisible] = useState(false);
  const [validationError, setValidationError] = useState<string | null>(null);
  const [newPassword, setNewPassword] = useState<string>("");

  //used to fetch user data from the backend when site is loaded
  useEffect(() => {
    const fetchUserInfo = async () => {
      try {
        let response = await ApiService.getAdminById(1);
        console.log("User Information:", response.data);
        setUserInfo(response.data);
      } catch (error) {
        console.error("Error fetching user information:", error);
      }
    };
    fetchUserInfo();
  }, []);

  //used to fetch user data from the backend when cancel is clicked
  const fetchUserData = async () => {
    try {
      const data = await ApiService.getAdminById(1);
      setUserInfo(data.data);
    } catch (error) {
      console.error("Error fetching user data:", error);
    }
  };

  const handleCancel = async () => {
    await fetchUserData();
    setIsEditing(false);
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    let updatedPassword = newPassword;
    if (name === "password") {
      setNewPassword(value);
      updatedPassword = value;
    } else {
      setUserInfo((prevInfo) => ({ ...prevInfo, [name]: value }));
    }

    //Throw error message if invalid - state updates are asynchronous, so workaround is declaring local updated version
    const error = validateForm(updatedPassword)
    if (error) {
      setValidationError(error)
    } else if (error == null) { setValidationError(null) }
  };

  const updatePassword = () => {
    setUserInfo((prevInfo) => ({ ...prevInfo, password: newPassword }));
  }

  const validateForm = (newPasswordLocal: string) => {
    const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[A-Za-z\d]{8,}$/;
    const hashedPasswordRegex = /^\$2[ayb]\$.{56}$/;

    if (newPasswordLocal != "" && !passwordRegex.test(newPasswordLocal)) {
      return "Adgangskoden skal være mindst 8 tegn lang og inkludere både store og små bogstaver samt et tal.";
    }
    if (newPasswordLocal != "" && hashedPasswordRegex.test(newPasswordLocal)) {
      return "Adgangskoden må ikke være en hashed adgangskode.";
    }
    return null;
  };

  const handleEditToggle = () => {
    setIsEditing(true);
    setValidationError(null);
  };

  const handleSaveChanges = async () => {
    const error = validateForm(newPassword);
    if (error) {
      setValidationError(error);
      return;
    }
    updatePassword();
    setShowWarningModal(true);
  }

  const togglePasswordVisibility = () => {
    setPasswordVisible(!passwordVisible);
  };

  const handleWarningConfirm = async () => {
    setShowWarningModal(false);
    try {
      await ApiService.editAdmin(userInfo, null); // Make the API call after confirming the warning
      setShowSuccessModal(true);
      navigate("/login");
    } catch (error) {
      console.error("Error updating user:", error);
    }
  };

  const handleWarningCancel = () => {
    setShowWarningModal(false);
  };

  const renderContent = () => {
    switch (currentView) {
      case "settings":
        return (
          <Row>
            <Col md={8}>
              <Form>
                <Form.Group controlId="formUsername">
                  <Form.Label>Brugernavn</Form.Label>
                  <Form.Control
                    type="text"
                    name="username"
                    value={userInfo.username}
                    onChange={handleInputChange}
                    disabled={!isEditing}
                    placeholder={userInfo.username}
                  />
                </Form.Group>
                <Form.Group controlId="formPassword">
                  <Form.Label>Adgangskode</Form.Label>
                  <div style={{ display: "flex", alignItems: "center" }}>
                    <Form.Control
                      type={passwordVisible ? "text" : "password"}
                      name="password"
                      onChange={handleInputChange}
                      disabled={!isEditing}
                      value={isEditing ? newPassword : "*******************"}
                      style={{ marginRight: "10px" }}
                    />
                    <Button
                      variant="secondary"
                      onClick={togglePasswordVisibility}
                    >
                      {passwordVisible ? "Skjul" : "Vis"}
                    </Button>
                  </div>
                </Form.Group>
                {validationError && (
                  <p style={{ color: "red" }}>{validationError}</p>
                )}
                <Button variant="success" onClick={isEditing ? handleSaveChanges : handleEditToggle} disabled={isEditing && !!validateForm(newPassword)}>
                  {isEditing ? "Gem ændringer" : "Ændre"}
                </Button>
                {isEditing && (
                  <Button variant="secondary" onClick={handleCancel}>Annuller</Button>
                )}
              </Form>
            </Col>
            <Col md={4} style={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
            </Col>
          </Row>
        );
      case "notifications":
        return (
          <div>
            <Form.Check type="checkbox" label="Notification 1" />
            <Form.Check type="checkbox" label="Notification 2" />
            <Form.Check type="checkbox" label="Notification 3" />
          </div>
        );
      default:
        return null;
    }
  };

  const handleCloseModal = () => setShowSuccessModal(false);

  return (
    <Container
      style={{
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        height: "100vh",
      }}
    >
      <Row style={{ width: "100%" }}>
        <Col
          md={3}
          style={{
            display: "flex",
            flexDirection: "column",
            alignItems: "flex-start",
            marginRight: "20px",
          }}
        >
          <Button
            onClick={() => setCurrentView("settings")}
            style={{ marginBottom: "10px" }}
            variant="success"
          >
            Din bruger
          </Button>
          <Button
            onClick={() => setCurrentView("notifications")}
            style={{ marginBottom: "10px" }}
            variant="success"
          >
            Notifikationer
          </Button>
          <LogoutButton />
        </Col>
        <Col
          md={8}
          style={{
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            border: "1px solid #ccc",
            padding: "20px",
            borderRadius: "8px",
            backgroundColor: "#f9f9f9",
          }}
        >
          {renderContent()}
        </Col>
      </Row>
      <Row>
        <Col>
          {/* Success Modal */}
          <Modal show={showSuccessModal} onHide={handleCloseModal} centered>
            <Modal.Header closeButton>
              <Modal.Title>Ændringer er gemt</Modal.Title>
            </Modal.Header>
            <Modal.Body>Ændringer foretaget er opdateret!</Modal.Body>
            <Modal.Footer>
              <Button variant="secondary" onClick={handleCloseModal}>
                Luk
              </Button>
            </Modal.Footer>
          </Modal>
          {/* Warning Modal */}
          <Modal show={showWarningModal} onHide={handleWarningCancel} centered>
            <Modal.Header closeButton>
              <Modal.Title>Advarsel</Modal.Title>
            </Modal.Header>
            <Modal.Body>Du vil blive sendt til login-siden. Er du sikker?</Modal.Body>
            <Modal.Footer>
              <Button variant="secondary" onClick={handleWarningCancel}>
                Annuller
              </Button>
              <Button variant="danger" onClick={handleWarningConfirm}>
                Bekræft
              </Button>
            </Modal.Footer>
          </Modal>
        </Col>
      </Row>
    </Container>
  );
};

export default SettingsForm;
