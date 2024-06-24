import React, { useState } from "react"
import { Button, Container, Stack, TextField } from "@mui/material"


/** TODO: How do I have different pages in a website? I assume thorugh different files. Assuming that's the case...
 * 1. Start out on a Start page with the server text box, name text box, and then a submit button
 * 2. When submit is clicked on, it should be starting a web socket that tries to connect to the server. Should that happen on the same page as website 1, OR should that be on a separate page that when loaded starts the connection (would this mean that the information would need to be carried through the url bar?)
 * 3. On that page with the websocket, Have all of the invormation come though and on each recieve GlobalState event, call root.render to render the new TurnInfo
 */

type HasTargetValue = {target : {value: string}}

export default function StartPage({connector} : {connector : (addr: string, name: string) => void}) : React.JSX.Element{
    let [hostname, setHostname] = useState("");
    let [name, setName] = useState("");
    let [port, setPort] = useState("")

    const handleHostnameChange = (event : HasTargetValue) => (setHostname(event.target.value));
    const handlePortChange = (event : HasTargetValue) => (setPort(event.target.value));
    const handleNameChange = (event : HasTargetValue) => (setName(event.target.value));
    const submitAnswer = () => (connector("ws://" + hostname + ":" + port, name));
    const submittable = port != '' && name != '' && hostname != '';

    

    return(
    <Container maxWidth="md">   
        <Stack spacing={1} direction="column">
            <Stack spacing={0.5} direction="row">
                <TextField id="server-hostname" label="Hostname" variant="outlined" onChange={handleHostnameChange}/>
                <TextField id="server-port" label="Port" variant="outlined" onChange={handlePortChange}/>
            </Stack>
            <TextField id="display-name" label="Name" variant="outlined" onChange={handleNameChange}/>
            <Button id="submit-button" variant="outlined" disabled={!submittable} onClick={submitAnswer}>Connect</Button>
        </Stack>
    </Container> )
}
//Maybe in a different file have above this component an App or some other top level component that has 4 states, 'start', 'connecting', 'playing', 'error' that deals with creating the websocket and then creating the appropriate rendering and such