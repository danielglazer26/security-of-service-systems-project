import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';


export const UserPage = () => {
  const USER_INFO_URL = "api/user/info";
  const navigate = useNavigate();


return (
<div>
  <h1>User Page</h1>
</div>
);
};

export default UserPage;
  
//     return (
//         <div>
//           <h1>User Page</h1>
//         </div>
//       );    
// };
