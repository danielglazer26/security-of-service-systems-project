import React, { useState } from 'react';

const TextInput = ({ onSubmit }) => {
  const [content, setContent] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    onSubmit(content);
    setContent('');
  };

  return (
    <form onSubmit={handleSubmit}>
      <input
        type="text"
        value={content}
        onChange={(e) => setContent(e.target.value)}
        placeholder="Enter text"
      />
      <button type="submit">Submit</button>
    </form>
  );
};

export default TextInput;

// import React, { useState } from 'react';
// import axios from './api/axios';

// const TextInput = ({ onSubmit }) => {
//   const [text, setText] = useState('');

//   const handleSubmit = async (e) => {
//     e.preventDefault();

//     try {
//       await axios.post('/api/text', { text });
//       onSubmit(text);
//       setText('');
//     } catch (error) {
//       console.error('Error storing text:', error.message);
//     }
//   };

//   return (
//     <form onSubmit={handleSubmit}>
//       <input
//         type="text"
//         value={text}
//         onChange={(e) => setText(e.target.value)}
//         placeholder="Enter text"
//       />
//       <button type="submit">Submit</button>
//     </form>
//   );
// };


// export default TextInput;
