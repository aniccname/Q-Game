import React, { useState } from "react"
import Button from "@mui/material/Button"
import Grid2 from "@mui/material/Unstable_Grid2"
import {Tile, Empty, Coord, Board, makeCoord, TurnAnswer, descendingOrder, TurnInfo, Placement, PlayerInfo, OpponentInfo} from "./Data"
import { JSXElementConstructor, StrictMode } from "react"
import { Container, Stack, Box} from "@mui/material"
import { ClassNames } from "@emotion/react"
import { validPlacements} from "./BackEnd"

export type Loc = Tile | Empty
type Submission = (ans : TurnAnswer) => void

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

/**
 * Creates a list of interactive player tiles
 * @param param0  {tiles: the tiles to draw.
 *                 active: the index of the active tile
 *                 selector: Function that takes in the tile and index of the tile to select.}
 * @returns 
 */
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
 * Creates list of player scores, names, and number of remaining tiles left 
 * @param props {playerScore: The score of the player
 *               otherScores: An array of all the other scores in the game} 
 * @returns 
 */
export function PlayerOrder({playerOrdering}
    : {playerOrdering: OpponentInfo[]}) : React.JSX.Element {
        //Name, score, num tiles
        const scores : [string, number, number][] = playerOrdering.map((info) => [info.name, info.score, info.numTiles]);
        const maxScore = Math.max(...scores.map(([_name, score, _numTiles], i) => score));
        return(
        <ul>
            {scores.map(([name, score, numtiles], i) => 
                (score == maxScore ? 
                <li key={i}> <b>{name + ": " + score + " pts. " + numtiles + " tiles remaining."} </b></li>
                :  <li key={i}> {name + ": " + score + " pts. " + numtiles + " tiles remaining."} </li>))}
        </ul>
        )
}

export function TilesRemaining({poolSize} : {poolSize : Number }) : React.JSX.Element {
    return <b>{"The board has " + poolSize + " tiles remaining."}</b>
}

//TODO: Should these be disabled depending on placements? This would mean that Pass, Replace, and Place all share the same values, which makes me think that it should probably be inside a Button group if this is the case. 
/**
 * A UI button to allow for the user to pass
 */
export function Pass({submission, disabled} : {submission : Submission, disabled: boolean}) : React.JSX.Element {
    return <Button variant="outlined" onClick={()=>submission("pass")} className="pass" disabled={disabled}>
        Pass
    </Button>
}

/**
 * A UI button to allow the user to replace all tiles
 */
export function Replace({submission, disabled} : {submission: Submission, disabled: boolean}) : React.JSX.Element {
    return <Button variant="outlined" onClick={()=>submission("replace")} className="replace" disabled={disabled}>
        Replace
    </Button>
}

/**
 * A UI button that submits all placed tiles <i>this turn</i> as the player's TurnAnswer
 */
export function Place({submission, placements, isPlaying} : {submission: Submission, placements: Placement[], isPlaying : boolean}) : React.JSX.Element {
    return <Button variant="outlined" onClick={()=>submission(placements)} className="place" disabled={placements.length == 0 || !isPlaying}> 
        Place
    </Button>
}

/**
 * A Button that applies the given reset function, resetting the state of the program to the beginning of the turn.
 */
function Undo({resetter, disabled} : {resetter: thunk, disabled: boolean}) : React.JSX.Element {
    return <Button variant="outlined" onClick={resetter} className="undo" disabled={disabled}>
        Undo
    </Button>
}

function MyTurn({isMyTurn} : {isMyTurn : boolean}) : React.JSX.Element {
    if (isMyTurn) {
        return <b>It's your turn! Waiting for your submission...</b>
    } else {
        return <>It's not your turn. Please wait and watch the game.</>
    }
}

