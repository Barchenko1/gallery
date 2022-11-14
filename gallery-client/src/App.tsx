import React from 'react';
import './App.css';
import images from "./images";
import Slider from './components/sliderPage/Slider'
import {
    Route,
    Routes,
    BrowserRouter
} from 'react-router-dom';
import OverviewPage from "./components/overviewPage/OverviewPage";
import Header from "./components/header/Header";

function App() {

  return (
      <BrowserRouter>
          <Header/>
          <Routes>
              <Route path="/" element={<OverviewPage slides={images}/>} />
              <Route path="/slider" element={<Slider slides={images} />} />
          </Routes>
      </BrowserRouter>
  );
}

export default App;
