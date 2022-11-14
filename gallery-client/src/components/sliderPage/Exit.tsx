import React from 'react';
import exit from "../../img/exit.png";
import { Link } from 'react-router-dom';

const Exit = () => {

    return(
        <div>
            <Link to="/">
                <img className="slider-exit" src={exit} alt="" />
            </Link>
        </div>
    )
}

export default Exit;