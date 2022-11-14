import React from 'react';
import './Slider.css'

interface IAutoPlay {
    handleClick:(speed: number) => void
}

const AutoPlay = ({handleClick}: IAutoPlay) => {

    return(
        <div className="slider-timer">
            <div onClick={() => handleClick(1)}>x1</div>
            <div onClick={() => handleClick(2)}>x2</div>
            <div onClick={() => handleClick(3)}>x3</div>
            <div onClick={() => handleClick(10000)}>stop</div>
        </div>
    )
}

export default AutoPlay;