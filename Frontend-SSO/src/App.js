import React, { useEffect, useState } from 'react';
import './App.css';
import { Routes, Route, useNavigate } from "react-router-dom";
import { Login } from "./Login";
import { Register } from "./Register";
import { TempApp } from "./TempApp";
//import 'bootstrap/dist/css/bootstrap.min.css';
//import "bootstrap/dist/js/bootstrap.bundle.min";

function App() {
  const [isLoading, setIsLoading] = useState('true');
  const urlParams = new URLSearchParams(window.location.search);
  const returnUrl = urlParams.get("returnUrl");
  const navigate = useNavigate();

  // useEffect(() => {
  //   const { state } = location;
  //   if (!state || !state.fromAnotherApp) {
  //     navigate("/"); // Redirect to the main app page if not accessed from another app
  //   }
  // }, [navigate, location]);

  useEffect(() => {
    if (!returnUrl) {
      navigate("/"); // Redirect to the main app page if returnUrl is not provided
      setIsLoading(false)
    }
    else {
      setIsLoading(false)
    }
  }, );

  if (isLoading) {
    return <div className='loading'>Loading...</div>; // Show loading indicator while checking authorization
  } 

  return (
    <div className="App">
      <p id="title">DEW SSO  <img src='/key.svg' alt='key icon' id = 'logo'/> </p>  
      <Routes>
        <Route path="/register" element={<Register />} />
        <Route path="/login" element={<Login />} />
        <Route path="/tempapp" element={<TempApp />} />
      </Routes>
    </div>
  );
}

export default App;
