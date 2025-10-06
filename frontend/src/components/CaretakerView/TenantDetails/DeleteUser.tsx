import React from "react";
import { Button } from "react-bootstrap";
import showAlert from "../../BothView/Alert/AlertFunction";

interface DeleteUserProps {
  tenantId: string;
  onDelete: (tenantId: string) => void;
}

const DeleteUser: React.FC<DeleteUserProps> = ({ tenantId, onDelete }) => {
  const handleDelete = () => {
    showAlert({
      title: "Slet bruger",
      message: "Er du sikker på, at du vil slette denne bruger?",
      onConfirm: () => {
        onDelete(tenantId);
      },
    });
    
  };

  return (
    <Button variant="danger" onClick={handleDelete}>
      Slet
    </Button>
  );
};

export default DeleteUser;