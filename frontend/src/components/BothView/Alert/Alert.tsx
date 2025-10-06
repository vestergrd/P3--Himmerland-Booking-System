import React, { useState } from "react";
import { Modal, Button } from "react-bootstrap";

// 
interface AlertPopupProps {
  title: string;
  message: string;
  show: boolean;
  onClose: () => void;
  onConfirm?: () => void;
  confirmText?: string;
  cancelText?: string;
}

// AlertPopup component
const AlertPopup: React.FC<AlertPopupProps> = ({
  title,
  message,
  show,
  onClose,
  onConfirm,
  confirmText = "OK",
  cancelText = "Cancel",
}) => {
  return (
    <Modal show={show} onHide={onClose} centered>
      <Modal.Header closeButton>
        <Modal.Title>{title}</Modal.Title>
      </Modal.Header>
      <Modal.Body>{message}</Modal.Body>
      <Modal.Footer>
        <Button variant="secondary" onClick={onClose}>
          {cancelText}
        </Button>
        {onConfirm && (
          <Button variant="primary" onClick={onConfirm}>
            {confirmText}
          </Button>
        )}
      </Modal.Footer>
    </Modal>
  );
};

export default AlertPopup;
