import React, { useState, useRef } from "react";
import { Button, Modal, Form } from "react-bootstrap";
import Resource from "../../modelInterfaces/Resource";
import { ResourceType } from "../../../utils/EnumSupport";
import ApiService from "../../../utils/ApiService";
import BaseImage from "../../BaseImage";
import defaultImage from "../../../assets/deafultResourcePic.jpg";
import { validateImage, getCroppedImage } from "../../../utils/pictureSupport";
import ReactCrop, { Crop } from "react-image-crop";
import "react-image-crop/dist/ReactCrop.css";

interface CaretakerResourceCardProps {
  resource: Resource;
  onEdit: (updatedResource: Resource, imageFile: File | null) => void;
  onToggleService: (id: number) => void;
  onDelete: (id: number, resourecType: ResourceType) => void;
}

const CaretakerResourceCard: React.FC<CaretakerResourceCardProps> = ({
  resource,
  onEdit,
  onToggleService,
  onDelete,
}) => {
  const [isEditing, setIsEditing] = useState(false);
  const [editedResource, setEditedResource] = useState(resource);
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

  const fetchImage = async () => {
    try {
      const response = await ApiService.fetchResourcePic(
        resource.type,
        resource.id
      );
      return response.data;
    } catch {
      return null;
    }
  };

  const handleInputChange = async (
    e: React.ChangeEvent<
      HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement
    >
  ) => {
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
          alert(
            "Ugyldig fil type eller for små dimensioner. Billedet skal være PNG/JPG og skal være minimum 300x300 pixels"
          );
          fileInput.value = "";
        }
      }
    } else {
      setEditedResource((prevEditedResource) => ({
        ...prevEditedResource,
        [name]: value,
      }));
    }
  };

  const handleCropComplete = async (crop: Crop) => {
    if (crop.width && crop.height && imgRef.current) {
      const croppedBlob = await getCroppedImage(
        imgRef.current,
        crop,
        `${resource.name}-cropped.jpg`
      );
      if (croppedBlob) {
        setImageFile(
          new File([croppedBlob], "cropped-image.jpg", { type: "image/jpeg" })
        );
      }
    }
  };

  const handleClose = () => {
    setImageSrc(null);
    setImageFile(null);
    setIsEditing(false);
  };

  const handleSave = () => {
    onEdit(editedResource, imageFile);
    console.log("editedresource:", editedResource);
    handleClose();
  };

  return (
    <div className="card mb-3">
      <div className="card-body d-flex justify-content-between align-items-center">
        {isEditing ? (
          <Modal show={isEditing} onHide={handleClose}>
            <Modal.Header closeButton>
              <Modal.Title>Rediger Ressource</Modal.Title>
            </Modal.Header>
            <Modal.Body>
              <Form>
                <Form.Group controlId="resourceName">
                  <Form.Label>Ressource navn</Form.Label>
                  <Form.Control
                    type="text"
                    name="name"
                    value={editedResource.name}
                    onChange={handleInputChange}
                  />
                </Form.Group>
                <Form.Group controlId="resourceDescription">
                  <Form.Label>Beskrivelse</Form.Label>
                  <Form.Control
                    type="text"
                    name="description"
                    value={editedResource.description}
                    onChange={handleInputChange}
                  />
                </Form.Group>
                <Form.Group controlId="imageFile">
                  <Form.Label>Billede:</Form.Label>
                  <Form.Control
                    type="file"
                    name="imageFile"
                    onChange={handleInputChange}
                  />
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

                <Button variant="primary" onClick={handleSave}>
                  Gem ændringer
                </Button>
              </Form>
            </Modal.Body>
          </Modal>
        ) : (
          <>
            <div className="resource-info">
              <h3 className="mb-3">{resource.name}</h3>
              <p>
                <strong>Status:</strong>{" "}
                {resource.status === "available" ? "Aktiv" : "Service"}
              </p>
              <p>
                <strong>Beskrivelse:</strong> {resource.description}
              </p>
              <p>
                <strong>Antal:</strong> {resource.capacity}
              </p>
            </div>
            <BaseImage
              fetchImage={fetchImage}
              defaultImage={defaultImage}
              altText={`${resource.name} image`}
              imageStyle={{
                width: "100%",
                height: "auto",
                maxWidth: "100px",
                maxHeight: "100px",
                objectFit: "contain"
              }}
            />

            <div className="resource-actions">
              <Button
                variant="outline-secondary"
                onClick={() => setIsEditing(true)}
              >
                Rediger
              </Button>
              <Button
                variant={
                  resource.status === "maintenance" ? "success" : "warning"
                }
                onClick={() => onToggleService(resource.id)}
                className="ms-2"
              >
                {resource.status === "maintenance" ? "Aktiver" : "Servicer"}
              </Button>
              <Button
                variant="danger"
                onClick={() =>
                  onDelete(
                    resource.id,
                    ResourceType[
                      resource.type.toUpperCase() as keyof typeof ResourceType
                    ]
                  )
                }
                className="ms-2"
              >
                Slet
              </Button>
            </div>
          </>
        )}
      </div>
    </div>
  );
};

export default CaretakerResourceCard;
