import {test, expect, jest} from "@jest/globals"
import {makeCoord, Board, makeTile, TurnInfo} from "../src/Data"
import { parseCall } from "../src/ProxyReferee"

const board1 : Board = new Board();
board1.set(makeCoord(0, 0), makeTile("red", "clover"));
board1.set(makeCoord(0, 1), makeTile("red", "star"));
board1.set(makeCoord(0, 2), makeTile("red", "8star"));
const blueClover = makeTile("blue", "clover");
const blueStar = makeTile("blue", "star");
const blue8Star = makeTile("blue", "8star");
const player1  = {score: 0, tiles : [blueClover, blueStar, blue8Star]}
const map = '{ "map": '
+'[[0, [0, { "color": "red", "shape": "clover" }]],' 
+ '[1, [0, { "color": "red", "shape": "star" }]],'
+ '[2, [0, { "color": "red", "shape": "8star" }]]],'
+'"players": [{"name": "me", "score": 0, "tile*":' 
+'[{ "color": "blue", "shape": "clover" },'
+ '{ "color": "blue", "shape": "star" },'
+ '{ "color": "blue", "shape": "8star" }]},'
+ '{"name": "not me", "tile#": 4, "score": 10}],'
+ '"tile*": 0 }'

test("setupTest", () => {
    const msg = '["setup", ['
    + map + ', [{ "color": "blue", "shape": "clover" },'
    + '{ "color": "blue", "shape": "star" },'
    + '{ "color": "blue", "shape": "8star" }]]]'
    const result : TurnInfo = {
        global: {
            board: board1,
            poolSize : 0,
            playerOrdering: [{name: "me", score: 0, numTiles: 3}, {name: "not me", score: 10, numTiles: 4}],
            activePlayerIndx: false
        },
        player : player1,
    };
    expect(parseCall(msg, "connected")).toEqual(result);
});

test("takeTurnTest", () => {
    const msg = '["take-turn", ['+ map + ']]'
    const result  = {
        global: {
            board: board1,
            poolSize : 0,
            playerOrdering: [{name: "me", score: 0, numTiles: 3}, {name: "not me", score: 10, numTiles: 4}],
            activePlayerIndx: 0,
        },
        player : player1,
    };
    expect(parseCall(msg, result)).toEqual(result)
});

test("newTilesTest", () => {
    const msg = '["new-tiles", [[{"color": "green", "shape": "star"}]]]'
    const map : TurnInfo = {
        global: {
            board: board1,
            poolSize : 0,
            playerOrdering: [{name: "me", score: 0, numTiles: 3}, {name: "not me", score: 10, numTiles: 4}],
            activePlayerIndx: false
        },
        player : player1,
    };
    const newPlayer = {score: 0, tiles : [blueClover, blueStar, blue8Star, makeTile("green", "star")]}
    const result : TurnInfo= {
        global: {
            board: board1,
            poolSize : 0,
            playerOrdering: [{name: "me", score: 0, numTiles: 3}, {name: "not me", score: 10, numTiles: 4}],
            activePlayerIndx: 0
        },
        player : newPlayer,
    }

    expect(parseCall(msg, map)).toEqual(result)
});

test("newTilesTurnInfoUndefined", () => {
    const msg = '["new-tiles", [[{"color": "green", "shape": "star"}]]]'
    expect(() => parseCall(msg, "connected")).toThrow("Illegal sequence of method calls")
})

test("watchTurnTest", () => {
    const msg = '["watch-turn", ['+ map + ', 1]]'
    const result  : TurnInfo = {
        global: {
            board: board1,
            poolSize : 0,
            playerOrdering: [{name: "me", score: 0, numTiles: 3}, {name: "not me", score: 10, numTiles: 4}],
            activePlayerIndx: 1
        },
        player : player1,
    };
    expect(parseCall(msg, result)).toEqual(result)
});

test("winTurnTrueTest", () => {
    const msg = '["win", [true]]';
    const map : TurnInfo  = {
        global: {
            board: board1,
            poolSize : 0,
            playerOrdering: [{name: "me", score: 0, numTiles: 3}, {name: "not me", score: 10, numTiles: 4}],
            activePlayerIndx: false
        },
        player : player1,
    };
    expect(parseCall(msg, map)).toBe(true);
});

test("winTurnFalseTest", () => {
    const msg = '["win", [false]]';
    const map : TurnInfo = {
        global: {
            board: board1,
            poolSize : 0,
            playerOrdering: [{name: "me", score: 0, numTiles: 3}, {name: "not me", score: 10, numTiles: 4}],
            activePlayerIndx: false
        },
        player : player1,
    };
    expect(parseCall(msg, map)).toBe(false);
})

test("winTurnNonBooleanTest", () => {
    const msg = '["win", [99]]';
    const map : TurnInfo = {
        global: {
            board: board1,
            poolSize : 0,
            playerOrdering: [{name: "me", score: 0, numTiles: 3}, {name: "not me", score: 10, numTiles: 4}],
            activePlayerIndx: false
        },
        player : player1,
    };
    expect(() => parseCall(msg, map)).toThrow("win expects a boolean as an argument");
})