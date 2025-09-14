import React, { useEffect, useState } from "react";
import { useLocation, Link } from "react-router-dom";

function Register() {
  const location = useLocation();
  const params = new URLSearchParams(location.search);
  const type = params.get("type"); // "athlete" or "coach"

  const backendUrl = "http://localhost:8080/api/users";

  const [formType, setFormType] = useState("athlete");
  const [formData, setFormData] = useState({
    fullname: "",
    email: "",
    password: "",
    dob: "",
    gender: "",
    location: "",
    contact: "",
    role: "athlete"
  });
  const [message, setMessage] = useState("");

  useEffect(() => {
    if (type === "coach") {
      setFormType("coach");
      setFormData((prev) => ({ ...prev, role: "coach" }));
    } else {
      setFormType("athlete");
      setFormData((prev) => ({ ...prev, role: "athlete" }));
    }
  }, [type]);

  const handleChange = (e) => {
    const { id, value } = e.target;
    setFormData((prev) => ({ ...prev, [id]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const res = await fetch(backendUrl, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(formData),
      });

      if (res.ok) {
        setMessage(`✅ Successfully registered as ${formType} in Sportzy.`);
      } else {
        const errorData = await res.json();
        setMessage(`❌ Registration failed: ${errorData.message || "Unknown error"}`);
      }
    } catch (err) {
      console.error(err);
      setMessage("❌ Registration failed: Server error");
    }
  };

  return (
    <div className="container mt-5" style={{ maxWidth: "500px" }}>
      <h1 className="text-danger text-center">Welcome to Sportzy</h1>
      <h3 className="text-center text-secondary mb-4">
        Register as {formType.charAt(0).toUpperCase() + formType.slice(1)}
      </h3>

      <form onSubmit={handleSubmit}>
        <div className="mb-3">
          <label className="form-label">Full Name:</label>
          <input
            className="form-control"
            id="fullname"
            value={formData.fullname}
            onChange={handleChange}
            required
          />
        </div>

        <div className="mb-3">
          <label className="form-label">Date of Birth:</label>
          <input
            type="date"
            className="form-control"
            id="dob"
            value={formData.dob}
            onChange={handleChange}
            required
          />
        </div>

        <div className="mb-3">
          <label className="form-label">Gender:</label>
          <input
            className="form-control"
            id="gender"
            value={formData.gender}
            onChange={handleChange}
            required
          />
        </div>

        <div className="mb-3">
          <label className="form-label">Location:</label>
          <input
            className="form-control"
            id="location"
            value={formData.location}
            onChange={handleChange}
            required
          />
        </div>

        <div className="mb-3">
          <label className="form-label">Contact:</label>
          <input
            className="form-control"
            id="contact"
            value={formData.contact}
            onChange={handleChange}
            required
          />
        </div>

        <div className="mb-3">
          <label className="form-label">Email:</label>
          <input
            type="email"
            className="form-control"
            id="email"
            value={formData.email}
            onChange={handleChange}
            required
          />
        </div>

        <div className="mb-3">
          <label className="form-label">Password:</label>
          <input
            type="password"
            className="form-control"
            id="password"
            value={formData.password}
            onChange={handleChange}
            required
          />
        </div>

        <button type="submit" className="btn btn-red w-100">
          Register
        </button>
      </form>

      {message && <div className="mt-3 text-center fw-bold">{message}</div>}

      <div className="mt-4 text-center">
        <Link to="/" className="text-primary">
          ⬅ Back to Home
        </Link>
      </div>
    </div>
  );
}

export default Register;
