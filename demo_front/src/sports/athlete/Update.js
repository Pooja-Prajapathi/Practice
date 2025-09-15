import React, { useEffect, useState } from "react";

function Update({ athleteId }) {
  const [athleteData, setAthleteData] = useState({
    sportInterest: "",
    heightCm: 0,
    weightKg: 0,
    medicalHistory: "",
    parentalConsent: false,
  });
  const [message, setMessage] = useState("");

  useEffect(() => {
    if (athleteId) {
      fetch(`http://localhost:8080/api/athletes/${athleteId}`)
        .then(async (res) => {
          if (res.ok) {
            const athlete = await res.json();
            setAthleteData({
              sportInterest: athlete.sportInterest || "",
              heightCm: athlete.heightCm || 0,
              weightKg: athlete.weightKg || 0,
              medicalHistory: athlete.medicalHistory || "",
              parentalConsent: athlete.parentalConsent || false,
            });
          }
        })
        .catch(() => setMessage("🚨 Error fetching athlete profile."));
    }
  }, [athleteId]);

  // Handle input change
  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setAthleteData((prev) => ({
      ...prev,
      [name]: type === "checkbox" ? checked : value,
    }));
  };

  // Save athlete profile (update only)
  const handleSave = async () => {
    if (!athleteId) {
      setMessage("🚨 No athleteId provided.");
      return;
    }

    try {
      const response = await fetch(
        `http://localhost:8080/api/athletes/${athleteId}`,
        {
          method: "PUT",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(athleteData),
        }
      );

      if (response.ok) {
        await response.json();
        setMessage("✅ Athlete profile updated successfully!");
        setAthleteData({
          sportInterest: "",
          heightCm: 0,
          weightKg: 0,
          medicalHistory: "",
          parentalConsent: false,
        });
      } else {
        const errorText = await response.text();
        setMessage(`❌ Failed: ${errorText}`);
      }
    } catch (err) {
      console.error(err);
      setMessage("🚨 Server error while updating athlete profile.");
    }
  };

  return (
    <div className="scroll-section">
      <p className="field-heading">Update Athlete Information</p>
      <form className="update-form">
        <label className="field-label">Sport Interest</label>
        <input
          type="text"
          name="sportInterest"
          placeholder="Enter your main sport"
          value={athleteData.sportInterest}
          onChange={handleChange}
        />

        <label className="field-label">Height (cm)</label>
        <input
          type="number"
          name="heightCm"
          placeholder="Enter height in cm"
          value={athleteData.heightCm}
          onChange={handleChange}
        />

        <label className="field-label">Weight (kg)</label>
        <input
          type="number"
          name="weightKg"
          placeholder="Enter weight in kg"
          value={athleteData.weightKg}
          onChange={handleChange}
        />

        <label className="field-label">Medical History</label>
        <textarea
          name="medicalHistory"
          placeholder="Enter relevant medical history"
          value={athleteData.medicalHistory}
          onChange={handleChange}
        />

        <label className="consent-checkbox-wrapper field-label">
          <input
            type="checkbox"
            name="parentalConsent"
            className="consent-checkbox"
            checked={athleteData.parentalConsent}
            onChange={handleChange}
          />
          <span className="consent-custom" aria-hidden="true"></span>
          Parental Consent
        </label>

        <button type="button" className="upload-button" onClick={handleSave}>
          Update Athlete Info
        </button>
      </form>

      {message && <div className="message mt-4">{message}</div>}
    </div>
  );
}

export default Update;
