import './App.css';
import React, { useEffect } from 'react';
import { Home } from './Home';
import { UserPage } from './UserPage';
import { Routes, Route, useNavigate } from "react-router-dom";
import axios from './api/axios';

// function App() {
//   return (
//     <div className="App">
//       <p id="title">Client App 01</p>  
//       <Routes>
//         <Route path="/home" element={<Home />} />
//         <Route path="/userpage" element={<UserPage />} />
//       </Routes>
//     </div>
//   );  
// }
// export default App;
const App = () => {
  const navigate = useNavigate();
  const USER_INFO_URL = "api/user/info";

  useEffect(() => {
    // Send GET request using Axios
    axios
    .get(USER_INFO_URL)
    .then((response) => {
      const data = response.data;
      console.log(data);
      navigate('/userpage'); // Redirect to /userpage on successful response
    })
    .catch((error) => {
      console.error('Error checking access:', error.message);
      if (error.response && error.response.status === 401) {
        console.log('stop');
        navigate('/home'); // Redirect to /home on 401 (unauthorized) response
      }
    });
}, [navigate]);

  return (
    <div className="App">
      <p id="title">Client App 01</p>
      <Routes>
        <Route path="/home" element={<Home />} />
        <Route path="/userpage" element={<UserPage />} />
      </Routes>
    </div>
  );
};

export default App;