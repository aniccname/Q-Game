import { test, expect, jest} from "@jest/globals"
import {Coord, Board, Tile, makeTile, parseJTile, localFunc, parseJPlayer, 
    PlayerInfo, makeCoord, TurnInfo, parseJPub, parseJMap} 
    from "../src/Data"

const board1 : Board = new Board();
board1.set(makeCoord(0, 0), makeTile("red", "clover"));
board1.set(makeCoord(0, 1), makeTile("red", "star"));
board1.set(makeCoord(0, 2), makeTile("red", "8star"));
const board2 : Board = new Board();
board2.set(makeCoord(9, 1), makeTile("red", "diamond"));
board2.set(makeCoord(10, 1), makeTile("blue", "diamond"));
const board3 : Board = new Board();
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

test("Board has", () => {
    expect(board1.has(makeCoord(0, 0))).toBe(true);
    expect(board1.has(makeCoord(-0, -0))).toBe(true);
    expect(board1.has(makeCoord(-1, -1))).toBe(false);
});

test("Board get", () => {
    expect(board1.get(makeCoord(0, 0))).toEqual(makeTile("red", "clover"));
    expect(board1.get(makeCoord(1, 0))).toBe(undefined);
});

test("Copy constructor", () => {
    const newBoard = new Board(board1);
    expect(newBoard.size).toBe(3);
    expect(newBoard.get(makeCoord(0, 0))).toEqual(makeTile("red", "clover"));
    expect(newBoard.get(makeCoord(0, 1))).toEqual(makeTile("red", "star"));
    expect(newBoard.get(makeCoord(0, 2))).toEqual(makeTile("red", "8star"));
    expect(newBoard.keys()).toEqual(board1.keys());
    expect(newBoard.values()).toEqual(board1.values());
    newBoard.set(makeCoord(0, 0), makeTile("blue", "star"));
    expect(newBoard.get(makeCoord(0, 0))).toEqual(makeTile("blue", "star"));
    expect(newBoard.size).toBe(3);
});

test("Replacing duplicate keys", () => {
    const exBoard = new Board(board1);
    expect(exBoard.get(makeCoord(0, 0))).toEqual(makeTile("red", "clover"));
    expect(exBoard.size).toBe(3);
    exBoard.set(makeCoord(0,0), makeTile("purple", "diamond"));
    expect(exBoard.get(makeCoord(0, 0))).toEqual(makeTile("purple", "diamond"));
    expect(exBoard.size).toBe(3);
});

test("Proper Board keys", () => {
    const keys = [makeCoord(0, 0), makeCoord(0, 1), makeCoord(0, 2)].values();
    expect(board1.keys()).toEqual(keys);
});

test("Proper Board values", () => {
    const values = [makeTile("red", "diamond"), makeTile("blue", "diamond")].values();
    expect(board2.values()).toEqual(values);
});

test("Proper Board entries", () => {
    const entries = [[makeCoord(0, 1), makeTile("green", "square")], [makeCoord(0, 2), makeTile("green", "clover")]].values();
    expect(board3.entries()).toEqual(entries);
});

test("Board toString name", () => {
    expect(board1[Symbol.toStringTag]).toBe("Board");
});

test("Modification w/ copy constructor", () => {
    const exboard = new Board(board1);
    expect(new Board(exboard.set(makeCoord(0, 0), makeTile("green", "diamond"))).size).toBe(3);
})

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

