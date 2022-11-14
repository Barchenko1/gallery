import React from "react";
import SupportBanner from "./supportBanner/SupportBanner";
import FoldersNavbarList from "./foldersNavbar/FoldersNavbarList";
import './NavBar.css'

const NavBar = () => {
    return(
        <div className="navigate-container">
            <SupportBanner />
            <FoldersNavbarList />
        </div>
    )
}

export default NavBar;