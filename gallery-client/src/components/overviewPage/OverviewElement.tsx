import React from 'react';
import {ISlide} from "../../types/IType";
/** @jsxImportSource @emotion/react */
import { css } from '@emotion/react';

interface IElement {
    index: number,
    content: string
}

const OverviewElement = ({index, content}: IElement) => {

    const handlePicture = (index: number) => {
        console.log(index);
    }

    return(
        <div
            onClick={() => handlePicture(index)}
            css={css`
                width: 100%;
                height: 100%;
                background-image: url('${content}');
                background-size: cover;
                transition: transform .2s;
                &:hover {
                    transform: scale(1.2);
                }
            `}
        />
    )
}

export default OverviewElement;