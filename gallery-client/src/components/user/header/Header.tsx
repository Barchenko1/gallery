import React from 'react';
import './Header.css'
import {Link} from "react-router-dom";

const Header = () => {
    return(
        <div className="header">
            <div>
                <Link to="/" className="title-link">Home</Link>
                <Link to="/subjects" className="title-link">Subjects</Link>
                <Link to="/about-us" className="title-link">About</Link>
            </div>
        </div>
    )
}

export default Header;