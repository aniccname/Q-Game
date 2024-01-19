import React, { StrictMode } from "react";
import { createRoot } from "react-dom/client";

import Game from "./FrontEnd";
import { Board, makeCoord, makeTile } from "./Data";

const root = createRoot(document.getElementById("root"));
const board1 = new Board();
board1.set(makeCoord(0, 0), makeTile("blue", "8star"));
const exampleInfo = {
  board : board1,
  poolSize : 0,
  myInfo : {
      tiles : [makeTile("green", "8star"), makeTile("green", "diamond")],
      score: 77
  },
  otherScores : [76, 89, 0]
}
root.render(
  <StrictMode>
    <Game turnInfo={exampleInfo}/>
  </StrictMode>
);