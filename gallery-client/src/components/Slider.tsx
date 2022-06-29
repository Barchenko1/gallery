import React, {useEffect, useRef, useState} from 'react';
import Arrow from "./Arrow";
import Dots from "./Dots";
import SliderContent from "./SliderContent";
import Slide from "./Slide";
/** @jsxImportSource @emotion/react */
import { css } from '@emotion/react';

const getWidth = () => window.innerWidth;

interface ISlider {
    slides:string[],
    autoPlay:number
}

const Slider = ({slides, autoPlay}: ISlider) => {

    const [state, setState] = useState({
        activeIndex: 0,
        translate: 0,
        transition: 0.45
    })

    const { activeIndex, translate, transition } = state

    const autoPlayRef = useRef<any>();

    useEffect(() => {
        autoPlayRef.current = nextSlide;
    })

    useEffect(() => {
        const play = () => {
            autoPlayRef.current();
        }

        const interval = setInterval(play, autoPlay * 1000)
        return () => clearInterval(interval);
    }, [autoPlay])

    const nextSlide = () => {
        if (activeIndex === slides.length - 1) {
            return setState({
                ...state,
                translate: 0,
                activeIndex: 0
            })
        }

        setState({
            ...state,
            activeIndex: activeIndex + 1,
            translate: (activeIndex + 1) * getWidth()
        })
    }

    const prevSlide = () => {
        if (activeIndex === 0) {
            return setState({
                ...state,
                translate: (slides.length - 1) * getWidth(),
                activeIndex: slides.length - 1
            })
        }

        setState({
            ...state,
            activeIndex: activeIndex - 1,
            translate: (activeIndex - 1) * getWidth()
        })
    }

    const width:number = getWidth() * slides.length;

    return(
        <div
            css={SliderCSS}>
            <SliderContent
                translate={translate}
                transition={transition}
                width={width}
            >
                {slides.map((slide, i) => (
                    <Slide key={slide + i} content={slide} />
                ))}
            </SliderContent>
            <>
                <Arrow direction="left" handleClick={prevSlide} />
                <Arrow direction="right" handleClick={nextSlide} />
            </>

            <Dots slides={slides} activeIndex={activeIndex} />
        </div>
    )
}

const SliderCSS = css`
  position: relative;
  height: 100vh;
  width: 100vw;
  margin: 0 auto;
  overflow: hidden;
`

export default Slider;