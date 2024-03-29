import React, { useState } from "react"
import Button from "@mui/material/Button"
import Grid2 from "@mui/material/Unstable_Grid2"
import {Tile, Empty, Coord, Board, makeCoord, makeTile, descendingOrder, TurnInfo} from "./Data"
import { JSXElementConstructor, StrictMode } from "react"
import { Container, Stack} from "@mui/material"

export type Loc = Tile | Empty

function drawTile(value: Tile) : string {
    return value.color.charAt(0).toUpperCase() + " " + value.shape.charAt(0).toUpperCase();
}

function drawLoc(value : Loc) : string {
    if (value === "Empty") {
        return value;
    }
    else {
        return drawTile(value);
    }
}

type thunk = () => void

//TODO: Scale? IS button the right element?
export function BoardLocation ({ value, onClick} :
     {value : Loc, onClick : thunk}) : React.JSX.Element  {
    return (
       <Button className="board-location" onClick={onClick} size="small">
            {drawLoc(value)}
       </Button>
    );
}

//TODO: Maybe revisit name?
type CoordPlacer = (c : Coord) => thunk;

type LocCoord = {coord : Coord, value: Loc};
//Is this needed?

/**
 * The current view that the player has over the board. Each coord in the array **must** be unique. 
 */
type BoardView = LocCoord[][];

/**
 * Creates an interactive gameboard that where each locations performs the given
 *              continuation. 
 * @param props An object containing the current game Board and a function to place 
 *              a Tile at the given location. //TODO: Elaborate
 */
export function GameBoard({board, placer} : {board : Board, placer : CoordPlacer}) : React.JSX.Element {
    const rows = expandBoard(board);
    return (
        <>
        {rows.map((row, r) => {
            return (
                <Stack spacing="0" direction="row" key={-r}>
                    {row.map((spot, c) => <BoardLocation value={spot.value} onClick={placer(spot.coord)} key={r * row.length + c}/>)}
                </Stack>
            )})}
        </>
    )
}

//TODO: Maybe change the things into a better way? 
/**
 * Expands the given board into a BoardView, inserting empty spaces around the
 * edge of the board and inbetween tiles.
 * @param b The non-empty board to expand into a BoardView
 * @returns A view of the given board with 1 empty space arround every board. 
 */
export function expandBoard(b : Board) : BoardView {
    if (b.size === 0) {
        throw Error("Cannot expand an empty board!");
    }

    const coords = [...b.keys()];
    const xs = coords.map((c) => c.x);
    const ys = coords.map((c) => c.y);
    const xMax = Math.max(...xs);
    const xMin = Math.min(...xs);
    const yMax = Math.max(...ys);
    const yMin = Math.min(...ys);
    return visualizeBoard(b, xMax + 1, xMin - 1, yMax + 1, yMin - 1);
}

/**
 * Creates a rectangular view of the given board with the given viewport.
 * @param b The board to view
 * @param xMax The rightmost x position of the viewport.
 * @param xMin The leftmost x position of the viewport.
 * @param yMax The downmost y position of the viewport.
 * @param yMin The upmost y position of the viewport.
 * @returns A 2D array of Locations in sorted order top to bottom left to right.
 */
function visualizeBoard(b: Board, xMax: number, xMin: number,
    yMax : number, yMin: number) : BoardView { 
        const coords = [...b.keys()].sort(descendingOrder);
        const spots : BoardView = [];
        for (let y = yMin; y <= yMax; y += 1) {
            const row : LocCoord[] = [];
            for (let x = xMin; x <= xMax; x += 1) {
                const c = makeCoord(x, y);
                //This sucks ass, how do I do it better?
                const r = b.get(c);
                const v : Loc = r !== undefined ? r : "Empty";
                row.push({coord: c, value: v})
            }
            spots.push(row);
        }

        return spots;
}


/**
 * Creates an interactive button to select a player tile to place down.
 * @param props {value: The tile to draw.
 *              id: a unique identifier for this tile. 
 *              active: the tile that is currently selected, if any.
 *              onClick: Continuation for the program.}
 * @returns A Tile button that on clicked performs the callback function. Is outlined if this tile is the active tile.
 */
export function PlayerTile({value, id, active, onClick} : 
    {value : Tile, id : number, active: number | "none", onClick : thunk}) : React.JSX.Element {
    const variant : "outlined" | "text" = active == id ? "outlined" : "text";
    return <Button variant={variant} onClick={onClick} className="player-tile"> 
        {drawTile(value)}
    </Button>
}

export function PlayerTiles({tiles, active, selector}
        : {tiles : Tile[], active : number | "none", selector : (t : Tile, key: number) => thunk})
    : React.JSX.Element {
    return (
        <Grid2 container spacing={0.5}>
            {tiles.map((tile, i) => <PlayerTile value={tile} key={i} id={i} active={active} onClick={selector(tile, i)}/>)}
        </Grid2>
    )
}

/**
 * Creates sorted list to rank the player's score.
 * @param props {playerScore: The score of the player
 *               otherScores: An array of all the other scores in the game} 
 * @returns 
 */
export function Scores({playerScore, otherScores}
    : {playerScore : number, otherScores : number[]}) : React.JSX.Element {
        
        const scores : [string, number][] = otherScores.map((s) => ["not you", s]);
        scores.push(["you", playerScore]);
        scores.sort(([_, s1], [__, s2]) => (s2 - s1));
        //Sort (figure out how).
        return(
        <ol>
            {scores.map(([name, s], i) => (<li key={i}>{name + ": " + s}</li>))}
        </ol>
        )
}

export function TilesRemaining({poolSize} : {poolSize : Number }) : React.JSX.Element {
    return <b>{"The board has " + poolSize + " tiles remaining."}</b>
}

export default function Game({turnInfo} : {turnInfo: TurnInfo}) : React.JSX.Element {
    let [hand, setHand] = useState<Tile[]>(turnInfo.myInfo.tiles);
    let [activeTile, setActiveTile] = useState<number | "none">("none");
    let [board, setBoard] = useState(turnInfo.board);
    const clickHandler = (coord : Coord) => () => {
        ///setBoard(new Board().set(coord, makeTile("blue", "diamond")));
        if (activeTile !== "none") {
            setBoard(new Board(board.set(coord, hand[activeTile])));
            setHand(hand.filter((_, i) => (i !== activeTile)))
            setActiveTile("none");
        }
    };
    const tileSelector = (t : Tile, id : number) => () => {
        setActiveTile(id);
        console.log(t, id);
    };
    
    return (
      <>
      <meta name="viewport" content="initial-scale=0.25, width=device-width" />
      <Stack direction="row" spacing={4}>
        <Container maxWidth="lg">
            <b> {"Board has a current size of " + board.size} </b>
            <div className="Game">
            <GameBoard board={board} placer={clickHandler}/>
            </div> 
            <PlayerTiles tiles={hand} selector={tileSelector} active={activeTile}/>
        </Container>
        <Container maxWidth="sm">
            <Stack direction="column">
                <TilesRemaining poolSize={turnInfo.poolSize}/>
                <Scores playerScore={turnInfo.myInfo.score} otherScores={turnInfo.otherScores}/>
            </Stack>
        </Container>
      </Stack>
      </>
    );
}
