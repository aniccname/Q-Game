//import { test, expect, jest} from "@jest/globals"
import {Coord, Board, Tile, makeTile, parseJTile, localFunc, parseJPlayer, 
    PlayerInfo, makeCoord, TurnInfo, parseJPub, parseJMap} 
    from "./Data.ts"

const board1 : Board = new Map<Coord, Tile>();
board1.set(makeCoord(0, 0), makeTile("red", "clover"));
board1.set(makeCoord(0, 1), makeTile("red", "star"));
board1.set(makeCoord(0, 2), makeTile("red", "8star"));
const board2 : Board = new Map<Coord, Tile>();
board2.set(makeCoord(9, 1), makeTile("red", "diamond"));
board2.set(makeCoord(10, 1), makeTile("blue", "diamond"));
const board3 : Board = new Map<Coord, Tile>();
board3.set(makeCoord(0, 1), makeTile("green", "square"));
board3.set(makeCoord(0, 2), makeTile("green", "clover"));
const redDiamond : Tile = makeTile("red", "diamond");
const blueClover : Tile = makeTile("blue", "clover");
const blueStar : Tile = makeTile("blue", "star");
const blue8Star : Tile = makeTile("blue", "8star");
const player1 : PlayerInfo = {score: 0, tiles : [blueClover, blueStar, blue8Star]}

test("Is it an import problem?", () => {
    expect(localFunc()).toBeUndefined();
});

const redDiamondTestFunc = () => {
    const parsed : Tile = parseJTile('{"color": "red", "shape": "diamond"}');
    expect(parsed).toEqual(redDiamond);
};

test("redDiamond Tile deserialization", redDiamondTestFunc);

test("blueCircle Tile deserialization", () => {
    expect(parseJTile('{"shape": "circle", "color": "blue"}'))
    .toEqual(makeTile("blue", "circle"));
});

test("Partial JTile", () => {
    expect(() => parseJTile('{ "color": "blue"}')).toThrow("not a JTile");
});

test("Invalid JTile", () => {
    expect(() => parseJTile('{"not_a_tile": true}')).toThrow("not a JTile");
});

test("Extra JTile information", () => {
    const parsed = parseJTile('{"color": "red", "shape": "diamond", "points": 10}');
    expect(parsed.color).toBe("red");
    expect(parsed.shape).toBe("diamond");
});

test("JPlayer deserialization", () => {
    const parsed : PlayerInfo = parseJPlayer('{ "score": 0, "tile*": [{ "color": "blue", "shape": "clover"},' +
    '{ "color": "blue", "shape": "star" }, { "color": "blue", "shape": "8star" }]}');
    expect(parsed).toEqual(player1);
});

test("JPlayer no tile deserialization", () => {
    expect(parseJPlayer('{ "score": 55, "tile*": []}'))
    .toEqual({
        score: 55,
        tiles : []
    });
});

test("Malformed JPlayer", () => {
    expect(() => parseJPlayer('{ "score": 0, "tile*": {"color": "purple", "shape": "circle"}}'))
    .toThrow("Given JSON value is not a JPlayer");
});

test("Empty JMap", () => {
    expect(() => parseJMap('[]')).toThrow("JMap must be non empty!");
});

test("One Column JMap", () => {
    expect(parseJMap('[[1, [9, { "color": "red", "shape": "diamond" }],'
    +                     '[10, { "color": "blue", "shape": "diamond" }]]]'))
    .toEqual(board2);
});

test("One Row JMap", () => {
    expect(parseJMap('[[1, [0, { "color": "green", "shape": "square"}]],'
    +                 '[2, [0, { "color": "green", "shape": "clover"}]]]'))
    .toEqual(board3);
});

test("2 Player no referee tiles JPub deserialization", () => {
    const parsed : TurnInfo = parseJPub('{ "map": '
    +'[[0, [0, { "color": "red", "shape": "clover" }]],' 
    + '[1, [0, { "color": "red", "shape": "star" }]],'
    + '[2, [0, { "color": "red", "shape": "8star" }]]],'
    +'"players": [{ "score": 0, "tile*": [{ "color": "blue", "shape": "clover" },'
    + '{ "color": "blue", "shape": "star" },'
    + '{ "color": "blue", "shape": "8star" }] }, 77], "tile*": 0 }');
    const result : TurnInfo = {
        board: board1,
        poolSize : 0,
        myInfo : player1,
        otherScores : [77]
    };
    expect(parsed).toEqual(result);
});

test("8 Player 50 referee tiles JPub deserialization", () => {
    const parsed : TurnInfo = parseJPub('{ "map": '
    +'[[0, [0, { "color": "red", "shape": "clover" }]],' 
    + '[1, [0, { "color": "red", "shape": "star" }]],'
    + '[2, [0, { "color": "red", "shape": "8star" }]]],'
    +'"players": [{ "score": 77, "tile*": [] }, 76, 89, 0], "tile*": 0 }');
    const result : TurnInfo = {
        board : board1,
        poolSize : 0,
        myInfo : {
            tiles : [],
            score: 77
        },
        otherScores : [76, 89, 0]
    }
});

test("Improper JPub formatting: Improper JMap formatting", () => {
    expect(() => parseJPub('{"map":[],'
    +'"players":[{"score":0,"tile*":[{"color":"blue","shape":"clover"},'
    +'{"color":"blue","shape":"star"},{"color":"blue","shape":"8star"}]}, 77],'
    +'"tile*":0}'))
    .toThrow("JMap must be non empty!");
});

test("Improper JPub formatting: No players", () => {
    expect(() => parseJPub('{ "map": '
    +'[[0, [0, { "color": "red", "shape": "clover" }]],' 
    + '[1, [0, { "color": "red", "shape": "star" }]],'
    + '[2, [0, { "color": "red", "shape": "8star" }]]],'
    +'"players": [], "tile*": 0 }'))
    .toThrow("Given JSON value is not a JPub!");
});

