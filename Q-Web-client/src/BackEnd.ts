export {
    match,
    validPlacement
}

import {Board, Tile, Coord, makeCoord} from "./Data"

/**
 * Determines if two tiles match in either shape or color
 */
function match(t1 : Tile, t2: Tile) : boolean {
    return (t1.color == t2.color) || (t1.shape == t2.shape);
}



function above(c: Coord) : Coord {
    return makeCoord(c.x, c.y - 1);
}

function below(c : Coord) : Coord {
    return makeCoord(c.x, c.y + 1);
}

function left(c : Coord) : Coord {
    return makeCoord(c.x - 1, c.y);
}

function right(c : Coord) : Coord {
    return makeCoord(c.x + 1, c.y);
}

/**
 * If both tiles exists, determines if htey match. Otherwise, returns true. 
 * @param t1 the tile that does exist
 * @param t2 the tile that may or may not exist
 */
function matchesIfExists(t1 : Tile | undefined, t2 : Tile | undefined) {
    return (t2 == undefined) || (t1 == undefined) || match(t1, t2)
}

/**
 * Determines whether it is valid to place tile t at location c on board b. 
 * @param c the coordinate that the tile will be placed at.
 * @param t the tile to place. 
 * @param b the board that the tile will be placed onto
 */
function validPlacement(c : Coord, t : Tile, b : Board) : boolean {
    let u = b.get(above(c));
    let d = b.get(below(c));
    let l = b.get(left(c));
    let r = b.get(right(c));
    return !b.has(c) 
        && (matchesIfExists(u, t) && matchesIfExists(u, d) && matchesIfExists(t, d))
        && (matchesIfExists(l, t) && matchesIfExists(l, r) && matchesIfExists(t, r))
        && (u != undefined || d != undefined || l != undefined || r != undefined)
}