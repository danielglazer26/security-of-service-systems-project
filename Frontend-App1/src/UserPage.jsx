import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import axios from './api/axios';

const App = () => {
  
  const [text, setText] = useState('');
  const [reviewTexts, setReviewTexts] = useState([]);
  const [reviewedTexts, setReviewedTexts] = useState([]);
  const location = useLocation()
  const { data } = location.state;
  const GET_TEXTS_URL = "api/text/review";
  const GET_REVIEWED_TEXTS_URL = "api/text";
  const POST_NEW_TEXT_URL = "api/text";
  const POST_REVIEWED_TEXT_URL = "api/text/review";
  

  // Fetch initial data on component mount
  useEffect(() => {
    fetchReviewTexts();
    fetchReviewedTexts();
  }, []);

  const fetchReviewTexts = async () => {
    try {
      const response = await axios.get(GET_TEXTS_URL, { withCredentials: true });
      setReviewTexts(response.data);
    } catch (error) {
      console.error('Error fetching review texts:', error);
    }
  };

  const fetchReviewedTexts = async () => {
    try {
      const response = await axios.get(GET_REVIEWED_TEXTS_URL, { withCredentials: true });
      setReviewedTexts(response.data);
    } catch (error) {
      console.error('Error fetching reviewed texts:', error);
    }
  };

  const handleTextChange = (event) => {
    setText(event.target.value);
  };

  const handleSubmit = async () => {
    try {
      await axios.post(POST_NEW_TEXT_URL, { text }, { withCredentials: true });
      setText('');
      fetchReviewTexts(); // Refresh the review texts after submission
    } catch (error) {
      console.error('Error submitting text:', error);
    }
  };

  const handleReviewSubmit = async (textId) => {
    try {
      await axios.post(POST_REVIEWED_TEXT_URL, { id: textId }, { withCredentials: true });
      fetchReviewTexts(); // Refresh the review texts after submission
      fetchReviewedTexts(); // Refresh the reviewed texts after submission
    } catch (error) {
      console.error('Error submitting review:', error);
    }
  };

  const handleDeleteReview = async (textId) => {
    try {
      await axios.delete(`${GET_REVIEWED_TEXTS_URL}?id=${textId}`, { withCredentials: true });
      fetchReviewedTexts(); // Refresh the reviewed texts after deletion
    } catch (error) {
      console.error('Error deleting review:', error);
    }
  };

  return (
    <div>
      <h1>Four Eyes Rule Demo</h1>

      {/* Text input for user */}
      <input type="text" value={text} onChange={handleTextChange} />
      <button onClick={handleSubmit}>Submit Text</button>

      {/* Text areas to display texts to review */}
      <h2>Texts to Review</h2>
      {reviewTexts.map((text) => (
        <div key={text.id}>
          <textarea defaultValue={text} readOnly />
          <button onClick={() => handleReviewSubmit(text.id)}>Submit Review</button>
        </div>
      ))}

      {/* List of all reviewed texts */}
      <h2>Reviewed Texts</h2>
      {reviewedTexts.map((review) => (
        <div key={review.id}>
          <textarea defaultValue={review.text} readOnly />
          <button onClick={() => handleDeleteReview(review.id)}>Delete</button>
        </div>
      ))}
    </div>
  );
};

export default App;

// import React, { useState, useEffect } from 'react';
// import TextInput from './TextInput';
// import ReviewText from './ReviewText';
// import DeleteText from './DeleteText';
// import { useNavigate, useLocation } from 'react-router-dom';
// import axios from './api/axios';

// const UserPage = () => {
//   const location = useLocation();
//   const [texts, setTexts] = useState([]);
//   const [reviewTexts, setReviewTexts] = useState([]);
//   const [reviewedTexts, setReviewedTexts] = useState([]);
//   const [isLoading, setIsLoading] = useState(true);
//   const { data } = location.state;
//   const GET_TEXTS_URL = "api/text/review";
//   const GET_REVIEWED_TEXTS_URL = "api/text";
//   const POST_NEW_TEXT_URL = "api/text";
//   const POST_REVIEWED_TEXT_URL = "api/text/review";


//   const fetchTexts = () => {
//     axios
//       .get(GET_TEXTS_URL,{ withCredentials: true })
//       .then((response) => {
//         setTexts(response.data);
//       })
//       .catch((error) => {
//         console.error('Error fetching texts:', error.message);
//       });
//   };

//   // const fetchReviewTexts = () => {
//   //   axios
//   //     .get('/api/text')
//   //     .then((response) => {
//   //       setReviewTexts(response.data);
//   //     })
//   //     .catch((error) => {
//   //       console.error('Error fetching review texts:', error.message);
//   //     });
//   // };

