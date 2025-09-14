import React, { useEffect, useState } from "react";

function Update() {
  const [athleteId, setAthleteId] = useState(null);
  const [user, setUser] = useState(null);
  const [athleteData, setAthleteData] = useState({
    sportInterest: "",
    heightCm: 0,
    weightKg: 0,
    medicalHistory: "",
    parentalConsent: false,
    badgePoints: 0,
  });
  const [message, setMessage] = useState("");

  // Load user + athlete info
  useEffect(() => {
    const storedUser = localStorage.getItem("user");
    if (storedUser) {
      const parsedUser = JSON.parse(storedUser);
      setUser(parsedUser);

      fetch(`http://localhost:8080/api/athletes/byUser/${parsedUser.id}`)
        .then(async (res) => {
          if (res.ok) {
            const athlete = await res.json();
            setAthleteId(athlete.id);
            setAthleteData({
              sportInterest: athlete.sportInterest || "",
              heightCm: athlete.heightCm || 0,
              weightKg: athlete.weightKg || 0,
              medicalHistory: athlete.medicalHistory || "",
              parentalConsent: athlete.parentalConsent || false,
              badgePoints: athlete.badgePoints || 0
            });
          }
        })
        .catch(() => setMessage("🚨 Error fetching athlete profile."));
    }
  }, []);

  // Handle input
  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setAthleteData((prev) => ({
      ...prev,
      [name]: type === "checkbox" ? checked : value
    }));
  };

  // Save athlete profile
  const handleSave = async () => {
    if (!user) {
      setMessage("🚨 User not found.");
      return;
    }

    const payload = { ...athleteData, user: { id: user.id } };

    try {
      const response = await fetch("http://localhost:8080/api/athletes", {
        method: athleteId ? "PUT" : "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
      });

      if (response.ok) {
        const savedAthlete = await response.json();
        setAthleteId(savedAthlete.id);
        setMessage("✅ Athlete profile saved successfully!");
      } else {
        const errorText = await response.text();
        setMessage(`❌ Failed: ${errorText}`);
      }
    } catch (err) {
      console.error(err);
      setMessage("🚨 Server error while saving athlete profile.");
    }
  };

  return (
    <div className="scroll-section">
      <h4>Update Athlete Information</h4>
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

        <label className="field-label">Badge Points</label>
        <input
          type="number"
          name="badgePoints"
          placeholder="Points earned"
          value={athleteData.badgePoints}
          onChange={handleChange}
          disabled // usually managed by backend
        />

        <button type="button" className="upload-button" onClick={handleSave}>
          Save Athlete Info
        </button>
      </form>

      {message && <div className="message mt-4">{message}</div>}
    </div>
  );
}

export default Update;
