import React, { useState, useEffect } from 'react';
import { Collapse, Button } from 'react-bootstrap';
import CaretakerResourceCard from './CaretakerResourceCard';
import Resource from '../../modelInterfaces/Resource';
import { ResourceType } from '../../../utils/EnumSupport';
import ApiService from '../../../utils/ApiService';
import AddResourceModal from '../AddResourceModal/AddResourceModal';

const CaretakerResourceOverview: React.FC = () => {
  const [resources, setResources] = useState<Resource[]>([]);
  const [showVærktøj, setShowVærktøj] = useState(true);
  const [showGæstehuse, setShowGæstehuse] = useState(true);
  const [showAndet, setShowAndet] = useState(true);
  const [showAddResourceModal, setShowAddResourceModal] = useState(false);
  const [trigger, setTrigger] = useState(false);

  useEffect(() => {
    const fetchResources = async () => {
      let værktøjResources = [];
      let gæstehuseResources = [];
      let andetResources = [];

      //Fetch tools
      try {
        const værktøjResponse = await ApiService.fetchResources(ResourceType.TOOL);
        console.log("tools:", værktøjResponse)
        værktøjResources = værktøjResponse.data;
      } catch (error) {
        console.error("Error fetching TOOL resources:", error);
      }
      
      //Fetch hospitality
      try {
        const gæstehuseResponse = await ApiService.fetchResources(ResourceType.HOSPITALITY);
        console.log("hospitality:", gæstehuseResponse)
        gæstehuseResources = gæstehuseResponse.data;
      } catch (error) {
        console.error("Error fetching HOSPITALITY resources:", error);
      }

      //Fetch utility
      try {
        const andetResponse = await ApiService.fetchResources(ResourceType.UTILITY);
        console.log("utility:", andetResponse)
        andetResources = andetResponse.data;
      } catch (error) {
        console.error("Error fetching UTILITY resources:", error);
      }

      const combinedResources = [
        ...værktøjResources,
        ...gæstehuseResources,
        ...andetResources,
      ];
      setResources(combinedResources);
    };

    fetchResources();
  }, [trigger]);

  //Categorize
  const værktøjResources = resources.filter(
    (resource) => resource.type && resource.type.toLowerCase() === ResourceType.TOOL.toLowerCase() && resource.status !== "deleted"
  );
  const gæstehuseResources = resources.filter(
    (resource) => resource.type && resource.type.toLowerCase() === ResourceType.HOSPITALITY.toLowerCase() && resource.status !== "deleted"
  );
  const andetResources = resources.filter(
    (resource) => resource.type && resource.type.toLowerCase() === ResourceType.UTILITY.toLowerCase() && resource.status !== "deleted"
  );

  const handleEdit = async (updatedResource: Resource, imageFile: File | null) => {
    try {
      const response = await ApiService.updateResource(
        updatedResource,
        ResourceType[updatedResource.type.toUpperCase() as keyof typeof ResourceType],
        imageFile
      );
      
      setResources((prevResources) =>
        prevResources.map((resource) =>
          resource.id === response.data.id ? response.data : resource
        )
      );
      window.dispatchEvent(new Event("resourceEdited"));
      console.log("response:", response)
      setTrigger((prev) => !prev); //used to reload the resources
    } catch (error) {
      console.error('Error updating resource:', error);
    }
  };

  const handleToggleService = async (id: number) => {
    try {
      const resourceToUpdate = resources.find((resource) => resource.id === id);
      if (!resourceToUpdate) {
        console.error("Resource not found for toggling service");
        return;
      }

      const updatedResource = {
        ...resourceToUpdate,
        status: resourceToUpdate.status === 'available' ? 'maintenance' : 'available',
      };
  
      const updateResponse = await ApiService.updateResource(
        updatedResource,
        ResourceType[updatedResource.type.toUpperCase() as keyof typeof ResourceType],
        null
      );

      if(updatedResource.status === "maintenance") {
        const cancelResponse = await ApiService.cancelBookingsForResource(updatedResource.id, updatedResource.type)
        console.log("cancel response:", cancelResponse)
      }
        
  
      setResources((prevResources) =>
        prevResources.map((resource) =>
          resource.id === updateResponse.data.id ? updateResponse.data : resource
        )
      );
      console.log("updateResponse:", updateResponse)
      setTrigger((prev) => !prev); //used to reload the resources
    } catch (error) {
      console.error("Error updating resource status:", error);
    }
  };

  const handleDelete = async (id: number, type: ResourceType) => {
    try {
      await ApiService.deleteResource(id, type);
      setResources((prevResources) => prevResources.filter((resource) => resource.id !== id));
      console.log("right bedore resourceremoved dispatch");
      window.dispatchEvent(new Event("resourceRemoved"));
    } catch (error) {
      console.error('Error deleting resource:', error);
    }
  };

  return (
    <div className="container mt-4 border border-darkgrey border-4 rounded mb-3">
      <h2 className="text-center mb-4"><strong>Ressourcer</strong></h2>

      <div className="text-center mb-4">
        <Button variant="primary" onClick={() => setShowAddResourceModal(true)}>
          Tilføj Ressource
        </Button>
      </div>

      <h3>
        <Button
          variant="secondary"
          onClick={() => setShowVærktøj(!showVærktøj)}
          aria-controls="værktøj-collapse"
          aria-expanded={showVærktøj}
          className="fs-5"
        >
          Værktøj
        </Button>
      </h3>
      <hr />
      <Collapse in={showVærktøj}>
        <div id="værktøj-collapse">
          {værktøjResources.length === 0 ? (
            <p>Ingen værktøj endnu</p>
          ) : (
            værktøjResources.map((resource) => (
              <CaretakerResourceCard
                key={resource.id}
                resource={resource}
                onEdit={handleEdit}
                onToggleService={handleToggleService}
                onDelete={handleDelete}
              />
            ))
          )}
        </div>
      </Collapse>

      <h3>
        <Button
          variant="secondary"
          onClick={() => setShowGæstehuse(!showGæstehuse)}
          aria-controls="gæstehuse-collapse"
          aria-expanded={showGæstehuse}
          className="fs-5"
        >
          Gæstehuse
        </Button>
      </h3>
      <hr />
      <Collapse in={showGæstehuse}>
        <div id="gæstehuse-collapse">
          {gæstehuseResources.length === 0 ? (
            <p>Ingen gæstehuse endnu</p>
          ) : (
            gæstehuseResources.map((resource) => (
              <CaretakerResourceCard
                key={resource.id}
                resource={resource}
                onEdit={handleEdit}
                onToggleService={handleToggleService}
                onDelete={handleDelete}
              />
            ))
          )}
        </div>
      </Collapse>

      <h3>
        <Button
          variant="secondary"
          onClick={() => setShowAndet(!showAndet)}
          aria-controls="andet-collapse"
          aria-expanded={showAndet}
          className="fs-5"
        >
          Andet
        </Button>
      </h3>
      <hr />
      <Collapse in={showAndet}>
        <div id="andet-collapse">
          {andetResources.length === 0 ? (
            <p>Ingen andre ressourcer endnu</p>
          ) : (
            andetResources.map((resource) => (
              <CaretakerResourceCard
                key={resource.id}
                resource={resource}
                onEdit={handleEdit}
                onToggleService={handleToggleService}
                onDelete={handleDelete}
              />
            ))
          )}
        </div>
      </Collapse>

      <AddResourceModal
        show={showAddResourceModal}
        onClose={() => setShowAddResourceModal(false)}
        onTrigger={() => setTrigger((prev) => !prev)}
      />
    </div>
  );
};

export default CaretakerResourceOverview;