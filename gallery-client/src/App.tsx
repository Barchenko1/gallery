import React from 'react';
import './App.css';
import images from "./images";
import Slider from './components/user/sliderPage/Slider'
import {
    Route,
    Routes,
    BrowserRouter
} from 'react-router-dom';
import OverviewPage from "./components/user/overviewPage/OverviewPage";
import Header from "./components/user/header/Header";
import SubjectPage from "./components/user/subject/SubjectPage";
import AboutUsPage from "./components/user/info/AboutUsPage";

function App() {

  return (
      <BrowserRouter>
          <Header/>
          <Routes>
              <Route path="/" element={<OverviewPage slides={images}/>} />
              <Route path="/subjects" element={<SubjectPage />} />
              <Route path="/slider" element={<Slider slides={images} />} />
              <Route path="/about-us" element={<AboutUsPage />}/>
          </Routes>
      </BrowserRouter>
  );
}

export default App;
