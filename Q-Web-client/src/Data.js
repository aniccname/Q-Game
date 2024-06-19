"use strict";
var __values = (this && this.__values) || function(o) {
    var s = typeof Symbol === "function" && Symbol.iterator, m = s && o[s], i = 0;
    if (m) return m.call(o);
    if (o && typeof o.length === "number") return {
        next: function () {
            if (o && i >= o.length) o = void 0;
            return { value: o && o[i++], done: !o };
        }
    };
    throw new TypeError(s ? "Object is not iterable." : "Symbol.iterator is not defined.");
};
var __read = (this && this.__read) || function (o, n) {
    var m = typeof Symbol === "function" && o[Symbol.iterator];
    if (!m) return o;
    var i = m.call(o), r, ar = [], e;
    try {
        while ((n === void 0 || n-- > 0) && !(r = i.next()).done) ar.push(r.value);
    }
    catch (error) { e = { error: error }; }
    finally {
        try {
            if (r && !r.done && (m = i["return"])) m.call(i);
        }
        finally { if (e) throw e.error; }
    }
    return ar;
};
var _a;
Object.defineProperty(exports, "__esModule", { value: true });
exports.localFunc = exports.Board = exports.descendingOrder = exports.makeTile = exports.makeCoord = exports.parseJMap = exports.parseJTile = exports.parseJPlayer = exports.parseJPub = void 0;
//TODO : Decide whether the function should enforce the enumeration of Colors and Shapes. 
var makeTile = function (c, s) { return ({ color: c, shape: s }); };
exports.makeTile = makeTile;
/**
 * Makes a new Coord with the given x and y value.
 * @param x The x position of the new coordinate.
 * @param y The y position of the new coordinate.
 * @returns A new Coord with the given x and y value.
 */
var makeCoord = function (x, y) { return ({ "x": x, "y": y }); };
exports.makeCoord = makeCoord;
/**
 * Compares the two coordinates in descending order (top to bottom, left to right).
 * @param c1 A coordinate to compare.
 * @param c2 A coordinate to compare.
 */
