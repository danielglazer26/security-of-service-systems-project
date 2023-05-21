import React from 'react';

export const Home = () => {
  return (
    <div className="home-container">
      <h1 className="home-title">Home</h1>
      <div className="button-container">
        <button className="sign-in-button">Sign In</button>
        <button className="sign-up-button">Sign Up</button>
      </div>
    </div>
  );
};