import React, { useEffect, useState } from "react";
import { useLocation, Link, useNavigate} from "react-router-dom";
import bgImage from "./login.jpeg";
import "./Home.css";

function Login() {
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const res = await fetch("http://localhost:8080/api/users/signin", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password }),
      });

      if (res.ok) {
        const user = await res.json();
        localStorage.setItem("user", JSON.stringify(user));
        setMessage(`Welcome ${user.fullname}! Setting up your account...`);

        if (user.role === "athlete") {
          await createAthlete(user.id);
          setTimeout(() => navigate("/athlete"), 1000);
        } else if (user.role === "coach") {
          await createCoach(user.id);
          setTimeout(() => navigate("/coach"), 1000);
        } else {
          setTimeout(() => navigate("/"), 1000);
        }
      } else {
        const errorData = await res.json();
        setMessage(
          `Login failed: ${errorData.message || "Invalid credentials"}`
        );
      }
    } catch (err) {
      console.error(err);
      setMessage("Login failed: Server error");
    }
  };

  const createAthlete = async (userId) => {
    try {
      const res = await fetch(`http://localhost:8080/api/athletes/${userId}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          sportInterest: "Not specified",
          heightCm: 0,
          weightKg: 0,
          medicalHistory: "",
          parentalConsent: false,
        }),
      });

      if (res.ok) {
            console.log("Athlete profile created");
          } else if (res.status === 409) {
            console.log("Athlete already exists, skipping creation");
          } else {
            console.error("Failed to create athlete profile");
          }
        } catch (err) {
          console.error("Error creating athlete profile", err);
        }
  };

  const createCoach = async (userId) => {
    try {
      const res = await fetch(`http://localhost:8080/api/coaches/${userId}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          specialization: "Not specified",
          experienceYears: 0,
          region:"Hyderabad"
        }),
      });

      if (res.ok) {
        console.log("Coach profile created");
      } else {
        console.error("Failed to create coach profile");
      }
    } catch (err) {
      console.error("Error creating coach profile", err);
    }
  };

return (
    <div className="login-page" style={{ backgroundImage: `url(${bgImage})` }}>
      <div className="login-overlay"></div>

      <div className="login-box">
        <h1 className="login-title">Login to Sportzy</h1>

        <form onSubmit={handleSubmit}>
          <div className="mb-3 text-start">
            <label htmlFor="email" className="form-label">
              Email address
            </label>
            <input
              type="email"
              className="form-control"
              id="email"
              placeholder="Enter your email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>

          <div className="mb-3 text-start">
            <label htmlFor="password" className="form-label">
              Password
            </label>
            <input
              type="password"
              className="form-control"
              id="password"
              placeholder="Enter your password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>

          <button type="submit" className="btn btn-danger w-100">
            Login
          </button>
        </form>

        {message && <div className="mt-3 fw-bold text-center">{message}</div>}

        <div className="back-home">
          <a href="/">⬅ Back to Home</a>
        </div>
      </div>
    </div>
  );
}

export default Login;