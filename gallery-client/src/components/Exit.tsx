import React from 'react';
/** @jsxImportSource @emotion/react */
import {css} from "@emotion/react";
import exit from "../img/exit.png";

interface IExit {
    handleClick:React.MouseEventHandler<HTMLDivElement>
}

const Exit = ({handleClick}: IExit) => {

    return(
        <div
            onClick={handleClick}
            css={css`
                  position: absolute;
                  top: 25px;
                  right:25px;
                  width:25px;
                  height:25px;
                  display:flex;
                  // width: 100%;
                  // display: flex;
                  // align-items: center;
                  // justify-content: center;
                  `}
        >
            <img src={exit} />
        </div>
    )
}

export default Exit;