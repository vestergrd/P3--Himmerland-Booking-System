import "bootstrap/dist/css/bootstrap.css";
import "bootstrap/dist/js/bootstrap.bundle.min.js";
import "./App.css";
import { Route, Routes, Navigate } from "react-router-dom";
import ProtectedRoute from "./components/ProtectedRoute";
import { getUserRole, defaultHomePages, UserRole } from "./utils/authConfig";
// Pages
import HomePage from "./pages/HomePage";
import LoginPage from "./pages/LoginPage";
import SignUpPage from "./pages/SignUpPage";
import AccountPage from "./pages/AccountPage";
import CTAccountPage from "./pages/CTAccountPage";
import CToverviewPage from "./pages/CTOverviewPage";
import CTresourcePage from "./pages/CTresourcePage";
import CTtenantOverview from "./pages/CTtenantOverview";
import OwnBookingsPage from "./pages/OwnBookingsPage";
import NotFoundPage from "./pages/NotFoundPage";
import ContactPage from "./pages/ContactPage";
import { DarkModeProvider } from "./components/DarkModeContext";

const routesConfig: {
  path: string;
  component: JSX.Element;
  allowedRoles: UserRole[];
}[] = [
  // Tenant routes
  { path: "/hjem", component: <HomePage />, allowedRoles: ["ROLE_TENANT"] },
  {
    path: "/konto",
    component: <AccountPage />,
    allowedRoles: ["ROLE_TENANT", "ROLE_ADMIN"],
  },
  {
    path: "/mine-reservationer",
    component: <OwnBookingsPage />,
    allowedRoles: ["ROLE_TENANT"],
  },
  {
    path: "/kontakt",
    component: <ContactPage />,
    allowedRoles: ["ROLE_TENANT"],
  },
  // Admin routes
  {
    path: "/admin-overblik",
    component: <CToverviewPage />,
    allowedRoles: ["ROLE_ADMIN"],
  },
  {
    path: "/ressource-overblik",
    component: <CTresourcePage />,
    allowedRoles: ["ROLE_ADMIN"],
  },
  {
    path: "/beboer-overblik",
    component: <CTtenantOverview />,
    allowedRoles: ["ROLE_ADMIN"],
  },
  {
    path: "/konto-admin",
    component: <CTAccountPage />,
    allowedRoles: ["ROLE_ADMIN"],
  },
];

function App() {
  const role = getUserRole();

  return (
    <DarkModeProvider>
      <Routes>
        {/* Public Routes */}
        <Route path="/login" element={<LoginPage />} />
        <Route path="/opret-konto" element={<SignUpPage />} />
        <Route
          path="/"
          element={
            <Navigate to={role ? defaultHomePages[role] : "/login"} replace />
          }
        />

        {/* Protected Routes */}
        {routesConfig.map(({ path, component, allowedRoles }) => (
          <Route
            key={path}
            path={path}
            element={
              <ProtectedRoute allowedRoles={allowedRoles}>
                {component}
              </ProtectedRoute>
            }
          />
        ))}

        {/* Fallback for undefined routes */}
        <Route path="*" element={<NotFoundPage />} />
      </Routes>
    </DarkModeProvider>
  );
}

export default App;
