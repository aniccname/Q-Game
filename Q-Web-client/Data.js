"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.makeTile = exports.makeCoord = exports.parseJTile = exports.parseJPlayer = exports.parseJPub = void 0;
var makeTile = function (c, s) { return ({ color: c, shape: s }); };
exports.makeTile = makeTile;
var makeCoord = function (x1, y1) { return ({ x: x1, y: y1 }); };
exports.makeCoord = makeCoord;
//#region JSON Serializers
//INVARIANT: b is not empty
function BoardToJMap(b) {
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
    for (var _i = 0, rows_1 = rows; _i < rows_1.length; _i++) {
        var _a = rows_1[_i], row = _a[0], cells = _a[1];
        _loop_1(row, cells);
    }
    return jMap;
}
function TileToJCell(ColumnIndex, t) {
    return [ColumnIndex, t];
}
//#endregion JSON Serializers
console.log(JSON.stringify({ color: "red", shape: "star" }));
//Parsing Functionality
function parseJTile(text) {
    return JSON.parse(text);
}
exports.parseJTile = parseJTile;
console.log(parseJTile('{"color":"blue","shape":"square"}'));
function parseJMap(text) {
    var b = new Map;
    var parsed = typeof text === "string" ? JSON.parse(text) : text;
    function parseJRow(parsed) {
        if (!(parsed instanceof Array)) {
            throw new Error("Given JSON value is not a JMap");
        }
        for (var _i = 0, _a = parsed.slice(1); _i < _a.length; _i++) {
            var jCell = _a[_i];
            parseJCell(parsed[0], jCell);
        }
    }
    function parseJCell(row, jCell) {
        if (!(jCell instanceof Array && typeof jCell[0] === 'number' && typeof jCell[1] === 'object')) {
            throw new Error("Given JSON value is not a JCell");
        }
        b.set(makeCoord(row, jCell[0]), parseJTile(jCell[1]));
    }
    function parseJTile(jTile) {
        if (!(jTile instanceof Object && jTile.color !== undefined && jTile.shape !== undefined)) {
            throw new Error("Given Object is not a JTile");
        }
        return jTile;
    }
    for (var _i = 0, parsed_1 = parsed; _i < parsed_1.length; _i++) {
        var jRow = parsed_1[_i];
        parseJRow(jRow);
    }
    return b;
}
function parseJPlayer(text) {
    var parsed = typeof text === "string" ? JSON.parse(text) : text;
    if (!(parsed instanceof Object && typeof parsed.score === "number" && typeof parsed["tile*"] === "object")) {
        throw new Error("Given JSON value is not a JPlayer!");
    }
    return {
        score: parsed.score,
        tiles: parsed["tile*"]
    };
}
exports.parseJPlayer = parseJPlayer;
function parseJPub(text) {
    var parsed = typeof text === "string" ? JSON.parse(text) : text;
    if (!(parsed instanceof Object && typeof parsed.map === "object"
        && typeof parsed["tile*"] === "number" && typeof parsed.players === "object")) {
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
//Tests
var exJCell = [3, { color: "green", shape: "8star" }];
var exBoard1 = new Map;
exBoard1.set(makeCoord(0, 0), makeTile("blue", "8star"));
exBoard1.set(makeCoord(0, 1), makeTile("blue", "square"));
exBoard1.set(makeCoord(1, 2), makeTile("purple", "clover"));
console.log(JSON.stringify(BoardToJMap(exBoard1)));
console.log(parseJMap(JSON.stringify(BoardToJMap(exBoard1))));
console.log(parseJPub({ "map": [[0, [0, { "color": "red", "shape": "clover" }]], [1, [0, { "color": "red", "shape": "star" }]], [2, [0, { "color": "red", "shape": "8star" }]]], "players": [{ "score": 0, "tile*": [{ "color": "blue", "shape": "clover" }, { "color": "blue", "shape": "star" }, { "color": "blue", "shape": "8star" }] }, 77], "tile*": 0 }));
