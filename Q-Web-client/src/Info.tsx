/** 
 * A module to represent the floating information manual that exists across
 * the client. 
 */

import React, { useRef, useState } from "react";
import { Backdrop, Box, Fab, Tooltip } from "@mui/material";
import InfoIcon from '@mui/icons-material/Info';
import Paper from '@mui/material/Paper'
import CancelIcon from '@mui/icons-material/Cancel';
import Tab from '@mui/material/Tab';
import TabContext from '@mui/lab/TabContext';
import TabList from '@mui/lab/TabList';
import TabPanel from '@mui/lab/TabPanel';

const INFO_BOX_WIDTH = "md";
const INFO_BOX_HEIGHT = "md";

/**
 * A floating action button that opens the game's manual on a backdrop. 
 * Clicking the button again will close the backdrop. 
 */
export default function Info() : React.JSX.Element {
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
            <div className="info"> 
                <Paper  elevation={4}>
                    <TabList onChange={(_, t) => setTab(t)} aria-label="Q Game information" variant="scrollable" scrollButtons="auto">
                            <Tab label="How to start" value="0"/>
                            <Tab label="Your Turn" value="1"/>
                            <Tab label="Your opponents" value="2"/>
                            <Tab label="How to end" value="3"/>
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
            </div>
        </Backdrop>
    </TabContext>
    )
}

function HowToStart() : React.JSX.Element {
    return (
        
        <Box maxWidth={INFO_BOX_WIDTH} maxHeight={INFO_BOX_HEIGHT} className="info-element">
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
        <Box maxWidth={INFO_BOX_WIDTH} maxHeight={INFO_BOX_HEIGHT} className="info-element">
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
        <Box maxWidth={INFO_BOX_WIDTH} maxHeight={INFO_BOX_HEIGHT} className="info-element">
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
        <div className="info-element">
            <h2>Winning the game</h2>
            <p>
                When the game ends, the player with the highest score wins. 
                If multiple players end up with the same score, they all 
                win! In this game, it's about not about being the best, it's
                about being as good as the best!
            </p>
            <h3>Scoring rules</h3>
            <p>
                The only way to get points is to place tiles. Passing or replacing 
                both give you 0 points. A placement's score is the summation of the 
                score of all tiles, plus any bonus points. The specific rules are
                explained below. 
            </p>
            <ul>
                {SCORING_RULES.map((rule, i) => <li key={i}>{rule}</li>)}
            </ul>
            <h3>Ending rules</h3>
            <p> A game ends either when: </p>
            <ul>
                {ENDING_RULES.map((rule, i) => <li key={i}>{rule}</li>)}
            </ul>
        </div>
    )
}
