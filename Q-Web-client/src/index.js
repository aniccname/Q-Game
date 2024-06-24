import React, { StrictMode } from "react";
import { createRoot } from "react-dom/client";

import Game, { PlayerOrder } from "./Game";
import { Board, makeCoord, makeTile } from "./Data";

const root = createRoot(document.getElementById("root"));
const board1 = new Board();
board1.set(makeCoord(0, 0), makeTile("blue", "8star"));
const exampleInfo = {
  global: {
    board : board1,
    poolSize : 0,
    playerOrdering: [{name: "you", numTiles: 6, score: 70}, 
    {name: "name1", numTiles: 4, score: 72}, 
    {name: "name2", numTiles: 7, score: 44}]
  },
  player : {
      tiles : [makeTile("blue", "8star"), makeTile("green", "8star")],
      score: 77
  },
}
root.render(
  <StrictMode>
    <Game turnInfo={exampleInfo} submission={console.log} isPlaying={false}/>
  </StrictMode>
);