//TODO: The gameboard needs some way of figuring out of it's our turn (where we should be able to take all these actions) or not (where we're just observing the changes while it's somebody's else's turn).
//My intuition on this is to take in the index of the active player, so that Game can highlight whose turn it is, but if this is the case what happens when it's our turn? 
// Do we highlight our score and name? I think this is probably the best way to go about it, but it might not play well with the current scoring code that I have. 
// The answer is that it's actually trivially easy. turnInfo would have to be modified to send the names of everyone, but that I don't think would be too much of an issue. 
/**
 * 
 * @param {turnInfo, submission} turn information to render and interact with.
 * @returns 
 */
export default function Game({turnInfo, submission, isPlaying} : 
    {turnInfo: TurnInfo, submission : Submission, isPlaying : boolean}) : React.JSX.Element {
    let [hand, setHand] = useState<Tile[]>(turnInfo.player.tiles);
    let [activeTile, setActiveTile] = useState<number | "none">("none");
    //board is a local copy of the current turns board to be mutated after each placement.
    let [board, setBoard] = useState(new Board(turnInfo.global.board));
    let [placements, setPlacements] = useState<Placement[]>([]);
    let [placementError, setPlacementError] = useState<string>("");
    const clickHandler = (coord : Coord) => () => {
        if (activeTile !== "none") {
            let selectedTile = hand[activeTile];
            const result = validPlacements(placements.concat([[coord, selectedTile]]), turnInfo.global.board)
            if (result == true) {
                setBoard(new Board(board.set(coord, selectedTile)));
                setHand(hand.filter((_, i) => (i !== activeTile)));
                placements.push([coord, selectedTile]);
                setPlacements(placements);
                setActiveTile("none");
                setPlacementError("");
            } else {
                setPlacementError(result);
            }
        }
    };
    const tileSelector = (t : Tile, id : number) => () => {
        setActiveTile(id != activeTile ? id : "none");
        console.log(t, id);
    };
    const resetter = function() {
        setHand(turnInfo.player.tiles);
        setActiveTile("none");
        setBoard(new Board(turnInfo.global.board));
        setPlacements([]);
    };
    const anyPlacements = placements.length > 0;
    //Updates the turnInfo with the all of the placements, since the we can't do it for 
    // each placement since placement rules work in relation to the old borad.
    const submittor = (ans : TurnAnswer) => 
        {placements.forEach(([c, t]) => turnInfo.global.board.set(c, t)); return submission(ans)}
    
    return (
      <>
      <meta name="viewport" content="initial-scale=0.25, width=device-width" />
      <Stack direction="row" spacing={4}>
        <Container maxWidth="lg">
            <b> {"Board has a current size of " + board.size} </b>
            <Box className="Game">
                <GameBoard board={board} placer={clickHandler}/>
            </Box> 
            <PlayerTiles tiles={hand} selector={tileSelector} active={activeTile}/>
            <Stack direction="row">
                <Pass submission={submittor} disabled={anyPlacements || !isPlaying}/>
                <Replace submission={submittor} disabled={anyPlacements || !isPlaying}/>
                <Place submission={submittor} placements={placements} isPlaying={isPlaying}/>
            </Stack>
            <>{placementError}</>
        </Container>
        <Undo resetter={resetter} disabled={!anyPlacements || !isPlaying}/>
        <Container maxWidth="sm">
            <Stack direction="column">
                <TilesRemaining poolSize={turnInfo.global.poolSize}/>
                <PlayerOrder playerOrdering={turnInfo.global.playerOrdering}/>
                <MyTurn isMyTurn={isPlaying}/>
            </Stack>
        </Container>
      </Stack>
      </>
    );
}

export function Play({turnInfo, submission} : {turnInfo : TurnInfo, submission : Submission}) {
    return <Game turnInfo={turnInfo} submission={submission} isPlaying={true}/>
}

export function Watch({turnInfo, submission} : {turnInfo : TurnInfo, submission : Submission}) {
    return <Game turnInfo={turnInfo} submission={submission} isPlaying={false}/>
}