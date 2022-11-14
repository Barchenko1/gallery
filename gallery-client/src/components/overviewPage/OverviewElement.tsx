import React from 'react';
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
            style={{backgroundImage: `url(${content}`}}
            className="overview-element"
        />
    )
}

export default OverviewElement;