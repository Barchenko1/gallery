import React from 'react';
import {ISlide} from "../types/IType";
/** @jsxImportSource @emotion/react */
import { css } from '@emotion/react';

const OverviewElement = ({content}: ISlide) => {

    return(
        <div
            css={css`
                height: 20%;
                width: 20%;
                background-image: url('${content}');
                background-size: cover;
                background-repeat: no-repeat;
                background-position: 50% 50%;
                `}
        />
    )
}

export default OverviewElement;