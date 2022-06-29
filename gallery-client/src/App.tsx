import React from 'react';
import './App.css';
import images from "./images";
import Slider from './components/Slider'

function App() {
  return (
    <Slider slides={images} />
  );
}

export default App;
