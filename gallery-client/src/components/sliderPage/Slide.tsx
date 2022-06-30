import React from 'react';
/** @jsxImportSource @emotion/react */
import { css } from '@emotion/react';
import {ISlide} from "../../types/IType";

const Slide = ({ content }: ISlide) => {
    console.log(content)
    return(
        <div
            css={css`
                height: 100%;
                width: 100%;
                background-image: url('${content}');
                background-size: cover;
                background-repeat: no-repeat;
                background-position: 50% 50%;
                `}
        />
    )
}

export default Slide;