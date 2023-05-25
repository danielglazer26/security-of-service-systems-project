import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import React, { useState, useEffect, useRef } from 'react';
import {faInfoCircle, faCheck, faTimes} from '@fortawesome/free-solid-svg-icons'
import axios from "./api/axios";
import { useNavigate, Link } from "react-router-dom";

export const Register = () => {
 
    const USER_REGEX =/^(?!.*\s)(?!.*[~`!@#$%^&*()+={}\[\]|\\:;"'<>,.?/₹]).{3,10}$/;
    const PWD_REGEX =/^(?!.*\s)(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[~`!@#$%^&*()--+={}\[\]|\\:;"'<>,.?/_₹]).{10,16}$/;
    const EMAIL_REGEX = /\S+@\S+\.\S+/;
    const REGISTER_URL = "api/auth/register";
    const urlParams = new URLSearchParams(window.location.search);
    const returnUrl = urlParams.get("returnUrl");

    const userRef = useRef();
    const errRef = useRef();
    const [user, setUser] = useState("");
    const [email, setEmail] = useState("");
    const [emailFocus, setEmailFocus] = useState(false);
    const [validName, setValidName] = useState(false);
    const [validEmail, setValidEmail] = useState(false);
    const [userFocus, setUserFocus] = useState(false);
    const [pwd, setPwd] = useState("");
    const [validPwd, setValidPwd] = useState(false);
    const [pwdFocus, setPwdFocus] = useState(false);
    const [matchPwd, setMatchPwd] = useState("");
    const [validMatch, setValidMatch] = useState(false);
    const [matchFocus, setMatchFocus] = useState(false);
    const [errMsg, setErrMsg] = useState("");
    const [success, setSuccess] = useState(false);
    const [qrCode, setQrCode] = useState("");
    const [showQrCode, setShowQrCode] = useState(false);

    const navigate = useNavigate();
    
    function handleClick() {
      // navigate("/login");
      navigate(`/login?returnUrl=${encodeURIComponent(returnUrl)}`)
    }

    useEffect(() => {
        userRef.current.focus();
    }, []);
    
    useEffect(() => {
        setValidName(USER_REGEX.test(user));
        console.log(USER_REGEX.test(user));
    }, [user]);

    useEffect(() => {
      setValidEmail(EMAIL_REGEX.test(email));
  }, [email]);
  
    useEffect(() => {
        setValidPwd(PWD_REGEX.test(pwd));
        setValidMatch(pwd === matchPwd);
    }, [pwd, matchPwd]);
    
    useEffect(() => {
        setErrMsg("");
    }, [user, pwd, matchPwd]);

    useEffect(() => {
      if (qrCode === "") {
        setShowQrCode(false);
      }
      else {
        setShowQrCode(true);
      }
  }, [qrCode]);

    const handleSubmit = async (e) => {
      e.preventDefault();
      const v1 = USER_REGEX.test(user);
      const v2 = PWD_REGEX.test(pwd);
      const v3 = EMAIL_REGEX.test(email);
      if (!v1 || !v2 || !v3) {
        setErrMsg("Invalid Entry");
        return;
      }
    
      try {
        const response = await axios.post(
          REGISTER_URL,
          JSON.stringify({ user, email, pwd }),
          {
            headers: { "Content-Type": "application/json" },
            withCredentials: true
          }
        );
        setQrCode(response.data.message);
        console.log(qrCode);
        //setSuccess(true);
        //clear state and controlled inputs
        setUser("");
        setEmail("");
        setPwd("");
        setMatchPwd("");
      } catch (err) {
        if (!err?.response) {
          setErrMsg("No Server Response");
        } else if (err.response?.status === 409) {
          setErrMsg("Username Taken");
        } else {
          setErrMsg("Registration Failed");
        }
        errRef.current.focus();
      }
    };

    return (
      <>
      {showQrCode? (
        <div id="qr" ref={userRef}>
        <img src = {qrCode} alt="qr code" id='qr_img'/>
        <p>Please scan QR Code</p>
        <button id= "ok_button" onClick={handleClick}> Ok </button>
      </div>
      ):
      (
        <section>
          <p
            ref={errRef}
            className={errMsg ? "errmsg" : "offscreen"}
            aria-live="assertive"
          >
            {errMsg}
          </p>
          <h1>Register</h1>
          <form onSubmit={handleSubmit}>         

            <label htmlFor="username">
              Username:
              <FontAwesomeIcon
                icon={faCheck}
                className={validName ? "valid" : "hide"}
              />
              <FontAwesomeIcon
                icon={faTimes}
                className={
                  validName || !user ? "hide" : "invalid"
                }
              />
            </label>
            <input
              type="text"
              id="username"
              ref={userRef}
              autoComplete="off"
              onChange={(e) => setUser(e.target.value)}
              value={user}
              required
              aria-invalid={validName ? "false" : "true"}
              aria-describedby="uidnote"
              onFocus={() => setUserFocus(true)}
              onBlur={() => setUserFocus(false)}
            />
            <p
              id="uidnote"
              className={
                userFocus && user && !validName
                  ? "instructions"
                  : "offscreen"
              }
            >
              <FontAwesomeIcon icon={faInfoCircle} />
              Required 3 to 10 characters, no whitespaces.
              <br />
              Letters, numbers, underscores and hyphens allowed.
              <br />
            </p>

            <label htmlFor="email">
              Email:
              <FontAwesomeIcon
                icon={faCheck}
                className={validEmail ? "valid" : "hide"}
              />
              <FontAwesomeIcon
                icon={faTimes}
                className={
                  validEmail || !email ? "hide" : "invalid"
                }
              />
            </label>
            <input
              type="email"
              id="email"
              //ref={userRef}
              autoComplete="off"
              onChange={(e) => setEmail(e.target.value)}
              value={email}
              required
              aria-invalid={validEmail ? "false" : "true"}
              aria-describedby="emailnote"
              onFocus={() => setEmailFocus(true)}
              onBlur={() => setEmailFocus(false)}
            />
            <p
              id="emailnote"
              className={
                emailFocus && email && !validEmail
                  ? "instructions"
                  : "offscreen"
              }
            >
              <FontAwesomeIcon icon={faInfoCircle} />
              Please enter valid email address.
            </p>
            <label htmlFor="password">
              Password:
              <FontAwesomeIcon
                icon={faCheck}
                className={validPwd ? "valid" : "hide"}
              />
              <FontAwesomeIcon
                icon={faTimes}
                className={
                  validPwd || !pwd ? "hide" : "invalid"
                }
              />
            </label>
            <input
              type="password"
              id="password"
              onChange={(e) => setPwd(e.target.value)}
              value={pwd}
              required
              aria-invalid={validPwd ? "false" : "true"}
              aria-describedby="pwdnote"
              onFocus={() => setPwdFocus(true)}
              onBlur={() => setPwdFocus(false)}
            />
            <p
              id="pwdnote"
              className={
                pwdFocus && !validPwd
                  ? "instructions"
                  : "offscreen"
              }
            >
              <FontAwesomeIcon icon={faInfoCircle} />
              Required 10 to 16 characters, whitespaces.
              <br />
              Must include uppercase and lowercase letters, a
              number and a special character.
              <br />
            </p>
            <label htmlFor="confirm_pwd">
              Confirm Password:
              <FontAwesomeIcon
                icon={faCheck}
                className={
                  validMatch && matchPwd ? "valid" : "hide"
                }
              />
              <FontAwesomeIcon
                icon={faTimes}
                className={
                  validMatch || !matchPwd ? "hide" : "invalid"
                }
              />
            </label>
            <input
              type="password"
              id="confirm_pwd"
              onChange={(e) => setMatchPwd(e.target.value)}
              value={matchPwd}
              required
              aria-invalid={validMatch ? "false" : "true"}
              aria-describedby="confirmnote"
              onFocus={() => setMatchFocus(true)}
              onBlur={() => setMatchFocus(false)}
            />
            <p
              id="confirmnote"
              className={
                matchFocus && !validMatch
                  ? "instructions"
                  : "offscreen"
              }
            >
              <FontAwesomeIcon icon={faInfoCircle} />
              Must match the first password input field.
            </p>
            
            <button
              disabled={
                !validName || !validPwd || !validMatch
                  ? true
                  : false
              }
            >
              Sign Up
            </button>
          </form>
          <p>
            Already registered?
            <br />
            <span className="line">
              {/* {<a href="/login">Sign In</a> } */}
              <Link to={`/login?returnUrl=${encodeURIComponent(returnUrl)}`}>Sign In</Link>
            </span>
          </p>
        </section>
      )}
      </>
    );  
  };
