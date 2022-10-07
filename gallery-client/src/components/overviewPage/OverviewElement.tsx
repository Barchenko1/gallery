import React from 'react';
import {ISlide} from "../../types/IType";
/** @jsxImportSource @emotion/react */
import { css } from '@emotion/react';
import { useNavigate } from "react-router-dom";

interface IElement {
    index: number,
    content: string
}

const OverviewElement = ({index, content}: IElement) => {

    const navigate = useNavigate();

    const handlePicture = (index: number) => {
        console.log(index);
        navigate("/slider");
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