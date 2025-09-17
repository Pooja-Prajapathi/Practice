import React, { useEffect, useState } from "react";

function Video({ coachId }) {
  const [videos, setVideos] = useState([]);
  const [message, setMessage] = useState("");

  useEffect(() => {
    if (!coachId) return;

    fetch(`http://localhost:8080/api/videos/${coachId}`)
      .then(res => {
        if (!res.ok) {
          throw new Error("Failed to fetch videos");
        }
        return res.json();
      })
      .then(data => setVideos(data))
      .catch(err => {
        console.error(err);
        setMessage("Failed to fetch videos.");
      });
  }, [coachId]);

  if (message) return <p>{message}</p>;
  if (videos.length === 0) return <p>No videos available.</p>;

  return (
    <div className="scroll-section">
      <p className="field-heading">Video Review</p>

      <div className="videos-grid" style={{ display: "grid", gap: "20px" }}>
        {videos.map((video, index) => (
          <div
            key={index}
            className="video-card"
            style={{
              border: "1px solid #ccc",
              padding: "15px",
              borderRadius: "8px",
              background: "#f9f9f9",
            }}
          >
            <p className="field-heading ">
                          {video.fileName || `Video ${index + 1}`}
                        </p>
                        <p className="field-label">
                          Athlete: {video.athleteName || "Unknown"} &nbsp; | &nbsp;
                          <span >
                            Uploaded: {video.uploadedAt}
                          </span>
                        </p>

            <div className="file-upload">
            {typeof video === "string" ? (
              <video
                src={video}
                controls
                className="preview-video"
              />
            ) : (
              <>
                <video
                  src={video.url}
                  controls
                  className="preview-video"
                />
              </>
            )}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

export default Video;
