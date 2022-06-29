import React from 'react';
/** @jsxImportSource @emotion/react */
import { css } from '@emotion/react';

interface ISliderContent {
    translate:number,
    transition:number,
    width:number,
    children:React.ReactNode
}

const SliderContent = ({translate, transition, width, children}: ISliderContent) => {
    return(
        <div css={css`
            transform: translateX(-${translate}px);
            transition: transform ease-out ${transition}s;
            height: 100%;
            width: ${width}px;
            display: flex;
        `}>
            {children}
        </div>
    )
}

export default SliderContent;