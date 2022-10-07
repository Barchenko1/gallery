import React from 'react';
/** @jsxImportSource @emotion/react */
import {css} from "@emotion/react";
import exit from "../../img/exit.png";
import { Link } from 'react-router-dom';

const Exit = () => {

    return(
        <div>
            <Link to="/">
                <img css={css`
                  position: absolute;
                  top: 25px;
                  right:25px;
                  width:25px;
                  height:25px;
                  // display:flex;
                  `}
                    src={exit} />
            </Link>
        </div>
    )
}

export default Exit;