"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.localFunc = exports.Board = exports.descendingOrder = exports.makeTile = exports.makeCoord = exports.parseJMap = exports.parseJTile = exports.parseJPlayer = exports.parseJPub = void 0;
//TODO : Decide whether the function should enforce the enumeration of Colors and Shapes. 
const makeTile = (c, s) => ({ color: c, shape: s });
exports.makeTile = makeTile;
/**
 * Makes a new Coord with the given x and y value.
 * @param x The x position of the new coordinate.
 * @param y The y position of the new coordinate.
 * @returns A new Coord with the given x and y value.
 */
const makeCoord = (x, y) => ({ "x": x, "y": y });
exports.makeCoord = makeCoord;
/**
 * Compares the two coordinates in descending order (top to bottom, left to right).
 * @param c1 A coordinate to compare.
 * @param c2 A coordinate to compare.
 */
function descendingOrder(c1, c2) {
    const verticalDiff = c1.y - c2.y;
    if (verticalDiff === 0) {
        return c1.x - c2.x;
    }
    else
        return verticalDiff;
}
exports.descendingOrder = descendingOrder;
class Board {
    internalMap;
    untransformedEntries;
    size;
    constructor(b) {
        this.internalMap = new Map();
        this.untransformedEntries = [];
        this.size = 0;
        if (b) {
            for (let [c, v] of b.entries()) {
                this.internalMap.set(this.transform(c), v);
                this.untransformedEntries.push([c, v]);
                this.size += 1;
            }
        }
    }
    clear() {
        this.untransformedEntries = [];
        this.internalMap.clear();
        this.size = 0;
    }
    delete(key) {
        if (this.internalMap.has(this.transform(key))) {
            this.internalMap.delete(this.transform(key));
            this.untransformedEntries = this.untransformedEntries.filter(([coord, _]) => (coord !== key));
            this.size -= 1;
            return true;
        }
        return false;
    }
    forEach(callbackfn, thisArg) {
        throw new Error("Method not implemented.");
    }
    has(key) {
        return this.internalMap.has(this.transform(key));
    }
    keys() {
        return this.untransformedEntries.map(([coord, _]) => (coord)).values();
    }
    values() {
        return this.internalMap.values();
    }
    [Symbol.iterator]() {
        return this.untransformedEntries.values();
    }
    [Symbol.toStringTag] = "Board";
    transform(c) {
        return "(" + c.x + "," + c.y + ")";
    }
    get(key) {
        return this.internalMap.get(this.transform(key));
    }
    set(key, value) {
        this.untransformedEntries = this.untransformedEntries.filter(([c, _]) => (c.x != key.x || c.y != key.y));
        this.untransformedEntries.push([key, value]);
        this.internalMap.set(this.transform(key), value);
        this.size = this.internalMap.size;
        return this;
    }
    entries() {
        return this.untransformedEntries.values();
    }
}
exports.Board = Board;
function test() {
    function doesThisBreak() { }
}
exports.localFunc = test;
//#region JSON Serializers
//INVARIANT: b is not empty
function BoardToJMap(b) {
    function PlaceInJRow(coord, t) {
        let jCells = rows.get(coord.x);
        if (jCells === undefined) {
            rows.set(coord.x, [TileToJCell(coord.y, t)]);
        }
        else {
            jCells.push(TileToJCell(coord.y, t));
        }
    }
    if (b.size == 0) {
        throw "Cannot convert an empty board to JMap";
    }
    const rows = new Map;
    b.forEach((t, c, _) => PlaceInJRow(c, t));
    const jMap = [];
    for (let [row, cells] of rows) {
        let jRow = [row];
        cells.forEach((jc, _) => jRow.push(jc));
        jMap.push(jRow);
    }
    return jMap;
}
/**
 * Serializes a Board into a JMap representation of a board.
 * @param b The Board to serialize
 * @returns The JMap representation of a board as a string.
 */
function serializeBoard(b) {
    return JSON.stringify(BoardToJMap(b));
}
function TileToJCell(ColumnIndex, t) {
    return [ColumnIndex, t];
}
function TileToJTile(t) {
    return t;
}
/**
 * Serializes a Board into a JTile representation of a Tile
 * @param t The Tile to serialize.
 * @returns The JTile representation of a board as a string.
 */
function serializeTile(t) {
    return JSON.stringify(TileToJTile(t));
}
//Parsing Functionality
/**
 * Parses the given JTile into a Tile.
 * @param text The JTile to parse.
 * @returns a new Tile equivalent to the given JTile.
 */
function parseJTile(text) {
    let parsed;
    if (typeof text === "string") {
        try {
            parsed = JSON.parse(text);
        }
        catch (e) {
            throw Error("Unable to parse given JPub. " + e);
        }
    }
    else {
        parsed = text;
    }
    if (parsed.color !== undefined && parsed.shape !== undefined) {
        return parsed;
    }
    else {
        throw Error("Given JSON is not a JTile");
    }
}
exports.parseJTile = parseJTile;
/**
 * Parses the given JMap into a board.
 * @param text The JMap to parse.
 * @returns a new Board equivalent to the given JTile.
 */
function parseJMap(text) {
    const b = new Board();
    const parsed = typeof text === "string" ? JSON.parse(text) : text;
    function parseJRow(parsed) {
        if (!(parsed instanceof Array)) {
            throw new Error("Given JSON value is not a JMap");
        }
        for (let jCell of parsed.slice(1)) {
            parseJCell(parsed[0], jCell);
        }
    }
    function parseJCell(row, jCell) {
        if (!(jCell instanceof Array && typeof jCell[0] === 'number' && typeof jCell[1] === 'object')) {
            throw new Error("Given JSON value is not a JCell");
        }
        b.set(makeCoord(jCell[0], row), parseJTile(jCell[1]));
    }
    for (let jRow of parsed) {
        parseJRow(jRow);
    }
    if (b.size == 0) {
        throw Error("JMap must be non empty!");
    }
    return b;
}
exports.parseJMap = parseJMap;
/**
 * Parses the given JPlayer into a board.
 * @param text The JPlayer to parse.
 * @returns a new PlayerInfo equivalent to the given JPlayer.
 */
function parseJPlayer(text) {
    const parsed = typeof text === "string" ? JSON.parse(text) : text;
    if (!(parsed instanceof Object && typeof parsed.score === "number" && parsed["tile*"] instanceof Array)) {
        throw new Error("Given JSON value is not a JPlayer!");
    }
    return {
        score: parsed.score,
        tiles: parsed["tile*"]
    };
}
exports.parseJPlayer = parseJPlayer;
/**
 * Parses the given JPub into a TurnInfo.
 * @param text The JPub to parse.
 * @returns a new TurnInfo equivalent to the given JPub.
 */
function parseJPub(text) {
    let parsed;
    if (typeof text === "string") {
        try {
            parsed = JSON.parse(text);
        }
        catch (e) {
            throw Error("Unable to parse given JPub. " + e);
        }
    }
    else {
        parsed = text;
    }
    if (!(parsed instanceof Object && typeof parsed.map === "object"
        && typeof parsed["tile*"] === "number" && typeof parsed.players === "object" && parsed.players.length > 1)) {
        throw new Error("Given JSON value is not a JPub!");
    }
    return {
        board: parseJMap(parsed.map),
        poolSize: parsed["tile*"],
        myInfo: parseJPlayer(parsed.players[0]),
        otherScores: parsed.players.slice(1)
    };
}
exports.parseJPub = parseJPub;
//#endregion JSON
