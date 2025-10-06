import React, { useState } from 'react';
import CardImage from "./CardImage";
import CardBody from "./CardBody";
import CardAction from "./CardActionButton";
import CreateBookingModal from '../CreateBookingModal/CreateBookingModal';
import Placeholder from '../../BothView/Placeholder/Placeholder';
import Resource from '../../modelInterfaces/Resource';

interface ResourceCardProps {
  resource: Resource;
  loading?: boolean;
}

const ResourceCard: React.FC<ResourceCardProps> = ({ resource, loading }) => {
  const [isModalOpen, setIsModalOpen] = useState(false);

  const handleBookingAdded = () => {
    console.log("Booking request sent");
    setIsModalOpen(false);
  };

  return (
    <div className="col-sm-4 col-md-2 mb-4">
      <div className="card shadow-sm">
        {loading ? (
          <>
            <Placeholder width="100%" height="150px" color="secondary" />
            <Placeholder width="100%" height="1rem" color="secondary" />
            <Placeholder width="100%" height="1rem" color="secondary" />
            <Placeholder width="100%" height="1rem" color="secondary" />
          </>
        ) : (
          <>
            <CardImage id={resource.id} type={resource.type} name={resource.name} />
            <CardBody
              name={resource.name}
              description={resource.description}
            />
            <div className="card-footer">
              <CardAction status={resource.status} toggleModal={() => setIsModalOpen(true)} />
            </div>
          </>
        )}
      </div>

      {isModalOpen && (
        <CreateBookingModal
          resource={resource}
          show={isModalOpen}
          booking={null}
          onBookingAdded={handleBookingAdded}
          onClose={() => setIsModalOpen(false)}
        />
      )}
    </div>
  );
};

export default ResourceCard;
