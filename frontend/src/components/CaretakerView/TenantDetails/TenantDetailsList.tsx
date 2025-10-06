import React, { useEffect, useState } from "react";
import ApiService from "../../../utils/ApiService";
import Tenant from "../../modelInterfaces/Tenant";
import BookForTenant from "../../CaretakerView/TenantDetails/BookForTenant";
import { Modal, Button } from "react-bootstrap";
import FilterSearch from "./FilterSearch";
import DeleteUser from "./DeleteUser";
import EditTenantDetails from "./EditTenantDetails";
import ProfilePicture from "../../BothView/Settings/ProfilePicture";

const TenantDetailsList: React.FC = () => {
  const [tenants, setTenants] = useState<Tenant[]>([]);
  const [filteredTenants, setFilteredTenants] = useState<Tenant[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [showModal, setShowModal] = useState<boolean>(false);
  const [selectedTenant, setSelectedTenant] = useState<Tenant | null>(null);
  const [showEditModal, setShowEditModal] = useState<boolean>(false);

  useEffect(() => {
    const fetchTenants = async () => {
      try {
        const response = await ApiService.fetchData("admin/getAllTenants");
        if (Array.isArray(response.data)) {
          setTenants(response.data);
          setFilteredTenants(response.data);
        } else {
          setError("Unexpected response format.");
        }
      } catch (err) {
        console.error("Error fetching tenants:", err);
        setError("Failed to fetch tenants.");
      } finally {
        setLoading(false);
      }
    };

    fetchTenants();
  }, []);

  const handleBookClick = async (tenant: Tenant) => {
    setSelectedTenant(tenant);
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setShowModal(false);
    setSelectedTenant(null);
  };

  const handleSearch = (filters: { name: string; address: string; phone: string }) => {
    const { name, address, phone } = filters;
    const filtered = tenants.filter((tenant) =>
      tenant.name.toLowerCase().includes(name.toLowerCase()) &&
      tenant.houseAddress.toLowerCase().includes(address.toLowerCase()) &&
      tenant.mobileNumber.includes(phone)
    );
    setFilteredTenants(filtered);
  };

  const handleDelete = async (tenantId: string) => {
    try {
      await ApiService.deleteData(`admin/deleteTenant/${tenantId}`);
      setTenants((prev) => prev.filter((tenant) => tenant.id !== Number(tenantId)));
      setFilteredTenants((prev) => prev.filter((tenant) => tenant.id !== Number(tenantId)));
    } catch (error) {
      console.error("Failed to delete tenant:", error);
    }
  }

  const handleUpdate = async (updatedTenant: Tenant) => {
    try {
      //await ApiService.editUserAdmin(updatedTenant.id, updatedTenant);
      setTenants((prev) => prev.map((tenant) => (tenant.id === updatedTenant.id ? updatedTenant : tenant)));
      setFilteredTenants((prev) => prev.map((tenant) => (tenant.id === updatedTenant.id ? updatedTenant : tenant)));
    } catch (error) {
      console.error("Failed to update tenant:", error);
    }
  }

  const handleEditClick = (tenant: Tenant) => {
    setSelectedTenant(tenant);
    setShowEditModal(true);
  };

  const handleCloseEditModal = () => {
    setShowEditModal(false);
    setSelectedTenant(null);
  };

  if (loading) {
    return <div>Loading...</div>;
  }

  if (error) {
    return <div>{error}</div>;
  }

  return (
    <div className="tenant-overview">
      <FilterSearch onSearch={handleSearch} />

      <div className="tenant-list">
        <h1>Udlejere</h1>
        {filteredTenants.map((tenant) => (
          <div key={tenant.id} className="tenant-card">
            <div className="tenant-info">
              <ProfilePicture imageSource={`admin/getTenant/${tenant.id}/profilePicture`} />
              <h2>{tenant.name}</h2>
              <p>Adresse: {tenant.houseAddress}</p>
              <p>Telefonnummer: {tenant.mobileNumber}</p>
              <p>Email: {tenant.email}</p>
            </div>
            <div className="tenant-actions">
              <button className="btn btn-success" onClick={() => handleBookClick(tenant)}>Book for {tenant.name}</button>
              {showEditModal && selectedTenant?.id === tenant.id && (
                <EditTenantDetails tenantId={tenant.id.toString()} onUpdate={handleUpdate} onClose={handleCloseEditModal} />
              )}
              <button className="btn btn-success" onClick={() => handleEditClick(tenant)}>Ændre</button>
              <DeleteUser tenantId={tenant.id.toString()} onDelete={handleDelete} />
            </div>
          </div>
        ))}
      </div>

      <Modal show={showModal} onHide={handleCloseModal}>
        <Modal.Header closeButton>
          <Modal.Title>Book for {selectedTenant?.name}</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {selectedTenant && (
            <BookForTenant
              onClose={() => handleCloseModal()}
              selectedTenant={selectedTenant}
            />
          )}
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={handleCloseModal}>
            Close
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
};

export default TenantDetailsList;