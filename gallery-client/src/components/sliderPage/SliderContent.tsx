import React from 'react';

interface ISliderContent {
    translate:number,
    transition:number,
    width:number,
    children:React.ReactNode
}

const SliderContent = ({translate, transition, width, children}: ISliderContent) => {

    const divStyle = {
        transform: `translateX(-${translate}px)`,
        transition: `transform ease-out ${transition}s`,
        width: `${width}px`,
    }

    console.log(children);
    return(
        <div style={divStyle} className="slider-content">
            {children}
        </div>
    )
}

export default SliderContent;