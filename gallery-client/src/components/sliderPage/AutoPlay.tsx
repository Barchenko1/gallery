import React from 'react';
/** @jsxImportSource @emotion/react */
import {css} from "@emotion/react";

interface IAutoPlay {
    handleClick:(speed: number) => void
}

const AutoPlay = ({handleClick}: IAutoPlay) => {

    return(
        <div css={css`
                  position: absolute;
                  top: 50px;
                  width: 100%;
                  display: flex;
                  align-items: center;
                  justify-content: center;
                  `}
        >
            <div css={buttonCss} onClick={() => handleClick(1)}>x1</div>
            <div css={buttonCss} onClick={() => handleClick(2)}>x2</div>
            <div css={buttonCss} onClick={() => handleClick(3)}>x3</div>
            <div css={buttonCss} onClick={() => handleClick(1000)}>stop</div>
        </div>
    )
}

const buttonCss = css`
                padding: 10px;
                margin-right: 5px;
                cursor: pointer;
                border-radius: 50%;
                `

export default AutoPlay;