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
const TILE_WIDTH = 32;
const TILE_HEIGHT = 32;

function drawTile(value: Tile) : React.JSX.Element {
    //return <img className="aaaa" src="images/red_star.png" alt={value.color + " " +  value.shape} width={TILE_WIDTH} height={TILE_HEIGHT}/>
    return <img className="tile" src={"images/" + value.shape + "/" + value.color + ".png"} 
        alt={value.color + " " +  value.shape} width={TILE_WIDTH} height={TILE_HEIGHT}/>
}

function drawLoc(value : Loc) : React.JSX.Element {
    if (value === "Empty") {
        return <img className="empty" src="images/empty.png" alt="empty location" width={TILE_WIDTH} height={TILE_HEIGHT}/>
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
        <span className="board-location">
            <Button onClick={onClick} size="small">
                    {drawLoc(value)}
            </Button>
        </span>
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
        <Stack spacing="0" direction="column">
        {rows.map((row, r) => {
            return (
                <Stack spacing="0" direction="row" key={-r}>
                    {row.map((spot, c) => <BoardLocation value={spot.value} onClick={placer(spot.coord)} key={r * row.length + c}/>)}
                </Stack>
            )})}
        </Stack>
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
        <Container className="player-tiles">
            <Grid2 spacing={0.5}>
                {tiles.map((tile, i) => <PlayerTile value={tile} key={i} id={i} active={active} onClick={selector(tile, i)}/>)}
            </Grid2>
        </Container>
    )
}

/**
 * Creates list of player scores, names, and number of remaining tiles left 
 * @param props {playerScore: The score of the player
 *               otherScores: An array of all the other scores in the game} 
 * @returns 
 */
export function PlayerOrder({playerOrdering, activePlayerIndx}
    : {playerOrdering: OpponentInfo[], activePlayerIndx : number | false}) : React.JSX.Element {
        //Name, score, num tiles
        const scores : [string, number, number][] = 
            playerOrdering.map((info) => [info.name, info.score, info.numTiles]);
        const maxScore = Math.max(...scores.map(([_name, score, _numTiles], i) => score));
        function formatPlayer(name : string, score : number, numtiles : number, i : number) : React.JSX.Element {
            const body = <>{name + ": " + score + " pts. " + numtiles + " tiles remaining."}</>;
            const withPlayer = i == activePlayerIndx ? <i>{body}</i> : body;
            const withLead = maxScore == score ? <b>{withPlayer}</b> : withPlayer
            return <li key={i}>{withLead}</li>;
        }
        return(
        <ul>
            {scores.map(([name, score, numtiles], i) => formatPlayer(name, score, numtiles, i))}
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
    return <Button variant="contained" onClick={()=>submission("pass")} className="pass" disabled={disabled}>
        Pass
    </Button>
}

/**
 * A UI button to allow the user to replace all tiles
 */
export function Replace({submission, disabled} : {submission: Submission, disabled: boolean}) : React.JSX.Element {
    return <Button variant="contained" onClick={()=>submission("replace")} className="replace" disabled={disabled}>
        Replace
    </Button>
}

/**
 * A UI button that submits all placed tiles <i>this turn</i> as the player's TurnAnswer
 */
export function Place({submission, placements, isPlaying} : {submission: Submission, placements: Placement[], isPlaying : boolean}) : React.JSX.Element {
    return <Button variant="contained" onClick={()=>submission(placements)} className="place" disabled={placements.length == 0 || !isPlaying}> 
        Place
    </Button>
}

/**
 * A Button that applies the given reset function, resetting the state of the program to the beginning of the turn.
 */
function Undo({resetter, disabled} : {resetter: thunk, disabled: boolean}) : React.JSX.Element {
    return <Button variant="contained" onClick={resetter} className="undo" disabled={disabled}>
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
 * @param props turn information to render and interact with.
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
        {placements.forEach(([c, t]) => turnInfo.global.board.set(c, t)); resetter(); return submission(ans)}
    
    return (
      <div className="game">
      <meta name="viewport" content="initial-scale=0.25, width=device-width" />
      <Stack direction="row" spacing={4}>
        <Container maxWidth="sm">
            <Stack spacing={2}>
                <Box className="game-board">
                    <GameBoard board={board} placer={clickHandler}/>
                </Box> 
                <PlayerTiles tiles={hand} selector={tileSelector} active={activeTile}/>
                <Stack direction="row">
                    <Pass submission={submittor} disabled={anyPlacements || !isPlaying}/>
                    <Replace submission={submittor} disabled={anyPlacements || !isPlaying}/>
                    <Place submission={submittor} placements={placements} isPlaying={isPlaying}/>
                </Stack>
                <p>{placementError}</p>
            </Stack>
        </Container>
            <Undo resetter={resetter} disabled={!anyPlacements}/>
        <Container maxWidth="sm">
            <Stack direction="column">
                <TilesRemaining poolSize={turnInfo.global.poolSize}/>
                <PlayerOrder playerOrdering={turnInfo.global.playerOrdering} activePlayerIndx={turnInfo.global.activePlayerIndx}/>
                <MyTurn isMyTurn={isPlaying}/>
            </Stack>
        </Container>
      </Stack>
      </div>
    );
}

export function Play({turnInfo, submission} : {turnInfo : TurnInfo, submission : Submission}) {
    return <Game turnInfo={turnInfo} submission={submission} isPlaying={true}/>
}

export function Watch({turnInfo} : {turnInfo : TurnInfo}) {
    return <Game turnInfo={turnInfo} submission={(ans : TurnAnswer) => ans} isPlaying={false}/>
}