//   const fetchReviewedTexts = () => {
//     axios
//       .get(GET_REVIEWED_TEXTS_URL, { withCredentials: true })
//       .then((response) => {
//         setReviewedTexts(response.data);
//         console.log(response)
//       })
//       .catch((error) => {
//         console.error('Error fetching reviewed texts:', error.message);
//       });
//   };

//   useEffect(() => {
//     fetchTexts();
//     // fetchReviewTexts();
//     fetchReviewedTexts();
//     setIsLoading(false);
//   }, []);

//   const handleTextSubmit = (text) => {
//     axios
//       .post(POST_NEW_TEXT_URL, { text }, { withCredentials: true })
//       .then((response) => {
//         fetchTexts();
//       })
//       .catch((error) => {
//         console.error('Error adding text:', error.message);
//       });
//   };

//   const handleReviewSubmit = (reviewText) => {
//     axios
//       .post(POST_REVIEWED_TEXT_URL, { reviewText })
//       .then((response) => {
//         fetchReviewedTexts();
//       })
//       .catch((error) => {
//         console.error('Error submitting review:', error.message);
//       });
//   };

//   const handleDelete = (id) => {
//     axios
//       .delete(`/api/text?id=${id}`)
//       .then((response) => {
//         fetchReviewedTexts();
//       })
//       .catch((error) => {
//         console.error('Error deleting text:', error.message);
//       });
//   };

//   return (
//     <div>
//       <h1>User Page</h1>
//       {!isLoading && (
//         <div>
//           <h2>Submit Text</h2>
//           <TextInput onSubmit={handleTextSubmit} />
//           <hr />
//           <h2>Review Texts</h2>
//           {texts.map((text) => (
//             <ReviewText
//               key={text.reviewedTextId}
//               text={text.content}
//               onSubmit={(reviewText) => handleReviewSubmit(reviewText.reviewedTextId, reviewText.content)}
//             />
//           ))}
//           <hr />
//           <h2>Reviewed Texts</h2>
//           {reviewedTexts.map((text) => (
//             <div key={text.id}>
//               <DeleteText text={text.text} onDelete={() => handleDelete(text.id)} />
//             </div>
//           ))}
//         </div>
//       )}
//     </div>
//   );
// };

// export default UserPage;

// // import React, { useEffect, useState } from 'react';
// // import { useNavigate, useLocation } from 'react-router-dom';
// // import TextInput from './TextInput';
// // import ReviewText from './ReviewText';
// // import DeleteText from './DeleteText';
// // import axios from './api/axios';


// // export const UserPage = () => {
// //   const navigate = useNavigate();
// //   const location = useLocation();
// //   const USER_INFO_URL = 'api/user/info';
// //   const { data } = location.state;

// //   const [text, setText] = useState('');
// //   const [reviewedText, setReviewedText] = useState('');

// //   const handleTextSubmit = (inputText) => {
// //     setText(inputText);
// //   };

// //   const handleReviewSubmit = (reviewText) => {
// //     setReviewedText(reviewText);
// //   };

// //   const handleDelete = async () => {
// //     try {
// //       await axios.delete('/api/text');
// //       setText('');
// //       setReviewedText('');
// //     } catch (error) {
// //       console.error('Error deleting text:', error.message);
// //     }
// //   };

// //   useEffect(() => {
// //     const fetchText = async () => {
// //       try {
// //         const response = await axios.get('/api/text');
// //         const { text } = response.data;
// //         setText(text);
// //       } catch (error) {
// //         console.error('Error fetching text:', error.message);
// //       }
// //     };

// //     fetchText();
// //   }, []);

// //   return (
// //     <div>
// //       <h1>SSO App - 4 Eyes Rule</h1>
// //       {data.role === 'USER' && !text && <TextInput onSubmit={handleTextSubmit} />}
// //       {data.role === 'USER' && text && !reviewedText && (
// //         <ReviewText text={text} onSubmit={handleReviewSubmit} />
// //       )}
// //       {data.role === 'USER' && reviewedText && (
// //         <DeleteText text={reviewedText} onDelete={handleDelete} />
// //       )}
// //     </div>
// //   );
// // };
// // //   return (
// // //     <div>
// // //       <h1>User Page</h1>
// // //       {/* Add your UserPage content */}
// // //       <p>{data.role}</p>
// // //     </div>
// // //   );
// // // };

// // export default UserPage;

// //   useEffect(() => {
// //     // Send GET request using Axios
// //     axios
// //     .get(USER_INFO_URL)
// //     .then((response) => {
// //       const data = response.data;
// //       console.log(data);
// //       navigate('/userpage'); // Redirect to /userpage on successful response
// //     })
// //     .catch((error) => {
// //       console.error('Error checking access to userpage:', error);
// //       if (error.response && error.response.status === 401) {
// //         navigate('/home'); // Redirect to /home on 401 (unauthorized) response
// //       }
// //       else {
// //         navigate('/home')
// //       }
// //     });
// // }, []);
