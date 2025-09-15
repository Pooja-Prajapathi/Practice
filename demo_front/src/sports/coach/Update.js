import React, { useEffect, useState } from "react";

function Update({ coachId }) {
  const [coachData, setCoachData] = useState({
    specialization: "",
    certification: "",
    experienceYears: 0,
    region: "",
    bio: "",
  });
  const [message, setMessage] = useState("");

  // Load coach info by coachId
  useEffect(() => {
    if (coachId) {
      fetch(`http://localhost:8080/api/coaches/${coachId}`)
        .then(async (res) => {
          if (res.ok) {
            const coach = await res.json();
            setCoachData({
              specialization: coach.specialization || "",
              certification: coach.certification || "",
              experienceYears: coach.experienceYears || 0,
              region: coach.region || "",
              bio: coach.bio || "",
            });
          } else {
            setMessage("🚨 Coach profile not found. Please create one.");
          }
        })
        .catch(() => setMessage("🚨 Error fetching coach profile."));
    }
  }, [coachId]);

  // Handle input change
  const handleChange = (e) => {
    const { name, value } = e.target;
    setCoachData((prev) => ({ ...prev, [name]: value }));
  };

  // Save coach profile
  const handleSave = async () => {
    if (!coachId) {
      setMessage("🚨 No coachId provided.");
      return;
    }

    try {
      const response = await fetch(
        `http://localhost:8080/api/coaches/${coachId}`,
        {
          method: "PUT",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(coachData),
        }
      );

      if (response.ok) {
        await response.json();
        setMessage("✅ Coach profile updated successfully!");
      } else {
        const errorText = await response.text();
        setMessage(`❌ Failed: ${errorText}`);
      }
    } catch (err) {
      console.error(err);
      setMessage("🚨 Server error while saving coach profile.");
    }
  };

  return (
    <div className="scroll-section">
      <h4>Update Coach Information</h4>
      <form className="update-form">
        <label className="field-label">Specialization</label>
        <input
          type="text"
          name="specialization"
          placeholder="Enter specialization (e.g., Football, Tennis)"
          value={coachData.specialization}
          onChange={handleChange}
        />

        <label className="field-label">Certification</label>
        <input
          type="text"
          name="certification"
          placeholder="Enter certification (e.g., FIFA Level 1)"
          value={coachData.certification}
          onChange={handleChange}
        />

        <label className="field-label">Years of Experience</label>
        <input
          type="number"
          name="experienceYears"
          placeholder="Enter number of years"
          value={coachData.experienceYears}
          onChange={handleChange}
        />

        <label className="field-label">Region</label>
        <input
          type="text"
          name="region"
          placeholder="Enter region (e.g., California, USA)"
          value={coachData.region}
          onChange={handleChange}
        />

        <label className="field-label">Bio</label>
        <textarea
          name="bio"
          placeholder="Write a short bio about yourself"
          value={coachData.bio}
          onChange={handleChange}
        />

        <button type="button" className="upload-button" onClick={handleSave}>
          Save Coach Info
        </button>
      </form>

      {message && <div className="message mt-4">{message}</div>}
    </div>
  );
}

export default Update;
