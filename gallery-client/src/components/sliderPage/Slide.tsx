import React from 'react';
import './Slider.css'
import {ISlide} from "../../types/IType";

const Slide = ({ content }: ISlide) => {
    console.log(content);
    return(
        <div style={{backgroundImage: `url(${content}`}} className="slide"/>
    )
}

export default Slide;