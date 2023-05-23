import React, { useRef, useState, useEffect, useContext } from "react";
import axios from "./api/axios";
import { useNavigate } from "react-router-dom";

export const Login = () => {

  const LOGIN_URL = "api/auth/login";
  const OTP_LOGIN_URL = "authorization/otp/login?verificationCode=";
  
  const userRef = useRef();
  const errRef = useRef();
  const [user, setUser] = useState("");
  const [pwd, setPwd] = useState("");
  const [verificationCode, setVerificationCode] = useState("");
  const [errMsg, setErrMsg] = useState("");
  const [success, setSuccess] = useState(false);

  const navigate = useNavigate();
  
  useEffect(() => {
    userRef.current.focus();
  }, []);

  useEffect(() => {
    setErrMsg("");
  }, [user, pwd]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post(
        LOGIN_URL,
        JSON.stringify({ user, pwd }),
        {
          //headers: { "Content-Type": "application/x-www-form-urlencoded" },
          headers: { "Content-Type": "application/json" },
          withCredentials: true
        }
      );
     // const accessToken = response?.data?.accessToken;
      //const roles = response?.data?.roles;
      //setAuth({ user, pwd, roles, accessToken });
      console.log(response.headers);
      setUser("");
      setPwd("");
      setSuccess(true);

    } catch (err) {
      if (!err?.response) {
        setErrMsg("No Server Response");
      } else if (err.response?.status === 400) {
        setErrMsg("Missing Username or Password");
      } else if (err.response?.status === 401) {
        setErrMsg("Unauthorized");
      } else {
        setErrMsg("Login Failed");
      }
      errRef.current.focus();
    }
  };

  const handleClick = async (e) => {
    e.preventDefault();
    try {
      const url = `${OTP_LOGIN_URL}${verificationCode}`;
      const response = await axios.post(
        url,
          undefined,
        {
          //headers: { "Content-Type": "application/x-www-form-urlencoded" },
          headers: { "Content-Type": "application/json" },
          withCredentials: true
        }
      );
     // const accessToken = response?.data?.accessToken;
      //const roles = response?.data?.roles;
      //setAuth({ user, pwd, roles, accessToken });
      // Extract the returnUrl parameter from the URL
      const urlParams = new URLSearchParams(window.location.search);
      const returnUrl = urlParams.get("returnUrl");
      // console.log(response);
      console.log(`Url: ${returnUrl}`)
      window.location.href = returnUrl;
      // navigate("/tempapp");
    console.log("click")
    } catch (err) {
      if (!err?.response) {
        setErrMsg("No Server Response");
      } else if (err.response?.status === 401) {
        setErrMsg("Unauthorized");
      } else {
        setErrMsg("Login Failed");
      }
      errRef.current.focus();
    }
  };

  return (
    <>
      {success ? (
      <section>
          <p
            ref={errRef}
            className={errMsg ? "errmsg" : "offscreen"}
            aria-live="assertive"
          >
            {errMsg}
          </p>
          <h1>OTP</h1>
          <br />
          <label htmlFor="otp">Please enter one-time-password from the Authenticator App:</label>
          <br />
            <input
              type="text"
              id="otp"
              ref={userRef}
              autoComplete="off"
              onChange={(e) => setVerificationCode(e.target.value)}
              value={verificationCode}
              required
            />
            <button id= "ok_button" onClick={handleClick}> Ok </button>
        </section>) : (
        <section>
          <p
            ref={errRef}
            className={errMsg ? "errmsg" : "offscreen"}
            aria-live="assertive"
          >
            {errMsg}
          </p>
          <h1>Sign In</h1>
          <form onSubmit={handleSubmit}>
            <label htmlFor="username">Username:</label>
            <input
              type="text"
              id="username"
              ref={userRef}
              autoComplete="off"
              onChange={(e) => setUser(e.target.value)}
              value={user}
              required
            />
            <label htmlFor="password">Password:</label>
            <input
              type="password"
              id="password"
              onChange={(e) => setPwd(e.target.value)}
              value={pwd}
              required
            />
            <button>Sign In</button>
          </form>
          <p>
            Don't have an Account?
            <br />
            <span className="line">
              <a href='/register'>Sign Up</a>
            </span>
          </p>
        </section>
      )}
    </>
  );
};

