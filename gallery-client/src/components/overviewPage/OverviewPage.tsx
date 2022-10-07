import React from 'react';
import {ISlideList} from "../../types/IType";
import OverviewElement from "./OverviewElement";
/** @jsxImportSource @emotion/react */
import { css } from '@emotion/react';

const OverviewPage = ({slides}: ISlideList) => {
    return(

        <div
            css={OverviewContainer}
        >
            {slides.map((slide, i) => (
                <OverviewElement key={slide + i} content={slide} index={i} />
            ))}
        </div>
    )
}

const OverviewContainer = css`
    display: grid;
    grid-gap: 5px;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    grid-template-rows: repeat(auto-fit, 200px);
    grid-auto-rows: 200px;
`

export default OverviewPage;