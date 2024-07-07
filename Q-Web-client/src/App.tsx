import React, { useRef, useState } from "react";
import StartPage from "./StartPage";
import { TurnAnswer, TurnInfo, serializeTurnAnswer } from "./Data";
import { acknowledgeMethod, parseCall, isTurn} from "./ProxyReferee";
import Game, { Play, Watch } from "./Game";
import ErrorMessage from "./ErrorPage"
import Container from "@mui/material/Container";
import Info from "./Info";
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
    myTurn : boolean) : React.JSX.Element {
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
            return <p className="connecting">Connected. Waiting for the game to start!</p>
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
                return <Play turnInfo={status} submission={replier}/>
            } else {
                return <Watch turnInfo={status}/>
            }
        }
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
        {renderStatus(status, startConnection, sendReply, myTurn)}
        <Info/>
    </Container>)

}