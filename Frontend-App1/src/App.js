import './App.css';
import React, { useEffect, useState } from 'react';
import { Home } from './Home';
import { UserPage } from './UserPage';
import { Routes, Route, useNavigate } from "react-router-dom";
import axios from './api/axios';


const App = () => {
  const navigate = useNavigate();
  const [isLoading, setIsLoading] = useState('true');
  const USER_INFO_URL = "api/user/info";

  useEffect(() => {
    // Send GET request using Axios
    axios
    .get(USER_INFO_URL)
    .then((response) => {
      const data = response.data;
      console.log(data);
      navigate('/userpage'); // Redirect to /userpage on successful response
      setIsLoading(false);
    })
    .catch((error) => {
      console.error('Error checking access:', error.message);
      if (error.response && error.response.status === 401) {
        console.log('stop');
        navigate('/home'); // Redirect to /home on 401 (unauthorized) response
        setIsLoading(false);
      }
    });
}, [navigate]);

if (isLoading) {
  return <div className='loading'>Loading...</div>; // Show loading indicator while checking authorization
}  
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