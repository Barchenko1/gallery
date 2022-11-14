import React from 'react';
import {ISlideList} from "../../types/IType";
import OverviewElement from "./OverviewElement";
import "./OverviewPage.css"
import SupportBanner from "../supportBanner/SupportBanner";
import FoldersNavbarList from "../foldersNavbar/FoldersNavbarList";

const OverviewPage = ({slides}: ISlideList) => {
    return(
        <div className="overview-container">
            <div className="image-container">
                {slides.map((slide, i) => (
                    <OverviewElement key={slide + i} content={slide} index={i} />
                ))}
            </div>
            <div className="navigate-container">
                <SupportBanner />
                <FoldersNavbarList />
            </div>
        </div>
    )
}

export default OverviewPage;