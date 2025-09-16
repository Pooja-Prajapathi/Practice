import React, { useState, useEffect } from "react";
import { User } from "lucide-react";
import { motion } from "framer-motion";
import { useNavigate } from "react-router-dom";
import "../../sports/Home.css";
import Update from "./Update";
import UploadVideo from "./UploadVideo";
import Report from "../Report";
import LeaderBoardTable from "../LeaderBoardTable";

function Athlete() {
  const navigate = useNavigate();
  const [athleteId, setAthleteId] = useState(null);
  const [user, setUser] = useState(null);
  const [activeTab, setActiveTab] = useState("Update Athlete Info");
  const [showProfileMenu, setShowProfileMenu] = useState(false);
  const [message, setMessage] = useState("");

  // Load user and athlete info
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
          } else {
            setMessage("🚨 Athlete profile not found. Please create one.");
          }
        })
        .catch(() => setMessage("🚨 Error fetching athlete profile."));
    }
  }, []);

  const tabs = ["Update Athlete Info", "Upload", "Report", "Leaderboard"];

  const handleLogout = () => {
    localStorage.removeItem("user");
    navigate("/");
  };

  return (
    <div className="athlete-container">
      {/* Header */}
      <div className="athlete-header">
        <h1>Athlete Dashboard</h1>
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
                  onClick={() => {
                    setActiveTab("Update Athlete Info");
                    setShowProfileMenu(false);
                  }}
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

      {/* Menu Tabs */}
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
        {activeTab === "Upload" && <UploadVideo athleteId={athleteId} />}
        {activeTab === "Update Athlete Info" && <Update athleteId={athleteId} />}
        {activeTab === "Report" && <Report/>}
        {activeTab === "Leaderboard" && <LeaderBoardTable />}
        {message && <div className="message mt-4">{message}</div>}
      </motion.div>
    </div>
  );
}

export default Athlete;
