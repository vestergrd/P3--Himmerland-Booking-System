import React, { useState, useEffect } from "react";
import ApiService from "../../../utils/ApiService";
import { Button, Form } from "react-bootstrap";

const CaretakerOptions = () => {
  const [caretakers, setCaretakers] = useState<string[]>([]);
  const [newCaretakerName, setNewCaretakerName] = useState<string>("");
  const [isEditing, setIsEditing] = useState<boolean>(false);

  useEffect(() => {
    const fetchCaretakerNames = async () => {
      try {
        const response = await ApiService.getAllCaretakerNames();
        setCaretakers(response.data);
      } catch (error) {
        console.error("Error fetching caretaker names:", error);
      }
    };

    fetchCaretakerNames();
  }, []);

  const handleAddCaretakerName = async () => {
    if (!newCaretakerName.trim()) {
      console.log("Please enter a valid name");
      return;
    }

    try {
      await ApiService.addCaretakerName(newCaretakerName);
      setCaretakers((prev) => [...prev, newCaretakerName]);
      setNewCaretakerName("");
      setIsEditing(false);
    } catch (error) {
      console.error("Error adding caretaker name:", error);
    }
  };

  const handleDeleteCaretakerName = async (caretakerName: string) => {
    try {
      await ApiService.removeCaretakerName(caretakerName);
      setCaretakers((prev) => prev.filter((name) => name !== caretakerName));
    } catch (error) {
      console.error("Error deleting caretaker name:", error);
    }
  };

  return (
    <div>
      <button
        className="btn btn-success"
        type="button"
        data-bs-toggle="offcanvas"
        data-bs-target="#offcanvasExample"
        aria-controls="offcanvasExample"
      >
        Medarbejder liste
      </button>

      <div
        className="offcanvas offcanvas-start"
        tabIndex={-1}
        id="offcanvasExample"
        aria-labelledby="offcanvasExampleLabel"
      >
        <div className="offcanvas-header">
          <h5 className="offcanvas-title" id="offcanvasExampleLabel">
            Medarbejder liste
          </h5>
          <button
            type="button"
            className="btn-close"
            data-bs-dismiss="offcanvas"
            aria-label="Close"
          ></button>
        </div>
        <div className="offcanvas-body">
          <div>
            <h6>Medarbejdere</h6>
            <ul className="list-group" style={{ textAlign: "center" }}>
              {caretakers.length > 0 ? (
                caretakers.map((name, index) => (
                  <li
                    key={index}
                    className="list-group-item d-flex justify-content-between align-items-center"
                  >
                    {name}
                    {isEditing && (
                      <Button
                        variant="outline-danger"
                        size="sm"
                        onClick={() => handleDeleteCaretakerName(name)}
                      >
                        Slet
                      </Button>
                    )}
                  </li>
                ))
              ) : (
                <li className="list-group-item text-muted">
                  Ingen medarbejdere tilføjet
                </li>
              )}
            </ul>
          </div>
          <div className="mt-3">
            {!isEditing && (
              <Button
                variant="primary"
                onClick={() => setIsEditing(true)}
                className="mt-3"
              >
                Rediger
              </Button>
            )}

            {isEditing && (
              <>
                <Form.Group>
                  <Form.Label>Tilføj medarbejdernavn</Form.Label>
                  <Form.Control
                    type="text"
                    placeholder="Indtast navn"
                    value={newCaretakerName}
                    onChange={(e) => setNewCaretakerName(e.target.value)}
                  />
                </Form.Group>
                <Button
                  variant={newCaretakerName == "" ? "outline-secondary" : "outline-success"}
                  onClick={handleAddCaretakerName}
                  className="mt-3"
                  disabled={newCaretakerName == ""}
                >
                  Bekræft
                </Button>
                <Button
                  variant="outline-danger"
                  onClick={() => setIsEditing(false)}
                  className="mt-3 ms-2"
                >
                  Cancel
                </Button>
              </>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default CaretakerOptions;