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
        setMessage("🚨 Failed to fetch videos.");
      });
  }, [coachId]);

  if (message) return <p>{message}</p>;
  if (videos.length === 0) return <p>No videos available for your athletes.</p>;

  return (
    <div className="videos-grid" style={{ display: "grid", gap: "20px" }}>
      {videos.map((video, index) => (
        <div
          key={index}
          className="video-card"
          style={{
            border: "1px solid #ccc",
            padding: "10px",
            borderRadius: "8px",
          }}
        >
          {/* If backend returns only URLs */}
          {typeof video === "string" ? (
            <video
              src={video}
              controls
              className="preview-video"
              style={{ width: "100%", borderRadius: "6px" }}
            />
          ) : (
            <>
              <h4>{video.fileName}</h4>
              <p>Uploaded: {video.uploadedAt}</p>
              <video
                src={video.url}
                controls
                className="preview-video"
                style={{ width: "100%", borderRadius: "6px" }}
              />
            </>
          )}
        </div>
      ))}
    </div>
  );
}

export default Video;
