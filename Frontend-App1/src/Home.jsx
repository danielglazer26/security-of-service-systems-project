import React from 'react';
import { useNavigate } from 'react-router-dom';

export const Home = () => {
  const handleSignIn = () => {
    const returnUrl = encodeURIComponent(window.location.href);
    window.location.href = `https://localhost:3000/login?returnUrl=${returnUrl}`;
  };

  const handleSignUp = () => {
    const returnUrl = encodeURIComponent(window.location.href);
    window.location.href = `https://localhost:3000/register?returnUrl=${returnUrl}`;
  };

  return (
    <div className="home-container">
      <h1 className="home-title">Home</h1>
      <div className="button-container">
        <button className="sign-in-button" onClick={handleSignIn}>Sign In</button>
        <button className="sign-up-button" onClick={handleSignUp}>Sign Up</button>
      </div>
    </div>
  );
};


// import React from 'react';

// export const Home = () => {
//   return (
//     <div className="home-container">
//       <h1 className="home-title">Home</h1>
//       <div className="button-container">
//         <button className="sign-in-button">Sign In</button>
//         <button className="sign-up-button">Sign Up</button>
//       </div>
//     </div>
//   );
// };