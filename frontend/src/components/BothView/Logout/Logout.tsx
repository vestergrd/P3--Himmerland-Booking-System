import React from 'react';
import { useNavigate } from 'react-router-dom';
import showAlert from '../Alert/AlertFunction';
import ApiService from '../../../utils/ApiService';

interface LogoutButtonProps {
  onLogout?: () => void;
  buttonText?: string;
  className?: string;
}

const LogoutButton: React.FC<LogoutButtonProps> = ({
  onLogout,
  buttonText = 'Log ud',
  className = 'btn btn-danger', 
}) => {
  
  const navigate = useNavigate();

  const handleLogout = async () => {
    // Show an alert before logging out
    showAlert({
      title: 'Log ud',
      message: 'Er du sikker på, at du vil logge ud?',
      onConfirm: async () => {
        if (onLogout) {
          await onLogout();
        }
        try {
          // API call to the logout endpoint
          await ApiService.sendData("logout");

          console.log('User logged out');
          
          // Delete authIndicator from cookies
          document.cookie = 'authIndicator=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';

          // Clears session storage
          window.sessionStorage.clear();
        } catch (error) {
          console.error('Error logging out', error);
        }

        navigate('/login');
      },
    });
  };

  return (
    <button onClick={handleLogout} className={className} style={{ marginBottom: "10px" }}>
      {buttonText}
    </button>
  );
};

export default LogoutButton;
