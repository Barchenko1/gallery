import React from "react";
import {Link} from "react-router-dom";

interface ISubject {
    link: string,
    linkText: string,
    imgLink: string,
    description: string
}

const Subject = ({link, linkText, imgLink, description}: ISubject) => {

    const viewImg = () => {
        return imgLink === "" ? <div/> : <img src={imgLink} alt={linkText} />
    }

    return(
        <div>
            {viewImg()}
            <Link to={link}>{linkText}</Link>
            <div>{description}</div>
        </div>
    )
}

export default Subject;