import React, { useState } from "react";
import { Upload as UploadIcon } from "lucide-react";
import { motion } from "framer-motion";
import "../../sports/Home.css";

function UploadVideo({ athleteId }) {
  const [file, setFile] = useState(null);
  const [previewURL, setPreviewURL] = useState("");
  const [message, setMessage] = useState("");

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
      setMessage("Please select a video first.");
      return;
    }
    if (!athleteId) {
      setMessage("Athlete ID not found. Please create your profile first.");
      return;
    }

    const formData = new FormData();
    formData.append("file", file);

    try {
      const response = await fetch(
        `http://localhost:8080/api/videos/${athleteId}`,
        { method: "POST", body: formData }
      );

      if (response.ok) {
        await response.text();
        setMessage("Video uploaded successfully!");
        setFile(null);
        setPreviewURL("");
      } else {
        const errorText = await response.text();
        setMessage(`Video upload failed: ${errorText}`);
      }
    } catch (err) {
      console.error(err);
      setMessage("Server error while uploading video.");
    }
  };

  return (
    <div className="scroll-section">
      {!previewURL && (
        <>
          <p className="field-heading">Upload and preview your performance videos</p>
          <motion.label className="upload-section" whileHover={{ scale: 1.02 }}>
            <UploadIcon className="upload-icon" />
            <span>Click to choose a video file</span>
            <input
              type="file"
              accept="video/*"
              onChange={handleFileChange}
              className="hidden file-upload"
            />
          </motion.label>
        </>
      )}

      {previewURL && (
        <div className="review-section">
          <p className="field-heading">
            Review Video
          </p>
          <div className="video-container">
            <video src={previewURL} controls className="preview-video" />
          </div>
          <div className="review-button">
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
    </div>
  );
}

export default UploadVideo;
