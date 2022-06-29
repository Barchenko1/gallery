import React from 'react';
import {ISlideList} from "../types/IType";
import OverviewElement from "./OverviewElement";
import Slide from "./Slide";
import SliderContent from "./SliderContent";
/** @jsxImportSource @emotion/react */
import { css } from '@emotion/react';

const OverviewPage = ({slides}: ISlideList) => {
    return(

        <div
            css={SliderCSS}
        >
            {slides.map((slide, i) => (
                <OverviewElement key={slide + i} content={slide} />
            ))}
        </div>
    )
}

const SliderCSS = css`
  position: relative;
  height: 100vh;
  width: 100vw;
  margin: 0 auto;
  overflow: hidden;
  display: flex;
  justify-content: space-evenly;
`

export default OverviewPage;