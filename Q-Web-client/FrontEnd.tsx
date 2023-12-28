import React from "react"
import {Button} from "@mui/material"
import {Tile, Empty, Coord, makeCoord} from "./Data.ts"
import { JSXElementConstructor, StrictMode } from "react"

type Loc = Tile | Empty

function drawLoc(value : Loc) : string {
    if (value === "Empty") {
        return value;
    }
    else {
        return value.color.charAt(0).toUpperCase + " " + value.shape.charAt(0).toUpperCase;
    }
}

//TODO: Figure out what the proper return type is (JSX has been depricated). Is it a button element?
function BoardLocation ({ value, location, onClick } :
     {value : Loc, location : Coord, onClick : () => void}) : React.JSX.Element  {
    return (
       <Button className="BoardLocation" onClick={onClick}>
            {drawLoc(value)}
       </Button>
    );
}

export default function Game() : React.JSX.Element {
    const clickHandler = () => (Error("I do a thing!"));
    return (
      <div className="Game">
        {BoardLocation({value: "Empty", location: makeCoord(0, 0), onClick: clickHandler})}
      </div>  
    );
}
