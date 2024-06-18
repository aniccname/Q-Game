import React, { useRef, useState } from "react";
import StartPage from "./StartPage";
import { TurnInfo } from "./Data";

/**
 * The Top level component of the Program. Represents either:
 * <li> Waiting for the connection information
 * <li> Connecting to the server
 * <li> The steady state connection to the game
 * <li> An error if the server is unable to connect
 */
export default function App() : React.JSX.Element {
    //I think this should be a TurnInfo or something like that, although the issue is that how can we keep the same information 
    let [status, setStatus] = useState<"submitting"| "connecting"| "connected" | TurnInfo>("submitting");
    let [name, setName] = useState<string>("");
    let socket : {current : WebSocket | null} = useRef(null);

    function startConnection(addr : string, name : string) {
        setStatus("connecting");
        socket.current = new WebSocket(addr);
        socket.current.addEventListener("open", onOpen);
        setName(name);
    }

    function onOpen(this: WebSocket, ev: Event) {
        setStatus("connected");
        this.send(name);
    }
    
    
}