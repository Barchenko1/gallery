import React from "react";

interface IDot {
    active:boolean
}

const Dot = ({active}: IDot) => {


    return(
        <div style={{backgroundColor: `${active ? 'black' : 'white'}`}} className="dot"/>
    )
}

export default Dot;