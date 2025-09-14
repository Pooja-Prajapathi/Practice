import React, { useState, useEffect } from "react";
import { Upload, User } from "lucide-react";
import { motion } from "framer-motion";
import { useNavigate } from "react-router-dom";
import "../../sports/Home.css";
import Update from "./Update"; // Import the new UpdateCoach component

function Coach() {
  const navigate = useNavigate();
  const [file, setFile] = useState(null);
  const [previewURL, setPreviewURL] = useState("");
  const [message, setMessage] = useState("");
  const [coachId, setCoachId] = useState(null);
  const [user, setUser] = useState(null);
  const [activeTab, setActiveTab] = useState("Upload");
  const [showProfileMenu, setShowProfileMenu] = useState(false);

  // Load user and coach ID for uploads
  useEffect(() => {
    const storedUser = localStorage.getItem("user");
    if (storedUser) {
      const parsedUser = JSON.parse(storedUser);
      setUser(parsedUser);

      fetch(`http://localhost:8080/api/coaches/byUser/${parsedUser.id}`)
        .then(async (res) => {
          if (res.ok) {
            const coach = await res.json();
            setCoachId(coach.id);
          } else {
            setMessage("🚨 Coach profile not found. Please create one.");
          }
        })
        .catch(() => setMessage("🚨 Error fetching coach profile."));
    }
  }, []);

  // File upload
  const handleFileChange = (e) => {
    const selectedFile = e.target.files[0];
    if (selectedFile) {
      setFile(selectedFile);
      setPreviewURL(URL.createObjectURL(selectedFile));
      setMessage("");
    }
  };

  const handleUpload = async () => {
    if (!file) {
      setMessage("⚠️ Please select a video first.");
      return;
    }
    if (!coachId) {
      setMessage("🚨 Coach ID not found. Please update your profile first.");
      return;
    }

    const formData = new FormData();
    formData.append("file", file);

    try {
      const response = await fetch(
        `http://localhost:8080/api/coaches/${coachId}/videos`,
        { method: "POST", body: formData }
      );

      if (response.ok) {
        await response.text();
        setMessage("✅ Video uploaded successfully!");
        setFile(null);
        setPreviewURL("");
      } else {
        const errorText = await response.text();
        setMessage(`❌ Video upload failed: ${errorText}`);
      }
    } catch (err) {
      console.error(err);
      setMessage("🚨 Server error while uploading video.");
    }
  };

  const tabs = ["Update Coach Info", "Upload", "Reports", "Leaderboard"];

  const handleLogout = () => {
    localStorage.removeItem("user");
    navigate("/");
  };

  return (
    <div className="athlete-container">
      {/* Header */}
      <div className="athlete-header">
        <h1>Coach Dashboard</h1>
        {user && (
          <div className="profile-section" style={{ position: "relative" }}>
            <span>{user.fullname}</span>
            <User
              className="profile-icon"
              onClick={() => setShowProfileMenu(!showProfileMenu)}
            />
            {showProfileMenu && (
              <div className="profile-dropdown">
                <div
                  className="dropdown-item"
                  onClick={() => alert("View Profile clicked")}
                >
                  View Profile
                </div>
                <div
                  className="dropdown-item"
                  onClick={() => alert("Update Profile clicked")}
                >
                  Update Profile
                </div>
                <div className="dropdown-item" onClick={handleLogout}>
                    Logout
                </div>
              </div>
            )}
          </div>
        )}
      </div>

      {/* Tabs */}
      <div className="menu-tabs">
        {tabs.map((tab) => (
          <div
            key={tab}
            className={`menu-tab ${activeTab === tab ? "active" : ""}`}
            onClick={() => setActiveTab(tab)}
          >
            {tab}
          </div>
        ))}
      </div>

      {/* Content Box */}
      <motion.div
        className="content-box"
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
      >
        {/* Update Coach Info */}
        {activeTab === "Update Coach Info" && <Update />}

        {/* Upload Tab */}
        {activeTab === "Upload" && (
          <>
            {!previewURL && (
              <>
                <p>Upload and preview training videos or resources</p>
                <motion.label
                  className="upload-section"
                  whileHover={{ scale: 1.02 }}
                >
                  <Upload className="mx-auto w-12 h-12 text-red-500" />
                  <span>Click to choose a video file</span>
                  <input
                    type="file"
                    accept="video/*"
                    onChange={handleFileChange}
                    className="hidden"
                  />
                </motion.label>
              </>
            )}

            {previewURL && (
              <div className="review-section">
                <h3 className="text-lg font-medium text-gray-700 mb-4">
                  Review Video
                </h3>
                <div className="video-container">
                  <video src={previewURL} controls className="preview-video" />
                </div>
                <div className="flex justify-center gap-4 mt-4">
                  <button className="upload-button" onClick={handleUpload}>
                    Upload Video
                  </button>
                  <button
                    className="upload-button bg-gray-400 hover:bg-gray-500"
                    onClick={() => {
                      setFile(null);
                      setPreviewURL("");
                      setMessage("");
                    }}
                  >
                    Cancel
                  </button>
                </div>
              </div>
            )}

            {message && <div className="message mt-4">{message}</div>}
          </>
        )}

        {/* Other Tabs */}
        {activeTab !== "Upload" && activeTab !== "Update Coach Info" && (
          <p>{activeTab} section coming soon!</p>
        )}
      </motion.div>
    </div>
  );
}

export default Coach;
