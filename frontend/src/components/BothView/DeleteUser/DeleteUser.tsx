import React from 'react';
import { Button } from 'react-bootstrap';
import showAlert from '../Alert/AlertFunction';
import ApiService from '../../../utils/ApiService';
import { useNavigate } from 'react-router-dom';

interface DeleteUserButtonProps {
  userId: number;
}

const DeleteUserButton: React.FC<DeleteUserButtonProps> = ({ userId }) => {
  const navigate = useNavigate();

const handleDeleteUser = async () => {
    const isAdmin = document.cookie.includes("ROLE_ADMIN");
    
    if (isAdmin) {
        showAlert({
            title: "Kan ikke slette bruger",
            message: "Administratorer kan ikke slettes.",
            confirmText: "OK",
            onConfirm: () => {},
        });
        return;
    }

    showAlert({
        title: "Slet bruger",
        message: "Er du sikker på at du vil slette din bruger?",
        confirmText: "Slet",
        onConfirm: async () => {
            try {
                const response = await ApiService.deleteTenant(userId);
            
                console.log('User logged out');

                document.cookie = 'authIndicator=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';
                
                window.sessionStorage.clear();
                console.log("Deleted User:", response.data);
            } catch (error) {
                console.error("Error deleting user:", error);
            }
            navigate("/login");
        },
    });
};

  return (
    <Button variant="danger" onClick={handleDeleteUser}>
      Slet bruger
    </Button>
  );
};

export default DeleteUserButton;