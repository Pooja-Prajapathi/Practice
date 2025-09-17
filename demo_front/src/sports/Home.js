import React from "react";
import { Link } from "react-router-dom";
import { motion } from "framer-motion";
import bgImage from "./home.jpeg";
import "./Home.css";

function Home() {
  return (
    <div
      className="home-container"
      style={{ backgroundImage: `url(${bgImage})` }}
    >
      <div className="overlay">
        <motion.h1
          initial={{ opacity: 0, y: -40 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 1 }}
          className="home-title"
        >
          Welcome to Sportzy
        </motion.h1>

        <motion.p
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 1, delay: 0.5 }}
          className="home-subtitle"
        >
          REGISTER AS
        </motion.p>

        <a href="/register?type=athlete" className="btn btn-red m-2">
          Athlete
        </a>
        <a href="/register?type=coach" className="btn btn-red m-2">
          Coach
        </a>

        <div className="mt-4">
          <Link to="/login" className="login-link">
            ➡ Login if you already have an account
          </Link>
        </div>
      </div>
    </div>
  );
}

export default Home;
