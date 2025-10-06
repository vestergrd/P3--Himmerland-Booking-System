import React, { useState, useEffect } from "react";
import "react-calendar/dist/Calendar.css";
import ApiService from "../../../utils/ApiService";
import Resource from "../../modelInterfaces/Resource";
import Tenant from "../../modelInterfaces/Tenant";

import CreateBookingModal from "../../TenantView/CreateBookingModal/CreateBookingModal";

interface BookForTenantProps {
    onClose: () => void;
    selectedTenant: Tenant;
}

const BookForTenant: React.FC<BookForTenantProps> = ({
    onClose,
    selectedTenant,
}) => {
    const [resources, setResources] = useState<Resource[]>([]);
    const [selectedResource, setSelectedResource] = useState<Resource | null>(null);
    const [isCreateBookingOpen, setIsCreateBookingOpen] = useState(true);

    useEffect(() => {
        const fetchResources = async () => {
            try {
                const toolResponse = await ApiService.fetchData("tool/get-all");
                const utilityResponse = await ApiService.fetchData("utility/get-all");
                const hospitalityResponse = await ApiService.fetchData("hospitality/get-all");

                const allResources = [
                    ...(toolResponse.data as Resource[]),
                    ...(utilityResponse.data as Resource[]),
                    ...(hospitalityResponse.data as Resource[]),
                ];

                filterAndSetResources(allResources)
            } catch (error) {
                console.error("Failed to fetch resources:", error);
            }
        };

        fetchResources();
    }, []);

    const filterAndSetResources = (resourceList: Resource[]) => {
        const filteredResourceList = resourceList.filter((resource) => resource.status !== "deleted" && resource.status !== "maintenance");
        setResources(filteredResourceList)
    }

    const handleClose = () => {
        onClose();
        setIsCreateBookingOpen(false);
    }

    return (
        <div>
            <div className="dropdowns" style={{ marginBottom: "5px" }}>
                <select
                    value={selectedResource?.id || ""}
                    onChange={(e) =>
                        setSelectedResource(resources.find((resource) => resource.id === Number(e.target.value)) || null)
                    }
                >
                    <option value="">Vælg resurse</option>
                    {resources.map((resource) => (
                        <option key={resource.id} value={resource.id}>
                            {resource.name}
                        </option>
                    ))}
                </select>
            </div>
            {selectedResource && isCreateBookingOpen && (
                <CreateBookingModal
                    resource={selectedResource}
                    show={true}
                    booking={null}
                    onBookingAdded={() => handleClose()}
                    onClose={() => handleClose()}
                    tenant={selectedTenant}
                />
            )}
        </div>
    );
};

export default BookForTenant;