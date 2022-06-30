import React, {useEffect, useRef, useState} from 'react';
import Arrow from "./Arrow";
import Dots from "./Dots";
import SliderContent from "./SliderContent";
import Slide from "./Slide";
/** @jsxImportSource @emotion/react */
import { css } from '@emotion/react';
import AutoPlay from "./AutoPlay";
import Exit from "./Exit";
import {ISlideList} from "../../types/IType";

const getWidth = () => window.innerWidth;

const Slider = ({slides}: ISlideList) => {

    const AUTO_PLAY_STOP: number = 1000;

    const [state, setState] = useState({
        activeIndex: 0,
        translate: 0,
        transition: 0.45,
        autoPlay: AUTO_PLAY_STOP
    })

    const { activeIndex, translate, transition, autoPlay } = state

    const autoPlayRef = useRef<any>(autoPlay);

    useEffect(() => {
        autoPlayRef.current = autoPlaySlide;
    })

    useEffect(() => {
        const play = () => {
            autoPlayRef.current();
        }

        let interval: NodeJS.Timer;
        if (autoPlay === AUTO_PLAY_STOP) {
            interval = setInterval(()=>{}, autoPlay * 1000);
        } else{
            interval = setInterval(play, autoPlay * 1000);
        }
        return () => clearInterval(interval);
    }, [autoPlay])

    const autoPlaySlide = () => {
        if (activeIndex === slides.length - 1) {
            return setState({
                ...state,
                translate: 0,
                activeIndex: 0,
                autoPlay: autoPlay,
            })
        }

        setState({
            ...state,
            activeIndex: activeIndex + 1,
            translate: (activeIndex + 1) * getWidth(),
            autoPlay: autoPlay,
        })
    }

    const nextSlide = () => {
        if (activeIndex === slides.length - 1) {
            return setState({
                ...state,
                translate: 0,
                activeIndex: 0,
                autoPlay: AUTO_PLAY_STOP,
            })
        }

        setState({
            ...state,
            activeIndex: activeIndex + 1,
            translate: (activeIndex + 1) * getWidth(),
            autoPlay: AUTO_PLAY_STOP,
        })
    }

    const prevSlide = () => {
        if (activeIndex === 0) {
            return setState({
                ...state,
                translate: (slides.length - 1) * getWidth(),
                activeIndex: slides.length - 1,
                autoPlay: AUTO_PLAY_STOP,
            })
        }

        setState({
            ...state,
            activeIndex: activeIndex - 1,
            translate: (activeIndex - 1) * getWidth(),
            autoPlay: AUTO_PLAY_STOP,
        })
    }

    const handleAutoPlay = (speed: number) => {

        setState({
            ...state,
            autoPlay: speed,
        });
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
                <AutoPlay handleClick={handleAutoPlay}/>
                <Exit/>
            </>
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