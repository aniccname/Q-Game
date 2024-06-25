import { run } from "cypress"
import {TurnInfo, Tile, parseJPub, parseJTile} from "./Data"
export {parseCall, acknowledgeMethod, isTurn}

/**
 * Represnets a mapping from remote method names to accepting functions. 
 * the tiles in setup method is redundant, so is ignored.
 */
type DispatchTable<T> = {
    "setup": (jpub : any) => T,
    "take-turn": (jpub: any) => T,
    "new-tiles": (jtiles: any) => T,
    "win": (didWin : any) => T,
    "watch-turn": (jpub : any) => T
    "ERROR" : (msg: any) => T
}


function parseWin(didWin : any) : boolean {
    if (typeof didWin == "boolean") {
        return didWin;
    } else {
        throw Error("win expects a boolean as an argument, got " + didWin);
    }
}

function parseError(msg : any) : Error {
    if (typeof msg != "string") {
        throw Error("Server responded with unknown error " + msg)
    } else {
        return Error("Server responded with error: " + msg);
    }
}

/**
 * Parses the remote method call of the format [method, [args...]] and evalutes to the new state of the game. 
 * @param msg the remote method call from the server.
 * @param state? the current state of the game, or a string if no state yet exists. 
 *  * if state is "connected", the next message must be "setup" to create the initial state of the game.
 * @returns the new stae of the game, 
 * including a boolean if the game is over representing whether this player won or lost. 
 */
function parseCall(msg : any, state : TurnInfo | string) : TurnInfo | boolean | Error {
    function parseNewtiles(jtiles : any) : TurnInfo {
        const tiles : Tile[] = jtiles.map(parseJTile);
        if (typeof state == "string") {
            throw Error("Illegal sequence of method calls. The initial method call must be setup")
        }
        return {
            global: state.global,
            player: {
                score: state.player.score,
                tiles: state.player.tiles.concat(tiles)
            }
        };
    }

    const dispatch : DispatchTable<TurnInfo | boolean | Error> = {
        "new-tiles": parseNewtiles,
        "setup" : parseJPub,
        "take-turn" : parseJPub,
        "watch-turn" : parseJPub,
        "win" : parseWin,
        "ERROR" : parseError
    }

    const test_dispatch : DispatchTable<TurnInfo | boolean | Error | "connected"> = {
        "new-tiles": (arg) => "connected",
        "setup" : parseJPub,
        "take-turn" : parseJPub,
        "watch-turn" : (arg) => "connected",
        "win" : parseWin,
        "ERROR" : parseError
    }
    return runDispatch(msg, dispatch);
}

function sendNothing(ws : WebSocket) {

}

/**
 * Sends a "void" acknowledgement to the server for remote method calls that require it. 
 * (everything but "take-turn" and "error")
 * @param ws the web socket that is connected to the remote server.
 * @param msg the remote method call from the server.
 */
function acknowledgeMethod(respondVoid : () => (void), msg : any) : void {
    const dispatch : DispatchTable<void> = {
        "new-tiles": respondVoid,
        "setup" : respondVoid,
        "take-turn" : sendNothing, //It will be sent later by the submission field. 
        "watch-turn" : respondVoid,
        "win" : respondVoid,
        "ERROR" : sendNothing
    }
    return runDispatch(msg, dispatch);
}

/**
 * Determines whether it is this user's turn to place, exchange, or pass. 
 * @param msg the message to parse
 * @returns whether it is this user's turn (and the server expects a turn answer as a result).
 */
function isTurn(msg: any) : boolean {
    const turn = () => true;
    const notTurn = () => false;
    const dispatch : DispatchTable<boolean> = {
        "new-tiles": notTurn,
        "setup" : notTurn,
        "take-turn" : turn,
        "watch-turn" : notTurn,
        "win" : notTurn,
        "ERROR" : notTurn
    }
    return runDispatch(msg, dispatch);
}

/**
 * Executes the appropriate function of the given dispatch table. 
 * @param msg The method and argument to dispatch to in the form of [method, [args]]
 * @param dispatch The dispatch table to use for the given method.
 * @returns The result of dispatching the given msg with the given table. 
 */
function runDispatch<T>(msg: any, dispatch : DispatchTable<T>) : T {
    msg = JSON.parse(msg);
    const method = msg[0];
    if (typeof method == "string" && 
        (method == "new-tiles" || method == "setup" || method == "take-turn" 
        || method == "watch-turn" || method == "win" || method == "ERROR")){
        return dispatch[method](msg[1][0]);
    } else {
        throw Error("invalid method name, got "  + method)
    }
}