import React, { useState } from 'react';

const ReviewText = ({text, onSubmit }) => {
  const [content, setContent] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    onSubmit(content);
    setContent('');
  };

  return (
    <form onSubmit={handleSubmit}>
      {/* <div>
        <strong>Text to Review:</strong> {text}
      </div> */}
      <textarea
        value={text}
        onChange={(e) => setContent(e.target.value)}
        placeholder="Enter review"
        
      ></textarea>
      <button type="submit">Submit Review</button>
    </form>
  );
};

export default ReviewText;

// import React, { useState } from 'react';
// import axios from './api/axios';

// const ReviewText = ({ text, onSubmit }) => {
//     const [reviewedText, setReviewedText] = useState('');
  
//     const handleSubmit = async (e) => {
//       e.preventDefault();
  
//       try {
//         await axios.post('/api/text/review', { text: reviewedText });
//         onSubmit(reviewedText);
//         setReviewedText('');
//       } catch (error) {
//         console.error('Error storing reviewed text:', error.message);
//       }
//     };
  
//     return (
//       <form onSubmit={handleSubmit}>
//         <div>
//           <strong>Text to Review:</strong> {text}
//         </div>
//         <textarea
//           value={reviewedText}
//           onChange={(e) => setReviewedText(e.target.value)}
//           placeholder="Enter review"
//         ></textarea>
//         <button type="submit">Submit Review</button>
//       </form>
//     );
//   };

// export default ReviewText;
