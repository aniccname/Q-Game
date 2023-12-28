"use strict";
const makeTile = (c, s) => ({ color: c, shape: s });
const makeCoord = (x1, y1) => ({ x: x1, y: y1 });
//INVARIANT: b is not empty
function BoardToJMap(b) {
    if (b.size == 0) {
        throw "Cannot convert an empty board to JMap";
    }
    const rows = new Map;
    function PlaceInJRow(coord, t) {
        let jCells = rows.get(coord.x);
        if (jCells === undefined) {
            rows.set(coord.x, [TileToJCell(coord.y, t)]);
        }
        else {
            jCells.push(TileToJCell(coord.y, t));
        }
    }
    b.forEach((t, c, _) => PlaceInJRow(c, t));
    const jMap = [];
    for (let [row, cells] of rows) {
        let jRow = [row];
        cells.forEach((jc, _) => jRow.push(jc));
        jMap.push(jRow);
    }
    return jMap;
}
function TileToJCell(ColumnIndex, t) {
    return [ColumnIndex, t];
}
console.log(JSON.stringify({ color: "red", shape: "star" }));
//Parsing Functionality
function parseJTile(text) {
    return JSON.parse(text);
}
console.log(parseJTile('{"color":"blue","shape":"square"}'));
function parseJMap(text) {
    const b = new Map;
    function parseJRow(text) {
        const parsed = JSON.parse(text);
        if (!(parsed instanceof Array)) {
            throw new Error("Given JSON value is not a JMap");
        }
        for (let jCell in parsed.slice(1)) {
            parseJCell(parsed[0], jCell);
        }
    }
    function parseJCell(row, jCell) {
        if (!(jCell instanceof Array) && jCell[0] instanceof Number && jCell[1] instanceof Object) {
            throw new Error("Given JSON value is not a jCell");
        }
        b.set(makeCoord(row, jCell[0]), parseJTile(jCell[1]));
    }
    if (b.size == 0) {
        throw Error("JMap must be non empty!")
    }
    return b;
}
//Tests
const exJCell = [3, { color: "green", shape: "8star" }];
const exBoard1 = new Map;
exBoard1.set(makeCoord(0, 0), makeTile("blue", "8star"));
exBoard1.set(makeCoord(0, 1), makeTile("blue", "square"));
exBoard1.set(makeCoord(1, 2), makeTile("purple", "clover"));
console.log(BoardToJMap(exBoard1))
console.log(JSON.stringify(BoardToJMap(exBoard1)));
//console.log(parseJMap(JSON.stringify(BoardToJMap(exBoard1))));
function isTile(obj) {
    return (obj.color !== undefined && obj.shape !== undefined)
}
function isPlacement(obj) {
    //TODO: Make "?" a constant for not yet known. 
    return typeof obj === "object" && obj.tile !== undefined && 
        obj.row !== undefined && typeof obj.col === "number";
}
function isJCell(obj) {
    return (typeof obj === "object" && typeof obj[0] === "number" && isPlacement(obj[1]))
}
function receiveJMap(key, value) {
    if (typeof value === "object" && typeof value[0] === "number" && isTile(value[1])) {
        return {tile: value[1], row:"?", col:value[0]}
    }
    //JRow Case
    if (isJCell(value)) {
        for (let jCell of value.slice(1)) {
            jCell.row = value[0]
        }
        return value.slice(1)
    }
    return value;
}
console.log(JSON.parse('[[0,[0,{"color":"blue","shape":"8star"}],[1,{"color":"blue","shape":"square"}]],[1,[2,{"color":"purple","shape":"clover"}]]]',
receiveJMap));
console.log(JSON.parse([[0,[0,{"color":"blue","shape":"8star"}],[1,{"color":"blue","shape":"square"}]],[1,[2,{"color":"purple","shape":"clover"}]]], receiveJMap))

//[[0,[0,{"color":"blue","shape":"8star"}],[1,{"color":"blue","shape":"square"}]],[1,[2,{"color":"purple","shape":"clover"}]]]