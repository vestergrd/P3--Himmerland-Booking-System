import React from "react";
import { FaRegCheckCircle, FaTools, FaTimesCircle } from 'react-icons/fa';

interface CardActionProps {
  status: string;
  toggleModal: () => void;
}

const CardAction: React.FC<CardActionProps> = ({ status, toggleModal }) => {
  if (status === "available") {
    return (
      <button className="btn btn-success" onClick={toggleModal}>
        <FaRegCheckCircle /> Reserver
      </button>
    );
  } else if (status === "maintenance") {
    return (
      <button className="btn btn-warning" disabled>
        <FaTools /> Under vedligeholdelse
      </button>
    );
  } else {
    return (
      <button className="btn btn-secondary" disabled>
        <FaTimesCircle /> Utilgængelig
      </button>
    );
  }
};

export default CardAction;
