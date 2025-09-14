import React from "react";
import { BrowserRouter as Router, Routes, Route, Link } from "react-router-dom";
import Login from "./sports/Login";
import Register from "./sports/Register";
import Athlete from "./sports/athlete/Main";
import Coach from "./sports/coach/Main";

function Home() {
  return (
    <div className="container mt-5 text-center">
      <h1 className="mb-4 text-danger">Welcome to Sportzy</h1>
      <p className="lead text-secondary">Do you want to register as</p>
      <a href="/register?type=athlete" className="btn btn-red m-2">
        Athlete
      </a>
      <a href="/register?type=coach" className="btn btn-red m-2">
        Coach
      </a>
      <div className="mt-4">
        <Link to="/login" className="text-primary">
          ➡ Login if you already have an account
        </Link>
      </div>
    </div>
  );
}

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/athlete" element={<Athlete/>}/>
        <Route path="/coach" element={<Coach/>}/>
      </Routes>
    </Router>
  );
}

export default App;
