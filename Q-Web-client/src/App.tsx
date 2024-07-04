import React, { useRef, useState } from "react";
import StartPage from "./StartPage";
import { TurnAnswer, TurnInfo, serializeTurnAnswer } from "./Data";
import { acknowledgeMethod, parseCall, isTurn} from "./ProxyReferee";
import Game, { Play, Watch } from "./Game";
import ErrorMessage from "./ErrorPage"
import { Backdrop, Box, Button, Container, Fab, Tooltip } from "@mui/material";
import InfoIcon from '@mui/icons-material/Info';
import Paper from '@mui/material/Paper'
import CancelIcon from '@mui/icons-material/Cancel';
import Tab from '@mui/material/Tab';
import TabContext from '@mui/lab/TabContext';
import TabList from '@mui/lab/TabList';
import TabPanel from '@mui/lab/TabPanel';
export {App}

/**
 * Represents all the possible states of the game. 
 *  * "submitting" the connection info,
 *  * "connecting" to the server
 *  * "connected" to the server and waiting for the game to start
 *  * the 'TurnInfo' of the current turn
 *  * a 'boolean' representing whether the game has been won or lost.
 */
type GameStatus = "submitting"| "connecting"| "connected" | "new tiles" | TurnInfo | boolean | Error;
const INFO_BOX_WIDTH = "md";

/**
 * Renders all of the staes of the game based off the given status.
 * @param status the current status of the game.
 * @param connector the callback to connect this client to the server.
 * @param replier the callback for when this player submits a turn. 
 * @param myTurn whether it is this player's turn.
 * @param turnNum the number of turns the game has gone through. 
 * @returns 
 */
function renderStatus(status: GameStatus, 
    connector : (addr : string, n : string) => void, 
    replier : (ans : TurnAnswer) => void,
    myTurn : boolean, turnNum : number) : React.JSX.Element {
        if (status == "submitting") {
            return <StartPage connector={connector}/>
        } else if (status == "connecting") {
            //TODO: Does this need a new function for the connecting page?
            return  (
            <p className="connecting">
                Attempting to establish a connection with the server!
            </p>)
        } else if (status == "connected") {
            //TODO: Does this need a waiting screen in a new file/function or not?
            return <p className="connecting">Connected. waiting for the game to start!</p>
        } else if (typeof status == "boolean") {
            //TODO: Create a winning/loss screen. 
            if (status) {
                return <p className="winning-message">Congratulations! You won!</p>
            } else {
                return <p className="losing-message">Unfortunately, you lost...</p>
            }
        } else if (status == "new tiles") {
            return <>got new tiles!</>
        } else if (status instanceof Error) {
            //Quick and dirty way to dispaly the error. Not sure how to depack it. 
            return <ErrorMessage msg={status.message}/>
        } else {
            if (myTurn) {//TODO: Figure out how to make it so that this re-rendered each time a message is sent. Maybe split it into Play and watch?
                return <Play turnInfo={status} submission={replier} turnNum={turnNum}/>
            } else {
                return <Watch turnInfo={status} turnNum={turnNum}/>
            }
        }
}


function Info() : React.JSX.Element {
    let [open, setOpen] = useState(false);
    let [tab, setTab] = useState("0")
    return (
    <TabContext value={tab}>
        <Box className="info-button">
            <Tooltip title={open ? "Close manual" : "Open manual"}>
                <Fab aria-label="info" color="primary" onClick={() => setOpen((b) => !b)}>
                    {open ? <CancelIcon/> : <InfoIcon/>}
                </Fab>
            </Tooltip>
        </Box>
        <Backdrop open={open} onClick={() => setOpen(b => b)}>
            <Paper className="info" elevation={4}>
                <TabList onChange={(_, t) => setTab(t)} aria-label="Q Game information">
                        <Tab label="How to start" value="0"/>
                        <Tab label="Your Turn" value="1"/>
                        <Tab label="Your opponents" value="2"/>
                        <Tab label="Scoring and Ending" value="3"/>
                </TabList>
                <TabPanel value="0">
                    <HowToStart/>
                </TabPanel>
                <TabPanel value="1">
                    <YourTurn/>
                </TabPanel>
                <TabPanel value="2">
                    <Opponents/>
                </TabPanel>
                <TabPanel value="3">
                    <ScoringEnding/>
                </TabPanel>
            </Paper>
        </Backdrop>
    </TabContext>
    )
}

function HowToStart() : React.JSX.Element {
    return (
        <Box maxWidth={INFO_BOX_WIDTH}>
            <h2>Connecting</h2>
            <p>
                To start, enter the hostname and port address of the server 
                you are connecting to. You will also be prompted to enter in 
                a display name that will identify you to all the other players.
            </p>
            <p>
                Once you (and the server owner) is ready, click connect. Once 
                you are connected, and enough players have joined the lobby, 
                the game will start.
            </p>
            <h2>Troubleshooting</h2>
            <p>
                If you encounter the <i>"the socket was unnexpectedly closed by the server" </i> 
                error, please check if the server is online, and you have entered the 
                right port and address.
            </p>
        </Box>
    )
}

function YourTurn() {
    //Should I include the ability to place tiles even when it's not your turn?
    return (
        <Box maxWidth={INFO_BOX_WIDTH}>
            <h2>Playing the game</h2>
            <p>When it is your turn, you are able to do 3 different actions.</p>
            <h3>Pass</h3>
            <p>When you pass, your turn ends and you keep all of your tiles.</p>
            <h3>Replace</h3>
            <p>
                When you replace, all of your tiles are replaced by random tiles
                from the server. 
            </p>
            <h3>Place</h3>
            <p>
                This option only exists if you have placed one or more tiles. 
                To place a tile, click on the tile you want to place in your hand 
                (the section below the board), and then click on the empty board
                location you want to place the tile at. Repeat for every other 
                tile you would like to place. In a single turn, tiles can only be
                placed in the same row/column. 
            </p>
            <p>
                A Tile can only be placed where it borders at least 1 other tile, 
                and shares a common color/shape with all tiles in the row/column.
            </p>
        </Box>
    )
}

