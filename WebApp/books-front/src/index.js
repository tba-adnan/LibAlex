import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';
import reportWebVitals from './reportWebVitals';
import MainBooks from './component/pages/mainbook';
import BookRoute from './component/router';
// 

const routing = ReactDOM.createRoot(document.getElementById('route'));
routing.render(<BookRoute />);
reportWebVitals();
