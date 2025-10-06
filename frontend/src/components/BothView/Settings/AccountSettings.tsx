import React, { useState, useEffect, useRef } from "react";
import {
  Form,
  Button,
  Modal,
  Row,
  Col,
  Container,
} from "react-bootstrap";
import LogoutButton from "../Logout/Logout";
import ProfilePicture from "./ProfilePicture";
import ApiService from "../../../utils/ApiService";
import showAlert from "../Alert/AlertFunction";
import { useNavigate } from "react-router-dom";
import DeleteUserButton from "../DeleteUser/DeleteUser";
import { validateImage, getCroppedImage } from "../../../utils/pictureSupport";
import ReactCrop, { Crop } from "react-image-crop";
import "react-image-crop/dist/ReactCrop.css";

interface UserInfo {
  id: number;
  username: string;
  name: string;
  houseAddress?: string;
  email: string;
  mobileNumber: string;
  password: string;
}

const SettingsForm: React.FC = () => {
  const [userInfo, setUserInfo] = useState<UserInfo>({
    id: 0,
    username: "",
    name: "",
    houseAddress: "",
    mobileNumber: "",
    email: "",
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

  const [imageFile, setImageFile] = useState<File | null>(null);
  const [imageSrc, setImageSrc] = useState<string | null>(null);
  const [crop, setCrop] = useState<Crop>({
    unit: "%",
    width: 50,
    height: 50,
    x: 25,
    y: 25,
  });
  const imgRef = useRef<HTMLImageElement | null>(null);

  //used to fetch user data from the backend when site is loaded
  useEffect(() => {
    // Fetch authenticated user information from the backend
    const fetchUserInfo = async () => {
      try {
        let response = await ApiService.fetchData<UserInfo>("tenant");
        console.log("User Information:", response.data);
        setUserInfo(response.data);
        validateForm(userInfo, newPassword);
      } catch (error) {
        console.error("Error fetching user information:", error);
      }
    };
    fetchUserInfo();
  }, []);

  //used to fetch user data from the backend when cancel is clicked
  const fetchUserData = async () => {
    try {
      const data = await ApiService.fetchData<UserInfo>("tenant");
      const { password, ...userInfoWithoutPassword } = data.data; // Exclude password
      setUserInfo({ ...userInfoWithoutPassword, password: "" });
    } catch (error) {
      console.error("Error fetching user data:", error);
    }
  };

  const handleCancel = async () => {
    await fetchUserData();
    setIsEditing(false);
  };

  const handleInputChange = async (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    let updatedUserInfo = userInfo;
    let updatedPassword = newPassword;

    if (e.target.type === "file") {
      const fileInput = e.target as HTMLInputElement;
      const file = fileInput.files?.[0];
      if (file) {
        const validImage = await validateImage(file);
        if (validImage) {
          setImageFile(file);
          const imageSrc = URL.createObjectURL(file);
          setImageSrc(imageSrc);
        } else {
          setImageFile(null);
          setImageSrc(null);
          alert("Ugyldig fil type eller for små dimensioner. Billedet skal være PNG/JPG og skal være minimum 300x300 pixels");
          fileInput.value = "";
        }
      }
    } else if (name === "password") {
      setNewPassword(value);
      updatedPassword = value;
    } else {
      setUserInfo((prevInfo) => ({ ...prevInfo, [name]: value }));
      updatedUserInfo = { ...userInfo, [name]: value, };
    }

    //Throw error message if invalid - state updates are asynchronous, so workaround is declaring local updated versions
    const error = validateForm(updatedUserInfo, updatedPassword)
    if (error) {
      setValidationError(error)
    } else if (error == null) { setValidationError(null) }
  };


  const handleCropComplete = async (crop: Crop) => {
    if (crop.width && crop.height && imgRef.current) {
      const croppedBlob = await getCroppedImage(imgRef.current, crop, `${userInfo.username}-cropped.jpg`);
      if (croppedBlob) {
        setImageFile(new File([croppedBlob], "cropped-image.jpg", { type: "image/jpeg" }));
      }
    }
  };

  const updatePassword = () => {
    setUserInfo((prevInfo) => ({ ...prevInfo, password: newPassword }));
  }


  const validateForm = (userInfoLocal: UserInfo, newPasswordLocal: string) => {
    const emailRegex = /^[^\s@]+@[^\s@]+$/;
    const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[A-Za-z\d]{8,}$/;
    const phoneNumberRegex = /^[0-9]{8}$/;
    const hashedPasswordRegex = /^\$2[ayb]\$.{56}$/;

    if (!userInfoLocal.username || !userInfoLocal.email || !userInfoLocal.name || !userInfoLocal.mobileNumber || !userInfoLocal.houseAddress) {
      return "Udfyld venligst alle felter.";
    }
    if (!emailRegex.test(userInfoLocal.email)) {
      return "Indtast en gyldig email.";
    }
    if (!phoneNumberRegex.test(userInfoLocal.mobileNumber)) {
      return "Indtast et gyldigt telefonnummer.";
    }
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
    const error = validateForm(userInfo, newPassword);
    if (error) {
      setValidationError(error);
      return;
    }
    updatePassword();
    setShowWarningModal(true); // Show warning modal before saving changes
  };

  const togglePasswordVisibility = () => {
    setPasswordVisible(!passwordVisible);
  };

  const handleWarningConfirm = async () => {
    setShowWarningModal(false);
    try {
      await ApiService.editUser(userInfo, imageFile); // Make the API call after confirming the warning
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
                <Form.Group controlId="formName">
                  <Form.Label>Navn</Form.Label>
                  <Form.Control
                    type="text"
                    name="name"
                    value={userInfo.name}
                    onChange={handleInputChange}
                    disabled={!isEditing}
                    placeholder={userInfo.name}
                  />
                </Form.Group>
                <Form.Group controlId="formHouseAddress">
                  <Form.Label>Adresse</Form.Label>
                  <Form.Control
                    type="text"
                    name="houseAddress"
                    value={userInfo.houseAddress}
                    onChange={handleInputChange}
                    disabled={!isEditing}
                    placeholder={userInfo.houseAddress}
                  />
                </Form.Group>
                <Form.Group controlId="formEmail">
                  <Form.Label>Email</Form.Label>
                  <Form.Control
                    type="email"
                    name="email"
                    value={userInfo.email}
                    onChange={handleInputChange}
                    disabled={!isEditing}
                    placeholder={userInfo.email}
                  />
                </Form.Group>
                <Form.Group controlId="formmobileNumber">
                  <Form.Label>Telefon nummer</Form.Label>
                  <Form.Control
                    type="text"
                    name="mobileNumber"
                    value={userInfo.mobileNumber}
                    onChange={handleInputChange}
                    disabled={!isEditing}
                    placeholder={userInfo.mobileNumber}
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
                <Button variant="success" onClick={isEditing ? handleSaveChanges : handleEditToggle} disabled={isEditing && !!validateForm(userInfo, newPassword)}>
                  {isEditing ? "Gem ændringer" : "Ændre"}
                </Button>
                {isEditing && (
                  <Button variant="secondary" onClick={handleCancel}>Annuller</Button>
                )}
              </Form>
            </Col>

            <Col md={4} style={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
              {!isEditing && (
                <ProfilePicture imageSource={"tenant/profilePicture"} />
              )}

              {isEditing && (
                <Form>
                  <Form.Group controlId="imageFile">
                    <Form.Label>Billede:</Form.Label>
                    <Form.Control type="file" name="imageFile" onChange={handleInputChange} required />
                    {imageSrc && (
                      <ReactCrop
                        crop={crop}
                        onChange={(newCrop) => setCrop(newCrop)}
                        aspect={1}
                        onComplete={handleCropComplete}
                      >
                        <img
                          ref={imgRef}
                          src={imageSrc}
                          alt="Upload"
                          onLoad={(e) => (imgRef.current = e.currentTarget)}
                        />
                      </ReactCrop>
                    )}
                  </Form.Group>
                </Form>
              )}
            </Col>
          </Row>
        );
      case "text":
        return <div>"Et eller andet tekst IDK"</div>;
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
            onClick={() => setCurrentView("text")}
            style={{ marginBottom: "10px" }}
            variant="success"
          >
            Samtykke
          </Button>
          <Button
            onClick={() => setCurrentView("notifications")}
            style={{ marginBottom: "10px" }}
            variant="success"
          >
            Notifikationer
          </Button>
          <LogoutButton />
          <DeleteUserButton userId={userInfo.id} />
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