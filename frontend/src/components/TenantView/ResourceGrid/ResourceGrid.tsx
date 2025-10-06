import React, { useState, useEffect } from "react";
import ResourceCard from "./ResourceCard";
import ApiService from "../../../utils/ApiService";
import { ResourceType } from "../../../utils/EnumSupport";
import Resource from "../../modelInterfaces/Resource";
import { Tab, Tabs } from 'react-bootstrap';
import { useNavigate } from "react-router-dom";

const mapToResourceType = (type: string | null | undefined): ResourceType | undefined => {
  if (type) {
    const upperCaseType = type.toUpperCase();
    if (upperCaseType in ResourceType) {
      return ResourceType[upperCaseType as keyof typeof ResourceType];
    }
  }
  console.error("Invalid resource type:", type);
  return undefined;
};

const ResourceGrid: React.FC = () => {
  const [tools, setTools] = useState<Resource[]>([]);
  const [hospitalities, setHospitalities] = useState<Resource[]>([]);
  const [otherResources, setOtherResources] = useState<Resource[]>([]);
  const [activeTab, setActiveTab] = useState<string>('tools');
  const [bookingCount, setBookingCount] = useState<number>(0);
  const navigate = useNavigate();

  useEffect(() => {
    const storedBookingCount = sessionStorage.getItem("bookingCount");
    if (storedBookingCount) {
      setBookingCount(Number(storedBookingCount));
    }

    const fetchResources = async () => {
      try {
        const toolsResponse = await ApiService.fetchResources(ResourceType.TOOL);
        const mappedTools = toolsResponse.data
        .filter((resource: Resource) => resource.status != "deleted")
        .map((resource: Resource) => ({
          ...resource,
          type: mapToResourceType(resource.type),
        }));
        setTools(mappedTools);

        const utilitiesResponse = await ApiService.fetchResources(ResourceType.UTILITY);
        const mappedUtilities = utilitiesResponse.data
        .filter((resource: Resource) => resource.status != "deleted")
        .map((resource: Resource) => ({
          ...resource,
          type: mapToResourceType(resource.type),
        }));
        setOtherResources(mappedUtilities);

        const hospitalitiesResponse = await ApiService.fetchResources(ResourceType.HOSPITALITY);
        const mappedHospitalities = hospitalitiesResponse.data
        .filter((resource: Resource) => resource.status != "deleted")
        .map((resource: Resource) => ({
          ...resource,
          type: mapToResourceType(resource.type),
        }));
        setHospitalities(mappedHospitalities);
      } catch (error) {
        console.error("Error fetching resources", error);
      }
    };

    fetchResources();
  }, []);

  useEffect(() => {
    const handleBookingUpdated = () => {
      const newBookingCount = bookingCount + 1;
      setBookingCount(newBookingCount);
      sessionStorage.setItem("bookingCount", newBookingCount.toString());
    };

    window.addEventListener("bookingsUpdated", handleBookingUpdated);
    return () => {
      window.removeEventListener("bookingsUpdated", handleBookingUpdated);
    };
  }, [bookingCount]);

  const getActiveTabClass = (tab: string) => (activeTab === tab ? 'active-tab' : '');

  return (
    <div className="container mt-4 border border-darkgrey border-4 rounded mb-3">
      {/* Resource Categories */}
      <Tabs 
        activeKey={activeTab} 
        onSelect={(key) => setActiveTab(key!)}
        id="resource-tabs" 
        className="mb-3"
      >
        <Tab 
          eventKey="tools" 
          title={<span className={`resource-heading ${getActiveTabClass('tools')}`}>Værktøj</span>}
        >
          <div className="row">
            {tools.length > 0 ? (
              tools.map((resource) => (
                <ResourceCard key={resource.id} resource={resource} />
              ))
            ) : (
              <p>Ingen værktøj tilgængelig</p>
            )}
          </div>
        </Tab>
        <Tab 
          eventKey="hospitalities" 
          title={<span className={`resource-heading ${getActiveTabClass('hospitalities')}`}>Gæstehuse & Lokaler</span>}
        >
          <div className="row">
            {hospitalities.length > 0 ? (
              hospitalities.map((resource) => (
                <ResourceCard key={resource.id} resource={resource} />
              ))
            ) : (
              <p>Ingen Gæstehuse eller Lokater tilgængelig</p>
            )}
          </div>
        </Tab>
        <Tab 
          eventKey="others" 
          title={<span className={`resource-heading ${getActiveTabClass('others')}`}>Andet</span>}
        >
          <div className="row">
            {otherResources.length > 0 ? (
              otherResources.map((resource) => (
                <ResourceCard key={resource.id} resource={resource} />
              ))
            ) : (
              <p>Intet tilgængeligt</p>
            )}
          </div>
        </Tab>
      </Tabs>
    </div>
  );
};

export default ResourceGrid;
