import React, { useEffect, useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import axios from './api/axios';


export const UserPage = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const USER_INFO_URL = "api/user/info";
  const { data } = location.state;


  return (
    <div>
      <h1>User Page</h1>
      {/* Add your UserPage content */}
      <p>{data.role}</p>
    </div>
  );
};

export default UserPage;

//   useEffect(() => {
//     // Send GET request using Axios
//     axios
//     .get(USER_INFO_URL)
//     .then((response) => {
//       const data = response.data;
//       console.log(data);
//       navigate('/userpage'); // Redirect to /userpage on successful response
//     })
//     .catch((error) => {
//       console.error('Error checking access to userpage:', error);
//       if (error.response && error.response.status === 401) {
//         navigate('/home'); // Redirect to /home on 401 (unauthorized) response
//       }
//       else {
//         navigate('/home')
//       }
//     });
// }, []);
