import React, { useState, useRef } from "react";
import { useNavigate } from "react-router-dom";
import ApiService from "../../../utils/ApiService";
import { validateImage, getCroppedImage } from "../../../utils/pictureSupport";
import ReactCrop, { Crop } from "react-image-crop";
import "react-image-crop/dist/ReactCrop.css";

interface SignUpDetails {
  username: string;
  password: string;
  email: string;
  name: string;
  houseAddress?: string;
  mobileNumber: string;
}

const SignUp: React.FC = () => {
  const navigate = useNavigate();
  const [details, setDetails] = useState<SignUpDetails>({ username: "", password: "", email: "", name: "", houseAddress: "", mobileNumber: "" });
  const [errorMessage, setErrorMessage] = useState<string>("");
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



  // Valider om det er en mail
  const isValidEmail = (email: string): boolean => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  };

  const ismobileNumber = (mobileNumber: string): boolean => {
    const mobileNumberRegex = /^\d{8}$/;
    return mobileNumberRegex.test(mobileNumber);
  };

  // Valider styrke af kodeord
  const isStrongPassword = (password: string): boolean => {
    // Min 8 karakterer, et stort bogstav, et tal
    const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[A-Za-z\d]{8,}$/;
    return passwordRegex.test(password);
  };

  const validateForm = (): boolean => {
    const { username, password, email, name } = details;
    if (!username || !password || !email || !name) {
      setErrorMessage("Udfyld venligst alle felter.");
      return false;
    }
    if (!isValidEmail(email)) {
      setErrorMessage("Indtast en gyldig email.");
      return false;
    }
    if (!ismobileNumber(details.mobileNumber)) {
      setErrorMessage("Indtast et gyldigt telefonnummer.");
      return false;
    }
    if (!isStrongPassword(password)) {
      setErrorMessage("Adgangskoden skal være mindst 8 tegn lang og inkludere både store og små bogstaver samt et tal");
      return false;
    }
    return true;
  };

  const handleChange = async (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
    const { id, value } = e.target;

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
    } else {
      setDetails((prevDetails) => ({
        ...prevDetails,
        [id]: value,
      }));
    }
  };

  const handleCropComplete = async (crop: Crop) => {
    if (crop.width && crop.height && imgRef.current) {
      const croppedBlob = await getCroppedImage(imgRef.current, crop, `${details.username}-cropped.jpg`);
      if (croppedBlob) {
        setImageFile(new File([croppedBlob], "cropped-image.jpg", { type: "image/jpeg" }));
      }
    }
  };


  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>): Promise<void> => {
    e.preventDefault();
    setErrorMessage("");

    if (!validateForm()) return;

    try {
      const response = await ApiService.signUp({ user: details, profilePicture: imageFile });

      console.log(response);
      console.log(response.data);

      if (response.status === 201) {
        navigate("/login");
      } else {
        setErrorMessage("Kunne ikke oprette bruger.");
      }
    } catch (error) {
      setErrorMessage("En fejl opstod ved oprettelse af bruger.");
    }
  };

  return (
    <div className="d-flex justify-content-center align-items-center min-vh-100">
      <form className="w-25 p-4 border rounded shadow" onSubmit={handleSubmit}>
        <h4 className="text-center mb-4">Opret en bruger</h4>
        {errorMessage && <div className="alert alert-danger">{errorMessage}</div>}

        <div className="mb-3 mt-5">
          <label htmlFor="name">Navn</label>
          <input
            type="text"
            className="form-control form-control-lg"
            id="name"
            value={details.name}
            onChange={handleChange}
            placeholder="Skriv name"
            required
          />
        </div>

        <div className="mb-3">
          <label htmlFor="email">Email</label>
          <input
            type="email"
            className="form-control form-control-lg"
            id="email"
            value={details.email}
            onChange={handleChange}
            placeholder="Skriv email"
            required
          />
        </div>

        <div className="mb-3 mt-5">
          <label htmlFor="username">Brugernavn</label>
          <input
            type="text"
            className="form-control form-control-lg"
            id="username"
            value={details.username}
            onChange={handleChange}
            placeholder="Skriv brugernavn"
            required
          />
        </div>

        <div className="mb-3">
          <label htmlFor="mobileNumber">Telefonnummer</label>
          <input
            type="text"
            className="form-control form-control-lg"
            id="mobileNumber"
            value={details.mobileNumber}
            onChange={handleChange}
            placeholder="Skriv telefonnummer"
            required
          />
        </div>

        <div className="mb-3">
          <label htmlFor="houseAddress">Adresse</label>
          <input
            type="text"
            className="form-control form-control-lg"
            id="houseAddress"
            value={details.houseAddress}
            onChange={handleChange}
            placeholder="Skriv adresse"
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="password">Adgangskode</label>
          <input
            type="password"
            className="form-control form-control-lg"
            id="password"
            value={details.password}
            onChange={handleChange}
            placeholder="Skriv adgangskode"
            required
          />
        </div>

        <div>
          <label htmlFor="imageFile" className="mt-3">Profilbillede (ikke påkrævet):</label> <br></br>
          <input
            type="file"
            id="imageFile"
            name="imageFile"
            onChange={handleChange}
          />
          {imageSrc && (
            <div>
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
            </div>
          )}
        </div>

        <button type="submit" className="btn btn-primary btn-lg btn-block mt-4">
          Opret Bruger
        </button>

        <div className="text-center mt-3">
          <button
            type="button"
            className="btn btn-secondary btn-lg"
            onClick={() => navigate("/")}
          >
            Allerede en bruger? Login
          </button>
        </div>
      </form>
    </div>
  );
};

export default SignUp;
