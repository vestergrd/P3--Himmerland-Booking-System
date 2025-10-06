import React from "react";
import { Navigate } from "react-router-dom";
import { getUserRole, defaultHomePages, UserRole } from "../utils/authConfig";

interface ProtectedRouteProps {
  children: React.ReactNode;
  allowedRoles: UserRole[];
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ children, allowedRoles }) => {
  const role = getUserRole();

  if (!role) {
    return <Navigate to="/login" replace />;
  }

  if (!allowedRoles.includes(role)) {
    return <Navigate to={defaultHomePages[role]} replace />;
  }

  return <>{children}</>;
};

export default ProtectedRoute;
