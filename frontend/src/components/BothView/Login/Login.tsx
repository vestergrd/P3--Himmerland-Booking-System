import React, { useState, useRef } from "react";
import { useNavigate } from "react-router-dom";
import ApiService from "../../../utils/ApiService";
import { isAxiosError } from "axios";

interface Credentials {
  username: string;
  password: string;
}

const Login: React.FC = () => {
  const navigate = useNavigate();
  const [credentials, setCredentials] = useState<Credentials>({
    username: "",
    password: "",
  });
  const [passwordVisible, setPasswordVisible] = useState<boolean>(false);
  const [errorMessage, setErrorMessage] = useState<string>("");
  const usernameRef = useRef<HTMLInputElement>(null);
  const passwordRef = useRef<HTMLInputElement>(null);
  const loginButtonRef = useRef<HTMLButtonElement>(null);
  const createAccountButtonRef = useRef<HTMLButtonElement>(null);

  const validateForm = (): boolean => {
    const { username, password } = credentials;
    if (!username || !password) {
      setErrorMessage("Udfyld venligst alle felter.");
      return false;
    }
    return true;
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>): void => {
    const { id, value } = e.target;
    setCredentials((prevCredentials) => ({
      ...prevCredentials,
      [id]: value,
    }));
  };

  const handleSubmit = async (
    e: React.FormEvent<HTMLFormElement>
  ): Promise<void> => {
    e.preventDefault();
    setErrorMessage("");

    if (!validateForm()) return;

    try {
      const response = await ApiService.login(credentials);

      if (response.status === 200) {
        navigate("/hjem");
      } else {
        setErrorMessage("Forkert brugernavn eller adgangskode.");
      }
    } catch (error) {
      if (isAxiosError(error)) {
        if (error.response && error.response.status === 401) {
          setErrorMessage("Forkert brugernavn eller adgangskode.");
        } else {
          setErrorMessage("En fejl opstod");
        }
      } else {
        setErrorMessage("En ukendt fejl opstod");
      }
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "ArrowDown" || e.key === "Tab") {
      e.preventDefault();
      if (e.target === usernameRef.current && passwordRef.current) {
        passwordRef.current.focus();
      } else if (e.target === passwordRef.current && loginButtonRef.current) {
        loginButtonRef.current.focus();
      } else if (e.target === loginButtonRef.current && createAccountButtonRef.current) {
        createAccountButtonRef.current.focus();
      }
    }

    if (e.key === "ArrowUp") {
      e.preventDefault();
      if (e.target === createAccountButtonRef.current && loginButtonRef.current) {
        loginButtonRef.current.focus();
      } else if (e.target === loginButtonRef.current && passwordRef.current) {
        passwordRef.current.focus();
      } else if (e.target === passwordRef.current && usernameRef.current) {
        usernameRef.current.focus();
      }
    }
  };

  return (
    <div className="d-flex justify-content-center align-items-center vh-100" style={{ margin: 0 }}>
      <form className="w-25 p-5 border rounded shadow-lg bg-white" onSubmit={handleSubmit}>
        <h4 className="text-center mb-4" style={{ color: "#388e3c" }}>
          Login
        </h4>

        {errorMessage && (
          <div className="alert alert-danger mb-3 text-center">
            {errorMessage}
          </div>
        )}

        <div className="mb-3">
          <label htmlFor="username" className="form-label">
            Brugernavn
          </label>
          <input
            ref={usernameRef}
            type="text"
            className="form-control form-control-lg"
            id="username"
            value={credentials.username}
            onChange={handleChange}
            placeholder="Skriv brugernavn"
            required
            onKeyDown={handleKeyDown}
          />
        </div>

        <div className="form-group mb-4">
          <label htmlFor="password" className="form-label">
            Adgangskode
          </label>
          <div className="input-group">
            <input
              ref={passwordRef}
              type={passwordVisible ? "text" : "password"}
              className="form-control form-control-lg"
              id="password"
              value={credentials.password}
              onChange={handleChange}
              placeholder="Skriv adgangskode"
              required
              onKeyDown={handleKeyDown}
            />
            <button
              type="button"
              className="btn btn-outline-secondary"
              aria-label="Toggle password visibility"
              onClick={() => setPasswordVisible(!passwordVisible)}
            >
              {passwordVisible ? "Hide" : "Show"}
            </button>
          </div>
        </div>

        <button
          ref={loginButtonRef}
          type="submit"
          className="btn btn-success btn-lg btn-block mt-4"
        >
          Login
        </button>

        <div className="text-center mt-3">
          <button
            ref={createAccountButtonRef}
            type="button"
            className="btn btn-outline-success btn-lg"
            onClick={() => navigate("/opret-konto")}
          >
            Lav bruger
          </button>
        </div>
      </form>
    </div>
  );
};

export default Login;
