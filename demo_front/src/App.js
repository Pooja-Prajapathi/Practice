import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Login from "./sports/Login";
import Register from "./sports/Register";
import Athlete from "./sports/athlete/Main";
import Coach from "./sports/coach/Main";
import Home from "./sports/Home";

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/athlete" element={<Athlete />} />
        <Route path="/coach" element={<Coach />} />
      </Routes>
    </Router>
  );
}

export default App;
