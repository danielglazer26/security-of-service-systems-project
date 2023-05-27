// DeleteText.js
import React from 'react';

const DeleteText = ({ text, onDelete }) => {
  return (
    <div>
      <strong>Text:</strong> {text}
      <button onClick={onDelete}>Delete</button>
    </div>
  );
};

export default DeleteText;

// import React from 'react';
// import axios from './api/axios';

// const DeleteText = ({ text, onDelete }) => {
//     const handleDelete = async () => {
//       try {
//         await axios.delete('/api/text');
//         onDelete();
//       } catch (error) {
//         console.error('Error deleting text:', error.message);
//       }
//     };
  
//     return (
//       <div>
//         <strong>Text:</strong> {text}
//         <button onClick={handleDelete}>Delete</button>
//       </div>
//     );
//   };

// export default DeleteText;
