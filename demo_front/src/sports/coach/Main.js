import React, { useState, useEffect } from "react";
import { User } from "lucide-react";
import { motion } from "framer-motion";
import { useNavigate } from "react-router-dom";
import "../../sports/Home.css";
import Update from "./Update";
import Video from "./Video";
import Report from "../Report";
import LeaderBoardTable from "../LeaderBoardTable";
import bgImage from "../home.jpeg";

function Coach() {
  const navigate = useNavigate();
  const [file, setFile] = useState(null);
  const [previewURL, setPreviewURL] = useState("");
  const [message, setMessage] = useState("");
  const [coachId, setCoachId] = useState(null);
  const [user, setUser] = useState(null);
  const [activeTab, setActiveTab] = useState("Update Coach Info");
  const [showProfileMenu, setShowProfileMenu] = useState(false);

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
            setMessage("Coach profile not found. Please create one.");
          }
        })
        .catch(() => setMessage("Error fetching coach profile."));
    }
  }, []);

  const handleFileChange = (e) => {
    const selectedFile = e.target.files[0];
    if (selectedFile) {
      setFile(selectedFile);
      setPreviewURL(URL.createObjectURL(selectedFile));
      setMessage("");
    }
  };

  const tabs = ["Update Coach Info", "Upload", "Reports", "Leaderboard"];

  const handleLogout = () => {
    localStorage.removeItem("user");
    navigate("/");
  };

  return (
    <div
       className="athlete-container"
       style={{
           backgroundImage: `url(${bgImage})`,
           backgroundSize: "cover",
           backgroundPosition: "center",
           backgroundRepeat: "no-repeat",
           minHeight: "100vh",
       }}
    >
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

      <motion.div
        className="content-box"
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
      >
        {activeTab === "Update Coach Info" && <Update coachId={coachId} />}
        {activeTab === "Upload" && <Video coachId={coachId}/>}
        {activeTab === "Reports" && <Report/>}
        {activeTab === "Leaderboard" && <LeaderBoardTable />}
      </motion.div>
    </div>
  );
}

export default Coach;
