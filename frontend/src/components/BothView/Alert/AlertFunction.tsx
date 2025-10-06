import React from 'react';
import ReactDOM from 'react-dom';
import { Modal, Button } from 'react-bootstrap';

interface AlertOptions {
  title: string;
  message: string;
  onConfirm?: () => void;
  confirmText?: string;
  cancelText?: string;
}

// Function to dynamically create and show an alert
function showAlert({
  title,
  message,
  onConfirm,
  confirmText = 'OK',
  cancelText = 'Cancel',
}: AlertOptions) {
  // Create a div element to render the modal into
  const container = document.createElement('div');
  document.body.appendChild(container);

  // Function to remove the modal from the DOM
  const closeAlert = () => {
    ReactDOM.unmountComponentAtNode(container);
    container.remove();
  };

  // Render the modal
  ReactDOM.render(
    <Modal show onHide={closeAlert} centered>
      <Modal.Header closeButton>
        <Modal.Title>{title}</Modal.Title>
      </Modal.Header>
      <Modal.Body>{message}</Modal.Body>
      <Modal.Footer>
        <Button variant="secondary" onClick={closeAlert}>
          {cancelText}
        </Button>
        {onConfirm && (
          <Button
            variant="success"
            onClick={() => {
              onConfirm();
              closeAlert();
            }}
          >
            {confirmText}
          </Button>
        )}
      </Modal.Footer>
    </Modal>,
    container
  );
}

export default showAlert;