function descendingOrder(c1, c2) {
    var verticalDiff = c1.y - c2.y;
    if (verticalDiff === 0) {
        return c1.x - c2.x;
    }
    else
        return verticalDiff;
}
exports.descendingOrder = descendingOrder;
var Board = /** @class */ (function () {
    function Board(b) {
        var e_1, _b;
        this[_a] = "Board";
        this.internalMap = new Map();
        this.untransformedEntries = [];
        this.size = 0;
        if (b) {
            try {
                for (var _c = __values(b.entries()), _d = _c.next(); !_d.done; _d = _c.next()) {
                    var _e = __read(_d.value, 2), c = _e[0], v = _e[1];
                    this.internalMap.set(this.transform(c), v);
                    this.untransformedEntries.push([c, v]);
                    this.size += 1;
                }
            }
            catch (e_1_1) { e_1 = { error: e_1_1 }; }
            finally {
                try {
                    if (_d && !_d.done && (_b = _c.return)) _b.call(_c);
                }
                finally { if (e_1) throw e_1.error; }
            }
        }
    }
    Board.prototype.clear = function () {
        this.untransformedEntries = [];
        this.internalMap.clear();
        this.size = 0;
    };
    Board.prototype.delete = function (key) {
        if (this.internalMap.has(this.transform(key))) {
            this.internalMap.delete(this.transform(key));
            this.untransformedEntries = this.untransformedEntries.filter(function (_b) {
                var _c = __read(_b, 2), coord = _c[0], _ = _c[1];
                return (coord !== key);
            });
            this.size -= 1;
            return true;
        }
        return false;
    };
    Board.prototype.forEach = function (callbackfn, thisArg) {
        throw new Error("Method not implemented.");
    };
    Board.prototype.has = function (key) {
        return this.internalMap.has(this.transform(key));
    };
    Board.prototype.keys = function () {
        return this.untransformedEntries.map(function (_b) {
            var _c = __read(_b, 2), coord = _c[0], _ = _c[1];
            return (coord);
        }).values();
    };
    Board.prototype.values = function () {
        return this.internalMap.values();
    };
    Board.prototype[Symbol.iterator] = function () {
        return this.untransformedEntries.values();
    };
    Board.prototype.transform = function (c) {
        return "(" + c.x + "," + c.y + ")";
    };
    Board.prototype.get = function (key) {
        return this.internalMap.get(this.transform(key));
    };
    Board.prototype.set = function (key, value) {
        this.untransformedEntries = this.untransformedEntries.filter(function (_b) {
            var _c = __read(_b, 2), c = _c[0], _ = _c[1];
            return (c.x != key.x || c.y != key.y);
        });
        this.untransformedEntries.push([key, value]);
        this.internalMap.set(this.transform(key), value);
        this.size = this.internalMap.size;
        return this;
    };
    Board.prototype.entries = function () {
        return this.untransformedEntries.values();
    };
    return Board;
}());
exports.Board = Board;
_a = Symbol.toStringTag;
function test() {
    function doesThisBreak() { }
}
exports.localFunc = test;
//#region JSON Serializers
//INVARIANT: b is not empty
function BoardToJMap(b) {
    var e_2, _b;
    function PlaceInJRow(coord, t) {
        var jCells = rows.get(coord.x);
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
    var rows = new Map;
    b.forEach(function (t, c, _) { return PlaceInJRow(c, t); });
    var jMap = [];
    var _loop_1 = function (row, cells) {
        var jRow = [row];
        cells.forEach(function (jc, _) { return jRow.push(jc); });
        jMap.push(jRow);
    };
    try {
        for (var rows_1 = __values(rows), rows_1_1 = rows_1.next(); !rows_1_1.done; rows_1_1 = rows_1.next()) {
            var _c = __read(rows_1_1.value, 2), row = _c[0], cells = _c[1];
            _loop_1(row, cells);
        }
    }
    catch (e_2_1) { e_2 = { error: e_2_1 }; }
    finally {
        try {
            if (rows_1_1 && !rows_1_1.done && (_b = rows_1.return)) _b.call(rows_1);
        }
        finally { if (e_2) throw e_2.error; }
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
    var parsed;
    if (typeof text === "string") {
        try {
            parsed = JSON.parse(text);
        }
        catch (e) {
            throw Error("Unable to parse given Jtile. " + e);
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
    var e_3, _b;
    var b = new Board();
    var parsed = typeof text === "string" ? JSON.parse(text) : text;
    function parseJRow(parsed) {
        var e_4, _b;
        if (!(parsed instanceof Array)) {
            throw new Error("Given JSON value is not a JMap");
        }
        try {
            for (var _c = __values(parsed.slice(1)), _d = _c.next(); !_d.done; _d = _c.next()) {
                var jCell = _d.value;
                parseJCell(parsed[0], jCell);
            }
        }
        catch (e_4_1) { e_4 = { error: e_4_1 }; }
        finally {
            try {
                if (_d && !_d.done && (_b = _c.return)) _b.call(_c);
            }
            finally { if (e_4) throw e_4.error; }
        }
    }
    function parseJCell(row, jCell) {
        if (!(jCell instanceof Array && typeof jCell[0] === 'number' && typeof jCell[1] === 'object')) {
            throw new Error("Given JSON value is not a JCell");
        }
        b.set(makeCoord(jCell[0], row), parseJTile(jCell[1]));
    }
    try {
        for (var parsed_1 = __values(parsed), parsed_1_1 = parsed_1.next(); !parsed_1_1.done; parsed_1_1 = parsed_1.next()) {
            var jRow = parsed_1_1.value;
            parseJRow(jRow);
        }
    }
    catch (e_3_1) { e_3 = { error: e_3_1 }; }
    finally {
        try {
            if (parsed_1_1 && !parsed_1_1.done && (_b = parsed_1.return)) _b.call(parsed_1);
        }
        finally { if (e_3) throw e_3.error; }
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
    var parsed = typeof text === "string" ? JSON.parse(text) : text;
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
 * Parses the given JPlayer or JOpponent into a OpponentInfo
 * @param text The JPlayer or JOpponent to parse.
 * @Returns a new JOpponent representing the JPlayer or JOpponent
 */
function parsePlayers(text) {
    var parsed;
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
    if (!(parsed instanceof Object
        && typeof parsed["score"] === "number"
        && typeof parsed["name"] === "string" &&
        (typeof parsed["tile#"] === "number" || typeof parsed["tile*"] === "object"))) {
        throw new Error("Given JSON value is not a JPlayer or JOpponent!");
    }
    return {
        name: parsed["name"],
        score: parsed["score"],
        numTiles: parsed["tile*"] != undefined ? parsed["tile*"].length : parsed["tile#"]
    };
}
/**
 * Parses the given JPub into a TurnInfo.
 * @param text The JPub to parse.
 * @returns a new TurnInfo equivalent to the given JPub.
 */
function parseJPub(text) {
    var parsed;
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
    var player = parsed.players.find(function (p) { return p["tile*"] != undefined; });
    var order = parsed.players.map(parsePlayers);
    return {
        global: {
            board: parseJMap(parsed.map),
            poolSize: parsed["tile*"],
            playerOrdering: order
        },
        player: parseJPlayer(player)
    };
}
exports.parseJPub = parseJPub;
//#endregion JSON
