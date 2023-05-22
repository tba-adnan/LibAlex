import React from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Saving from "./pages/savings";
import MainBooks from "./pages/mainbook";
import Saved from "./pages/saved";



export default class BookRoute extends React.Component {
    constructor() {
        super();
    }

render() {
    return (
      <BrowserRouter>
      <Routes>
          <Route path="/" element={<MainBooks />} />
          <Route path="savings" element={<Saving />} />
          <Route path="saved" element={<Saved />} />
      </Routes>
     </BrowserRouter>
     
      );
}}