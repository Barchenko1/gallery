import React from 'react';
import "./../NavBar.css"
import qr from "../../../../img/TKj3WJhmSgcvuzaBxKNX2L9eMTEBFbkWaE.png"
import {Link} from "react-router-dom";

const SupportBanner = () => {
    return(
        <div className="supportBanner">
            <Link to="/">
                <img src="https://c5.patreon.com/external/logo/become_a_patron_button.png"
                     alt="Became a patron" />
            </Link>
            <img className="qr" src={qr} alt="" />
            <div>
                Please, support project
            </div>
        </div>
    )
}

export default SupportBanner;