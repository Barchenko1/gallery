import React from 'react';
import {ISlideList} from "../../../types/IType";
import OverviewElement from "./OverviewElement";
import "./OverviewPage.css"
import useWindowDimensions from "../../../hooks/useWindow";
import NavBar from "../navBar/NavBar";



const OverviewPage = ({slides}: ISlideList) => {

    const windowSize = useWindowDimensions();

    const divStyle = () => {
        return windowSize.innerWidth > 550 ? "3fr 1fr" : "";
    }

    const viewNavBar = () => {
        return windowSize.innerWidth > 550 ? <NavBar />: null;
    }

    console.log(windowSize);
    // console.log(divStyle())
    return(
        <div className="overview-container" style={{gridTemplateColumns: divStyle()}}>
            <div className="image-container">
                {slides.map((slide, i) => (
                    <OverviewElement key={slide + i} content={slide} index={i} />
                ))}
            </div>
            {viewNavBar()}
        </div>
    )
}

export default OverviewPage;