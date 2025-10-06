import React, { useState, useEffect, useRef } from "react";
import ApiService from "../../../utils/ApiService";
import Tenant from "../../modelInterfaces/Tenant";
import { Modal, Button, Form } from "react-bootstrap";
import { validateImage, getCroppedImage } from "../../../utils/pictureSupport";
import ReactCrop, { Crop } from "react-image-crop";
import "react-image-crop/dist/ReactCrop.css";

interface EditTenantDetailsProps {
  tenantId: string;
  onClose: () => void;
  onUpdate: (updatedTenant: Tenant) => void;
}

const EditTenantDetails: React.FC<EditTenantDetailsProps> = ({ tenantId, onClose, onUpdate }) => {
  const [tenant, setTenant] = useState<Tenant | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

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

  useEffect(() => {
    const fetchTenant = async () => {
      try {
        const response = await ApiService.fetchData(`admin/getTenant/${tenantId}`);
        setTenant(response.data as Tenant);
      } catch (err) {
        console.error("Error fetching tenant:", err);
        setError("Failed to fetch tenant.");
      } finally {
        setLoading(false);
      }
    };

    fetchTenant();
  }, [tenantId]);

  const handleChange = async (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;

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
      setTenant((prevTenant) => (prevTenant ? { ...prevTenant, [name]: value } : null));
    }
  };

  const handleCropComplete = async (crop: Crop) => {
    if (crop.width && crop.height && imgRef.current) {
      const croppedBlob = await getCroppedImage(imgRef.current, crop, `${tenant?.name}-cropped.jpg`);
      if (croppedBlob) {
        setImageFile(new File([croppedBlob], "cropped-image.jpg", { type: "image/jpeg" }));
      }
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (tenant) {
        const response = await ApiService.editUserAdmin(tenant, imageFile);
        onUpdate(response.data);
        onClose();
      } else {
        setError("Tenant data is missing.");
      }
    } catch (err) {
      console.error("Error updating tenant:", err);
      setError("Failed to update tenant.");
    }
  };

  if (loading) {
    return <div>Loading...</div>;
  }

  if (error) {
    return <div>{error}</div>;
  }

  return (
    <Modal show={true} onHide={onClose}>
      <Modal.Header closeButton>
        <Modal.Title>Ændre beboer</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        {tenant && (
          <Form onSubmit={handleSubmit}>
            <Form.Group controlId="formTenantName">
              <Form.Label>Navn</Form.Label>
              <Form.Control
                type="text"
                name="name"
                value={tenant.name}
                onChange={handleChange}
              />
            </Form.Group>

            <Form.Group controlId="formTenantUsername">
              <Form.Label>Brugernavn</Form.Label>
              <Form.Control
                type="text"
                name="username"
                value={tenant.username}
                onChange={handleChange}
              />
            </Form.Group>

            <Form.Group controlId="formTenantAddress">
              <Form.Label>Adresse</Form.Label>
              <Form.Control
                type="text"
                name="houseAddress"
                value={tenant.houseAddress}
                onChange={handleChange}
              />
            </Form.Group>

            <Form.Group controlId="formTenantPhone">
              <Form.Label>Telefonnummer</Form.Label>
              <Form.Control
                type="text"
                name="mobileNumber"
                value={tenant.mobileNumber}
                onChange={handleChange}
              />
            </Form.Group>

            <Form.Group controlId="formTenantEmail">
              <Form.Label>Email</Form.Label>
              <Form.Control
                type="email"
                name="email"
                value={tenant.email}
                onChange={handleChange}
              />
            </Form.Group>

            <Form.Group controlId="imageFile">
              <Form.Label>Billede</Form.Label>
              <Form.Control type="file" name="imageFile" onChange={handleChange} />
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

            <Button variant="primary" type="submit">
              Gem ændringer
            </Button>
          </Form>
        )}
      </Modal.Body>
    </Modal>
  );
};

export default EditTenantDetails;