function Opponents() : React.JSX.Element {
    return (
        <Box maxWidth={INFO_BOX_WIDTH}>
            <h2>Rounds</h2>
            <p>
                To the right of the game board, is the information about the
                referee and your opponents. 
            </p>
            <p>
                At the top lives the amount of tiles the referee has left. The
                referee can only replace tiles if there are tiles remaining. 
            </p>
            <p>
                Below, is the list of the active players, in round order. A 
                round is sequence of turns from the first player in the list, 
                to the last. If a player leaves or disconnects, their turn
                will be skipped, and if they were the last player in the round,
                a new round will begin.
            </p>
            <p>
                Each player in the round list has their corresponding score and
                number of tiles remaining. If a player places all of their tiles,
                the game is over. The player who is leading the game with the 
                highest score is bolded, and the player whose turn it is, is 
                italicized.
            </p>
            {/*Should this be in how to play, instead of Opponents*/}
            <p>
                At the bottom, is your turn indicator. 
                If it is your turn, you are on the clock to give an
                answer. If not, you are free to plan your next moves
                while one of your opponents makes their move. When
                it's your turn, any placed tiles will be returned to
                your hand. 
            </p>
        </Box>
    )
}

function ScoringEnding() : React.JSX.Element {
    const SCORING_RULES : string[] = 
    ["Each tile placed is +1 point.",
     "Each tile gains +1 point for each preexisting tile in the tile's row and/or column.",
     "A player gains +6 bonus points for completing a Q, a sequence of exactly " +
     "6 tiles that contain all shapes or colors and nothing else.",
     "A player gains +6 bonus points for placing all of their tiles."]
    const ENDING_RULES : string[] = 
    ["A player has placed all of the tiles in their hand.",
     "There have been no placements in a round (all players have either passed " +
     "or replaced)."]
    return (
        <Box maxWidth={INFO_BOX_WIDTH}>
            <h2>Scoring rules</h2>
            <p>
                A placement's score is the summation of the score of all tiles,
                plus any bonus points from placing a Q or placing all tiles.
            </p>
            <ul>
                {SCORING_RULES.map((rule, i) => <li key={i}>{rule}</li>)}
            </ul>
            <h2>Ending rules</h2>
            <p> A game ends either when: </p>
            <ul>
                {ENDING_RULES.map((rule, i) => <li key={i}>{rule}</li>)}
            </ul>
        </Box>
    )
}

/**
 * The Top level component of the Program. Represents either:
 * <li> Waiting for the connection information
 * <li> Connecting to the server
 * <li> The steady state connection to the game
 * <li> An error if the server is unable to connect
 */
export default function App() : React.JSX.Element {
    //Status and name are not working. I think this is because I'm not changing them, so the closure rules are working based off of substitution at creation time, not having a refernece to them. 
    // As such, I need to use ref instead of State, or create an object to hold the state so that I can use the mutated value in the closures generated. 
    let [status, updateStatus] = useState<GameStatus>("submitting");
    let [myTurn, setMyTurn] = useState<boolean>(false);
    //Paired with ref to avoid stale closures.
    let curStatus : {current : GameStatus} = useRef("submitting");
    let name : {current : string} = useRef("");
    let socket : {current : WebSocket | null} = useRef(null);
    let [turnNum, setTurnNum] = useState(0);
    
    function setStatus(gs : GameStatus) : void {
        updateStatus(gs);
        curStatus.current = gs;
    }

    const startConnection = (addr : string, n : string) => {
        setStatus("connecting");
        name.current = n;
        socket.current = new WebSocket(addr);
        socket.current.addEventListener("open", onOpen);
        socket.current.addEventListener("message", onMessage);
        socket.current.addEventListener("error", onError)
        socket.current.addEventListener("close", onClose)
        console.log(curStatus.current, name);
    }

    function onOpen(this: WebSocket, ev: Event) : void {
        setStatus("connected");
        console.log(curStatus.current, name);
        this.send(name.current);
    }

    function onMessage(this : WebSocket, ev : MessageEvent<any>) {
        //const status = getStatus();
        if (typeof curStatus.current == "boolean" || curStatus.current instanceof Error) {
            throw Error("Invalid status state '" + curStatus.current
                     + "' when receiving message from remote server. Game is already over")
        }
        console.log(ev.data);
        setStatus(parseCall(ev.data, curStatus.current));
        setMyTurn(isTurn(ev.data));
        acknowledgeMethod(() => (this.send("void")), ev.data);
        setTurnNum(turn => turn + 1);
    }
    
    function onError(this: WebSocket, ev : Event) {
        console.log("Error ", ev, " occured");
        setStatus(Error("An unexpected error occured"));
    }

    function onClose(this: WebSocket, _ev : Event) {
        //This means that the socket closed before the game was over.
        if (typeof curStatus.current !== "boolean" && !(curStatus.current instanceof Error)) {
            setStatus(Error("the socket was unexpectedly closed by the server"));
        }
    }

    function sendReply (ans: TurnAnswer) : void {
        if (socket.current == null) {
            throw Error("unable to send reply as no connection has yet been established")
        }
        console.log("sending turn answer " + ans)
        socket.current.send(serializeTurnAnswer(ans));
        setMyTurn(false);
    }
    return (
    <Container className="page">
        {renderStatus(status, startConnection, sendReply, myTurn, turnNum)}
        <Info/>
    </Container>)

}