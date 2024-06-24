export {
    match,
    validPlacement,
    validPlacements
}

import {Board, Tile, Coord, makeCoord, Placement} from "./Data"

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


function contiguousTilesFactory(dir : (c : Coord) => Coord) {
    return function(start : Coord, b : Board) : Tile[] {
        function helper(cur: Coord, acc : Tile[]) : Tile[] {
            const t = b.get(cur);
            // Is this really better than an if statement?
            if (t == undefined) {
                return acc;
            } else {
                acc.push(t);
                return helper(dir(cur), acc);
            }
        }
        return helper(dir(start), []);
    }
    
}

/**
 * Returns the list of contiguous tiles that are above the starting coordinate in a line. 
 * @param start the coordinate where the list of continuous tiles starts above.
 * @param b the board that the tiles are on.
 */
const allAbove = contiguousTilesFactory(above);
/**
 * Returns the list of contiguous tiles that are below the starting coordinate in a line. 
 * @param start the coordinate where the list of continuous tiles starts below.
 * @param b the board that the tiles are on.
 */
const allBelow = contiguousTilesFactory(below);
/**
 * Returns the list of contiguous tiles that are left of the starting coordinate in a line. 
 * @param start the coordinate where the list of continuous tiles starts leftward.
 * @param b the board that the tiles are on.
 */
const allLeft = contiguousTilesFactory(left);
/**
 * Returns the list of contiguous tiles that are right of the starting coordinate in a line. 
 * @param start the coordinate where the list of continuous tiles starts rightward.
 * @param b the board that the tiles are on.
 */
const allRight = contiguousTilesFactory(right);

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
    let u = allAbove(c, b);
    let d = allBelow(c, b);
    let l = allLeft(c, b);
    let r = allRight(c, b);
    return !b.has(c) 
        && allMatch(t, u) && allMatch(t, d) && allMatch(t, l) && allMatch(t, r)
        && (u.length > 0 || d.length > 0 || l.length > 0|| r.length > 0)
}

function inALine(placements : Placement[]) {
    if (placements.length > 0) {
        const [c0, _] = placements[0];
        return placements.every(([c, _]) => c.x == c0.x)
            || placements.every(([c, _]) => c.y == c0.y)
    } else {
        return false;
    }
}

/**
 * Determins if the given tiles matches all of the tiles. 
 * @param t0 the tile to check if it matches all of tiles. 
 * @param tiles the tiles to determine if they all match
 * @returns 
 */
function allMatch(t0: Tile, tiles : Tile[]) {
    if (tiles.length > 0) {
        return tiles.every((t) => match(t0, t));
    } else {
        return true;
    }
}


/**
 * Determines if the sequence of placements is valid.
 * @param placements the sequence of placements.
 * @param b the board the sequence will be placed onto. 
 */
function validPlacements(placements : Placement[], b : Board) : boolean {
    const changedBoard = new Board(b); 
    return placements.every(([c, t]) => 
        {const r = validPlacement(c, t, changedBoard); 
         changedBoard.set(c, t);
         return r;})
        && inALine(placements);
}