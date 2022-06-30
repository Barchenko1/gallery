import React from 'react';
/** @jsxImportSource @emotion/react */
import { css } from '@emotion/react';

interface IDot {
    active:boolean
}

interface IDots {
    slides:string[],
    activeIndex:number
}

const Dot = ({active}: IDot) => {

    return(
        <span
            css={css`
                padding: 10px;
                margin-right: 5px;
                cursor: pointer;
                border-radius: 50%;
                background: ${active ? 'black' : 'white'};
                `}
        />
    )
}

const Dots = ({ slides, activeIndex }: IDots) => {

    return(
        <div
            css={css`
                  position: absolute;
                  bottom: 25px;
                  width: 100%;
                  display: flex;
                  align-items: center;
                  justify-content: center;
                  `}
        >
            {slides.map((slide, i) => (
                <Dot key={slide} active={activeIndex === i} />
            ))}
        </div>
    )
}

export default Dots;