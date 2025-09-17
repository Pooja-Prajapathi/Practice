import React, { useEffect, useState } from "react";
import "./Home.css";

function LeaderBoardTable() {
  const [leaderboards, setLeaderboards] = useState([]);
  const [message, setMessage] = useState("");

  useEffect(() => {
    fetch("http://localhost:8080/api/leaderboards")
      .then((res) => {
        if (!res.ok) throw new Error("Failed to fetch leaderboard data");
        return res.json();
      })
      .then((data) => setLeaderboards(data))
      .catch((err) => {
        console.error(err);
        setMessage("Failed to load leaderboard.");
      });
  }, []);

  if (message) return <p>{message}</p>;
  if (leaderboards.length === 0) return <p>No leaderboard data available.</p>;

  return (
    <div className="leaderboard-table-container scroll-section">
    <p className="field-heading">LeaderBoard </p>
      <table className="leaderboard-table">
        <thead>
          <tr>
            <th>Rank</th>
            <th>Athlete</th>
            <th>Coach</th>
            <th>Sport</th>
            <th>Region</th>
            <th>Points</th>
          </tr>
        </thead>
        <tbody>
          {leaderboards.map((lb) => (
            <tr key={lb.id}>
              <td>{lb.rank || "-"}</td>
              <td>{lb.athlete?.user.fullname || "-"}</td>
              <td>{lb.coach?.user.fullname || "-"}</td>
              <td>{lb.sport}</td>
              <td>{lb.region || "-"}</td>
              <td>{lb.points || 0}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default LeaderBoardTable;
