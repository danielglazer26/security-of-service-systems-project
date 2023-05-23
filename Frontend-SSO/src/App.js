import React, { useState } from 'react';
import './App.css';
import { Routes, Route } from "react-router-dom";
import { Login } from "./Login";
import { Register } from "./Register";
import { TempApp } from "./TempApp";
//import 'bootstrap/dist/css/bootstrap.min.css';
//import "bootstrap/dist/js/bootstrap.bundle.min";

function App() {

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
