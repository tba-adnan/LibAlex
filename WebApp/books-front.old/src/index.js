import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';
import Navbar from './components/navbar';
import MainBooks from './components/main';
import Footer from './components/footer';
import Index from './components';
// 
import reportWebVitals from './reportWebVitals';



const main = ReactDOM.createRoot(document.getElementById('main'))
main.render(<Index/>);




// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
