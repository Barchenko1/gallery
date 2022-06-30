import React from 'react';
import {ISlide} from "../../types/IType";
/** @jsxImportSource @emotion/react */
import { css } from '@emotion/react';

const OverviewElement = ({content}: ISlide) => {

    return(
        <div
            css={css`
                width: 100%;
                height: 100%;
                background-image: url('${content}');
                background-size: cover;
                `}
        />
    )
}

export default OverviewElement;