import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import axios from './api/axios';

const UserPage = () => {

    const location = useLocation();
    const navigate = useNavigate();
    const { data } = location.state;

    const LOGOUT_URL =  "api/auth/logout";

    // Logout
    const handleLogout = async () => {
        try {
          await axios.get(LOGOUT_URL, { withCredentials: true });
          // Redirect to the login page or any other desired page
          navigate('/home');
        } catch (error) {
          console.error('Logout error:', error);
        }
      };

      
  //   return (
  //     <div className="user-page-container">
  //       <h1 className="user-page-heading">User Page</h1>
  //       <p className="user-role">Username: {data.user}</p>
  //       <p className="user-role">Email: {data.email}</p>
  //       <p className="user-role">Role: {data.role}</p>
  //       <div className="logout-container">
  //         <button className="logout-button" onClick={handleLogout}>
  //           Logout
  //         </button>
  //       </div>
  //     </div>
  //   );
  // }    

  return (
    <div className="user-page-container">
      <h1 className="user-page-heading">User Page</h1>
      <p className="user-role"><strong>Username:</strong> {data.user}</p>
      <p className="user-role"><strong>Email:</strong> {data.email}</p>
      <p className="user-role"><strong>Role:</strong> {data.role}</p>
      <div className="logout-container">
        <button className="logout-button" onClick={handleLogout}>
          Logout
        </button>
      </div>
    </div>
  );
};


export default UserPage;



