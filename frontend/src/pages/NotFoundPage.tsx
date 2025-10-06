import { useNavigate } from "react-router-dom";

const NotFoundPage = () => {
  const navigate = useNavigate();

  return (
    <div className="NotFoundContainer">
    <h1>{`404 Siden findes ikke :(`}</h1>
      <button
        className="NotFoundButton"
        onClick={() => navigate("/hjem")}
      >
        Tilbage til hjem
      </button>
    </div>
  );
};

export default NotFoundPage;
