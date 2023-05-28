import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import axios from './api/axios';

const UserPage = () => {

    const location = useLocation();
    const navigate = useNavigate();
    const { data } = location.state;

    const [text, setText] = useState('');
    const [textsToReview, setTextsToReview] = useState([]);
    const [reviewedTexts, setReviewedTexts] = useState([]);

    const GET_TEXTS_URL = "api/text/review";
    const GET_REVIEWED_TEXTS_URL = "api/text";
    const POST_NEW_TEXT_URL = "api/text";
    const POST_REVIEWED_TEXT_URL = "api/text/review";
    const LOGOUT_URL =  "api/auth/logout";

    // Fetch initial data on component mount
    useEffect(() => {
        fetchTextsToReview();
        console.log(`To review: ${textsToReview}`)
        fetchReviewedTexts();
        console.log(`Reviewed: ${reviewedTexts}`)
    }, []);

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

      // GET Texts to review
      const fetchTextsToReview = async () => {
        try {
          const response = await axios.get(GET_TEXTS_URL, { withCredentials: true });
          setTextsToReview(response.data);
          console.log(textsToReview)
        } catch (error) {
          console.error('Error fetching review texts:', error);
        }
      }

      // GET Reviewed Texts
      const fetchReviewedTexts = async () => {
        try {
          const response = await axios.get(GET_REVIEWED_TEXTS_URL, { withCredentials: true });
          setReviewedTexts(response.data);
          console.log(reviewedTexts)
        } catch (error) {
          console.error('Error fetching review texts:', error);
        }
      }

      // POST New Text
      const handleNewTextSubmit = async (e) => {
        e.preventDefault();
        try {
          const response = await axios.post(POST_NEW_TEXT_URL, { content: text }, { withCredentials: true });
          fetchTextsToReview()
          // Handle the response as needed
          // console.log('Text submitted:', response.data);
          // Clear the text input after submission
          setText('');
        } catch (error) {
          console.error('Text submission error:', error);
        }
      };

    
      // POST Reviewed Text
        
      const handleReviewedTextSubmit = async (textId, textContent) => {
        try {
          const requestBody = {
            reviewedTextId: textId,
            content: textContent,
          };
      
          const response = await axios.post(
            POST_REVIEWED_TEXT_URL,
            requestBody,
            {
              headers: {
                "Content-Type": "application/json",
              },
              withCredentials: true,
            }
          );
      
          // Handle the response as needed
          console.log('Reviewed text submitted:', response.data);

          // Fetch updated review texts
          fetchTextsToReview();
          fetchReviewedTexts();
        } catch (error) {
          console.error('Reviewed text submission error:', error);
        }
      };

      const handleDelete = async (textId) => {
        try {
          await axios.delete(`${GET_REVIEWED_TEXTS_URL}?id=${textId}`, { withCredentials: true });
          fetchReviewedTexts(); // Refresh the reviewed texts after deletion
        } catch (error) {
          console.error('Error deleting review:', error);
        }
      };

    //   const handleEdit = (textId, content) => {
    //     // Find the text to edit
    //     const textToEdit = reviewTexts.find((text) => text.reviewedTextId === textId);
    //     if (textToEdit) {
    //       // Set the text to edit in the state
    //       setText(content);
    //       // Remove the text from the reviewTexts list
    //       setReviewTexts(reviewTexts.filter((text) => text.reviewedTextId !== textId));
    //     }
    //   };
    return (
      <div className="user-page-container">
        <h1 className="user-page-heading">User Page</h1>
        <p className="user-role">{data.role}</p>
    
        <div className="logout-container">
          <button className="logout-button" onClick={handleLogout}>Logout</button>
        </div>
    
        <form onSubmit={handleNewTextSubmit} className="text-submit-form">
          <input
            type="text"
            value={text}
            onChange={(e) => setText(e.target.value)}
            placeholder="Enter text"
            className="text-input"
          />
          <button type="submit" className="submit-button">Submit</button>
        </form>
    
        {(data.role === 'ADMIN' || data.role === 'MODERATOR') && (
          <div className="content-container">
            <div className="text-review-container">
              <h2 className="section-heading">Texts to Review</h2>
              {textsToReview.length > 0 ? (
                <ul className="text-list">
                  {textsToReview.map((text) => (
                    <li key={text.id} className="text-item">
                      <p className="text-content">{text.content}</p>
                      {(data.role === 'ADMIN' || data.role === 'MODERATOR') && (
                        <button
                          onClick={() => handleReviewedTextSubmit(text.id, text.content)}
                          className="review-submit-button"
                        >
                          Submit
                        </button>
                      )}
                    </li>
                  ))}
                </ul>
              ) : (
                <p>No texts to review</p>
              )}
            </div>
          </div>
        )}
    
        <div className="content-container">
          <div className="reviewed-texts-container">
            <h2 className="section-heading">Reviewed Texts</h2>
            {reviewedTexts.length > 0 ? (
              <ul className="text-list">
                {reviewedTexts.map((text) => (
                  <li key={text.id} className="text-item">
                    <p className="text-content">{text.content}</p>
                    {data.role === 'ADMIN' && (
                      <button onClick={() => handleDelete(text.id)} className="delete-button">
                        Delete
                      </button>
                    )}
                  </li>
                ))}
              </ul>
            ) : (
              <p>No reviewed texts</p>
            )}
          </div>
        </div>
      </div>
    );
  }
    // return (
    //     <div className="user-page-container">
    //       <h1 className="user-page-heading">User Page</h1>
    //       <p className="user-role">{data.role}</p>

    //       <div className="logout-container">
    //         <button className="logout-button" onClick={handleLogout}>Logout</button>
    //       </div>
      
    //       <form onSubmit={handleNewTextSubmit} className="text-submit-form">
    //         <input
    //           type="text"
    //           value={text}
    //           onChange={(e) => setText(e.target.value)}
    //           placeholder="Enter text"
    //           className="text-input"
    //         />
    //         <button type="submit" className="submit-button">Submit</button>
    //       </form>

    //       <div className="content-container">
    //       {(data.role === 'ADMIN' || data.role === 'MODERATOR') && (
    //         <div className="text-review-container">
    //           <h2 className="section-heading">Texts to Review</h2>
    //           {textsToReview.length > 0 ? (
    //             <ul className="text-list">
    //               {textsToReview.map((text) => (
    //                 <li key={text.id} className="text-item">
    //                   <p className="text-content">{text.content}</p>
    //                   {(data.role === 'ADMIN' || data.role === 'MODERATOR') && (
    //                     <button
    //                       onClick={() => handleReviewedTextSubmit(text.id, text.content)}
    //                       className="review-submit-button"
    //                     >
    //                       Submit
    //                     </button>
    //                   )}
    //                 </li>
    //               ))}
    //             </ul>
    //           ) : (
    //             <p>No texts to review</p>
    //           )}
    //         </div>
    //       )}
    //         <div className="reviewed-texts-container">
    //           <h2 className="section-heading">Reviewed Texts</h2>
    //           {reviewedTexts.length > 0 ? (
    //             <ul className="text-list">
    //               {reviewedTexts.map((text) => (
    //                 <li key={text.id} className="text-item">
    //                   <p className="text-content">{text.content}</p>
    //                   {data.role === 'ADMIN' && (
    //                     <button onClick={() => handleDelete(text.id)} className="delete-button">
    //                       Delete
    //                     </button>
    //                   )}
    //                 </li>
    //               ))}
    //             </ul>
    //           ) : (
    //             <p>No reviewed texts</p>
    //           )}
    //         </div>
    //       </div>
    //     </div>
    //   );
    // }	
    // return (
    //     <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', flexDirection: 'column' }}>
    //       <h1>User Page</h1>
    //       {/* Add your UserPage content */}
    //       <p>{data.role}</p>
      
    //       <div>
    //         <button onClick={handleLogout}>Logout</button>
    //       </div>
    //       <form onSubmit={handleNewTextSubmit}>
    //         <input
    //           type="text"
    //           value={text}
    //           onChange={(e) => setText(e.target.value)}
    //           placeholder="Enter text"
    //         />
    //         <button type="submit">Submit</button>
    //       </form>
    //       <div style={{ display: 'flex', gap: '20px' }}>
    //         <div>
    //           <h2>Texts to Review</h2>
    //           {textsToReview.length > 0 ? (
    //             <ul style={{ listStyle: 'none', padding: 0 }}>
    //               {textsToReview.map((text) => (
    //                 <li key={text.id} style={{ display: 'flex', alignItems: 'center' }}>
    //                   <p style={{ marginRight: '10px' }}>{text.content}</p>
    //                   {(data.role === "ADMIN" || data.role === "MODERATOR") && (
    //                     <button onClick={() => handleReviewedTextSubmit(text.id, text.content)}>Submit</button>
    //                   )}
    //                 </li>
    //               ))}
    //             </ul>
    //           ) : (
    //             <p>No texts to review</p>
    //           )}
    //         </div>
    //         <div>
    //           <h2>Reviewed Texts</h2>
    //           {reviewedTexts.length > 0 ? (
    //             <ul style={{ listStyle: 'none', padding: 0 }}>
    //               {reviewedTexts.map((text) => (
    //                 <li key={text.id} style={{ display: 'flex', alignItems: 'center' }}>
    //                   <p style={{ marginRight: '10px' }}>{text.content}</p>
    //                   {data.role === "ADMIN" && (
    //                     <button onClick={() => handleDelete(text.id)}>Delete</button>
    //                   )}
    //                 </li>
    //               ))}
    //             </ul>
    //           ) : (
    //             <p>No reviewed texts</p>
    //           )}
    //         </div>
    //       </div>
    //     </div>
    //   );
    // }
    //   return (
    //     <div>
    //       <h1>User Page</h1>
    //       {/* Add your UserPage content */}
    //       <p>{data.role}</p>
    
    //       <div>
    //     <button onClick={handleLogout}>Logout</button>
    //     </div>
    //     <form onSubmit={handleNewTextSubmit}>
    //     <input
    //       type="text"
    //       value={text}
    //       onChange={(e) => setText(e.target.value)}
    //       placeholder="Enter text"
    //     />
    //     <button type="submit">Submit</button>
    //   </form>
    //   <h2>Texts to Review</h2>
    //   {textsToReview.length > 0 ? (
    //     <ul style={{ listStyle: 'none', padding: 0 }}>
    //       {textsToReview.map((text) => (
    //         <li key={text.id}>
    //           <p>{text.content}</p>
    //           { (data.role === "ADMIN" || data.role === "MODERATOR") && <button onClick={() => handleReviewedTextSubmit(text.id, text.content)}>Submit</button>}
    //         </li>
    //       ))}
    //     </ul>
    //   ) : (
    //     <p>No texts to review</p>
    //   )}
    //       <h2>Reviewed Texts</h2>
    //       {reviewedTexts.length > 0 ? (
    //     <ul style={{ listStyle: 'none', padding: 0 }}>
    //       {reviewedTexts.map((text) => (
    //         <li key={text.id}>
    //           <p>{text.content}</p>
    //           {data.role === "ADMIN" && <button onClick={() => handleDelete(text.id)}>Delete</button> }
    //         </li>
    //       ))}
    //     </ul >
    //   ) : (
    //     <p>No reviewed texts</p>
    //   )}
    //     </div>
    //   );
    // };
export default UserPage;



