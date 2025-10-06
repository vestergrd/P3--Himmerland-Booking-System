import React, { useState, useEffect } from 'react';
import { Modal, Button } from 'react-bootstrap';
import ApiService from '../../../utils/ApiService';

interface CaretakerBooking {
  id: number;
  name: string;
  resourceName: string;
  startDate: Date;
  endDate: Date;
  pickupTime: string;
  dropoffTime: string;
  mobileNumber: string;
  houseAddress: string;
  email: string;
  status: string;
  receiverName: string;
  handoverName: string;
  isFutureBooking: boolean;
  isPastBooking: boolean;
}

interface CaretakerBookingCardProps {
  booking: CaretakerBooking;
  onCancel: (id: number) => void;
  onComplete: (id: number) => void;
  trigger: () => void
}

const CaretakerBookingCard: React.FC<CaretakerBookingCardProps> = ({ booking, onCancel, onComplete, trigger }) => {
  const [showModal, setShowModal] = useState(false);
  const [showModalNames, setShowModalNames] = useState(false);
  const [selectedName, setSelectedName] = useState<string | null>(null);
  const [showConfirmButton, setShowConfirmButton] = useState(false);
  const [caretakers, setCaretakers] = useState<string[]>([]);
  const [trigger2, setTrigger2] = useState(false);

  useEffect(() => {
    const fetchCaretakerNames = async () => {
      try {
        const response = await ApiService.getAllCaretakerNames();
        setCaretakers(response.data);
      } catch (error) {
        console.error("Error fetching caretaker names:", error);
      }
    };

    fetchCaretakerNames();
  }, [trigger2]);

  const handleClose = () => setShowModal(false);
  const handleShow = () => setShowModal(true);

  const handleCloseNames = () => {
    setShowModalNames(false);
    setSelectedName(null);
    setShowConfirmButton(false);
  };
  const handleShowNames = () => {
    setTrigger2((prev) => !prev);
    setShowModalNames(true);
  }

  const handleNameSelect = (name: string) => {
    setSelectedName(name);
    setShowConfirmButton(true);
  };

  const handleConfirm = async () => {
    if (selectedName) {
      try {
        const formattedName = selectedName.replace(/['"]+/g, '');

        if (booking.status === "CONFIRMED" || booking.status === "LATE") {
          await ApiService.setReceiverName(booking.id, formattedName);
        } else {
          await ApiService.setHandoverName(booking.id, formattedName);
        }

        onComplete(booking.id);
        trigger();
      } catch (error) {
        console.error("Error handling caretaker name:", error);
      }

      handleCloseNames();
    } else {
      console.warn("No name selected");
    }
  };

  return (
    <div className="card mb-3">
      <div className="card-body d-flex justify-content-between align-items-center">
        <div>
          <strong>{booking.name}</strong>: {booking.resourceName}
          <div>Start: {booking.startDate.toLocaleDateString()}</div>
          <div>Slut: {booking.endDate.toLocaleDateString()}</div>
          <div>Adresse: {booking.houseAddress}</div>
        </div>
        <div>
          <Button variant="outline-secondary" onClick={handleShow}>
            Detaljer
          </Button>
          {(booking.isFutureBooking || (booking.startDate.toDateString() === new Date().toDateString() && booking.status === "PENDING")) && (
            <>
              <Button variant="danger" className="ms-2" onClick={() => onCancel(booking.id)}>
                Annuller
              </Button>
            </>
          )}
          {(booking.isFutureBooking || (booking.startDate.toDateString() === new Date().toDateString() && booking.status === "PENDING")) && (
            <>
              <Button variant="success" className="ms-2" onClick={handleShowNames}>
                Udlever
              </Button>
            </>
          )}

          {!booking.isFutureBooking && !booking.isPastBooking && booking.status === "CONFIRMED" && (
            <Button variant="success" className="ms-2" onClick={handleShowNames}>
              Modtag
            </Button>
          )}
          {booking.status === "LATE" && (
            <Button variant="success" className="ms-2" onClick={handleShowNames}>
              Modtag
            </Button>
          )}
          {booking.status === "LATE" && (
            <div className="text-danger">
            <strong>Afleveringstid overskredet!</strong>
            </div>
          )}
        </div>
      </div>

      {/* Modal for Booking Details */}
      <Modal show={showModal} onHide={handleClose}>
        <Modal.Header closeButton>
          <Modal.Title>Booking detaljer</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <h4>Beboer information:</h4>
          <p><strong>Navn:</strong> {booking.name}</p>
          <p><strong>Adresse:</strong> {booking.houseAddress}</p>
          <p><strong>Telefon:</strong> {booking.mobileNumber}</p>
          <p><strong>Email:</strong> {booking.email}</p>
          <br />
          <h4>Reservation information:</h4>
          <p><strong>Ressource:</strong> {booking.resourceName}</p>
          <p><strong>Startdato:</strong> {booking.startDate.toLocaleDateString()} kl. {booking.pickupTime}</p>
          <p><strong>Slutdato:</strong> {booking.endDate.toLocaleDateString()} kl. {booking.dropoffTime}</p>
          <p><strong>Udlevering:</strong> {booking.handoverName}</p>
          <p><strong>Modtagelse:</strong> {booking.receiverName}</p>
        </Modal.Body>
      </Modal>

      {/* Modal for selecting caretaker names */}
      <Modal show={showModalNames} onHide={handleCloseNames}>
        <Modal.Header closeButton>
          <Modal.Title>Vælg modtager</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <p><strong>Medarbejder navne:</strong></p>
          <div>
            {caretakers.map((name, index) => (
              <Button key={index} variant="outline-primary" onClick={() => handleNameSelect(name)} className="me-2 mb-2">
                {name}
              </Button>
            ))}
          </div>
          {showConfirmButton && (
            <Button variant="success" onClick={handleConfirm} className="mt-3">
              Bekræft
            </Button>
          )}
        </Modal.Body>
      </Modal>
    </div>
  );
};

export default CaretakerBookingCard;